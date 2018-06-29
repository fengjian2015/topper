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

import com.bclould.tea.Presenter.BuySellPresenter;
import com.bclould.tea.R;
import com.bclould.tea.base.BaseActivity;
import com.bclould.tea.base.MyApp;
import com.bclould.tea.history.DBManager;
import com.bclould.tea.model.OrderListInfo;
import com.bclould.tea.ui.adapter.OrderRVAdapter;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by GA on 2018/5/14.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class SumBuySellActivity extends BaseActivity {
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
    @Bind(R.id.bark)
    ImageView mBark;
    @Bind(R.id.tv_title)
    TextView mTvTitle;
    @Bind(R.id.iv2)
    ImageView mIv2;
    @Bind(R.id.ll_error)
    LinearLayout mLlError;
    private DBManager mMgr;
    private OrderRVAdapter mOrderRVAdapter;
    private BuySellPresenter mBuySellPresenter;
    private int mPage = 1;
    private int mPageSize = 1000;
    private int mType = 0;
    private String mCoinName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sum_buysell);
        ButterKnife.bind(this);
        MyApp.getInstance().addActivity(this);
        mMgr = new DBManager(this);
        mBuySellPresenter = new BuySellPresenter(this);
        initIntent();
        initRecyclerView();
        initData();
        initListener();
    }

    private void initIntent() {
        mType = getIntent().getIntExtra("type", 0);
        mCoinName = getIntent().getStringExtra("coin_name");
        if (mType == 1) {
            mTvTitle.setText(getString(R.string.sum_buy));
        } else {
            mTvTitle.setText(getString(R.string.sum_sell));
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

    List<OrderListInfo.DataBean> mDataList = new ArrayList<>();

    private void initData() {
        mBuySellPresenter.getMyOrderList(mCoinName, mType, mPage, mPageSize, new BuySellPresenter.CallBack3() {
            @Override
            public void send(List<OrderListInfo.DataBean> data) {
                if (mRecyclerView != null) {
                    mRefreshLayout.finishRefresh();
                    if (data.size() != 0) {
                        mRecyclerView.setVisibility(View.VISIBLE);
                        mLlNoData.setVisibility(View.GONE);
                        mLlError.setVisibility(View.GONE);
                        mDataList.clear();
                        mDataList.addAll(data);
                        mOrderRVAdapter.notifyDataSetChanged();
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

    private void initRecyclerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mOrderRVAdapter = new OrderRVAdapter(this, mDataList, mMgr, 1);
        mRecyclerView.setAdapter(mOrderRVAdapter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
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
