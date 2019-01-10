package com.bclould.tea.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.bclould.tea.R;
import com.bclould.tea.base.BaseActivity;
import com.bclould.tea.ui.adapter.GonggaoManagerPVAdapter;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by GA on 2018/5/9.
 */

public class GonggaoManagerActivity extends BaseActivity {

    @Bind(R.id.bark)
    ImageView mBark;
    @Bind(R.id.xx)
    TextView mXx;
    @Bind(R.id.xx2)
    TextView mXx2;
    @Bind(R.id.xx3)
    TextView mXx3;
    @Bind(R.id.ll_menu)
    LinearLayout mLlMenu;
    @Bind(R.id.view_pager)
    ViewPager mViewPager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gong_gao);
        ButterKnife.bind(this);
        setTitle(getString(R.string.system_notification));
        mViewPager.setCurrentItem(0);
        setSelector(0);
        initTopMenu();
        initViewPager();
    }
    private void setSelector(int index) {
        switch (index) {
            case 0:
                mXx.setVisibility(View.VISIBLE);
                mXx2.setVisibility(View.INVISIBLE);
                mXx3.setVisibility(View.INVISIBLE);
                break;
            case 1:
                mXx.setVisibility(View.INVISIBLE);
                mXx2.setVisibility(View.VISIBLE);
                mXx3.setVisibility(View.INVISIBLE);
                break;
            case 2:
                mXx.setVisibility(View.INVISIBLE);
                mXx2.setVisibility(View.INVISIBLE);
                mXx3.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void initTopMenu() {
        for (int i = 0; i < mLlMenu.getChildCount(); i++) {
            View childAt = mLlMenu.getChildAt(i);
            childAt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int index = mLlMenu.indexOfChild(view);
                    setSelector(index);
                    mViewPager.setCurrentItem(index);
                }
            });
        }
    }

    private void initViewPager() {
        GonggaoManagerPVAdapter gonggaoManagerPVAdapter = new GonggaoManagerPVAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(gonggaoManagerPVAdapter);
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setSelector(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @OnClick(R.id.bark)
    public void onViewClicked() {
        finish();
    }
}
