package com.dashiji.biyun.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dashiji.biyun.R;
import com.dashiji.biyun.base.BaseActivity;
import com.dashiji.biyun.base.MyApp;
import com.dashiji.biyun.history.DBManager;
import com.dashiji.biyun.model.AddRequestInfo;
import com.dashiji.biyun.ui.adapter.NewFriendRVAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by GA on 2017/10/11.
 */

public class NewFriendActivity extends BaseActivity {


    @Bind(R.id.bark)
    ImageView mBark;
    @Bind(R.id.tv_add)
    TextView mTvAdd;
    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    private DBManager mMgr;
    List<AddRequestInfo> mAddRequestInfos = new ArrayList<>();
    private NewFriendRVAdapter mNewFriendRVAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_friend);
        ButterKnife.bind(this);
        mMgr = new DBManager(this);
        initRecyclerView();
        initData();
        MyApp.getInstance().addActivity(this);
    }

    private void initData() {
        ArrayList<AddRequestInfo> addRequestInfos = mMgr.queryRequest();
        Collections.reverse(addRequestInfos);
        mAddRequestInfos.addAll(addRequestInfos);
        mNewFriendRVAdapter.notifyDataSetChanged();
    }

    private void initRecyclerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mNewFriendRVAdapter = new NewFriendRVAdapter(this, mAddRequestInfos, mMgr);
        mRecyclerView.setAdapter(mNewFriendRVAdapter);
    }


    @OnClick({R.id.bark, R.id.tv_add})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;
            case R.id.tv_add:
                startActivity(new Intent(this, AddFriendActivity.class));
                break;
        }
    }
}
