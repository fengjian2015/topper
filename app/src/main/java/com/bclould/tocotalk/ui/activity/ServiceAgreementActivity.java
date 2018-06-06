package com.bclould.tocotalk.ui.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;
import android.widget.TextView;

import com.bclould.tocotalk.Presenter.RegisterPresenter;
import com.bclould.tocotalk.R;
import com.bclould.tocotalk.utils.Constants;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by GA on 2018/4/13.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class ServiceAgreementActivity extends AppCompatActivity {


    @Bind(R.id.tv_title)
    TextView mTvTitle;
    @Bind(R.id.xx)
    TextView mXx;
    @Bind(R.id.web_view)
    WebView mWebView;
    @Bind(R.id.xx2)
    TextView mXx2;
    @Bind(R.id.tv_consent)
    TextView mTvConsent;
    private String mUser;
    private String mEmail;
    private String mVcode;
    private String mPassword;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_agreement);
        ButterKnife.bind(this);
        initWebView();
        initIntent();
    }

    private void initWebView() {
        mWebView.loadUrl(Constants.BASE_URL + "user_agreement");
    }

    private void initIntent() {
        Intent intent = getIntent();
        mUser = intent.getStringExtra("username");
        mEmail = intent.getStringExtra("email");
        mVcode = intent.getStringExtra("vcode");
        mPassword = intent.getStringExtra("password");
    }


    @OnClick(R.id.tv_consent)
    public void onViewClicked() {
        RegisterPresenter registerPresenter = new RegisterPresenter(this);
        registerPresenter.register(mUser, mEmail, mPassword, mVcode);
    }
}
