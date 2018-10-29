package com.bclould.tea.ui.activity;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;


import com.bclould.tea.Presenter.DistributionPresenter;
import com.bclould.tea.R;
import com.bclould.tea.base.BaseActivity;
import com.bclould.tea.base.MyApp;
import com.bclould.tea.model.MyTeamInfo;
import com.bclould.tea.ui.adapter.MyTeamAdapter;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

@RequiresApi(api = Build.VERSION_CODES.N)
public class MyTeamActivity extends BaseActivity {


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

    private MyTeamAdapter mMyTeamAdapter;
    private List<MyTeamInfo.DataBean.UserListBean> mHashMapList=new ArrayList<>();
    private int user_id=0;
    private int page=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyApp.getInstance().addActivity(this);
        setContentView(R.layout.activity_my_team);
        ButterKnife.bind(this);
        initRecyclerView();
        initHttp(true,1);
    }

    private void initHttp(final boolean isRefresh,int p){
        new DistributionPresenter(this).myTeam(user_id,p, new DistributionPresenter.CallBack2() {
            @Override
            public void send(MyTeamInfo baseInfo) {
                initData(baseInfo,isRefresh);
                resetRecycler(isRefresh);
                if(baseInfo.getData().getUser_list().size()==10){
                    mRefreshLayout.setEnableLoadMore(true);
                }else{
                    mRefreshLayout.setEnableLoadMore(false);
                }
                if(isRefresh){
                    page=0;
                }else{
                    page++;
                }
            }

            @Override
            public void error() {
                resetRecycler(isRefresh);

            }
        });
    }

    private void resetRecycler(boolean isRefresh){
        if(isRefresh){
            mRefreshLayout.finishRefresh();
        }else{
            mRefreshLayout.finishLoadMore();
        }
    }

    private void initData(MyTeamInfo baseInfo, boolean isRefresh) {
        mTvTeamNumber.setText(baseInfo.getData().getTeam_member());
        mTvDirectDriveNumber.setText(baseInfo.getData().getPush_member());
        mTvPerformance.setText(baseInfo.getData().getPerformance());
        mTvTeamName.setText(baseInfo.getData().getUser_name());
        if(isRefresh){
            mHashMapList.clear();
        }
        mHashMapList.addAll(baseInfo.getData().getUser_list());
        mMyTeamAdapter.notifyDataSetChanged();
    }

    private void initRecyclerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mMyTeamAdapter = new MyTeamAdapter(this,mHashMapList);
        mMyTeamAdapter.setOnItemClick(new MyTeamAdapter.OnItemClick() {
            @Override
            public void next(int id) {
                user_id=id;
                initHttp(true,1);
            }

            @Override
            public void prev(int id) {
                user_id=id;
                initHttp(true,1);
            }
        });
        mRecyclerView.setAdapter(mMyTeamAdapter);
        mRefreshLayout.setEnableLoadMore(false);
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
                mRefreshLayout.finishLoadMore();
            }
        });
    }

    @OnClick({R.id.bark,R.id.tv_break})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;
            case R.id.tv_break:
                user_id=0;
                initHttp(true,1);
                break;
        }
    }

}
