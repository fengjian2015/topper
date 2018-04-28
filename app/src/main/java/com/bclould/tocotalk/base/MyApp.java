package com.bclould.tocotalk.base;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.bclould.tocotalk.history.DBManager;
import com.bclould.tocotalk.listener.CrashHandler;
import com.bclould.tocotalk.model.CoinInfo;
import com.bclould.tocotalk.model.CoinListInfo;
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

        //初始化sp
        MySharedPreferences.getInstance().init(this);

        new CrashHandler(this);
        //初始化
        ZoomMediaLoader.getInstance().init(new TestImageLoader());

        //初始化xmpp
        XmppConnection.getInstance().setDB(new DBManager(this));

        XmppConnection.getInstance().setContext(this);

        new Constants(this);

        //创建项目公开目录
        createDir();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            // 请注意这里会有一个版本适配bug，所以请在这里添加非空判断
            if (connectivityManager != null) {
                connectivityManager.requestNetwork(new NetworkRequest.Builder().build(), new ConnectivityManager.NetworkCallback() {

                    /**
                     * 网络可用的回调
                     * */
                    @Override
                    public void onAvailable(Network network) {
                        super.onAvailable(network);
                        Log.e("lzp", "onAvailable");
                    }

                    /**
                     * 网络丢失的回调
                     * */
                    @Override
                    public void onLost(Network network) {
                        super.onLost(network);
                        Log.e("lzp", "onLost");
                    }

                    /**
                     * 当建立网络连接时，回调连接的属性
                     * */
                    @Override
                    public void onLinkPropertiesChanged(Network network, LinkProperties linkProperties) {
                        super.onLinkPropertiesChanged(network, linkProperties);
                        Log.e("lzp", "onLinkPropertiesChanged");
                    }

                    /**
                     *  按照官方的字面意思是，当我们的网络的某个能力发生了变化回调，那么也就是说可能会回调多次
                     *
                     *  之后在仔细的研究
                     * */
                    @Override
                    public void onCapabilitiesChanged(Network network, NetworkCapabilities networkCapabilities) {
                        super.onCapabilitiesChanged(network, networkCapabilities);
                        Log.e("lzp", "onCapabilitiesChanged");
                    }

                    /**
                     * 在网络失去连接的时候回调，但是如果是一个生硬的断开，他可能不回调
                     * */
                    @Override
                    public void onLosing(Network network, int maxMsToLive) {
                        super.onLosing(network, maxMsToLive);
                        Log.e("lzp", "onLosing");
                    }


                });
            }
        }

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
