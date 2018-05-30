package com.bclould.tocotalk.ui.activity;

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
import android.widget.Toast;

import com.bclould.tocotalk.R;
import com.bclould.tocotalk.base.BaseActivity;
import com.bclould.tocotalk.history.DBRoomManage;
import com.bclould.tocotalk.model.RoomManageInfo;
import com.bclould.tocotalk.ui.adapter.GroupListRVAdapter;
import com.bclould.tocotalk.utils.StringUtils;
import com.bclould.tocotalk.utils.ToastShow;

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
        dbRoomManage = new DBRoomManage(this);
        roomManagesList = dbRoomManage.queryAllRequest();
    }

    private void initRecyclerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        GroupListRVAdapter groupListRVAdapter = new GroupListRVAdapter(this, roomManagesList);
        mRecyclerView.setAdapter(groupListRVAdapter);
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
                break;
        }
    }
}
