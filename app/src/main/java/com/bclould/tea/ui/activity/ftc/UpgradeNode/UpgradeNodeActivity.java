package com.bclould.tea.ui.activity.ftc.UpgradeNode;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.bclould.tea.R;
import com.bclould.tea.base.BaseActivity;
import com.bclould.tea.model.UpgradeInfo;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

@RequiresApi(api = Build.VERSION_CODES.N)
public class UpgradeNodeActivity extends BaseActivity implements UpgradeNodeContacts.View{


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

    private UpgradeNodeContacts.Presenter mPresenter;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upgrade_node);
        ButterKnife.bind(this);
        mPresenter=new UpgradeNodePresenter();
        mPresenter.bindView(this);
        mPresenter.start(this);
    }

    @Override
    public void initView() {
        setTitle(getString(R.string.upgrade_node),getString(R.string.purchase_history));
        mVpContent.setOffscreenPageLimit(4);
        mTlTab.setTabMode(TabLayout.GRAVITY_CENTER);
        mTlTab.setTabTextColors(ContextCompat.getColor(this, R.color.secondary_text_color), ContextCompat.getColor(this, R.color.btn_bg_color));
        mTlTab.setSelectedTabIndicatorColor(ContextCompat.getColor(this, R.color.btn_bg_color));
        ViewCompat.setElevation(mTlTab, 10);
        mTlTab.setupWithViewPager(mVpContent);
    }

    @OnClick({R.id.bark,R.id.tv_add})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;
            case R.id.tv_add:
                mPresenter.goHistoryActivity();
                break;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.release();
    }

    @Override
    public FragmentManager getFm() {
        return getSupportFragmentManager();
    }

    @Override
    public void setAdapter() {
        mVpContent.setAdapter(mPresenter.getAdapter());
        mTlTab.getTabAt(0).select();
    }

    @Override
    public void setView(UpgradeInfo mUpgradeInfo) {
        mTvLevel.setText(mUpgradeInfo.getData().getCurrent_node());
        mTvAvailable.setText(mUpgradeInfo.getData().getOver_num());
    }
}
