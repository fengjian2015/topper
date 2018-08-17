package com.bclould.tea.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bclould.tea.R;
import com.bclould.tea.base.BaseActivity;
import com.bclould.tea.base.MyApp;
import com.bclould.tea.utils.AppLanguageUtils;
import com.bclould.tea.utils.MySharedPreferences;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by GA on 2017/9/27.
 */

public class EmailBindingActivity extends BaseActivity {
    public static final String PHONE = "phone";
    @Bind(R.id.bark)
    ImageView mBark;
    @Bind(R.id.phone)
    EditText mPhone;
    @Bind(R.id.yz_code)
    EditText mYzCode;
    @Bind(R.id.send_message)
    Button mSendMessage;
    @Bind(R.id.btn_submit)
    Button mBtnSubmit;
    @Bind(R.id.ll_binding_number)
    LinearLayout mLlBindingNumber;
    @Bind(R.id.binding_phone_number)
    TextView mBindingPhoneNumber;
    @Bind(R.id.change_phone_number)
    Button mChangePhoneNumber;
    @Bind(R.id.ll_change_number)
    LinearLayout mLlChangeNumber;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_binding_phone);
        ButterKnife.bind(this);
        initInterface();
        MyApp.getInstance().addActivity(this);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(AppLanguageUtils.attachBaseContext(newBase, MySharedPreferences.getInstance().getString(newBase.getString(R.string.language_pref_key))));
    }

    private void initInterface() {

        String phone = MySharedPreferences.getInstance().getString(PHONE);

        if (phone != null) {

            mLlChangeNumber.setVisibility(View.VISIBLE);

            mLlBindingNumber.setVisibility(View.GONE);

        } else {

            mLlChangeNumber.setVisibility(View.GONE);

            mLlBindingNumber.setVisibility(View.VISIBLE);

        }

        mBindingPhoneNumber.setText(getString(R.string.present_phone_number) + phone);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {

            mLlBindingNumber.setVisibility(View.VISIBLE);

            mLlChangeNumber.setVisibility(View.GONE);

        }
    }

    @OnClick({R.id.bark, R.id.send_message, R.id.btn_submit, R.id.change_phone_number})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:

                finish();

                break;
            case R.id.send_message:
                break;
            case R.id.change_phone_number:

                startActivityForResult(new Intent(this, VerifyCodeActivity.class), 1);

                break;
            case R.id.btn_submit:

                String phone = mPhone.getText().toString().trim();

                String yzCode = mYzCode.getText().toString().trim();

                if (phone.isEmpty() && yzCode.isEmpty()) {

                    Toast.makeText(this, "手机号码或者验证码不能为空", Toast.LENGTH_SHORT).show();

                } else {

                    MySharedPreferences.getInstance().setString(PHONE, phone);

                    finish();

                }

                break;
        }
    }
}
