package com.bclould.tea.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.bclould.tea.R;
import com.bclould.tea.base.BaseActivity;
import com.bclould.tea.utils.UtilTool;
import java.text.DecimalFormat;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by GA on 2018/4/3.
 */

public class TransferDetailsActivity extends BaseActivity {
    @Bind(R.id.iv)
    ImageView mIv;
    @Bind(R.id.tv_hint)
    TextView mTvHint;
    @Bind(R.id.tv_coin)
    TextView mTvCoin;
    @Bind(R.id.tv_count)
    TextView mTvCount;
    @Bind(R.id.tv_time)
    TextView mTvTime;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer_details);
        ButterKnife.bind(this);
        setTitle(getString(R.string.transfer_details),getString(R.string.record));
        initIntent();
    }

    private void initIntent() {
        Intent intent = getIntent();
        String count = intent.getStringExtra("count");
        String coin = intent.getStringExtra("coin");
        String time = intent.getStringExtra("time");
        int type = intent.getIntExtra("type", 0);
        if (type == 0) {
            mTvHint.setText(getString(R.string.received_money));
        } else {
            mTvHint.setText(getString(R.string.out_money));
        }
        DecimalFormat df = new DecimalFormat("0.000000");
        String countd = df.format(Double.parseDouble(count));
        mTvCoin.setText(coin);
        mTvCount.setText(UtilTool.removeZero(countd));
        mTvTime.setText(getString(R.string.transfer_time) + "：" + time);
    }

    @OnClick({R.id.bark, R.id.tv_add})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;
            case R.id.tv_add:
                Intent intent = new Intent(this, BillDetailsActivity.class);
                intent.putExtra("type", 2);
                startActivity(intent);
                break;
        }
    }
}
