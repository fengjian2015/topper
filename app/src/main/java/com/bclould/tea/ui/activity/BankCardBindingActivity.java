package com.bclould.tea.ui.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bclould.tea.Presenter.BankCardPresenter;
import com.bclould.tea.Presenter.RealNamePresenter;
import com.bclould.tea.R;
import com.bclould.tea.base.BaseActivity;
import com.bclould.tea.base.MyApp;
import com.bclould.tea.model.BankCardInfo;
import com.bclould.tea.ui.widget.DeleteCacheDialog;
import com.bclould.tea.utils.AnimatorTool;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by GA on 2017/9/26.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class BankCardBindingActivity extends BaseActivity {

    @Bind(R.id.bark)
    ImageView mBark;
    @Bind(R.id.btn_next)
    Button mBtnNext;
    @Bind(R.id.et_card_number)
    EditText mEtCardNumber;
    private BankCardPresenter mBankCardPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_card_binding);
        ButterKnife.bind(this);
        MyApp.getInstance().addActivity(this);
        mBankCardPresenter = new BankCardPresenter(this);
        initData();
    }

    private void initData() {
        RealNamePresenter realNamePresenter = new RealNamePresenter(this);
        realNamePresenter.realNameInfo(new RealNamePresenter.CallBack2() {
            @Override
            public void send(int type, String mark) {
                if (type == 1) {
                    showDialog();
                    mBtnNext.setClickable(false);
                } else if (type == 4) {
                    showDialog();
                    mBtnNext.setClickable(false);
                    Toast.makeText(BankCardBindingActivity.this, getString(R.string.verify_error_hint), Toast.LENGTH_SHORT).show();
                } else if (type == 2) {
                    mBtnNext.setClickable(false);
                    Toast.makeText(BankCardBindingActivity.this, getString(R.string.verify_check_pending), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void error() {

            }
        });
    }


    //验证手机号和密码
    private boolean checkEdit() {
        if (mEtCardNumber.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, getString(R.string.toast_card_number), Toast.LENGTH_SHORT).show();
            AnimatorTool.getInstance().editTextAnimator(mEtCardNumber);
        } else {
            return true;
        }
        return false;
    }

    @OnClick({R.id.bark, R.id.btn_next})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;
            case R.id.btn_next:
                if (checkEdit()) {
                    next();
                }
                break;
        }
    }

    private void next() {
        final String cardNumber = mEtCardNumber.getText().toString().trim();
        mBankCardPresenter.bankCardInfo(cardNumber, new BankCardPresenter.CallBack() {
            @Override
            public void send(BankCardInfo.DataBean data) {
                if (!BankCardBindingActivity.this.isDestroyed()) {
                    Intent intent = new Intent(BankCardBindingActivity.this, BankCardBindingActivity2.class);
                    if (data.getTruename() != null && data.getTruename().isEmpty()) {
                        showDialog();
                    } else {
                        intent.putExtra("data", data);
                        intent.putExtra("cardNumber", cardNumber);
                        startActivity(intent);
                        finish();
                    }
                }
            }
        });
    }

    private void showDialog() {
        final DeleteCacheDialog deleteCacheDialog = new DeleteCacheDialog(R.layout.dialog_delete_cache, this, R.style.dialog);
        deleteCacheDialog.show();
        deleteCacheDialog.setCanceledOnTouchOutside(false);
        deleteCacheDialog.setTitle(getString(R.string.real_name_authentication_hint));
        Button cancel = (Button) deleteCacheDialog.findViewById(R.id.btn_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteCacheDialog.dismiss();
            }
        });
        Button confirm = (Button) deleteCacheDialog.findViewById(R.id.btn_confirm);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteCacheDialog.dismiss();
                startActivity(new Intent(BankCardBindingActivity.this, RealNameC1Activity.class));
            }
        });

    }
}
