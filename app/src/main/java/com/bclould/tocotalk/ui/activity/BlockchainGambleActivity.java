package com.bclould.tocotalk.ui.activity;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bclould.tocotalk.R;
import com.bclould.tocotalk.base.BaseActivity;
import com.bclould.tocotalk.ui.adapter.BlockchainGambleVPAdapter;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by GA on 2018/4/23.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class BlockchainGambleActivity extends BaseActivity {
    @Bind(R.id.bark)
    ImageView mBark;
    @Bind(R.id.tv_title)
    TextView mTvTitle;
    @Bind(R.id.iv_more)
    ImageView mIvMore;
    @Bind(R.id.rl_title)
    RelativeLayout mRlTitle;
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
    @Bind(R.id.xx4)
    TextView mXx4;
    private DisplayMetrics mDm;
    private ViewGroup mView;
    private int mHeightPixels;
    private PopupWindow mPopupWindow;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blockchain_gamble);
        ButterKnife.bind(this);
        getPhoneSize();
        mViewPager.setCurrentItem(0);
        setSelector(0);
        initTopMenu();
        initViewPager();
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

    private void initViewPager() {
        BlockchainGambleVPAdapter blockchainGambleVPAdapter = new BlockchainGambleVPAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(blockchainGambleVPAdapter);
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

    @OnClick({R.id.bark, R.id.iv_more})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;
            case R.id.iv_more:
                showPopup();
                break;
        }
    }

    //获取屏幕高度
    private void getPhoneSize() {
        mDm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(mDm);
        mHeightPixels = mDm.heightPixels;
    }

    //初始化pop
    private void showPopup() {

        int widthPixels = mDm.widthPixels;

        mView = (ViewGroup) LayoutInflater.from(this).inflate(R.layout.pop_blockchain_gamble, null);

        mPopupWindow = new PopupWindow(mView, widthPixels / 100 * 35, mHeightPixels / 6, true);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
        // 设置背景颜色变暗
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.9f;
        getWindow().setAttributes(lp);
        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {

            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1f;
                getWindow().setAttributes(lp);
            }
        });
        mPopupWindow.showAsDropDown(mXx4, (widthPixels - widthPixels / 100 * 35 - 20), 0);
        popChildClick();
    }

    private void popChildClick() {
        final TextView RegulationStatement = (TextView) mView.findViewById(R.id.tv_regulation_statement);
        RegulationStatement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPopupWindow.dismiss();
                startActivity(new Intent(BlockchainGambleActivity.this, RegulationStatementActivity.class));
            }
        });
        final TextView guessRecord = (TextView) mView.findViewById(R.id.tv_guess_record);
        guessRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPopupWindow.dismiss();
                startActivity(new Intent(BlockchainGambleActivity.this, GuessRecordActivity.class));

            }
        });
    }

}