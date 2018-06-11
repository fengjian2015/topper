package com.bclould.tocotalk.ui.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bclould.tocotalk.Presenter.CoinPresenter;
import com.bclould.tocotalk.Presenter.DillDataPresenter;
import com.bclould.tocotalk.Presenter.PersonalDetailsPresenter;
import com.bclould.tocotalk.R;
import com.bclould.tocotalk.base.BaseActivity;
import com.bclould.tocotalk.base.FragmentFactory;
import com.bclould.tocotalk.base.MyApp;
import com.bclould.tocotalk.history.DBManager;
import com.bclould.tocotalk.model.AuatarListInfo;
import com.bclould.tocotalk.model.AwsInfo;
import com.bclould.tocotalk.model.GitHubInfo;
import com.bclould.tocotalk.network.DownLoadApk;
import com.bclould.tocotalk.network.RetrofitUtil;
import com.bclould.tocotalk.ui.widget.DeleteCacheDialog;
import com.bclould.tocotalk.utils.Constants;
import com.bclould.tocotalk.utils.MessageEvent;
import com.bclould.tocotalk.utils.MySharedPreferences;
import com.bclould.tocotalk.utils.UtilTool;
import com.bclould.tocotalk.xmpp.XmppConnection;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.bclould.tocotalk.ui.activity.ConversationActivity.ACCESSKEYID;
import static com.bclould.tocotalk.ui.activity.ConversationActivity.SECRETACCESSKEY;
import static com.bclould.tocotalk.ui.activity.ConversationActivity.SESSIONTOKEN;

@android.support.annotation.RequiresApi(api = Build.VERSION_CODES.N)
public class MainActivity extends BaseActivity {

    @Bind(R.id.main_fl)
    FrameLayout mMainFl;
    @Bind(R.id.main_bottom_menu)
    LinearLayout mMainBottomMenu;
    private ArrayList<MyOnTouchListener> onTouchListeners = new ArrayList<MyOnTouchListener>(10);
    public static MainActivity instance = null;
    private FragmentManager mSupportFragmentManager;
    private CoinPresenter mCoinPresenter;
    private DBManager mMgr;

    //单例
    public static MainActivity getInstance() {
        if (instance == null) {
            instance = new MainActivity();
        }
        return instance;
    }

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSupportFragmentManager = getSupportFragmentManager();
        if (savedInstanceState != null && savedInstanceState.getBoolean("isMainActivityDestroy", false)) {
            //当activity被系统销毁，获取到之前的fragment，并且移除之前的fragment的状态
            for (Fragment fragment : mSupportFragmentManager.getFragments()) {
                mSupportFragmentManager.beginTransaction().remove(fragment).commit();
            }
        }
        setContentView(R.layout.activity_main);
        mCoinPresenter = new CoinPresenter(this);
        ButterKnife.bind(this);
        mMgr = new DBManager(this);
        initInterface();
        MyApp.getInstance().addActivity(this);
        XmppConnection.loginService(this);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        for (MyOnTouchListener listener : onTouchListeners) {
            if (listener != null) {
                listener.onTouch(ev);
            }
        }
        if (ev.getAction() == MotionEvent.ACTION_DOWN || ev.getAction() == MotionEvent.ACTION_MOVE) {
            View v = getCurrentFocus();
            if (isShouldHideInput(v, ev)) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    EventBus.getDefault().post(new MessageEvent(getString(R.string.hide_keyboard)));
                }
            }
            return super.dispatchTouchEvent(ev);
        }
        // 必不可少，否则所有的组件都不会有TouchEvent了
        if (getWindow().superDispatchTouchEvent(ev)) {
            return true;
        }
        return super.dispatchTouchEvent(ev);
    }

    public void registerMyOnTouchListener(MyOnTouchListener myOnTouchListener) {
        onTouchListeners.add(myOnTouchListener);
    }

    public void unregisterMyOnTouchListener(MyOnTouchListener myOnTouchListener) {
        onTouchListeners.remove(myOnTouchListener);
    }

    public boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] leftTop = {0, 0};
            //获取输入框当前的location位置
            v.getLocationInWindow(leftTop);
            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + v.getHeight();
            int right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击的是输入框区域，保留点击EditText的事件
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    //初始化界面
    private void initInterface() {
        getMyImage();
        //开始选中聊天Fragment
        setSelector(0);
        //切换Fragment
        changeFragment(0);
        //初始化底部菜单
        initBottomMenu();
        //获取权限
        UtilTool.getPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, getString(R.string.jurisdiction_store_hint));
        UtilTool.getPermissions(this, Manifest.permission.CAMERA, "", getString(R.string.jurisdiction_camera_hint));
        UtilTool.getPermissions(this, Manifest.permission.RECORD_AUDIO, "", getString(R.string.jurisdiction_voice_hint));
        //儲存自己信息
        //自动登录即时通讯
