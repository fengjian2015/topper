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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bclould.tea.Presenter.NewsNoticePresenter;
import com.bclould.tea.R;
import com.bclould.tea.model.GonggaoListInfo;
import com.bclould.tea.ui.adapter.NewsManagerRVAdapter;
import com.bclould.tea.ui.widget.DeleteCacheDialog;
import com.bclould.tea.utils.ActivityUtil;
import com.bclould.tea.utils.Constants;
import com.bclould.tea.utils.MessageEvent;
import com.bclould.tea.utils.SpaceItemDecoration;
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

/**
 * Created by GA on 2018/5/8.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class NewsBrowseRecordFragment extends Fragment {
    @Bind(R.id.iv)
    ImageView mIv;
    @Bind(R.id.ll_no_data)
    LinearLayout mLlNoData;
    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @Bind(R.id.refreshLayout)
    SmartRefreshLayout mRefreshLayout;
    @Bind(R.id.iv2)
    ImageView mIv2;
    @Bind(R.id.ll_error)
    LinearLayout mLlError;
    private NewsNoticePresenter mNewsNoticePresenter;
    private int PULL_UP = 0;
    private int PULL_DOWN = 1;
    private int mPage_id = 0;
    private int mPageSize = 10;
    boolean isFinish = true;
    private NewsManagerRVAdapter mNewsManagerRVAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_news_manager, container, false);
        ButterKnife.bind(this, view);
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
        return view;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        String msg = event.getMsg();
        if (msg.equals(getString(R.string.empty_news_browsing_history))) {
            initData(PULL_DOWN);
            mDataList.clear();
        } else if (msg.equals(getString(R.string.empty_news_browsing_history_callback))) {
            if (mDataList.size() != 0) {
                showDialog();
            } else {
                Toast.makeText(getContext(), getString(R.string.news_browse_record_null_hint), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showDialog() {
        final DeleteCacheDialog deleteCacheDialog = new DeleteCacheDialog(R.layout.dialog_delete_cache, getContext(), R.style.dialog);
        deleteCacheDialog.show();
        deleteCacheDialog.setTitle(getString(R.string.empty_news_browsing_history_hint));
        Button cancel = (Button) deleteCacheDialog.findViewById(R.id.btn_cancel);
        Button confirm = (Button) deleteCacheDialog.findViewById(R.id.btn_confirm);
        confirm.setTextColor(getResources().getColor(R.color.red));
        confirm.setText(getString(R.string.empty));
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteCacheDialog.dismiss();
            }
        });
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteCacheDialog.dismiss();
                mNewsNoticePresenter.deleteDrowsingHistory();
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mNewsNoticePresenter = new NewsNoticePresenter(getContext());
        initListener();
        initRecyclerView();
        initData(PULL_DOWN);
    }

    private void initListener() {
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                if (isFinish)
                    initData(PULL_DOWN);
            }
        });
        mRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshLayout) {
                if (isFinish)
                    initData(PULL_UP);
            }
        });
    }

    List<GonggaoListInfo.DataBean> mDataList = new ArrayList<>();

    private void initData(final int type) {
        if (type == PULL_DOWN) {
            mPage_id = 0;
        }
        isFinish = false;
        mNewsNoticePresenter.getBrowseRecord(mPage_id, mPageSize, new NewsNoticePresenter.CallBack2() {
            @Override
            public void send(List<GonggaoListInfo.DataBean> data) {
                if (ActivityUtil.isActivityOnTop(getActivity())) {
                    if (mRecyclerView != null) {
                        if (type == PULL_DOWN) {
                            mRefreshLayout.finishRefresh();
                        } else {
                            mRefreshLayout.finishLoadMore();
                        }
                        isFinish = true;
                        if (mDataList.size() != 0 || data.size() != 0) {
                            mRecyclerView.setVisibility(View.VISIBLE);
                            mLlNoData.setVisibility(View.GONE);
                            mLlError.setVisibility(View.GONE);
                            if (type == PULL_DOWN) {
                                if (data.size() == 0) {
                                    mRecyclerView.setVisibility(View.GONE);
                                    mLlNoData.setVisibility(View.VISIBLE);
                                    mLlError.setVisibility(View.GONE);
                                } else {
                                    mDataList.clear();
                                }
                            }
                            mDataList.addAll(data);
                            if (mDataList.size() != 0) {
                                mPage_id = mDataList.get(mDataList.size() - 1).getId();
                            }
                            mNewsManagerRVAdapter.notifyDataSetChanged();
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
                    if (type == PULL_UP) {
                        mRefreshLayout.finishLoadMore();
                    } else {
                        mRefreshLayout.finishRefresh();
                    }
                    mRecyclerView.setVisibility(View.GONE);
                    mLlNoData.setVisibility(View.GONE);
                    mLlError.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void finishRefresh() {
                isFinish = true;
                if (type == PULL_UP) {
                    mRefreshLayout.finishLoadMore();
                } else {
                    mRefreshLayout.finishRefresh();
                }
            }
        });
    }

    private void initRecyclerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mNewsManagerRVAdapter = new NewsManagerRVAdapter(getContext(), mDataList, mNewsNoticePresenter, Constants.NEW_BROWSE_TYPE);
        mRecyclerView.setAdapter(mNewsManagerRVAdapter);
        mRecyclerView.addItemDecoration(new SpaceItemDecoration(40));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        EventBus.getDefault().unregister(this);
    }
}
