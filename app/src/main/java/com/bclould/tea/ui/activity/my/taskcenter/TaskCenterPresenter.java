package com.bclould.tea.ui.activity.my.taskcenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.bclould.tea.Presenter.DistributionPresenter;
import com.bclould.tea.Presenter.TaskPersenter;
import com.bclould.tea.base.BaseView;
import com.bclould.tea.model.base.BaseInfoConstants;
import com.bclould.tea.model.base.BaseListInfo;
import com.bclould.tea.model.base.BaseMapInfo;
import com.bclould.tea.ui.activity.HTMLActivity;
import com.bclould.tea.ui.activity.MainActivity;
import com.bclould.tea.ui.adapter.TaskCenterAdapter;
import com.bclould.tea.utils.ActivityUtil;
import com.bclould.tea.utils.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by fengjian on 2019/1/8.
 */

public class TaskCenterPresenter implements TaskCenterContacts.Presenter {
    private TaskCenterContacts.View mView;
    private Activity mActivity;

    private TaskCenterAdapter mTaskCenterAdapter;
    List<Map> mHashMapList = new ArrayList<>();
    private int page = 1;

    @Override
    public void bindView(BaseView view) {
        mView= (TaskCenterContacts.View) view;
    }

    @Override
    public <T extends Context> void start(T context) {
        mActivity= (Activity) context;
        mView.initView();
        initRecyclerView();
        initHttp(true);
    }

    @Override
    public void release() {

    }

    private void initRecyclerView() {
        mTaskCenterAdapter = new TaskCenterAdapter(mActivity, mHashMapList);
        mView.setAdapter(mTaskCenterAdapter);
        mTaskCenterAdapter.addOnItemListener(new TaskCenterAdapter.OnItemListener() {
            @Override
            public void onItemClick(int position) {
                if("login".equals(mHashMapList.get(position).get(BaseInfoConstants.CODE)+"")) {
                    loginReward(mHashMapList.get(position).get(BaseInfoConstants.CODE)+"");
                }if("shop_login".equals(mHashMapList.get(position).get(BaseInfoConstants.CODE)+"")){
                    goShopping();
                }else{
                    try {
                        Class clazz = Class.forName(mHashMapList.get(position).get(BaseInfoConstants.ANDROID_URL)+"");
                        Intent intent = new Intent(mActivity,clazz);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        mActivity.startActivity(intent);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
    }

    private void goShopping(){
        Intent intent = new Intent(mActivity, HTMLActivity.class);
        intent.putExtra("html5Url", Constants.WEB_MALL);
        mActivity.startActivity(intent);
    }

    private void loginReward(String code){
        new TaskPersenter(mActivity).taskReward(code,true, new TaskPersenter.CallBack2() {
            @Override
            public void send(BaseMapInfo baseInfo) {
                if (!ActivityUtil.isActivityOnTop(mActivity)) return;
                initHttp(true);
            }

            @Override
            public void error() {

            }
        });
    }


    @Override
    public void initOptionPicker() {

    }

    public void initHttp(final boolean isRefresh) {
        new TaskPersenter(mActivity).taskLists(new TaskPersenter.CallBack1() {
            @Override
            public void send(BaseListInfo data) {
                mView.resetRefresh(isRefresh);
                mHashMapList.clear();
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
