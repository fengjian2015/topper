package com.bclould.tocotalk.base;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.bclould.tocotalk.history.DBManager;
import com.bclould.tocotalk.listener.CrashHandler;
import com.bclould.tocotalk.model.CoinInfo;
import com.bclould.tocotalk.model.CoinListInfo;
import com.bclould.tocotalk.model.StateInfo;
import com.bclould.tocotalk.richtext.base.depence.fresco.FrescoCacheParams;
import com.bclould.tocotalk.richtext.base.depence.fresco.FrescoImageLoader;
import com.bclould.tocotalk.richtext.base.depence.retrofit.RetrofitClient;
import com.bclould.tocotalk.richtext.base.depence.tools.Constant;
import com.bclould.tocotalk.richtext.base.depence.tools.Utils;
import com.bclould.tocotalk.utils.Constants;
import com.bclould.tocotalk.utils.GlideImgLoader;
import com.bclould.tocotalk.utils.MySharedPreferences;
import com.bclould.tocotalk.xmpp.XmppConnection;
import com.danikula.videocache.HttpProxyCacheServer;
import com.facebook.cache.disk.DiskCacheConfig;
import com.facebook.common.internal.Supplier;
import com.facebook.common.util.ByteConstants;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.backends.okhttp3.OkHttpImagePipelineConfigFactory;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.imnjh.imagepicker.PickerConfig;
import com.imnjh.imagepicker.SImagePicker;
import com.previewlibrary.ZoomMediaLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by GA on 2017/9/18.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class MyApp extends Application {

    private static MyApp context;
    private ActivityManager activityManager;


    public static MyApp instance = null;
    private List<Activity> mActivityList = new ArrayList<>();//储存打开的Activity
    public List<CoinInfo.DataBean> mCoinList = new ArrayList<>();
    public List<StateInfo.DataBean> mStateList = new ArrayList<>();
    public List<CoinListInfo.DataBean> mOtcCoinList = new ArrayList<>();
    public List<CoinListInfo.DataBean> mBetCoinList = new ArrayList<>();
    public boolean isLogin = false;

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
        activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        ScreenParamsInit();
        FrescoInit();
        SImagePicker.init(new PickerConfig.Builder().setAppContext(this)
                .setImageLoader(new FrescoImageLoader())
                .build());

        //初始化sp
        MySharedPreferences.getInstance().init(this);

        new CrashHandler(this);
        //初始化
        ZoomMediaLoader.getInstance().init(GlideImgLoader.getInstance());

        //初始化xmpp
        XmppConnection.getInstance().setDB(new DBManager(this));

        XmppConnection.getInstance().setContext(this);

        new Constants(this);

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

    @Override
    public void onTerminate() {
        super.onTerminate();
        DBManager dbManager = new DBManager(this);
        if (dbManager.db != null) {
            dbManager.db.close();
        }
        dbManager = null;
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

    public static MyApp app() {
        return context;
    }

    private void ScreenParamsInit() {
        int a[] = Utils.getScreenSize();
        Constant.screenWithPx = a[0];
        Constant.screenHeightPx = a[1];
        Constant.screenWithDp = Utils.px2dip(Constant.screenWithPx);
        Constant.screenHeightDp = Utils.px2dip(Constant.screenHeightPx);
    }

    private void FrescoInit() {
        DiskCacheConfig diskCacheConfig = DiskCacheConfig.newBuilder(this)
                .setMaxCacheSize(40 * ByteConstants.MB)
                .setBaseDirectoryPathSupplier(new Supplier<File>() {
                    @Override
                    public File get() {
                        return getCacheDir();
                    }
                })
                .build();

        final FrescoCacheParams bitmapCacheParams = new FrescoCacheParams(activityManager);
        ImagePipelineConfig imagePipelineConfig = OkHttpImagePipelineConfigFactory.newBuilder(this, RetrofitClient.getInstance().getOkHttpClient())
                .setMainDiskCacheConfig(diskCacheConfig)
                .setBitmapMemoryCacheParamsSupplier(bitmapCacheParams)
                .setDownsampleEnabled(true)
                .build();
        Fresco.initialize(this, imagePipelineConfig);
    }
}
