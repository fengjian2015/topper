package com.bclould.tocotalk.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bclould.tocotalk.base.MyApp;
import com.bclould.tocotalk.R;
import com.bclould.tocotalk.base.BaseActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by GA on 2017/10/11.
 */

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
    @Bind(R.id.iv_binding_email)
    ImageView mIvBindingEmail;
    @Bind(R.id.rl_binding_email)
    RelativeLayout mRlBindingEmail;
    @Bind(R.id.iv_google)
    ImageView mIvGoogle;
    @Bind(R.id.rl_google)
    RelativeLayout mRlGoogle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_safety);
        ButterKnife.bind(this);
        MyApp.getInstance().addActivity(this);
    }

    @OnClick({R.id.bark, R.id.rl_login_password, R.id.rl_pay_password, R.id.rl_binding_email, R.id.rl_google})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:

                finish();

                break;
            case R.id.rl_login_password:

                startActivity(new Intent(this, LoginPasswordActivity.class));

                break;
            case R.id.rl_pay_password:

                startActivity(new Intent(this, PayPasswordActivity.class));

                break;
            case R.id.rl_binding_email:

                startActivity(new Intent(this, EmailBindingActivity.class));

                break;
            case R.id.rl_google:

                startActivity(new Intent(this, GoogleVerificationActivity.class));

                break;
        }
    }
}
