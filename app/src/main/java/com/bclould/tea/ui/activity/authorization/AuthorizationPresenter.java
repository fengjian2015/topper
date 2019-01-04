package com.bclould.tea.ui.activity.authorization;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.View;

import com.alibaba.fastjson.JSONObject;
import com.bclould.tea.Presenter.ExternalPresenter;
import com.bclould.tea.base.BaseView;
import com.bclould.tea.model.ExternalInfo;
import com.bclould.tea.model.ExternalTokenInfo;
import com.bclould.tea.model.ExternalUserInfo;
import com.bclould.tea.utils.MySharedPreferences;
import com.bclould.tea.utils.SharedPreferencesUtil;
import com.bclould.tea.utils.StringUtils;

/**
 * Created by GIjia on 2018/12/25.
 */

public class AuthorizationPresenter implements AuthorizationContacts.Presenter{
    private AuthorizationContacts.View mView;
    private Activity mContext;

    private ExternalInfo mExternalInfo;
    private ExternalUserInfo mExternalUserInfo;
    private String access_token;
    @Override
    public void bindView(BaseView view) {
        mView= (AuthorizationContacts.View) view;
        mView.initView();
    }

    @Override
    public <T extends Context> void start(T context) {
        mContext= (Activity) context;
        MySharedPreferences.getInstance().setBoolean(SharedPreferencesUtil.IS_EXTERNAL, false);
        mExternalInfo = JSONObject.parseObject(MySharedPreferences.getInstance().getString(SharedPreferencesUtil.EXTERNAL_CONTENT), ExternalInfo.class);
        initHttp();
    }

    private void initHttp() {
        new ExternalPresenter(mContext).publicsh("client_credentials", mExternalInfo.getClient_id(), mExternalInfo.getClient_secret(), mExternalInfo.getScope(), new ExternalPresenter.CallBack() {
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
                    mView.setllDataView();
                }
            }

            @Override
            public void error() {

            }
        });
    }

    public void getUserInfo(String access_token) {
        new ExternalPresenter(mContext).getExUsers(access_token, new ExternalPresenter.CallBack1() {
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


    private void result(String json) {
        Intent intent = new Intent();
        intent.setData(Uri.parse(mExternalInfo.getScope() + "://topper_chat_result/result?content=" + json));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
        mContext.finish();
    }


    @Override
    public void release() {

    }


    @Override
    public String getAccessToken() {
        return access_token;
    }
}
