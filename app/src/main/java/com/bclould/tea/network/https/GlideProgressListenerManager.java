package com.bclould.tea.network.https;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by GIjia on 2018/7/29.
 */

public class GlideProgressListenerManager {
    private static GlideProgressListenerManager manager;

    private List<GlideProgressListener> listeners;// 设置状态改变的监听

    private GlideProgressListenerManager() {
        listeners = new ArrayList<>();
    }

    public static GlideProgressListenerManager get() {
        if (manager == null) {
            synchronized (GlideProgressListenerManager.class) {
                if (manager == null) {
                    manager = new GlideProgressListenerManager();
                }
            }
        }
        return manager;
    }

    public void registerListener(GlideProgressListener listener) {
        synchronized (listeners) {
            if(!listeners.contains(listener)) {
                listeners.add(listener);
            }
        }
    }

    public void unregisterListener(
            GlideProgressListener listener) {
        if(listener==null)return;
        synchronized (listeners) {
            if(listeners.contains(listener)) {
                listeners.remove(listener);
            }
        }
    }

    public void progress(int progress,String url){
        if(listeners!=null&&listeners.size()>0){
            for(GlideProgressListener listener:listeners){
                listener.progress(progress,url);
            }
        }
    }

    public void start(String url){
        if(listeners!=null&&listeners.size()>0){
            for(GlideProgressListener listener:listeners){
                listener.start(url);
            }
        }
    }

    public void end(String url){
        if(listeners!=null&&listeners.size()>0){
            for(GlideProgressListener listener:listeners){
                listener.end(url);
            }
        }
    }
}
