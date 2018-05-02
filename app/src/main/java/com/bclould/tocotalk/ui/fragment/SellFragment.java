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
import com.bclould.tocotalk.base.MyApp;
import com.bclould.tocotalk.model.DealListInfo;
import com.bclould.tocotalk.ui.adapter.BuySellRVAdapter;
import com.bclould.tocotalk.utils.MessageEvent;
import com.bclould.tocotalk.utils.MySharedPreferences;
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

import static com.bclould.tocotalk.Presenter.LoginPresenter.STATE;

/**
 * Created by GA on 2017/9/20.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class SellFragment extends BaseFragment {

    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @Bind(R.id.refreshLayout)
    SmartRefreshLayout mRefreshLayout;
    @Bind(R.id.iv)
    ImageView mIv;
    @Bind(R.id.ll_no_data)
    LinearLayout mLlNoData;
    private View mView;
    private String mCoinName = "";
    private String mState = MySharedPreferences.getInstance().getString(STATE);
    private List<DealListInfo.DataBean> mDataList = new ArrayList<>();
    private BuySellRVAdapter mBuySellRVAdapter;
    private BuySellPresenter mBuySellPresenter;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mView == null)
            mView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_buy, container, false);
        if (MyApp.getInstance().mOtcCoinList.size() != 0) {
            mCoinName = MyApp.getInstance().mOtcCoinList.get(0).getName();
        }
        ButterKnife.bind(this, mView);
        mBuySellPresenter = new BuySellPresenter(getContext());
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
        initRecyclerView();
        initData(mCoinName, mState);
        initListener();
        return mView;
    }

    /*@Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            initData(mCoinName);
        }
    }*/

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        String msg = event.getMsg();
        if (event.getCoinName() != null) {
            mCoinName = event.getCoinName();
        } else if (event.getState() != null) {
            mState = event.getState();
        }
        if (msg.equals(getString(R.string.coin_switchover))) {
            initData(mCoinName, mState);
        } else if (msg.equals(getString(R.string.publish_deal))) {
            initData(mCoinName, mState);
            UtilTool.Log("卖", mState);
        } else if (msg.equals(getString(R.string.state_switchover))) {
            initData(mCoinName, mState);
        } else if (msg.equals(getString(R.string.sold_out_buy))) {
            initData(mCoinName, mState);
        }
    }

    private void initRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);
        mBuySellRVAdapter = new BuySellRVAdapter(getActivity(), true, mDataList, mBuySellPresenter);
        mRecyclerView.setAdapter(mBuySellRVAdapter);
    }

    private void initListener() {
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                refreshlayout.finishRefresh(2000);
                initData(mCoinName, mState);
            }
        });
    }

    private void initData(String coinName, String state) {
        mBuySellPresenter.getDealList(2, coinName, state, new BuySellPresenter.CallBack() {
            @Override
            public void send(List<DealListInfo.DataBean> dataBean, String coin) {
                if (mRecyclerView != null) {
                    if (dataBean.size() != 0) {
                        mRecyclerView.setVisibility(View.VISIBLE);
                        mLlNoData.setVisibility(View.GONE);
                        mDataList.clear();
                        mDataList.addAll(dataBean);
                        mBuySellRVAdapter.notifyDataSetChanged();
                    } else {
                        mRecyclerView.setVisibility(View.GONE);
                        mLlNoData.setVisibility(View.VISIBLE);
                    }
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
