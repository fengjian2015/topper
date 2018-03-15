package com.bclould.tocotalk.ui.activity;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bclould.tocotalk.R;
import com.bclould.tocotalk.base.BaseActivity;
import com.bclould.tocotalk.base.MyApp;
import com.bclould.tocotalk.ui.adapter.MyAssetsVPAdapter;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by GA on 2017/9/22.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class MyAssetsActivity extends BaseActivity {


    @Bind(R.id.bark)
    ImageView mBark;
    @Bind(R.id.xx)
    TextView mXx;
    @Bind(R.id.xx2)
    TextView mXx2;
    @Bind(R.id.top_menu)
    LinearLayout mTopMenu;
    @Bind(R.id.viewPager)
    ViewPager mViewPager;
    @Bind(R.id.xx3)
    TextView mXx3;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_my_assets);

        ButterKnife.bind(this);

        //初始化界面
        initInterface();

        MyApp.getInstance().addActivity(this);

    }


    //初始化界面
    private void initInterface() {
        mViewPager.setCurrentItem(0);
        setSelector(0);
        initTopMenu();
        initViewPager();
    }

    //初始化ViewPager
    private void initViewPager() {

        mViewPager.setAdapter(new MyAssetsVPAdapter(getSupportFragmentManager()));

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

    //初始化菜单
    private void initTopMenu() {

        for (int i = 0; i < mTopMenu.getChildCount(); i++) {

            final View childAt = mTopMenu.getChildAt(i);

            childAt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    int index = mTopMenu.indexOfChild(childAt);

                    setSelector(index);

                    mViewPager.setCurrentItem(index);

                }
            });


        }

    }

    //菜单选项选中处理
    private void setSelector(int index) {
        for (int i = 0; i < mTopMenu.getChildCount(); i++) {

            if (i == index) {

                mTopMenu.getChildAt(i).setSelected(true);

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

            } else {

                mTopMenu.getChildAt(i).setSelected(false);

            }
        }
    }

    //点击事件的处理
    //点击事件的处理
    @OnClick({R.id.bark})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:

                finish();

                break;
            /*case R.id.tv_bank_card:

                startActivity(new Intent(this, BankCardActivity.class));

                break;*/
        }
    }
}
