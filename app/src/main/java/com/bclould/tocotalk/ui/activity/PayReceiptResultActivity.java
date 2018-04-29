package com.bclould.tocotalk.ui.activity;

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
import com.bclould.tocotalk.utils.UtilTool;

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
            mTvWho.setText(getString(R.string.sk_person));
            mIvResult.setImageResource(R.mipmap.icon_pay_c1);
            mTvPayType.setText(getString(R.string.payment_succeed));
        } else {
            mIvResult.setImageResource(R.mipmap.icon_get_c1);
            mTvWho.setText(getString(R.string.fk_person));
            mTvPayType.setText(getString(R.string.sk_succeed));
        }
        mTvCoinCount.setText(number + coinName);
        mTvName.setText(name);
        String jid = name + "@" + Constants.DOMAINNAME;
        mTvTouxiang.setImageBitmap(UtilTool.getImage(mMgr, jid, PayReceiptResultActivity.this));
    }

    @OnClick(R.id.btn_finish)
    public void onViewClicked() {
        finish();
    }
}

