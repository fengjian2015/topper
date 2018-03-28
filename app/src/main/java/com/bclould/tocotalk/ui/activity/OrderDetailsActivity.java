package com.bclould.tocotalk.ui.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bclould.tocotalk.Presenter.OrderDetailsPresenter;
import com.bclould.tocotalk.R;
import com.bclould.tocotalk.base.BaseActivity;
import com.bclould.tocotalk.model.OrderInfo;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by GA on 2018/1/19.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class OrderDetailsActivity extends BaseActivity {
    @Bind(R.id.bark)
    ImageView mBark;
    @Bind(R.id.tv_title)
    TextView mTvTitle;
    @Bind(R.id.tv_help)
    TextView mTvHelp;
    @Bind(R.id.tv_order_number)
    TextView mTvOrderNumber;
    @Bind(R.id.tv_pay_type)
    TextView mTvPayType;
    @Bind(R.id.tv_time)
    TextView mTvTime;
    @Bind(R.id.tv)
    TextView mTv;
    @Bind(R.id.rl_order_intro)
    RelativeLayout mRlOrderIntro;
    @Bind(R.id.tv_money)
    TextView mTvMoney;
    @Bind(R.id.tv_count)
    TextView mTvCount;
    @Bind(R.id.tv_price)
    TextView mTvPrice;
    @Bind(R.id.tv_buysell)
    TextView mTvBuysell;
    @Bind(R.id.tv_remark)
    TextView mTvRemark;
    @Bind(R.id.iv_bank)
    ImageView mIvBank;
    @Bind(R.id.tv_bank_name)
    TextView mTvBankName;
    @Bind(R.id.tv_bank_site)
    TextView mTvBankSite;
    @Bind(R.id.tv_bank_number)
    TextView mTvBankNumber;
    @Bind(R.id.rl_bank)
    RelativeLayout mRlBank;
    @Bind(R.id.iv_alipay)
    ImageView mIvAlipay;
    @Bind(R.id.tv_alipay_name)
    TextView mTvAlipayName;
    @Bind(R.id.tv_alipay_number)
    TextView mTvAlipayNumber;
    @Bind(R.id.rl_alipay)
    RelativeLayout mRlAlipay;
    @Bind(R.id.iv_wechat)
    ImageView mIvWechat;
    @Bind(R.id.tv_wechat_name)
    TextView mTvWechatName;
    @Bind(R.id.tv_wechat_number)
    TextView mTvWechatNumber;
    @Bind(R.id.rl_wechat)
    RelativeLayout mRlWechat;
    @Bind(R.id.tv_buysell2)
    TextView mTvBuysell2;
    @Bind(R.id.btn_cancel_order2)
    Button mBtnCancelOrder2;
    @Bind(R.id.btn_confirm_send_coin)
    Button mBtnConfirmSendCoin;
    @Bind(R.id.ll_seller)
    LinearLayout mLlSeller;
    @Bind(R.id.btn_cancel_order)
    Button mBtnCancelOrder;
    @Bind(R.id.btn_confirm_pay)
    Button mBtnConfirmPay;
    @Bind(R.id.ll_buyer)
    LinearLayout mLlBuyer;
    @Bind(R.id.ll_order)
    LinearLayout mLlOrder;
    private OrderInfo.DataBean mData;
    private OrderDetailsPresenter mOrderDetailsPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);
        ButterKnife.bind(this);
        initInterface();
    }

    private void initInterface() {
        mOrderDetailsPresenter = new OrderDetailsPresenter(this);
        Intent intent = getIntent();
        mData = (OrderInfo.DataBean) intent.getSerializableExtra("data");
        String type = intent.getStringExtra("type");
        if (type.equals("买")) {
            mTvTitle.setText("购买" + mData.getCoin_name());
            mLlBuyer.setVisibility(View.VISIBLE);
            mLlSeller.setVisibility(View.GONE);
            mTvBuysell.setText("卖家:" + mData.getTo_user_name());
            mTvBuysell2.setText("买家:" + mData.getUser_name());
        } else {
            mLlBuyer.setVisibility(View.GONE);
            mLlSeller.setVisibility(View.VISIBLE);
            mTvTitle.setText("售出" + mData.getCoin_name());
            mTvBuysell.setText("买家:" + mData.getUser_name());
            mTvBuysell2.setText("卖家:" + mData.getTo_user_name());
        }
        mTvCount.setText(mData.getNumber());
        mTvMoney.setText(mData.getTrans_amount());
        mTvPrice.setText(mData.getPrice());
        mTvOrderNumber.setText("订单号:" + mData.getOrder_no());
    }

    @OnClick({R.id.bark, R.id.tv_help, R.id.btn_cancel_order2, R.id.btn_confirm_send_coin, R.id.btn_cancel_order, R.id.btn_confirm_pay})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;
            case R.id.tv_help:
                startActivity(new Intent(this, ProblemFeedBackActivity.class));
                break;
            case R.id.btn_cancel_order2:
                break;
            case R.id.btn_confirm_send_coin:
                break;
            case R.id.btn_cancel_order:
                break;
            case R.id.btn_confirm_pay:
                break;
        }
    }
}
