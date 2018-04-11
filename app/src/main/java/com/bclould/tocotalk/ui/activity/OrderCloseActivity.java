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
import android.widget.TextView;
import android.widget.Toast;

import com.bclould.tocotalk.Presenter.OrderDetailsPresenter;
import com.bclould.tocotalk.R;
import com.bclould.tocotalk.base.BaseActivity;
import com.bclould.tocotalk.model.OrderInfo2;
import com.bclould.tocotalk.ui.widget.DeleteCacheDialog;
import com.bclould.tocotalk.utils.MessageEvent;
import com.bclould.tocotalk.utils.UtilTool;

import org.greenrobot.eventbus.EventBus;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by GA on 2018/4/10.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class OrderCloseActivity extends BaseActivity {
    @Bind(R.id.bark)
    ImageView mBark;
    @Bind(R.id.tv_title)
    TextView mTvTitle;
    @Bind(R.id.tv_question)
    TextView mTvQuestion;
    @Bind(R.id.tv_order_type)
    TextView mTvOrderType;
    @Bind(R.id.tv_who)
    TextView mTvWho;
    @Bind(R.id.tv_name)
    TextView mTvName;
    @Bind(R.id.tv_hint_limit)
    TextView mTvHintLimit;
    @Bind(R.id.tv_limit)
    TextView mTvLimit;
    @Bind(R.id.tv_hint_money)
    TextView mTvHintMoney;
    @Bind(R.id.tv_money)
    TextView mTvMoney;
    @Bind(R.id.tv_hint_count)
    TextView mTvHintCount;
    @Bind(R.id.tv_count)
    TextView mTvCount;
    @Bind(R.id.tv_hint_price)
    TextView mTvHintPrice;
    @Bind(R.id.tv_price)
    TextView mTvPrice;
    @Bind(R.id.tv_order_number)
    TextView mTvOrderNumber;
    @Bind(R.id.tv_time)
    TextView mTvTime;
    @Bind(R.id.ll_finish)
    LinearLayout mLlFinish;
    @Bind(R.id.ll_exception_buy)
    LinearLayout mLlExceptionBuy;
    @Bind(R.id.ll_exception_sell)
    LinearLayout mLlExceptionSell;
    private String mId;
    private int mStatus;
    private OrderDetailsPresenter mOrderDetailsPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_close);
        ButterKnife.bind(this);
        mOrderDetailsPresenter = new OrderDetailsPresenter(this);
        initIntent();
        initData();
    }

    private void initData() {
        mOrderDetailsPresenter.orderInfo(mId, new OrderDetailsPresenter.CallBack() {
            @Override
            public void send(OrderInfo2.DataBean data) {
                mInfo.setData(data);
                if (data.getTo_user_name().equals(UtilTool.getUser())) {
                    mTvWho.setText("买家");
                    mTvName.setText(data.getUser_name());
                } else {
                    mTvWho.setText("卖家");
                    mTvName.setText(data.getTo_user_name());
                }
                if (data.getType() == 1) {
                    if (data.getStatus() == 0) {
                        mTvOrderType.setText("订单已取消");
                    } else if (data.getStatus() == 3) {
                        mTvOrderType.setText("订单已完成");
                        mLlFinish.setVisibility(View.VISIBLE);
                    } else if (data.getStatus() == 4) {
                        mTvOrderType.setText("訂單異常");
                        mLlExceptionBuy.setVisibility(View.VISIBLE);
                        mTvOrderType.setTextColor(getColor(R.color.color_orange));
                    }
                    mTvTitle.setText("购买" + data.getCoin_name());
                } else {
                    if (data.getStatus() == 0) {
                        mTvOrderType.setText("订单已取消");
                    } else if (data.getStatus() == 3) {
                        mTvOrderType.setText("订单已完成");
                        mLlFinish.setVisibility(View.VISIBLE);
                    } else if (data.getStatus() == 4) {
                        mTvOrderType.setText("訂單異常");
                        mLlExceptionSell.setVisibility(View.VISIBLE);
                        mTvOrderType.setTextColor(getColor(R.color.color_orange));
                    }
                    mTvTitle.setText("售出" + data.getCoin_name());
                }
                mTvMoney.setText(data.getTrans_amount());
                mTvPrice.setText(data.getPrice());
                mTvHintCount.setText("交易数量(" + data.getCoin_name() + ")");
                mTvCount.setText(data.getNumber());
                mTvOrderNumber.setText(data.getOrder_no());
                mTvTime.setText(data.getCreated_at());
                mTvLimit.setText(data.getMin_amount() + " - " + data.getMax_amount());
            }
        });
    }

    private void initIntent() {
        mStatus = getIntent().getIntExtra("status", 0);
        mId = getIntent().getStringExtra("id");
        if (mStatus == 0) {
            mTvOrderType.setText("订单已取消");
        } else if (mStatus == 3) {
            mTvOrderType.setText("订单已完成");
            mLlFinish.setVisibility(View.VISIBLE);
        } else if (mStatus == 4) {
            mTvOrderType.setText("訂單異常");
            mTvOrderType.setTextColor(getColor(R.color.color_orange));
        }
    }

    @OnClick({R.id.bark, R.id.tv_question, R.id.btn_confirm})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;
            case R.id.tv_question:
                startActivity(new Intent(this, ProblemFeedBackActivity.class));
                break;
            case R.id.btn_confirm:
                showDialog();
                break;
        }
    }

    public void showDialog() {
        final DeleteCacheDialog deleteCacheDialog = new DeleteCacheDialog(R.layout.dialog_delete_cache, this);
        deleteCacheDialog.show();
        deleteCacheDialog.setTitle("是否确认放币？");
        Button cancel = (Button) deleteCacheDialog.findViewById(R.id.btn_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteCacheDialog.dismiss();
            }
        });
        Button confirm = (Button) deleteCacheDialog.findViewById(R.id.btn_confirm);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirm();
            }
        });
    }

    OrderInfo2 mInfo = new OrderInfo2();

    private void confirm() {
        mOrderDetailsPresenter.confirmGiveCoin(mInfo.getData().getTrans_id(), mInfo.getData().getId(), new OrderDetailsPresenter.CallBack2() {
            @Override
            public void send() {
                MessageEvent messageEvent = new MessageEvent("确认放币");
                messageEvent.setId(mInfo.getData().getId() + "");
                EventBus.getDefault().post(messageEvent);
                Toast.makeText(OrderCloseActivity.this, "完成交易", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}
