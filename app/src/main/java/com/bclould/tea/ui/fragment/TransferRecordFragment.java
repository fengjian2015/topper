package com.bclould.tea.ui.fragment;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.bclould.tea.Presenter.FinanciaPresenter;
import com.bclould.tea.R;
import com.bclould.tea.model.TransferRecordInfo;
import com.bclould.tea.ui.adapter.TransferRecordAdapter;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by GIjia on 2018/8/22.
 */
@RequiresApi(api = Build.VERSION_CODES.N)
public class TransferRecordFragment extends LazyFragment {

    private View view;
    // 标志位，标志已经初始化完成。
    private boolean isPrepared;
    private Context context;

    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @Bind(R.id.refresh_layout)
    SmartRefreshLayout mRefreshLayout;
    @Bind(R.id.ll_no_data)
    LinearLayout mLlNoData;

    private TransferRecordAdapter mTransferRecordAdapter;
    private LinearLayoutManager mLayoutManager;
    private List<TransferRecordInfo.DataBean> mHashMapList = new ArrayList<>();

    private int type = 0;
    private int page = 1;
    private int coin_id;
    private String coin_name;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = View.inflate(getActivity(), R.layout.fragment_freed, null);
        ButterKnife.bind(this, view);
        context = getActivity();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        isPrepared = true;
        lazyLoad();
        super.onActivityCreated(savedInstanceState);
    }

    public void setData(int type, int coin_id, String coin_name) {
        this.type = type;
        this.coin_id = coin_id;
        this.coin_name = coin_name;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void lazyLoad() {
        if (!isPrepared || !isVisible) {
            return;
        }
        initAdapter();
        init();
    }

    private void init() {
        initHttp(true, 1);
    }

    private void initHttp(final boolean isRefresh, int p) {
        new FinanciaPresenter(getActivity()).financialInOut(coin_id, p, type, new FinanciaPresenter.CallBack4() {
            @Override
            public void send(TransferRecordInfo baseInfo) {
                resetRefresh(isRefresh);
                if (isRefresh) {
                    if(baseInfo.getData().size()==0){
                        mLlNoData.setVisibility(View.VISIBLE);
                        mRefreshLayout.setVisibility(View.GONE);
                        return;
                    }
                    mHashMapList.clear();
                    page = 1;
                } else {
                    page++;
                }
                mLlNoData.setVisibility(View.GONE);
                mRefreshLayout.setVisibility(View.VISIBLE);
                if (baseInfo.getData().size() == 20) {
                    mRefreshLayout.setEnableLoadMore(true);
                } else {
                    mRefreshLayout.setEnableLoadMore(false);
                }
                mHashMapList.addAll(baseInfo.getData());
                mTransferRecordAdapter.notifyDataSetChanged();
            }

            @Override
            public void error() {
                resetRefresh(isRefresh);
            }
        });
    }

    private void resetRefresh(boolean isRefresh) {
        if (isRefresh) {
            mRefreshLayout.finishRefresh();
        } else {
            mRefreshLayout.finishLoadMore();
        }
    }

    private void initAdapter() {
        mRecyclerView.setFocusable(false);
        mLayoutManager = new LinearLayoutManager(context);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mTransferRecordAdapter = new TransferRecordAdapter(context, mHashMapList, coin_name);
        mRecyclerView.setAdapter(mTransferRecordAdapter);
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
                initHttp(false, page+1);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
