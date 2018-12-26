package com.bclould.tea.ui.activity;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bclould.tea.Presenter.LoginPresenter;
import com.bclould.tea.R;
import com.bclould.tea.base.BaseActivity;
import com.bclould.tea.base.MyApp;
import com.bclould.tea.model.LoginRecordInfo;
import com.bclould.tea.ui.adapter.LoginRecordRVAdapter;
import com.bclould.tea.utils.ActivityUtil;
import com.bclould.tea.utils.AppLanguageUtils;
import com.bclould.tea.utils.MySharedPreferences;

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
    @Bind(R.id.iv2)
    ImageView mIv2;
    @Bind(R.id.ll_error)
    LinearLayout mLlError;
    private LoginRecordRVAdapter mLoginRecordRVAdapter;
    List<LoginRecordInfo.DataBean> mDataList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_record);
        ButterKnife.bind(this);
        setTitle(getString(R.string.login_record));
        MyApp.getInstance().addActivity(this);
        initRecyler();
        initData();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(AppLanguageUtils.attachBaseContext(newBase, MySharedPreferences.getInstance().getString(newBase.getString(R.string.language_pref_key))));
    }

    private void initData() {
        LoginPresenter loginPresenter = new LoginPresenter(this);
        loginPresenter.loginRecord(new LoginPresenter.CallBack() {
            @Override
            public void send(List<LoginRecordInfo.DataBean> data) {
                if (ActivityUtil.isActivityOnTop(LoginRecordActivity.this)) {
                    mRecyclerView.setVisibility(View.VISIBLE);
                    mLlError.setVisibility(View.GONE);
                    mDataList.addAll(data);
                    mLoginRecordRVAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void error() {
                if (ActivityUtil.isActivityOnTop(LoginRecordActivity.this)) {
                    mRecyclerView.setVisibility(View.GONE);
                    mLlError.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void initRecyler() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mLoginRecordRVAdapter = new LoginRecordRVAdapter(this, mDataList);
        mRecyclerView.setAdapter(mLoginRecordRVAdapter);
    }

    @OnClick({R.id.bark, R.id.ll_error})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;
            case R.id.ll_error:
                initData();
                break;
        }
    }
}