//        loginIM();
        initAWS();
        //检测版本更新
        checkVersion();
        //获取币种
        getCoinList();
        //获取国家
        getStateList();

    }

    private void getMyImage() {
        if (!mMgr.findUser(UtilTool.getJid())) {
            PersonalDetailsPresenter personalDetailsPresenter = new PersonalDetailsPresenter(this);
            personalDetailsPresenter.getFriendImageList(UtilTool.getJid(), new PersonalDetailsPresenter.CallBack2() {
                @Override
                public void send(List<AuatarListInfo.DataBean> data) {
                    if (data != null && data.size() != 0) {
                        mMgr.addUserList(data);
                    }
                }
            });
        }
    }

    private void initAWS() {
        DillDataPresenter dillDataPresenter = new DillDataPresenter(this);
        dillDataPresenter.getSessionToken(new DillDataPresenter.CallBack3() {
            @Override
            public void send(AwsInfo.DataBean data) {
                MySharedPreferences.getInstance().setString(ACCESSKEYID, data.getAccessKeyId());
                MySharedPreferences.getInstance().setString(SECRETACCESSKEY, data.getSecretAccessKey());
                MySharedPreferences.getInstance().setString(SESSIONTOKEN, data.getSessionToken());
            }
        });
    }

//    private void pingService() {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                PingManager pingManager = PingManager.getInstanceFor(XmppConnection.getInstance().getConnection());
//                pingManager.setPingInterval(60);
//                try {
//                    pingManager.pingMyServer();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                pingManager.registerPingFailedListener(new PingFailedListener() {
//                    @Override
//                    public void pingFailed() {
//                        if (tExit == null) {
//                            tExit = new Timer();
//                            tExit.schedule(new TimeTask(), 1000);
//                        }
//                    }
//                });
//            }
//        }).start();
//    }
//
//    private class TimeTask extends TimerTask {
//        @Override
//        public void run() {
//
//            if (UtilTool.getUser() != null && UtilTool.getpw() != null) {
//                Log.i("XMConnectionListener", "尝试登录");
//                // 连接服务器
//                try {
//                    if (!XmppConnection.getInstance().isAuthenticated()) {// 用户未登录
//                        if (XmppConnection.getInstance().login(UtilTool.getUser(), UtilTool.getpw())) {
//                            Log.i("XMConnectionListener", "登录成功");
//                            EventBus.getDefault().post(new MessageEvent(getString(R.string.login_succeed)));
//                            Intent intent = new Intent();
//                            intent.setAction("XMPPConnectionListener");
//                            intent.putExtra("type", true);
//                            sendBroadcast(intent);
//                        } else {
//                            Log.i("XMConnectionListener", "重新登录");
//                            tExit.schedule(new TimeTask(), 1000);
//                        }
//                    }
//                } catch (Exception e) {
//                    Log.i("XMConnectionListener", "尝试登录,出现异常!");
//                    Log.i("XMConnectionListener", e.getMessage());
//                }
//            }
//        }
//    }

    private void getStateList() {
        mCoinPresenter.getState();
    }

    private void getCoinList() {
        mCoinPresenter.getCoin();
    }

    //检测版本更新
    private void checkVersion() {
        //判断是否开启网络
        if (UtilTool.isNetworkAvailable(this)) {
            RetrofitUtil.getInstance(this)
                    .getServer()
                    .checkVersion("https://api.github.com/repos/bclould/tocotalk/releases/latest")//githua获取版本更新
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                    .subscribe(new Observer<GitHubInfo>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(GitHubInfo baseInfo) {
                            //判断是否需要更新
                            float version = Float.parseFloat(UtilTool.getVersionCode(MainActivity.this));
                            String tag_version = "";
                            if (baseInfo.getTag_name().contains("v")) {
                                tag_version = baseInfo.getTag_name().replace("v", "");
                            } else {
                                tag_version = baseInfo.getTag_name();
                            }
                            float tag = Float.parseFloat(tag_version);
                            if (version < tag) {
                                showUpdateDialog(baseInfo);
                            } else {
                                MySharedPreferences.getInstance().setString(Constants.NEW_APK_URL, "");
                                MySharedPreferences.getInstance().setString(Constants.NEW_APK_NAME, "");
                                MySharedPreferences.getInstance().setString(Constants.NEW_APK_BODY, "");
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            UtilTool.Log("日志", e.getMessage());
                            Toast.makeText(MainActivity.this, getString(R.string.toast_network_error), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        } else {
            Toast.makeText(MainActivity.this, getString(R.string.toast_network_error), Toast.LENGTH_SHORT).show();
        }
    }

    //显示Dialog
    private void showUpdateDialog(GitHubInfo gitHubInfo) {
        //获取download连接
        final String url = gitHubInfo.getAssets().get(0).getBrowser_download_url();
        //获取apk名字
        final String appName = gitHubInfo.getName();
        //更新描述
        final String body = gitHubInfo.getBody();
        MySharedPreferences.getInstance().setString(Constants.NEW_APK_URL, url);
        MySharedPreferences.getInstance().setString(Constants.NEW_APK_NAME, appName);
        MySharedPreferences.getInstance().setString(Constants.NEW_APK_BODY, body);
        //显示更新dialog
        final DeleteCacheDialog deleteCacheDialog = new DeleteCacheDialog(R.layout.dialog_delete_cache, this, R.style.dialog);
        deleteCacheDialog.show();
        deleteCacheDialog.setTitle(getString(R.string.check_new_version));
        Button cancel = (Button) deleteCacheDialog.findViewById(R.id.btn_cancel);
        Button confirm = (Button) deleteCacheDialog.findViewById(R.id.btn_confirm);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteCacheDialog.dismiss();
            }
        });
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!canDownloadState()) {
                    showDownloadSetting();
                    return;
                }
                UtilTool.Log("版本更新", url);
                DownLoadApk.download(MainActivity.this, url, body, appName);
                deleteCacheDialog.dismiss();
            }
        });

    }

    //获取intent意图
    private boolean intentAvailable(Intent intent) {
        PackageManager packageManager = getPackageManager();
        List list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    //更新完成弹出安装
    private void showDownloadSetting() {
        String packageName = "com.android.providers.downloads";
        Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + packageName));
        if (intentAvailable(intent)) {
            startActivity(intent);
        }
    }

    //下载状态
    private boolean canDownloadState() {
        try {
            int state = this.getPackageManager().getApplicationEnabledSetting("com.android.providers.downloads");

            if (state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED
                    || state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_USER
                    || state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_UNTIL_USED) {
                return false;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //页面销毁删除掉储存的fragment
        Map<Integer, Fragment> map = FragmentFactory.mMainMap;
        FragmentFactory.getInstanes().setNull();
        for (int i : map.keySet()) {
            mSupportFragmentManager.beginTransaction().remove(map.get(i));
            mSupportFragmentManager.beginTransaction().hide(map.get(i));
        }
    }

    //初始化底部菜单栏
    private void initBottomMenu() {

        for (int i = 0; i < mMainBottomMenu.getChildCount(); i++) {

            final View childAt = mMainBottomMenu.getChildAt(i);

            childAt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    int index = mMainBottomMenu.indexOfChild(childAt);

                    changeFragment(index);

                    setSelector(index);
                }
            });

        }

    }


    //切换Fragment
    int lastIndex = 0;

    @SuppressLint("RestrictedApi")
    private void changeFragment(int index) {

        if (mSupportFragmentManager == null) {

            mSupportFragmentManager = getSupportFragmentManager();
        }

        FragmentTransaction ft = mSupportFragmentManager.beginTransaction();

        FragmentFactory fragmentFactory = FragmentFactory.getInstanes();

        Fragment LastFragment = fragmentFactory.createMainFragment(lastIndex);

        Fragment fragment = fragmentFactory.createMainFragment(index);

        if (mSupportFragmentManager.getFragments() == null) {
            if (!fragment.isAdded()) {
                ft.add(R.id.main_fl, fragment);
            }
        } else if (!mSupportFragmentManager.getFragments().contains(fragment)) {
            if (!fragment.isAdded()) {
                ft.add(R.id.main_fl, fragment);
            }
        }
        if (ft != null) {
            ft.hide(LastFragment);

            ft.show(fragment);

            ft.commit();
        }

        lastIndex = index;

    }


