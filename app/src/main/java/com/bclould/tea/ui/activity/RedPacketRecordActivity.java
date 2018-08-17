package com.bclould.tea.ui.activity;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bclould.tea.R;
import com.bclould.tea.base.BaseActivity;
import com.bclould.tea.base.MyApp;
import com.bclould.tea.ui.adapter.RedRecordVPAdapter;
import com.bclould.tea.utils.AppLanguageUtils;
import com.bclould.tea.utils.MySharedPreferences;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by GA on 2018/1/3.
 */

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class RedPacketRecordActivity extends BaseActivity {
    @Bind(R.id.bark)
    ImageView mBark;
    @Bind(R.id.xx)
    TextView mXx;
    @Bind(R.id.xx2)
    TextView mXx2;
    @Bind(R.id.ll_menu)
    LinearLayout mLlMenu;
    @Bind(R.id.tv_redpacket_record)
    TextView mTvRedpacketRecord;
    @Bind(R.id.title)
    RelativeLayout mTitle;
    @Bind(R.id.viewPager)
    ViewPager mViewPager;
    private RedRecordVPAdapter mRedRecordVPAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(getResources().getColor(R.color.redpacket4));
        setContentView(R.layout.activity_red_packet_record);
        ButterKnife.bind(this);
        MyApp.getInstance().addActivity(this);
        setSelector(0);
        initTopMenu();
        initViewPager();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(AppLanguageUtils.attachBaseContext(newBase, MySharedPreferences.getInstance().getString(newBase.getString(R.string.language_pref_key))));
    }

    private void initViewPager() {
        mRedRecordVPAdapter = new RedRecordVPAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mRedRecordVPAdapter);
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

    //菜单选项选中处理
    private void setSelector(int index) {

        if (index == 0) {
            mLlMenu.getChildAt(index).setSelected(true);
            mLlMenu.getChildAt(1).setSelected(false);
            mXx.setVisibility(View.VISIBLE);
            mXx2.setVisibility(View.GONE);
        } else {
            mLlMenu.getChildAt(index).setSelected(true);
            mLlMenu.getChildAt(0).setSelected(false);
            mXx.setVisibility(View.GONE);
            mXx2.setVisibility(View.VISIBLE);

        }

    }

    @OnClick(R.id.bark)
    public void onViewClicked() {
        finish();
    }
}
