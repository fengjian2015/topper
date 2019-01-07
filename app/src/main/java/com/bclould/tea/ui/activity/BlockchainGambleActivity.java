package com.bclould.tea.ui.activity;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bclould.tea.R;
import com.bclould.tea.base.BaseActivity;
import com.bclould.tea.ui.adapter.BlockchainGambleVPAdapter;
import com.bclould.tea.ui.widget.WinningPopWindow;
import com.bclould.tea.utils.ActivityUtil;
import com.bclould.tea.utils.Constants;
import com.bclould.tea.utils.EventBusUtil;
import com.bclould.tea.utils.MessageEvent;
import com.bclould.tea.utils.WinningManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by GA on 2018/4/23.
 */

public class BlockchainGambleActivity extends BaseActivity {
    @Bind(R.id.bark)
    ImageView mBark;
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
    private DisplayMetrics mDm;
    private ViewGroup mView;
    private int mHeightPixels;
    private PopupWindow mPopupWindow;
    private WinningPopWindow mWinningPopWindow;
    private boolean hasFocus;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blockchain_gamble);
        ButterKnife.bind(this);
        setTitle(getString(R.string.insert_coins_guess), R.mipmap.icon_nav_add);
        EventBus.getDefault().register(this);//初始化EventBus
        getPhoneSize();
        mViewPager.setCurrentItem(0);
        setSelector(0);
        initTopMenu();
        initViewPager();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    //接受通知
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        String msg = event.getMsg();
        if (msg.equals(EventBusUtil.winning_show)) {
            show(event.getContent());
        } else if (msg.equals(EventBusUtil.winning_shut_down)) {
            shutDown();
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        // TODO Auto-generated method stub
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            this.hasFocus = hasFocus;
        }
    }

    private void show(final String content) {
        if (hasFocus && ActivityUtil.isActivityOnTop(this)) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mWinningPopWindow = new WinningPopWindow(BlockchainGambleActivity.this, content, mRlTitle);
                }
            });
        }
    }

    private void shutDown() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mWinningPopWindow != null) {
                    mWinningPopWindow.dismiss();
                }
            }
        });
        ;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);//初始化EventBus
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

        mPopupWindow = new PopupWindow(mView, ViewGroup.LayoutParams.WRAP_CONTENT, (int) (getResources().getDimension(R.dimen.y200)), true);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
        mPopupWindow.showAsDropDown(mTvTitleTop, (widthPixels - mPopupWindow.getWidth()), 0);
        popChildClick();
    }

    private void popChildClick() {
        final TextView RegulationStatement = (TextView) mView.findViewById(R.id.tv_regulation_statement);
        RegulationStatement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPopupWindow.dismiss();
                Intent intent = new Intent(BlockchainGambleActivity.this, WebViewActivity.class);
                intent.putExtra("url", Constants.BASE_URL + Constants.GUESS_WEB_URL);
                intent.putExtra("title", getString(R.string.regulation_statement));
                startActivity(intent);
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