//     当activity销毁时不保存其内部的view的状态

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("isMainActivityDestroy", true);
    }

    //菜单选项选中处理
    private void setSelector(int index) {

        for (int i = 0; i < mMainBottomMenu.getChildCount(); i++) {

            if (i == index) {

                mMainBottomMenu.getChildAt(i).setSelected(true);

            } else {

                mMainBottomMenu.getChildAt(i).setSelected(false);

            }
        }
    }

    /**
     * 菜单、返回键响应
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exitBy2Click();      //调用双击退出函数
        }
        return false;
    }

    //双击退出函数
    private static Boolean isExit = false;

    private void exitBy2Click() {
        Timer tExit = null;
        if (isExit == false) {
            isExit = true; // 准备退出
            Toast.makeText(this, getString(R.string.toast_quit_hint), Toast.LENGTH_SHORT).show();
            tExit = new Timer();
            tExit.schedule(new TimerTask() {
                @Override
                public void run() {
                    isExit = false; // 取消退出
                }
            }, 3000); // 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务

        } else {
            Intent home = new Intent(Intent.ACTION_MAIN);
            home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            home.addCategory(Intent.CATEGORY_HOME);
            startActivity(home);
        }
    }

    public interface MyOnTouchListener {
        public boolean onTouch(MotionEvent ev);
    }
}
