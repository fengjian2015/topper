package com.bclould.tea.ui.activity;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bclould.tea.Presenter.FinanciaPresenter;
import com.bclould.tea.R;
import com.bclould.tea.base.BaseActivity;
import com.bclould.tea.model.FinanciaProductInfo;
import com.bclould.tea.model.HistoryInfo;
import com.bclould.tea.ui.adapter.FinancialHistoryAdapter;
import com.bclould.tea.ui.widget.MenuGridPopWindow;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

@RequiresApi(api = Build.VERSION_CODES.N)
public class FinancialHistoryActivity extends BaseActivity {

    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @Bind(R.id.refresh_layout)
    SmartRefreshLayout mRefreshLayout;
    @Bind(R.id.tv_title)
    TextView mTvTitle;
    @Bind(R.id.ll_no_data)
    LinearLayout mLlNoData;
    @Bind(R.id.tv_filtrate)
    TextView mTvFiltrate;

    private FinancialHistoryAdapter mFinancialHistoryAdapter;
    private LinearLayoutManager mLayoutManager;
    private List<HistoryInfo.DataBean> mHashMapList = new ArrayList<>();
    private int page = 1;
    private int type = 0;//0收益明细  1理财记录
    private int coin_id;
    private String coin_name;
    private List<FinanciaProductInfo.DataBean> mProductList = new ArrayList<>();
    private int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        ButterKnife.bind(this);
        initIntent();
        initAdapter();
        initHttp(true, 1);
        initDialog();
    }

    private void initIntent() {
        type = getIntent().getIntExtra("type", 0);
        coin_id = getIntent().getIntExtra("coin_id", coin_id);
        coin_name = getIntent().getStringExtra("coin_name");
        if (type == 0) {
            mTvTitle.setText(getString(R.string.income_breakdown));
        } else {
            mTvTitle.setText(getString(R.string.financial_record));
        }
        mTvFiltrate.setVisibility(View.VISIBLE);
    }

    private void initAdapter() {
        mRecyclerView.setFocusable(false);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mFinancialHistoryAdapter = new FinancialHistoryAdapter(this, mHashMapList, coin_name);
        mRecyclerView.setAdapter(mFinancialHistoryAdapter);
        mRefreshLayout.setEnableLoadMore(false);
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                initHttp(true, 1);
            }
        });
        mRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshLayout) {
                initHttp(false, page++);
            }
        });
    }

    private void initHttp(final boolean isRefresh, int p) {
        mFinancialHistoryAdapter.notifyDataSetChanged();
        if (type == 0) {
            new FinanciaPresenter(this).incomeList(coin_id, p, id, new FinanciaPresenter.CallBack3() {
                @Override
                public void send(HistoryInfo baseInfo) {
                    setData(baseInfo, isRefresh);
                }

                @Override
                public void error() {
                    resetRefresh(isRefresh);
                }
            });
        } else {
            new FinanciaPresenter(this).buyList(coin_id, p, id, new FinanciaPresenter.CallBack3() {
                @Override
                public void send(HistoryInfo baseInfo) {
                    setData(baseInfo, isRefresh);
                }

                @Override
                public void error() {
                    resetRefresh(isRefresh);
                }
            });
        }

    }

    private void initDialog() {
        new FinanciaPresenter(this).financialProduct(coin_id, new FinanciaPresenter.CallBack5() {
            @Override
            public void send(FinanciaProductInfo baseInfo) {
                mProductList.clear();
                mProductList.addAll(baseInfo.getData());
            }

            @Override
            public void error() {

            }
        });
    }

    private void setData(HistoryInfo baseInfo, boolean isRefresh) {
        resetRefresh(isRefresh);
        if (isRefresh) {
            if (baseInfo.getData().size() == 0) {
                mLlNoData.setVisibility(View.VISIBLE);
                mRefreshLayout.setVisibility(View.GONE);
                return;
            }
            mHashMapList.clear();
            page = 1;
        } else {
            page++;
        }
        if (baseInfo.getData().size() == 20) {
            mRefreshLayout.setEnableLoadMore(true);
        } else {
            mRefreshLayout.setEnableLoadMore(false);
        }
        mLlNoData.setVisibility(View.GONE);
        mRefreshLayout.setVisibility(View.VISIBLE);
        mHashMapList.addAll(baseInfo.getData());
        mFinancialHistoryAdapter.notifyDataSetChanged();
    }

    private void resetRefresh(boolean isRefresh) {
        if (isRefresh) {
            mRefreshLayout.finishRefresh();
        } else {
            mRefreshLayout.finishLoadMore();
        }
    }


    @OnClick({R.id.bark, R.id.tv_filtrate})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;
            case R.id.tv_filtrate:
                // TODO: 2018/11/2 筛选
                showDialog();
                break;
        }
    }


    private void showDialog() {
        if (mProductList.size() == 0) {
            return;
        }
        final MenuGridPopWindow menu = new MenuGridPopWindow(this, mProductList);
        menu.setListOnClick(new MenuGridPopWindow.ListOnClick() {
            @Override
            public void onclickitem(int position) {
                id = mProductList.get(position).getId();
                initHttp(true, 1);
            }
        });
        menu.setColor(Color.BLACK);
        menu.showAtLocation();
    }
}
