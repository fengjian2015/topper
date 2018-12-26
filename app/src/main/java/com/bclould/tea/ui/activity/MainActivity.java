package com.bclould.tea.ui.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.widget.TextView;
import android.widget.Toast;

import com.bclould.tea.Presenter.CoinPresenter;
import com.bclould.tea.Presenter.GroupPresenter;
import com.bclould.tea.Presenter.IndividualDetailsPresenter;
import com.bclould.tea.Presenter.PersonalDetailsPresenter;
import com.bclould.tea.Presenter.UpdateLogPresenter;
import com.bclould.tea.R;
import com.bclould.tea.base.BaseActivity;
import com.bclould.tea.base.FragmentFactory;
import com.bclould.tea.base.MyApp;
import com.bclould.tea.history.DBManager;
import com.bclould.tea.history.DBRoomManage;
import com.bclould.tea.history.DBRoomMember;
import com.bclould.tea.model.AuatarListInfo;
import com.bclould.tea.model.ConversationInfo;
import com.bclould.tea.model.GroupInfo;
import com.bclould.tea.model.IndividualInfo;
import com.bclould.tea.model.MessageTopInfo;
import com.bclould.tea.service.IMCoreService;
import com.bclould.tea.service.IMService;
import com.bclould.tea.topperchat.AddFriendReceiver;
import com.bclould.tea.topperchat.WsConnection;
import com.bclould.tea.ui.activity.authorization.AuthorizationActivity;
import com.bclould.tea.ui.fragment.DiscoverFragment;
import com.bclould.tea.ui.widget.DeleteCacheDialog;
import com.bclould.tea.utils.Constants;
import com.bclould.tea.utils.AppLanguageUtils;
import com.bclould.tea.utils.EventBusUtil;
import com.bclould.tea.utils.IMUtils;
import com.bclould.tea.utils.MessageEvent;
import com.bclould.tea.utils.MySharedPreferences;
import com.bclould.tea.utils.SharedPreferencesUtil;
import com.bclould.tea.utils.StatusBarCompat;
import com.bclould.tea.utils.StringUtils;
import com.bclould.tea.utils.UtilTool;
import com.bclould.tea.xmpp.ConnectStateChangeListenerManager;
import com.bclould.tea.xmpp.RoomManage;
import com.bumptech.glide.Glide;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.leolin.shortcutbadger.ShortcutBadger;

import static com.bclould.tea.Presenter.LoginPresenter.IS_UPDATE;


@android.support.annotation.RequiresApi(api = Build.VERSION_CODES.N)
public class MainActivity extends BaseActivity {
    @Bind(R.id.number)
    TextView mNumber;
    @Bind(R.id.main_fl)
    FrameLayout mMainFl;
    @Bind(R.id.main_bottom_menu)
    LinearLayout mMainBottomMenu;
    private ArrayList<MyOnTouchListener> onTouchListeners = new ArrayList<MyOnTouchListener>(10);
    public static MainActivity instance = null;
    private FragmentManager mSupportFragmentManager;
    private CoinPresenter mCoinPresenter;
    private DBManager mMgr;
    private DBRoomManage mDBRoomManage;
    private DBRoomMember mDBRoomMember;
    private AddFriendReceiver mReceiver;

