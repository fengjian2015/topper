package com.bclould.tea.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;

import com.bclould.tea.R;
import com.bclould.tea.base.BaseActivity;
import com.bclould.tea.ui.widget.PWDDialog;
import com.bclould.tea.utils.AppLanguageUtils;
import com.bclould.tea.utils.MySharedPreferences;
import com.bclould.tea.utils.UtilTool;

@RequiresApi(api = Build.VERSION_CODES.N)
public class PayOutsideCallsActivity extends BaseActivity {
    private String content;
    private PWDDialog pwdDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_outside_calls);
        OutsideCalls();
    }
    private void OutsideCalls(){
        Intent intent = getIntent();
        Uri uri = intent.getData();
        if(uri!=null) {
//            content = uri.getQueryParameter("url");
//            if (!StringUtils.isEmpty(content)) {
                showPWDialog();
//            }
        }
    }
    private void showPWDialog() {
        pwdDialog=new PWDDialog(this);
        pwdDialog.setOnPWDresult(new PWDDialog.OnPWDresult() {
            @Override
            public void success(String password) {
                PayOutsideCallsActivity.this.finish();
//                    sendRed(password);
                sendHttp();
            }
        });
        String coins="DOGE";
        String mCount="100";
        pwdDialog.showDialog(UtilTool.removeZero(mCount + ""),coins,coins + getString(R.string.pay),null,null);
    }

    private void sendHttp(){
        Intent intent=new Intent(PayOutsideCallsActivity.this,PayReceiptResultActivity.class);
        startActivity(intent);
    }


}
