package com.bclould.tea.ui.activity;

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

import com.bclould.tea.Presenter.DynamicPresenter;
import com.bclould.tea.R;
import com.bclould.tea.base.BaseActivity;
import com.bclould.tea.model.DynamicListInfo;
import com.bclould.tea.ui.adapter.DynamicRVAdapter;
import com.bclould.tea.utils.MessageEvent;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
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
 * Created by GA on 2018/5/15.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class PersonageDynamicActivity extends BaseActivity {

    @Bind(R.id.bark)
    ImageView mBark;
    @Bind(R.id.tv_title)
    TextView mTvTitle;
    @Bind(R.id.iv)
    ImageView mIv;
    @Bind(R.id.tv_hint)
    TextView mTvHint;
    @Bind(R.id.ll_no_data)
    LinearLayout mLlNoData;
    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @Bind(R.id.refreshLayout)
    SmartRefreshLayout mRefreshLayout;
    private String mName;
    private String mUser;
    private DynamicPresenter mDynamicPresenter;
    private int PULL_UP = 0;
    private int PULL_DOWN = 1;
    private int end = 0;
    private int mPage = 1;
    private int mPageSize = 10;
    private DynamicRVAdapter mDynamicRVAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_dynamic);
        ButterKnife.bind(this);
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
        initIntent();
        initListener();
        initRecyclerView();
        initData(PULL_DOWN);
    }

    //接受通知
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        String msg = event.getMsg();
        if (msg.equals(getString(R.string.delete_dynamic))) {
            String id = event.getId();
            for (int i = 0; i < mDataList.size(); i++) {
                if (mDataList.get(i).getId() == Integer.parseInt(id)) {
                    mDataList.remove(i);
                    mDynamicRVAdapter.notifyItemRemoved(i);
                    mDynamicRVAdapter.notifyItemRangeChanged(0, mDataList.size() - i);
                    return;
                }
            }
        }
    }

    boolean isFinish = true;
    private void initListener() {
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                refreshLayout.finishRefresh(2000);
                initData(PULL_DOWN);
            }
        });
        mRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshLayout) {
                refreshLayout.finishLoadMore(1000);
                if (isFinish) {
                    initData(PULL_UP);
                }
            }
        });
    }

    List<DynamicListInfo.DataBean> mDataList = new ArrayList<>();

    private void initData(final int type) {
        if (type == PULL_DOWN) {
            mPage = 1;
            end = 0;
        }
        isFinish = false;
        mDynamicPresenter.taDynamicList(mPage, mPageSize, mUser, new DynamicPresenter.CallBack2() {
            @Override
            public void send(List<DynamicListInfo.DataBean> data) {
                if (mRecyclerView != null) {
                    if (mDataList.size() != 0 || data.size() != 0) {
                        isFinish = true;
                        if (type == PULL_UP) {
                            if (data.size() == mPageSize) {
                                mPage++;
                                mDataList.addAll(data);
                                mDynamicRVAdapter.notifyDataSetChanged();
                            } else {
                                if (end == 0) {
                                    end++;
                                    mDataList.addAll(data);
                                    mDynamicRVAdapter.notifyDataSetChanged();
                                }
                            }
                        } else {
                            if (mPage == 1) {
                                mPage++;
                            }
                            mDataList.clear();
                            mDataList.addAll(data);
                            mDynamicRVAdapter.notifyDataSetChanged();
                        }
                        mRecyclerView.setVisibility(View.VISIBLE);
                        mLlNoData.setVisibility(View.GONE);
                    } else {
                        mRecyclerView.setVisibility(View.GONE);
                        mLlNoData.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }

    private void initRecyclerView() {
        mDynamicPresenter = new DynamicPresenter(this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mDynamicRVAdapter = new DynamicRVAdapter(this, mDataList, mDynamicPresenter);
        mRecyclerView.setAdapter(mDynamicRVAdapter);
    }

    private void initIntent() {
        mName = getIntent().getStringExtra("name");
        mUser= getIntent().getStringExtra("user");
        mTvTitle.setText(mName);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        EventBus.getDefault().unregister(this);
    }

    @OnClick(R.id.bark)
    public void onViewClicked() {
        finish();
    }

}