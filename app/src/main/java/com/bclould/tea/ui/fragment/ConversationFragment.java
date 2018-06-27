package com.bclould.tea.ui.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bclould.tea.R;
import com.bclould.tea.history.DBManager;
import com.bclould.tea.history.DBRoomManage;
import com.bclould.tea.history.DBRoomMember;
import com.bclould.tea.model.ConversationInfo;
import com.bclould.tea.model.QrRedInfo;
import com.bclould.tea.topperchat.WsConnection;
import com.bclould.tea.ui.activity.AddFriendActivity;
import com.bclould.tea.ui.activity.GrabQRCodeRedActivity;
import com.bclould.tea.ui.activity.ScanQRCodeActivity;
import com.bclould.tea.ui.activity.SearchActivity;
import com.bclould.tea.ui.activity.SendQRCodeRedActivity;
import com.bclould.tea.ui.adapter.ConversationAdapter;
import com.bclould.tea.utils.Constants;
import com.bclould.tea.utils.MessageEvent;
import com.bclould.tea.utils.ToastShow;
import com.bclould.tea.utils.UtilTool;
import com.bclould.tea.xmpp.ConnectStateChangeListenerManager;
import com.bclould.tea.xmpp.IConnectStateChangeListener;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;

/**
 * Created by GA on 2017/12/12.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class ConversationFragment extends Fragment implements IConnectStateChangeListener {

    @Bind(R.id.iv_more)
    ImageView mIvMore;
    @Bind(R.id.rl_title)
    RelativeLayout mRlTitle;
    @Bind(R.id.xx)
    TextView mXx;
    @Bind(R.id.iv_warning)
    ImageView mIvWarning;
    @Bind(R.id.rl_ununited)
    RelativeLayout mRlUnunited;
    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @Bind(R.id.iv)
    ImageView mIv;
    @Bind(R.id.ll_no_data)
    LinearLayout mLlNoData;
    @Bind(R.id.tv_title)
    TextView mTvTitle;
    @Bind(R.id.title_progress)
    ProgressBar mTitleProgress;
    private List<Map<String, Object>> list = new ArrayList<>();
    private List<ConversationInfo> showlist = new ArrayList<>();
    private DBManager mgr;
    private DBRoomMember mDBRoomMember;
    private DBRoomManage mDBRoomManage;
    public static ConversationFragment instance = null;
    private ConversationAdapter mConversationAdapter;
    private int imState = -1;
    private DisplayMetrics mDm;
    private int mHeightPixels;
    private ViewGroup mView;
    private PopupWindow mPopupWindow;
    private int QRCODE = 1;

    public static ConversationFragment getInstance() {

        if (instance == null) {
            instance = new ConversationFragment();
        }
        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_conversation_list, container, false);
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
        /*if (receiver == null) {
            receiver = new AddFriendReceiver();
            IntentFilter intentFilter = new IntentFilter("XMPPConnectionListener");
            getActivity().registerReceiver(receiver, intentFilter);
        }*/
        ButterKnife.bind(this, view);
        mgr = new DBManager(getActivity());
        mDBRoomMember=new DBRoomMember(getActivity());
        mDBRoomManage=new DBRoomManage(getActivity());
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getPhoneSize();
        initRelogin();
        initRecyclerView();
        initData();
    }

    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    initRecyclerView();
                    initData();
                    //发送登录失败通知
                    EventBus.getDefault().post(new MessageEvent(getString(R.string.login_error)));
                    ToastShow.showToast2((Activity) getContext(), getString(R.string.toast_network_error));
                    break;
                case 1:
                    initRecyclerView();
                    initData();
                    EventBus.getDefault().post(new MessageEvent(getString(R.string.login_succeed)));
                    if (mRlUnunited != null) {
                        mRlUnunited.setVisibility(View.GONE);
                    }
                    break;

            }
        }
    };

    private void initRelogin() {
        ConnectStateChangeListenerManager.get().registerStateChangeListener(this);
        if ((WsConnection.getInstance().get(getContext())!=null&&WsConnection.getInstance().get(getContext()).isOpen())|| WsConnection.getInstance().isLogin()) {
            ConnectStateChangeListenerManager.get().notifyListener(ConnectStateChangeListenerManager.CONNECTED);
        }else {
            ConnectStateChangeListenerManager.get().notifyListener(ConnectStateChangeListenerManager.CONNECTING);
        }
    }

    private void onChangeChatState(final int serviceState) {
        ((Activity) getContext()).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (serviceState == ConnectStateChangeListenerManager.CONNECTED) {// 已连接
                    mTitleProgress.setVisibility(View.GONE);
                    mTvTitle.setText(getString(R.string.talk));
                } else if (serviceState == ConnectStateChangeListenerManager.CONNECTING) {// 连接中
                    mTitleProgress.setVisibility(View.VISIBLE);
                    mTvTitle.setText(getString(R.string.in_link));
                } else if (serviceState == ConnectStateChangeListenerManager.DISCONNECT) {// 未连接
                    mTitleProgress.setVisibility(View.GONE);
                    mTvTitle.setText(getString(R.string.talk) + getString(R.string.not_link));
                } else if (serviceState == ConnectStateChangeListenerManager.RECEIVING) {//收取中
                    mTitleProgress.setVisibility(View.GONE);
                    mTvTitle.setText(getString(R.string.talk));
                }
            }
        });
    }

    @Override
    public void onStateChange(int serviceState) {
        if (serviceState == -1 || mTvTitle == null) return;
        if (imState == serviceState) {
            return;
        } else {
            imState = serviceState;
        }
        if (serviceState == ConnectStateChangeListenerManager.CONNECTED) {// 已连接
            mHandler.sendEmptyMessage(1);
        } else if (serviceState == ConnectStateChangeListenerManager.CONNECTING) {// 连接中

        } else if (serviceState == ConnectStateChangeListenerManager.DISCONNECT) {// 未连接
            mHandler.sendEmptyMessage(0);
        } else if (serviceState == ConnectStateChangeListenerManager.RECEIVING) {//收取中
            mHandler.sendEmptyMessage(1);
        }
        onChangeChatState(serviceState);
    }


    //获取屏幕高度
    private void getPhoneSize() {

        mDm = new DisplayMetrics();

        if (getActivity() != null)
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(mDm);

        mHeightPixels = mDm.heightPixels;
    }

    @OnClick({R.id.iv_more, R.id.rl_ununited, R.id.iv_search})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_search:
                startActivity(new Intent(getActivity(), SearchActivity.class));
                break;
            case R.id.iv_more:
                initPopWindow();
                break;
            case R.id.rl_ununited:
                startActivity(new Intent(Settings.ACTION_SETTINGS));
                break;
        }
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
                    }

                }
            });
        }
    }

    //广播接收器
    public class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            boolean type = intent.getBooleanExtra("type", false);
            if (mRlUnunited != null) {
                if (type) {
                    mRlUnunited.setVisibility(View.GONE);
                } else {
                    mRlUnunited.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        String msg = event.getMsg();
        if (msg.equals(getString(R.string.login_succeed))) {
            initData();
        } else if (msg.equals(getString(R.string.oneself_send_msg))) {
            initData();
        } else if (msg.equals(getString(R.string.send_red_packet_le))) {
            initData();
        } else if (msg.equals(getString(R.string.dispose_unread_msg))) {
            initData();
        } else if (msg.equals(getString(R.string.new_friend))) {
            initData();
        } else if (msg.equals(getString(R.string.login_error))) {
            mRlUnunited.setVisibility(View.VISIBLE);
        } else if (msg.equals(getString(R.string.message_top_change))) {
            initData();
        } else if (msg.equals(getString(R.string.change_friend_remark))) {
            initData();
        } else if (msg.equals(getString(R.string.delete_friend))) {
            initData();
        }else if(msg.equals(getString(R.string.quit_group))){
            initData();
        }else if(msg.equals(getString(R.string.refresh_the_interface))){
            initData();
        }else if(msg.equals(getString(R.string.modify_group_name))){
            initData();
        }else if(msg.equals(getString(R.string.refresh_group_room))){
            initData();
        }else if(msg.equals(getString(R.string.kick_out_success))){
            initData();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        ConnectStateChangeListenerManager.get().unregisterStateChangeListener(this);
    }

    private void initData() {
        if (mRecyclerView != null) {
            List<ConversationInfo> conversationInfos = mgr.queryConversation();
            if (conversationInfos.size() == 0) {
                mLlNoData.setVisibility(View.VISIBLE);
                mRecyclerView.setVisibility(View.GONE);
            } else {
                mLlNoData.setVisibility(View.GONE);
                mRecyclerView.setVisibility(View.VISIBLE);
            }
            showlist.removeAll(showlist);
            showlist.addAll(conversationInfos);
            sort();
            mConversationAdapter.notifyDataSetChanged();
        }
    }

    private void initRecyclerView() {
        mConversationAdapter = new ConversationAdapter( getActivity(), getSimpleData(), mgr, mRlTitle, mDBRoomMember,mDBRoomManage);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mConversationAdapter);
    }

    private List<ConversationInfo> getSimpleData() {
        return showlist;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    private void sort() {
        //将showlist按时间排成倒序
        Collections.sort(showlist, new Comparator<ConversationInfo>() {
            @Override
            public int compare(ConversationInfo conversationInfo, ConversationInfo conversationInfo2) {
                String at = conversationInfo.getTime();
                String bt = conversationInfo2.getTime();
                String atop = conversationInfo.getIstop();
                String btop = conversationInfo2.getIstop();
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                try {
                    if ("true".equals(btop) && !"true".equals(atop)) {
                        return 1;
                    } else if (!"true".equals(btop) && "true".equals(atop)) {
                        return -1;
                    } else if (formatter.parse(bt).getTime() > formatter.parse(at).getTime()) {
                        return 1;
                    } else if (formatter.parse(bt).getTime() < formatter.parse(at).getTime()) {
                        return -1;
                    } else {
                        return 0;
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return 0;
            }
        });
    }
}
