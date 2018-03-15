package com.bclould.tocotalk.ui.activity;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bclould.tocotalk.Presenter.LoginPasswordPresenter;
import com.bclould.tocotalk.R;
import com.bclould.tocotalk.base.BaseActivity;
import com.bclould.tocotalk.base.MyApp;
import com.bclould.tocotalk.utils.AnimatorTool;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by GA on 2017/10/11.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class LoginPasswordActivity extends BaseActivity {

    @Bind(R.id.bark)
    ImageView mBark;
    @Bind(R.id.et_pay_password)
    EditText mEtPayPassword;
    @Bind(R.id.eye)
    ImageView mEye;
    @Bind(R.id.rl_show_hidden)
    RelativeLayout mRlShowHidden;
    @Bind(R.id.ll_pay_pw)
    LinearLayout mLlPayPw;
    @Bind(R.id.et_vcode)
    EditText mEtVcode;
    @Bind(R.id.ll_vcode)
    LinearLayout mLlVcode;
    @Bind(R.id.btn_finish)
    Button mBtnFinish;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_password);
        ButterKnife.bind(this);
        MyApp.getInstance().addActivity(this);
    }

    //提交修改的密码
    private void submit() {
        String vocde = mEtVcode.getText().toString().trim();
        String password = mEtPayPassword.getText().toString().trim();
        LoginPasswordPresenter loginPasswordPresenter = new LoginPasswordPresenter(this);
        loginPasswordPresenter.submit(vocde, password, password);

    }

    boolean isEye = false;

    @OnClick({R.id.bark, R.id.rl_show_hidden, R.id.btn_finish})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;
            case R.id.rl_show_hidden:
                isEye = !isEye;
                eyeShowHidden(isEye);
                break;
            case R.id.btn_finish:
                if (checkEdit()) {
                    submit();
                }
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
        if (mEtPayPassword.getText().toString().trim().equals("")) {
            Toast.makeText(this, getString(R.string.toast_password), Toast.LENGTH_SHORT).show();
            AnimatorTool.getInstance().editTextAnimator(mEtPayPassword);
        } else if (mEtVcode.getText().toString().trim().equals("")) {
            Toast.makeText(this, getString(R.string.toast_password), Toast.LENGTH_SHORT).show();
            AnimatorTool.getInstance().editTextAnimator(mEtVcode);
        } else {
            return true;
        }
        return false;
    }
}
