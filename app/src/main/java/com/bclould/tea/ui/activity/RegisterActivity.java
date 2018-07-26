package com.bclould.tea.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bclould.tea.Presenter.RegisterPresenter;
import com.bclould.tea.R;
import com.bclould.tea.base.LoginBaseActivity;
import com.bclould.tea.base.MyApp;
import com.bclould.tea.utils.AnimatorTool;
import com.bclould.tea.utils.AppLanguageUtils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by GA on 2017/9/20.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class RegisterActivity extends LoginBaseActivity {

    @Bind(R.id.iv_back)
    ImageView mIvBack;
    @Bind(R.id.tv_register)
    TextView mTvRegister;
    @Bind(R.id.et_email)
    EditText mEtEmail;
    @Bind(R.id.et_username)
    EditText mEtUsername;
    @Bind(R.id.btn_next)
    Button mBtnNext;
    private RegisterPresenter mRegisterPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        MyApp.getInstance().addActivity(this);
        mRegisterPresenter = new RegisterPresenter(this);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(AppLanguageUtils.attachBaseContext(newBase, newBase.getString(R.string.language_pref_key)));
    }

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


    //验证邮箱和图形验证码
    private boolean checkEdit() {
        if (mEtEmail.getText().toString().trim().equals("")) {
            Toast.makeText(this, getResources().getString(R.string.toast_email), Toast.LENGTH_SHORT).show();
            AnimatorTool.getInstance().editTextAnimator(mEtEmail);
        } else if (mEtUsername.getText().toString().trim().equals("")) {
            Toast.makeText(this, getResources().getString(R.string.toast_username), Toast.LENGTH_SHORT).show();
            AnimatorTool.getInstance().editTextAnimator(mEtUsername);
        } else if (!mEtEmail.getText().toString().contains("@")) {
            Toast.makeText(this, getResources().getString(R.string.toast_email_format), Toast.LENGTH_SHORT).show();
            AnimatorTool.getInstance().editTextAnimator(mEtUsername);
        } else if (mEtUsername.getText().toString().length() < 6) {
            Toast.makeText(this, getResources().getString(R.string.toast_username_min), Toast.LENGTH_SHORT).show();
            AnimatorTool.getInstance().editTextAnimator(mEtUsername);
        } else {
            return true;
        }
        return false;
    }

    @OnClick({R.id.iv_back, R.id.btn_next})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.btn_next:
                if (checkEdit()) {
                    next();
                }
                break;
        }
    }

    //注册
    private void next() {
        String email = mEtEmail.getText().toString().trim();
        String username = mEtUsername.getText().toString().trim();
        final Intent intent = new Intent(this, RegisterActivity2.class);
        intent.putExtra("username", username);
        intent.putExtra("email", email);
        mRegisterPresenter.signUpValidator(email, username, new RegisterPresenter.CallBack2() {
            @Override
            public void send() {
                startActivity(intent);
            }
        });
    }
}
