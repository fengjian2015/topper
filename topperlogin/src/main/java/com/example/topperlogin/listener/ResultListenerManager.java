package com.example.topperlogin.listener;

import com.example.topperlogin.model.ExternalUserInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by GIjia on 2018/12/21.
 */

public class ResultListenerManager {
    private static ResultListenerManager manager;

    private List<IResultListener> listeners;// 设置状态改变的监听

    private static Object lock = new Object();

    private ResultListenerManager() {
        listeners = new ArrayList<IResultListener>();
    }

    public static ResultListenerManager get() {
        if (manager == null) {
            synchronized (ResultListenerManager.class) {
                if (manager == null) {
                    manager = new ResultListenerManager();
                }
            }
        }
        return manager;
    }

    public void registerResultListener(IResultListener listener) {
        synchronized (listeners) {
            if(!listeners.contains(listener)) {
                listeners.add(listener);
            }
        }
    }

    public void unIResultListener(IResultListener listener) {
        if(listener==null)return;
        synchronized (listeners) {
            if(listeners.contains(listener)) {
                listeners.remove(listener);
            }
        }
    }

    public void unIResultListener() {
        synchronized (listeners) {
            listeners.clear();
        }
    }

    public void notifyListener(ExternalUserInfo externalUserInfo) {
        synchronized (lock) {
            for (IResultListener lis : listeners) {
                lis.onState(externalUserInfo);
            }
        }
    }

}
