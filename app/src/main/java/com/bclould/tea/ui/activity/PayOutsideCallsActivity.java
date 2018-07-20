package com.bclould.tea.ui.activity;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bclould.tea.R;
import com.bclould.tea.base.BaseActivity;
import com.bclould.tea.ui.widget.PWDDialog;
import com.bclould.tea.ui.widget.VirtualKeyboardView;
import com.bclould.tea.utils.StringUtils;
import com.bclould.tea.utils.UtilTool;
import com.maning.pswedittextlibrary.MNPasswordEditText;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Map;

import static com.bclould.tea.R.style.BottomDialog;

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
