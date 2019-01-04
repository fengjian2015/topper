package com.bclould.tea.topperchat;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.bclould.tea.utils.MySharedPreferences;
import com.bclould.tea.utils.UtilTool;

/**
 * Created by GIjia on 2018/6/12.
 */
public class PingThread extends Thread {
    private Context context;
    private int pingNumber;

    public PingThread(Context context, int pingNumber) {
        this.context = context;
        this.pingNumber=pingNumber;
    }

    @Override
    public void run() {

        while (WsConnection.getInstance().isLogin()&&pingNumber==SocketListener.pingNumber) {
            try {
                WsConnection.getInstance().changeMsgStateOvertime();
                UtilTool.Log("fengjian","發送ping");
                WsConnection.getInstance().senPing();
                WsOfflineConnection.getInstance().senPing(context);
                sleep(20 * 1000);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
