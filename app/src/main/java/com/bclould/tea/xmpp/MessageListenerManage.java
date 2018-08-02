package com.bclould.tea.xmpp;

import com.bclould.tea.model.MessageInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by GIjia on 2018/8/2.
 */

public class MessageListenerManage {
    private static MessageListenerManage manager;

    private List<MessageManageListener> listeners;// 设置状态改变的监听

    private MessageListenerManage() {
        listeners = new ArrayList<MessageManageListener>();
    }

    public static MessageListenerManage get() {
        if (manager == null) {
            synchronized (MessageListenerManage.class) {
                if (manager == null) {
                    manager = new MessageListenerManage();
                }
            }
        }
        return manager;
    }

    public void addMessageManageListener(MessageManageListener messageManageListener) {
        listeners.add(messageManageListener);
    }

    public void removerMessageManageListener(MessageManageListener messageManageListener) {
        if (listeners.contains(messageManageListener)) {
            listeners.remove(messageManageListener);
        }
    }

    public void refreshAddData(MessageInfo messageInfo) {
        if (listeners != null) {
            for (MessageManageListener messageManageListener : listeners) {
                messageManageListener.refreshAddData(messageInfo);
            }
        }
    }

    public void sendFileResults(String newFile2, boolean isSuccess) {
        if (listeners != null) {
            for (MessageManageListener messageManageListener : listeners) {
                messageManageListener.sendFileResults(newFile2, isSuccess);
            }
        }
    }

    public void sendFile(String msgId, boolean isSuccess) {
        if (listeners != null) {
            for (MessageManageListener messageManageListener : listeners) {
                messageManageListener.sendFile(msgId, isSuccess);
            }
        }
    }

    public void sendError(int id) {
        if (listeners != null) {
            for (MessageManageListener messageManageListener : listeners) {
                messageManageListener.sendError(id);
            }
        }
    }
}
