package com.bclould.tea.ui.activity;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.bclould.tea.R;
import com.bclould.tea.base.BaseActivity;
import com.bclould.tea.base.MyApp;
import com.bclould.tea.history.DBManager;
import com.bclould.tea.model.MessageInfo;
import com.bclould.tea.model.UserInfo;
import com.bclould.tea.ui.adapter.FriendListRVAdapter;
import com.bclould.tea.utils.StringUtils;
import com.bclould.tea.utils.UtilTool;
import com.bclould.tea.xmpp.RoomManage;
import com.gjiazhe.wavesidebar.WaveSideBar;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
@RequiresApi(api = Build.VERSION_CODES.N)
public class SelectFriendGetActivity extends BaseActivity implements FriendListRVAdapter.OnclickListener{

    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @Bind(R.id.side_bar)
    WaveSideBar mSideBar;

    private DBManager mMgr;
    private List<UserInfo> mUsers = new ArrayList<>();
    private FriendListRVAdapter mFriendListRVAdapter;
    private String roomId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_friend_get);
        ButterKnife.bind(this);
        MyApp.getInstance().addActivity(this);
        mMgr = new DBManager(this);
        roomId=getIntent().getStringExtra("roomId");
        initRecylerView();
        setListener();
        updateData();
    }

    private void initRecylerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mFriendListRVAdapter = new FriendListRVAdapter(this, mUsers, mMgr);
        mFriendListRVAdapter.setOnClickListener(this);
        mRecyclerView.setAdapter(mFriendListRVAdapter);
        mRecyclerView.setNestedScrollingEnabled(false);
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

    @OnClick({R.id.bark})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;

        }
    }

    @Override
    public void onclick(int position) {
        MessageInfo messageInfo = new MessageInfo();
        messageInfo.setHeadUrl(mUsers.get(position).getPath());
        messageInfo.setMessage(mUsers.get(position).getUserName());
        messageInfo.setCardUser(mUsers.get(position).getUser());
        RoomManage.getInstance().getRoom(roomId).sendCaed(messageInfo);
        SelectFriendGetActivity.this.finish();
    }

    @Override
    public void onLongClick(int position) {

    }
}
