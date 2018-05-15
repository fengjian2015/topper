package com.bclould.tocotalk.ui.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bclould.tocotalk.R;
import com.bclould.tocotalk.base.MyApp;
import com.bclould.tocotalk.model.QrRedInfo;
import com.bclould.tocotalk.service.IMCoreService;
import com.bclould.tocotalk.ui.activity.AddFriendActivity;
import com.bclould.tocotalk.ui.activity.GrabQRCodeRedActivity;
import com.bclould.tocotalk.ui.activity.PublicshDynamicActivity;
import com.bclould.tocotalk.ui.activity.ScanQRCodeActivity;
import com.bclould.tocotalk.ui.activity.SendQRCodeRedActivity;
import com.bclould.tocotalk.ui.adapter.CloudMessageVPAdapter;
import com.bclould.tocotalk.ui.widget.LoadingProgressDialog;
import com.bclould.tocotalk.utils.Constants;
import com.bclould.tocotalk.utils.MessageEvent;
import com.bclould.tocotalk.utils.ToastShow;
import com.bclould.tocotalk.utils.UtilTool;
import com.bclould.tocotalk.xmpp.ConnectStateChangeListenerManager;
import com.bclould.tocotalk.xmpp.IConnectStateChangeListener;
import com.bclould.tocotalk.xmpp.XmppConnection;
import com.bclould.tocotalk.xmpp.XmppListener;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Timer;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;

