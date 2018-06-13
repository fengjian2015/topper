package com.bclould.tocotalk.network;

import android.os.Build;
import android.support.annotation.RequiresApi;

import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSFederationCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSFederationToken;
import com.bclould.tocotalk.Presenter.DillDataPresenter;
import com.bclould.tocotalk.base.MyApp;
import com.bclould.tocotalk.model.OSSInfo;


/**
 * Created by GA on 2018/6/13.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class OSSupload {

    private static OSSupload instance;
    private DillDataPresenter mDillDataPresenter;
    private String mEndpoint;

    //单例
    public static OSSupload getInstance() {
        if (instance == null) {
            instance = new OSSupload();
        }
        return instance;
    }

    public OSSClient visitOSS() {
        if (mDillDataPresenter == null) {
            mDillDataPresenter = new DillDataPresenter(MyApp.getInstance().app());
        }
        OSSCredentialProvider credentialProvider = new OSSFederationCredentialProvider() {
            private String mSecurityToken;
            private String mAccessKeySecret;
            private String mAccessKeyId;

            @Override
            public OSSFederationToken getFederationToken() {
                mDillDataPresenter.getSessionToken(new DillDataPresenter.CallBack3() {
                    @Override
                    public void send(OSSInfo.DataBean data) {
                        mAccessKeyId = data.getCredentials().getAccessKeyId();
                        mAccessKeySecret = data.getCredentials().getAccessKeySecret();
                        mSecurityToken = data.getCredentials().getSecurityToken();
                        mEndpoint = data.getEndpoint();
                    }
                });
                return new OSSFederationToken(mAccessKeyId, mAccessKeySecret, mSecurityToken, mEndpoint);
            }
        };
        return new OSSClient(MyApp.getInstance().app(), mEndpoint, credentialProvider);
    }
}
