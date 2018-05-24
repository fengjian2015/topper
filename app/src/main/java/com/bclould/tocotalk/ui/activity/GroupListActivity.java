package com.bclould.tocotalk.ui.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.bclould.tocotalk.R;
import com.bclould.tocotalk.base.BaseActivity;
import com.bclould.tocotalk.history.DBRoomManage;
import com.bclould.tocotalk.model.RoomManageInfo;
import com.bclould.tocotalk.ui.adapter.GroupListRVAdapter;
import com.bclould.tocotalk.utils.UtilTool;
import com.bclould.tocotalk.xmpp.RoomManage;
import com.bclould.tocotalk.xmpp.XmppConnection;

import org.jivesoftware.smackx.muc.HostedRoom;
import org.jivesoftware.smackx.muc.MultiUserChat;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by GA on 2018/1/5.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class GroupListActivity extends BaseActivity {

    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    private ArrayList<RoomManageInfo> roomManagesList;
    private DBRoomManage dbRoomManage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_list);
        ButterKnife.bind(this);
        initData();
        initRecyclerView();
    }


    private void initData() {
        dbRoomManage=new DBRoomManage(this);
        roomManagesList=dbRoomManage.queryAllRequest();
//        mHostRooms = XmppConnection.getInstance().getJoinedRooms();
//        List<HostedRoom> hostRooms = XmppConnection.getInstance().getHostRooms();
//        if(mHostRooms==null){
//            return;
//        }
//        for (int i=0;i<mHostRooms.size();i++){
//            RoomManage.getInstance().addMultiMessageManage(mHostRooms.get(i),"群聊"+i).joinMultiUserChat("昵称"+i,mHostRooms.get(i));
//        }

    }

    private void initRecyclerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        GroupListRVAdapter groupListRVAdapter = new GroupListRVAdapter(this, roomManagesList);
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
