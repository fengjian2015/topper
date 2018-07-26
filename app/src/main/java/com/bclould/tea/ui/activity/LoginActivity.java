package com.bclould.tea.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bclould.tea.Presenter.LoginPresenter;
import com.bclould.tea.R;
import com.bclould.tea.base.LoginBaseActivity;
import com.bclould.tea.base.MyApp;
import com.bclould.tea.history.DBUserCode;
import com.bclould.tea.model.UserCodeInfo;
import com.bclould.tea.topperchat.WsConnection;
import com.bclould.tea.ui.adapter.EmailCodeAdapter;
import com.bclould.tea.ui.widget.MyAutoCompleteTextView;
import com.bclould.tea.utils.AnimatorTool;
import com.bclould.tea.utils.AppLanguageUtils;
import com.bclould.tea.utils.MySharedPreferences;
import com.bclould.tea.utils.UtilTool;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.bclould.tea.Presenter.LoginPresenter.EMAIL;
import static com.bclould.tea.Presenter.LoginPresenter.LOGINPW;
import static com.bclould.tea.ui.activity.SystemSetActivity.PRIVATE;

/**
 * Created by GA on 2017/9/20.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class LoginActivity extends LoginBaseActivity {


    @Bind(R.id.iv_back)
    ImageView mIvBack;
    @Bind(R.id.tv_login)
    TextView mTvLogin;
    @Bind(R.id.et_emily)
    MyAutoCompleteTextView mEtEmily;
    @Bind(R.id.et_password)
    EditText mEtPassword;
    @Bind(R.id.eye)
    ImageView mEye;
    @Bind(R.id.tv_find_password)
    TextView mTvFindPassword;
    @Bind(R.id.btn_login)
    Button mBtnLogin;

    private DBUserCode mDBUserCode;
    private List<String> userCodeList = new ArrayList<>();
    private EmailCodeAdapter mAdapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);
        initUserCodeList();
        if (MySharedPreferences.getInstance().getBoolean(PRIVATE)) {
            String email = MySharedPreferences.getInstance().getString(EMAIL);
            String logPW = MySharedPreferences.getInstance().getString(LOGINPW);
            mEtEmily.setText(email);
            mEtPassword.setText(logPW);
        }

        MyApp.getInstance().addActivity(this);
        if(WsConnection.getInstance().ws!=null){
            UtilTool.Log("fengjian",WsConnection.getInstance().ws.isOpen()+"   ");
        }
        UtilTool.Log("fengjian",WsConnection.getInstance().ws+"   ");
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(AppLanguageUtils.attachBaseContext(newBase, newBase.getString(R.string.language_pref_key)));
    }

    private void initUserCodeList() {
        mDBUserCode=new DBUserCode(this);
        userCodeList=mDBUserCode.selectAllEmily();
        setloginUserNameEditHeight();

        mAdapter = new EmailCodeAdapter(userCodeList,this,mDBUserCode);
        mEtEmily.setAdapter(mAdapter);
        mEtEmily.setDropDownBackgroundResource(R.drawable.emily_listitem);// 下拉框的背景
        updateEditAdapter(userCodeList.size());
        mEtEmily.setDropDownVerticalOffset(1);
        mAdapter.notifyDataSetChanged();
    }


    private void setloginUserNameEditHeight() {
        if (userCodeList != null && userCodeList.size() > 1) {
            setloginUserNameEditText();
        } else if (userCodeList != null && userCodeList.size() == 1) {
            mEtEmily.setText(userCodeList.get(userCodeList.size()-1));
            mEtEmily.setSelection(mEtEmily.getText().length());
        }
    }

    private void setloginUserNameEditText() {
        UserCodeInfo userCodeInfo= mDBUserCode.queryLastUser();
        mEtEmily.setText(userCodeInfo.getEmail());// 这个是设置帐号，最后一个
        mEtEmily.setSelection(mEtEmily.getText().length());
    }

    public void updateEditAdapter(int number) {
        if(number<=0){
            mEtEmily.setDropDownHeight(0);
        }else if(number>0 && number<4){
            mEtEmily.setDropDownHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        }else if(userCodeList.size()>=4){
            mEtEmily.setDropDownHeight(getResources().getDimensionPixelOffset(R.dimen.y84)*4);
        }
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


    @OnClick({R.id.iv_back, R.id.eye, R.id.tv_find_password, R.id.btn_login})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.eye:
                isEye = !isEye;
                showHidePassword();//隐藏显示密码
                break;
            case R.id.tv_find_password:
                startActivity(new Intent(this, FindPasswordActivity.class));
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
            mEye.setSelected(true);
            mEtPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            mEtPassword.setSelection(mEtPassword.length());
        } else {
            mEtPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            mEtPassword.setSelection(mEtPassword.length());
            mEye.setSelected(false);
        }
    }

    //登录
    private void login() {
        String email = mEtEmily.getText().toString();
        String password = mEtPassword.getText().toString();
        LoginPresenter loginPresenter = new LoginPresenter(this);
        Locale locale = getResources().getConfiguration().locale;
        String language = (locale.getLanguage() + "-" + locale.getCountry()).toLowerCase();
        loginPresenter.Login(email, password, "",mDBUserCode, language);
    }

    //验证手机号和密码
    private boolean checkEdit() {
        if (mEtEmily.getText().toString().trim().equals("")) {
            Toast.makeText(this, getResources().getString(R.string.toast_email), Toast.LENGTH_SHORT).show();
            AnimatorTool.getInstance().editTextAnimator(mEtEmily);
        } else if (mEtPassword.getText().toString().trim().equals("")) {
            Toast.makeText(this, getResources().getString(R.string.toast_password), Toast.LENGTH_SHORT).show();
            AnimatorTool.getInstance().editTextAnimator(mEtPassword);
        } else if (!mEtEmily.getText().toString().contains("@")) {
            Toast.makeText(this, getResources().getString(R.string.toast_email_format), Toast.LENGTH_SHORT).show();
            AnimatorTool.getInstance().editTextAnimator(mEtEmily);
        } else {
            return true;
        }
        return false;
    }

}
