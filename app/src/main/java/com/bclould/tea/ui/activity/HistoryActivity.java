package com.bclould.tea.ui.activity;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.bclould.tea.Presenter.DistributionPresenter;
import com.bclould.tea.R;
import com.bclould.tea.base.BaseActivity;
import com.bclould.tea.model.HistoryInfo;
import com.bclould.tea.ui.adapter.HistoryAdapter;
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
public class HistoryActivity extends BaseActivity {

    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @Bind(R.id.refresh_layout)
    SmartRefreshLayout mRefreshLayout;

    private HistoryAdapter mHistoryAdapter;
    private LinearLayoutManager mLayoutManager;
    private List<HistoryInfo.DataBean> mHashMapList=new ArrayList<>();
    private int page=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        ButterKnife.bind(this);
        initAdapter();
        initHttp(true,1);
    }

    private void initAdapter() {
        mRecyclerView.setFocusable(false);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mHistoryAdapter=new HistoryAdapter(this,mHashMapList);
        mRecyclerView.setAdapter(mHistoryAdapter);
        mRefreshLayout.setEnableLoadMore(false);
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                initHttp(true,1);
            }
        });
        mRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshLayout) {
                initHttp(false,page+1);
            }
        });
    }

    private void initHttp(final boolean isRefresh,int p){
        mHistoryAdapter.notifyDataSetChanged();
        new DistributionPresenter(this).historyFtc(p, new DistributionPresenter.CallBack6() {
            @Override
            public void send(HistoryInfo baseInfo) {
                resetRefresh(isRefresh);
                if(isRefresh){
                    mHashMapList.clear();
                    page=1;
                }else{
                    page++;
                }
                if(baseInfo.getData().size()==10){
                    mRefreshLayout.setEnableLoadMore(true);
                }else{
                    mRefreshLayout.setEnableLoadMore(false);
                }
                mHashMapList.addAll(baseInfo.getData());
                mHistoryAdapter.notifyDataSetChanged();
            }

            @Override
            public void error() {
                resetRefresh(isRefresh);
            }
        });
    }

    private void resetRefresh(boolean isRefresh){
        if(isRefresh){
            mRefreshLayout.finishRefresh();
        }else{
            mRefreshLayout.finishLoadMore();
        }
    }


    @OnClick({R.id.bark})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;
        }
    }

}
