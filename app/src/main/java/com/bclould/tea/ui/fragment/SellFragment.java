package com.bclould.tea.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.bclould.tea.Presenter.BuySellPresenter;
import com.bclould.tea.R;
import com.bclould.tea.base.BaseFragment;
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
import static com.bclould.tea.Presenter.LoginPresenter.STATE_ID;

/**
 * Created by GA on 2017/9/20.
 */

public class SellFragment extends BaseFragment {

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
    private List<DealListInfo.DataBean> mDataList = new ArrayList<>();
    private BuySellRVAdapter mBuySellRVAdapter;
    private BuySellPresenter mBuySellPresenter;
    private int PULL_UP = 0;
    private int PULL_DOWN = 1;
    private int mPage = 1;
    private int mPageSize = 10;
    private int mState_id;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mView == null)
            mView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_buy, container, false);
        if (MyApp.getInstance().mOtcCoinList.size() != 0) {
            mCoinName = MyApp.getInstance().mOtcCoinList.get(0).getName();
        }

        mState_id = MySharedPreferences.getInstance().getInteger(STATE_ID);
        ButterKnife.bind(this, mView);
        mBuySellPresenter = new BuySellPresenter(getContext());
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
        initRecyclerView();
        initData(PULL_DOWN,1);
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
        }   if (event.getNumber() != 0) {
            mState_id = event.getNumber();
        }
        if (msg.equals(getString(R.string.coin_switchover))) {
            initData(PULL_DOWN,1);
        } else if (msg.equals(getString(R.string.publish_deal))) {
            initData(PULL_DOWN,1);
        } else if (msg.equals(getString(R.string.state_switchover))) {
            initData(PULL_DOWN,1);
        } else if (msg.equals(getString(R.string.sold_out_buy))) {
            initData(PULL_DOWN,1);
        }
    }

    private void initRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);
        mBuySellRVAdapter = new BuySellRVAdapter(getActivity(), true, mDataList, mBuySellPresenter);
        mRecyclerView.setAdapter(mBuySellRVAdapter);
    }

    boolean isFinish = true;

    private void initListener() {
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                if (isFinish)
                    initData(PULL_DOWN,1);
            }
        });
        mRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshLayout) {
                if (isFinish) {
                    initData(PULL_UP,mPage+1);
                }
            }
        });
    }

    private void initData(final int type,int p) {

        isFinish = false;
        mBuySellPresenter.getDealList(p, mPageSize, 2, mCoinName, mState_id, new BuySellPresenter.CallBack() {
            @Override
            public void send(List<DealListInfo.DataBean> dataBean) {
                if (ActivityUtil.isActivityOnTop(getActivity())) {
                    if (mRecyclerView != null) {
                        if (type == PULL_DOWN) {
                            mRefreshLayout.finishRefresh();
                            mPage=1;
                        } else {
                            mPage++;
                            mRefreshLayout.finishLoadMore();
                        }
                        isFinish = true;
                        if (mDataList.size() != 0 || dataBean.size() != 0) {
                            mRecyclerView.setVisibility(View.VISIBLE);
                            mLlNoData.setVisibility(View.GONE);
                            mLlError.setVisibility(View.GONE);
                            if (type == PULL_DOWN) {
                                if (dataBean.size() == 0) {
                                    mRecyclerView.setVisibility(View.GONE);
                                    mLlNoData.setVisibility(View.VISIBLE);
                                    mLlError.setVisibility(View.GONE);
                                } else {
                                    mDataList.clear();
                                }
                            }
                            mDataList.addAll(dataBean);
                            mBuySellRVAdapter.notifyDataSetChanged();
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
                    isFinish = true;
                    if (type == PULL_DOWN) {
                        mRefreshLayout.finishRefresh();
                    } else {
                        mRefreshLayout.finishLoadMore();
                    }
                    if (type == PULL_DOWN) {
                        mRecyclerView.setVisibility(View.GONE);
                        mLlNoData.setVisibility(View.GONE);
                        mLlError.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void finishRefresh() {
                isFinish = true;
                if (type == PULL_DOWN) {
                    mRefreshLayout.finishRefresh();
                } else {
                    mRefreshLayout.finishLoadMore();
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
