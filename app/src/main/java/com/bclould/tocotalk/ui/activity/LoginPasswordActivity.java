package com.bclould.tocotalk.ui.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bclould.tocotalk.base.MyApp;
import com.bclould.tocotalk.Presenter.LoginPasswordPresenter;
import com.bclould.tocotalk.R;
import com.bclould.tocotalk.base.BaseActivity;
import com.bclould.tocotalk.utils.AnimatorTool;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.bclould.tocotalk.R.style.BottomDialog;

/**
 * Created by GA on 2017/10/11.
 */

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
    @Bind(R.id.et_pay_password2)
    EditText mEtPayPassword2;
    @Bind(R.id.eye2)
    ImageView mEye2;
    @Bind(R.id.rl_show_hidden2)
    RelativeLayout mRlShowHidden2;
    @Bind(R.id.ll_pay_pw2)
    LinearLayout mLlPayPw2;
    @Bind(R.id.btn_finish)
    Button mBtnFinish;
    private Dialog mBottomDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_password);
        ButterKnife.bind(this);
        MyApp.getInstance().addActivity(this);
    }

    //提交修改的密码
    private void submit() {
        EditText etGoogleCode = (EditText) mBottomDialog.findViewById(R.id.et_google_code);
        String googleCode = etGoogleCode.getText().toString().trim();
        String password = mEtPayPassword.getText().toString().trim();
        String password2 = mEtPayPassword2.getText().toString().trim();
        if (!googleCode.isEmpty()) {
            LoginPasswordPresenter loginPasswordPresenter = new LoginPasswordPresenter(this);
            loginPasswordPresenter.submit(googleCode, password, password2);
        } else {
            AnimatorTool.getInstance().editTextAnimator(etGoogleCode);
            Toast.makeText(this, getString(R.string.toast_google_code), Toast.LENGTH_SHORT).show();
        }
    }

    boolean isEye = false;
    boolean isEye2 = false;

    @OnClick({R.id.bark, R.id.rl_show_hidden, R.id.rl_show_hidden2, R.id.btn_finish})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;
            case R.id.rl_show_hidden:
                isEye = !isEye;
                eyeShowHidden(0, isEye);
                break;
            case R.id.rl_show_hidden2:
                isEye2 = !isEye2;
                eyeShowHidden(1, isEye2);
                break;
            case R.id.btn_finish:
                if (checkEdit())
                    showGoogleDialog();
                break;
        }
    }

    //显示谷歌验证弹窗
    private void showGoogleDialog() {
        mBottomDialog = new Dialog(this, R.style.BottomDialog2);
        View contentView = LayoutInflater.from(this).inflate(R.layout.dialog_google_code, null);
        //获得dialog的window窗口
        Window window = mBottomDialog.getWindow();
        window.getDecorView().setPadding(0, 0, 0, 0);
        //获得window窗口的属性
        WindowManager.LayoutParams lp = window.getAttributes();
        //设置窗口宽度为充满全屏
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        //将设置好的属性set回去
        window.setAttributes(lp);
        window.setGravity(Gravity.BOTTOM);
        window.setWindowAnimations(BottomDialog);
        mBottomDialog.setContentView(contentView);
        mBottomDialog.show();
        Button confirm = (Button) mBottomDialog.findViewById(R.id.btn_confirm);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submit();
            }
        });
    }

    //输入框的文本显示和隐藏
    private void eyeShowHidden(int index, boolean isEye) {

        if (index == 0) {
            if (isEye) {
                mEye.setSelected(true);
                mEtPayPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                mEtPayPassword.setSelection(mEtPayPassword.length());
            } else {
                mEye.setSelected(false);
                mEtPayPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                mEtPayPassword.setSelection(mEtPayPassword.length());
            }
        } else {
            if (isEye) {
                mEye2.setSelected(true);
                mEtPayPassword2.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                mEtPayPassword2.setSelection(mEtPayPassword2.length());
            } else {
                mEye2.setSelected(false);
                mEtPayPassword2.setTransformationMethod(PasswordTransformationMethod.getInstance());
                mEtPayPassword2.setSelection(mEtPayPassword2.length());
            }
        }
    }

    //验证手机号和密码
    private boolean checkEdit() {
        if (mEtPayPassword.getText().toString().trim().equals("")) {
            Toast.makeText(this, getString(R.string.toast_password), Toast.LENGTH_SHORT).show();
            AnimatorTool.getInstance().editTextAnimator(mEtPayPassword);
        } else if (mEtPayPassword2.getText().toString().trim().equals("")) {
            Toast.makeText(this, getString(R.string.toast_password), Toast.LENGTH_SHORT).show();
            AnimatorTool.getInstance().editTextAnimator(mEtPayPassword2);
        } else {
            return true;
        }
        return false;
    }

}
