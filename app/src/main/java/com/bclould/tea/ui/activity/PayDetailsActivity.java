package com.bclould.tea.ui.activity;

import android.content.ClipboardManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bclould.tea.Presenter.BuySellPresenter;
import com.bclould.tea.R;
import com.bclould.tea.base.BaseActivity;
import com.bclould.tea.base.MyApp;
import com.bclould.tea.model.TransRecordInfo;
import com.bclould.tea.utils.ActivityUtil;
import com.bclould.tea.utils.StringUtils;

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
    @Bind(R.id.tv_tx_id)
    TextView mTvTxId;
    @Bind(R.id.tv_copy)
    TextView mTvCopy;
    @Bind(R.id.rl_tx_id)
    RelativeLayout mRlTxId;
    @Bind(R.id.cv_data)
    CardView mCvData;
    @Bind(R.id.iv2)
    ImageView mIv2;
    @Bind(R.id.ll_error)
    LinearLayout mLlError;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_details);
        ButterKnife.bind(this);
        MyApp.getInstance().addActivity(this);
        initData();
    }

    private void initData() {
        String id = getIntent().getStringExtra("id");
        String log_id = getIntent().getStringExtra("log_id");
        String type_number = getIntent().getStringExtra("type_number");
        BuySellPresenter buySellPresenter = new BuySellPresenter(this);
        if (type_number != null) {
            if (Integer.parseInt(type_number) == 7 || Integer.parseInt(type_number) == 8) {
                mRlTxId.setVisibility(View.VISIBLE);
            } else {
                mRlTxId.setVisibility(View.GONE);
            }
        }
        buySellPresenter.transRecordInfo(log_id, id, type_number, new BuySellPresenter.CallBack7() {

            @Override
            public void send(TransRecordInfo.DataBean data) {
                if (ActivityUtil.isActivityOnTop(PayDetailsActivity.this)) {
                    mCvData.setVisibility(View.VISIBLE);
                    mLlError.setVisibility(View.GONE);
                    mTvName.setText(data.getType_name());
                    mTvLimit.setText(data.getName());
                    mTvMoney.setText(data.getNumber());
                    mTvCount.setText(data.getCoin_name());
                    mTvPrice.setText(data.getCreated_at());
                    if (!StringUtils.isEmpty(data.getTxid())) {
                        mTvTxId.setText("Txid: " + data.getTxid());
                    }
                }
            }

            @Override
            public void error() {
                if (ActivityUtil.isActivityOnTop(PayDetailsActivity.this)) {
                    mCvData.setVisibility(View.GONE);
                    mLlError.setVisibility(View.VISIBLE);
                }
            }
        });
        mTvCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                // 将文本内容放到系统剪贴板里。
                cm.setText(mTvTxId.getText());
                Toast.makeText(PayDetailsActivity.this, getString(R.string.copy_succeed), Toast.LENGTH_LONG).show();
            }
        });
    }


    @OnClick({R.id.bark, R.id.ll_error})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;
            case R.id.ll_error:
                initData();
                break;
        }
    }
}
