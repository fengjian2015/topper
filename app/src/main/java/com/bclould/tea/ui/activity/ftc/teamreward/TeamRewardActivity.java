package com.bclould.tea.ui.activity.ftc.teamreward;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bclould.tea.R;
import com.bclould.tea.base.BaseActivity;
import com.bclould.tea.ui.adapter.TeamRewardAdapter;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TeamRewardActivity extends BaseActivity implements TeamRewardContacts.View{

    @Bind(R.id.iv_more)
    ImageView mIvMore;
    @Bind(R.id.tv_date)
    TextView mTvDate;
    @Bind(R.id.rl_date_selector)
    RelativeLayout mRlDateSelector;
    @Bind(R.id.tv_hint)
    TextView mTvHint;
    @Bind(R.id.ll_no_data)
    LinearLayout mLlNoData;
    @Bind(R.id.ll_error)
    LinearLayout mLlError;
    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @Bind(R.id.refresh_layout)
    SmartRefreshLayout mRefreshLayout;

    private TeamRewardContacts.Presenter mPresenter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_reward);
        ButterKnife.bind(this);
        mPresenter=new TeamRewardPresenter();
        mPresenter.bindView(this);
        mPresenter.start(this);
    }

    @Override
    public void initView() {
        setTitle(getString(R.string.team_direct_reward_record));
        initRecyclerView();
    }

    private void initRecyclerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRefreshLayout.setEnableLoadMore(false);
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                mPresenter.initHttp(true);
            }
        });
        mRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshLayout) {
                mPresenter.initHttp(false);
            }
        });
    }

    @OnClick({R.id.bark, R.id.rl_date_selector, R.id.ll_error})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;
            case R.id.rl_date_selector:
                mPresenter.initOptionPicker();
                break;
            case R.id.ll_error:
                mPresenter.initHttp(true);
                break;
        }
    }

    @Override
    public void setDateView(String time) {
        mTvDate.setText(time);
    }

    @Override
    public void setAdapter(TeamRewardAdapter adapter) {
        mRecyclerView.setAdapter(adapter);
    }

    public void resetRefresh(boolean isRefresh) {
        if (isRefresh) {
            mRefreshLayout.finishRefresh();
        } else {
            mRefreshLayout.finishLoadMore();
        }
    }

    @Override
    public void setEnableLoadMore(boolean istrue) {
        mRefreshLayout.setEnableLoadMore(istrue);
    }

    @Override
    public void setViewIsGone(int isVmRecyclerView, int isVmLlNoData, int isVmLlError) {
        mRecyclerView.setVisibility(isVmRecyclerView);
        mLlNoData.setVisibility(isVmLlNoData);
        mLlError.setVisibility(isVmLlError);
    }

    @Override
    protected void onDestroy() {
        mPresenter.release();
        super.onDestroy();
    }
}
