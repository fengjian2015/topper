package com.dashiji.biyun.ui.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dashiji.biyun.Presenter.FindPasswordPresenter;
import com.dashiji.biyun.R;
import com.dashiji.biyun.base.MyApp;
import com.dashiji.biyun.utils.AnimatorTool;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by GA on 2017/11/1.
 */

public class FindPasswordActivity extends AppCompatActivity {

    @Bind(R.id.et_email)
    EditText mEtEmail;
    @Bind(R.id.btn_next)
    Button mBtnNext;
    @Bind(R.id.ll_step_one)
    LinearLayout mLlStepOne;
    @Bind(R.id.et_password)
    EditText mEtPassword;
    @Bind(R.id.iv_eye)
    ImageView mIvEye;
    @Bind(R.id.et_email_code)
    EditText mEtEmailCode;
    @Bind(R.id.btn_last_step)
    Button mBtnLastStep;
    @Bind(R.id.btn_submit)
    Button mBtnSubmit;
    @Bind(R.id.ll_step_tow)
    LinearLayout mLlStepTow;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_password);
        ButterKnife.bind(this);
        setEdit();
        MyApp.getInstance().addActivity(this);
    }

    //不能回车
    private void setEdit() {
        mEtEmail.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                return true;
            }
        });
        mEtEmailCode.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                return true;
            }
        });

        mEtPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                return true;
            }
        });
    }

    //监听返回键
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideInput(v, ev)) {

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
            return super.dispatchTouchEvent(ev);
        }
        // 必不可少，否则所有的组件都不会有TouchEvent了
        if (getWindow().superDispatchTouchEvent(ev)) {
            return true;
        }
        return onTouchEvent(ev);
    }

    public boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] leftTop = {0, 0};
            //获取输入框当前的location位置
            v.getLocationInWindow(leftTop);
            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + v.getHeight();
            int right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击的是输入框区域，保留点击EditText的事件
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    //验证手机号和密码
    private boolean checkEdit() {
        if (mEtEmail.getText().toString().trim().equals("")) {
            Toast.makeText(this, getResources().getString(R.string.toast_email), Toast.LENGTH_SHORT).show();
            AnimatorTool.getInstance().editTextAnimator(mEtEmail);
        } else {
            return true;
        }
        return false;
    }

    //显示隐藏密码
    boolean isEye = false;

    private void showHidePassword() {
        if (isEye) {
            mIvEye.setSelected(true);
            mEtPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            mEtPassword.setSelection(mEtPassword.length());
        } else {
            mEtPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            mEtPassword.setSelection(mEtPassword.length());
            mIvEye.setSelected(false);
        }
    }

    @OnClick({R.id.btn_next, R.id.iv_eye, R.id.btn_last_step, R.id.btn_submit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_next:
                if (checkEdit()) {
                    FindPasswordPresenter findPasswordPresenter = new FindPasswordPresenter(this);
                    findPasswordPresenter.sendRegcode(mEtEmail.getText().toString(), new FindPasswordPresenter.CallBack() {
                        @Override
                        public void send() {
                            mLlStepOne.setVisibility(View.GONE);
                            mLlStepTow.setVisibility(View.VISIBLE);
                        }
                    });
                }
                break;
            case R.id.iv_eye:
                isEye = !isEye;
                showHidePassword();
                break;
            case R.id.btn_last_step:
                mLlStepOne.setVisibility(View.VISIBLE);
                mLlStepTow.setVisibility(View.GONE);
                break;
            case R.id.btn_submit:
                if (checkEdit2()) {
                    String email = mEtEmail.getText().toString();
                    String password = mEtPassword.getText().toString();
                    String emailCode = mEtEmailCode.getText().toString();
                    FindPasswordPresenter findPasswordPresenter = new FindPasswordPresenter(this);
                    findPasswordPresenter.submit(emailCode, email, password);
                }
                break;
        }
    }

    //验证手机号和密码
    private boolean checkEdit2() {
        if (mEtEmailCode.getText().toString().trim().equals("")) {
            Toast.makeText(this, getResources().getString(R.string.toast_vcode), Toast.LENGTH_SHORT).show();
            AnimatorTool.getInstance().editTextAnimator(mEtEmailCode);
        } else if (mEtPassword.getText().toString().trim().equals("")) {
            Toast.makeText(this, getResources().getString(R.string.toast_password), Toast.LENGTH_SHORT).show();
            AnimatorTool.getInstance().editTextAnimator(mEtPassword);
        } else {
            return true;
        }
        return false;
    }
}
