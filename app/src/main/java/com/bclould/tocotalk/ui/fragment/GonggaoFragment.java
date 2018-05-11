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
import android.widget.TextView;

import com.bclould.tocotalk.Presenter.NewsNoticePresenter;
import com.bclould.tocotalk.R;
import com.bclould.tocotalk.model.GonggaoListInfo;
import com.bclould.tocotalk.ui.adapter.GonggaoManagerRVAdapter;
import com.bclould.tocotalk.utils.SpaceItemDecoration;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by GA on 2018/5/7.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class GonggaoFragment extends Fragment {
    private static GonggaoFragment instance;
    @Bind(R.id.tv_kaifa)
    TextView mTvKaifa;
    @Bind(R.id.iv)
    ImageView mIv;
    @Bind(R.id.ll_no_data)
    LinearLayout mLlNoData;
    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @Bind(R.id.refreshLayout)
    SmartRefreshLayout mRefreshLayout;
    private NewsNoticePresenter mNewsNoticePresenter;
    private GonggaoManagerRVAdapter mGonggaoManagerRVAdapter;
    private int mPage = 1;
    private int mStatus = 1;
    private int mPageSize = 1000;

    public static GonggaoFragment getInstance() {
        if (instance == null) {
            instance = new GonggaoFragment();
        }
        return instance;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_gonggao, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mNewsNoticePresenter = new NewsNoticePresenter(getContext());
        initListener();
        initRecyclerView();
        initData();
    }

    private void initListener() {
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                refreshLayout.finishRefresh(2000);
                initData();
            }
        });
    }

    List<GonggaoListInfo.DataBean> mDataList = new ArrayList<>();

    private void initData() {
        mNewsNoticePresenter.getGonggaoList(mPage, mPageSize, mStatus, new NewsNoticePresenter.CallBack2() {
            @Override
            public void send(List<GonggaoListInfo.DataBean> data) {
                if (mRecyclerView != null) {
                    if (data.size() != 0) {
                        mRecyclerView.setVisibility(View.VISIBLE);
                        mLlNoData.setVisibility(View.GONE);
                        mDataList.clear();
                        mDataList.addAll(data);
                        mGonggaoManagerRVAdapter.notifyDataSetChanged();
                    } else {
                        mLlNoData.setVisibility(View.VISIBLE);
                        mRecyclerView.setVisibility(View.GONE);
                    }
                }
            }
        });
    }

    private void initRecyclerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mGonggaoManagerRVAdapter = new GonggaoManagerRVAdapter(getContext(), mDataList);
        mRecyclerView.setAdapter(mGonggaoManagerRVAdapter);
        mRecyclerView.addItemDecoration(new SpaceItemDecoration(40));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
