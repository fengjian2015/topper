package com.bclould.tea.ui.activity;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.view.View;
import com.bclould.tea.R;
import com.bclould.tea.base.BaseActivity;
import com.bclould.tea.ui.adapter.NodePagerAdapter;
import com.bclould.tea.ui.fragment.TransferRecordFragment;
import java.util.ArrayList;
import java.util.List;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

@RequiresApi(api = Build.VERSION_CODES.N)
public class TransferRecordActivity extends BaseActivity {

    @Bind(R.id.tl_tab)
    TabLayout mTlTab;
    @Bind(R.id.vp_content)
    ViewPager mVpContent;

    private List<Integer> tabIndicators;
    private List<Fragment> tabFragments;
    private NodePagerAdapter mNodePagerAdapter;
    private int[] mStrings = new int[]{R.string.all, R.string.shift_to, R.string.transfer_out};
    TransferRecordFragment allFragment;
    TransferRecordFragment toFragment;
    TransferRecordFragment outFragment;
    private int coin_id;
    private String coin_name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer_record);
        ButterKnife.bind(this);
        initIntent();
        init();
        initData();
    }

    private void initIntent() {
        coin_id = getIntent().getIntExtra("coin_id", coin_id);
        coin_name = getIntent().getStringExtra("coin_name");
    }

    private void init() {
        mVpContent.setOffscreenPageLimit(3);
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
        allFragment= new TransferRecordFragment();
        allFragment.setData(0,coin_id,coin_name);
        tabFragments.add(allFragment);

        toFragment=new TransferRecordFragment();
        toFragment.setData(1,coin_id,coin_name);
        tabFragments.add(toFragment);

        outFragment=new TransferRecordFragment();
        outFragment.setData(2,coin_id,coin_name);
        tabFragments.add(outFragment);

        mNodePagerAdapter = new NodePagerAdapter(getSupportFragmentManager(), tabFragments, tabIndicators, this);
        mVpContent.setAdapter(mNodePagerAdapter);
        mTlTab.getTabAt(0).select();
    }

    @OnClick({R.id.bark})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;
        }
    }

}
