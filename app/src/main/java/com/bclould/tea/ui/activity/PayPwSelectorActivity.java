package com.bclould.tea.ui.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.text.Editable;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bclould.tea.Presenter.BankCardPresenter;
import com.bclould.tea.Presenter.CurrencyInOutPresenter;
import com.bclould.tea.R;
import com.bclould.tea.base.BaseActivity;
import com.bclould.tea.base.MyApp;
import com.bclould.tea.ui.widget.DeleteCacheDialog;
import com.bclould.tea.ui.widget.VirtualKeyboardView;
import com.bclould.tea.utils.FingerprintUtil;
import com.bclould.tea.utils.MySharedPreferences;
import com.maning.pswedittextlibrary.MNPasswordEditText;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.bclould.tea.R.style.BottomDialog;

/**
 * Created by GA on 2018/5/17.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class PayPwSelectorActivity extends BaseActivity {
    @Bind(R.id.bark)
    ImageView mBark;
    @Bind(R.id.iv_login_password)
    ImageView mIvLoginPassword;
    @Bind(R.id.rl_number_pw)
    RelativeLayout mRlNumberPw;
    @Bind(R.id.iv_gesture)
    ImageView mIvGesture;
    @Bind(R.id.tv_gesture)
    TextView mTvGesture;
    @Bind(R.id.on_off_gesture)
    ImageView mOnOffGesture;
    @Bind(R.id.rl_gesture_pw)
    RelativeLayout mRlGesturePw;
    @Bind(R.id.iv_fingerprint)
    ImageView mIvFingerprint;
    @Bind(R.id.tv_fingerprint)
    TextView mTvFingerprint;
    @Bind(R.id.on_off_fingerprint)
    ImageView mOnOffFingerprint;
    @Bind(R.id.rl_fingerprint_pw)
    RelativeLayout mRlFingerprintPw;
    private String GESTURE_PW = "gesture_pw";
    private String FINGERPRINT_PW = "fingerprint_pw";
    private ArrayList<Map<String, String>> valueList;
    private Animation mEnterAnim;
    private Animation mExitAnim;
    private Dialog mRedDialog;
    private GridView mGridView;
    private MNPasswordEditText mEtPassword;
    private BankCardPresenter mBankCardPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_pw_selector);
        ButterKnife.bind(this);
        MyApp.getInstance().addActivity(this);
        initSp();
    }

    private void initSp() {
        isFingerprint = MySharedPreferences.getInstance().getBoolean(FINGERPRINT_PW);
        mOnOffFingerprint.setSelected(isFingerprint);
        isGesture = MySharedPreferences.getInstance().getBoolean(GESTURE_PW);
        mOnOffGesture.setSelected(isGesture);
    }

    boolean isGesture = false;
    boolean isFingerprint = false;

    @OnClick({R.id.bark, R.id.rl_number_pw, R.id.rl_gesture_pw, R.id.rl_fingerprint_pw})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;
            case R.id.rl_number_pw:
                startActivity(new Intent(this, PayPasswordActivity.class));
                break;
            case R.id.rl_gesture_pw:
                Toast.makeText(this, "開發當中，敬請期待", Toast.LENGTH_SHORT).show();
//                setGesturePw();
                break;
            case R.id.rl_fingerprint_pw:
                Toast.makeText(this, "開發當中，敬請期待", Toast.LENGTH_SHORT).show();
                /*if (isFingerprint) {
                    showCancelFingerprintDialog();
                } else {
                    setFingerprintPw();
                }*/
                break;
        }
    }

    private void showCancelFingerprintDialog() {
        final DeleteCacheDialog deleteCacheDialog = new DeleteCacheDialog(R.layout.dialog_delete_cache, this, R.style.dialog);
        deleteCacheDialog.show();
        deleteCacheDialog.setTitle(getString(R.string.close_fingerprint_hint));
        Button cancel = (Button) deleteCacheDialog.findViewById(R.id.btn_cancel);
        Button confirm = (Button) deleteCacheDialog.findViewById(R.id.btn_confirm);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteCacheDialog.dismiss();
            }
        });
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isFingerprint = !isFingerprint;
                mOnOffFingerprint.setSelected(isFingerprint);
                MySharedPreferences.getInstance().setBoolean(FINGERPRINT_PW, isFingerprint);
                deleteCacheDialog.dismiss();
            }
        });
    }

    public void showHintDialog() {
        final DeleteCacheDialog deleteCacheDialog = new DeleteCacheDialog(R.layout.dialog_pw_hint, this, R.style.dialog);
        deleteCacheDialog.show();
        deleteCacheDialog.setCanceledOnTouchOutside(false);
        TextView retry = (TextView) deleteCacheDialog.findViewById(R.id.tv_retry);
        TextView findPassword = (TextView) deleteCacheDialog.findViewById(R.id.tv_find_password);
        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteCacheDialog.dismiss();
                mRedDialog.show();
            }
        });
        findPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteCacheDialog.dismiss();
                startActivity(new Intent(PayPwSelectorActivity.this, PayPasswordActivity.class));
            }
        });
    }

    private void setFingerprintPw() {

        FingerprintUtil.callFingerPrint(new FingerprintUtil.OnCallBackListenr() {

            private TextView mCheck;
            private DeleteCacheDialog mDeleteCacheDialog;

            @Override
            public void onSupportFailed() {
                Toast.makeText(PayPwSelectorActivity.this, getString(R.string.nonsupport_fingerprint), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onInsecurity() {
                Toast.makeText(PayPwSelectorActivity.this, getString(R.string.insecurity), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onEnrollFailed() {
                Toast.makeText(PayPwSelectorActivity.this, getString(R.string.no_set_fingerprint), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationStart() {
                mDeleteCacheDialog = new DeleteCacheDialog(R.layout.dialog_fingerprint_pw, PayPwSelectorActivity.this, R.style.dialog);
                mDeleteCacheDialog.show();
                mDeleteCacheDialog.setCancelable(false);
                TextView cancel = (TextView) mDeleteCacheDialog.findViewById(R.id.tv_cancel);
                mCheck = (TextView) mDeleteCacheDialog.findViewById(R.id.tv_check);
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        FingerprintUtil.cancel();
                        mDeleteCacheDialog.dismiss();
                    }
                });
            }

            @Override
            public void onAuthenticationError(int errMsgId, CharSequence errString) {
                if (mDeleteCacheDialog != null && mDeleteCacheDialog.isShowing()) {
                    mCheck.setText(getString(R.string.check_fingerprint_error));
                    mCheck.setTextColor(getResources().getColor(R.color.red));
                }
            }

            @Override
            public void onAuthenticationFailed() {
                if (mDeleteCacheDialog != null && mDeleteCacheDialog.isShowing()) {
                    mCheck.setText(getString(R.string.check_fingerprint_error));
                    mCheck.setTextColor(getResources().getColor(R.color.red));
                }
            }

            @Override
            public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
                Toast.makeText(PayPwSelectorActivity.this, getString(R.string.finger_move_fast), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationSucceeded(FingerprintManagerCompat.AuthenticationResult result) {
                if (mDeleteCacheDialog != null && mDeleteCacheDialog.isShowing()) {
                    mCheck.setText(getString(R.string.check_fingerprint_succeed));
                    mCheck.setTextColor(getResources().getColor(R.color.blue2));
                    new Handler() {
                        public void handleMessage(Message msg) {
                            mDeleteCacheDialog.dismiss();
                            showPWDialog();
                        }
                    }.sendEmptyMessageDelayed(0, 1500);
                }
            }
        });
    }

    private void showPWDialog() {
        mEnterAnim = AnimationUtils.loadAnimation(this, R.anim.dialog_enter);
        mExitAnim = AnimationUtils.loadAnimation(this, R.anim.dialog_exit);
        mRedDialog = new Dialog(this, R.style.BottomDialog2);
        View contentView = LayoutInflater.from(this).inflate(R.layout.dialog_passwrod, null);
        //获得dialog的window窗口
        Window window = mRedDialog.getWindow();
        window.getDecorView().setPadding(0, 0, 0, 0);
        //获得window窗口的属性
        WindowManager.LayoutParams lp = window.getAttributes();
        //设置窗口宽度为充满全屏
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        //将设置好的属性set回去
        window.setAttributes(lp);
        window.setGravity(Gravity.CENTER);
        window.setWindowAnimations(BottomDialog);
        mRedDialog.setContentView(contentView);
        mRedDialog.show();
        mRedDialog.setCancelable(false);
        mRedDialog.setCanceledOnTouchOutside(false);
        initDialog();
    }

    private void initDialog() {
        TextView coin = (TextView) mRedDialog.findViewById(R.id.tv_coin);
        coin.setVisibility(View.GONE);
        TextView countCoin = (TextView) mRedDialog.findViewById(R.id.tv_count_coin);
        mEtPassword = (MNPasswordEditText) mRedDialog.findViewById(R.id.et_password);
        // 设置不调用系统键盘
        if (Build.VERSION.SDK_INT <= 10) {
            mEtPassword.setInputType(InputType.TYPE_NULL);
        } else {
            this.getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            try {
                Class<EditText> cls = EditText.class;
                Method setShowSoftInputOnFocus;
                setShowSoftInputOnFocus = cls.getMethod("setShowSoftInputOnFocus",
                        boolean.class);
                setShowSoftInputOnFocus.setAccessible(true);
                setShowSoftInputOnFocus.invoke(mEtPassword, false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        final VirtualKeyboardView virtualKeyboardView = (VirtualKeyboardView) mRedDialog.findViewById(R.id.virtualKeyboardView);
        ImageView bark = (ImageView) mRedDialog.findViewById(R.id.bark);
        bark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRedDialog.dismiss();
                mEtPassword.setText("");
            }
        });
        valueList = virtualKeyboardView.getValueList();
        countCoin.setText(getString(R.string.verify_pay_pw));
        virtualKeyboardView.getLayoutBack().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                virtualKeyboardView.startAnimation(mExitAnim);
                virtualKeyboardView.setVisibility(View.GONE);
            }
        });
        mGridView = virtualKeyboardView.getGridView();
        mGridView.setOnItemClickListener(onItemClickListener);
        mEtPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                virtualKeyboardView.setFocusable(true);
                virtualKeyboardView.setFocusableInTouchMode(true);
                virtualKeyboardView.startAnimation(mEnterAnim);
                virtualKeyboardView.setVisibility(View.VISIBLE);
            }
        });

        mEtPassword.setOnPasswordChangeListener(new MNPasswordEditText.OnPasswordChangeListener() {
            @Override
            public void onPasswordChange(String password) {
                if (password.length() == 6) {
                    mRedDialog.dismiss();
                    mEtPassword.setText("");
                    check(password);
                }
            }
        });
    }

    private void check(String password) {
        CurrencyInOutPresenter currencyInOutPresenter = new CurrencyInOutPresenter(this);
        currencyInOutPresenter.check(password);
    }

    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

            if (position < 11 && position != 9) {    //点击0~9按钮

                String amount = mEtPassword.getText().toString().trim();
                amount = amount + valueList.get(position).get("name");

                mEtPassword.setText(amount);

                Editable ea = mEtPassword.getText();
                mEtPassword.setSelection(ea.length());
            } else {

                if (position == 9) {      //点击退格键
                    String amount = mEtPassword.getText().toString().trim();
                    if (!amount.contains(".")) {
                        amount = amount + valueList.get(position).get("name");
                        mEtPassword.setText(amount);

                        Editable ea = mEtPassword.getText();
                        mEtPassword.setSelection(ea.length());
                    }
                }

                if (position == 11) {      //点击退格键
                    String amount = mEtPassword.getText().toString().trim();
                    if (amount.length() > 0) {
                        amount = amount.substring(0, amount.length() - 1);
                        mEtPassword.setText(amount);
                        Editable ea = mEtPassword.getText();
                        mEtPassword.setSelection(ea.length());
                    }
                }
            }
        }
    };

    private void setGesturePw() {
        startActivity(new Intent(this, SetGesturePWActivity.class));
        /*isGesture = !isGesture;
        MySharedPreferences.getInstance().setBoolean(GESTURE_PW, isGesture);
        mOnOffGesture.setSelected(isGesture);*/
    }

    public void setData() {
        Toast.makeText(this, getString(R.string.open_fingerprint_succeed), Toast.LENGTH_SHORT).show();
        isFingerprint = !isFingerprint;
        mOnOffFingerprint.setSelected(isFingerprint);
        MySharedPreferences.getInstance().setBoolean(FINGERPRINT_PW, isFingerprint);
    }
}
