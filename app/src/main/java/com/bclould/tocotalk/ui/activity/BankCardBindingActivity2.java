package com.bclould.tocotalk.ui.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bclould.tocotalk.Presenter.BankCardPresenter;
import com.bclould.tocotalk.R;
import com.bclould.tocotalk.base.BaseActivity;
import com.bclould.tocotalk.base.MyApp;
import com.bclould.tocotalk.model.BankCardInfo;
import com.bclould.tocotalk.utils.AnimatorTool;
import com.bclould.tocotalk.utils.MessageEvent;

import org.greenrobot.eventbus.EventBus;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by GA on 2017/9/27.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class BankCardBindingActivity2 extends BaseActivity {


    @Bind(R.id.bark)
    ImageView mBark;
    @Bind(R.id.tv_card_type)
    TextView mTvCardType;
    @Bind(R.id.et_opening_bank)
    EditText mEtOpeningBank;
    @Bind(R.id.tv_cardholder)
    TextView mTvCardholder;
    @Bind(R.id.tv_card_number)
    TextView mTvCardNumber;
    @Bind(R.id.btn_next)
    Button mBtnNext;
    private BankCardInfo.DataBean mData;
    private String mCardNumber;
    private BankCardPresenter mBankCardPresenter;

    public static BankCardBindingActivity2 instance = null;
    public static BankCardBindingActivity2 getInstance() {
        if (instance == null) {
            instance = new BankCardBindingActivity2();
        }
        return instance;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_card_binding2);
        ButterKnife.bind(this);
        MyApp.getInstance().addActivity(this);
        initIntent();
        mBankCardPresenter = new BankCardPresenter(this);
    }

    private void initIntent() {
        Intent intent = getIntent();
        mData = (BankCardInfo.DataBean) intent.getSerializableExtra("data");
        mCardNumber = intent.getStringExtra("cardNumber");
        mTvCardholder.setText(mData.getTruename());
        mTvCardNumber.setText(mData.getCard_number());
        mTvCardType.setText(mData.getBank());
    }

    @OnClick({R.id.bark, R.id.btn_next})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;
            case R.id.btn_next:
                if (checkEdit()) {
                    submit();
                }
                break;
        }
    }

    private void submit() {
        String openingBank = mEtOpeningBank.getText().toString().trim();
        mBankCardPresenter.bindBankCard(mData.getTruename(), mData.getBank(), openingBank, mCardNumber, new BankCardPresenter.CallBack3() {
            @Override
            public void send(int status) {
                if (status == 1) {
                    finish();
                    Toast.makeText(BankCardBindingActivity2.this, "绑定成功", Toast.LENGTH_SHORT).show();
                    EventBus.getDefault().post(new MessageEvent("银行卡绑定解绑"));
                } else {
                    Toast.makeText(BankCardBindingActivity2.this, "绑定失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean checkEdit() {
        if (mEtOpeningBank.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "开户行不能为空", Toast.LENGTH_SHORT).show();
            AnimatorTool.getInstance().editTextAnimator(mEtOpeningBank);
        }else {
            return true;
        }
        return false;
    }
}
