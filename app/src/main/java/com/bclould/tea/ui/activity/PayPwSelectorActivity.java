package com.bclould.tea.ui.activity;

import android.annotation.SuppressLint;
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
import com.bclould.tea.Presenter.UpdateLogPresenter;
import com.bclould.tea.R;
import com.bclould.tea.base.BaseActivity;
import com.bclould.tea.base.MyApp;
import com.bclould.tea.ui.widget.DeleteCacheDialog;
import com.bclould.tea.ui.widget.PWDDialog;
import com.bclould.tea.ui.widget.VirtualKeyboardView;
import com.bclould.tea.utils.FingerprintUtil;
import com.bclould.tea.utils.MessageEvent;
import com.bclould.tea.utils.MySharedPreferences;
import com.bclould.tea.utils.ToastShow;
import com.bclould.tea.utils.UtilTool;
import com.maning.pswedittextlibrary.MNPasswordEditText;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

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
    public static final String GESTURE_PW_SELE = "gesture_pw_sele";
    public static final String FINGERPRINT_PW_SELE = "fingerprint_pw_sele";
    private PWDDialog pwdDialog;
    private UpdateLogPresenter mUpdateLogPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_pw_selector);
        ButterKnife.bind(this);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        MyApp.getInstance().addActivity(this);
        mUpdateLogPresenter = new UpdateLogPresenter(this);
        initSp();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        String msg = event.getMsg();
        if (msg.equals(getString(R.string.set_gesture))) {
            initSp();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        EventBus.getDefault().unregister(this);
    }

    private void initSp() {
        isFingerprint = MySharedPreferences.getInstance().getBoolean(FINGERPRINT_PW_SELE);
        mOnOffFingerprint.setSelected(isFingerprint);
        isGesture = MySharedPreferences.getInstance().getBoolean(GESTURE_PW_SELE);
        mOnOffGesture.setSelected(isGesture);
    }

    boolean isGesture = false;
    boolean isFingerprint = false;

    @OnClick({R.id.bark, R.id.rl_gesture_pw, R.id.rl_fingerprint_pw})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;
            case R.id.rl_gesture_pw:
//                Toast.makeText(this, "開發當中，敬請期待", Toast.LENGTH_SHORT).show();
                if (!isFingerprint) {
                    if (isGesture) {
                        showCancelFingerprintDialog(2);
                    } else {
                        setGesturePw();
                    }
                } else {
                    ToastShow.showToast2(this, getString(R.string.already_set_fingerprint));
                }
                break;
            case R.id.rl_fingerprint_pw:
