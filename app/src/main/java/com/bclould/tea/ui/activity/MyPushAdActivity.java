package com.bclould.tea.ui.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.bclould.tea.Presenter.BuySellPresenter;
import com.bclould.tea.R;
import com.bclould.tea.base.BaseActivity;
import com.bclould.tea.model.OrderStatisticsInfo;
import com.bclould.tea.ui.adapter.CloudCircleVPAdapter;
import com.bclould.tea.ui.adapter.PayManageGVAdapter;
import com.bclould.tea.ui.fragment.MyPushBuyFragment;
import com.bclould.tea.ui.fragment.MyPushSellFragment;
import com.bclould.tea.utils.ActivityUtil;
import com.bclould.tea.utils.MessageEvent;
import org.greenrobot.eventbus.EventBus;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.bclould.tea.R.style.BottomDialog;

/**
 * Created by GA on 2018/4/27.
 */

public class MyPushAdActivity extends BaseActivity {

    @Bind(R.id.iv)
    ImageView mIv;
    @Bind(R.id.tv_sum_sell)
    TextView mTvSumSell;
    @Bind(R.id.iv_jiantou)
    ImageView mIvJiantou;
    @Bind(R.id.iv2)
    ImageView mIv2;
    @Bind(R.id.tv_sum_buy)
    TextView mTvSumBuy;
    @Bind(R.id.iv_jiantou2)
    ImageView mIvJiantou2;
    @Bind(R.id.iv3)
    ImageView mIv3;
    @Bind(R.id.ll_on_off)
    LinearLayout mLlOnOff;
    @Bind(R.id.tv_order_sum)
    TextView mTvOrderSum;
    @Bind(R.id.tv_anomaly_order_sum)
    TextView mTvAnomalyOrderSum;
    @Bind(R.id.tv_sum_deal_count)
    TextView mTvSumDealCount;
    @Bind(R.id.tv_finish_sum)
    TextView mTvFinishSum;
    @Bind(R.id.tv_cancel_sum)
    TextView mTvCancelSum;
    @Bind(R.id.tv_underway_sum)
    TextView mTvUnderwaySum;
    @Bind(R.id.ll_sum)
    LinearLayout mLlSum;
    @Bind(R.id.tv_xx)
    TextView mTvXx;
    @Bind(R.id.rl_my_buy)
    RelativeLayout mRlMyBuy;
    @Bind(R.id.tv_xx2)
    TextView mTvXx2;
    @Bind(R.id.rl_my_sell)
    RelativeLayout mRlMySell;
    @Bind(R.id.view_pager)
    ViewPager mViewPager;
    @Bind(R.id.rl_sum_sell)
    RelativeLayout mRlSumSell;
    @Bind(R.id.rl_sum_buy)
    RelativeLayout mRlSumBuy;
    @Bind(R.id.tv_on_off)
    TextView mTvOnOff;
    @Bind(R.id.iv_jiantou3)
    ImageView mIvJiantou3;
    @Bind(R.id.ll_data)
    LinearLayout mLlData;
    @Bind(R.id.iv4)
    ImageView mIv4;
    @Bind(R.id.ll_error)
    LinearLayout mLlError;
    private String mCoinName;
    private Map<String, Integer> mMap = new HashMap<>();
    private Dialog mBottomDialog;
    private int mType;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_push_ad);
        ButterKnife.bind(this);
        setTitle(getString(R.string.my_publish),getString(R.string.filtrate));
        mMap.put(getString(R.string.filtrate), 0);
        mFiltrateList.add(getString(R.string.all));
        mFiltrateList.add(getString(R.string.yi_sold_out));
        mFiltrateList.add(getString(R.string.deal_zhong));
        initIntent();
        initFragments();
        initViewPage();
        initData();
    }

    private void initData() {
        BuySellPresenter buySellPresenter = new BuySellPresenter(this);
        buySellPresenter.getTotal(mCoinName, new BuySellPresenter.CallBack6() {
            @Override
            public void send(OrderStatisticsInfo.DataBean data) {
                if (ActivityUtil.isActivityOnTop(MyPushAdActivity.this)) {
                    mLlData.setVisibility(View.VISIBLE);
                    mLlError.setVisibility(View.GONE);
                    mTvSumBuy.setText(data.getBuy_sum());
                    mTvSumSell.setText(data.getSale_sum());
                    mTvSumDealCount.setText(data.getTotal_trans() + "");
                    mTvAnomalyOrderSum.setText(data.getAbnormal() + "");
                    mTvCancelSum.setText(data.getCancel() + "");
                    mTvFinishSum.setText(data.getComplete() + "");
                    mTvOrderSum.setText(data.getAll() + "");
                    mTvUnderwaySum.setText(data.getProcessing() + "");
                }
            }

            @Override
            public void error() {
                if (ActivityUtil.isActivityOnTop(MyPushAdActivity.this)) {
                    mLlData.setVisibility(View.GONE);
                    mLlError.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void initIntent() {
        Intent intent = getIntent();
        mCoinName = intent.getStringExtra("coinName");
    }

    List<Fragment> mFragmentList = new ArrayList<>();

    private void initFragments() {
        MyPushBuyFragment myPushBuyFragment = new MyPushBuyFragment();
        Bundle bundle=new Bundle();
        bundle.putString("mCoinName",mCoinName);
        myPushBuyFragment.setArguments(bundle);

        MyPushSellFragment myPushSellFragment = new MyPushSellFragment();
        Bundle bundle1=new Bundle();
        bundle1.putString("mCoinName",mCoinName);
        myPushSellFragment.setArguments(bundle1);

        mFragmentList.add(myPushBuyFragment);
        mFragmentList.add(myPushSellFragment);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    private void initViewPage() {
        mViewPager.setCurrentItem(0);
        mRlMyBuy.setSelected(true);
        mTvXx.setVisibility(View.VISIBLE);
        CloudCircleVPAdapter cloudCircleVPAdapter = new CloudCircleVPAdapter(getSupportFragmentManager(), mFragmentList);
        mViewPager.setAdapter(cloudCircleVPAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    mTvXx.setVisibility(View.VISIBLE);
                    mTvXx2.setVisibility(View.GONE);
                } else {
                    mTvXx.setVisibility(View.GONE);
                    mTvXx2.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private List<String> mFiltrateList = new ArrayList<>();
    boolean isOnOff = false;

    @OnClick({R.id.ll_error, R.id.bark, R.id.tv_add, R.id.rl_my_buy, R.id.rl_my_sell, R.id.ll_on_off, R.id.rl_sum_buy, R.id.rl_sum_sell})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;
            case R.id.ll_error:
                initData();
                break;
            case R.id.rl_sum_buy:
                Intent intent = new Intent(this, SumBuySellActivity.class);
                intent.putExtra("type", 1);
                intent.putExtra("coin_name", mCoinName);
                startActivity(intent);
                break;
            case R.id.rl_sum_sell:
                Intent intent2 = new Intent(this, SumBuySellActivity.class);
                intent2.putExtra("type", 2);
                intent2.putExtra("coin_name", mCoinName);
                startActivity(intent2);
                break;
            case R.id.ll_on_off:
                isOnOff = !isOnOff;
                if (isOnOff) {
                    mLlSum.setVisibility(View.VISIBLE);
                    mIvJiantou3.setImageResource(R.mipmap.icon_more_up);
                    mTvOnOff.setText(getString(R.string.shouqi));
                } else {
                    mLlSum.setVisibility(View.GONE);
                    mIvJiantou3.setImageResource(R.mipmap.icon_more_down);
                    mTvOnOff.setText(getString(R.string.zhankai));
                }
                break;
            case R.id.tv_add:
                showFiltrateDialog();
                break;
            case R.id.rl_my_buy:
                mRlMyBuy.setSelected(true);
                mRlMySell.setSelected(false);
                mTvXx.setVisibility(View.VISIBLE);
                mTvXx2.setVisibility(View.GONE);
                mViewPager.setCurrentItem(0);
                break;
            case R.id.rl_my_sell:
                mRlMyBuy.setSelected(false);
                mRlMySell.setSelected(true);
                mTvXx.setVisibility(View.GONE);
                mTvXx2.setVisibility(View.VISIBLE);
                mViewPager.setCurrentItem(1);
                break;
        }
    }

    private void showFiltrateDialog() {
        mBottomDialog = new Dialog(this, R.style.BottomDialog2);
        View contentView = LayoutInflater.from(this).inflate(R.layout.dialog_bill, null);
        //获得dialog的window窗口
        Window window = mBottomDialog.getWindow();
        window.getDecorView().setPadding(0, 0, 0, 0);
        //获得window窗口的属性
        WindowManager.LayoutParams lp = window.getAttributes();
        //设置窗口宽度为充满全屏
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        //将设置好的属性set回去
        window.setAttributes(lp);
        window.setGravity(Gravity.BOTTOM);
        window.setWindowAnimations(BottomDialog);
        mBottomDialog.setContentView(contentView);
        mBottomDialog.show();
        GridView gridView = (GridView) mBottomDialog.findViewById(R.id.grid_view);
        Button cancel = (Button) mBottomDialog.findViewById(R.id.btn_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBottomDialog.dismiss();
            }
        });
        gridView.setAdapter(new PayManageGVAdapter(this, mFiltrateList, mMap, new PayManageGVAdapter.CallBack() {
            //接口回调
            @Override
            public void send(int position, String typeName) {
                if (typeName.equals(getString(R.string.all))) {
                    mType = 2;
                } else if (typeName.equals(getString(R.string.yi_sold_out))) {
                    mType = 0;
                } else if (typeName.equals(getString(R.string.deal_zhong))) {
                    mType = 1;
                }
                mMap.put(getString(R.string.filtrate), position);
                mBottomDialog.dismiss();
                MessageEvent messageEvent = new MessageEvent(getString(R.string.my_ad_filtrate));
                messageEvent.setNumber(mType);
                EventBus.getDefault().post(messageEvent);
            }
        }));
    }
}
