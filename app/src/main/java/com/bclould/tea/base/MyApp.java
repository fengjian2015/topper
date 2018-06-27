package com.bclould.tea.base;

import android.app.Activity;
import android.app.Application;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.bclould.tea.R;
import com.bclould.tea.history.DBManager;
import com.bclould.tea.listener.CrashHandler;
import com.bclould.tea.model.CoinListInfo;
import com.bclould.tea.model.StateInfo;
import com.bclould.tea.network.MyHostnameVerifier;
import com.bclould.tea.service.ImageUpService;
import com.bclould.tea.utils.Constants;
import com.bclould.tea.utils.GlideImgLoader;
import com.bclould.tea.utils.MySharedPreferences;
import com.bclould.tea.utils.UtilTool;
import com.danikula.videocache.HttpProxyCacheServer;
import com.previewlibrary.ZoomMediaLoader;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.PushAgent;
import com.umeng.message.UTrack;
import com.umeng.message.UmengMessageHandler;
import com.umeng.message.UmengNotificationClickHandler;
import com.umeng.message.entity.UMessage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;


/**
 * Created by GA on 2017/9/18.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class MyApp extends Application {

    private static MyApp context;
    public static final String UPDATE_STATUS_ACTION = "com.bclould.tea.action.UPDATE_STATUS";


    public static MyApp instance = null;
    public Stack mActivityList = new Stack<Activity>();//储存打开的Activity
    public List<CoinListInfo.DataBean> mCoinList = new ArrayList<>();
    public List<CoinListInfo.DataBean> mRedCoinList = new ArrayList<>();
    public List<StateInfo.DataBean> mStateList = new ArrayList<>();
    public List<CoinListInfo.DataBean> mOtcCoinList = new ArrayList<>();
    public List<CoinListInfo.DataBean> mBetCoinList = new ArrayList<>();
    public List<CoinListInfo.DataBean> mPayCoinList = new ArrayList<>();
    private Handler mHandler;

    //单例
    public static MyApp getInstance() {
        if (instance == null) {
            instance = new MyApp();
        }
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        context = this;

        new MyHostnameVerifier();

        //初始化sp
        MySharedPreferences.getInstance().init(this);

        new CrashHandler(this);
        //初始化
        ZoomMediaLoader.getInstance().init(GlideImgLoader.getInstance());

        //创建项目公开目录
        createDir();

        UMConfigure.init(this, UMConfigure.DEVICE_TYPE_PHONE, "a5a739a2c291c19ad390fa93c365fdda");

        initUpush();

    }

    private void initUpush() {
        PushAgent mPushAgent = PushAgent.getInstance(this);
        mHandler = new Handler(getMainLooper());
        //注册推送服务 每次调用register都会回调该接口
        mPushAgent.register(new IUmengRegisterCallback() {
            @Override
            public void onSuccess(String deviceToken) {
                sendBroadcast(new Intent(UPDATE_STATUS_ACTION));
            }

            @Override
            public void onFailure(String s, String s1) {
                sendBroadcast(new Intent(UPDATE_STATUS_ACTION));
            }
        });
        String registrationId = mPushAgent.getRegistrationId();
        UtilTool.Log("設備ID", registrationId);
        UmengMessageHandler messageHandler = new UmengMessageHandler() {

            /**
             * 通知的回调方法（通知送达时会回调）
             */
            @Override
            public void dealWithNotificationMessage(Context context, UMessage msg) {
                //调用super，会展示通知，不调用super，则不展示通知。
                super.dealWithNotificationMessage(context, msg);
            }

            /**
             * 自定义消息的回调方法
             */
            @Override
            public void dealWithCustomMessage(final Context context, final UMessage msg) {

                mHandler.post(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        // 对自定义消息的处理方式，点击或者忽略
                        boolean isClickOrDismissed = true;
                        if (isClickOrDismissed) {
                            //自定义消息的点击统计
                            UTrack.getInstance(getApplicationContext()).trackMsgClick(msg);
                        } else {
                            //自定义消息的忽略统计
                            UTrack.getInstance(getApplicationContext()).trackMsgDismissed(msg);
                        }
                        Toast.makeText(context, msg.custom, Toast.LENGTH_LONG).show();
                    }
                });
            }

            /**
             * 自定义通知栏样式的回调方法
             */
            @Override
            public Notification getNotification(Context context, UMessage msg) {
                switch (msg.builder_id) {
                    case 1:
                        Notification.Builder builder = new Notification.Builder(context);
                        RemoteViews myNotificationView = new RemoteViews(context.getPackageName(),
                                R.layout.notification_view);
                        myNotificationView.setTextViewText(R.id.notification_title, msg.title);
                        myNotificationView.setTextViewText(R.id.notification_text, msg.text);
                        myNotificationView.setImageViewBitmap(R.id.notification_large_icon, getLargeIcon(context, msg));
                        myNotificationView.setImageViewResource(R.id.notification_small_icon,
                                getSmallIconId(context, msg));
                        builder.setContent(myNotificationView)
                                .setSmallIcon(getSmallIconId(context, msg))
                                .setTicker(msg.ticker)
                                .setAutoCancel(true);

                        return builder.getNotification();
                    default:
                        //默认为0，若填写的builder_id并不存在，也使用默认。
                        return super.getNotification(context, msg);
                }
            }
        };
        mPushAgent.setMessageHandler(messageHandler);
        /**
         * 自定义行为的回调处理，参考文档：高级功能-通知的展示及提醒-自定义通知打开动作
         * UmengNotificationClickHandler是在BroadcastReceiver中被调用，故
         * 如果需启动Activity，需添加Intent.FLAG_ACTIVITY_NEW_TASK
         * */
        UmengNotificationClickHandler notificationClickHandler = new UmengNotificationClickHandler() {

            @Override
            public void launchApp(Context context, UMessage msg) {
                super.launchApp(context, msg);
            }

            @Override
            public void openUrl(Context context, UMessage msg) {
                super.openUrl(context, msg);
            }

            @Override
            public void openActivity(Context context, UMessage msg) {
                super.openActivity(context, msg);
            }

            @Override
            public void dealWithCustomAction(Context context, UMessage msg) {
                Toast.makeText(context, msg.custom, Toast.LENGTH_LONG).show();
            }
        };
        mPushAgent.setNotificationClickHandler(notificationClickHandler);

    }

    //创建项目公开目录

    private void createDir() {
        File file = new File(Constants.PUBLICDIR);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    //初始化视频缓存库
    private HttpProxyCacheServer proxy;

    public static HttpProxyCacheServer getProxy(Context context) {
        MyApp app = (MyApp) context.getApplicationContext();
        return app.proxy == null ? (app.proxy = app.newProxy()) : app.proxy;
    }

    private HttpProxyCacheServer newProxy() {
        return new HttpProxyCacheServer(this);
    }

    //退出app
    public void RestartApp() {
        exit();
    }

    // 添加Activity到容器中
    public void addActivity(Activity activity) {
        mActivityList.add(activity);
    }

    // 遍历所有Activity并finish
    public void exit() {
        for (int i = 0; i < mActivityList.size(); i++) {
            Activity activity = (Activity) mActivityList.get(i);
            activity.finish();
        }
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        DBManager dbManager = new DBManager(this);
        if (dbManager.db != null) {
            dbManager.db.close();
        }
        dbManager = null;
        stopService(new Intent(this, ImageUpService.class));
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        DBManager dbManager = new DBManager(this);
        if (dbManager.db != null) {
            dbManager.db.close();
        }
        dbManager = null;
    }

    public MyApp app() {
        return context;
    }

}
