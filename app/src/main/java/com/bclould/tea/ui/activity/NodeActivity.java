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
import android.support.v4.widget.NestedScrollView;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bclould.tea.R;
import com.bclould.tea.base.BaseActivity;
import com.bclould.tea.base.MyApp;
import com.bclould.tea.model.NodeInfo;
import com.bclould.tea.ui.adapter.NodePagerAdapter;
import com.bclould.tea.ui.fragment.FreedFragment;
import com.bclould.tea.utils.Constants;
import com.bclould.tea.utils.EventBusUtil;
import com.bclould.tea.utils.MessageEvent;
import com.bclould.tea.utils.UtilTool;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

@RequiresApi(api = Build.VERSION_CODES.N)
public class NodeActivity extends BaseActivity {
    @Bind(R.id.tv_all)
    TextView mTvAll;
    @Bind(R.id.tv1)
    TextView mTv1;
    @Bind(R.id.tv_money)
    TextView mTvMoney;
    @Bind(R.id.tv_level)
    TextView mTvLevel;
    @Bind(R.id.tv_data)
    TextView mTvData;
    @Bind(R.id.tv_total_performance)
    TextView mTvTotalPerformance;
    @Bind(R.id.tv_node_revenue)
    TextView mTvNodeRevenue;
    @Bind(R.id.tv_super_dividend)
    TextView mTvSuperDividend;
    @Bind(R.id.tv_main_node)
    TextView mTvMainNode;
    @Bind(R.id.ll_advanced)
    LinearLayout mLlAdvanced;
    @Bind(R.id.tl_tab)
    TabLayout mTlTab;
    @Bind(R.id.vp_content)
    ViewPager mVpContent;
    @Bind(R.id.scrollView)
    NestedScrollView mScrollView;

    private List<Integer> tabIndicators;
    private List<Fragment> tabFragments;
    private NodePagerAdapter mNodePagerAdapter;
    private int[] mStrings = new int[]{R.string.freed, R.string.income, R.string.dividend};
    private float myHeigth;
    FreedFragment releaseFragment;
    FreedFragment earningsFragment;
    FreedFragment participationFragment;
    private NodeInfo mNodeInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_node);
        MyApp.getInstance().addActivity(this);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        initData();
        init();
        initContent();
    }

    private void init() {
        //2、通过Resources获取
        DisplayMetrics dm = getResources().getDisplayMetrics();
        myHeigth = dm.heightPixels;
        mVpContent.setOffscreenPageLimit(4);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mVpContent.getLayoutParams();
        layoutParams.height = (int) (myHeigth - getResources().getDimensionPixelSize(R.dimen.y200) - UtilTool.getStateBar3(this));
        mVpContent.setLayoutParams(layoutParams);
        mTlTab.setTabMode(TabLayout.GRAVITY_CENTER);
        mTlTab.setTabTextColors(ContextCompat.getColor(this, R.color.secondary_text_color), ContextCompat.getColor(this, R.color.btn_bg_color));
        mTlTab.setSelectedTabIndicatorColor(ContextCompat.getColor(this, R.color.btn_bg_color));
        ViewCompat.setElevation(mTlTab, 10);
        mTlTab.setupWithViewPager(mVpContent);
    }


    private void initData(){
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
    }

    private void initContent() {

        mNodePagerAdapter = new NodePagerAdapter(getSupportFragmentManager(), tabFragments, tabIndicators, this);
        mVpContent.setAdapter(mNodePagerAdapter);
        mTlTab.getTabAt(0).select();
    }

    @OnClick({R.id.bark, R.id.iv_right,R.id.rl_upgrade})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;
            case R.id.iv_right:
                Intent intent = new Intent(this, HTMLActivity.class);
                intent.putExtra("html5Url", Constants.BASE_URL+"team/node/level/desc");
                startActivity(intent);
                break;
            case R.id.rl_upgrade:
                startActivity(new Intent(NodeActivity.this,UpgradeNodeActivity.class));
                break;
        }
    }


    //接受通知
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        String msg = event.getMsg();
        if (msg.equals(EventBusUtil.refresh_node_activity)) {
            mNodeInfo=event.getNodeInfo();
            setView();
        }
    }

    private void setView(){
        mTvMoney.setText(mNodeInfo.getData().getTotal_income());
        mTvLevel.setText(mNodeInfo.getData().getLevel());
        mTvData.setText(getString(R.string.freed)+":"+mNodeInfo.getData().getFreed()+"  "+
        getString(R.string.income)+":"+mNodeInfo.getData().getIncome()+"  "+
        getString(R.string.dividend)+":"+mNodeInfo.getData().getDividend());
        mTvTotalPerformance.setText(mNodeInfo.getData().getTotal_performance());
        mTvNodeRevenue.setText(mNodeInfo.getData().getNode_income());
        mTvSuperDividend.setText(mNodeInfo.getData().getSuper_dividend());
        mTvMainNode.setText(mNodeInfo.getData().getMain_super_dividend());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
