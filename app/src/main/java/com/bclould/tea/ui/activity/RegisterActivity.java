package com.bclould.tea.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Base64;
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
import com.bclould.tea.model.QrCardInfo;
import com.bclould.tea.utils.AnimatorTool;
import com.bclould.tea.utils.Constants;
import com.bclould.tea.utils.UtilTool;
import com.bclould.tea.utils.permissions.AuthorizationUserTools;
import com.google.gson.Gson;

import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by GA on 2017/9/20.
 */

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
    @Bind(R.id.et_referrer)
    EditText mEtReferrer;
    private RegisterPresenter mRegisterPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        mRegisterPresenter = new RegisterPresenter(this);
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
            Toast.makeText(this, getResources().getString(R.string.toast_email_or_phone), Toast.LENGTH_SHORT).show();
            AnimatorTool.getInstance().editTextAnimator(mEtEmail);
        } else if (mEtUsername.getText().toString().trim().equals("")) {
            Toast.makeText(this, getResources().getString(R.string.toast_username), Toast.LENGTH_SHORT).show();
            AnimatorTool.getInstance().editTextAnimator(mEtUsername);
        } else if (mEtUsername.getText().toString().length() < 6) {
            Toast.makeText(this, getResources().getString(R.string.toast_username_min), Toast.LENGTH_SHORT).show();
            AnimatorTool.getInstance().editTextAnimator(mEtUsername);
        }else {
            return true;
        }
        return false;
    }

    @OnClick({R.id.iv_back, R.id.btn_next,R.id.iv_qr_code})
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
            case R.id.iv_qr_code:
                // TODO: 2018/11/8 扫一扫
                if (!AuthorizationUserTools.isCameraCanUse(this))
                    return;
                Intent intent = new Intent(this, ScanQRCodeActivity.class);
                intent.putExtra("code", 4);
                startActivityForResult(intent, 4);
                break;
        }
    }

    //注册
    private void next() {
        String email = mEtEmail.getText().toString().trim();
        String username = mEtUsername.getText().toString().trim();
        String referrer=mEtReferrer.getText().toString().trim();
        final Intent intent = new Intent(this, RegisterActivity2.class);
        intent.putExtra("username", username);
        intent.putExtra("email", email);
        intent.putExtra("referrer",referrer);
        Locale locale = getResources().getConfiguration().locale;
        String language = (locale.getLanguage() + "-" + locale.getCountry()).toLowerCase();
        mRegisterPresenter.signUpValidator(email, username,referrer,language, new RegisterPresenter.CallBack2() {
            @Override
            public void send() {
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 4) {
                String result = data.getStringExtra("result");
                if (result != null && !result.isEmpty()) {
                    if (result.contains(Constants.COMMANDUSERNAME)) {
                        String base64 = result.substring(Constants.COMMANDUSERNAME.length(), result.length());
                        String jsonresult = new String(Base64.decode(base64, Base64.DEFAULT));
                        UtilTool.Log("日志", jsonresult);

                        Gson gson = new Gson();
                        QrCardInfo qrCardInfo = gson.fromJson(jsonresult, QrCardInfo.class);
                        String name = qrCardInfo.getName();
                        mEtReferrer.setText(name+"");
                    }
                }
            }
        }
    }
}
