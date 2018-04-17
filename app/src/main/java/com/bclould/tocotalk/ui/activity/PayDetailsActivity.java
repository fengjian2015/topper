package com.bclould.tocotalk.ui.activity;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.widget.ImageView;
import android.widget.TextView;

import com.bclould.tocotalk.Presenter.BuySellPresenter;
import com.bclould.tocotalk.R;
import com.bclould.tocotalk.base.BaseActivity;
import com.bclould.tocotalk.model.OrderListInfo;
import com.bclould.tocotalk.model.TransRecordInfo;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by GA on 2018/4/14.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class PayDetailsActivity extends BaseActivity {
    @Bind(R.id.bark)
    ImageView mBark;
    @Bind(R.id.tv_title)
    TextView mTvTitle;
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_details);
        ButterKnife.bind(this);
        initData();
    }

    private void initData() {
        String id = getIntent().getStringExtra("id");
        String type_number = getIntent().getStringExtra("type_number");
        BuySellPresenter buySellPresenter = new BuySellPresenter(this);
        buySellPresenter.transRecordInfo(id, type_number, new BuySellPresenter.CallBack3() {


            @Override
            public void send(List<OrderListInfo.DataBean> data) {

            }

            @Override
            public void send2(TransRecordInfo.DataBean data) {
                mTvName.setText(data.getName());
                mTvLimit.setText(data.getType_name());
                mTvMoney.setText(data.getNumber());
                mTvCount.setText(data.getCoin_name());
                mTvPrice.setText(data.getCreated_at());
            }
        });
    }

    @OnClick(R.id.bark)
    public void onViewClicked() {
        finish();
    }
}
