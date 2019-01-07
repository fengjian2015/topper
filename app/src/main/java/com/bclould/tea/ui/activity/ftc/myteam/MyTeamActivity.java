package com.bclould.tea.ui.activity.ftc.myteam;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.bclould.tea.R;
import com.bclould.tea.base.BaseActivity;
import com.bclould.tea.model.MyTeamInfo;
import com.bclould.tea.ui.activity.my.teamreward.TeamRewardActivity;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MyTeamActivity extends BaseActivity implements MyTeamContacts.View {


    @Bind(R.id.tv_team_number)
    TextView mTvTeamNumber;
    @Bind(R.id.tv_direct_drive_number)
    TextView mTvDirectDriveNumber;
    @Bind(R.id.tv_performance)
    TextView mTvPerformance;
    @Bind(R.id.tv_team_name)
    TextView mTvTeamName;
    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @Bind(R.id.refresh_layout)
    SmartRefreshLayout mRefreshLayout;
    @Bind(R.id.tv_break)
    TextView mTvBreak;
    @Bind(R.id.tv_team_push_reward)
    TextView mTvTeamPushReward;

    private MyTeamContacts.Presenter mMyTeamPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_team);
        ButterKnife.bind(this);
        mMyTeamPresenter = new MyTeamPresenter();
        mMyTeamPresenter.bindView(this);
        mMyTeamPresenter.start(this);

    }

    @Override
    public void initView() {
        setTitle(getString(R.string.my_team));
        initRecyclerView();
    }

    private void initRecyclerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mMyTeamPresenter.initRecyclerView();
        mRefreshLayout.setEnableLoadMore(false);
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                mMyTeamPresenter.initHttp(true, 1);
            }
        });

        mRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshLayout) {
                mMyTeamPresenter.initHttp(false, mMyTeamPresenter.getPage() + 1);
                mRefreshLayout.finishLoadMore();
            }
        });
        mRecyclerView.setAdapter(mMyTeamPresenter.getMyTeamAdapter());
    }

    @OnClick({R.id.bark, R.id.tv_break,R.id.rl_reward})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;
            case R.id.tv_break:
                mMyTeamPresenter.setUserId(0);
                mMyTeamPresenter.initHttp(true, 1);
                break;
            case R.id.rl_reward:
                startActivity(new Intent(this, TeamRewardActivity.class));
                break;
        }
    }

    @Override
    public void setEnableLoadMore(boolean istrue) {
        mRefreshLayout.setEnableLoadMore(istrue);
    }

    @Override
    public void setResetRecycler(boolean isRefresh) {
        if (isRefresh) {
            mRefreshLayout.finishRefresh();
        } else {
            mRefreshLayout.finishLoadMore();
        }
    }

    @Override
    public void setNumberView(MyTeamInfo baseInfo) {
        mTvTeamNumber.setText(baseInfo.getData().getTeam_member());
        mTvDirectDriveNumber.setText(baseInfo.getData().getPush_member());
        mTvPerformance.setText(baseInfo.getData().getPerformance());
        mTvTeamName.setText(baseInfo.getData().getUser_name());
        mTvTeamPushReward.setText(baseInfo.getData().getTeam_push_reward());
    }
}
