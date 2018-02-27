package com.bclould.tocotalk.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bclould.tocotalk.base.MyApp;
import com.bclould.tocotalk.R;
import com.bclould.tocotalk.base.BaseActivity;
import com.bclould.tocotalk.utils.MySharedPreferences;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by GA on 2017/9/27.
 */

public class BankCardBindingActivity2 extends BaseActivity {

    @Bind(R.id.bark)
    ImageView mBark;
    @Bind(R.id.phone)
    EditText mPhone;
    @Bind(R.id.yz_code)
    EditText mYzCode;
    @Bind(R.id.send_message)
    Button mSendMessage;
    @Bind(R.id.btn_next)
    Button mBtnNext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_card_binding2);
        ButterKnife.bind(this);
        mPhone.setHint(MySharedPreferences.getInstance().getString(EmailBindingActivity.PHONE));
        MyApp.getInstance().addActivity(this);
    }



    //点击事件的处理
    @OnClick({R.id.bark, R.id.send_message, R.id.btn_next})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:

                finish();

                break;
            case R.id.send_message:
                break;
            case R.id.btn_next:

                String phone = mPhone.getText().toString().trim();

                String yzCode = mYzCode.getText().toString().trim();

                if (phone.isEmpty() && yzCode.isEmpty()) {

                    Toast.makeText(this, "手机号码或者验证码不能为空", Toast.LENGTH_SHORT).show();

                } else {

                    startActivity(new Intent(this, BankCardBindingActivity3.class));

                    finish();

                }

                break;
        }
    }
}
