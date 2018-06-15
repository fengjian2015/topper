package com.bclould.tocotalk.topperchat;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.bclould.tocotalk.utils.MySharedPreferences;
import com.bclould.tocotalk.utils.UtilTool;

/**
 * Created by GIjia on 2018/6/12.
 * 一分鐘沒有得到反饋需要重連
 */
@RequiresApi(api = Build.VERSION_CODES.N)
public class PingThreadRequest extends Thread {
    private Context context;

    public PingThreadRequest(Context context) {
        this.context = context;
    }

    @Override
    public void run() {
        while (WsConnection.getInstance().isLogin()) {
            try {
                sleep(60 * 1000);
                if(MySharedPreferences.getInstance().getInteger("ping")!=1){
                    UtilTool.Log("fengjian","未收到ping反饋，需要重新登錄");
                    WsConnection.getInstance().setIsLogin(false);
                    WsOfflineConnection.getInstance().setIsLogin(false);
                    break;
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
