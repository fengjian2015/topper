package com.bclould.tocotalk.base;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.bclould.tocotalk.history.DBManager;
import com.bclould.tocotalk.listener.CrashHandler;
import com.bclould.tocotalk.model.CoinInfo;
import com.bclould.tocotalk.model.StateInfo;
import com.bclould.tocotalk.utils.Constants;
import com.bclould.tocotalk.utils.MySharedPreferences;
import com.bclould.tocotalk.utils.TestImageLoader;
import com.bclould.tocotalk.xmpp.XmppConnection;
import com.danikula.videocache.HttpProxyCacheServer;
import com.previewlibrary.ZoomMediaLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by GA on 2017/9/18.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class MyApp extends Application {

    public static MyApp instance = null;
    private List<Activity> mActivityList = new ArrayList<>();//储存打开的Activity
    public List<CoinInfo.DataBean> mCoinList = new ArrayList<>();
    public List<StateInfo.DataBean> mStateList = new ArrayList<>();

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

        //初始化sp
        MySharedPreferences.getInstance().init(this);

        new CrashHandler(this);
        //初始化
        ZoomMediaLoader.getInstance().init(new TestImageLoader());

        //初始化xmpp
        XmppConnection.getInstance().setDB(new DBManager(this));

        XmppConnection.getInstance().setContext(this);

        //创建项目公开目录
        createDir();

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
        for (Activity activity : mActivityList) {
            activity.finish();
        }
    }

}
