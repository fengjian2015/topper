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
 * Created by GA on 2017/10/9.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class ShenFenVerifyActivity extends BaseActivity {


    @Bind(R.id.bark)
    ImageView mBark;
    @Bind(R.id.rl_autonym_attestation)
    RelativeLayout mRlAutonymAttestation;
    @Bind(R.id.rl_phone_autonym)
    RelativeLayout mRlPhoneAutonym;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shenfen_verify);
        ButterKnife.bind(this);
        MyApp.getInstance().addActivity(this);
    }

    //跳转事件的处理
    private void setSkip(Class clazz) {
        startActivity(new Intent(this, clazz));
    }

    @OnClick({R.id.bark, R.id.rl_autonym_attestation, R.id.rl_phone_autonym})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;
            case R.id.rl_autonym_attestation:
                setSkip(RealNameC1Activity.class);
                break;
            case R.id.rl_phone_autonym:
                setSkip(UpIdCardActivity.class);
                break;
        }
    }
}
