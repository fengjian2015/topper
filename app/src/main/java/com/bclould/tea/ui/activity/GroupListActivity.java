package com.bclould.tea.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.bclould.tea.Presenter.GroupPresenter;
import com.bclould.tea.R;
import com.bclould.tea.base.BaseActivity;
import com.bclould.tea.base.MyApp;
import com.bclould.tea.history.DBManager;
import com.bclould.tea.history.DBRoomManage;
import com.bclould.tea.history.DBRoomMember;
import com.bclould.tea.model.GroupInfo;
import com.bclould.tea.model.RoomManageInfo;
import com.bclould.tea.ui.adapter.GroupListRVAdapter;
import com.bclould.tea.utils.AppLanguageUtils;
import com.bclould.tea.utils.EventBusUtil;
import com.bclould.tea.utils.MessageEvent;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

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
    @Bind(R.id.et_create)
    EditText etCreate;
    @Bind(R.id.refresh_layout)
    SmartRefreshLayout mRefreshLayout;
    @Bind(R.id.ll_no_data)
    LinearLayout mLlNoData;

    private ArrayList<RoomManageInfo> roomManagesList;
    private ArrayList<Boolean> roomList = new ArrayList<>();
    private DBRoomManage dbRoomManage;
    private DBManager mDBManager;
    private GroupListRVAdapter groupListRVAdapter;
    private DBRoomMember mDBRoomMember;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_list);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);//初始化EventBus
        MyApp.getInstance().addActivity(this);
        initData();
        initRecyclerView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(AppLanguageUtils.attachBaseContext(newBase, newBase.getString(R.string.language_pref_key)));
    }

    private void initData() {
        dbRoomManage = new DBRoomManage(this);
        mDBRoomMember = new DBRoomMember(this);
        mDBManager = new DBManager(this);
        roomManagesList = dbRoomManage.queryAllRequest();
        for (int i = 0; i < roomManagesList.size(); i++) {
            roomList.add(false);
        }
        changeShowView(roomManagesList.size());
    }

    //接受通知
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        String msg = event.getMsg();
        if (msg.equals(EventBusUtil.quit_group)) {
            updataRecyclerView();
        }else if(msg.equals(getString(R.string.create_group_chat))){
            finish();
        }
    }

    private void changeShowView(int size){
        if(size<=0){
            mLlNoData.setVisibility(View.VISIBLE);
            mRefreshLayout.setVisibility(View.GONE);
        }else{
            mLlNoData.setVisibility(View.GONE);
            mRefreshLayout.setVisibility(View.VISIBLE);
        }
    }

    private void initRecyclerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        groupListRVAdapter = new GroupListRVAdapter(this, roomManagesList, mDBManager, roomList,dbRoomManage);
        mRecyclerView.setAdapter(groupListRVAdapter);
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                getGroup(false);
            }
        });
    }

    private void updataRecyclerView(){
        roomManagesList.clear();
        roomList.clear();
        roomManagesList.addAll(dbRoomManage.queryAllRequest());
        for (int i = 0; i < roomManagesList.size(); i++) {
            roomList.add(false);
        }
        changeShowView(roomManagesList.size());
        groupListRVAdapter.notifyDataSetChanged();
    }

    private void getGroup(boolean isShow) {
        new GroupPresenter(this).getGroup(mDBRoomMember,dbRoomManage,mDBManager,isShow,new GroupPresenter.CallBack1() {
            @Override
            public void send(GroupInfo baseInfo) {
                mRefreshLayout.finishRefresh();
                updataRecyclerView();
            }

            @Override
            public void error() {
                mRefreshLayout.finishRefresh();
            }

            @Override
            public void finishRefresh() {
                mRefreshLayout.finishRefresh();
            }
        });
    }

    @OnClick({R.id.bark, R.id.tv_create,R.id.ll_no_data})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;
            case R.id.tv_create:
                Intent intent = new Intent(this, CreateGroupRoomActivity.class);
                intent.putExtra("roomName", etCreate.getText().toString());
                intent.putExtra("type",0);
                startActivity(intent);
                break;
            case R.id.ll_no_data:
                getGroup(true);
                break;
        }
    }

}
