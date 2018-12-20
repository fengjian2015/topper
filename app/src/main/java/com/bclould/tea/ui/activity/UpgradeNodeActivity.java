package com.bclould.tea.ui.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bclould.tea.Presenter.DistributionPresenter;
import com.bclould.tea.R;
import com.bclould.tea.base.BaseActivity;
import com.bclould.tea.model.UpgradeInfo;
import com.bclould.tea.ui.adapter.NodePagerAdapter;
import com.bclould.tea.ui.fragment.UpgradeFragment;
import com.bclould.tea.ui.widget.PWDDialog;
import com.bclould.tea.utils.EventBusUtil;
import com.bclould.tea.utils.MessageEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

@RequiresApi(api = Build.VERSION_CODES.N)
public class UpgradeNodeActivity extends BaseActivity {


    @Bind(R.id.tv_level)
    TextView mTvLevel;
    @Bind(R.id.tv_available)
    TextView mTvAvailable;
    @Bind(R.id.rl_current)
    LinearLayout mRlCurrent;
    @Bind(R.id.tl_tab)
    TabLayout mTlTab;
    @Bind(R.id.vp_content)
    ViewPager mVpContent;
    private PWDDialog pwdDialog;

    private int[] mStrings = new int[]{R.string.consensus_node, R.string.super_node, R.string.master_node};
    private UpgradeFragment consensusFragment;
    private UpgradeFragment superFragment;
    private UpgradeFragment lordFragment;
    private List<Integer> tabIndicators;
    private List<Fragment> tabFragments;
    private NodePagerAdapter mNodePagerAdapter;
    private UpgradeInfo mUpgradeInfo;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upgrade_node);
        ButterKnife.bind(this);
        setTitle(getString(R.string.upgrade_node),getString(R.string.purchase_history));
        EventBus.getDefault().register(this);
        init();
        initData();
        initContent();
        initHttp();
    }

    private void init() {
        mVpContent.setOffscreenPageLimit(4);
        mTlTab.setTabMode(TabLayout.GRAVITY_CENTER);
        mTlTab.setTabTextColors(ContextCompat.getColor(this, R.color.secondary_text_color), ContextCompat.getColor(this, R.color.btn_bg_color));
        mTlTab.setSelectedTabIndicatorColor(ContextCompat.getColor(this, R.color.btn_bg_color));
        ViewCompat.setElevation(mTlTab, 10);
        mTlTab.setupWithViewPager(mVpContent);
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

    private void initContent() {
        mNodePagerAdapter = new NodePagerAdapter(getSupportFragmentManager(), tabFragments, tabIndicators, this);
        mVpContent.setAdapter(mNodePagerAdapter);
        mTlTab.getTabAt(0).select();
    }

    private void initHttp(){
        new DistributionPresenter(this).nodeBuy(new DistributionPresenter.CallBack4() {
            @Override
            public void send(UpgradeInfo baseInfo) {
                mUpgradeInfo=baseInfo;
                consensusFragment.setUpgradeInfo(mUpgradeInfo);
                superFragment.setUpgradeInfo(mUpgradeInfo);
                lordFragment.setUpgradeInfo(mUpgradeInfo);
                setView();
            }

            @Override
            public void error() {

            }
        });
    }

    private void setView(){
        mTvLevel.setText(mUpgradeInfo.getData().getCurrent_node());
        mTvAvailable.setText(mUpgradeInfo.getData().getOver_num());
    }

    @OnClick({R.id.bark,R.id.tv_add})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;
            case R.id.tv_add:
                startActivity(new Intent(UpgradeNodeActivity.this,HistoryActivity.class));
                break;
        }
    }


    //接受通知
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        String msg = event.getMsg();
        if (msg.equals(EventBusUtil.refresh_upgrade)) {
            initHttp();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
