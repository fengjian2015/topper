package com.bclould.tocotalk.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.bclould.tocotalk.R;
import com.bclould.tocotalk.base.BaseActivity;
import com.bclould.tocotalk.ui.adapter.GroupListRVAdapter;
import com.bclould.tocotalk.xmpp.XmppConnection;

import org.jivesoftware.smackx.muc.HostedRoom;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by GA on 2018/1/5.
 */

public class GroupListActivity extends BaseActivity {

    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    private List<String> mHostRooms;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_list);
        ButterKnife.bind(this);
        initRecyclerView();
        initData();
    }

    private void initData() {
        mHostRooms = XmppConnection.getInstance().getJoinedRooms();
        List<HostedRoom> hostRooms = XmppConnection.getInstance().getHostRooms();
    }

    private void initRecyclerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        GroupListRVAdapter groupListRVAdapter = new GroupListRVAdapter(this, mHostRooms);
        mRecyclerView.setAdapter(groupListRVAdapter);
    }

    @OnClick({R.id.bark, R.id.tv_create})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;
            case R.id.tv_create:
                startActivity(new Intent(this, CreateGroupRoomActivity.class));
                break;
        }
    }
}
