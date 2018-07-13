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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bclould.tea.Presenter.CollectPresenter;
import com.bclould.tea.R;
import com.bclould.tea.base.BaseActivity;
import com.bclould.tea.model.CollectInfo;
import com.bclould.tea.ui.adapter.CollectRVAdapter;
import com.bclould.tea.utils.ActivityUtil;
import com.bclould.tea.utils.MessageEvent;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by GA on 2018/7/13.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class CollectActivity extends BaseActivity {
    @Bind(R.id.bark)
    ImageView mBark;
    @Bind(R.id.tv_add)
    TextView mTvAdd;
    @Bind(R.id.iv)
    ImageView mIv;
    @Bind(R.id.tv_hint)
    TextView mTvHint;
    @Bind(R.id.ll_no_data)
    LinearLayout mLlNoData;
    @Bind(R.id.iv2)
    ImageView mIv2;
    @Bind(R.id.ll_error)
    LinearLayout mLlError;
    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    List<CollectInfo.DataBean> mDataList = new ArrayList<>();
    @Bind(R.id.refresh_layout)
    SmartRefreshLayout mRefreshLayout;
    private CollectRVAdapter mCollectRVAdapter;
    private CollectPresenter mCollectPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collect);
        ButterKnife.bind(this);
        if(!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this);
        }
        mCollectPresenter = new CollectPresenter(this);
        initRecyclerView();
        initListener();
        initData();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        String msg = event.getMsg();
        if (msg.equals(getString(R.string.add_collect))) {
            initData();
        }
    }

    private void initListener() {
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                initData();
            }
        });
    }

    private void initRecyclerView() {
        mCollectRVAdapter = new CollectRVAdapter(this, mDataList, mCollectPresenter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mCollectRVAdapter);
    }

    private void initData() {
        mCollectPresenter.getCollectList(new CollectPresenter.CallBack() {
            @Override
            public void send(List<CollectInfo.DataBean> data) {
                if (ActivityUtil.isActivityOnTop(CollectActivity.this)) {
                    mRefreshLayout.finishRefresh();
                    if (data.size() != 0) {
                        mRecyclerView.setVisibility(View.VISIBLE);
                        mLlNoData.setVisibility(View.GONE);
                        mLlError.setVisibility(View.GONE);
                        mDataList.clear();
                        mDataList.addAll(data);
                        mCollectRVAdapter.notifyDataSetChanged();
                    } else {
                        mRecyclerView.setVisibility(View.GONE);
                        mLlNoData.setVisibility(View.VISIBLE);
                        mLlError.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void error() {
                mRefreshLayout.finishRefresh();
                mRecyclerView.setVisibility(View.GONE);
                mLlNoData.setVisibility(View.GONE);
                mLlError.setVisibility(View.VISIBLE);
            }

            @Override
            public void finishRefresh() {
                mRefreshLayout.finishRefresh();
            }
        });
    }

    @OnClick({R.id.bark, R.id.tv_add})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;
            case R.id.tv_add:
                startActivity(new Intent(this, AddCollectActivity.class));
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        ButterKnife.unbind(this);
    }
}
