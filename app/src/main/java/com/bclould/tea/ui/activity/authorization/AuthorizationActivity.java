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

@RequiresApi(api = Build.VERSION_CODES.N)
public class AuthorizationActivity extends BaseActivity {
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
    private ExternalInfo mExternalInfo;
    private ExternalUserInfo mExternalUserInfo;
    private String access_token;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authorization);
        ButterKnife.bind(this);
        init();

    }

    private void initHttp() {
        new ExternalPresenter(this).publicsh("client_credentials", mExternalInfo.getClient_id(), mExternalInfo.getClient_secret(), mExternalInfo.getScope(), new ExternalPresenter.CallBack() {
            @Override
            public void send(ExternalTokenInfo data) {
                if (!StringUtils.isEmpty(data.getError())) {
                    mExternalUserInfo.setStatus(2);
                    mExternalUserInfo.setError(data.getError());
                    mExternalUserInfo.setMessage(data.getError_description());
                    result(JSONObject.toJSONString(mExternalUserInfo));
                    return;
                }
                if (data.isAuthorized()) {
                    getUserInfo(data.getAccess_token());
                } else {
                    access_token=data.getAccess_token();
                    mLlData.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void error() {

            }
        });
    }

    private void getUserInfo(String access_token) {
        new ExternalPresenter(this).getExUsers(access_token, new ExternalPresenter.CallBack1() {
            @Override
            public void send(ExternalUserInfo data) {
                mExternalUserInfo = data;
                result(JSONObject.toJSONString(mExternalUserInfo));
            }

            @Override
            public void error() {

            }
        });


    }

    private void init() {
        MySharedPreferences.getInstance().setBoolean(SharedPreferencesUtil.IS_EXTERNAL, false);
        mExternalInfo = JSONObject.parseObject(MySharedPreferences.getInstance().getString(SharedPreferencesUtil.EXTERNAL_CONTENT), ExternalInfo.class);
        initHttp();
    }

    private void result(String json) {
        Intent intent = new Intent();
        intent.setData(Uri.parse(mExternalInfo.getScope() + "://topper_chat_result/result?content=" + json));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @OnClick({R.id.tv_title_top, R.id.btn_next})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_title_top:
                finish();
                break;
            case R.id.btn_next:
                getUserInfo(access_token);
                break;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        finish();
    }
}
