package com.bclould.tocotalk.network;

import android.os.Build;
import android.support.annotation.RequiresApi;

import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.common.auth.OSSAuthCredentialsProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.bclould.tocotalk.base.MyApp;
import com.bclould.tocotalk.utils.Constants;


/**
 * Created by GA on 2018/6/13.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class OSSupload {

    private static OSSupload instance;
    private OSSClient mOSSClient;

    //单例
    public static OSSupload getInstance() {
        if (instance == null) {
            instance = new OSSupload();
        }
        return instance;
    }

    public OSSClient visitOSS() {
        if (mOSSClient == null) {
            OSSCredentialProvider credentialProvider = new OSSAuthCredentialsProvider(Constants.BASE_URL + Constants.STS_SERVER);
            ClientConfiguration conf = new ClientConfiguration();
            conf.setConnectionTimeout(15 * 1000); // 连接超时，默认15秒
            conf.setSocketTimeout(15 * 1000); // socket超时，默认15秒
            conf.setMaxConcurrentRequest(20); // 最大并发请求书，默认5个
            conf.setMaxErrorRetry(2); // 失败后最大重试次数，默认2次
            mOSSClient = new OSSClient(MyApp.getInstance().app(), Constants.OSS_ENDOPINT, credentialProvider);
        }
        return mOSSClient;
    }
}
