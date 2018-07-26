package com.bclould.tea.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bclould.tea.R;
import com.bclould.tea.base.BaseActivity;
import com.bclould.tea.base.MyApp;
import com.bclould.tea.ui.widget.InputCodeLayout;
import com.bclould.tea.utils.AppLanguageUtils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by GA on 2017/10/12.
 */

public class VerifyCodeActivity extends BaseActivity {

    @Bind(R.id.bark)
    ImageView mBark;
    @Bind(R.id.phone_number)
    TextView mPhoneNumber;
    @Bind(R.id.anew_send)
    TextView mAnewSend;
    @Bind(R.id.btn_next)
    Button mBtnNext;
    @Bind(R.id.inputCodeLayout)
    InputCodeLayout mInputCodeLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_code);
        ButterKnife.bind(this);
        MyApp.getInstance().addActivity(this);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(AppLanguageUtils.attachBaseContext(newBase, newBase.getString(R.string.language_pref_key)));
    }

    @OnClick({R.id.bark, R.id.anew_send, R.id.btn_next})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:

                finish();

                break;
            case R.id.anew_send:
                break;
            case R.id.btn_next:
                Intent intent = getIntent();
                setResult(Activity.RESULT_OK, intent);
                finish();
                break;
        }
    }
}
