package com.bclould.tocotalk.ui.activity;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bclould.tocotalk.Presenter.FindPasswordPresenter;
import com.bclould.tocotalk.R;
import com.bclould.tocotalk.base.MyApp;
import com.bclould.tocotalk.utils.AnimatorTool;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by GA on 2017/11/1.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class FindPasswordActivity extends AppCompatActivity {


    @Bind(R.id.iv_back)
    ImageView mIvBack;
    @Bind(R.id.tv_find_password)
    TextView mTvFindPassword;
    @Bind(R.id.et_email)
    EditText mEtEmail;
    @Bind(R.id.et_vcode)
    EditText mEtVcode;
    @Bind(R.id.tv_send)
    TextView mTvSend;
    @Bind(R.id.et_password)
    EditText mEtPassword;
    @Bind(R.id.eye)
    ImageView mEye;
    @Bind(R.id.btn_login)
    Button mBtnLogin;
    private FindPasswordPresenter mFindPasswordPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_password);
        ButterKnife.bind(this);
        MyApp.getInstance().addActivity(this);
        mFindPasswordPresenter = new FindPasswordPresenter(this);
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
        } else if (mEtVcode.getText().toString().trim().equals("")) {
            Toast.makeText(this, getResources().getString(R.string.toast_vcode), Toast.LENGTH_SHORT).show();
            AnimatorTool.getInstance().editTextAnimator(mEtVcode);
        } else if (mEtPassword.getText().toString().trim().equals("")) {
            Toast.makeText(this, getResources().getString(R.string.toast_password), Toast.LENGTH_SHORT).show();
            AnimatorTool.getInstance().editTextAnimator(mEtPassword);
        } else if (!mEtEmail.getText().toString().contains("@")) {
            Toast.makeText(this, getResources().getString(R.string.toast_email_format), Toast.LENGTH_SHORT).show();
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
            mEye.setSelected(true);
            mEtPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            mEtPassword.setSelection(mEtPassword.length());
        } else {
            mEtPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            mEtPassword.setSelection(mEtPassword.length());
            mEye.setSelected(false);
        }
    }

    @OnClick({R.id.iv_back, R.id.tv_send, R.id.eye, R.id.btn_login})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_send:
                if (mEtEmail.getText().toString().isEmpty()) {
                    Toast.makeText(this, getResources().getString(R.string.toast_email), Toast.LENGTH_SHORT).show();
                    AnimatorTool.getInstance().editTextAnimator(mEtEmail);
                    sendVcode();
                } else if (!mEtEmail.getText().toString().contains("@")) {
                    Toast.makeText(this, getResources().getString(R.string.toast_email_format), Toast.LENGTH_SHORT).show();
                    AnimatorTool.getInstance().editTextAnimator(mEtEmail);
                } else {
                    sendVcode();
                }
                break;
            case R.id.eye:
                isEye = !isEye;
                showHidePassword();
                break;
            case R.id.btn_login:
                if (checkEdit()) {
                    submit();
                }
                break;
        }
    }

    private void sendVcode() {
        mFindPasswordPresenter.sendRegcode(mEtEmail.getText().toString());
    }

    private void submit() {
        String email = mEtEmail.getText().toString();
        String password = mEtPassword.getText().toString();
        String vcode = mEtVcode.getText().toString();
        mFindPasswordPresenter.submit(vcode, email, password);
    }
}
