package com.bclould.tocotalk.ui.activity;

import android.content.Context;
import android.content.Intent;
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
import android.widget.TextView;
import android.widget.Toast;

import com.bclould.tocotalk.Presenter.LoginPresenter;
import com.bclould.tocotalk.R;
import com.bclould.tocotalk.base.MyApp;
import com.bclould.tocotalk.utils.AnimatorTool;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by GA on 2017/9/20.
 */

public class LoginActivity extends AppCompatActivity {

    @Bind(R.id.et_emily)
    EditText mEtEmily;
    @Bind(R.id.et_password)
    EditText mEtPassword;
    @Bind(R.id.iv_eye)
    ImageView mIvEye;
    @Bind(R.id.tv_find_password)
    TextView mTvFindPassword;
    @Bind(R.id.btn_login)
    Button mBtnLogin;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login2);

        ButterKnife.bind(this);

        setEdit();

//        showCaptcha();
        MyApp.getInstance().addActivity(this);

    }

    //不能回车
    private void setEdit() {
        mEtEmily.setOnEditorActionListener(new TextView.OnEditorActionListener() {
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


    @OnClick({R.id.iv_eye, R.id.tv_find_password, R.id.btn_login, R.id.tv_register})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_eye:
                isEye = !isEye;
                showHidePassword();//隐藏显示密码
                break;
            case R.id.tv_find_password:
                startActivity(new Intent(this, FindPasswordActivity.class));
                break;
            case R.id.tv_register:
                startActivity(new Intent(this, RegisterActivity.class));
                break;
            case R.id.btn_login:
                //判断邮箱、密码、验证码是否为空，格式是否正确
                if (checkEdit()) {
                    login();

                }
                break;
        }
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

    //登录
    private void login() {
        String emil = mEtEmily.getText().toString();
        String password = mEtPassword.getText().toString();
        LoginPresenter loginPresenter = new LoginPresenter(this);
        loginPresenter.Login(emil, password);
    }

    //验证手机号和密码
    private boolean checkEdit() {
        if (mEtEmily.getText().toString().trim().equals("")) {
            Toast.makeText(this, getResources().getString(R.string.toast_email), Toast.LENGTH_SHORT).show();
            AnimatorTool.getInstance().editTextAnimator(mEtEmily);
        } else if (mEtPassword.getText().toString().trim().equals("")) {
            Toast.makeText(this, getResources().getString(R.string.toast_password), Toast.LENGTH_SHORT).show();
            AnimatorTool.getInstance().editTextAnimator(mEtPassword);
        } else {
            return true;
        }
        return false;
    }
}
