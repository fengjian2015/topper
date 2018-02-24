package com.dashiji.biyun.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.dashiji.biyun.base.MyApp;
import com.dashiji.biyun.R;
import com.dashiji.biyun.base.BaseActivity;
import com.dashiji.biyun.utils.MySharedPreferences;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by GA on 2017/9/26.
 */

public class BankCardBindingActivity extends BaseActivity {

    public static final String BANKCARDNUMBER = "bank_card_number";
    @Bind(R.id.bark)
    ImageView mBark;
    @Bind(R.id.real_name)
    EditText mRealName;
    @Bind(R.id.bank_card_number)
    EditText mBankCardNumber;
    @Bind(R.id.btn_next)
    Button mBtnNext;
    private String mRealNames;
    private String mIdCardNumbers;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_card_binding);
        ButterKnife.bind(this);
        mRealNames = MySharedPreferences.getInstance().getString(RealNameActivity.REALNAME);
        mRealName.setHint(mRealNames);
        MyApp.getInstance().addActivity(this);
    }

    //点击事件的处理
    @OnClick({R.id.bark, R.id.btn_next})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:

                finish();

                break;
            case R.id.btn_next:
                if (checkEdit()) {
                    startActivity(new Intent(this, BankCardBindingActivity2.class));
                    finish();
                }
                break;
        }
    }

    //验证手机号和密码
    private boolean checkEdit() {
        if (mRealName.getText().toString().trim().isEmpty()) {
            if (mRealNames.isEmpty()) {
                Toast.makeText(this, "姓名不能为空", Toast.LENGTH_SHORT).show();
            } else {
                return true;
            }
        } else if (mBankCardNumber.getText().toString().isEmpty()) {
            Toast.makeText(this, "卡号不能为空", Toast.LENGTH_SHORT).show();
        } else {
            return true;
        }
        return false;
    }
}
