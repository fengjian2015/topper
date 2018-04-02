package com.bclould.tocotalk.ui.activity;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;

import com.bclould.tocotalk.Presenter.LoginPresenter;
import com.bclould.tocotalk.R;
import com.bclould.tocotalk.base.BaseActivity;
import com.bclould.tocotalk.model.LoginRecordInfo;
import com.bclould.tocotalk.ui.adapter.LoginRecordRVAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by GA on 2018/4/2.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class LoginRecordActivity extends BaseActivity {
    @Bind(R.id.bark)
    ImageView mBark;
    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    private LoginRecordRVAdapter mLoginRecordRVAdapter;
    List<LoginRecordInfo.DataBean> mDataList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_record);
        ButterKnife.bind(this);
        initRecyler();
        initData();
    }

    private void initData() {
        LoginPresenter loginPresenter = new LoginPresenter(this);
        loginPresenter.loginRecord(new LoginPresenter.CallBack() {
            @Override
            public void send(List<LoginRecordInfo.DataBean> data) {
                mDataList.addAll(data);
                mLoginRecordRVAdapter.notifyDataSetChanged();
            }
        });
    }

    private void initRecyler() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mLoginRecordRVAdapter = new LoginRecordRVAdapter(this, mDataList);
        mRecyclerView.setAdapter(mLoginRecordRVAdapter);
    }

    @OnClick(R.id.bark)
    public void onViewClicked() {
        finish();
    }
}
