package com.bclould.tea.ui.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bclould.tea.Presenter.GroupPresenter;
import com.bclould.tea.R;
import com.bclould.tea.history.DBManager;
import com.bclould.tea.history.DBPublicManage;
import com.bclould.tea.history.DBRoomManage;
import com.bclould.tea.history.DBRoomMember;
import com.bclould.tea.model.ConversationInfo;
import com.bclould.tea.model.GroupInfo;
import com.bclould.tea.model.QrRedInfo;
import com.bclould.tea.topperchat.WsConnection;
import com.bclould.tea.ui.activity.AddFriendActivity;
import com.bclould.tea.ui.activity.GrabQRCodeRedActivity;
import com.bclould.tea.ui.activity.GroupListActivity;
import com.bclould.tea.ui.activity.InitialActivity;
import com.bclould.tea.ui.activity.LoginActivity;
import com.bclould.tea.ui.activity.MyFriendActivity;
import com.bclould.tea.ui.activity.PublicActivity;
import com.bclould.tea.ui.activity.RegisterActivity;
import com.bclould.tea.ui.activity.ScanQRCodeActivity;
import com.bclould.tea.ui.activity.SearchActivity;
import com.bclould.tea.ui.adapter.ConversationAdapter;
import com.bclould.tea.ui.widget.MenuListPopWindow2;
import com.bclould.tea.utils.ActivityUtil;
import com.bclould.tea.utils.Constants;
import com.bclould.tea.utils.EventBusUtil;
import com.bclould.tea.utils.MessageEvent;
import com.bclould.tea.utils.StatusBarCompat;
import com.bclould.tea.utils.UtilTool;
import com.bclould.tea.utils.permissions.AuthorizationUserTools;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;

/**
 * Created by GA on 2017/12/12.
 */

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
    @Bind(R.id.status_bar_fix)
    View mStatusBarFix;
    @Bind(R.id.iv_search)
    ImageView mIvSearch;
    @Bind(R.id.btn_login)
    Button mBtnLogin;
    @Bind(R.id.tv_register)
    TextView mTvRegister;
    @Bind(R.id.ll_no_login)
    LinearLayout mLlNoLogin;
    private List<Map<String, Object>> list = new ArrayList<>();
    private List<ConversationInfo> showlist = new ArrayList<>();
    private DBManager mgr;
    private DBRoomMember mDBRoomMember;
    private DBRoomManage mDBRoomManage;
    public static ConversationFragment instance = null;
    private ConversationAdapter mConversationAdapter;
    private int imState = -1;
    private DisplayMetrics mDm;
    private RefreshList mRefreshList;
    private LinearLayoutManager linearLayoutManager;
    private DBPublicManage mDBPublicManage;

    public static ConversationFragment getInstance() {

        if (instance == null) {
            instance = new ConversationFragment();
        }
        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_conversation_list, container, false);
        ButterKnife.bind(this, view);
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
        initInterface();
        return view;
    }

    public void initInterface() {
        if(getActivity()==null)return;
        mStatusBarFix.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, StatusBarCompat.getStateBarHeight(getActivity())));
        mgr = new DBManager(getContext());
        mDBRoomMember = new DBRoomMember(getContext());
        mDBRoomManage = new DBRoomManage(getContext());
        mDBPublicManage = new DBPublicManage(getContext());
        mRefreshList = new RefreshList();
        createFile();

        getPhoneSize();
        initRecyclerView();
        if (!WsConnection.getInstance().getOutConnection()) {
            initRelogin();
            initData();
        } else {
            mTvTitle.setText(getString(R.string.talk));
            mLlNoData.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.GONE);
            mLlNoLogin.setVisibility(View.VISIBLE);
        }
    }

    private void createFile() {
        UtilTool.createNomedia(Constants.BACKGOUND);
        UtilTool.createNomedia(Constants.PUBLICDIR);
        UtilTool.createNomedia(Constants.VIDEO);
        UtilTool.createNomedia(getActivity().getFilesDir().getAbsolutePath() + "/images");
    }

    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(getActivity()==null&&!isAdded())return;
            switch (msg.what) {
                case 0:
                    initRecyclerView();
                    initData();
                    //发送登录失败通知
                    EventBus.getDefault().post(new MessageEvent(getString(R.string.login_error)));
                    break;
                case 1:
                    initRecyclerView();
                    initData();
                    EventBus.getDefault().post(new MessageEvent(getString(R.string.login_succeed)));
                    if (mRlUnunited != null) {
                        mRlUnunited.setVisibility(View.GONE);
                    }
                    break;
                case 2:
                    mConversationAdapter.notifyDataSetChanged();
                    break;
            }
        }
    };

    private void initRelogin() {
        ConnectStateChangeListenerManager.get().registerStateChangeListener(this);
        if ((WsConnection.getInstance().get(getActivity()) != null && WsConnection.getInstance().get(getActivity()).isOpen()) || WsConnection.getInstance().isLogin()) {
            ConnectStateChangeListenerManager.get().notifyListener(ConnectStateChangeListenerManager.CONNECTED);
        } else {
            ConnectStateChangeListenerManager.get().notifyListener(ConnectStateChangeListenerManager.CONNECTING);
        }
    }

    private void onChangeChatState(final int serviceState) {
        ((Activity) getContext()).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
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
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

    }


    @Override
    public void onStateChange(int serviceState) {
        if (serviceState == -1 || mTvTitle == null || mTitleProgress == null||WsConnection.getInstance().getOutConnection()) return;
        onChangeChatState(serviceState);
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
    }


    //获取屏幕高度
    private void getPhoneSize() {
        mDm = new DisplayMetrics();
        if (getActivity() != null)
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(mDm);
    }

    @OnClick({R.id.iv_more, R.id.rl_ununited, R.id.iv_search, R.id.btn_login, R.id.tv_register})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_search:
                if (!WsConnection.getInstance().getOutConnection()) {
                    startActivity(new Intent(getActivity(), SearchActivity.class));
                } else {
                    startActivity(new Intent(getActivity(), InitialActivity.class));
                }
