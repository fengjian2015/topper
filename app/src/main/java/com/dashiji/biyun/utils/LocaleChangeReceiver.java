package com.dashiji.biyun.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.dashiji.biyun.base.MyApp;

/**
 * Created by GA on 2017/11/10.
 */

public class LocaleChangeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals(Intent.ACTION_LOCALE_CHANGED)) {
            UtilTool.Log("日志", "监听到系统语言切换");
            MyApp.getInstance().RestartApp();
        }
    }
}
