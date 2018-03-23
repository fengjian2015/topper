package com.bclould.tocotalk.ui.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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

import com.bclould.tocotalk.R;
import com.bclould.tocotalk.base.BaseActivity;
import com.bclould.tocotalk.base.FragmentFactory;
import com.bclould.tocotalk.base.MyApp;
import com.bclould.tocotalk.model.GitHubInfo;
import com.bclould.tocotalk.network.DownLoadApk;
import com.bclould.tocotalk.network.RetrofitUtil;
import com.bclould.tocotalk.ui.widget.DeleteCacheDialog;
import com.bclould.tocotalk.ui.widget.LoadingProgressDialog;
import com.bclould.tocotalk.utils.MessageEvent;
import com.bclould.tocotalk.utils.UtilTool;
import com.bclould.tocotalk.xmpp.XMConnectionListener;
import com.bclould.tocotalk.xmpp.XmppConnection;

import org.greenrobot.eventbus.EventBus;
import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smackx.ping.PingFailedListener;
import org.jivesoftware.smackx.ping.PingManager;

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

@android.support.annotation.RequiresApi(api = Build.VERSION_CODES.N)
public class MainActivity extends BaseActivity {

    @Bind(R.id.main_fl)
    FrameLayout mMainFl;
    @Bind(R.id.main_bottom_menu)
    LinearLayout mMainBottomMenu;

    public static MainActivity instance = null;
    private FragmentManager mSupportFragmentManager;
    private LoadingProgressDialog mProgressDialog;

    //单例
    public static MainActivity getInstance() {
        if (instance == null) {
            instance = new MainActivity();
        }
        return instance;
    }

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
        ButterKnife.bind(this);
        initInterface();
        MyApp.getInstance().addActivity(this);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideInput(v, ev)) {

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
            return super.dispatchTouchEvent(ev);
        }
        // 必不可少，否则所有的组件都不会有TouchEvent了
        if (getWindow().superDispatchTouchEvent(ev)) {
            return true;
        }
        return onTouchEvent(ev);
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
        //开始选中聊天Fragment
        setSelector(0);
        //切换Fragment
        changeFragment(0);
        //初始化底部菜单
        initBottomMenu();
        //获取权限
        UtilTool.getPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, "获取储存权限失败");
        UtilTool.getPermissions(this, Manifest.permission.CAMERA, "", "获取相机权限失败");
        UtilTool.getPermissions(this, Manifest.permission.RECORD_AUDIO, "", "获取语音权限失败");
        //自动登录即时通讯
        loginIM();
        //检测版本更新
        checkVersion();
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
                            float tag = Float.parseFloat(baseInfo.getTag_name());
                            if (version < tag)
                                showUpdateDialog(baseInfo);
                        }

                        @Override
                        public void onError(Throwable e) {
                            hideDialog();
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
        //显示更新dialog
        final DeleteCacheDialog deleteCacheDialog = new DeleteCacheDialog(R.layout.dialog_delete_cache, this);
        deleteCacheDialog.show();
        deleteCacheDialog.setTitle("检测到有新版本！");
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

    //显示登录中Dialog
    public void showDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = LoadingProgressDialog.createDialog(this);
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setMessage("登录中...");
        }
        mProgressDialog.show();
    }

    //隐藏登录中dialog
    public void hideDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }


    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    Toast.makeText(MainActivity.this, "网络异常，请稍后重试", Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    hideDialog();
                    break;
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //隐藏dialog
        hideDialog();
        //页面销毁删除掉储存的fragment
        Map<Integer, Fragment> map = FragmentFactory.mMainMap;
        FragmentFactory.getInstanes().setNull();
        for (int i : map.keySet()) {
            mSupportFragmentManager.beginTransaction().remove(map.get(i));
            mSupportFragmentManager.beginTransaction().hide(map.get(i));
        }
    }

    //登录即时通讯
    private void loginIM() {
        showDialog();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //连接openfile
                    AbstractXMPPConnection connection = XmppConnection.getInstance().getConnection();
                    //判断是否连接
                    if (connection != null && connection.isConnected()) {
                        String myUser = UtilTool.getMyUser();
                        String user = myUser.substring(0, myUser.indexOf("@"));
                        connection.login(user, UtilTool.getpw());
                        connection.addConnectionListener(new XMConnectionListener(user, UtilTool.getpw(), MainActivity.this));
                        /*if (connection.isAuthenticated()) {//登录成功
                            PingManager.setDefaultPingInterval(10);
                            PingManager myPingManager = PingManager.getInstanceFor(connection);
                            myPingManager.registerPingFailedListener(new PingFailedListener() {
                                @Override
                                public void pingFailed() {
                                    Toast.makeText(MainActivity.this, "发送心跳包失败", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }*/
                        //登录成功发送通知
                        EventBus.getDefault().post(new MessageEvent("登录成功"));
                        UtilTool.Log("fsdafa", "登录成功");
                        Message message = new Message();
                        message.what = 1;
                        mHandler.sendMessage(message);
                    }
                } catch (Exception e) {
                    //发送登录失败通知
                    EventBus.getDefault().post(new MessageEvent("登录失败"));
                    Message message = new Message();
                    mHandler.sendMessage(message);
                    message.what = 0;
                    hideDialog();
                    UtilTool.Log("日志", e.getMessage());
                    e.printStackTrace();
                }
            }
        }).start();
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

    private void changeFragment(int index) {

        if (mSupportFragmentManager == null) {

            mSupportFragmentManager = getSupportFragmentManager();
        }

        FragmentTransaction ft = mSupportFragmentManager.beginTransaction();

        FragmentFactory fragmentFactory = FragmentFactory.getInstanes();

        Fragment LastFragment = fragmentFactory.createMainFragment(lastIndex);

        Fragment fragment = fragmentFactory.createMainFragment(index);

        if (mSupportFragmentManager.getFragments() == null) {

            ft.add(R.id.main_fl, fragment);


        } else if (!mSupportFragmentManager.getFragments().contains(fragment)) {

            ft.add(R.id.main_fl, fragment);

        }

        ft.hide(LastFragment);

        ft.show(fragment);

        ft.commit();

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
            Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
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

}
