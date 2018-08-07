package com.bclould.tea.alipay;

import android.content.Context;


/**
 * Created by GIjia on 2018/8/7.
 */

public class AlipayClient {
    private static AlipayClient mInstance;
    private Context mContext;

    public static AlipayClient getInstance(){
        if(mInstance == null){
            synchronized (AlipayClient.class){
                if(mInstance == null){
                    mInstance = new AlipayClient();
                }
            }
        }
        return mInstance;
    }

    public void init(Context context){
        mContext=context;
        initAlipay(context);
    }

    private void initAlipay(Context context) {

    }
}
