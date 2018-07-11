package com.bclould.tea.xmpp;

import com.bclould.tea.model.MessageInfo;

/**
 * Created by GIjia on 2018/5/23.
 */

public interface MessageManageListener {
    //發送成功刷新數據
    void refreshAddData(MessageInfo messageInfo);

    void sendError(int id);

    void sendFileResults(String newFile2,boolean isSuccess);
    void sendFile(String msgId,boolean isSuccess);
}
