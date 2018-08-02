package com.bclould.tea.utils;

import com.bclould.tea.topperchat.WsContans;

/**
 * Created by GIjia on 2018/8/2.
 */

public class IMUtils {

    /**
     * true 表示需要重啟服務，長時間未工作導致有問題
     * @param time
     * @return
     */
    public static boolean compareServiceTime(){
        long time=MySharedPreferences.getInstance().getLong(WsContans.IMSERVEICE_TIME);
        long newtime=System.currentTimeMillis();
        if(newtime>(time+10*1000)){
            UtilTool.Log("fengjian","超過時長需要重啟服務"+newtime+"    "+time);
            return true;
        }
        UtilTool.Log("fengjian","未超過時長"+newtime+"     "+time);
        return false;
    }
}
