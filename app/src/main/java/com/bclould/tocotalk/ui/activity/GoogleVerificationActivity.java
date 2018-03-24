package com.bclould.tocotalk.ui.activity;

import android.content.ClipboardManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bclould.tocotalk.Presenter.GoogleVerificationPresenter;
import com.bclould.tocotalk.R;
import com.bclould.tocotalk.base.BaseActivity;
import com.bclould.tocotalk.base.MyApp;
import com.bclould.tocotalk.model.GoogleInfo;
import com.bclould.tocotalk.utils.AnimatorTool;
import com.bclould.tocotalk.utils.UtilTool;
import com.bumptech.glide.Glide;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by GA on 2017/11/29.
 */
@RequiresApi(api = Build.VERSION_CODES.N)
public class GoogleVerificationActivity extends BaseActivity {
    @Bind(R.id.bark)
    ImageView mBark;
    @Bind(R.id.tv)
    TextView mTv;
    @Bind(R.id.tv_download)
    TextView mTvDownload;
    @Bind(R.id.iv_qr_code)
    ImageView mIvQrCode;
    @Bind(R.id.secret_key)
    TextView mSecretKey;
    @Bind(R.id.btn_copy)
    Button mBtnCopy;
    @Bind(R.id.et_code)
    EditText mEtCode;
    @Bind(R.id.btn_finish)
    Button mBtnFinish;
    @Bind(R.id.rl_title)
    RelativeLayout mRlTitle;
    @Bind(R.id.xx)
    TextView mXx;
    @Bind(R.id.ll_binding)
    LinearLayout mLlBinding;
    @Bind(R.id.btn_unbinding)
    Button mBtnUnbinding;
    @Bind(R.id.ll_unbinding)
    LinearLayout mLlUnbinding;
    private GoogleVerificationPresenter mGoogleVerificationPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_verification);
        ButterKnife.bind(this);
        MyApp.getInstance().addActivity(this);
        initInterface();
    }

    private void initInterface() {
        getGoogleKey();
    }

    private void getGoogleKey() {
        mGoogleVerificationPresenter = new GoogleVerificationPresenter(this);
        mGoogleVerificationPresenter.getGoogleKey();
    }

    @OnClick({R.id.bark, R.id.tv_download, R.id.btn_copy, R.id.btn_finish, R.id.btn_unbinding})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;
            case R.id.tv_download:
                break;
            case R.id.btn_unbinding:
                unBinding();
                break;
            case R.id.btn_copy:
                copySecretKey();
                break;
            case R.id.btn_finish:
                if (checkEdit())
                    bindGoogle();
                break;
        }
    }

    private void unBinding() {
        mGoogleVerificationPresenter.unBinding();
    }

    //绑定谷歌验证器
    private void bindGoogle() {
        String code = mEtCode.getText().toString().trim();
        mGoogleVerificationPresenter.bindGoogle(code);
    }

    //验证手机号和密码
    private boolean checkEdit() {
        if (mEtCode.getText().toString().trim().equals("")) {
            Toast.makeText(this, getResources().getString(R.string.get_code), Toast.LENGTH_SHORT).show();
            AnimatorTool.getInstance().editTextAnimator(mEtCode);
        } else {
            return true;
        }
        return false;
    }

    private void copySecretKey() {
        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        cm.setText(mSecretKey.getText());
        Toast.makeText(this, "复制成功", Toast.LENGTH_LONG).show();
    }

    public void setData(GoogleInfo googleInfo) {
        UtilTool.Log("谷歌", googleInfo.getIs_google_verify() + "");
        if (googleInfo.getIs_google_verify() == 1) {
            mLlBinding.setVisibility(View.GONE);
            mLlUnbinding.setVisibility(View.VISIBLE);
        } else {
            mLlBinding.setVisibility(View.VISIBLE);
            mLlUnbinding.setVisibility(View.GONE);
            mSecretKey.setText(googleInfo.getKey());
            Glide.with(this).load(googleInfo.getImg()).into(mIvQrCode);
        }
    }
}
