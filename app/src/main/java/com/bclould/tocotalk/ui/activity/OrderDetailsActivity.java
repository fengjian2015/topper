package com.bclould.tocotalk.ui.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bclould.tocotalk.Presenter.OrderDetailsPresenter;
import com.bclould.tocotalk.R;
import com.bclould.tocotalk.base.BaseActivity;
import com.bclould.tocotalk.model.OrderInfo;
import com.bclould.tocotalk.model.OrderInfo2;
import com.bclould.tocotalk.utils.MessageEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.Timer;
import java.util.TimerTask;

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
    @Bind(R.id.tv2)
    TextView mTv2;
    @Bind(R.id.tv_time)
    TextView mTvTime;
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
    @Bind(R.id.tv_buysell2)
    TextView mTvBuysell2;
    @Bind(R.id.ll_seller)
    LinearLayout mLlSeller;
    @Bind(R.id.ll_buyer)
    LinearLayout mLlBuyer;
    @Bind(R.id.ll_order)
    LinearLayout mLlOrder;
    private OrderInfo.DataBean mData;
    Timer mTimer = new Timer();
    private int mRecLen = 0;
    private OrderDetailsPresenter mOrderDetailsPresenter;
    TimerTask mTask = new TimerTask() {
        @Override
        public void run() {

            runOnUiThread(new Runnable() {      // UI thread
                @Override
                public void run() {
                    mRecLen--;
                    int second = mRecLen % 60;
                    int minute = mRecLen / 60;
                    mTvTime.setText(minute + "分" + second + "秒");
                    if (mRecLen < 0) {
                        mTimer.cancel();
                        finish();
                        Toast.makeText(OrderDetailsActivity.this, "订单超时", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    };
    private String mType;

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
        mType = intent.getStringExtra("type");
        if (mType.equals("订单")) {
            String id = intent.getStringExtra("id");
            initData(id);
        } else {
            mData = (OrderInfo.DataBean) intent.getSerializableExtra("data");
            mRecLen = mData.getDeadline();
            mTimer.schedule(mTask, 1000, 1000);
            if (mData.getType() == 1) {
                mTvTitle.setText("购买" + mData.getCoin_name());
                mLlBuyer.setVisibility(View.VISIBLE);
                mLlSeller.setVisibility(View.GONE);
                mTvBuysell.setText("卖家:" + mData.getTo_user_name());
                mTvBuysell2.setText("买家:" + mData.getUser_name());
            } else {
                mLlBuyer.setVisibility(View.GONE);
                mLlSeller.setVisibility(View.VISIBLE);
                mTvTitle.setText("售出" + mData.getCoin_name());
                mTvBuysell2.setText("买家:" + mData.getTo_user_name());
                mTvBuysell.setText("卖家:" + mData.getUser_name());
            }
            mTvCount.setText(mData.getNumber());
            mTvMoney.setText(mData.getTrans_amount());
            mTvPrice.setText(mData.getPrice());
            mTvOrderNumber.setText("订单号:" + mData.getOrder_no());
        }
    }

    OrderInfo2 mInfo = new OrderInfo2();

    private void initData(String id) {
        mOrderDetailsPresenter.orderInfo(id, new OrderDetailsPresenter.CallBack() {
            @Override
            public void send(OrderInfo2.DataBean data) {
                mInfo.setData(data);
                mTvOrderNumber.setText("订单号:" + data.getOrder_no());
                mRecLen = data.getDeadline();
                mTimer.schedule(mTask, 1000, 1000);
                if (data.getType() == 1) {
                    mLlBuyer.setVisibility(View.VISIBLE);
                    mLlSeller.setVisibility(View.GONE);
                    mTvTitle.setText("购买" + data.getCoin_name());
                    mTvBuysell.setText("卖家:" + data.getTo_user_name());
                    mTvBuysell2.setText("买家:" + data.getUser_name());
                } else {
                    mLlBuyer.setVisibility(View.GONE);
                    mLlSeller.setVisibility(View.VISIBLE);
                    mTvTitle.setText("售出" + data.getCoin_name());
                    mTvBuysell.setText("买家:" + data.getUser_name());
                    mTvBuysell2.setText("卖家:" + data.getTo_user_name());
                }
                mTvCount.setText(data.getNumber());
                mTvMoney.setText(data.getTrans_amount());
                mTvPrice.setText(data.getPrice());
                mTvPayType.setText(data.getStatus_name());
            }
        });
    }

    @OnClick({R.id.bark, R.id.tv_help, R.id.btn_buy_cancel, R.id.btn_sell_cancel, R.id.btn_buy_confirm, R.id.btn_sell_confirm})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;
            case R.id.tv_help:
                startActivity(new Intent(this, ProblemFeedBackActivity.class));
                break;
            case R.id.btn_buy_cancel:
                cancel();
                break;
            case R.id.btn_sell_cancel:
                cancel();
                break;
            case R.id.btn_buy_confirm:
                confirmPay();
                break;
            case R.id.btn_sell_confirm:
                confirmGiveCoin();
                break;
        }
    }

    private void confirmGiveCoin() {
        if (mType.equals("订单")) {
            mOrderDetailsPresenter.confirmGiveCoin(mInfo.getData().getTrans_id(), mInfo.getData().getId(), new OrderDetailsPresenter.CallBack2() {
                @Override
                public void send() {
                    MessageEvent messageEvent = new MessageEvent("确认放币");
                    messageEvent.setId(mInfo.getData().getId() + "");
                    EventBus.getDefault().post(messageEvent);
                    Toast.makeText(OrderDetailsActivity.this, "完成交易", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        } else {
            mOrderDetailsPresenter.confirmGiveCoin(mData.getTrans_id(), mData.getId(), new OrderDetailsPresenter.CallBack2() {
                @Override
                public void send() {
                    MessageEvent messageEvent = new MessageEvent("确认放币");
                    messageEvent.setId(mData.getId() + "");
                    EventBus.getDefault().post(messageEvent);
                    Toast.makeText(OrderDetailsActivity.this, "完成交易", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        }
    }

    private void confirmPay() {
        if (mType.equals("订单")) {
            mOrderDetailsPresenter.confirmPay(mInfo.getData().getTrans_id(), mInfo.getData().getId(), new OrderDetailsPresenter.CallBack2() {
                @Override
                public void send() {
                    mTvPayType.setText("等待放币");
                    MessageEvent messageEvent = new MessageEvent("确认付款");
                    messageEvent.setId(mInfo.getData().getId() + "");
                    EventBus.getDefault().post(messageEvent);
                    Toast.makeText(OrderDetailsActivity.this, "确认付款成功", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            mOrderDetailsPresenter.confirmPay(mData.getTrans_id(), mData.getId(), new OrderDetailsPresenter.CallBack2() {
                @Override
                public void send() {
                    mTvPayType.setText("等待放币");
                    MessageEvent messageEvent = new MessageEvent("确认付款");
                    messageEvent.setId(mData.getId() + "");
                    EventBus.getDefault().post(messageEvent);
                    EventBus.getDefault().post(new MessageEvent("确认付款"));
                    Toast.makeText(OrderDetailsActivity.this, "确认付款成功", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void cancel() {
        if (mType.equals("订单")) {
            mOrderDetailsPresenter.cancel(mInfo.getData().getTrans_id(), mInfo.getData().getId(), new OrderDetailsPresenter.CallBack2() {
                @Override
                public void send() {
                    MessageEvent messageEvent = new MessageEvent("取消订单");
                    messageEvent.setId(mInfo.getData().getId() + "");
                    EventBus.getDefault().post(messageEvent);
                    Toast.makeText(OrderDetailsActivity.this, "取消交易", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        } else {
            mOrderDetailsPresenter.cancel(mData.getTrans_id(), mData.getId(), new OrderDetailsPresenter.CallBack2() {
                @Override
                public void send() {
                    MessageEvent messageEvent = new MessageEvent("取消订单");
                    messageEvent.setId(mData.getId() + "");
                    EventBus.getDefault().post(messageEvent);
                    Toast.makeText(OrderDetailsActivity.this, "取消交易", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        }
    }
}
