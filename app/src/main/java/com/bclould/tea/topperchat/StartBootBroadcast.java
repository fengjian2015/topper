package com.bclould.tea.topperchat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.bclould.tea.service.IMCoreService;
import com.bclould.tea.utils.UtilTool;

/**
 * Created by GIjia on 2018/6/19.
 */
@RequiresApi(api = Build.VERSION_CODES.N)
public class StartBootBroadcast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        UtilTool.Log("fengjian","開機廣播");
        if (!UtilTool.isServiceRunning(context, IMCoreService.CORE_SERVICE_NAME)) {
            Intent intent1 = new Intent(context, IMCoreService.class);
            context.startService(intent1);
        }
    }
}
