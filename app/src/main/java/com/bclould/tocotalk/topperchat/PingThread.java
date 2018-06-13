package com.bclould.tocotalk.topperchat;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.bclould.tocotalk.utils.MySharedPreferences;

/**
 * Created by GIjia on 2018/6/12.
 */
@RequiresApi(api = Build.VERSION_CODES.N)
public class PingThread extends Thread {
    private Context context;

    public PingThread(Context context) {
        this.context = context;
    }

    @Override
    public void run() {
        MySharedPreferences.getInstance().setInteger("ping",0);
        while (WsConnection.getInstance().isLogin()) {
            try {
                WsConnection.getInstance().senPing();
                WsOfflineConnection.getInstance().senPing(context);
                sleep(20 * 1000);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