//                Toast.makeText(this, "開發當中，敬請期待", Toast.LENGTH_SHORT).show();
                if (!isGesture) {
                    if (isFingerprint) {
                        showCancelFingerprintDialog(1);
                    } else {
                        checkFingerprint();
                    }
                } else {
                    ToastShow.showToast2(this, getString(R.string.already_set_fingerprint));
                }
                break;
        }
    }

    private void showCancelFingerprintDialog(final int type) {
        final DeleteCacheDialog deleteCacheDialog = new DeleteCacheDialog(R.layout.dialog_delete_cache, this, R.style.dialog);
        deleteCacheDialog.show();
        if (type == 1) {
            deleteCacheDialog.setTitle(getString(R.string.close_fingerprint_hint));
        } else {
            deleteCacheDialog.setTitle(getString(R.string.close_gesture_hint));
        }
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
                deleteCacheDialog.dismiss();
                if (type == 1) {
                    showPWDialog(0);
                } else {
                    showPWDialog(3);
                }
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
                pwdDialog.show();
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

    private void checkFingerprint() {

        FingerprintUtil.callFingerPrint(new FingerprintUtil.OnCallBackListenr() {

            private TextView mCheck;
            private Dialog mFingerprintdialog;

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
                if (mFingerprintdialog == null) {
                    mFingerprintdialog = new Dialog(PayPwSelectorActivity.this, R.style.dialog2);
                    View contentView = LayoutInflater.from(PayPwSelectorActivity.this).inflate(R.layout.dialog_fingerprint_pw, null);
                    //获得dialog的window窗口
                    Window window = mFingerprintdialog.getWindow();
                    window.getDecorView().setPadding(0, 0, 0, 0);
                    //获得window窗口的属性
                    WindowManager.LayoutParams lp = window.getAttributes();
                    //设置窗口宽度为充满全屏
                    lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                    //将设置好的属性set回去
                    window.setAttributes(lp);
                    mFingerprintdialog.setContentView(contentView);
                    mFingerprintdialog.setCancelable(false);
                }
                if (!mFingerprintdialog.isShowing()) {
                    mFingerprintdialog.show();
                    TextView cancel = (TextView) mFingerprintdialog.findViewById(R.id.tv_cancel);
                    mCheck = (TextView) mFingerprintdialog.findViewById(R.id.tv_check);
                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            FingerprintUtil.cancel();
                            mFingerprintdialog.dismiss();
                        }
                    });
                }
            }

            @Override
            public void onAuthenticationError(int errMsgId, CharSequence errString) {
                if (mFingerprintdialog != null && mFingerprintdialog.isShowing()) {
                    mCheck.setText(getString(R.string.check_fingerprint_error));
                    mCheck.setTextColor(getResources().getColor(R.color.red));
                }
            }

            @Override
            public void onAuthenticationFailed() {
                if (mFingerprintdialog != null && mFingerprintdialog.isShowing()) {
                    mCheck.setText(getString(R.string.check_fingerprint_error));
                    mCheck.setTextColor(getResources().getColor(R.color.red));
                }
            }

            @Override
            public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
                Toast.makeText(PayPwSelectorActivity.this, getString(R.string.finger_move_fast), Toast.LENGTH_SHORT).show();
            }

            @SuppressLint("HandlerLeak")
            @Override
            public void onAuthenticationSucceeded(FingerprintManagerCompat.AuthenticationResult result) {
                if (mFingerprintdialog != null && mFingerprintdialog.isShowing()) {
                    mCheck.setText(getString(R.string.check_fingerprint_succeed));
                    mCheck.setTextColor(getResources().getColor(R.color.blue2));
                    new Handler() {
                        public void handleMessage(Message msg) {
                            mFingerprintdialog.dismiss();
                            showPWDialog(1);
                        }
                    }.sendEmptyMessageDelayed(0, 1500);
                }
            }
        });
    }

    private void showPWDialog(final int type) {
        pwdDialog=new PWDDialog(this);
        pwdDialog.setOnPWDresult(new PWDDialog.OnPWDresult() {
            @Override
            public void success(String password) {
                if (type == 3) {
                    closeGesture(password);
                } else {
                    setFingerprint(password, type);
                }
            }
        });
        pwdDialog.showDialog(getString(R.string.verify_pay_pw),null,null,null,null);
    }


    private void closeGesture(String password) {
        mUpdateLogPresenter.setGesture(password, 0, "",new UpdateLogPresenter.CallBack2() {
            @Override
            public void send(int status) {
                if (status == 0) {
                    Toast.makeText(PayPwSelectorActivity.this, getString(R.string.close_gesture), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void error() {

            }
        });
    }

    private void setFingerprint(String password, int status) {
        mUpdateLogPresenter.setFingerprint(password, status, new UpdateLogPresenter.CallBack2() {
            @Override
            public void send(int status) {
                if (status == 0) {
                    isFingerprint = false;
                    Toast.makeText(PayPwSelectorActivity.this, getString(R.string.close_fingerprint), Toast.LENGTH_SHORT).show();
                } else {
                    isFingerprint = true;
                    Toast.makeText(PayPwSelectorActivity.this, getString(R.string.open_fingerprint_succeed), Toast.LENGTH_SHORT).show();
                }
                mOnOffFingerprint.setSelected(isFingerprint);
                MySharedPreferences.getInstance().setBoolean(FINGERPRINT_PW_SELE, isFingerprint);
            }

            @Override
            public void error() {

            }
        });
    }

    private void setGesturePw() {
        startActivity(new Intent(this, SetGesturePWActivity.class));
    }
}
