package com.bclould.tocotalk.ui.fragment;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bclould.tocotalk.Presenter.BuySellPresenter;
import com.bclould.tocotalk.R;
import com.bclould.tocotalk.base.MyApp;
import com.bclould.tocotalk.history.DBManager;
import com.bclould.tocotalk.model.OrderListInfo;
import com.bclould.tocotalk.model.TransRecordInfo;
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
import butterknife.OnClick;


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
    @Bind(R.id.et_search)
    EditText mEtSearch;
    @Bind(R.id.iv_search)
    ImageView mIvSearch;
    @Bind(R.id.cb_search)
    CardView mCbSearch;
    @Bind(R.id.tv_hint)
    TextView mTvHint;
    private String mCoinName = "";
    private String mFiltrate = "";
    private List<OrderListInfo.DataBean> mDataList = new ArrayList<>();
    private OrderRVAdapter mOrderRVAdapter;
    private DBManager mMgr;
    private BuySellPresenter mBuySellPresenter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_buy, container, false);
        if (MyApp.getInstance().mOtcCoinList.size() != 0) {
            mCoinName = MyApp.getInstance().mOtcCoinList.get(0).getName();
        }
        mBuySellPresenter = new BuySellPresenter(getContext());
        ButterKnife.bind(this, view);
        mTvHint.setText(getString(R.string.no_order));
        mCbSearch.setVisibility(View.VISIBLE);
        mFiltrate = "2";
        mMgr = new DBManager(getContext());
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
        initRecyclerView();
        initData(mCoinName, mFiltrate, "");
        initListener();
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
        if (msg.equals(getString(R.string.coin_switchover))) {
            initData(mCoinName, mFiltrate, "");
        } else if (msg.equals(getString(R.string.confirm_fk))) {
            initData(mCoinName, mFiltrate, "");
           /* for (OrderListInfo.DataBean info : mNewsList) {
                if (info.getId() == Integer.parseInt(event.getId())) {
                    info.setStatus_name("等待放币");
                    info.setStatus(2);
                    mOrderRVAdapter.notifyDataSetChanged();
                }
            }*/
        } else if (msg.equals(getString(R.string.cancel_order))) {
            for (OrderListInfo.DataBean info : mDataList) {
                if (info.getId() == Integer.parseInt(event.getId())) {
                    info.setStatus_name(getString(R.string.canceled_canc));
                    info.setStatus(0);
                    mOrderRVAdapter.notifyDataSetChanged();
                }
            }
        } else if (msg.equals(getString(R.string.confirm_fb))) {
            for (OrderListInfo.DataBean info : mDataList) {
                if (info.getId() == Integer.parseInt(event.getId())) {
                    info.setStatus_name(getString(R.string.off_the_stocks));
                    info.setStatus(3);
                    mOrderRVAdapter.notifyDataSetChanged();
                }
            }
        } else if (msg.equals(getString(R.string.create_order))) {
            initData(mCoinName, mFiltrate, "");
        } else if (msg.equals(getString(R.string.create_order))) {
            initData(mCoinName, mFiltrate, "");
        } else if (msg.equals(getString(R.string.deal_order_filtrate))) {
            initData(mCoinName, mFiltrate, "");
        } else if (msg.equals(getString(R.string.order_update))) {
            initData(mCoinName, mFiltrate, "");
        }
    }

    private void initListener() {
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                refreshlayout.finishRefresh(2000);
                initData(mCoinName, mFiltrate, "");
            }
        });
        mEtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {//修改回车键功能
                    // 隐藏键盘
                    ((InputMethodManager) mEtSearch.getContext().getSystemService(Context.INPUT_METHOD_SERVICE))
                            .hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                                    InputMethodManager.HIDE_NOT_ALWAYS);
                    String user = mEtSearch.getText().toString().trim();
                    initData(mCoinName, mFiltrate, user);
                    return true;
                }
                return false;
            }
        });
    }

    private void initRecyclerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mOrderRVAdapter = new OrderRVAdapter(getActivity(), mDataList, mMgr);
        mRecyclerView.setAdapter(mOrderRVAdapter);
    }

    private void initData(String coinName, String filtrate, String user) {
        mEtSearch.setText("");
        mDataList.clear();
        mOrderRVAdapter.notifyDataSetChanged();
        mBuySellPresenter.getOrderList(coinName, filtrate, user, new BuySellPresenter.CallBack3() {
            @Override
            public void send(List<OrderListInfo.DataBean> data) {
                if (mRecyclerView != null) {
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
            }

            @Override
            public void send2(TransRecordInfo.DataBean data) {

            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
        ButterKnife.unbind(this);
    }

    @OnClick(R.id.iv_search)
    public void onViewClicked() {
    }
}
