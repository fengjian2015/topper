package com.dashiji.biyun.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.dashiji.biyun.base.MyApp;
import com.dashiji.biyun.R;
import com.dashiji.biyun.base.BaseActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by GA on 2017/10/9.
 */

public class ShenFenVerifyActivity extends BaseActivity {

    @Bind(R.id.bark)
    ImageView mBark;
    @Bind(R.id.rl_autonym_attestation)
    RelativeLayout mRlAutonymAttestation;
    @Bind(R.id.rl_phone_autonym)
    RelativeLayout mRlPhoneAutonym;
    @Bind(R.id.rl_bank_binding)
    RelativeLayout mRlBankBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shenfen_verify);
        ButterKnife.bind(this);
        MyApp.getInstance().addActivity(this);
    }

    //点击事件的处理
    @OnClick({R.id.bark, R.id.rl_autonym_attestation, R.id.rl_phone_autonym, R.id.rl_bank_binding})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:

                finish();

                break;
            case R.id.rl_autonym_attestation:

                setSkip(RealNameActivity.class);

                break;
            case R.id.rl_phone_autonym:

                setSkip(EmailBindingActivity.class);

                break;
            case R.id.rl_bank_binding:

                setSkip(BankCardBindingActivity.class);

                break;
        }
    }

    //跳转事件的处理
    private void setSkip(Class clazz) {

        startActivity(new Intent(this, clazz));

    }

}
