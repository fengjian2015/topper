package com.bclould.tocotalk.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bclould.tocotalk.base.MyApp;
import com.bclould.tocotalk.R;
import com.bclould.tocotalk.base.BaseActivity;
import com.bclould.tocotalk.ui.adapter.ProblemFeedBackPVAdapter;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by GA on 2017/10/17.
 */

public class ProblemFeedBackActivity extends BaseActivity {


    @Bind(R.id.bark)
    ImageView mBark;
    @Bind(R.id.viewPager)
    ViewPager mViewPager;
    @Bind(R.id.ll_menu)
    LinearLayout mLlMenu;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_problem_feedback);
        ButterKnife.bind(this);
        initInterface();
        MyApp.getInstance().addActivity(this);
    }

    private void initInterface() {

        setSelector(0);

        mViewPager.setCurrentItem(0);

        initBottomMenu();

        initViewPage();

    }

    private void initViewPage() {

        mViewPager.setAdapter(new ProblemFeedBackPVAdapter(getSupportFragmentManager()));

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

    //菜单选项选中处理
    private void setSelector(int index) {

        for (int i = 0; i < mLlMenu.getChildCount(); i++) {

            if (i == index) {

                mLlMenu.getChildAt(i).setSelected(true);

            } else {

                mLlMenu.getChildAt(i).setSelected(false);

            }
        }
    }

    private void initBottomMenu() {

        for (int i = 0; i < mLlMenu.getChildCount(); i++) {

            final View childAt = mLlMenu.getChildAt(i);

            childAt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    int index = mLlMenu.indexOfChild(childAt);

                    setSelector(index);

                    mViewPager.setCurrentItem(index);

                }
            });
        }
    }


    @OnClick(R.id.bark)
    public void onViewClicked() {

        finish();

    }
}
