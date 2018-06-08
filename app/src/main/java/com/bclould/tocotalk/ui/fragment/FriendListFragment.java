package com.bclould.tocotalk.ui.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
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
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bclould.tocotalk.Presenter.CloudMessagePresenter;
import com.bclould.tocotalk.Presenter.PersonalDetailsPresenter;
import com.bclould.tocotalk.R;
import com.bclould.tocotalk.history.DBManager;
import com.bclould.tocotalk.model.AuatarListInfo;
import com.bclould.tocotalk.model.QrRedInfo;
import com.bclould.tocotalk.model.UserInfo;
import com.bclould.tocotalk.ui.activity.AddFriendActivity;
import com.bclould.tocotalk.ui.activity.GrabQRCodeRedActivity;
import com.bclould.tocotalk.ui.activity.GroupListActivity;
import com.bclould.tocotalk.ui.activity.NewFriendActivity;
import com.bclould.tocotalk.ui.activity.PublicshDynamicActivity;
import com.bclould.tocotalk.ui.activity.ScanQRCodeActivity;
import com.bclould.tocotalk.ui.activity.SearchActivity;
import com.bclould.tocotalk.ui.activity.SendQRCodeRedActivity;
import com.bclould.tocotalk.ui.adapter.FriendListRVAdapter;
import com.bclould.tocotalk.utils.Constants;
import com.bclould.tocotalk.utils.MessageEvent;
import com.bclould.tocotalk.utils.MySharedPreferences;
import com.bclould.tocotalk.utils.StringUtils;
import com.bclould.tocotalk.utils.UtilTool;
import com.bclould.tocotalk.xmpp.XmppConnection;
import com.gjiazhe.wavesidebar.WaveSideBar;
import com.google.gson.Gson;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.StanzaTypeFilter;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.roster.RosterGroup;
import org.jxmpp.jid.impl.JidCreate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;
import static com.bclould.tocotalk.ui.activity.SystemSetActivity.INFORM;
import static com.bclould.tocotalk.utils.MySharedPreferences.SETTING;

