package com.dashiji.biyun.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.dashiji.biyun.base.MyApp;
import com.dashiji.biyun.R;
import com.dashiji.biyun.base.BaseActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by GA on 2017/9/27.
 */

public class BankCardBindingActivity3 extends BaseActivity {

    @Bind(R.id.bark)
    ImageView mBark;
    @Bind(R.id.password)
    EditText mPassword;
    @Bind(R.id.password2)
    EditText mPassword2;
    @Bind(R.id.btn_submit)
    Button mBtnSubmit;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_card_binding3);
        ButterKnife.bind(this);
        MyApp.getInstance().addActivity(this);
    }

    //点击事件的处理
    @OnClick({R.id.bark, R.id.btn_submit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;
            case R.id.btn_submit:

                String passWord = mPassword.getText().toString().trim();

                String passWord2 = mPassword2.getText().toString().trim();

                if (passWord.isEmpty() && passWord2.isEmpty()) {

                    Toast.makeText(this, "交易密码不能为空", Toast.LENGTH_SHORT).show();

                } else {

                    startActivity(new Intent(this, BankCardBindingActivity4.class));

                    finish();

                }

                break;
        }
    }
}
