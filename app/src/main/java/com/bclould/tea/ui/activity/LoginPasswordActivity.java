package com.bclould.tea.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bclould.tea.Presenter.LoginPasswordPresenter;
import com.bclould.tea.Presenter.RegisterPresenter;
import com.bclould.tea.R;
import com.bclould.tea.base.BaseActivity;
import com.bclould.tea.utils.AnimatorTool;
import com.bclould.tea.utils.StringUtils;
import com.bclould.tea.utils.UtilTool;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by GA on 2017/10/11.
 */

public class LoginPasswordActivity extends BaseActivity {

    @Bind(R.id.et_pay_password)
    EditText mEtPayPassword;
    @Bind(R.id.eye)
    ImageView mEye;
    @Bind(R.id.rl_pay_pw)
    RelativeLayout mRlPayPw;
    @Bind(R.id.et_vcode)
    EditText mEtVcode;
    @Bind(R.id.btn_finish)
    Button mBtnFinish;
    @Bind(R.id.tv_send)
    TextView mTvSend;

    private int mRecLen;
    Timer mTimer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_password);
        ButterKnife.bind(this);
        setTitle(getString(R.string.login_pw));
    }


    //提交修改的密码
    private void submit() {
        String vocde = mEtVcode.getText().toString().trim();
        String password = mEtPayPassword.getText().toString().trim();
        LoginPasswordPresenter loginPasswordPresenter = new LoginPasswordPresenter(this);
        loginPasswordPresenter.submit(vocde, password, password);

    }

    boolean isEye = false;

    @OnClick({R.id.bark, R.id.eye, R.id.btn_finish,R.id.tv_send})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;
            case R.id.eye:
                isEye = !isEye;
                eyeShowHidden(isEye);
                break;
            case R.id.btn_finish:
                if (checkEdit()) {
                    submit();
                }
                break;
            case R.id.tv_send:
                sendVcode();
                break;
        }
    }

    //输入框的文本显示和隐藏
    private void eyeShowHidden(boolean isEye) {
        if (isEye) {
            mEye.setSelected(true);
            mEtPayPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            mEtPayPassword.setSelection(mEtPayPassword.length());
        } else {
            mEye.setSelected(false);
            mEtPayPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            mEtPayPassword.setSelection(mEtPayPassword.length());
        }
    }

    //验证手机号和密码
    private boolean checkEdit() {
        String password = mEtPayPassword.getText().toString().trim();
        if (StringUtils.isEmpty(password)) {
            Toast.makeText(this, getString(R.string.toast_password), Toast.LENGTH_SHORT).show();
            AnimatorTool.getInstance().editTextAnimator(mEtPayPassword);
        } else if (mEtVcode.getText().toString().trim().equals("")) {
            Toast.makeText(this, getString(R.string.toast_vcode), Toast.LENGTH_SHORT).show();
            AnimatorTool.getInstance().editTextAnimator(mEtVcode);
        } else {
            return true;
        }
        return false;
    }

    private void sendVcode() {
        new RegisterPresenter(this).sendRegcode(UtilTool.getEmail(), new RegisterPresenter.CallBack() {
            @Override
            public void send() {
                Toast.makeText(LoginPasswordActivity.this, getString(R.string.send_succeed), Toast.LENGTH_SHORT).show();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }
}
