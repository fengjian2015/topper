package com.bclould.tea.utils;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;

/**
 * Created by fengjian on 2018/12/28.
 */

public class ContextUtil {
    /**
     * 判断是否还存活
     * @param context
     * true 存在
     */
    public static boolean isExist(Context context){
        if(context instanceof Activity &&((Activity) context).isFinishing()){
            return false;
        }
        if(context instanceof Service && !UtilTool.isServiceRunning(context,context.getClass().getName()) ){
            return false;
        }
        if(context==null){
            return false;
        }
        return true;
    }
}
