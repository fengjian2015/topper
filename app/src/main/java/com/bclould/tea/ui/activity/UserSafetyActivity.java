package com.bclould.tea.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bclould.tea.R;
import com.bclould.tea.base.BaseActivity;
import com.bclould.tea.base.MyApp;
import com.bclould.tea.utils.AppLanguageUtils;
import com.bclould.tea.utils.MySharedPreferences;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.bclould.tea.Presenter.LoginPresenter.STATE;

/**
 * Created by GA on 2017/10/11.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class UserSafetyActivity extends BaseActivity {
    @Bind(R.id.bark)
    ImageView mBark;
    @Bind(R.id.rl_autonym_attestation)
    RelativeLayout mRlAutonymAttestation;
    @Bind(R.id.rl_login_password)
    RelativeLayout mRlLoginPassword;
    @Bind(R.id.rl_login_set)
    RelativeLayout mRlLoginSet;
    @Bind(R.id.rl_login_record)
    RelativeLayout mRlLoginRecord;
    @Bind(R.id.rl_pay_password)
    RelativeLayout mRlPayPassword;
    @Bind(R.id.rl_app_look)
    RelativeLayout mRlAppLook;
    @Bind(R.id.rl_google)
    RelativeLayout mRlGoogle;
    @Bind(R.id.tv_status)
    TextView mTvStatus;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_safety);
        ButterKnife.bind(this);
        MyApp.getInstance().addActivity(this);
        if (MySharedPreferences.getInstance().getSp().contains(STATE)) {
            mTvStatus.setText(getString(R.string.authenticated));
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(AppLanguageUtils.attachBaseContext(newBase, MySharedPreferences.getInstance().getString(newBase.getString(R.string.language_pref_key))));
    }

    @OnClick({R.id.rl_app_look, R.id.rl_autonym_attestation, R.id.bark, R.id.rl_login_password, R.id.rl_pay_password, R.id.rl_google, R.id.rl_login_set, R.id.rl_login_record})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;
            case R.id.rl_app_look:
                startActivity(new Intent(this, PayPwSelectorActivity.class));
                break;
            case R.id.rl_autonym_attestation:
                startActivity(new Intent(this, RealNameC1Activity.class));
                break;
            case R.id.rl_login_password:
                startActivity(new Intent(this, LoginPasswordActivity.class));
                break;
            case R.id.rl_pay_password:
                startActivity(new Intent(this, PayPasswordActivity.class));
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
