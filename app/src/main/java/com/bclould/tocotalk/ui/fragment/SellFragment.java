package com.bclould.tocotalk.ui.fragment;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bclould.tocotalk.Presenter.BuySellPresenter;
import com.bclould.tocotalk.R;
import com.bclould.tocotalk.base.BaseFragment;
import com.bclould.tocotalk.model.DealListInfo;
import com.bclould.tocotalk.ui.adapter.BuySellRVAdapter;
import com.bclould.tocotalk.utils.MessageEvent;
import com.bclould.tocotalk.utils.UtilTool;
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

/**
 * Created by GA on 2017/9/20.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class SellFragment extends BaseFragment {

    public static SellFragment instance = null;
    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @Bind(R.id.refreshLayout)
    SmartRefreshLayout mRefreshLayout;
    @Bind(R.id.iv)
    ImageView mIv;
    @Bind(R.id.ll_no_data)
    LinearLayout mLlNoData;
    private View mView;
    private String mCoinName = "BTC";
    private List<DealListInfo.DataBean> mDataList = new ArrayList<>();
    private BuySellRVAdapter mBuySellRVAdapter;

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
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
        initRecyclerView();
        initListener();
        return mView;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            initData(mCoinName);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        String msg = event.getMsg();
        mCoinName = event.getCoinName();
        if (msg.equals("幣種切換")) {
            initData(mCoinName);
        } else if (msg.equals("发布交易")) {
            initData(mCoinName);
            UtilTool.Log("卖", mCoinName);
        }
    }

    private void initRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);
        mBuySellRVAdapter = new BuySellRVAdapter(getActivity(), true, mDataList);
        mRecyclerView.setAdapter(mBuySellRVAdapter);
    }

    private void initListener() {
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                refreshlayout.finishRefresh(2000);
                initData(mCoinName);
            }
        });
    }

    private void initData(String coin) {
        mDataList.clear();
        BuySellPresenter buySellPresenter = new BuySellPresenter(getContext());
        buySellPresenter.getDealList(2, coin, new BuySellPresenter.CallBack() {
            @Override
            public void send(List<DealListInfo.DataBean> dataBean, String coin) {
                if (dataBean.size() != 0) {
                    mRecyclerView.setVisibility(View.VISIBLE);
                    mLlNoData.setVisibility(View.GONE);
                    mDataList.addAll(dataBean);
                    mBuySellRVAdapter.notifyDataSetChanged();
                } else {
                    mRecyclerView.setVisibility(View.GONE);
                    mLlNoData.setVisibility(View.VISIBLE);
                }
            }
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
        ButterKnife.unbind(this);
    }

}
