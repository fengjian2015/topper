package com.bclould.tea.ui.activity;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
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

import com.bclould.tea.Presenter.FindPasswordPresenter;
import com.bclould.tea.Presenter.RegisterPresenter;
import com.bclould.tea.R;
import com.bclould.tea.base.LoginBaseActivity;
import com.bclould.tea.base.MyApp;
import com.bclould.tea.base.SwipeActivity;
import com.bclould.tea.utils.AnimatorTool;
import com.bclould.tea.utils.AppLanguageUtils;
import com.bclould.tea.utils.MySharedPreferences;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by GA on 2017/11/1.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class FindPasswordActivity extends LoginBaseActivity {


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
    private RegisterPresenter mRegisterPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_password);
        ButterKnife.bind(this);
        mFindPasswordPresenter = new FindPasswordPresenter(this);
        mRegisterPresenter = new RegisterPresenter(this);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }

    private int mRecLen = 60;
    Timer mTimer = new Timer();

    private void sendVcode() {
        mRegisterPresenter.sendRegcode(mEtEmail.getText().toString(), new RegisterPresenter.CallBack() {
            @Override
            public void send() {
                mRecLen = 60;
                mTimer = new Timer();
                mTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {

                        runOnUiThread(new Runnable() {      // UI thread
                            @Override
                            public void run() {
                                mRecLen--;
                                mTvSend.setText(mRecLen + "s" + getString(R.string.back_send));
                                if (mRecLen <= 0) {
                                    if (mTimer != null) {
                                        mTimer.cancel();
                                        mTimer = null;
                                    }
                                    mTvSend.setEnabled(true);
                                    mTvSend.setText(getString(R.string.send));
                                } else {
                                    mTvSend.setEnabled(false);
                                }
                            }
                        });
                    }
                }, 1000, 1000);
            }
        });
    }

    private void submit() {
        String email = mEtEmail.getText().toString();
        String password = mEtPassword.getText().toString();
        String vcode = mEtVcode.getText().toString();
        mFindPasswordPresenter.submit(vcode, email, password);
    }
}
