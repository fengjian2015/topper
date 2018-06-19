package com.bclould.tea.ui.activity;

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

import com.bclould.tea.base.MyApp;
import com.bclould.tea.R;
import com.bclould.tea.base.BaseActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by GA on 2017/10/10.
 */

public class NickNameActivity extends BaseActivity {

    @Bind(R.id.bark)
    ImageView mBark;
    @Bind(R.id.confirm)
    Button mConfirm;
    @Bind(R.id.nickname)
    EditText mNickname;
    @Bind(R.id.text_count)
    TextView mTextCount;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nick_name);
        ButterKnife.bind(this);
        listenerEditText();
        MyApp.getInstance().addActivity(this);
    }

    //监听输入框
    private void listenerEditText() {

        mNickname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int before, int count) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int count, int after) {

                if (mNickname.getText().length() != 0) {

                    mTextCount.setTextColor(Color.GRAY);

                    mTextCount.setText("(还可以输入" + (15 - mNickname.getText().length() + "个字)"));

                    if (15 - mNickname.getText().length() == 0) {

                        mTextCount.setTextColor(getResources().getColor(R.color.red));

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
