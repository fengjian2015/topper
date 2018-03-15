package com.bclould.tocotalk.ui.activity;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bclould.tocotalk.Presenter.CurrencyInOutPresenter;
import com.bclould.tocotalk.R;
import com.bclould.tocotalk.base.BaseActivity;
import com.bclould.tocotalk.model.BaseInfo;
import com.bumptech.glide.Glide;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by GA on 2018/3/15.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class InCoinActivity extends BaseActivity {
    @Bind(R.id.bark)
    ImageView mBark;
    @Bind(R.id.tv_record)
    TextView mTvRecord;
    @Bind(R.id.tv_count)
    TextView mTvCount;
    @Bind(R.id.tv_usdt)
    TextView mTvUsdt;
    @Bind(R.id.iv_site_qr)
    ImageView mIvSiteQr;
    @Bind(R.id.tv_site)
    TextView mTvSite;
    @Bind(R.id.card_view)
    CardView mCardView;
    @Bind(R.id.btn_copy)
    Button mBtnCopy;
    private int mId;
    private String mCoinName;
    private String mOver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_coin);
        ButterKnife.bind(this);
        initData();
        initIntent();
    }

    private void initIntent() {
        Intent intent = getIntent();
        mId = intent.getIntExtra("id", 0);
        mCoinName = intent.getStringExtra("coinName");
        mOver = intent.getStringExtra("over");
        mTvCount.setText(mOver);
    }

    private void initData() {
        CurrencyInOutPresenter currencyInOutPresenter = new CurrencyInOutPresenter(this);
        currencyInOutPresenter.inCoin(mId, new CurrencyInOutPresenter.CallBack() {
            @Override
            public void send(BaseInfo.DataBean data) {
                Glide.with(InCoinActivity.this).load(data.getUrl()).into(mIvSiteQr);
                mTvSite.setText(data.getAddress());
            }
        });
    }

    @OnClick({R.id.bark, R.id.tv_record, R.id.btn_copy})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;
            case R.id.tv_record:
//                startActivity(new Intent(this));
                break;
            case R.id.btn_copy:
                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                cm.setText(mTvSite.getText().toString());
                Toast.makeText(this, "复制成功", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
