package com.bclould.tocotalk.base;

import android.app.Activity;
import android.app.Application;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.bclould.tocotalk.history.DBManager;
import com.bclould.tocotalk.utils.MySharedPreferences;
import com.bclould.tocotalk.utils.TestImageLoader;
import com.bclould.tocotalk.xmpp.XmppConnection;
import com.previewlibrary.ZoomMediaLoader;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by GA on 2017/9/18.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class MyApp extends Application {

    public static MyApp instance = null;
    private List<Activity> mActivityList = new ArrayList<>();

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

        ZoomMediaLoader.getInstance().init(new TestImageLoader());

        XmppConnection.getInstance().setDB(new DBManager(this));

        XmppConnection.getInstance().setContext(this);

//        AWSMobileClient.getInstance().initialize(this).execute();

    }

    public void RestartApp() {
        /*Intent i = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        UtilTool.Log("日志", "重启APP");
        startActivity(i);*/
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
