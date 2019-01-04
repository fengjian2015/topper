package com.bclould.tea.ui.activity.my;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;
import com.bclould.tea.R;
import com.bclould.tea.base.BaseActivity;
import com.bclould.tea.ui.activity.GoogleVerificationActivity;
import com.bclould.tea.ui.activity.LoginPasswordActivity;
import com.bclould.tea.ui.activity.LoginRecordActivity;
import com.bclould.tea.ui.activity.LoginSetActivity;
import com.bclould.tea.ui.activity.PayPasswordActivity;
import com.bclould.tea.ui.activity.PayPwSelectorActivity;
import com.bclould.tea.ui.activity.RealNameC1Activity;
import com.bclould.tea.utils.MySharedPreferences;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.bclould.tea.Presenter.LoginPresenter.STATE;

/**
 * Created by GA on 2017/10/11.
 */

public class UserSafetyActivity extends BaseActivity {
    @Bind(R.id.tv_status)
    TextView mTvStatus;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_safety);
        ButterKnife.bind(this);
        setTitle(getString(R.string.user_safety));
        if (!MySharedPreferences.getInstance().getString(STATE).isEmpty()) {
            mTvStatus.setText(getString(R.string.authenticated));
        }
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
