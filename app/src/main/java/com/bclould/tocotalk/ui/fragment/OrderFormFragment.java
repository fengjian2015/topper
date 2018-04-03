package com.bclould.tocotalk.ui.fragment;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bclould.tocotalk.Presenter.BuySellPresenter;
import com.bclould.tocotalk.R;
import com.bclould.tocotalk.history.DBManager;
import com.bclould.tocotalk.model.OrderListInfo;
import com.bclould.tocotalk.ui.adapter.OrderRVAdapter;
import com.bclould.tocotalk.utils.MessageEvent;
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
 * Created by GA on 2018/1/15.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class OrderFormFragment extends Fragment {
    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @Bind(R.id.refreshLayout)
    SmartRefreshLayout mRefreshLayout;
    @Bind(R.id.iv)
    ImageView mIv;
    @Bind(R.id.ll_no_data)
    LinearLayout mLlNoData;
    private String mCoinName = "BTC";
    private String mFiltrate = "全部";
    private List<OrderListInfo.DataBean> mDataList = new ArrayList<>();
    private OrderRVAdapter mOrderRVAdapter;
    private DBManager mMgr;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_buy, container, false);
        ButterKnife.bind(this, view);
        mMgr = new DBManager(getContext());
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
        initRecyclerView();
        initListener();
        initData(mCoinName, mFiltrate);
        return view;
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        String msg = event.getMsg();
        if (event.getCoinName() != null) {
            mCoinName = event.getCoinName();
        } else if (event.getFiltrate() != null) {
            mFiltrate = event.getFiltrate();
        }
        if (msg.equals("幣種切換")) {
            initData(mCoinName, mFiltrate);
        } else if (msg.equals("确认付款")) {
            for (OrderListInfo.DataBean info : mDataList) {
                if (info.getId() == Integer.parseInt(event.getId())) {
                    info.setStatus_name("等待放币");
                    mOrderRVAdapter.notifyDataSetChanged();
                }
            }
        } else if (msg.equals("取消订单")) {
            for (OrderListInfo.DataBean info : mDataList) {
                if (info.getId() == Integer.parseInt(event.getId())) {
                    info.setStatus_name("已取消");
                    mOrderRVAdapter.notifyDataSetChanged();
                }
            }
        } else if (msg.equals("确认放币")) {
            for (OrderListInfo.DataBean info : mDataList) {
                if (info.getId() == Integer.parseInt(event.getId())) {
                    info.setStatus_name("已完成");
                    mOrderRVAdapter.notifyDataSetChanged();
                }
            }
        } else if (msg.equals("创建订单")) {
            initData(mCoinName, mFiltrate);
        } else if (msg.equals("创建订单")) {
            initData(mCoinName, mFiltrate);
        }else if(msg.equals("交易订单筛选")){
            initData(mCoinName, mFiltrate);
        }
    }

    private void initListener() {
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                refreshlayout.finishRefresh(2000);
                initData(mCoinName, mFiltrate);
            }
        });
    }

    private void initRecyclerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mOrderRVAdapter = new OrderRVAdapter(getActivity(), mDataList, mMgr);
        mRecyclerView.setAdapter(mOrderRVAdapter);
    }

    private void initData(String coinName, String filtrate) {
        mDataList.clear();
        BuySellPresenter buySellPresenter = new BuySellPresenter(getContext());
        buySellPresenter.getOrderList(coinName, filtrate, new BuySellPresenter.CallBack3() {
            @Override
            public void send(List<OrderListInfo.DataBean> data) {
                if (data.size() != 0) {
                    mRecyclerView.setVisibility(View.VISIBLE);
                    mLlNoData.setVisibility(View.GONE);
                    mDataList.addAll(data);
                    mOrderRVAdapter.notifyDataSetChanged();
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
