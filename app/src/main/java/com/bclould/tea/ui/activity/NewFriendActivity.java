package com.bclould.tea.ui.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bclould.tea.Presenter.PersonalDetailsPresenter;
import com.bclould.tea.R;
import com.bclould.tea.base.BaseActivity;
import com.bclould.tea.base.MyApp;
import com.bclould.tea.history.DBManager;
import com.bclould.tea.model.AddRequestInfo;
import com.bclould.tea.model.AuatarListInfo;
import com.bclould.tea.ui.adapter.NewFriendRVAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by GA on 2017/10/11.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class NewFriendActivity extends BaseActivity {


    @Bind(R.id.bark)
    ImageView mBark;
    @Bind(R.id.tv_add)
    TextView mTvAdd;
    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    private DBManager mMgr;
    List<AddRequestInfo> mAddRequestInfos = new ArrayList<>();
    List<String> mImageList = new ArrayList<>();
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
        ArrayList<AddRequestInfo> addRequestInfos = mMgr.queryAllRequest();
        Collections.reverse(addRequestInfos);
        mAddRequestInfos.addAll(addRequestInfos);
        String userList = "";
        for (AddRequestInfo info : mAddRequestInfos) {
            if (userList.isEmpty()) {
                userList = info.getUser();
            } else {
                userList += "," + info.getUser();
            }
        }
        PersonalDetailsPresenter personalDetailsPresenter = new PersonalDetailsPresenter(this);
        personalDetailsPresenter.getFriendImageList(userList, new PersonalDetailsPresenter.CallBack2() {
            @Override
            public void send(final List<AuatarListInfo.DataBean> data) {
                if (data != null && data.size() != 0) {
                    for (int i = 0; i < data.size(); i++) {
                        mImageList.add(data.get(i).getAvatar());
                    }
                    mNewFriendRVAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void error() {

            }
        });

    }

    private void initRecyclerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mNewFriendRVAdapter = new NewFriendRVAdapter(this, mAddRequestInfos, mMgr, mImageList);
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
