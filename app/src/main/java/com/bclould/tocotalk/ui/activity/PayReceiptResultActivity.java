package com.bclould.tocotalk.ui.activity;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bclould.tocotalk.R;
import com.bclould.tocotalk.base.BaseActivity;
import com.bclould.tocotalk.history.DBManager;
import com.bclould.tocotalk.utils.Constants;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by GA on 2018/3/28.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class PayReceiptResultActivity extends BaseActivity {

    @Bind(R.id.iv_result)
    ImageView mIvResult;
    @Bind(R.id.tv_pay_type)
    TextView mTvPayType;
    @Bind(R.id.tv_coin_count)
    TextView mTvCoinCount;
    @Bind(R.id.tv_who)
    TextView mTvWho;
    @Bind(R.id.tv_touxiang)
    ImageView mTvTouxiang;
    @Bind(R.id.tv_name)
    TextView mTvName;
    @Bind(R.id.btn_finish)
    Button mBtnFinish;
    private DBManager mMgr;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_receipt_result);
        ButterKnife.bind(this);
        mMgr = new DBManager(this);
        initIntent();
    }

    private void initIntent() {
        Bundle bundle = getIntent().getExtras();
        String coinName = bundle.getString("coinName");
        String name = bundle.getString("name");
        String date = bundle.getString("date");
        String number = bundle.getString("number");
        String type = bundle.getString("type");
        if (type.equals(Constants.MONEYIN)) {
            mTvWho.setText("收款人");
            mIvResult.setImageResource(R.mipmap.icon_pay_c1);
            mTvPayType.setText("支付成功");
        } else {
            mIvResult.setImageResource(R.mipmap.icon_get_c1);
            mTvWho.setText("付款人");
            mTvPayType.setText("收款成功");
        }
        mTvCoinCount.setText(number + coinName);
        mTvName.setText(name);
        try {
            Drawable drawable = Drawable.createFromPath(mMgr.queryUser(name + "@" + Constants.DOMAINNAME).get(0).getPath());
            mTvTouxiang.setImageDrawable(drawable);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.btn_finish)
    public void onViewClicked() {
        finish();
    }
}

