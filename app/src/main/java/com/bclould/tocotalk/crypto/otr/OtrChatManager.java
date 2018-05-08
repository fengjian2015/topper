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
import java.security.PublicKey;

/**
 * Created by GIjia on 2018/5/4.
 */

public class OtrChatManager implements OtrEngineListener {
    private final String protocolName="xmpp";
    private final String OPEN_OTR="?OTR";
    private OtrEngineHostImpl otrEngineHost;
    private OtrPolicyImpl otrPolicy;
    private OtrEngineImpl localEngineImpl;
    private Context context;

    public SessionID sessionID(String localUserId, String remoteUserId){
        if(localUserId.isEmpty()||remoteUserId.isEmpty())return null;
        return new SessionID(localUserId,remoteUserId,protocolName);
    }

    public void startMessage(SessionID sessionID,Context context){
        this.context=context;
        OtrKeyManagerDefaultImpl otrKeyManagerDefault=new OtrKeyManagerDefaultImpl(new OtrAndroidKeyManagerImpl(sessionID));
        otrPolicy=new OtrPolicyImpl(OtrPolicy.OTRL_POLICY_ALWAYS);
        try {
            otrEngineHost=new OtrEngineHostImpl(otrPolicy,otrKeyManagerDefault);
        } catch (IOException e) {
            e.printStackTrace();
        }
        localEngineImpl = new OtrEngineImpl(otrEngineHost);
        localEngineImpl.addOtrEngineListener(this);
    }

    public String startSession(SessionID sessionID){
        try {
            localEngineImpl.endSession(sessionID);
            localEngineImpl.startSession(sessionID);
            return otrEngineHost.lastInjectedMessage;
        } catch (OtrException e) {
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
                String receivedMessage = localEngineImpl.transformReceiving(sessionID, msg);
                return receivedMessage;
            }
            return context.getString(R.string.otr_error);
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

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void sessionStatusChanged(SessionID sessionID) {
        SessionStatus sStatus = localEngineImpl.getSessionStatus(sessionID);
        final Session session = localEngineImpl.getSession(sessionID);
        if (sStatus == SessionStatus.ENCRYPTED) {
            OtrChatListenerManager.getInstance().addOTRState(sessionID.getRemoteUserId(),"true");
            EventBus.getDefault().post(new MessageEvent(context.getString(R.string.otr_isopen)));
////                otrKeyManagerDefault1.loadRemotePublicKey(bobSessionID);
//                PublicKey remoteKey = usBob.getRemotePublicKey(sessionID);
//                otrEngineHost1.storeRemoteKey(sessionID, remoteKey);
//
//                if (otrSm == null) {
//                    // SMP handler - make sure we only add this once per session!
//                    otrSm = new OtrSm(session, otrEngineHost1.getKeyManager(),
//                            sessionID, MainActivity.this);
//                    session.addTlvHandler(otrSm);
//
//                    mOtrSms.put(sessionID.toString(), otrSm);
//                }
        } else if (sStatus == SessionStatus.PLAINTEXT) {
            OtrChatListenerManager.getInstance().addOTRState(sessionID.getRemoteUserId(),"false");
            EventBus.getDefault().post(new MessageEvent(context.getString(R.string.otr_isopen)));
//                if (otrSm != null) {
//                    session.removeTlvHandler(otrSm);
//                    mOtrSms.remove(sessionID.toString());
//                }
//
//                otrEngineHost.removeSessionResource(sessionID);


        } else if (sStatus == SessionStatus.FINISHED) {
            OtrChatListenerManager.getInstance().addOTRState(sessionID.getRemoteUserId(),"false");
            EventBus.getDefault().post(new MessageEvent(context.getString(R.string.otr_isopen)));
            // Do nothing.  The user must take affirmative action to
            // restart or end the session, so that they don't send
            // plaintext by mistake.
        }
    }
}
