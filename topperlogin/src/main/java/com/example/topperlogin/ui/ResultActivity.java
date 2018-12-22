package com.example.topperlogin.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.alibaba.fastjson.JSONObject;
import com.example.topperlogin.R;
import com.example.topperlogin.listener.ResultListenerManager;
import com.example.topperlogin.model.ExternalUserInfo;
import com.example.topperlogin.topperAuthorization.ExtermalUserManage;

public class ResultActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        initData();
    }

    private void initData() {
        Intent intent = getIntent();
        Uri uri = intent.getData();
        if (uri != null) {
            String content = uri.getQueryParameter("content");
            ExtermalUserManage.setExternalUserInfo(JSONObject.parseObject(content,ExternalUserInfo.class));
            ResultListenerManager.get().notifyListener(ExtermalUserManage.getExternalUserInfo());
        }
        finish();
    }
}
