package com.bclould.tocotalk.ui.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bclould.tocotalk.Presenter.OrderDetailsPresenter;
import com.bclould.tocotalk.R;
import com.bclould.tocotalk.base.BaseActivity;
import com.bclould.tocotalk.model.OrderInfo2;
import com.bclould.tocotalk.utils.UtilTool;

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
                if (data.getTo_user_name().equals(UtilTool.getUser())) {
                    mTvWho.setText("买家");
                    mTvName.setText(data.getUser_name());
                } else {
                    mTvWho.setText("卖家");
                    mTvName.setText(data.getTo_user_name());
                }
                if(data.getType() == 1){
                    mTvTitle.setText("购买" + data.getCoin_name());
                }else {
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
            mLlFinish.setVisibility(View.GONE);
            mTvOrderType.setText("订单已取消");
        } else {
            mLlFinish.setVisibility(View.VISIBLE);
            mTvOrderType.setText("订单已完成");
        }
    }

    @OnClick({R.id.bark, R.id.tv_question})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;
            case R.id.tv_question:
                startActivity(new Intent(this, ProblemFeedBackActivity.class));
                break;
        }
    }
}
