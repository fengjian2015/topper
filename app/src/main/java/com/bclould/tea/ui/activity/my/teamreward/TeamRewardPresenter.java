package com.bclould.tea.ui.activity.my.teamreward;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import com.bclould.tea.Presenter.DistributionPresenter;
import com.bclould.tea.base.BaseView;
import com.bclould.tea.model.base.BaseListInfo;
import com.bclould.tea.ui.adapter.TeamRewardAdapter;
import com.bclould.tea.utils.TimeSelectUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by fengjian on 2019/1/7.
 */

public class TeamRewardPresenter implements TeamRewardContacts.Presenter, TimeSelectUtil.OnTimeReturnListener {
    private TeamRewardContacts.View mView;
    private Activity mActivity;

    private TeamRewardAdapter mTeamRewardAdapter;
    List<HashMap> mInOutList = new ArrayList<>();
    private TimeSelectUtil mTimeSelectUtil;

    String mDate = "";
    private int page = 1;

    private DistributionPresenter mDillDataPresenter;

    @Override
    public void bindView(BaseView view) {
        mView = (TeamRewardContacts.View) view;
    }

    @Override
    public <T extends Context> void start(T context) {
        mActivity = (Activity) context;
        mView.initView();
        initData();
        initRecyclerView();
        initHttp(true);
    }

    @Override
    public void release() {
        if (mTimeSelectUtil == null) return;
        mTimeSelectUtil.dismiss();
    }

    private void initRecyclerView() {
        mTeamRewardAdapter = new TeamRewardAdapter(mActivity, mInOutList);
        mView.setAdapter(mTeamRewardAdapter);
    }

    private void initData() {
        mTimeSelectUtil = new TimeSelectUtil(mActivity, 3);
        mTimeSelectUtil.setOnTimeReturnListener(this);
        mDate = mTimeSelectUtil.getDate();
        mView.setDateView(mDate);
        mDillDataPresenter = new DistributionPresenter(mActivity);
    }


    public void initHttp(final boolean isRefresh) {
        int p = 1;
        if (isRefresh) {
            p = 1;
        } else {
            p = page + 1;
        }
        mDillDataPresenter.teamReward(p, mDate, new DistributionPresenter.CallBack7() {
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
                    mInOutList.clear();
                } else {
                    page++;
                }
                if (data.getData().size() != 0) {
                    mView.setViewIsGone(View.VISIBLE,View.GONE,View.GONE);
                    mInOutList.addAll((List)data.getData());
                    mTeamRewardAdapter.notifyDataSetChanged();
                } else {
                    mView.setViewIsGone(View.GONE,View.VISIBLE,View.GONE);
                }
            }

            @Override
            public void error() {
                mView.resetRefresh(isRefresh);
                mView.setViewIsGone(View.GONE,View.GONE,View.VISIBLE);
            }
        });
    }

    @Override
    public void getTime(String time) {
        mDate = time;
        mView.setDateView(mDate);
        initHttp(true);
    }

    @Override
    public void initOptionPicker() {
        if (mTimeSelectUtil == null) return;
        mTimeSelectUtil.initOptionPicker();
    }
}
