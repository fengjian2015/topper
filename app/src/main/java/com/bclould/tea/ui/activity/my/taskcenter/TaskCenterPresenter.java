package com.bclould.tea.ui.activity.my.taskcenter;

import android.app.Activity;
import android.content.Context;
import com.bclould.tea.Presenter.DistributionPresenter;
import com.bclould.tea.base.BaseView;
import com.bclould.tea.model.base.BaseListInfo;
import com.bclould.tea.ui.adapter.TaskCenterAdapter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by fengjian on 2019/1/8.
 */

public class TaskCenterPresenter implements TaskCenterContacts.Presenter {
    private TaskCenterContacts.View mView;
    private Activity mActivity;

    private TaskCenterAdapter mTaskCenterAdapter;
    List<HashMap> mHashMapList = new ArrayList<>();
    private int page = 1;

    private DistributionPresenter mDillDataPresenter;

    @Override
    public void bindView(BaseView view) {
        mView= (TaskCenterContacts.View) view;
    }

    @Override
    public <T extends Context> void start(T context) {
        mActivity= (Activity) context;
        mView.initView();
        initData();
        initRecyclerView();
        initHttp(true);
    }

    @Override
    public void release() {

    }

    private void initRecyclerView() {
        mTaskCenterAdapter = new TaskCenterAdapter(mActivity, mHashMapList);
        mView.setAdapter(mTaskCenterAdapter);
    }

    private void initData() {
        mDillDataPresenter = new DistributionPresenter(mActivity);
    }



    @Override
    public void initOptionPicker() {

    }

    public void initHttp(final boolean isRefresh) {
        int p = 1;
        if (isRefresh) {
            p = 1;
        } else {
            p = page + 1;
        }
        mDillDataPresenter.teamReward(p,"" , new DistributionPresenter.CallBack7() {
            @Override
            public void send(BaseListInfo data) {
                mView.resetRefresh(isRefresh);
                data.getData();
                if (data.getData().size() == 20) {
                    mView.setEnableLoadMore(true);
                } else {
                    mView.setEnableLoadMore(false);
                }
                if (isRefresh) {
                    page = 1;
                    mHashMapList.clear();
                } else {
                    page++;
                }
                if (data.getData().size() != 0) {
                    mHashMapList.addAll((List)data.getData());
                    mTaskCenterAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void error() {
                mView.resetRefresh(isRefresh);
            }
        });
    }
}
