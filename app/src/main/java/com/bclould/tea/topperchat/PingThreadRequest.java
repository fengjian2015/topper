package com.bclould.tea.topperchat;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.bclould.tea.utils.MySharedPreferences;
import com.bclould.tea.utils.UtilTool;

/**
 * Created by GIjia on 2018/6/12.
 * 一分鐘沒有得到反饋需要重連
 */
public class PingThreadRequest extends Thread {
    private Context context;
    private int pingNumber;

    public PingThreadRequest(Context context, int pingNumber) {
        this.context = context;
        this.pingNumber=pingNumber;
    }

    @Override
    public void run() {
        while (WsConnection.getInstance().isLogin()&&pingNumber==SocketListener.pingNumber) {
            try {
                sleep(2 * 60 * 1000);
                if(MySharedPreferences.getInstance().getInteger("ping")!=1){
                    UtilTool.Log("fengjian","未收到ping反饋，需要重新登錄");
                    WsConnection.getInstance().closeConnection();
                    WsOfflineConnection.getInstance().setIsLogin(false);
                    break;
                }else{
                    MySharedPreferences.getInstance().setInteger("ping",0);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
