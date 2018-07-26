package com.bclould.tea.topperchat;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;

import com.bclould.tea.utils.UtilTool;
import com.tencent.android.tpush.XGIOperateCallback;
import com.tencent.android.tpush.XGPushManager;

/**
 * Created by GIjia on 2018/7/17.
 */
@RequiresApi(api = Build.VERSION_CODES.N)
public class XGManage {
    private static XGManage mInstance;
    private Context mContext;
    public static XGManage getInstance(){
        if(mInstance == null){
            synchronized (XGManage.class){
                if(mInstance == null){
                    mInstance = new XGManage();
                }
            }
        }
        return mInstance;
    }

    public void init(Context context){
        mContext=context;
        initPush(context);
    }

    private void initPush(Context context) {
        XGPushManager.registerPush(context, new XGIOperateCallback() {
            @Override
            public void onSuccess(Object data, int flag) {
                //token在设备卸载重装的时候有可能会变
                UtilTool.Log("fengjian","注册成功，设备token为：" + data);
            }
            @Override
            public void onFail(Object data, int errCode, String msg) {
                UtilTool.Log("fengjian","注册失败，错误码：" + errCode + ",错误信息：" + msg);
            }
        });
    }

    public void setAlias(){
        XGPushManager.appendAccount(mContext, UtilTool.getTocoId(), new XGIOperateCallback() {
            @Override
            public void onSuccess(Object o, int i) {
                UtilTool.Log("fengjian","添加別名成功："+o);
            }

            @Override
            public void onFail(Object o, int i, String s) {
                UtilTool.Log("fengjian","添加別名失败："+o+"    "+s);
                mHandler.sendEmptyMessageDelayed(1,1*1000);
            }
        });
    }

    Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            setAlias();
        }
    };


    public void deleteAlias(){
        XGPushManager.delAccount(mContext, UtilTool.getTocoId(), new XGIOperateCallback() {
            @Override
            public void onSuccess(Object o, int i) {
                UtilTool.Log("fengjian","移除別名成功："+o);
            }

            @Override
            public void onFail(Object o, int i, String s) {
                UtilTool.Log("fengjian","移除別名失败："+o+"         "+s);
            }
        });
    }
}
