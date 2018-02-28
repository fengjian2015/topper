package com.bclould.tocotalk.ui.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bclould.tocotalk.Presenter.CloudMessagePresenter;
import com.bclould.tocotalk.R;
import com.bclould.tocotalk.history.DBManager;
import com.bclould.tocotalk.model.UserInfo;
import com.bclould.tocotalk.ui.activity.GroupListActivity;
import com.bclould.tocotalk.ui.activity.NewFriendActivity;
import com.bclould.tocotalk.ui.activity.SearchActivity;
import com.bclould.tocotalk.ui.adapter.FriendListVPAdapter;
import com.bclould.tocotalk.utils.Constants;
import com.bclould.tocotalk.utils.FullyLinearLayoutManager;
import com.bclould.tocotalk.utils.MessageEvent;
import com.bclould.tocotalk.utils.MySharedPreferences;
import com.bclould.tocotalk.utils.UtilTool;
import com.bclould.tocotalk.xmpp.XmppConnection;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
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
import org.jivesoftware.smack.roster.RosterListener;
import org.jxmpp.jid.Jid;
import org.jxmpp.jid.impl.JidCreate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by GA on 2017/9/19.
 */

public class FriendListFragment extends Fragment {

    private static final String NEWFRIEND = "new_friend";
    public static FriendListFragment instance = null;
    String response, acceptAdd, alertName, alertSubName;
    @Bind(R.id.ll_search)
    LinearLayout mLlSearch;
    @Bind(R.id.iv)
    ImageView mIv;
    @Bind(R.id.number)
    TextView mNumber;
    @Bind(R.id.news_friend)
    RelativeLayout mNewsFriend;
    @Bind(R.id.my_group)
    LinearLayout mMyGroup;
    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @Bind(R.id.refresh_layout)
    SmartRefreshLayout mRefreshLayout;
    private Map<String, Boolean> mFromMap = new HashMap<>();
    private List<UserInfo> mUsers = new ArrayList<>();
    Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mFriendListVPAdapter.notifyDataSetChanged();
        }
    };
    private MyReceiver receiver;
    private FriendListVPAdapter mFriendListVPAdapter;
    private DBManager mMgr;
    private int mId;
    private int mNewFriend;

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
        queryUser();
        initRecylerView();
        setListener();
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
        if (receiver == null) {
            receiver = new MyReceiver();
            IntentFilter intentFilter = new IntentFilter("com.example.eric_jqm_chat.SearchActivity");
            getActivity().registerReceiver(receiver, intentFilter);
        }
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        String msg = event.getMsg();
        if (msg.equals("登录成功")) {
            addFriendListener();
            initData();
            getMyImage();
            rosterListener();
        } else if (msg.equals("新的好友")) {
            initData();
        }
    }

    private void getMyImage() {
        try {
            if (!mMgr.findUser(Constants.MYUSER)) {
                byte[] myImage = UtilTool.getUserImage(XmppConnection.getInstance().getConnection(), Constants.MYUSER);
                if (myImage != null) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(myImage, 0, myImage.length);
                    UtilTool.saveImages(bitmap, Constants.MYUSER, getContext(), mMgr);
                } else {
                    Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.img_nfriend_headshot1);
                    /*ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] bytes = baos.toByteArray();
                    XmppConnection.getInstance().changeImage(bytes);*/
                    UtilTool.saveImages(bitmap, Constants.MYUSER, getContext(), mMgr);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void rosterListener() {
        Roster roster = Roster.getInstanceFor(XmppConnection.getInstance().getConnection());
        roster.addRosterListener(new RosterListener() {
            @Override
            public void entriesAdded(Collection<Jid> addresses) {
            }

            @Override
            public void entriesUpdated(Collection<Jid> addresses) {
            }

            @Override
            public void entriesDeleted(Collection<Jid> addresses) {
            }

            @Override
            public void presenceChanged(Presence presence) {
                String from = presence.getFrom().toString();
                String user = null;
                if (from.contains("/")) {
                    user = from.substring(0, from.indexOf("/"));
                } else {
                    user = from;
                }
                byte[] bytes = UtilTool.getUserImage(XmppConnection.getInstance().getConnection(), user);
                Bitmap bitmap = null;
                if (bytes != null)
                    bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                String path = UtilTool.saveImages(bitmap, user, getContext(), mMgr);
            }
        });

    }

    //广播接收器
    public class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //接收传递的字符串response
            Bundle bundle = intent.getExtras();
            response = bundle.getString("response");
            UtilTool.Log("fsdafa", "广播收到" + response);
//            text_response.setText(response);
            if (response == null) {
                //获取传递的字符串及发送方JID
                acceptAdd = bundle.getString("acceptAdd");
                alertName = bundle.getString("fromName");
                if (alertName != null) {
                    //裁剪JID得到对方用户名
                    alertSubName = alertName.substring(0, alertName.indexOf("@"));
                }
                if (acceptAdd.equals("收到添加请求！")) {
                    //弹出一个对话框，包含同意和拒绝按钮
                    UtilTool.playHint(getContext());
                    mNewFriend += 1;
                    MySharedPreferences.getInstance().setInteger(NEWFRIEND, mNewFriend);
                    mNumber.setText(mNewFriend + "");
                    mNumber.setVisibility(View.VISIBLE);
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("添加好友请求");
                    builder.setMessage("用户" + alertSubName + "请求添加你为好友");
                    builder.setPositiveButton("同意", new DialogInterface.OnClickListener() {
                        //同意按钮监听事件，发送同意Presence包及添加对方为好友的申请
                        @Override
                        public void onClick(DialogInterface dialog, int arg1) {
                            Presence presenceRes = new Presence(Presence.Type.subscribed);
                            presenceRes.setTo(alertName);
                            try {
                                XmppConnection.getInstance().getConnection().sendStanza(presenceRes);
                                Roster.getInstanceFor(XmppConnection.getInstance().getConnection()).createEntry(JidCreate.entityBareFrom(alertName), null, new String[]{"Friends"});
                                initData();
                                mMgr.updateRequest(mId, 1);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    });
                    builder.setNegativeButton("拒绝", new DialogInterface.OnClickListener() {
                        //拒绝按钮监听事件，发送拒绝Presence包
                        @Override
                        public void onClick(DialogInterface dialog, int arg1) {
                            Presence presenceRes = new Presence(Presence.Type.unsubscribe);
                            presenceRes.setTo(alertName);
                            try {
                                XmppConnection.getInstance().getConnection().sendStanza(presenceRes);
                                Roster roster = Roster.getInstanceFor(XmppConnection.getInstance().getConnection());
                                RosterEntry entry = roster.getEntry(JidCreate.entityBareFrom(alertName));
                                roster.removeEntry(entry);
                                initData();
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

    /**
     * 添加好友请求信息监听
     */
    public void addFriendListener() {
        //条件过滤器

        AndFilter filter = new AndFilter(new StanzaTypeFilter(Presence.class));
        //添加监听
        if (XmppConnection.getInstance().getConnection().isConnected())
            XmppConnection.getInstance().getConnection().addAsyncStanzaListener(packetListener, filter);


    }

    StanzaListener packetListener = new StanzaListener() {
        @Override
        public void processStanza(Stanza packet) throws SmackException.NotConnectedException, InterruptedException {
            if (packet instanceof Presence) {
                Presence presence = (Presence) packet;
                String from = presence.getFrom().toString();//发送方
                String to = presence.getTo().toString();//接收方
                if (presence.getType().equals(Presence.Type.subscribe)) {
                    //发送广播传递发送方的JIDfrom及字符串
                    UtilTool.Log("日志1", mMgr.findUser(from) + "");
                    UtilTool.Log("日志1", mMgr.findRequest(from) + "");
                    if (!mMgr.findUser(from)) {
                        acceptAdd = "收到添加请求！";
                        Intent intent = new Intent();
                        intent.putExtra("fromName", from);
                        intent.putExtra("acceptAdd", acceptAdd);
                        intent.setAction("com.example.eric_jqm_chat.SearchActivity");
                        getContext().sendBroadcast(intent);
                        mId = mMgr.addRequest(from, 0);
                    }
                } else if (presence.getType().equals(
                        Presence.Type.subscribed)) {
                    //发送广播传递response字符串
                    response = "恭喜，对方同意添加好友！";
                    Intent intent = new Intent();
                    intent.putExtra("response", response);
                    intent.setAction("com.example.eric_jqm_chat.SearchActivity");
                    try {
                        Roster.getInstanceFor(XmppConnection.getInstance().getConnection()).createEntry(JidCreate.entityBareFrom(from), null, new String[]{"Friends"});
                        UtilTool.Log("fsdafa", "恭喜，对方同意添加好友！");
                        initData();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    getContext().sendBroadcast(intent);
                } else if (presence.getType().equals(
                        Presence.Type.unsubscribe)) {
                    //发送广播传递response字符串
                    response = "抱歉，对方拒绝添加好友，将你从好友列表移除！";
                    try {
                        Roster roster = Roster.getInstanceFor(XmppConnection.getInstance().getConnection());
                        RosterEntry entry = roster.getEntry(JidCreate.entityBareFrom(from));
                        roster.removeEntry(entry);
                        UtilTool.Log("fsdafa", "删除成功！");
                        initData();
                    } catch (Exception e) {
                        e.printStackTrace();
                        UtilTool.Log("fsdafa", e.getMessage());
                    }
                    Intent intent = new Intent();
                    intent.putExtra("response", response);
                    intent.setAction("com.example.eric_jqm_chat.SearchActivity");
                    getContext().sendBroadcast(intent);
                } else if (presence.getType().equals(
                        Presence.Type.unsubscribed)) {
                    try {
                        Roster roster = Roster.getInstanceFor(XmppConnection.getInstance().getConnection());
                        RosterEntry entry = roster.getEntry(JidCreate.entityBareFrom(from));
                        roster.removeEntry(entry);
                        UtilTool.Log("fsdafa", "删除成功！");
                        initData();
                    } catch (Exception e) {
                        e.printStackTrace();
                        UtilTool.Log("fsdafa", e.getMessage());
                    }
                } else if (presence.getType().equals(
                        Presence.Type.unavailable)) {
                    String user = from.substring(0, from.indexOf("@"));
                    UtilTool.Log("fsdafa", user + "离线");
                    mFromMap.put(user, false);
                    Message message = new Message();
                    myHandler.sendMessage(message);
                } else {
                    String user = from.substring(0, from.indexOf("@"));
                    UtilTool.Log("fsdafa", user + "上线");
                    mFromMap.put(user, true);
                    Message message = new Message();
                    myHandler.sendMessage(message);
                }
            }
        }
    };

    private void setListener() {
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                refreshlayout.finishRefresh(2000);
                initData();
            }
        });
    }

    private void initData() {
        mUsers.removeAll(mUsers);
        final CloudMessagePresenter cloudMessagePresenter = new CloudMessagePresenter();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Collection<RosterGroup> rosterGroups = cloudMessagePresenter.getContact();
                    if (rosterGroups != null) {
                        for (RosterGroup group : rosterGroups) {
                            List<RosterEntry> entries = group.getEntries();
                            for (RosterEntry rosterEntry : entries) {
                                if (!mMgr.findUser(rosterEntry.getUser())) {

                                    byte[] bytes = UtilTool.getUserImage(XmppConnection.getInstance().getConnection(), rosterEntry.getUser());
                                    Bitmap bitmap = null;
                                    if (bytes != null) {
                                        bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                        UtilTool.Log("日志", "请求的头像");
                                    } else {
                                        bitmap = UtilTool.setDefaultimage(getContext());
                                        UtilTool.Log("日志", "默认的头像");
                                    }
                                    String path = UtilTool.saveImages(bitmap, rosterEntry.getUser(), getContext(), mMgr);
                                    UserInfo userInfo = new UserInfo();
                                    userInfo.setUser(rosterEntry.getUser());
                                    userInfo.setPath(path);
                                    mUsers.add(userInfo);

                                } else {
                                    List<UserInfo> userInfos = mMgr.queryUser(rosterEntry.getUser());
                                    UserInfo userInfo = userInfos.get(0);
                                    mUsers.add(userInfo);
                                }
                            }
                        }
                        Message message = new Message();
                        myHandler.sendMessage(message);
                    }

                } catch (Exception e) {
                    UtilTool.Log("日志", e.getMessage());
                }
            }
        }).start();
    }

    private void queryUser() {
        mUsers.removeAll(mUsers);
        List<UserInfo> userInfos = mMgr.queryAllUser();
        for (UserInfo info : userInfos) {
            String name = info.getUser().substring(0, info.getUser().indexOf("@"));
            mFromMap.put(name, false);
        }
        mUsers.addAll(userInfos);
    }

    private void initRecylerView() {
//        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setLayoutManager(new FullyLinearLayoutManager(getContext()));
        mFriendListVPAdapter = new FriendListVPAdapter(getContext(), mUsers, mFromMap, mMgr);
        mRecyclerView.setAdapter(mFriendListVPAdapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }


    @OnClick({R.id.ll_search, R.id.news_friend, R.id.my_group})
    public void onViewClicked(View view) {
        switch (view.getId()) {
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

                XmppConnection.getInstance().joinMultiUserChat(Constants.MYUSER, "群聊六", mMgr);

                break;
        }
    }
}
