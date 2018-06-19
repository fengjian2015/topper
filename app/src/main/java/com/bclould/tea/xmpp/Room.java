package com.bclould.tea.xmpp;


import android.graphics.Bitmap;
import com.bclould.tea.model.MessageInfo;
import com.bclould.tea.model.UserInfo;

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
    void createRoom(String roomJid,String roomName, List<UserInfo> users);
    void joinMultiUserChat(String user, String roomJid);
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
    void sendOTR(String message);
}
