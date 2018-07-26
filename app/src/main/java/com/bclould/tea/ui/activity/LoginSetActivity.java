package com.bclould.tea.ui.activity;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bclould.tea.Presenter.LoginPresenter;
import com.bclould.tea.R;
import com.bclould.tea.base.BaseActivity;
import com.bclould.tea.base.MyApp;
import com.bclould.tea.utils.AnimatorTool;
import com.bclould.tea.utils.AppLanguageUtils;
import com.bclould.tea.utils.MySharedPreferences;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.bclould.tea.Presenter.LoginPresenter.LOGINSET;

/**
 * Created by GA on 2018/3/12.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class LoginSetActivity extends BaseActivity {
    @Bind(R.id.bark)
    ImageView mBark;
    @Bind(R.id.cb_email)
    CheckBox mCbEmail;
    @Bind(R.id.rl_email_verify)
    RelativeLayout mRlEmailVerify;
    @Bind(R.id.cb_google)
    CheckBox mCbGoogle;
    @Bind(R.id.rl_google_verify)
    RelativeLayout mRlGoogleVerify;
    @Bind(R.id.cb_no_verify)
    CheckBox mCbNoVerify;
    @Bind(R.id.rl_no_verify)
    RelativeLayout mRlNoVerify;
    @Bind(R.id.et_pay_password)
    EditText mEtPayPassword;
    @Bind(R.id.eye)
    ImageView mEye;
    @Bind(R.id.rl_eye)
    RelativeLayout mRlEye;
    @Bind(R.id.et_vcode)
    EditText mEtVcode;
    @Bind(R.id.btn_finish)
    Button mBtnFinish;
    @Bind(R.id.ll_check_box)
    LinearLayout mLlCheckBox;
    private boolean isEye;
    private LoginPresenter mLoginPresenter;
    private int mIndex;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_set);
        ButterKnife.bind(this);
        MyApp.getInstance().addActivity(this);
        mLoginPresenter = new LoginPresenter(this);
        init();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(AppLanguageUtils.attachBaseContext(newBase, newBase.getString(R.string.language_pref_key)));
    }

    private void init() {
        String index = MySharedPreferences.getInstance().getString(LOGINSET);
        if (!index.isEmpty()) {
            switch (Integer.parseInt(index)) {
                case 0:
                    mCbNoVerify.setChecked(true);
                    break;
                case 1:
                    mCbEmail.setChecked(true);
                    break;
                case 2:
                    mCbGoogle.setChecked(true);
                    break;
            }
        } else {
            mCbNoVerify.setChecked(true);
        }
        initCheckBox();
    }

    private void initCheckBox() {
        for (int i = 0; i < mLlCheckBox.getChildCount(); i++) {
            View childAt = mLlCheckBox.getChildAt(i);
            childAt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int index = mLlCheckBox.indexOfChild(view);
                    selectorCheck(index);
                    mIndex = index;
                }
            });
        }
    }

    private void selectorCheck(int index) {
        switch (index) {
            case 0:
                mCbNoVerify.setChecked(true);
                mCbEmail.setChecked(false);
                mCbGoogle.setChecked(false);
                break;
            case 1:
                mCbNoVerify.setChecked(false);
                mCbEmail.setChecked(true);
                mCbGoogle.setChecked(false);
                break;
            case 2:
                mCbNoVerify.setChecked(false);
                mCbEmail.setChecked(false);
                mCbGoogle.setChecked(true);
                break;
        }
    }

    @OnClick({R.id.bark, R.id.rl_eye, R.id.btn_finish})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;
            case R.id.rl_eye:
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

    private void submit() {
        String pw = mEtPayPassword.getText().toString();
        String googleCode = mEtVcode.getText().toString();
        mLoginPresenter.loginValidateTypeSetting(mIndex, pw, googleCode);
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
            Toast.makeText(this, getString(R.string.toast_google_code), Toast.LENGTH_SHORT).show();
            AnimatorTool.getInstance().editTextAnimator(mEtVcode);
        } else {
            return true;
        }
        return false;
    }
}
