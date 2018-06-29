package com.bclould.tea.topperchat;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import com.bclould.tea.utils.UtilTool;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.PushAgent;
import com.umeng.message.UTrack;
import com.umeng.message.UmengMessageHandler;
import com.umeng.message.entity.UMessage;

/**
 * Created by GIjia on 2018/6/28.
 */
@RequiresApi(api = Build.VERSION_CODES.N)
public class UmManage {
    private static UmManage mInstance;
    private Context mContext;
    public PushAgent mPushAgent;
    public final static String SECRET="a5a739a2c291c19ad390fa93c365fdda";
    public static UmManage getInstance(){
        if(mInstance == null){
            synchronized (UmManage.class){
                if(mInstance == null){
                    mInstance = new UmManage();
                }
            }
        }
        return mInstance;
    }

    public void init(Context context){
        mContext=context;
        UMConfigure.init(context, UMConfigure.DEVICE_TYPE_PHONE, SECRET);
        initUpush();
    }

    private void initUpush() {
        UMConfigure.setLogEnabled(true);
        mPushAgent = PushAgent.getInstance(mContext);
        //注册推送服务 每次调用register都会回调该接口
        mPushAgent.register(new IUmengRegisterCallback() {
            @Override
            public void onSuccess(String deviceToken) {
                UtilTool.Log("fengjian",deviceToken);
            }

            @Override
            public void onFailure(String s, String s1) {
            }
        });
        UmengMessageHandler messageHandler = new UmengMessageHandler() {

            /**
             * 通知的回调方法（通知送达时会回调）
             */
            @Override
            public void dealWithNotificationMessage(Context context, UMessage msg) {
                //调用super，会展示通知，不调用super，则不展示通知。
//                super.dealWithNotificationMessage(context, msg);
                UtilTool.Log("fengjian",msg.custom);
                SocketListener.getInstance(context).friendRequest(msg.extra);

            }
        };
        mPushAgent.setMessageHandler(messageHandler);

    }

    public void setAlias(){
        mPushAgent.setAlias(UtilTool.getTocoId(), "Android", new UTrack.ICallBack() {
            @Override
            public void onMessage(boolean b, String s) {
                UtilTool.Log("fengjian","添加別名："+b+"   結果："+s);
            }
        });
    }


    public void deleteAlias(){
        mPushAgent.deleteAlias(UtilTool.getTocoId(), "Android", new UTrack.ICallBack() {
            @Override
            public void onMessage(boolean b, String s) {
                UtilTool.Log("fengjian","移除別名"+b+"    結果："+s);
            }
        });
    }
}
