package com.bclould.tea.ui.activity.ftc.upgradenode;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;

import com.bclould.tea.Presenter.DistributionPresenter;
import com.bclould.tea.R;
import com.bclould.tea.base.BaseView;
import com.bclould.tea.model.UpgradeInfo;
import com.bclould.tea.ui.activity.HistoryActivity;
import com.bclould.tea.ui.adapter.NodePagerAdapter;
import com.bclould.tea.ui.fragment.UpgradeFragment;
import com.bclould.tea.utils.EventBusUtil;
import com.bclould.tea.utils.MessageEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by GIjia on 2018/12/27.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class UpgradeNodePresenter implements UpgradeNodeContacts.Presenter{
    private Activity mActivity;
    private UpgradeNodeContacts.View mView;


    private int[] mStrings = new int[]{R.string.consensus_node, R.string.super_node, R.string.master_node};
    private UpgradeFragment consensusFragment;
    private UpgradeFragment superFragment;
    private UpgradeFragment lordFragment;
    private List<Integer> tabIndicators;
    private List<Fragment> tabFragments;

    private NodePagerAdapter mNodePagerAdapter;
    private UpgradeInfo mUpgradeInfo;
    @Override
    public void bindView(BaseView view) {
        mView= (UpgradeNodeContacts.View) view;
    }

    @Override
    public <T extends Context> void start(T context) {
        mActivity= (Activity) context;
        mView.initView();
        initData();
        initAdapter();
        initHttp();
        EventBus.getDefault().register(this);
    }

    @Override
    public void release() {
        EventBus.getDefault().unregister(this);
    }

    private void initData() {
        tabIndicators = new ArrayList<>();
        for (int i = 0; i < mStrings.length; i++) {
            tabIndicators.add(mStrings[i]);
        }
        tabFragments = new ArrayList<>();
        consensusFragment = new UpgradeFragment();
        consensusFragment.setType(1);
        tabFragments.add(consensusFragment);

        superFragment = new UpgradeFragment();
        superFragment.setType(2);
        tabFragments.add(superFragment);

        lordFragment = new UpgradeFragment();
        lordFragment.setType(3);
        tabFragments.add(lordFragment);
    }

    private void initHttp(){
        new DistributionPresenter(mActivity).nodeBuy(new DistributionPresenter.CallBack4() {
            @Override
            public void send(UpgradeInfo baseInfo) {
                mUpgradeInfo=baseInfo;
                consensusFragment.setUpgradeInfo(mUpgradeInfo);
                superFragment.setUpgradeInfo(mUpgradeInfo);
                lordFragment.setUpgradeInfo(mUpgradeInfo);

            }

            @Override
            public void error() {

            }
        });
    }

    //接受通知
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        String msg = event.getMsg();
        if (msg.equals(EventBusUtil.refresh_upgrade)) {
            initHttp();
        }
    }

    private void initAdapter(){
        mNodePagerAdapter = new NodePagerAdapter(mView.getFm(), tabFragments, tabIndicators, mActivity);
        mView.setAdapter();
    }

    @Override
    public NodePagerAdapter getAdapter() {
        return mNodePagerAdapter;
    }

    @Override
    public void goHistoryActivity() {
        mActivity.startActivity(new Intent(mActivity,HistoryActivity.class));
    }
}
