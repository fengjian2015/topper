package com.bclould.tea.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.webkit.WebView;
import android.widget.TextView;

import com.bclould.tea.Presenter.RegisterPresenter;
import com.bclould.tea.R;
import com.bclould.tea.base.LoginBaseActivity;
import com.bclould.tea.base.MyApp;
import com.bclould.tea.utils.AppLanguageUtils;
import com.bclould.tea.utils.Constants;
import com.bclould.tea.utils.MySharedPreferences;
import com.bclould.tea.utils.UtilTool;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by GA on 2018/4/13.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class ServiceAgreementActivity extends LoginBaseActivity {


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
    private String referrer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_agreement);
        ButterKnife.bind(this);
        MyApp.getInstance().addActivity(this);
        initWebView();
        initIntent();
    }

    private void initWebView() {
        mWebView.loadUrl(Constants.BASE_URL + "api/user_agreement" + "/" + UtilTool.getLanguage(this));
    }

    private void initIntent() {
        Intent intent = getIntent();
        referrer=intent.getStringExtra("referrer");
        mUser = intent.getStringExtra("username");
        mEmail = intent.getStringExtra("email");
        mVcode = intent.getStringExtra("vcode");
        mPassword = intent.getStringExtra("password");
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(AppLanguageUtils.attachBaseContext(newBase, MySharedPreferences.getInstance().getString(newBase.getString(R.string.language_pref_key))));
    }

    @OnClick(R.id.tv_consent)
    public void onViewClicked() {
        RegisterPresenter registerPresenter = new RegisterPresenter(this);
        registerPresenter.register(mUser, mEmail, mPassword, mVcode,referrer);
    }
}
