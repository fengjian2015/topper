package com.bclould.tocotalk.xmpp;


import com.bclould.tocotalk.model.MessageInfo;

public interface Room {
    //發送成功刷新數據
    void refreshAddData(MessageInfo messageInfo);

    void sendError(int id);

    void sendFileResults(String newFile2,boolean isSuccess);
}
