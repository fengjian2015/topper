package com.bclould.tea.ui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.bclould.tea.Presenter.PersonalDetailsPresenter;
import com.bclould.tea.R;
import com.bclould.tea.base.BaseActivity;
import com.bclould.tea.history.DBManager;
import com.bclould.tea.model.AuatarListInfo;
import com.bclould.tea.model.MessageInfo;
import com.bclould.tea.model.NewFriendInfo;
import com.bclould.tea.model.QrRedInfo;
import com.bclould.tea.model.UserInfo;
import com.bclould.tea.ui.adapter.FriendListRVAdapter;
import com.bclould.tea.ui.fragment.FriendListFragment;
import com.bclould.tea.ui.widget.DeleteCacheDialog;
import com.bclould.tea.ui.widget.MenuListPopWindow;
import com.bclould.tea.utils.ActivityUtil;
import com.bclould.tea.utils.Constants;
import com.bclould.tea.utils.EventBusUtil;
import com.bclould.tea.utils.MessageEvent;
import com.bclould.tea.utils.MySharedPreferences;
import com.bclould.tea.utils.StringUtils;
import com.bclould.tea.utils.UtilTool;
import com.bclould.tea.utils.permissions.AuthorizationUserTools;
import com.gjiazhe.wavesidebar.WaveSideBar;
import com.google.gson.Gson;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.bclould.tea.ui.adapter.ChatAdapter.TO_CARD_MSG;

/**
 * Created by GA on 2018/7/31.
 */
public class MyFriendActivity extends BaseActivity implements FriendListRVAdapter.OnclickListener {

    public static final String NEWFRIEND = "new_friend";
    public static FriendListFragment instance = null;
    @Bind(R.id.rl_title)
    RelativeLayout mRlTitle;
    @Bind(R.id.xx)
    TextView mXx;
    @Bind(R.id.iv)
    ImageView mIv;
    @Bind(R.id.number)
    TextView mNumber;
    @Bind(R.id.news_friend)
    RelativeLayout mNewsFriend;
    @Bind(R.id.iv1)
    ImageView mIv1;
    @Bind(R.id.my_group)
    RelativeLayout mMyGroup;
    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @Bind(R.id.refresh_layout)
    SmartRefreshLayout mRefreshLayout;
    @Bind(R.id.side_bar)
    WaveSideBar mSideBar;
    @Bind(R.id.status_bar_fix)
    View mStatusBarFix;