/**
 * Created by GA on 2017/9/19.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class FriendListFragment extends Fragment {

    public static final String NEWFRIEND = "new_friend";
    public static FriendListFragment instance = null;
    String response, acceptAdd, tocoId, alertSubName;
    @Bind(R.id.iv_more)
    ImageView mIvMore;
    @Bind(R.id.rl_title)
    RelativeLayout mRlTitle;
    @Bind(R.id.xx)
    TextView mXx;
    @Bind(R.id.ll_search)
    LinearLayout mLlSearch;
    @Bind(R.id.iv)
    ImageView mIv;
    @Bind(R.id.number)
    TextView mNumber;
    @Bind(R.id.news_friend)
    RelativeLayout mNewsFriend;
    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @Bind(R.id.side_bar)
    WaveSideBar mSideBar;

    private int QRCODE = 1;
    private DisplayMetrics mDm;
    private int mHeightPixels;
    private ViewGroup mView;
    private PopupWindow mPopupWindow;


    private TreeMap<String, Boolean> mFromMap = new TreeMap<>();
    private List<UserInfo> mUsers = new ArrayList<>();
    Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    updateData();
                    break;
                case 1:
                    mNewFriend = MySharedPreferences.getInstance().getInteger(NEWFRIEND);
                    mNumber.setText(mNewFriend + "");
                    mNumber.setVisibility(View.VISIBLE);
//                    mId = mMgr.addRequest(from, 0);
                    break;
//                case 2:
//                    String from2 = (String) msg.obj;
//                    mNewFriend += 1;
//                    MySharedPreferences.getInstance().setInteger(NEWFRIEND, mNewFriend);
//                    mNumber.setText(mNewFriend + "");
//                    mNumber.setVisibility(View.VISIBLE);
//                    int id = mMgr.queryRequest(from2).getId();
//                    mMgr.updateRequest(id, 0);
//                    break;
            }
        }
    };
    private MyReceiver receiver;
    private FriendListRVAdapter mFriendListRVAdapter;
    private DBManager mMgr;
    private int mId;
    private int mNewFriend;
    private PersonalDetailsPresenter mPersonalDetailsPresenter;

    public static FriendListFragment getInstance() {

        if (instance == null) {

            instance = new FriendListFragment();

        }

        return instance;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_friend_list, container, false);
        ButterKnife.bind(this, view);
        mNewFriend = MySharedPreferences.getInstance().getInteger(NEWFRIEND);
        if (mNewFriend != 0) {
            mNumber.setText(mNewFriend + "");
            mNumber.setVisibility(View.VISIBLE);
        }
        mMgr = new DBManager(getContext());
        mPersonalDetailsPresenter = new PersonalDetailsPresenter(getContext());
        initData();
        setListener();
        initRecylerView();
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
        if (receiver == null) {
            receiver = new MyReceiver();
            IntentFilter intentFilter = new IntentFilter("com.example.eric_jqm_chat.SearchActivity");
            getActivity().registerReceiver(receiver, intentFilter);
        }
        updateData();
        getPhoneSize();
        return view;
    }

    //获取屏幕高度
    private void getPhoneSize() {

        mDm = new DisplayMetrics();

        if (getActivity() != null)
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(mDm);

        mHeightPixels = mDm.heightPixels;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        String msg = event.getMsg();
        if (msg.equals(getString(R.string.login_succeed))) {
            initData();
        } else if (msg.equals(getString(R.string.new_friend))) {
            initData();
        } else if (msg.equals(getString(R.string.delete_friend))) {
            updateData();
        } else if (msg.equals(getString(R.string.change_friend_remark))) {
            updateData();
        }else if(msg.equals(getString(R.string.receive_add_request))){
            sendHandler();
        }
    }

    private void sendHandler() {
        Message message=new Message();
        message.what=1;
        myHandler.sendMessage(message);
    }

    private void updateData() {
        mUsers.clear();
        List<UserInfo> userInfos = mMgr.queryAllUser();
        UserInfo userInfo = null;
        UserInfo userInfo2 = null;
        for (UserInfo info : userInfos) {
            if (UtilTool.getTocoId().equals(info.getUser())) {
                userInfo = info;
            } else if (StringUtils.isEmpty(info.getUser())) {
                userInfo2 = info;
            }
        }
        userInfos.remove(userInfo);
        if (userInfo2 != null)
            userInfos.remove(userInfo2);
        mUsers.addAll(userInfos);
        Collections.sort(mUsers);
        mFriendListRVAdapter.notifyDataSetChanged();
        /*synchronized (mUsers) {
            refreshDataWithRoomIdInBackground();
        }*/
    }

    //广播接收器
    public class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(final Context context, Intent intent) {
            //接收传递的字符串response
            Bundle bundle = intent.getExtras();
            response = bundle.getString("response");
            UtilTool.Log("fsdafa", "广播收到" + response);
            if (response == null) {
                //获取传递的字符串及发送方JID
                acceptAdd = bundle.getString("acceptAdd");
                tocoId = bundle.getString("fromName");
                alertSubName=bundle.getString("alertSubName");
                if (acceptAdd.equals(getString(R.string.receive_add_request))) {
                    //弹出一个对话框，包含同意和拒绝按钮
                    SharedPreferences sp = getContext().getSharedPreferences(SETTING, 0);
                    if (sp.contains(INFORM)) {
                        if (MySharedPreferences.getInstance().getBoolean(INFORM)) {
                            UtilTool.playHint(getContext());
                        }
                    } else {
                        UtilTool.playHint(getContext());
                    }
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle(getString(R.string.add_request));
                    builder.setMessage(getString(R.string.user) + alertSubName + getString(R.string.request_add));
                    builder.setPositiveButton(getString(R.string.consent), new DialogInterface.OnClickListener() {
                        //同意按钮监听事件，发送同意Presence包及添加对方为好友的申请
                        @Override
                        public void onClick(DialogInterface dialog, int arg1) {
                            try {
                                new PersonalDetailsPresenter(context).confirmAddFriend(tocoId, 1, new PersonalDetailsPresenter.CallBack() {
                                    @Override
                                    public void send() {
                                        mMgr.updateRequest(tocoId, 1);
                                        //同意
                                        initData();
                                    }
                                });
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    builder.setNegativeButton(getString(R.string.reject), new DialogInterface.OnClickListener() {
                        //拒绝按钮监听事件，发送拒绝Presence包
                        @Override
                        public void onClick(DialogInterface dialog, int arg1) {
                            try {
                                //拒絕
                                new PersonalDetailsPresenter(context).confirmAddFriend(tocoId, 2, new PersonalDetailsPresenter.CallBack() {
                                    @Override
                                    public void send() {
                                        mMgr.updateRequest(tocoId, 2);
                                        updateData();
                                    }
                                });
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    builder.show();
                }
            }
        }
    }


    private void setListener() {
        mSideBar.bringToFront();
        mSideBar.setOnSelectIndexItemListener(new WaveSideBar.OnSelectIndexItemListener() {
            @Override
            public void onSelectIndexItem(String index) {
                for (int i = 0; i < mUsers.size(); i++) {
                    if (mUsers.get(i).getFirstLetter().equals(index)) {
                        ((LinearLayoutManager) mRecyclerView.getLayoutManager()).scrollToPositionWithOffset(i, 0);
                        return;
                    }
                }
            }
        });
    }

    private void initData() {
        try {
            UtilTool.Log("fengjian","發送獲取好友列表請求");
            mPersonalDetailsPresenter.getFriendList(new PersonalDetailsPresenter.CallBack2() {
                @Override
                public void send(List<AuatarListInfo.DataBean> data) {
                    mMgr.deleteAllFriend();
                    mMgr.addUserList(data);
                    myHandler.sendEmptyMessage(0);
                }
            });
        } catch (Exception e) {
            UtilTool.Log("日志", e.getMessage());
        }

    }

    private void initRecylerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mFriendListRVAdapter = new FriendListRVAdapter(getContext(), mUsers, mMgr,mRlTitle);
        mRecyclerView.setAdapter(mFriendListRVAdapter);
        mRecyclerView.setNestedScrollingEnabled(false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }


    @OnClick({R.id.iv_more, R.id.ll_search, R.id.news_friend,R.id.my_group})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_more:
                initPopWindow();
                break;
            case R.id.ll_search:
                startActivity(new Intent(getActivity(), SearchActivity.class));
                break;
            case R.id.news_friend:
                startActivity(new Intent(getActivity(), NewFriendActivity.class));
                MySharedPreferences.getInstance().setInteger(NEWFRIEND, 0);
                mNumber.setVisibility(View.GONE);
                mNewFriend = 0;
                break;
            case R.id.my_group:
                startActivity(new Intent(getActivity(), GroupListActivity.class));
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