    private Bundle mBundle;//用系統分享

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
        StatusBarCompat.setImmersionStateMode(this);
        super.onCreate(savedInstanceState);
        setSwipeEnabled(false);
        mSupportFragmentManager = getSupportFragmentManager();
        if (savedInstanceState != null && savedInstanceState.getBoolean("isMainActivityDestroy", false)) {
            //当activity被系统销毁，获取到之前的fragment，并且移除之前的fragment的状态
            if (mSupportFragmentManager.getFragments() != null) {
                for (Fragment fragment : mSupportFragmentManager.getFragments()) {
                    mSupportFragmentManager.beginTransaction().remove(fragment).commit();
                }
            }
        }
        setContentView(R.layout.activity_main);
        mCoinPresenter = new CoinPresenter(this);
        mBundle = getIntent().getExtras();
        ButterKnife.bind(this);
        mMgr = new DBManager(this);
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
        mDBRoomManage = new DBRoomManage(this);
        mDBRoomMember = new DBRoomMember(this);
        initRelogin();
        initInterface();
        MyApp.getInstance().addActivity(this);
        WsConnection.loginService(this);
        initAddFriendReceiver();
        isGoSelectConversation();
    }

    private void isGoSelectConversation() {
        if (mBundle != null && MySharedPreferences.getInstance().getBoolean("SHARE") && !WsConnection.getInstance().getOutConnection()) {
            Intent intent = new Intent(this, SelectConversationActivity.class);
            intent.putExtra("type", 3);
            intent.putExtras(mBundle);
            startActivity(intent);
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(AppLanguageUtils.attachBaseContext(newBase, MySharedPreferences.getInstance().getString(newBase.getString(R.string.language_pref_key))));
    }

    private void initRelogin() {
        if (!WsConnection.getInstance().getOutConnection()) {
            ConnectStateChangeListenerManager.get().notifyListener(ConnectStateChangeListenerManager.CONNECTING);
            Intent intent = new Intent(this, IMCoreService.class);
            if (WsConnection.isServiceWork(this, IMCoreService.CORE_SERVICE_NAME)) {
                WsConnection.stopAllIMCoreService(this);
                stopService(intent);
            }
            startService(intent);
        }
    }

    private void resumeRelogin() {
        if (!WsConnection.getInstance().getOutConnection()) {
            if (!WsConnection.isServiceWork(this, IMCoreService.CORE_SERVICE_NAME)) {
                ConnectStateChangeListenerManager.get().notifyListener(ConnectStateChangeListenerManager.CONNECTING);
                Intent intent1 = new Intent(this, IMCoreService.class);
                startService(intent1);
            } else if (!WsConnection.isServiceWork(this, IMCoreService.SERVICE_NAME)) {
                ConnectStateChangeListenerManager.get().notifyListener(ConnectStateChangeListenerManager.CONNECTING);
                stopService(new Intent(this, IMService.class));
                Intent intent = new Intent();
                intent.setAction(IMCoreService.ACTION_START_IMSERVICE);
                sendBroadcast(intent);
            } else {
                if (IMUtils.compareServiceTime()) {
                    stopService(new Intent(this, IMService.class));
                    Intent intent = new Intent();
                    intent.setAction(IMCoreService.ACTION_START_IMSERVICE);
                    sendBroadcast(intent);
                }
            }
        } else {
            ConnectStateChangeListenerManager.get().notifyListener(ConnectStateChangeListenerManager.CONNECTING);
        }
    }

    /**
     * 第三方授权根据IS_EXTERNAL判断是否跳转到授权页面
     */
    private void goAuthorization(){
        boolean isExternal=MySharedPreferences.getInstance().getBoolean(SharedPreferencesUtil.IS_EXTERNAL);
        if(isExternal){
            Intent intentResult = new Intent(MyApp.getInstance().getTopActivity(), AuthorizationActivity.class);
            startActivity(intentResult);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        resumeRelogin();
        goAuthorization();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        //whence =1 登錄   2 退出登錄  3強制退出
        int whence = intent.getIntExtra("whence", 0);
        initRelogin();
        refreshNumber();
        isGoSelectConversation();
        if (MySharedPreferences.getInstance().getInteger(IS_UPDATE) == 1) {
            //检测版本更新
            checkVersion();
        }
        if (1 == whence) {
            DiscoverFragment discoverFragment = DiscoverFragment.getInstance();
            discoverFragment.initInterface();
            setSelector(0);
            //切换Fragment
            changeFragment(0);
            getStateList();
            getGroup();
            getMyImage();
            getFriends();
            getChatBackGround();
            mallLogin();
        } else if (2 == whence || 3 == whence) {
            DiscoverFragment discoverFragment = DiscoverFragment.getInstance();
            discoverFragment.initInterface();
            setSelector(0);
            //切换Fragment
            changeFragment(0);
            if (3 == whence) {
                showLoginOut();
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        String msg = event.getMsg();
        if (msg.equals(EventBusUtil.refresh_msg_number)) {
            refreshNumber();
        }
    }

    /**
     * 判断是否跳转到商城登录
     */
    private void mallLogin(){
        String mallUrl=MySharedPreferences.getInstance().getString(SharedPreferencesUtil.WEB_LOGIN);
        if(!StringUtils.isEmpty(mallUrl)){
            Intent intent=new Intent(this,HTMLActivity.class);
            intent.putExtra("html5Url",mallUrl);
            startActivity(intent);
        }
    }

    private void refreshNumber() {
        int numer = mMgr.queryConversationNumber();
        if (numer > 0 && !WsConnection.getInstance().getOutConnection()) {
            mNumber.setVisibility(View.VISIBLE);
            if (numer >= 100) {
                ShortcutBadger.applyCount(MainActivity.this, 99); //for 1.1.4+
                mNumber.setText("99+");
            } else {
                ShortcutBadger.applyCount(MainActivity.this, numer); //for 1.1.4+
                mNumber.setText(numer + "");
            }
        } else {
            ShortcutBadger.removeCount(MainActivity.this);
            mNumber.setVisibility(View.GONE);
        }
    }

    public void showLoginOut() {
        final DeleteCacheDialog deleteCacheDialog = new DeleteCacheDialog(R.layout.dialog_delete_cache, MainActivity.this, R.style.dialog);
        deleteCacheDialog.show();
        deleteCacheDialog.setTitle(this.getString(R.string.force_quit_login_again));
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
                deleteCacheDialog.dismiss();
                startActivity(new Intent(MainActivity.this, InitialActivity.class));
            }
        });
    }

    private void initAddFriendReceiver() {
        if (mReceiver == null) {
            IntentFilter filter = new IntentFilter();
            filter.addAction("com.bclould.tea.addfriend");
            mReceiver = new AddFriendReceiver();
            registerReceiver(mReceiver, filter);
        }
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
//                    EventBus.getDefault().post(new MessageEvent(getString(R.string.hide_keyboard)));
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

        //开始选中聊天Fragment
        if (WsConnection.getInstance().getOutConnection()) {
            setSelector(0);
            //切换Fragment
            changeFragment(0);
        } else {
            setSelector(0);
            //切换Fragment
            changeFragment(0);
            getStateList();
            getGroup();
            getMyImage();
            getFriends();
            getChatBackGround();
        }
        //初始化底部菜单
        initBottomMenu();
        //获取权限
        UtilTool.getPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, getString(R.string.jurisdiction_store_hint));
        UtilTool.getPermissions(this, Manifest.permission.CAMERA, "", getString(R.string.jurisdiction_camera_hint));
        UtilTool.getPermissions(this, Manifest.permission.RECORD_AUDIO, "", getString(R.string.jurisdiction_voice_hint));
        if (MySharedPreferences.getInstance().getInteger(IS_UPDATE) == 1) {
            //检测版本更新
            checkVersion();
        }
        refreshNumber();
    }


    private void getGroup() {
        UtilTool.Log("token", UtilTool.getToken());
        new GroupPresenter(this).getGroup(mDBRoomMember, mDBRoomManage, mMgr, false, new GroupPresenter.CallBack1() {
            @Override
            public void send(GroupInfo baseInfo) {
                // TODO: 2018/6/11 獲取群聊房間塞入數據庫
                new Handler() {
                    public void handleMessage(Message msg) {
                        getMessageTop();
                    }
                }.sendEmptyMessageDelayed(0, 5 * 1000);
            }

            @Override
            public void error() {
            }

            @Override
            public void finishRefresh() {
            }
        });
    }

    private void getMyImage() {
        UtilTool.Log("token", UtilTool.getToken());
        if (!mMgr.findUser(UtilTool.getTocoId())) {
            IndividualDetailsPresenter personalDetailsPresenter = new IndividualDetailsPresenter(this);
            personalDetailsPresenter.getIndividual(UtilTool.getTocoId(), false, new IndividualDetailsPresenter.CallBack() {
                @Override
                public void send(IndividualInfo.DataBean data) {
                    if (data != null) {
                        List<AuatarListInfo.DataBean> datas = new ArrayList<>();
                        AuatarListInfo.DataBean dataBean = new AuatarListInfo.DataBean();
                        dataBean.setAvatar(data.getAvatar());
                        dataBean.setName(data.getName());
                        dataBean.setRemark(data.getRemark());
                        dataBean.setToco_id(UtilTool.getTocoId());
                        datas.add(dataBean);
                        mMgr.addUserList(datas);
                        EventBus.getDefault().post(new MessageEvent(getString(R.string.xg_touxaing)));
                    }
                }

                @Override
                public void error() {

                }
            });
        }
    }

    private void getMessageTop() {
        UtilTool.Log("token", UtilTool.getToken());
        new GroupPresenter(this).getTopMessage(new GroupPresenter.CallBack5() {
            @Override
            public void send(MessageTopInfo baseInfo) {
                for (MessageTopInfo.DataBean dataBean : baseInfo.getData()) {
                    if (mMgr.findConversation(dataBean.getFor_id())) {
                        mMgr.updateConversationIstop(dataBean.getFor_id(), "true");
                    } else {
                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Date curDate = new Date(System.currentTimeMillis());
                        String time = formatter.format(curDate);
                        ConversationInfo info = new ConversationInfo();
                        info.setTime(time);
                        info.setUser(dataBean.getFor_id());
                        info.setMessage("");
                        info.setIstop("true");
                        info.setCreateTime(UtilTool.createChatCreatTime());
                        if (mDBRoomManage.findRoom(dataBean.getFor_id())) {
                            info.setFriend(mDBRoomManage.findRoomName(dataBean.getFor_id()));
                            info.setChatType(RoomManage.ROOM_TYPE_MULTI);
                        } else if (mMgr.findUser(dataBean.getFor_id())) {
                            info.setFriend(mMgr.queryRemark(dataBean.getFor_id()));
                            info.setChatType(RoomManage.ROOM_TYPE_SINGLE);
                        } else {
                            break;
                        }
                        mMgr.addConversation(info);
                    }
                }
                EventBus.getDefault().post(new MessageEvent(getString(R.string.message_top_change)));
            }
        });
    }

    private void getChatBackGround() {
        UtilTool.Log("token", UtilTool.getToken());
        UtilTool.Log("fengjiantoken", UtilTool.getToken());
        new GroupPresenter(this).getBackgound(new GroupPresenter.CallBack2() {
            @Override
            public void send(String url) {
                String urls = MySharedPreferences.getInstance().getString("backgroundu_url" + UtilTool.getTocoId());
                String filePath = MySharedPreferences.getInstance().getString("backgroundu_file" + UtilTool.getTocoId());
                if (!StringUtils.isEmpty(UtilTool.getPostfix3(urls)) && url.equals(urls)) {
                    File file = new File(Constants.BACKGOUND + filePath);
                    if (!file.exists()) {
                        String key = UtilTool.getUserId() + UtilTool.createtFileName() + ".png";
                        MySharedPreferences.getInstance().setString("backgroundu_file" + UtilTool.getTocoId(), key);
                        downBackGround(url, Constants.BACKGOUND + key);
                        EventBus.getDefault().post(new MessageEvent(getString(R.string.conversation_backgound)));
                    }
                } else {
                    String key = UtilTool.getUserId() + UtilTool.createtFileName() + ".png";
                    MySharedPreferences.getInstance().setString("backgroundu_url" + UtilTool.getTocoId(), url);
                    MySharedPreferences.getInstance().setString("backgroundu_file" + UtilTool.getTocoId(), key);
                    downBackGround(url, Constants.BACKGOUND + key);
                    EventBus.getDefault().post(new MessageEvent(getString(R.string.conversation_backgound)));
                }
            }
        });
    }

    private void downBackGround(final String url, final String filepath) {
        new Thread() {
            @Override
            public void run() {
                try {
                    File file = Glide.with(MainActivity.this)
                            .load(url)
                            .downloadOnly(500, 500)
                            .get();
                    UtilTool.copyFile(file.getAbsolutePath(), filepath);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }

    private void getFriends() {
        new PersonalDetailsPresenter(this).getFriendList(new PersonalDetailsPresenter.CallBack2() {
            @Override
            public void send(List<AuatarListInfo.DataBean> data) {
                mMgr.deleteAllFriend();
                mMgr.addUserList(data);
            }

            @Override
            public void error() {

            }

            @Override
            public void finishRefresh() {

            }
        });
    }

    public void getStateList() {
        mCoinPresenter.getState(new CoinPresenter.CallBack4() {
            @Override
            public void send() {

            }

            @Override
            public void error() {

            }
        });
    }

    //检测版本更新
    private void checkVersion() {
        UpdateLogPresenter updateLogPresenter = new UpdateLogPresenter(this);
        updateLogPresenter.checkVersion(new UpdateLogPresenter.CallBack2() {
            @Override
            public void send(int type) {
            }

            @Override
            public void error() {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //页面销毁删除掉储存的fragment
        Map<Integer, Fragment> map = FragmentFactory.mMainMap;
        FragmentFactory.getInstanes().setNull();
        EventBus.getDefault().unregister(this);
        for (int i : map.keySet()) {
            mSupportFragmentManager.beginTransaction().remove(map.get(i));
            mSupportFragmentManager.beginTransaction().hide(map.get(i));
        }
        MySharedPreferences.getInstance().setBoolean(SharedPreferencesUtil.IS_EXTERNAL,false);
        unregisterReceiver(mReceiver);
    }

    //初始化底部菜单栏
    private void initBottomMenu() {
        for (int i = 0; i < mMainBottomMenu.getChildCount(); i++) {
            final View childAt = mMainBottomMenu.getChildAt(i);
            childAt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int index = mMainBottomMenu.indexOfChild(childAt);
                    if(index==2){
                        Intent intent=new Intent(MainActivity.this,HTMLActivity.class);
                        intent.putExtra("html5Url",Constants.WEB_MALL);
                        startActivity(intent);
                        return;
                    }
                    changeFragment(index);
                    setSelector(index);
                    converstonTop(index);
                }
            });
        }
    }

    private void converstonTop(int index) {
        if (index == 0 && mMainBottomMenu.getChildAt(index).isSelected() && UtilTool.homeClickTwo()) {
            EventBus.getDefault().post(new MessageEvent(getString(R.string.home_msg_click_two)));
        }
    }


    //切换Fragment
    int lastIndex = 0;

    @SuppressLint("RestrictedApi")
    private void changeFragment(int index) {
        try {
            if (mSupportFragmentManager == null) {
                mSupportFragmentManager = getSupportFragmentManager();
            }
            FragmentTransaction ft = mSupportFragmentManager.beginTransaction();
            FragmentFactory fragmentFactory = FragmentFactory.getInstanes();
            Fragment LastFragment = fragmentFactory.createMainFragment(lastIndex);
            Fragment fragment = fragmentFactory.createMainFragment(index);
            if (mSupportFragmentManager.getFragments() == null) {
                if (!fragment.isAdded() && null == mSupportFragmentManager.findFragmentByTag(index + "")) {
                    ft.add(R.id.main_fl, fragment, index + "");
                }
            } else if (!mSupportFragmentManager.getFragments().contains(fragment)) {
                if (!fragment.isAdded() && null == mSupportFragmentManager.findFragmentByTag(index + "")) {
                    ft.add(R.id.main_fl, fragment, index + "");
                }
            }
            if (ft != null) {
                ft.hide(LastFragment);
                ft.show(fragment);
                ft.commitAllowingStateLoss();
                if (mSupportFragmentManager != null)
                    mSupportFragmentManager.executePendingTransactions();
            }
            lastIndex = index;
        }catch (Exception e){
            e.printStackTrace();
        }
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
            try {
                Intent home = new Intent(Intent.ACTION_MAIN);
                home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                home.addCategory(Intent.CATEGORY_HOME);
                startActivity(home);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public interface MyOnTouchListener {
        boolean onTouch(MotionEvent ev);
    }
}