    private DisplayMetrics mDm;
    private TreeMap<String, Boolean> mFromMap = new TreeMap<>();
    private List<UserInfo> mUsers = new ArrayList<>();
    @SuppressLint("HandlerLeak")
    Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    updateData();
                    break;
                case 1:
                    mNewFriend = MySharedPreferences.getInstance().getInteger(NEWFRIEND);
                    if (mNewFriend > 0) {
                        mNumber.setText(mNewFriend + "");
                        mNumber.setVisibility(View.VISIBLE);
                    } else {
                        mNumber.setVisibility(View.GONE);
                    }
                    break;
            }
        }
    };
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_list);
        ButterKnife.bind(this);
        setTitle(getString(R.string.friend));
        mNewFriend = MySharedPreferences.getInstance().getInteger(NEWFRIEND);
        if (mNewFriend > 0) {
            mNumber.setText(mNewFriend + "");
            mNumber.setVisibility(View.VISIBLE);
        }
        mMgr = new DBManager(this);
        mPersonalDetailsPresenter = new PersonalDetailsPresenter(this);
        initData();
        setListener();
        initRecylerView();
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
        updateData();
        getPhoneSize();
        showNumber();
    }

    //获取屏幕高度
    private void getPhoneSize() {
        mSideBar.setIndexItems();
        mDm = new DisplayMetrics();

        if (this != null)
            this.getWindowManager().getDefaultDisplay().getMetrics(mDm);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        ButterKnife.unbind(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        String msg = event.getMsg();
        if (msg.equals(getString(R.string.login_succeed))) {
            initData();
        } else if (msg.equals(EventBusUtil.new_friend)) {
            initData();
        } else if (msg.equals(EventBusUtil.delete_friend)) {
            updateData();
        } else if (msg.equals(getString(R.string.change_friend_remark))) {
            updateData();
        } else if (msg.equals(EventBusUtil.receive_add_request)) {
            sendHandler();
        } else if (msg.equals(getString(R.string.refresh_the_interface))) {
            updateData();
            initData();
        }
    }

    private void sendHandler() {
        Message message = new Message();
        message.what = 1;
        myHandler.sendMessage(message);
    }

    Map<String, Integer> mMap = new HashMap<>();

    private void updateData() {
        if (mFriendListRVAdapter == null) {
            return;
        }
        mUsers.clear();
        mMap.clear();
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
        mTvTitleTop.setText(getString(R.string.friend) + "(" + mUsers.size() + ")");
        Collections.sort(mUsers);
        try {
            for (int i = 0; i < mUsers.size(); i++) {
                if (!mUsers.get(i).getFirstLetter().equals("#")) {
                    mMap.put(mUsers.get(i).getFirstLetter(), i);
                }
            }
            String[] arr = mMap.keySet().toArray(new String[mMap.keySet().size() + 1]);
            arr[arr.length - 1] = "#";
            Arrays.sort(arr, String.CASE_INSENSITIVE_ORDER);
            for (int i = 0; i < arr.length; i++) {
                if (i < arr.length - 1)
                    arr[i] = arr[i + 1];
                if (i == arr.length - 1) {
                    arr[i] = "#";
                }
            }
            mSideBar.setIndexItems(arr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mFriendListRVAdapter.notifyDataSetChanged();
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

    private void showNumber() {
        new PersonalDetailsPresenter(this).getNewFriendData(false, new PersonalDetailsPresenter.CallBack5() {
            @Override
            public void send(NewFriendInfo listdata) {
                if (ActivityUtil.isActivityOnTop(MyFriendActivity.this)) {
                    mMgr.deleteRequest();
                    int number = 0;
                    for (int i = 0; i < listdata.getData().size(); i++) {
                        mMgr.addRequest(listdata.getData().get(i).getToco_id(), listdata.getData().get(i).getStatus(), listdata.getData().get(i).getName());
                        if (!listdata.getData().get(i).getToco_id().equals(UtilTool.getTocoId()) &&
                                listdata.getData().get(i).getStatus() == 1) {
                            number++;
                        }
                    }
                    MySharedPreferences.getInstance().setInteger(NEWFRIEND, number);
                    if (number > 0) {
                        mNumber.setText(number + "");
                        mNumber.setVisibility(View.VISIBLE);
                    } else {
                        mNumber.setVisibility(View.GONE);
                    }
                }
            }
        });
    }

    private void initData() {
        try {
            UtilTool.Log("fengjian", "發送獲取好友列表請求");
            mPersonalDetailsPresenter.getFriendList(new PersonalDetailsPresenter.CallBack2() {
                @Override
                public void send(List<AuatarListInfo.DataBean> data) {
                    if (ActivityUtil.isActivityOnTop(MyFriendActivity.this)) {
                        mRefreshLayout.finishRefresh();
                        mMgr.deleteAllFriend();
                        mMgr.addUserList(data);
                        myHandler.sendEmptyMessage(0);
                    }
                }

                @Override
                public void error() {
                    if (ActivityUtil.isActivityOnTop(MyFriendActivity.this)) {
                        mRefreshLayout.finishRefresh();
                    }
                }

                @Override
                public void finishRefresh() {
                    if (ActivityUtil.isActivityOnTop(MyFriendActivity.this)) {
                        mRefreshLayout.finishRefresh();
                    }
                }
            });
        } catch (Exception e) {
            UtilTool.Log("日志", e.getMessage());
        }

    }

    private void initRecylerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mFriendListRVAdapter = new FriendListRVAdapter(this, mUsers, mMgr);
        mFriendListRVAdapter.setOnClickListener(this);
        mRecyclerView.setAdapter(mFriendListRVAdapter);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                initData();
            }
        });
    }


    @OnClick({R.id.bark,  R.id.news_friend, R.id.my_group, R.id.my_public})
    public void onViewClicked(View view) {
        switch (view.getId()) {
//            case R.id.iv_more:
//                initPopWindow();
//                break;
            case R.id.bark:
                finish();
                break;
//            case R.id.iv_search:
//                startActivity(new Intent(this, SearchActivity.class));
//                break;
            case R.id.news_friend:
                startActivity(new Intent(this, NewFriendActivity.class));
                MySharedPreferences.getInstance().setInteger(NEWFRIEND, 0);
                mNumber.setVisibility(View.GONE);
                mNewFriend = 0;
                break;
            case R.id.my_group:
                startActivity(new Intent(this, GroupListActivity.class));
//                ToastShow.showToast2(this, getString(R.string.hint_unfinished));
                break;
            case R.id.my_public:
                startActivity(new Intent(this, PublicActivity.class));
                break;
        }
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
                Intent intent = new Intent(this, GrabQRCodeRedActivity.class);
                intent.putExtra("id", qrRedInfo.getRedID());
                intent.putExtra("type", true);
                startActivity(intent);
            }
        }
    }

    @Override
    public void onclick(int position) {
        Intent intent = new Intent(this, ConversationActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("name", mUsers.get(position).getUserName());
        bundle.putString("user", mUsers.get(position).getUser());
        intent.putExtras(bundle);
        mMgr.updateNumber(mUsers.get(position).getUser(), 0);
        EventBus.getDefault().post(new MessageEvent(EventBusUtil.dispose_unread_msg));
        this.startActivity(intent);
    }

    @Override
    public void onLongClick(int position) {
        String remark = mUsers.get(position).getRemark();
        if (StringUtils.isEmpty(remark)) {
            remark = mUsers.get(position).getUserName();
        }
        showRemarkDialog(mUsers.get(position).getUserName(), remark, mUsers.get(position).getUser());
    }

    private void showRemarkDialog(final String name, final String remark, final String user) {
        List<String> list = Arrays.asList(new String[]{this.getString(R.string.updata_remark), this.getString(R.string.send_card), this.getString(R.string.delete_friend)});
        final MenuListPopWindow menu = new MenuListPopWindow(this, list);
        menu.setListOnClick(new MenuListPopWindow.ListOnClick() {
            @Override
            public void onclickitem(int position) {
                Intent intent;
                switch (position) {
                    case 0:
                        menu.dismiss();
                        break;
                    case 1:
                        menu.dismiss();
                        intent = new Intent(MyFriendActivity.this, RemarkActivity.class);
                        intent.putExtra("name", name);
                        intent.putExtra("remark", remark);
                        intent.putExtra("user", user);
                        startActivity(intent);
                        break;
                    case 2:
                        menu.dismiss();
                        UserInfo info = mMgr.queryUser(user);
                        intent = new Intent(MyFriendActivity.this, SelectConversationActivity.class);
                        intent.putExtra("type", 2);
                        MessageInfo messageInfo = new MessageInfo();
                        messageInfo.setHeadUrl(info.getPath());
                        messageInfo.setMessage(name);
                        messageInfo.setCardUser(user);
                        intent.putExtra("msgType", TO_CARD_MSG);
                        intent.putExtra("messageInfo", messageInfo);
                        startActivity(intent);
                        break;
                    case 3:
                        menu.dismiss();
                        showDeleteDialog(remark, user, user);
                        break;
                }
            }
        });
        menu.setColor(Color.BLACK);
        menu.showAtLocation();
    }

    private void showDeleteDialog(String mName, final String mUser, final String roomId) {
        final DeleteCacheDialog deleteCacheDialog = new DeleteCacheDialog(R.layout.dialog_delete_cache, this, R.style.dialog);
        deleteCacheDialog.show();
        deleteCacheDialog.setTitle(getString(R.string.confirm_delete) + " " + mName + " " + getString(R.string.what));
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
                try {
                    new PersonalDetailsPresenter(MyFriendActivity.this).deleteFriend(mUser, new PersonalDetailsPresenter.CallBack() {
                        @Override
                        public void send() {
                            mMgr.deleteConversation(roomId);
                            mMgr.deleteMessage(roomId, 0);
                            mMgr.deleteUser(mUser);
                            EventBus.getDefault().post(new MessageEvent(EventBusUtil.delete_friend));
                        }
                    });
                    deleteCacheDialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
