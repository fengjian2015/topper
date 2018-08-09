package com.bclould.tea.base;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;

import com.baidu.ocr.sdk.OCR;
import com.baidu.ocr.sdk.OnResultListener;
import com.baidu.ocr.sdk.exception.OCRError;
import com.baidu.ocr.sdk.model.AccessToken;
import com.bclould.tea.R;
import com.bclould.tea.listener.CrashHandler;
import com.bclould.tea.model.CoinListInfo;
import com.bclould.tea.model.StateInfo;
import com.bclould.tea.network.MyHostnameVerifier;
import com.bclould.tea.topperchat.RoomMemberManage;
import com.bclould.tea.topperchat.XGManage;
import com.bclould.tea.utils.AppLanguageUtils;
import com.bclould.tea.utils.Constants;
import com.bclould.tea.utils.GlideImgLoader;
import com.bclould.tea.utils.MyLifecycleHandler;
import com.bclould.tea.utils.MySharedPreferences;
import com.danikula.videocache.HttpProxyCacheServer;
import com.previewlibrary.ZoomMediaLoader;

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
    public static MyApp instance = null;
    public Stack mActivityList = new Stack<Activity>();//储存打开的Activity
    public List<CoinListInfo.DataBean> mCoinList = new ArrayList<>();
    public List<CoinListInfo.DataBean> mRedCoinList = new ArrayList<>();
    public List<StateInfo.DataBean> mStateList = new ArrayList<>();
    public List<CoinListInfo.DataBean> mOtcCoinList = new ArrayList<>();
    public List<CoinListInfo.DataBean> mBetCoinList = new ArrayList<>();
    public List<CoinListInfo.DataBean> mPayCoinList = new ArrayList<>();
    public List<CoinListInfo.DataBean> mRewardCoinList = new ArrayList<>();

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

        onLanguageChange();

        new MyHostnameVerifier();

        //初始化sp
        MySharedPreferences.getInstance().init(this);

        new CrashHandler(this);
        //初始化
        ZoomMediaLoader.getInstance().init(GlideImgLoader.getInstance());

        //创建项目公开目录
        createDir();

        XGManage.getInstance().init(this);

        registerActivityLifecycleCallbacks(new MyLifecycleHandler());

        RoomMemberManage.getInstance().setContext(this);

        OCR.getInstance(this).initAccessTokenWithAkSk(new OnResultListener<AccessToken>() {
            @Override
            public void onResult(AccessToken result) {

            }

            @Override
            public void onError(OCRError error) {
                error.printStackTrace();
            }
        }, getApplicationContext(), "kwPFq1qEMF7Uu9XQ1eYXuza8", "GlhguubBaj0XGKYYOpNVZHCYquokpdIj");
    }


    //创建项目公开目录

    private void createDir() {
        File file = new File(Constants.PUBLICDIR);
        File download = new File(Constants.DOWNLOAD);
        if (!file.exists()) {
            file.mkdirs();
        }
        if (!download.exists()) {
            download.mkdirs();
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

    // 遍历所有Activity并finish指定activity
    public void exit(String activityName) {
        for (int i = 0; i < mActivityList.size(); i++) {
            Activity activity = (Activity) mActivityList.get(i);
            if (activity.getClass().getName().equals(activityName)) {
                activity.finish();
            }
        }
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        exit();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
    }

    public MyApp app() {
        return context;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(AppLanguageUtils.attachBaseContext(base, getAppLanguage(base)));
    }

    /**
     * Handling Configuration Changes
     *
     * @param newConfig newConfig
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        onLanguageChange();
    }

    private void onLanguageChange() {
        AppLanguageUtils.changeAppLanguage(this, AppLanguageUtils.getSupportLanguage(getAppLanguage(this)));
    }

    private String getAppLanguage(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(context.getString(R.string.language_pref_key), "");
    }

}
