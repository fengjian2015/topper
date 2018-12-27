package com.bclould.tea.ui.activity.ftc.Node;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;

import com.bclould.tea.R;
import com.bclould.tea.base.BaseView;
import com.bclould.tea.model.NodeInfo;
import com.bclould.tea.ui.adapter.NodePagerAdapter;
import com.bclould.tea.ui.fragment.FreedFragment;
import com.bclould.tea.utils.EventBusUtil;
import com.bclould.tea.utils.MessageEvent;
import com.bclould.tea.utils.UtilTool;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by GIjia on 2018/12/27.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class NodePresenter implements NodeContacts.Presenter{
    private NodeContacts.View mView;
    private Activity mActivity;

    FreedFragment releaseFragment;
    FreedFragment earningsFragment;
    FreedFragment participationFragment;

    private NodeInfo mNodeInfo;
    private List<Integer> tabIndicators;
    private List<Fragment> tabFragments;
    private NodePagerAdapter mNodePagerAdapter;
    private int[] mStrings = new int[]{R.string.freed, R.string.income, R.string.dividend};

    @Override
    public void bindView(BaseView view) {
        mView= (NodeContacts.View) view;
    }

    @Override
    public <T extends Context> void start(T context) {
        mActivity= (Activity) context;
        initData();
        mView.initView();
        mView.setAdapter();
        EventBus.getDefault().register(this);
    }

    @Override
    public void release() {
        EventBus.getDefault().unregister(this);
    }

    public void initData(){
        tabIndicators = new ArrayList<>();
        for (int i = 0; i < mStrings.length; i++) {
            tabIndicators.add(mStrings[i]);
        }
        tabFragments = new ArrayList<>();
        releaseFragment= new FreedFragment();
        releaseFragment.setType(1);
        tabFragments.add(releaseFragment);

        earningsFragment=new FreedFragment();
        earningsFragment.setType(2);
        tabFragments.add(earningsFragment);

        participationFragment=new FreedFragment();
        participationFragment.setType(3);
        tabFragments.add(participationFragment);
        mNodePagerAdapter = new NodePagerAdapter(mView.getFm(), tabFragments, tabIndicators, mActivity);

    }

    //接受通知
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        String msg = event.getMsg();
        if (msg.equals(EventBusUtil.refresh_node_activity)) {
            mNodeInfo=event.getNodeInfo();
            mView.setViewData(mNodeInfo);
        }
    }

    private float getMyHeigth(){
        DisplayMetrics dm = mActivity.getResources().getDisplayMetrics();
        float myHeigth = dm.heightPixels;
        return myHeigth;
    }

    @Override
    public int getVpContentHight(){
        return (int) (getMyHeigth() - mActivity.getResources().getDimensionPixelSize(R.dimen.y200) - UtilTool.getStateBar3(mActivity));
    }

    @Override
    public NodePagerAdapter getAdapter() {
        return mNodePagerAdapter;
    }

    @Override
    public NodeInfo getNodeInfo() {
        return mNodeInfo;
    }
}
