package com.bclould.tocotalk.ui.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bclould.tocotalk.Presenter.CloudMessagePresenter;
import com.bclould.tocotalk.Presenter.PersonalDetailsPresenter;
import com.bclould.tocotalk.R;
import com.bclould.tocotalk.history.DBManager;
import com.bclould.tocotalk.model.AuatarListInfo;
import com.bclould.tocotalk.model.RemarkListInfo;
import com.bclould.tocotalk.model.UserInfo;
import com.bclould.tocotalk.ui.activity.NewFriendActivity;
import com.bclould.tocotalk.ui.activity.SearchActivity;
import com.bclould.tocotalk.ui.adapter.FriendListRVAdapter;
import com.bclould.tocotalk.utils.MessageEvent;
import com.bclould.tocotalk.utils.MySharedPreferences;
import com.bclould.tocotalk.utils.UtilTool;
import com.bclould.tocotalk.xmpp.XmppConnection;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
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
import org.jivesoftware.smack.util.StringUtils;
import org.jxmpp.jid.Jid;
import org.jxmpp.jid.impl.JidCreate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TreeMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.bclould.tocotalk.ui.activity.SystemSetActivity.INFORM;
import static com.bclould.tocotalk.utils.MySharedPreferences.SETTING;

/**
 * Created by GA on 2017/9/19.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
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
    @Bind(R.id.listView)
    ListView mListView;
    @Bind(R.id.refresh_layout)
    SmartRefreshLayout mRefreshLayout;
    @Bind(R.id.scrollView)
    ScrollView mScrollView;

    private TreeMap<String, Boolean> mFromMap = new TreeMap<>();
    private List<UserInfo> mUsers = new ArrayList<>();
    Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    updateData();
                    /*queryUser();
                    mFriendListRVAdapter.notifyDataSetChanged();*/
                    break;
                case 1:
                    String from = (String) msg.obj;
                    mNewFriend += 1;
                    MySharedPreferences.getInstance().setInteger(NEWFRIEND, mNewFriend);
                    mNumber.setText(mNewFriend + "");
                    mNumber.setVisibility(View.VISIBLE);
                    mId = mMgr.addRequest(from, 0);
                    break;
                case 2:
                    String from2 = (String) msg.obj;
                    mNewFriend += 1;
                    MySharedPreferences.getInstance().setInteger(NEWFRIEND, mNewFriend);
                    mNumber.setText(mNewFriend + "");
                    mNumber.setVisibility(View.VISIBLE);
                    int id = mMgr.queryRequest(from2).getId();
                    mMgr.updateRequest(id, 0);
                    break;
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
        setListener();
        initRecylerView();
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
//        updateData();
        if (receiver == null) {
            receiver = new MyReceiver();
            IntentFilter intentFilter = new IntentFilter("com.example.eric_jqm_chat.SearchActivity");
            getActivity().registerReceiver(receiver, intentFilter);
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mScrollView.post(new Runnable() {
            @Override
            public void run() {
                mScrollView.scrollTo(0, 0);
            }
        });
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            mScrollView.post(new Runnable() {
                @Override
                public void run() {
                    mScrollView.scrollTo(0, 0);
                }
            });
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            mScrollView.post(new Runnable() {
                @Override
                public void run() {
                    mScrollView.scrollTo(0, 0);
                }
            });
        }
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
            addFriendListener();
            initData();
        } else if (msg.equals(getString(R.string.new_friend))) {
            initData();
        } else if (msg.equals(getString(R.string.delete_friend))) {
            updateData();
        } else if (msg.equals(getString(R.string.change_friend_remark))) {
            updateData();
        }
    }

    private void updateData() {
        mUsers.clear();
        List<UserInfo> userInfos = mMgr.queryAllUser();
        UserInfo userInfo = null;
        UserInfo userInfo2 = null;
        for (UserInfo info : userInfos) {
            if (info.getUser().equals(UtilTool.getJid())) {
                userInfo = info;
            } else if (info.getUser().isEmpty()) {
                userInfo2 = info;
            }
        }
        userInfos.remove(userInfo);
        if (userInfo2 != null)
            userInfos.remove(userInfo2);
        mUsers.addAll(userInfos);
        mFriendListRVAdapter.notifyDataSetChanged();
    }

    /*private void getImage() {
        mPersonalDetailsPresenter.getFriendImageList(UtilTool.getUser(), new PersonalDetailsPresenter.CallBack2() {
            @Override
            public void send(final AuatarListInfo.DataBean dataBean) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (dataBean != null && !dataBean.getAvatar().isEmpty()) {
                            try {
                                UtilTool.Log("頭像", dataBean.getAvatar());
                                Drawable drawable = Glide.with(getContext())
                                        .load(dataBean.getAvatar())
                                        .into(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                                        .get();
                                BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
                                Bitmap bitmap = bitmapDrawable.getBitmap();
                                UtilTool.saveImages(bitmap, UtilTool.getUser(), getContext(), mMgr);
                            } catch (Exception e) {
                            }
                        } else {
                            mMgr.addUser(UtilTool.getUser(), "");
                        }
                        Message message = new Message();
                        myHandler.sendMessage(message);
                    }
                }).start();
            }
        });
        try {
            if (!mMgr.findUser(Constants.MYUSER)) {
                byte[] myImage = UtilTool.getUserImage(XmppConnection.getInstance().getConnection(), Constants.MYUSER);
                if (myImage != null) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(myImage, 0, myImage.length);
                    UtilTool.saveImages(bitmap, Constants.MYUSER, getContext(), mMgr);
                } else {
                    Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.img_nfriend_headshot1);
                    UtilTool.saveImages(bitmap, Constants.MYUSER, getContext(), mMgr);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

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
                    builder.setNegativeButton(getString(R.string.reject), new DialogInterface.OnClickListener() {
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
                                updateData();
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
                if (from.contains("/")) {
                    from = from.substring(0, from.indexOf("/"));
                }
                String to = presence.getTo().toString();//接收方
                if (presence.getType().equals(Presence.Type.subscribe)) {
                    //发送广播传递发送方的JIDfrom及字符串
                    UtilTool.Log("日志1", mMgr.findUser(from) + "");
                    UtilTool.Log("发", from);
                    if (!mMgr.findUser(from)) {
                        if (!mMgr.findRequest(from)) {
                            acceptAdd = getString(R.string.receive_add_request);
                            Intent intent = new Intent();
                            intent.putExtra("fromName", from);
                            intent.putExtra("acceptAdd", acceptAdd);
                            intent.setAction("com.example.eric_jqm_chat.SearchActivity");
                            getContext().sendBroadcast(intent);
                            Message message = new Message();
                            message.what = 1;
                            message.obj = from;
                            myHandler.sendMessage(message);
                        } else if (mMgr.queryRequest(from).getType() == 1) {
                            acceptAdd = getString(R.string.receive_add_request);
                            Intent intent = new Intent();
                            intent.putExtra("fromName", from);
                            intent.putExtra("acceptAdd", acceptAdd);
                            intent.setAction("com.example.eric_jqm_chat.SearchActivity");
                            getContext().sendBroadcast(intent);
                            Message message = new Message();
                            message.what = 2;
                            message.obj = from;
                            myHandler.sendMessage(message);
                        } else {

                        }
                    }
                } else if (presence.getType().equals(
                        Presence.Type.subscribed)) {
                    //发送广播传递response字符串
                    response = getString(R.string.ta_consent_add_friend);
                    Intent intent = new Intent();
                    intent.putExtra("response", response);
                    intent.setAction("com.example.eric_jqm_chat.SearchActivity");
                    try {
                        Roster.getInstanceFor(XmppConnection.getInstance().getConnection()).createEntry(JidCreate.entityBareFrom(from), null, new String[]{"Friends"});
                        UtilTool.Log("fsdafa", "恭喜，对方同意添加好友！");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    getContext().sendBroadcast(intent);
                } else if (presence.getType().equals(
                        Presence.Type.unsubscribe)) {
                    //发送广播传递response字符串
                    response = getString(R.string.ta_reject_add_friend);
                    try {
                        Roster roster = Roster.getInstanceFor(XmppConnection.getInstance().getConnection());
                        RosterEntry entry = roster.getEntry(JidCreate.entityBareFrom(from));
                        roster.removeEntry(entry);
                        UtilTool.Log("fsdafa", "删除成功！");
                        mMgr.deleteUser(from);
                        updateData();
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
                   /* try {
                        Roster roster = Roster.getInstanceFor(XmppConnection.getInstance().getConnection());
                        RosterEntry entry = roster.getEntry(JidCreate.entityBareFrom(from));
                        roster.removeEntry(entry);
                        UtilTool.Log("fsdafa", "删除成功！");
                        initData();
                    } catch (Exception e) {
                        e.printStackTrace();
                        UtilTool.Log("fsdafa", e.getMessage());
                    }*/
                } else if (presence.getType().equals(
                        Presence.Type.unavailable)) {
                    String user = from.substring(0, from.indexOf("@"));
                    UtilTool.Log("fsdafa", user + "离线");
                    mMgr.updateUser(from, 0);
                    Message message = new Message();
                    message.what = 0;
                    myHandler.sendMessage(message);
                } else {
                    String user = from.substring(0, from.indexOf("@"));
                    UtilTool.Log("fsdafa", user + "上线");
                    mMgr.updateUser(from, 1);
                    Message message = new Message();
                    message.what = 0;
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
        final CloudMessagePresenter cloudMessagePresenter = new CloudMessagePresenter();
        try {
            Collection<RosterGroup> rosterGroups = cloudMessagePresenter.getContact();
            if (rosterGroups != null) {
                UtilTool.Log("分組", rosterGroups.size() + "");
                for (RosterGroup group : rosterGroups) {
                    List<RosterEntry> entries = group.getEntries();
                    for (final RosterEntry rosterEntry : entries) {
                        if (!mMgr.findUser(rosterEntry.getUser())) {
                            String user = rosterEntry.getUser().substring(0, rosterEntry.getUser().indexOf("@"));
                            UtilTool.Log("好友2", user + "   " + rosterEntry.getName());
                            mPersonalDetailsPresenter.getFriendImageList(user, new PersonalDetailsPresenter.CallBack2() {
                                @Override
                                public void send(final AuatarListInfo.DataBean dataBean) {
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (dataBean != null && !dataBean.getAvatar().isEmpty()) {
                                                try {
                                                    UtilTool.Log("頭像", dataBean.getAvatar());
                                                    Drawable drawable = Glide.with(getContext())
                                                            .load(dataBean.getAvatar())
                                                            .into(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                                                            .get();
                                                    BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
                                                    Bitmap bitmap = bitmapDrawable.getBitmap();
                                                    UtilTool.saveImages(bitmap, rosterEntry.getUser(), getContext(), mMgr);
                                                } catch (Exception e) {
                                                }
                                            } else {
                                                mMgr.addUser(rosterEntry.getUser(), "");
                                            }
                                            Message message = new Message();
                                            myHandler.sendMessage(message);
                                        }
                                    }).start();
                                }
                            });
                                   /* byte[] bytes = UtilTool.getUserImage(XmppConnection.getInstance().getConnection(), rosterEntry.getUser());
                                    Bitmap bitmap = null;
                                    if (bytes != null) {
                                        bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                        UtilTool.Log("日志", "请求的头像");
                                    } else {
                                        bitmap = UtilTool.setDefaultimage(getContext());
                                        UtilTool.Log("日志", "默认的头像");
                                    }*/

                        }
                    }
                }
                String user = mMgr.queryAllUserName().toString().replaceAll(" ", "");
                if (StringUtils.isEmpty(user)) return;
                mPersonalDetailsPresenter.getFriendRemark(user.substring(1, user.length() - 1), new PersonalDetailsPresenter.CallBack3() {
                    @Override
                    public void send(List<RemarkListInfo.DataBean> listdata) {
                        if (listdata == null) return;
                        mMgr.updateRemark(listdata);
                        updateData();
                    }
                });
            }

        } catch (Exception e) {
            UtilTool.Log("日志", e.getMessage());
        }
    }

   /* private void queryUser() {
        mUsers.clear();
        List<UserInfo> userInfos = mMgr.queryAllUser();
        UserInfo userInfo = null;
        UserInfo userInfo2 = null;
        for (UserInfo info : userInfos) {
            if (info.getUser().equals(UtilTool.getJid())) {
                userInfo = info;
            } else if (info.getUser().isEmpty()) {
                userInfo2 = info;
            }
        }
        userInfos.remove(userInfo);
        if (userInfo2 != null)
            userInfos.remove(userInfo2);
        mUsers.addAll(userInfos);
    }*/

    private void initRecylerView() {
        mFriendListRVAdapter = new FriendListRVAdapter(getContext(), mUsers, mMgr);
        mListView.setAdapter(mFriendListRVAdapter);
        mListView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    mScrollView.requestDisallowInterceptTouchEvent(false);
                } else {
                    mScrollView.requestDisallowInterceptTouchEvent(true);
                }
                return false;
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }


    @OnClick({R.id.ll_search, R.id.news_friend})
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
            /*case R.id.my_group:

                startActivity(new Intent(getActivity(), GroupListActivity.class));

                XmppConnection.getInstance().joinMultiUserChat(Constants.MYUSER, "群聊六", mMgr);

                break;*/
        }
    }
}
