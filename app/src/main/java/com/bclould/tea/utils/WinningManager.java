package com.bclould.tea.utils;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import org.greenrobot.eventbus.EventBus;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by GIjia on 2018/12/13.
 */

public class WinningManager {
    public static List<String> sStringList=new ArrayList<>();
    public static final int SHUT_DOWN=1;
    public static final int SHOW=2;
    private boolean isShow=true;
    private long time=3000;
    private static WinningManager mInstance;

    Handler mHandler=new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case SHOW:
                    String content= (String) msg.obj;
                    reMoveList();
                    show(content);
                    mHandler.sendEmptyMessageDelayed(SHUT_DOWN,time);
                    break;
                case SHUT_DOWN:
                    down();
                    break;
            }
        }
    };

    public static WinningManager getInstance() {
        if (mInstance == null) {
            synchronized (WinningManager.class) {
                if (mInstance == null) {
                    mInstance = new WinningManager();
                }
            }
        }
        return mInstance;
    }

    public void addList(String content){
        synchronized (sStringList){
            sStringList.add(content);
            if(isShow&&sStringList.size()>0){
                isShow=false;
                Message message=new Message();
                message.what=SHOW;
                message.obj=sStringList.get(0);
                mHandler.sendMessage(message);
            }
        }
    }

    private void reMoveList(){
        synchronized (sStringList){
            sStringList.remove(0);
        }
    }

    private void show(String content){
        MessageEvent messageEvent = new MessageEvent(EventBusUtil.winning_show);
        messageEvent.setContent(content);
        EventBus.getDefault().post(messageEvent);
    }

    private void down(){
        MessageEvent messageEvent = new MessageEvent(EventBusUtil.winning_shut_down);
        EventBus.getDefault().post(messageEvent);
        showAgain();
    }

    private void showAgain(){
        if(sStringList!=null&&sStringList.size()>0){
            Message message=new Message();
            message.what=SHOW;
            message.obj=sStringList.get(0);
            mHandler.sendMessage(message);
        }else{
            isShow=true;
        }
    }
}
