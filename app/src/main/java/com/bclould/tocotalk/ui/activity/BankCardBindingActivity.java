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
import android.widget.Toast;

import com.bclould.tocotalk.Presenter.BankCardPresenter;
import com.bclould.tocotalk.Presenter.RealNamePresenter;
import com.bclould.tocotalk.R;
import com.bclould.tocotalk.base.BaseActivity;
import com.bclould.tocotalk.base.MyApp;
import com.bclould.tocotalk.model.BankCardInfo;
import com.bclould.tocotalk.ui.widget.DeleteCacheDialog;
import com.bclould.tocotalk.utils.AnimatorTool;

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
        realNamePresenter.realNameInfo(new RealNamePresenter.CallBack() {
            @Override
            public void send(String message) {
                if (message.equals("未认证")) {
                    showDialog();
                    mBtnNext.setClickable(false);
                } else if (message.equals("认证失败")) {
                    showDialog();
                    mBtnNext.setClickable(false);
                    Toast.makeText(BankCardBindingActivity.this, "实名认证失败，请重新认证", Toast.LENGTH_SHORT).show();
                } else if (message.equals("待审核")) {
                    mBtnNext.setClickable(false);
                    Toast.makeText(BankCardBindingActivity.this, "实名认证审核中", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    //验证手机号和密码
    private boolean checkEdit() {
        if (mEtCardNumber.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "卡号不能为空", Toast.LENGTH_SHORT).show();
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
        });
    }

    private void showDialog() {
        final DeleteCacheDialog deleteCacheDialog = new DeleteCacheDialog(R.layout.dialog_delete_cache, this);
        deleteCacheDialog.show();
        deleteCacheDialog.setCanceledOnTouchOutside(false);
        deleteCacheDialog.setTitle("您还没有实名认证，请先去实名认证");
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
