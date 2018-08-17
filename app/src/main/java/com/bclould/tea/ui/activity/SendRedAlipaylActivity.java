package com.bclould.tea.ui.activity;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.alipay.sdk.app.EnvUtils;
import com.bclould.tea.R;
import com.bclould.tea.alipay.AlipayClient;
import com.bclould.tea.base.BaseActivity;
import com.bclould.tea.utils.AppLanguageUtils;
import com.bclould.tea.utils.MySharedPreferences;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

@RequiresApi(api = Build.VERSION_CODES.N)
public class SendRedAlipaylActivity extends BaseActivity {

    @Bind(R.id.et_count)
    EditText mEtCount;
    @Bind(R.id.et_remark)
    EditText mEtRemark;
    @Bind(R.id.tv_allmoney)
    TextView mTvAllmoney;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EnvUtils.setEnv(EnvUtils.EnvEnum.SANDBOX);
        setContentView(R.layout.activity_send_red_alipayl);
        ButterKnife.bind(this);
        setOnClick();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(AppLanguageUtils.attachBaseContext(newBase, MySharedPreferences.getInstance().getString(newBase.getString(R.string.language_pref_key))));
    }

    private void setOnClick() {
        mEtCount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mTvAllmoney.setText(mEtCount.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }


    @OnClick({R.id.bark, R.id.btn_send})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;
            case R.id.btn_send:
//                AlipayClient.getInstance().payV2(SendRedAlipaylActivity.this);
                AlipayClient.getInstance().authV2(SendRedAlipaylActivity.this);
                break;
        }
    }
}
