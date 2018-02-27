package com.bclould.tocotalk.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bclould.tocotalk.R;
import com.bclould.tocotalk.base.BaseActivity;
import com.bclould.tocotalk.history.DBManager;
import com.bclould.tocotalk.model.UserInfo;
import com.bclould.tocotalk.ui.adapter.CreateGroupRVAdapter;
import com.bclould.tocotalk.xmpp.XmppConnection;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by GA on 2018/1/5.
 */

public class CreateGroupRoomActivity extends BaseActivity {
    @Bind(R.id.bark)
    ImageView mBark;
    @Bind(R.id.tv_create)
    TextView mTvCreate;
    @Bind(R.id.et_group_name)
    EditText mEtGroupName;
    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    private List<UserInfo> mUserInfos;
    private List<UserInfo> mUserInfoList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group_room);
        ButterKnife.bind(this);
        initData();
        initRecylerView();
    }

    private void initData() {
        DBManager mgr = new DBManager(this);
        mUserInfos = mgr.queryAllUser();
    }

    private void initRecylerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        CreateGroupRVAdapter createGroupRVAdapter = new CreateGroupRVAdapter(this, mUserInfos);
        mRecyclerView.setAdapter(createGroupRVAdapter);
    }

    @OnClick({R.id.bark, R.id.tv_create})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;
            case R.id.tv_create:
                XmppConnection xmppConnection = XmppConnection.getInstance();
                if (mUserInfoList != null)
                    try {
                        xmppConnection.createRoom(mEtGroupName.getText().toString(), null, mUserInfoList);
                        finish();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                else
                    Toast.makeText(this, "请选择好友", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    public void setData(UserInfo userInfo, boolean b) {
        if (b)
            mUserInfoList.add(userInfo);
        else
            mUserInfoList.remove(userInfo);
    }
}
