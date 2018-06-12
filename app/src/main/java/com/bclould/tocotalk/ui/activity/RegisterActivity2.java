package com.bclould.tocotalk.ui.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bclould.tocotalk.Presenter.RegisterPresenter;
import com.bclould.tocotalk.R;
import com.bclould.tocotalk.utils.AnimatorTool;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by GA on 2018/6/6.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class RegisterActivity2 extends AppCompatActivity {
    @Bind(R.id.iv_back)
    ImageView mIvBack;
    @Bind(R.id.tv_login)
    TextView mTvLogin;
    @Bind(R.id.et_vcode)
    EditText mEtVcode;
    @Bind(R.id.tv_send)
    TextView mTvSend;
    @Bind(R.id.et_password)
    EditText mEtPassword;
    @Bind(R.id.eye)
    ImageView mEye;
    @Bind(R.id.btn_register)
    Button mBtnRegister;
    private RegisterPresenter mRegisterPresenter;
    private String mUsername;
    private String mEmail;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register2);
        ButterKnife.bind(this);
        mRegisterPresenter = new RegisterPresenter(this);
        initIntent();
    }

    private void initIntent() {
        mUsername = getIntent().getStringExtra("username");
        mEmail = getIntent().getStringExtra("email");
    }

    @OnClick({R.id.iv_back, R.id.tv_send, R.id.eye, R.id.btn_register})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_send:
                sendVcode();
                break;
            case R.id.eye:
                isEye = !isEye;
                showHidePassword();//隐藏显示密码
                break;
            case R.id.btn_register:
                if (checkEdit()) {
                    register();
                }
                break;
        }
    }

    private void sendVcode() {
        mRegisterPresenter.signUpValidator(mEmail, mUsername, new RegisterPresenter.CallBack2() {
            @Override
            public void send() {
            }
        });
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

    //验证邮箱和图形验证码
    private boolean checkEdit() {
        if (mEtVcode.getText().toString().trim().equals("")) {
            Toast.makeText(this, getResources().getString(R.string.toast_vcode), Toast.LENGTH_SHORT).show();
            AnimatorTool.getInstance().editTextAnimator(mEtVcode);
        } else if (mEtPassword.getText().toString().trim().equals("")) {
            Toast.makeText(this, getResources().getString(R.string.toast_password), Toast.LENGTH_SHORT).show();
            AnimatorTool.getInstance().editTextAnimator(mEtPassword);
        } else {
            return true;
        }
        return false;
    }

    //注册
    private void register() {
        String vcode = mEtVcode.getText().toString().trim();
        String password = mEtPassword.getText().toString().trim();
        Intent intent = new Intent(this, ServiceAgreementActivity.class);
        intent.putExtra("username", mUsername);
        intent.putExtra("email", mEmail);
        intent.putExtra("vcode", vcode);
        intent.putExtra("password", password);
        startActivity(intent);
    }
}