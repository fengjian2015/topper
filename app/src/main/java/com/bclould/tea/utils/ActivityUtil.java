package com.bclould.tea.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.bclould.tea.base.MyApp;
import com.bclould.tea.topperchat.WsConnection;

import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.N)
public class ActivityUtil {
	@SuppressLint("NewApi")
	public static boolean isActivityOnTop(Activity act) {
		if (act != null) {
			if (Build.VERSION.SDK_INT >= 17) {
				if (act.isDestroyed() || act.isFinishing()) {
					return false;
				}
			} else {
				if (act.isFinishing()) {
					return false;
				}
			}
		}else {
			return false;
		}
		return true;
	}

	public static boolean isGoStartActivity(Activity context){
		if(!isActivityRunning(context)||WsConnection.getInstance().getOutConnection()){
			PackageManager packageManager = context.getPackageManager();
			Intent intent = packageManager.getLaunchIntentForPackage("com.bclould.tea");
			context.startActivity(intent);
			context.finish();
			return true;
		}else {
			return false;
		}
	}


	public static boolean isActivityRunning(Activity activity){
		if(MyApp.getInstance().mActivityList.size()>0){
			return true;
		}else {
			return false;
		}
	}

	public static boolean isAppRunning(Context context){
		ActivityManager am = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(100);
		boolean isAppRunning = false;
		String MY_PKG_NAME = "com.bclould.tea";
		for (ActivityManager.RunningTaskInfo info : list) {
			if (info.topActivity.getPackageName().equals(MY_PKG_NAME) || info.baseActivity.getPackageName().equals(MY_PKG_NAME)) {
				isAppRunning = true;
				break;
			}
		}
		return isAppRunning;
	}
}
