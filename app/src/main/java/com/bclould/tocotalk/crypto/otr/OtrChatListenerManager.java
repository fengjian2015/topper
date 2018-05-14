package com.bclould.tocotalk.crypto.otr;


import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.Toast;

import com.bclould.tocotalk.R;
import com.bclould.tocotalk.utils.Constants;
import com.bclould.tocotalk.utils.UtilTool;

import net.java.otr4j.OtrException;
import net.java.otr4j.session.SessionID;

import org.jxmpp.jid.impl.JidCreate;

import java.util.HashMap;

/**
 * Created by GIjia on 2018/5/7.
 */

public class OtrChatListenerManager {
    private final String protocolName="xmpp";
    public static OtrChatListenerManager mInstance;
    private HashMap<String,OtrChatManager> hashMap=new HashMap<>();
    private HashMap<String,String> statehash=new HashMap();
    public static OtrChatListenerManager getInstance() {
        if(mInstance==null){
            mInstance=new OtrChatListenerManager();
        }
        return mInstance;
    }

    public void addOTRState(String key,String value){
        statehash.put(key,value);
    }

    public String getOTRState(String key){
        try {
            String isopen= statehash.get(key);
            if(isopen==null)return "false";
            return isopen;
        }catch (Exception e){
        }
        return "false";
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void changeState(String mUser,Context context){
        try {
            if (getIsAboutOpen(sessionID(UtilTool.getJid(),mUser))){
                Toast.makeText(context,context.getString(R.string.is_open),Toast.LENGTH_SHORT).show();
                return;
            }
            if("true".equals(getOTRState(mUser))){
                endMessage(sessionID(UtilTool.getJid(),mUser));
            }else{
                createOtrChatManager(sessionID(UtilTool.getJid(),mUser),context);
                startMessage(sessionID(UtilTool.getJid(),mUser),context);
                startSession(sessionID(UtilTool.getJid(),mUser));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public boolean getIsAboutOpen(SessionID sessionID){
        try {
            return hashMap.get(sessionID.toString()).getIsAboutOpen();
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public void createOtrChatManager(SessionID sessionID, Context context){
        try {
            if(!isExist(sessionID)) {
                OtrChatManager otrChatManager = new OtrChatManager();
                otrChatManager.startMessage(sessionID, context);
                hashMap.put(sessionID.toString(), otrChatManager);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public SessionID sessionID(String localUserId, String remoteUserId){
        if(localUserId.isEmpty())localUserId="null";
        if(remoteUserId.isEmpty())remoteUserId="null";
        SessionID sessionID=new SessionID(localUserId,remoteUserId,protocolName);
        return sessionID;
    }

    public void addOtrChatManager(SessionID sessionID, OtrChatManager otrChatManager){
        try {
            hashMap.put(sessionID.toString(),otrChatManager);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public boolean isVerified(SessionID sessionID,Context context){
        try {
            createOtrChatManager(sessionID,context);
            return hashMap.get(sessionID.toString()).isVerified(sessionID);
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public String getRemotePublicKey(SessionID sessionID,Context context){
        try {
            createOtrChatManager(sessionID,context);
            return hashMap.get(sessionID.toString()).getRemotePublicKey(sessionID);
        }catch (Exception e){
            return "";
        }
    }

    public String getLocalPublicKey(SessionID sessionID,Context context){
        try {
            createOtrChatManager(sessionID,context);
            return hashMap.get(sessionID.toString()).getLocalPublicKey(sessionID);
        }catch (Exception e){
            return "";
        }
    }


    public void startMessage(SessionID sessionID,Context context){
        try {
            hashMap.get(sessionID.toString()).startMessage(sessionID,context);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void startSession(SessionID sessionID){
        if(!isExist(sessionID))return;
        hashMap.get(sessionID.toString()).startSession(sessionID);
    }

    public void establish(SessionID sessionID,String lastInjectedMessage){
        try {
            hashMap.get(sessionID.toString()).establish(sessionID,lastInjectedMessage);
        } catch (OtrException e) {
            e.printStackTrace();
        }
    }

    public String receivedMessagesChange(String msg,SessionID sessionID) {
        if(!isExist(sessionID))return msg;
        return hashMap.get(sessionID.toString()).receivedMessagesChange(msg,sessionID);
    }

    public String sentMessagesChange(String msg,SessionID sessionID){
        if(!isExist(sessionID))return msg;
        return hashMap.get(sessionID.toString()).sentMessagesChange(msg,sessionID);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void endMessage(SessionID sessionID){
        if(!isExist(sessionID))return;
        hashMap.get(sessionID.toString()).endMessage(sessionID,true);
    }


    public boolean isOtrEstablishMessage(String chatMsg, SessionID sessionID,Context context){
        try {
            if(!chatMsg.contains("?OTR"))return false;
            if(!isExist(sessionID)){
                createOtrChatManager(sessionID,context);
            }
           return hashMap.get(sessionID.toString()).isOtrEstablishMessage(chatMsg,sessionID.getRemoteUserId());
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public boolean isOtrMessage(String chatMsg, SessionID sessionID,Context context){
        try {
            if(!chatMsg.contains("?OTR"))return false;
            if(!isExist(sessionID)){
                createOtrChatManager(sessionID,context);
            }
            return hashMap.get(sessionID.toString()).isOtrMessage(chatMsg,sessionID.getRemoteUserId());
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }


    public boolean isExist(SessionID sessionID){
        try {
            return hashMap.get(sessionID.toString())!=null;
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }
}
