package com.bclould.tocotalk.crypto.otr;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.bclould.tocotalk.R;
import com.bclould.tocotalk.utils.Constants;
import com.bclould.tocotalk.utils.MessageEvent;
import com.bclould.tocotalk.utils.MySharedPreferences;
import com.bclould.tocotalk.xmpp.XmppConnection;

import net.java.otr4j.OtrEngineImpl;
import net.java.otr4j.OtrEngineListener;
import net.java.otr4j.OtrException;
import net.java.otr4j.OtrKeyManagerDefaultImpl;
import net.java.otr4j.OtrPolicy;
import net.java.otr4j.OtrPolicyImpl;
import net.java.otr4j.session.OtrSm;
import net.java.otr4j.session.Session;
import net.java.otr4j.session.SessionID;
import net.java.otr4j.session.SessionStatus;

import org.greenrobot.eventbus.EventBus;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jxmpp.jid.impl.JidCreate;

import java.io.IOException;
import java.security.KeyPair;
import java.security.PublicKey;
import java.util.Hashtable;

/**
 * Created by GIjia on 2018/5/4.
 */

public class OtrChatManager implements OtrEngineListener, OtrSm.OtrSmEngineHost  {
    private final String protocolName="xmpp";
    private final String OPEN_OTR="?OTR";
    private OtrEngineHostImpl otrEngineHost;
    private OtrPolicyImpl otrPolicy;
    private OtrEngineImpl localEngineImpl;
    private Context context;
    private OtrKeyManagerDefaultImpl otrKeyManagerDefault;

    public SessionID sessionID(String localUserId, String remoteUserId){
        if(localUserId.isEmpty()||remoteUserId.isEmpty())return null;
        return new SessionID(localUserId,remoteUserId,protocolName);
    }

