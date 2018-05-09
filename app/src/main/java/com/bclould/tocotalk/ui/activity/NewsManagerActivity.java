package com.bclould.tocotalk.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bclould.tocotalk.R;
import com.bclould.tocotalk.base.BaseActivity;
import com.bclould.tocotalk.ui.adapter.NewsManagerPVAdapter;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by GA on 2018/5/8.
 */

public class NewsManagerActivity extends BaseActivity {
    @Bind(R.id.bark)
    ImageView mBark;
    @Bind(R.id.tv_push)
    TextView mTvPush;
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
        setContentView(R.layout.activity_news_manager);
        ButterKnife.bind(this);
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
        NewsManagerPVAdapter newsManagerPVAdapter = new NewsManagerPVAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(newsManagerPVAdapter);
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

    @OnClick({R.id.bark, R.id.tv_push})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;
            case R.id.tv_push:
                startActivity(new Intent(this, NewsEditActivity.class));
                break;
        }
    }
}
