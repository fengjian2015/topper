package com.dashiji.biyun.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.dashiji.biyun.Presenter.BuySellPresenter;
import com.dashiji.biyun.R;
import com.dashiji.biyun.base.BaseFragment;
import com.dashiji.biyun.model.DealListInfo;
import com.dashiji.biyun.ui.adapter.BuySellRVAdapter;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by GA on 2017/9/20.
 */

public class SellFragment extends BaseFragment {

    public static SellFragment instance = null;
    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @Bind(R.id.refreshLayout)
    SmartRefreshLayout mRefreshLayout;
    @Bind(R.id.ll_coin_selector)
    LinearLayout mLlCoinSelector;
    private View mView;
    private int mIndex;

    public static SellFragment getInstance() {

        if (instance == null) {

            instance = new SellFragment();

        }

        return instance;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mView == null)
            mView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_buy, container, false);
        ButterKnife.bind(this, mView);
        setListener();
        setSelector2(0);
//        initData("BTC");
        initCoinSelector();
        return mView;
    }

    private void setListener() {
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                refreshlayout.finishRefresh(2000);
                switch (mIndex) {
                    case 0:
                        initData("BTC");
                        break;
                    case 1:
                        initData("TPX");
                        break;
                    case 2:
                        initData("USDT");
                        break;
                }
            }
        });
        mRefreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                refreshlayout.finishLoadmore(2000);
            }
        });
    }

    private void initData(String coin) {
        BuySellPresenter buySellPresenter = new BuySellPresenter(getContext());
        buySellPresenter.getDealList(1, coin, new BuySellPresenter.CallBack() {
            @Override
            public void send(List<DealListInfo.DataBean> dataBean, String coin) {
                mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

                mRecyclerView.setAdapter(new BuySellRVAdapter(getActivity(), true, dataBean));
            }
        });
    }

    private void initCoinSelector() {
        for (int i = 0; i < mLlCoinSelector.getChildCount(); i++) {

            final View childAt = mLlCoinSelector.getChildAt(i);

            childAt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    int index = mLlCoinSelector.indexOfChild(childAt);

                    setSelector2(index);

                }
            });
        }
    }

    //菜单选项选中处理
    private void setSelector2(int index) {
        mIndex = index;

        for (int i = 0; i < mLlCoinSelector.getChildCount(); i++) {

            if (i == index) {
                mLlCoinSelector.getChildAt(i).setSelected(true);
                switch (index) {
                    case 0:
                        initData("BTC");
                        break;
                    case 1:
                        initData("TPX");
                        break;
                    case 2:
                        initData("USDT");
                        break;
                }

            } else {
                mLlCoinSelector.getChildAt(i).setSelected(false);

            }
        }
    }

    private void initRecyclerView() {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

}
