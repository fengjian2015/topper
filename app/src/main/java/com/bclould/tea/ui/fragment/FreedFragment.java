package com.bclould.tea.ui.fragment;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bclould.tea.Presenter.DistributionPresenter;
import com.bclould.tea.R;
import com.bclould.tea.model.NodeInfo;
import com.bclould.tea.ui.adapter.NodeAdapter;
import com.bclould.tea.utils.EventBusUtil;
import com.bclould.tea.utils.MessageEvent;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by GIjia on 2018/8/22.
 */
@RequiresApi(api = Build.VERSION_CODES.N)
public class FreedFragment extends LazyFragment {
    private View view;
    // 标志位，标志已经初始化完成。
    private boolean isPrepared;
    private Context context;

    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @Bind(R.id.refresh_layout)
    SmartRefreshLayout mRefreshLayout;

    private NodeAdapter mNodeAdapter;
    private LinearLayoutManager mLayoutManager;
    private List<NodeInfo.DataBean.ListsBean> mHashMapList=new ArrayList<>();

    private int type=1;
    private int page=1;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = View.inflate(getActivity(), R.layout.fragment_freed, null);
        ButterKnife.bind(this, view);
        context = getActivity();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        isPrepared = true;
        lazyLoad();
        super.onActivityCreated(savedInstanceState);
    }

    public void setType(int type){
        this.type=type;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void lazyLoad() {
        if (!isPrepared || !isVisible) {
            return;
        }
        initAdapter();
        init();
    }

    private void init() {
        initHttp(true,1);
    }

    private void initHttp(final boolean isRefresh,int p){
        new DistributionPresenter(getActivity()).nodeList(type,p, new DistributionPresenter.CallBack3() {
            @Override
            public void send(NodeInfo baseInfo) {
                resetRefresh(isRefresh);
                if(isRefresh){
                    mHashMapList.clear();
                    page=1;
                    MessageEvent messageInfo = new MessageEvent(EventBusUtil.refresh_node_activity);
                    messageInfo.setNodeInfo(baseInfo);
                    EventBus.getDefault().post(messageInfo);
                }else{
                    page++;
                }
                if(baseInfo.getData().getLists().size()==10){
                    mRefreshLayout.setEnableLoadMore(true);
                }else{
                    mRefreshLayout.setEnableLoadMore(false);
                }
                mHashMapList.addAll(baseInfo.getData().getLists());
                mNodeAdapter.notifyDataSetChanged();
            }

            @Override
            public void error() {
                resetRefresh(isRefresh);
            }
        });
    }

    private void resetRefresh(boolean isRefresh){
        if(isRefresh){
            mRefreshLayout.finishRefresh();
        }else{
            mRefreshLayout.finishLoadMore();
        }
    }

    private void initAdapter() {
        mRecyclerView.setFocusable(false);
        mLayoutManager = new LinearLayoutManager(context);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mNodeAdapter=new NodeAdapter(context,mHashMapList);
        mRecyclerView.setAdapter(mNodeAdapter);
        mRefreshLayout.setEnableLoadMore(false);
        mNodeAdapter.setOnClickListener(new NodeAdapter.OnclickListener() {
            @Override
            public void onclick(int position) {

            }
        });
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                initHttp(true,1);
            }
        });
        mRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshLayout) {
                initHttp(false,page+1);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
