package com.bclould.tea.ui.fragment;

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

import com.bclould.tea.Presenter.BuySellPresenter;
import com.bclould.tea.R;
import com.bclould.tea.base.MyApp;
import com.bclould.tea.model.DealListInfo;
import com.bclould.tea.ui.adapter.BuySellRVAdapter;
import com.bclould.tea.utils.ActivityUtil;
import com.bclould.tea.utils.MessageEvent;
import com.bclould.tea.utils.MySharedPreferences;
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

import static com.bclould.tea.Presenter.LoginPresenter.STATE;

/**
 * Created by GA on 2017/9/20.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class BuyFragment extends Fragment {

    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @Bind(R.id.refreshLayout)
    SmartRefreshLayout mRefreshLayout;
    @Bind(R.id.iv)
    ImageView mIv;
    @Bind(R.id.ll_no_data)
    LinearLayout mLlNoData;
    @Bind(R.id.iv2)
    ImageView mIv2;
    @Bind(R.id.ll_error)
    LinearLayout mLlError;
    private View mView;
    private String mCoinName = "TPC";
    private String mState;
    private List<DealListInfo.DataBean> mDataList = new ArrayList<>();
    private BuySellRVAdapter mBuySellRVAdapter;
    private BuySellPresenter mBuySellPresenter;
    private int PULL_UP = 0;
    private int PULL_DOWN = 1;
    private int mPage = 1;
    private int mPageSize = 10;
    private int end = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mView == null)
            mView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_buy, container, false);
        if (MyApp.getInstance().mOtcCoinList.size() != 0) {
            mCoinName = MyApp.getInstance().mOtcCoinList.get(0).getName();
        }
        if (MySharedPreferences.getInstance().getSp().contains(STATE)) {
            mState = MySharedPreferences.getInstance().getString(STATE);
        } else {
            mState = getString(R.string.china_mainland);
        }
        ButterKnife.bind(this, mView);
        mBuySellPresenter = new BuySellPresenter(getContext());
        initRecyclerView();
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
        initData(mCoinName, mState, PULL_DOWN);
        bindBankStatus();
        initListener();
        return mView;
    }

    private void bindBankStatus() {
        mBuySellPresenter.bindBankStatus();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        String msg = event.getMsg();
        if (event.getCoinName() != null) {
            mCoinName = event.getCoinName();
        } else if (event.getState() != null) {
            mState = event.getState();
        }
        if (msg.equals(getString(R.string.coin_switchover))) {
            initData(mCoinName, mState, PULL_DOWN);
        } else if (msg.equals(getString(R.string.publish_deal))) {
            initData(mCoinName, mState, PULL_DOWN);
        } else if (msg.equals(getString(R.string.state_switchover))) {
            initData(mCoinName, mState, PULL_DOWN);
        } else if (msg.equals(getString(R.string.sold_out_sell))) {
            initData(mCoinName, mState, PULL_DOWN);
        }
    }

    /*@Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            initData(mCoinName);
        }
    }*/

    boolean isFinish = true;

    private void initListener() {
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                refreshlayout.finishRefresh(2000);
                initData(mCoinName, mState, PULL_DOWN);
            }
        });
        mRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshLayout) {
                refreshLayout.finishLoadMore(1000);
                if (isFinish) {
                    initData(mCoinName, mState, PULL_UP);
                }
            }
        });
    }

    private void initRecyclerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mBuySellRVAdapter = new BuySellRVAdapter(getActivity(), false, mDataList, mBuySellPresenter);
        mRecyclerView.setAdapter(mBuySellRVAdapter);
    }


    private void initData(String coinName, String state, final int type) {
        if (type == PULL_DOWN) {
            mPage = 1;
            end = 0;
        }
        isFinish = false;
        mBuySellPresenter.getDealList(mPage, mPageSize, 1, coinName, state, new BuySellPresenter.CallBack() {
            @Override
            public void send(List<DealListInfo.DataBean> dataBean, String coin) {
                if (ActivityUtil.isActivityOnTop(getActivity())) {
                    if (mRecyclerView != null) {
                        if (mDataList.size() != 0 || dataBean.size() != 0) {
                            isFinish = true;
                            if (type == PULL_UP) {
                                if (dataBean.size() == mPageSize) {
                                    mPage++;
                                    mDataList.addAll(dataBean);
                                    mBuySellRVAdapter.notifyDataSetChanged();
                                } else {
                                    if (end == 0) {
                                        end++;
                                        mDataList.addAll(dataBean);
                                        mBuySellRVAdapter.notifyDataSetChanged();
                                    }
                                }
                            } else {
                                if (mPage == 1) {
                                    mPage++;
                                }
                                mDataList.clear();
                                mDataList.addAll(dataBean);
                                mBuySellRVAdapter.notifyDataSetChanged();
                            }
                            mRecyclerView.setVisibility(View.VISIBLE);
                            mLlNoData.setVisibility(View.GONE);
                            mLlError.setVisibility(View.GONE);
                        } else {
                            mRecyclerView.setVisibility(View.GONE);
                            mLlNoData.setVisibility(View.VISIBLE);
                            mLlError.setVisibility(View.GONE);
                        }
                    }
                }
            }

            @Override
            public void error() {
                if (ActivityUtil.isActivityOnTop(getActivity())) {
                    if (type == PULL_DOWN) {
                        mRecyclerView.setVisibility(View.GONE);
                        mLlNoData.setVisibility(View.GONE);
                        mLlError.setVisibility(View.VISIBLE);
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