/**
 * Created by GA on 2017/9/19.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class CloudMessageFragment extends Fragment implements IConnectStateChangeListener {


    public static CloudMessageFragment instance = null;
    @Bind(R.id.status_bar_fix)
    View mStatusBarFix;
    @Bind(R.id.xx)
    TextView mXx;
    @Bind(R.id.yunxin_xx)
    TextView mYunxinXx;
    @Bind(R.id.haoyou_xx)
    TextView mHaoyouXx;
    @Bind(R.id.cloud_circle_menu)
    LinearLayout mCloudCircleMenu;
    @Bind(R.id.cloud_circle_add)
    RelativeLayout mCloudCircleAdd;
    @Bind(R.id.cloud_circle_vp)
    ViewPager mCloudCircleVp;
    @Bind(R.id.ll_login)
    LinearLayout mLlLogin;
    @Bind(R.id.iv_anim)
    ImageView mIvAnim;
    @Bind(R.id.ll_chat)
    LinearLayout mLlChat;
    @Bind(R.id.ll_friend)
    LinearLayout mLlFriend;
    private DisplayMetrics mDm;
    private int mHeightPixels;
    private ViewGroup mView;
    private PopupWindow mPopupWindow;
    private int QRCODE = 1;
    private LoadingProgressDialog mProgressDialog;
    private Timer tExit;
    private AnimationDrawable mAnim;
    private boolean isLogin = false;
    private int imState = -1;//用記錄斷狀態，避免狀態重複修改

    public static CloudMessageFragment getInstance() {

        if (instance == null) {

            instance = new CloudMessageFragment();

        }

        return instance;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_cloud_message, container, false);
        ButterKnife.bind(this, view);
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initInterface();
    }

    //初始化界面
    private void initInterface() {
        initRelogin();
    }

    private void initRelogin() {
        ConnectStateChangeListenerManager.get().registerStateChangeListener(this);
        if (ConnectStateChangeListenerManager.get().getCurrentState() != ConnectStateChangeListenerManager.CONNECTED) {
            ConnectStateChangeListenerManager.get().notifyListener(ConnectStateChangeListenerManager.CONNECTING);
        }
        Intent intent = new Intent(getContext(), IMCoreService.class);
        if (XmppConnection.isServiceWork(getContext(), "com.bclould.tocotalk.service.IMCoreService")) {
            XmppConnection.stopAllIMCoreService(getContext());
            getContext().stopService(intent);
        }
        getContext().startService(intent);
    }


    //初始化ViewPager
    private void initViewPager() {

        CloudMessageVPAdapter cloudMessageVPAdapter = new CloudMessageVPAdapter(getChildFragmentManager(), this);

        mCloudCircleVp.setAdapter(cloudMessageVPAdapter);

        mCloudCircleVp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                setSelector(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    //显示登录中Dialog
    public void showDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = LoadingProgressDialog.createDialog(getContext());
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setMessage(getString(R.string.login_underway));
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        String msg = event.getMsg();
        if (msg.equals(getString(R.string.login_error))) {
            mHandler.sendEmptyMessage(2);
        }
    }

    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    if (mLlChat != null && mLlFriend != null && mCloudCircleAdd != null) {
                        mLlChat.setClickable(true);
                        mLlFriend.setClickable(true);
                        mCloudCircleAdd.setClickable(true);
                    }
                    XmppListener.xmppListener = null;
                    getPhoneSize();
                    setSelector(0);
                    mCloudCircleVp.setCurrentItem(0);
                    initTopMenu();
                    initViewPager();
                    //发送登录失败通知
                    EventBus.getDefault().post(new MessageEvent(getString(R.string.login_error)));
                    if (mAnim != null)
                        mAnim.stop();
                    mLlLogin.setVisibility(View.GONE);
                    ToastShow.showToast2((Activity) getContext(), getString(R.string.toast_network_error));
                    break;
                case 1:
                    if (mLlChat != null && mLlFriend != null && mCloudCircleAdd != null) {
                        mLlChat.setClickable(true);
                        mLlFriend.setClickable(true);
                        mCloudCircleAdd.setClickable(true);
                    }
                    XmppListener.get(getContext());
                    getPhoneSize();
                    initTopMenu();
                    initViewPager();
                    setSelector(0);
                    mCloudCircleVp.setCurrentItem(0);
                    EventBus.getDefault().post(new MessageEvent(getString(R.string.login_succeed)));
                    if (mAnim != null)
                        mAnim.stop();
                    mLlLogin.setVisibility(View.GONE);
                    isLogin = true;
                    MyApp.getInstance().isLogin = true;
                    break;
                case 2:
                    if (mLlChat != null && mLlFriend != null && mCloudCircleAdd != null) {
                        mLlChat.setClickable(true);
                        mLlFriend.setClickable(true);
                        mCloudCircleAdd.setClickable(true);
                    }
                    getPhoneSize();
                    setSelector(0);
                    mCloudCircleVp.setCurrentItem(0);
                    initTopMenu();
                    initViewPager();
                    //发送登录失败通知
                    Intent intent = new Intent();
                    intent.setAction("XMPPConnectionListener");
                    intent.putExtra("type", false);
                    getContext().sendBroadcast(intent);
                    if (mAnim != null)
                        mAnim.stop();
                    mLlLogin.setVisibility(View.GONE);
                    Toast.makeText(getContext(), getString(R.string.toast_network_error), Toast.LENGTH_SHORT).show();
                    break;
                case 3:
                    XmppListener.xmppListener = null;
                    break;
            }
        }
    };

    @Override
    public void onStateChange(int serviceState) {
        if (serviceState == -1 || mCloudCircleVp == null) return;
        if (imState == serviceState) {
            return;
        } else {
            imState = serviceState;
        }
        if (serviceState == ConnectStateChangeListenerManager.CONNECTED) {// 已连接
            mHandler.sendEmptyMessage(1);
        } else if (serviceState == ConnectStateChangeListenerManager.CONNECTING) {// 连接中
            mLlLogin.setVisibility(View.VISIBLE);
            if (mAnim != null)
                mAnim.stop();
            mAnim = (AnimationDrawable) mIvAnim.getBackground();
            mAnim.start();
            mHandler.sendEmptyMessage(3);
        } else if (serviceState == ConnectStateChangeListenerManager.DISCONNECT) {// 未连接
            mHandler.sendEmptyMessage(0);
        } else if (serviceState == ConnectStateChangeListenerManager.RECEIVING) {//收取中
            mHandler.sendEmptyMessage(1);
        }
    }

    //初始化顶部菜单栏
    private void initTopMenu() {

        if (mCloudCircleMenu != null) {
            for (int i = 0; i < mCloudCircleMenu.getChildCount(); i++) {

                final View childAt = mCloudCircleMenu.getChildAt(i);

                childAt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        int index = mCloudCircleMenu.indexOfChild(childAt);

                        setSelector(index);

                        mCloudCircleVp.setCurrentItem(index);

                    }
                });
            }
        }
    }

    //菜单选项选中处理
    private void setSelector(int index) {

        if (mCloudCircleMenu != null) {
            for (int i = 0; i < mCloudCircleMenu.getChildCount(); i++) {

                if (i == index) {

                    mCloudCircleMenu.getChildAt(i).setSelected(true);

                    switch (index) {

                        case 0:

                            mYunxinXx.setVisibility(View.VISIBLE);

                            mHaoyouXx.setVisibility(View.INVISIBLE);

                            break;
                        case 1:

                            mYunxinXx.setVisibility(View.INVISIBLE);

                            mHaoyouXx.setVisibility(View.VISIBLE);

                            break;
                    }

                } else {

                    mCloudCircleMenu.getChildAt(i).setSelected(false);

                }
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        EventBus.getDefault().unregister(this);
    }

    @OnClick(R.id.cloud_circle_add)
    public void onViewClicked() {
        if (isLogin) {
            initPopWindow();
        }
    }

    //获取屏幕高度
    private void getPhoneSize() {

        mDm = new DisplayMetrics();

        if (getActivity() != null)
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(mDm);

        mHeightPixels = mDm.heightPixels;
    }

    //初始化pop
    private void initPopWindow() {

        int widthPixels = mDm.widthPixels;

        mView = (ViewGroup) LayoutInflater.from(getContext()).inflate(R.layout.pop_cloud_message, null);

        mPopupWindow = new PopupWindow(mView, widthPixels / 100 * 35, mHeightPixels / 4, true);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
        // 设置背景颜色变暗
        WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
        lp.alpha = 0.9f;
        getActivity().getWindow().setAttributes(lp);
        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {

            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
                lp.alpha = 1f;
                getActivity().getWindow().setAttributes(lp);
            }
        });
        mPopupWindow.showAsDropDown(mXx, (widthPixels - widthPixels / 100 * 35 - 20), 0);

        popChildClick();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            String result = data.getStringExtra("result");
            if (!result.isEmpty() && result.contains(Constants.REDPACKAGE)) {
                String base64 = result.substring(Constants.REDPACKAGE.length(), result.length());
                byte[] bytes = Base64.decode(base64, Base64.DEFAULT);
                String jsonresult = new String(bytes);
                Gson gson = new Gson();
                QrRedInfo qrRedInfo = gson.fromJson(jsonresult, QrRedInfo.class);
                UtilTool.Log("日志", qrRedInfo.getRedID());
                Intent intent = new Intent(getActivity(), GrabQRCodeRedActivity.class);
                intent.putExtra("id", qrRedInfo.getRedID());
                intent.putExtra("type", true);
                startActivity(intent);
            }
        }
    }

    //给pop子控件设置点击事件
    private void popChildClick() {

        int childCount = mView.getChildCount();

        for (int i = 0; i < childCount; i++) {

            final View childAt = mView.getChildAt(i);

            childAt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    int index = mView.indexOfChild(childAt);

                    switch (index) {
                        case 0:
                            Intent intent = new Intent(getActivity(), ScanQRCodeActivity.class);
                            intent.putExtra("code", QRCODE);
                            startActivityForResult(intent, 0);
                            mPopupWindow.dismiss();
                            break;
                        case 1:
                            startActivity(new Intent(getActivity(), SendQRCodeRedActivity.class));
                            mPopupWindow.dismiss();
                            break;
                        case 2:
                            startActivity(new Intent(getActivity(), AddFriendActivity.class));
                            mPopupWindow.dismiss();
                            break;
                        case 3:
                            startActivity(new Intent(getActivity(), PublicshDynamicActivity.class));
                            mPopupWindow.dismiss();
                            break;
                    }

                }
            });
        }
    }
}