//                startActivity(new Intent(getActivity(), SendRedGroupAlipaylActivity.class));
//                startActivity(new Intent(getActivity(), ConversationBurnListActivity.class));
                break;
            case R.id.iv_more:
                initPopWindow();
                break;
            case R.id.rl_ununited:
                startActivity(new Intent(Settings.ACTION_SETTINGS));
                break;
            case R.id.btn_login:
                startActivity(new Intent(getActivity(), LoginActivity.class));
                break;
            case R.id.tv_register:
                startActivity(new Intent(getActivity(), RegisterActivity.class));
                break;
        }
    }

    //初始化pop
    private void initPopWindow() {
        int widthPixels = mDm.widthPixels;
        List<HashMap> list=new ArrayList<>();
        list.add(MenuListPopWindow2.setHashMapData(getContext(),R.string.start_group,R.mipmap.icon_chat_startgroupchat));
        list.add(MenuListPopWindow2.setHashMapData(getContext(),R.string.my_friend,R.mipmap.icon_chat_myfriend));
        list.add(MenuListPopWindow2.setHashMapData(getContext(),R.string.add_friend,R.mipmap.icon_talk_add));
        list.add(MenuListPopWindow2.setHashMapData(getContext(),R.string.the_pulice,R.mipmap.icon_thepublic1));
        list.add(MenuListPopWindow2.setHashMapData(getContext(),R.string.rich_scan,R.mipmap.icon_news_flicking));
        final MenuListPopWindow2 menuListPopWindow2=new MenuListPopWindow2(getActivity(),(int) (getResources().getDimension(R.dimen.y500)),list);
        if(!ActivityUtil.isActivityOnTop(getContext()))return;
        menuListPopWindow2.showAsDropDown(mXx, (widthPixels - menuListPopWindow2.getPopupWidth()), 0);
        menuListPopWindow2.setListOnClick(new MenuListPopWindow2.ListOnClick() {
            @Override
            public void onclickitem(int position) {
                switch (position) {
                    case 0:
                        if (!WsConnection.getInstance().getOutConnection()) {
                            startActivity(new Intent(getActivity(), GroupListActivity.class));
                        } else {
                            startActivity(new Intent(getActivity(), InitialActivity.class));
                        }
                        menuListPopWindow2.dismiss();
                        break;
                    case 1:
                        if (!WsConnection.getInstance().getOutConnection()) {
                            startActivity(new Intent(getActivity(), MyFriendActivity.class));
                        } else {
                            startActivity(new Intent(getActivity(), InitialActivity.class));
                        }
                        menuListPopWindow2.dismiss();
                        break;
                    case 2:
                        if (!WsConnection.getInstance().getOutConnection()) {
                            startActivity(new Intent(getActivity(), AddFriendActivity.class));
                        } else {
                            startActivity(new Intent(getActivity(), InitialActivity.class));
                        }
                        menuListPopWindow2.dismiss();
                        break;
                    case 3:
                        if (!WsConnection.getInstance().getOutConnection()) {
                            startActivity(new Intent(getActivity(), PublicActivity.class));
                        } else {
                            startActivity(new Intent(getActivity(), InitialActivity.class));
                        }
                        menuListPopWindow2.dismiss();
                        break;
                    case 4:
                        if (!WsConnection.getInstance().getOutConnection()) {
                            if (!AuthorizationUserTools.isCameraCanUse(getActivity()))
                                return;
                            Intent intent = new Intent(getActivity(), ScanQRCodeActivity.class);
                            intent.putExtra("code", 1);
                            startActivityForResult(intent, 0);
                        } else {
                            startActivity(new Intent(getActivity(), InitialActivity.class));
                        }
                        menuListPopWindow2.dismiss();
                        break;
                }
            }
        });
    }


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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        String msg = event.getMsg();
        if (msg.equals(getString(R.string.login_succeed))) {
            getGroup();
            initData();
        } else if (msg.equals(EventBusUtil.oneself_send_msg)) {
            initData();
        } else if (msg.equals(EventBusUtil.send_red_packet_le)) {
            initData();
        } else if (msg.equals(EventBusUtil.dispose_unread_msg)) {
            EventBus.getDefault().post(new MessageEvent(EventBusUtil.refresh_msg_number));
            initData();
        } else if (msg.equals(EventBusUtil.new_friend)) {
            initData();
        } else if (msg.equals(getString(R.string.login_error))) {
            mRlUnunited.setVisibility(View.VISIBLE);
        } else if (msg.equals(getString(R.string.message_top_change))) {
            initData();
        } else if (msg.equals(getString(R.string.change_friend_remark))) {
            initData();
        } else if (msg.equals(EventBusUtil.delete_friend)) {
            initData();
        } else if (msg.equals(EventBusUtil.quit_group)) {
            initData();
        } else if (msg.equals(getString(R.string.refresh_the_interface))) {
            mLlNoLogin.setVisibility(View.GONE);
            mTitleProgress.setVisibility(View.GONE);
            initInterface();
        } else if (msg.equals(getString(R.string.modify_group_name))) {
            initData();
        } else if (msg.equals(EventBusUtil.refresh_group_room)) {
            initData();
        } else if (msg.equals(EventBusUtil.kick_out_success)) {
            initData();
        } else if (msg.equals(getString(R.string.home_msg_click_two))) {
            unNumbertopList();
        } else if (msg.equals(getString(R.string.refresh))) {
            mConversationAdapter.notifyDataSetChanged();
        }
    }

    private void unNumbertopList() {
        A:
        for (int i = 0; i < showlist.size(); i++) {
            if (showlist.get(i).getNumber() > 0) {
                linearLayoutManager.scrollToPositionWithOffset(i, 0);
                break A;
            }
        }
    }

    private void getGroup() {
        new GroupPresenter(getActivity()).getGroup(mDBRoomMember, mDBRoomManage, mgr, false, new GroupPresenter.CallBack1() {
            @Override
            public void send(GroupInfo baseInfo) {
                // TODO: 2018/6/11 獲取群聊房間塞入數據庫
            }

            @Override
            public void error() {
            }

            @Override
            public void finishRefresh() {
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ConnectStateChangeListenerManager.get().unregisterStateChangeListener(this);
        EventBus.getDefault().unregister(this);
    }

    private void initData() {
        if (mRecyclerView != null) {
            mRefreshList.run();
        }
    }

    class RefreshList implements Runnable {
        @Override
        public void run() {
            synchronized (mRecyclerView) {
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
                mHandler.sendEmptyMessage(2);
            }
        }
    }

    private void initRecyclerView() {
        if (mRecyclerView == null) return;
        mConversationAdapter = new ConversationAdapter(getActivity(), getSimpleData(), mgr, mRlTitle, mDBRoomMember, mDBRoomManage,mDBPublicManage);
        linearLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(mConversationAdapter);
    }

    private List<ConversationInfo> getSimpleData() {
        return showlist;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        EventBus.getDefault().unregister(this);
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
