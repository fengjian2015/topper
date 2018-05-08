package com.bclould.tocotalk.crypto.otr;


import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;

import net.java.otr4j.OtrException;
import net.java.otr4j.session.SessionID;
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
        return statehash.get(key);
    }

    public void createOtrChatManager(SessionID sessionID, Context context){
        try {
            OtrChatManager otrChatManager=new OtrChatManager();
            otrChatManager.startMessage(sessionID,context);
            hashMap.put(sessionID.toString(),otrChatManager);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public SessionID sessionID(String localUserId, String remoteUserId){
        if(localUserId.isEmpty()||remoteUserId.isEmpty())return null;
        return new SessionID(localUserId,remoteUserId,protocolName);
    }

    public void addOtrChatManager(SessionID sessionID, OtrChatManager otrChatManager){
        try {
            hashMap.put(sessionID.toString(),otrChatManager);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public void startMessage(SessionID sessionID,Context context){
        try {
            hashMap.get(sessionID.toString()).startMessage(sessionID,context);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

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
        removeOtrChatManager(sessionID);
    }

    public void removeOtrChatManager(SessionID sessionID){
        try {
            hashMap.remove(sessionID.toString());
        }catch (Exception e){
            e.printStackTrace();
        }
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

    public boolean isExist(SessionID sessionID){
        return hashMap.get(sessionID.toString())!=null;
    }
}
