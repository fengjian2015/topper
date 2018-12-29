package com.bclould.tea.ui.activity.ftc.myteam;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.bclould.tea.Presenter.DistributionPresenter;
import com.bclould.tea.base.BaseView;
import com.bclould.tea.model.MyTeamInfo;
import com.bclould.tea.ui.adapter.MyTeamAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by GIjia on 2018/12/25.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class MyTeamPresenter implements MyTeamContacts.Presenter{
    private MyTeamContacts.View mView;
    private Activity mActivity;

    private MyTeamAdapter mMyTeamAdapter;
    private List<MyTeamInfo.DataBean.UserListBean> mHashMapList=new ArrayList<>();
    private int user_id=0;
    private int page=1;
    @Override
    public void bindView(BaseView view) {
        mView= (MyTeamContacts.View) view;
    }

    @Override
    public <T extends Context> void start(T context) {
        mActivity= (Activity) context;
        mView.initView();
        initHttp(true,1);
    }

    @Override
    public void release() {

    }

    @Override
    public void initRecyclerView(){
        mMyTeamAdapter = new MyTeamAdapter(mActivity,mHashMapList);
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

    }

    public void initHttp(final boolean isRefresh,int p){
        new DistributionPresenter(mActivity).myTeam(user_id,p, new DistributionPresenter.CallBack2() {
            @Override
            public void send(MyTeamInfo baseInfo) {
                initData(baseInfo,isRefresh);
                resetRecycler(isRefresh);
                if(baseInfo.getData().getUser_list().size()==10){
                    mView.setEnableLoadMore(true);
                }else{
                    mView.setEnableLoadMore(false);
                }
                if(isRefresh){
                    page=1;
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

    private void initData(MyTeamInfo baseInfo, boolean isRefresh) {
        mView.setNumberView(baseInfo);
        if(isRefresh){
            mHashMapList.clear();
        }
        mHashMapList.addAll(baseInfo.getData().getUser_list());
        mMyTeamAdapter.notifyDataSetChanged();
    }


    private void resetRecycler(boolean isRefresh){
        mView.setResetRecycler(isRefresh);
    }

    @Override
    public int getPage() {
        return page;
    }

    @Override
    public MyTeamAdapter getMyTeamAdapter() {
        return mMyTeamAdapter;
    }

    @Override
    public void setUserId(int userId) {
        this.user_id=userId;
    }
}
