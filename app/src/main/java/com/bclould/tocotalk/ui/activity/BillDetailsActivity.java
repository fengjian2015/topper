package com.bclould.tocotalk.ui.activity;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bclould.tocotalk.R;
import com.bclould.tocotalk.base.BaseActivity;
import com.bclould.tocotalk.base.MyApp;
import com.bclould.tocotalk.history.DBManager;
import com.bclould.tocotalk.utils.Constants;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by GA on 2017/9/26.
 */

@RequiresApi(api = Build.VERSION_CODES.M)
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
        initIntent();
    }

    private void initIntent() {
        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        String time = intent.getStringExtra("time");
        String coin = intent.getStringExtra("coin");
        String coinCount = intent.getStringExtra("coinCount");
        int id = intent.getIntExtra("coinCount", 0);
        boolean type = intent.getBooleanExtra("type", false);
        DBManager dbManager = new DBManager(this);
        mTouxiang.setImageBitmap(BitmapFactory.decodeFile(dbManager.queryUser(name + "@" + Constants.DOMAINNAME).get(0).getUser()));
        mName.setText(name);
        mTime.setText(time);
        mCurrency.setText(coin);
        if (type) {
            mMoney.setTextColor(getColor(R.color.red));
            mMoney.setText("+" + coinCount + " " + coin);
        } else {
            mMoney.setTextColor(getColor(R.color.green));
            mMoney.setText("-" + coinCount + " " + coin);
        }
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
