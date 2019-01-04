package com.bclould.tea.utils;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.ComponentName;
import android.content.Context;
import java.util.ArrayList;

public class CheckClassIsWork {

	public static boolean isWorked(Context context, String serviceName) {
		ActivityManager myManager=(ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
		ArrayList<RunningServiceInfo> runningService = (ArrayList<RunningServiceInfo>) myManager.getRunningServices(30);
		for(int i = 0 ; i<runningService.size();i++){   
			if(runningService.get(i).service.getClassName().toString().
					equals(serviceName)){   
				return true;   
			}   
		}   
		return false;   
	}  
	
	public static boolean isTopActivity(Context context, String topActivity){
		ActivityManager am = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
		if(am.getRunningTasks(1)==null||am.getRunningTasks(1).size()==0){
			return false;
		}
		ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
		if (cn.getClassName().contains(topActivity)){
			UtilTool.Log("-------------","打開了登錄界面");
			return true;
		}
		return false;  
	}
}
