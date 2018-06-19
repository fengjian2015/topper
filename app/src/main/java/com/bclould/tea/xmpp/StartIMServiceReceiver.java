package com.bclould.tea.xmpp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.bclould.tea.service.IMCoreService;
import com.bclould.tea.service.IMService;
import com.bclould.tea.topperchat.WsConnection;
import com.bclould.tea.utils.UtilTool;

/**
 * Created by lenovo on 2016/7/18.
 */
public class StartIMServiceReceiver extends BroadcastReceiver {
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public synchronized void onReceive(Context context, Intent intent) {
        if (IMCoreService.ACTION_START_IMSERVICE.equals(intent.getAction())) {
            UtilTool.Log("fengjian","---收到打开IMService的广播");
            if (WsConnection.isServiceWork(context,
                    "com.bclould.tea.service.IMService")) {
                return;
            }
            Intent startIntent = new Intent(context, IMService.class);
            context.startService(startIntent);
        }
    }
}