    public void startMessage(SessionID sessionID,Context context){
        this.context=context;
//        otrKeyManagerDefault=new OtrKeyManagerDefaultImpl(new OtrAndroidKeyManagerImpl(sessionID));
        otrKeyManagerDefault=new OtrAndroidKeyManagerImpl().OtrAndroidKeyManagerImpl(sessionID,context);
        otrPolicy=new OtrPolicyImpl(OtrPolicy.OTRL_POLICY_ALWAYS);
        try {
            otrEngineHost=new OtrEngineHostImpl(otrPolicy,otrKeyManagerDefault);
        } catch (IOException e) {
            e.printStackTrace();
        }
        localEngineImpl = new OtrEngineImpl(otrEngineHost);
        localEngineImpl.addOtrEngineListener(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public String startSession(SessionID sessionID){
        try {
            localEngineImpl.endSession(sessionID);
            localEngineImpl.startSession(sessionID);
            ChatManager manager = ChatManager.getInstanceFor(XmppConnection.getInstance().getConnection());
            Chat chat = manager.createChat(JidCreate.entityBareFrom(sessionID.getRemoteUserId()), null);
            chat.sendMessage(Constants.OTR_REQUEST);
            return otrEngineHost.lastInjectedMessage;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 兩人請求加密流程
     * OtrDebugLogger.log(usBob.transformReceiving(bobSessionID, otrEngineHost.lastInjectedMessage)+"\n"
     +usAlice.transformReceiving(aliceSessionID, otrEngineHost1.lastInjectedMessage)+"\n"
     + usBob.transformReceiving(bobSessionID, otrEngineHost.lastInjectedMessage)+"\n"
     + usAlice.transformReceiving(aliceSessionID, otrEngineHost1.lastInjectedMessage)+"\n"
     + usBob.transformReceiving(bobSessionID, otrEngineHost.lastInjectedMessage)
     */
    public String establish(SessionID sessionID,String lastInjectedMessage) throws OtrException {
            localEngineImpl.transformReceiving(sessionID, lastInjectedMessage);
            return otrEngineHost.lastInjectedMessage;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public String receivedMessagesChange(String msg, SessionID sessionID){
        try {
            SessionStatus sStatus = localEngineImpl.getSessionStatus(sessionID(Constants.MYUSER,sessionID.getRemoteUserId()));
            if(sStatus==SessionStatus.ENCRYPTED) {
                otrKeyManagerDefault.verify(sessionID);

                String receivedMessage = localEngineImpl.transformReceiving(sessionID, msg);
                return receivedMessage;
            }else if(msg.contains(OPEN_OTR)){
                return context.getString(R.string.otr_error);
            }
            return msg;
        } catch (Exception e) {
            e.printStackTrace();
            return msg;
        }
    }

    public String sentMessagesChange(String msg, SessionID sessionID){
        try {
            String sentMessage = localEngineImpl.transformSending(sessionID, msg);
            if(("".equals(sentMessage)||sentMessage==null)){
                return msg;
            }
            return sentMessage;
        } catch (Exception e) {
            e.printStackTrace();
            return msg;
        }
    }

    public boolean isVerified(SessionID sessionID){
        return otrKeyManagerDefault.isVerified(sessionID);
    }

    public String getRemotePublicKey(SessionID sessionID){
        return otrKeyManagerDefault.getRemoteFingerprint(sessionID);
    }

    public String getLocalPublicKey(SessionID sessionID){
        return otrKeyManagerDefault.getLocalFingerprint(sessionID);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void endMessage(SessionID sessionID,boolean isSend){
        try {
            OtrChatListenerManager.getInstance().addOTRState(sessionID.getRemoteUserId(),"false");
            localEngineImpl.endSession(sessionID);
            if(isSend) {
                ChatManager manager = ChatManager.getInstanceFor(XmppConnection.getInstance().getConnection());
                Chat chat = manager.createChat(JidCreate.entityBareFrom(sessionID.getRemoteUserId()), null);
                chat.sendMessage(establish(sessionID(Constants.MYUSER,sessionID.getRemoteUserId()),OPEN_OTR));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 判断是否是请求加密
     * @param chatMsg
     * @param from
     * @return
     */
    @SuppressLint("NewApi")
    public boolean isOtrEstablishMessage(String chatMsg, String from){
        try {
            if(chatMsg.contains(OPEN_OTR)){
                SessionStatus sessionStatus =localEngineImpl.getSessionStatus(sessionID(Constants.MYUSER,from));
                if(sessionStatus==SessionStatus.ENCRYPTED&&Constants.OTR_REQUEST.equals(chatMsg)){
                    endMessage(sessionID(Constants.MYUSER,from),false);
                }else if(sessionStatus== SessionStatus.ENCRYPTED){
                    return false;
                }
                chatMsg =establish(sessionID(Constants.MYUSER,from),chatMsg);
                ChatManager manager = ChatManager.getInstanceFor(XmppConnection.getInstance().getConnection());
                Chat chat = manager.createChat(JidCreate.entityBareFrom(from), null);
                chat.sendMessage(chatMsg);
                return true;
            }
            return false;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    private Hashtable<String, OtrSm> mOtrSms=new Hashtable<>();
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void sessionStatusChanged(SessionID sessionID) {
        SessionStatus sStatus = localEngineImpl.getSessionStatus(sessionID);
        final Session session = localEngineImpl.getSession(sessionID);
        OtrSm otrSm = mOtrSms.get(sessionID.toString());

        if (sStatus == SessionStatus.ENCRYPTED) {
            OtrChatListenerManager.getInstance().addOTRState(sessionID.getRemoteUserId(),"true");
            EventBus.getDefault().post(new MessageEvent(context.getString(R.string.otr_isopen)));

            PublicKey remoteKey = localEngineImpl.getRemotePublicKey(sessionID);
            otrEngineHost.storeRemoteKey(sessionID, remoteKey);

            if (otrSm == null) {
                // SMP handler - make sure we only add this once per session!
                otrSm = new OtrSm(session, otrEngineHost.getKeyManager(),
                        sessionID, this);
                session.addTlvHandler(otrSm);
                mOtrSms.put(sessionID.toString(), otrSm);
            }
        } else if (sStatus == SessionStatus.PLAINTEXT) {
            OtrChatListenerManager.getInstance().addOTRState(sessionID.getRemoteUserId(),"false");
            EventBus.getDefault().post(new MessageEvent(context.getString(R.string.otr_isopen)));
            if (otrSm != null) {
                session.removeTlvHandler(otrSm);
                mOtrSms.remove(sessionID.toString());
            }
            otrEngineHost.removeSessionResource(sessionID);
        } else if (sStatus == SessionStatus.FINISHED) {
            OtrChatListenerManager.getInstance().addOTRState(sessionID.getRemoteUserId(),"false");
            EventBus.getDefault().post(new MessageEvent(context.getString(R.string.otr_isopen)));
            // Do nothing.  The user must take affirmative action to
            // restart or end the session, so that they don't send
            // plaintext by mistake.
        }
    }

    @Override
    public void injectMessage(SessionID sessionID, String msg) {

    }

    @Override
    public void showWarning(SessionID sessionID, String warning) {

    }

    @Override
    public void showError(SessionID sessionID, String error) {

    }

    @Override
    public OtrPolicy getSessionPolicy(SessionID sessionID) {
        return null;
    }

    @Override
    public KeyPair getKeyPair(SessionID sessionID) {
        return null;
    }

    @Override
    public void askForSecret(SessionID sessionID, String question) {

    }
}
