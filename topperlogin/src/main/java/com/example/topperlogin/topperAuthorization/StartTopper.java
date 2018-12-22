package com.example.topperlogin.topperAuthorization;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import com.alibaba.fastjson.JSONObject;
import com.example.topperlogin.model.ExternalInfo;

import static com.example.topperlogin.topperAuthorization.Constants.LOGIN_JUMP;
import static com.example.topperlogin.topperAuthorization.Constants.TOPPERCHAT;


/**
 * Created by GIjia on 2018/12/21.
 */

public class StartTopper {
    public static void start(Activity context,String client_id,String client_secret,String scope){
        ExternalInfo externalInfo=new ExternalInfo();
        externalInfo.setClient_id(client_id);
        externalInfo.setClient_secret(client_secret);
        externalInfo.setScope(scope);
        if (Util.isApkInstalled(context, TOPPERCHAT)) {
            //URL拉起方法
            Intent intent = new Intent();
            intent.setData(Uri.parse(LOGIN_JUMP+ JSONObject.toJSONString(externalInfo)));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } else {
           //todo跳转网页登录
        }
    }
}
