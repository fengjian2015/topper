package com.bclould.tocotalk.xmpp;


import android.graphics.Bitmap;
import com.bclould.tocotalk.model.MessageInfo;
import com.bclould.tocotalk.model.UserInfo;

import org.jivesoftware.smackx.muc.MultiUserChat;

import java.util.List;

public interface Room {
    void addMessageManageListener(MessageManageListener messageManageListener);
    void removerMessageManageListener(MessageManageListener messageManageListener);
    void sendVoice(int duration, String fileName);
    MessageInfo sendMessage(String message);
    void Upload(final String path);
    void sendTransfer(String mRemark,String mCoin,String mCount);
    void sendRed(String mRemark,String mCoin,double mCount,int id);
    void sendLocationMessage(String file,Bitmap bitmap, String title, String address, float lat, float lng);
    boolean anewSendText(String message, int id);
    boolean anewSendVoice(MessageInfo messageInfo);
    MultiUserChat createRoom(String roomJid,String roomName, String nickName, List<UserInfo> users);
    MultiUserChat joinMultiUserChat(String user, String roomJid);
    void changeName(String name);
    boolean sendCaed(MessageInfo messageInfo);
    boolean sendShareLink(MessageInfo messageInfo);
    boolean sendShareGuess(MessageInfo messageInfo);
    void anewSendUpload(MessageInfo messageInfo);
    void anewSendLocation(MessageInfo messageInfo);
    void anewSendCard(MessageInfo messageInfo);
    void anewSendShareLink(MessageInfo messageInfo);
    void anewSendShareGuess(MessageInfo messageInfo);
    void transmitVideo(MessageInfo messageInfo);
}
