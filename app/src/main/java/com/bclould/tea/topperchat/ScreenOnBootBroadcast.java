package com.bclould.tea.topperchat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.bclould.tea.service.IMCoreService;

/**
 * Created by GIjia on 2018/6/19.
 */
@RequiresApi(api = Build.VERSION_CODES.N)
public class ScreenOnBootBroadcast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (!WsConnection.isServiceWork(context, IMCoreService.CORE_SERVICE_NAME)) {
            Intent intent1 = new Intent(context, IMCoreService.class);
            context.startService(intent1);
        }
    }
}
