package com.dashiji.biyun.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dashiji.biyun.base.MyApp;
import com.dashiji.biyun.R;
import com.dashiji.biyun.base.BaseActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by GA on 2017/9/26.
 */

public class BillDetailsActivity extends BaseActivity {

    @Bind(R.id.bark)
    ImageView mBark;
    @Bind(R.id.touxiang)
    ImageView mTouxiang;
    @Bind(R.id.name)
    TextView mName;
    @Bind(R.id.money)
    TextView mMoney;
    @Bind(R.id.type)
    TextView mType;
    @Bind(R.id.currency)
    TextView mCurrency;
    @Bind(R.id.time)
    TextView mTime;
    @Bind(R.id.dingdan_number)
    TextView mDingdanNumber;
    @Bind(R.id.rl_deal_record)
    RelativeLayout mRlDealRecord;
    @Bind(R.id.rl_query)
    RelativeLayout mRlQuery;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_details);
        ButterKnife.bind(this);
        MyApp.getInstance().addActivity(this);
    }

    @OnClick({R.id.bark, R.id.rl_deal_record, R.id.rl_query})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;
            case R.id.rl_deal_record:
                startActivity(new Intent(this, DealRecordActivity.class));
                break;
            case R.id.rl_query:
                startActivity(new Intent(this, ProblemFeedBackActivity.class));
                break;
        }
    }
}
