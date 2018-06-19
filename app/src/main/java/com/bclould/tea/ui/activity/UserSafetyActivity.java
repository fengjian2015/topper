package com.bclould.tea.ui.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bclould.tea.R;
import com.bclould.tea.base.BaseActivity;
import com.bclould.tea.base.MyApp;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by GA on 2017/10/11.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class UserSafetyActivity extends BaseActivity {


    @Bind(R.id.bark)
    ImageView mBark;
    @Bind(R.id.iv_login_password)
    ImageView mIvLoginPassword;
    @Bind(R.id.rl_login_password)
    RelativeLayout mRlLoginPassword;
    @Bind(R.id.iv_pay_password)
    ImageView mIvPayPassword;
    @Bind(R.id.rl_pay_password)
    RelativeLayout mRlPayPassword;
    @Bind(R.id.iv_google)
    ImageView mIvGoogle;
    @Bind(R.id.rl_google)
    RelativeLayout mRlGoogle;
    @Bind(R.id.iv_login_set)
    ImageView mIvLoginSet;
    @Bind(R.id.rl_login_set)
    RelativeLayout mRlLoginSet;
    @Bind(R.id.iv_login_record)
    ImageView mIvLoginRecord;
    @Bind(R.id.rl_login_record)
    RelativeLayout mRlLoginRecord;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_safety);
        ButterKnife.bind(this);
        MyApp.getInstance().addActivity(this);
    }

    @OnClick({R.id.bark, R.id.rl_login_password, R.id.rl_pay_password, R.id.rl_google, R.id.rl_login_set, R.id.rl_login_record})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;
            case R.id.rl_login_password:
                startActivity(new Intent(this, LoginPasswordActivity.class));
                break;
            case R.id.rl_pay_password:
                startActivity(new Intent(this, PayPwSelectorActivity.class));
                break;
            case R.id.rl_google:
                startActivity(new Intent(this, GoogleVerificationActivity.class));
                break;
            case R.id.rl_login_set:
                startActivity(new Intent(this, LoginSetActivity.class));
                break;
            case R.id.rl_login_record:
                startActivity(new Intent(this, LoginRecordActivity.class));
                break;
        }
    }
}
