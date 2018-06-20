package com.bclould.tea.ui.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.bclould.tea.Presenter.GroupPresenter;
import com.bclould.tea.R;
import com.bclould.tea.base.BaseActivity;
import com.bclould.tea.history.DBManager;
import com.bclould.tea.base.MyApp;
import com.bclould.tea.history.DBRoomManage;
import com.bclould.tea.history.DBRoomMember;
import com.bclould.tea.model.GroupInfo;
import com.bclould.tea.model.RoomManageInfo;
import com.bclould.tea.model.RoomMemberInfo;
import com.bclould.tea.ui.adapter.GroupListRVAdapter;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;

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
    @Bind(R.id.tv_delete)
    TextView tvDelete;
    @Bind(R.id.et_create)
    EditText etCreate;
    @Bind(R.id.refresh_layout)
    SmartRefreshLayout mRefreshLayout;

    private ArrayList<RoomManageInfo> roomManagesList;
    private ArrayList<Boolean> roomList=new ArrayList<>();
    private DBRoomManage dbRoomManage;
    private DBManager mDBManager;
    private  GroupListRVAdapter groupListRVAdapter;
    private DBRoomMember mDBRoomMember;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_list);
        ButterKnife.bind(this);
        MyApp.getInstance().addActivity(this);
        initData();
        initRecyclerView();
    }


    private void initData() {
        dbRoomManage = new DBRoomManage(this);
        mDBRoomMember=new DBRoomMember(this);
        mDBManager=new DBManager(this);
        roomManagesList = dbRoomManage.queryAllRequest();
        for (int i=0;i<roomManagesList.size();i++){
            roomList.add(false);
        }
    }

    private void initRecyclerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        groupListRVAdapter = new GroupListRVAdapter(this, roomManagesList,mDBManager,roomList);
        mRecyclerView.setAdapter(groupListRVAdapter);
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                refreshlayout.finishRefresh(1000);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        getGroup();
                    }
                }).start();

            }
        });
    }

    private void getGroup() {
        new GroupPresenter(this).getGroup(new GroupPresenter.CallBack1() {
            @Override
            public void send(GroupInfo baseInfo) {
                // TODO: 2018/6/11 獲取群聊房間塞入數據庫
                dbRoomManage.deleteAllRoom();
                mDBRoomMember.deleteAllRoomMember();
                for (GroupInfo.DataBean dataBean : baseInfo.getData()) {
                    RoomManageInfo roomManageInfo = new RoomManageInfo();
                    roomManageInfo.setRoomName(dataBean.getName());
                    roomManageInfo.setRoomId(dataBean.getId() + "");
                    dbRoomManage.addRoom(roomManageInfo);
                    for (GroupInfo.DataBean.UsersBean usersBean: dataBean.getUsers()){
                        RoomMemberInfo roomMemberInfo=new RoomMemberInfo();
                        roomMemberInfo.setRoomId(dataBean.getId()+"");
                        roomMemberInfo.setJid(usersBean.getToco_id());
                        roomMemberInfo.setImage_url(usersBean.getAvatar());
                        mDBRoomMember.addRoomMember(roomMemberInfo);
                    }
                }
            }
        });
    }

    @OnClick({R.id.bark, R.id.tv_create,R.id.tv_delete})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;
            case R.id.tv_create:
                Intent intent=new Intent(this,CreateGroupRoomActivity.class);
                intent.putExtra("roomName",etCreate.getText().toString());
                startActivity(intent);
                break;
            case R.id.tv_delete:
                // TODO: 2018/5/30 刪除群
                deleteGroup();
                break;
        }
    }

    private void deleteGroup() {
//        new GroupPresenter(GroupListActivity.this).deleteGroup(Integer.parseInt(roomid));
    }
}
