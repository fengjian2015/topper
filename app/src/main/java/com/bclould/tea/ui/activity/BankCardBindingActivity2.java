package com.bclould.tea.ui.activity;

import android.content.Context;
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

import com.bclould.tea.Presenter.BankCardPresenter;
import com.bclould.tea.R;
import com.bclould.tea.base.BaseActivity;
import com.bclould.tea.base.MyApp;
import com.bclould.tea.model.BankCardInfo;
import com.bclould.tea.model.BaseInfo;
import com.bclould.tea.utils.AnimatorTool;
import com.bclould.tea.utils.AppLanguageUtils;
import com.bclould.tea.utils.MessageEvent;

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
    @Bind(R.id.et_cardholder)
    EditText mEtCardholder;
    @Bind(R.id.tv_bank_number)
    TextView mTvBankNumber;
    @Bind(R.id.et_card_type)
    EditText mEtCardType;
    @Bind(R.id.et_opening_bank)
    EditText mEtOpeningBank;
    @Bind(R.id.btn_next)
    Button mBtnNext;
    private BankCardInfo.DataBean mData;
    private String mCardNumber;
    private BankCardPresenter mBankCardPresenter;

    private int mState_id;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_card_binding2);
        ButterKnife.bind(this);
        MyApp.getInstance().addActivity(this);
        initIntent();
        mBankCardPresenter = new BankCardPresenter(this);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(AppLanguageUtils.attachBaseContext(newBase, newBase.getString(R.string.language_pref_key)));
    }

    private void initIntent() {
        Intent intent = getIntent();
        boolean type = intent.getBooleanExtra("type", false);
        mState_id = intent.getIntExtra("country_id", 0);
        mCardNumber = intent.getStringExtra("card_number");
        mTvBankNumber.setText(mCardNumber);
        if (type) {
            mData = (BankCardInfo.DataBean) intent.getSerializableExtra("data");
            if (!mData.getBank().isEmpty()) {
                mEtCardType.setText(mData.getBank());
                mEtCardType.setKeyListener(null);
            }
            if (!mData.getTruename().isEmpty()) {
                mEtCardholder.setText(mData.getTruename());
                mEtCardholder.setKeyListener(null);
            }
        } else {
            String card_type = intent.getStringExtra("card_type");
            String bank_name = intent.getStringExtra("bank_name");
            if (!bank_name.isEmpty()) {
                mEtCardType.setText(bank_name);
                mEtCardType.setKeyListener(null);
            }
        }
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
        String bankType = mEtCardType.getText().toString().trim();
        String truename = mEtCardholder.getText().toString().trim();
        mBankCardPresenter.bindBankCard(truename, bankType, openingBank, mCardNumber, mState_id, new BankCardPresenter.CallBack3() {
            @Override
            public void send(BaseInfo data) {
                if (data.getStatus() == 1) {
                    finish();
                    Toast.makeText(BankCardBindingActivity2.this, getString(R.string.binding_succeed), Toast.LENGTH_SHORT).show();
                    EventBus.getDefault().post(new MessageEvent(getString(R.string.bank_binding_unbinding)));
                } else {
                    Toast.makeText(BankCardBindingActivity2.this, getString(R.string.binding_error), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean checkEdit() {
        if (mEtOpeningBank.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, getString(R.string.toast_open_bank), Toast.LENGTH_SHORT).show();
            AnimatorTool.getInstance().editTextAnimator(mEtOpeningBank);
        } else if (mEtCardType.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, getString(R.string.toast_card_type), Toast.LENGTH_SHORT).show();
            AnimatorTool.getInstance().editTextAnimator(mEtCardType);
        }else if (mEtCardholder.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, getString(R.string.toast_card_holder), Toast.LENGTH_SHORT).show();
            AnimatorTool.getInstance().editTextAnimator(mEtCardholder);
        }else {
            return true;
        }
        return false;
    }
}
