package com.bclould.tea.topperchat;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bclould.tea.utils.UtilTool;
import com.tencent.android.tpush.XGPushBaseReceiver;
import com.tencent.android.tpush.XGPushClickedResult;
import com.tencent.android.tpush.XGPushRegisterResult;
import com.tencent.android.tpush.XGPushShowedResult;
import com.tencent.android.tpush.XGPushTextMessage;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by GIjia on 2018/7/17.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class XGReceiver extends XGPushBaseReceiver {
    @Override
    public void onRegisterResult(Context context, int i, XGPushRegisterResult xgPushRegisterResult) {

    }

    @Override
    public void onUnregisterResult(Context context, int i) {

    }

    @Override
    public void onSetTagResult(Context context, int i, String s) {

    }

    @Override
    public void onDeleteTagResult(Context context, int i, String s) {

    }

    @Override
    public void onTextMessage(Context context, XGPushTextMessage xgPushTextMessage) {
        UtilTool.Log("fengjian","onTextMessage:"+xgPushTextMessage.getContent()+"   "+xgPushTextMessage.getCustomContent()+"    "+xgPushTextMessage.getTitle());
        try {
            Map<String,String> jsonMap= JSON.parseObject(xgPushTextMessage.getCustomContent(),Map.class);
            SocketListener.getInstance(context).umengFriendRequest(jsonMap);
        }catch (Exception e){
            UtilTool.Log("fengjian","解析錯誤");
            e.printStackTrace();
        }
    }

    @Override
    public void onNotifactionClickedResult(Context context, XGPushClickedResult xgPushClickedResult) {
        UtilTool.Log("fengjian","onNotifactionClickedResult:"+xgPushClickedResult.getCustomContent());
    }

    @Override
    public void onNotifactionShowedResult(Context context, XGPushShowedResult xgPushShowedResult) {
        UtilTool.Log("fengjian","onNotifactionShowedResult:"+xgPushShowedResult.getCustomContent());
    }
}
