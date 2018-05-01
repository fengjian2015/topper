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

import com.bclould.tocotalk.Presenter.BlockchainGuessPresenter;
import com.bclould.tocotalk.R;
import com.bclould.tocotalk.model.GuessListInfo;
import com.bclould.tocotalk.ui.adapter.GuessListRVAdapter;
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
 * Created by GA on 2018/4/23.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class PersonageGuessFragment extends Fragment {
    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @Bind(R.id.iv)
    ImageView mIv;
    @Bind(R.id.ll_no_data)
    LinearLayout mLlNoData;
    @Bind(R.id.refresh_layout)
    SmartRefreshLayout mRefreshLayout;
    private BlockchainGuessPresenter mBlockchainGuessPresenter;
    private GuessListRVAdapter mGuessListRVAdapter;
    private int mPage = 1;
    private int mPageSize = 1000;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_personage_guess, container, false);
        ButterKnife.bind(this, view);
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
        init();
        return view;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        String msg = event.getMsg();
        if (msg.equals(getString(R.string.push_guess))) {
            initData();
        } else if (msg.equals(getString(R.string.bet))) {
            initData();
        } else if (msg.equals(getString(R.string.guess_cancel))) {
            initData();
        }
    }

    private void init() {
        mBlockchainGuessPresenter = new BlockchainGuessPresenter(getContext());
        initData();
        initRecyclerView();
        initListener();
    }

    private void initListener() {
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                refreshlayout.finishRefresh(2000);
                initData();
            }
        });
    }

    private void initRecyclerView() {
        mGuessListRVAdapter = new GuessListRVAdapter(mDataList, getContext());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mGuessListRVAdapter);
    }

    List<GuessListInfo.DataBean> mDataList = new ArrayList<>();

    private void initData() {
        mDataList.clear();
        mBlockchainGuessPresenter.getGuessList(mPage, mPageSize, 1, new BlockchainGuessPresenter.CallBack() {
            @Override
            public void send(List<GuessListInfo.DataBean> data) {
                if (data.size() != 0) {
                    for (GuessListInfo.DataBean dataBean : data) {
                        UtilTool.Log("競猜", dataBean.getStatus() + "");
                    }
                    mRecyclerView.setVisibility(View.VISIBLE);
                    mLlNoData.setVisibility(View.GONE);
                    mDataList.addAll(data);
                    mGuessListRVAdapter.notifyDataSetChanged();
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
        ButterKnife.unbind(this);
        EventBus.getDefault().unregister(this);
    }
}