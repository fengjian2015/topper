package com.dashiji.biyun.ui.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.dashiji.biyun.base.MyApp;
import com.dashiji.biyun.R;
import com.dashiji.biyun.base.BaseActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by GA on 2017/10/10.
 */

public class SignatureActivity extends BaseActivity {

    @Bind(R.id.bark)
    ImageView mBark;
    @Bind(R.id.confirm)
    Button mConfirm;
    @Bind(R.id.signaturen)
    EditText mSignaturen;
    @Bind(R.id.text_count)
    TextView mTextCount;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signature);
        ButterKnife.bind(this);
        listenerEditText();
        MyApp.getInstance().addActivity(this);
    }

    //监听输入框
    private void listenerEditText() {

        mSignaturen.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int before, int count) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int count, int after) {

                if (mSignaturen.getText().length() != 0) {

                    mTextCount.setTextColor(Color.GRAY);

                    mTextCount.setText("(还可以输入" + (30 - mSignaturen.getText().length() + "个字)"));

                    if (30 - mSignaturen.getText().length() == 0) {

                        mTextCount.setTextColor(Color.RED);

                        mTextCount.setText("(不能再输入了！)");

                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {


            }
        });

    }


    @OnClick({R.id.bark, R.id.confirm})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:

                finish();

                break;
            case R.id.confirm:

                finish();

                break;
        }
    }
}
