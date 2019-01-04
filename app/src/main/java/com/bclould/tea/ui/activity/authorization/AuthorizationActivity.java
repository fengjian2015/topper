package com.bclould.tea.ui.activity.authorization;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.bclould.tea.Presenter.ExternalPresenter;
import com.bclould.tea.R;
import com.bclould.tea.base.BaseActivity;
import com.bclould.tea.model.ExternalInfo;
import com.bclould.tea.model.ExternalTokenInfo;
import com.bclould.tea.model.ExternalUserInfo;
import com.bclould.tea.utils.MySharedPreferences;
import com.bclould.tea.utils.SharedPreferencesUtil;
import com.bclould.tea.utils.StringUtils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AuthorizationActivity extends BaseActivity implements AuthorizationContacts.View{
    @Bind(R.id.tv_title_top)
    TextView mTvTitleTop;
    @Bind(R.id.iv_logo)
    ImageView mIvLogo;
    @Bind(R.id.tv_name)
    TextView mTvName;
    @Bind(R.id.tv_permissions)
    TextView mTvPermissions;
    @Bind(R.id.btn_next)
    Button mBtnNext;
    @Bind(R.id.ll_data)
    LinearLayout mLlData;


    private AuthorizationPresenter mAuthorizationPresenter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authorization);
        ButterKnife.bind(this);
        mAuthorizationPresenter=new AuthorizationPresenter();
        mAuthorizationPresenter.bindView(this);
        mAuthorizationPresenter.start(this);
    }

    @Override
    public void initView() {

    }

    @OnClick({R.id.tv_title_top, R.id.btn_next})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_title_top:
                finish();
                break;
            case R.id.btn_next:
                mAuthorizationPresenter.getUserInfo(mAuthorizationPresenter.getAccessToken());
                break;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        finish();
    }

    @Override
    public void setllDataView() {
        mLlData.setVisibility(View.VISIBLE);
    }
}
