package com.bclould.tea.ui.activity.ftc.node;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bclould.tea.R;
import com.bclould.tea.base.BaseActivity;
import com.bclould.tea.model.NodeInfo;
import com.bclould.tea.ui.activity.HTMLActivity;
import com.bclould.tea.ui.activity.ftc.upgradenode.UpgradeNodeActivity;
import com.bclould.tea.utils.Constants;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

@RequiresApi(api = Build.VERSION_CODES.N)
public class NodeActivity extends BaseActivity implements NodeContacts.View{
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


    private NodeContacts.Presenter mNodePresenter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_node);
        ButterKnife.bind(this);
        mNodePresenter=new NodePresenter();
        mNodePresenter.bindView(this);
        mNodePresenter.start(this);

    }

    @Override
    public void initView() {
        //2、通过Resources获取
        mVpContent.setOffscreenPageLimit(4);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mVpContent.getLayoutParams();
        layoutParams.height = mNodePresenter.getVpContentHight();
        mVpContent.setLayoutParams(layoutParams);
        mTlTab.setTabMode(TabLayout.GRAVITY_CENTER);
        mTlTab.setTabTextColors(ContextCompat.getColor(this, R.color.secondary_text_color), ContextCompat.getColor(this, R.color.btn_bg_color));
        mTlTab.setSelectedTabIndicatorColor(ContextCompat.getColor(this, R.color.btn_bg_color));
        ViewCompat.setElevation(mTlTab, 10);
        mTlTab.setupWithViewPager(mVpContent);

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


    @Override
    public void setViewData(NodeInfo mNodeInfo) {
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
        mNodePresenter.release();
    }

    @Override
    public FragmentManager getFm() {
        return getSupportFragmentManager();
    }

    @Override
    public void setAdapter() {
        mVpContent.setAdapter(mNodePresenter.getAdapter());
        mTlTab.getTabAt(0).select();
    }

}
