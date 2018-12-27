package com.bclould.tea.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bclould.tea.Presenter.UpdateLogPresenter;
import com.bclould.tea.R;
import com.bclould.tea.base.BaseActivity;
import com.bclould.tea.base.MyApp;
import com.bclould.tea.ui.widget.DeleteCacheDialog;
import com.bclould.tea.ui.widget.PWDDialog;
import com.bclould.tea.utils.AppLanguageUtils;
import com.bclould.tea.utils.MessageEvent;
import com.bclould.tea.utils.MySharedPreferences;
import com.bclould.tea.utils.ToastShow;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by GA on 2018/5/17.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class PayPwSelectorActivity extends BaseActivity {

    public static final String GESTURE_PW_SELE = "gesture_pw_sele";
    public static final String FINGERPRINT_PW_SELE = "fingerprint_pw_sele";
    @Bind(R.id.tv_gesture)
    TextView mTvGesture;
    @Bind(R.id.on_off_gesture)
    ImageView mOnOffGesture;
    @Bind(R.id.rl_gesture_pw)
    RelativeLayout mRlGesturePw;
    @Bind(R.id.tv_fingerprint)
    TextView mTvFingerprint;
    @Bind(R.id.on_off_fingerprint)
    ImageView mOnOffFingerprint;
    @Bind(R.id.rl_fingerprint_pw)
    RelativeLayout mRlFingerprintPw;
    private PWDDialog pwdDialog;
    private UpdateLogPresenter mUpdateLogPresenter;
    private int mCode;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_pw_selector);
        ButterKnife.bind(this);
        setTitle(getString(R.string.set_app_look));
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        init();
        mUpdateLogPresenter = new UpdateLogPresenter(this);
        initSp();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(AppLanguageUtils.attachBaseContext(newBase, MySharedPreferences.getInstance().getString(newBase.getString(R.string.language_pref_key))));
    }

    private void init() {
        mCode = getIntent().getIntExtra("code", 0);
        FingerprintManagerCompat managerCompat = FingerprintManagerCompat.from(MyApp.getInstance().app());
        if (!managerCompat.isHardwareDetected()) { //判断设备是否支持
            mRlFingerprintPw.setVisibility(View.GONE);
        } else {
            mRlFingerprintPw.setVisibility(View.VISIBLE);
        }
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
                        Intent intent = new Intent(this, DeblockingFingerprintActivity.class);
                        intent.putExtra("type", true);
                        startActivityForResult(intent, 0);
                    }
                } else {
                    ToastShow.showToast2(this, getString(R.string.already_set_fingerprint));
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == RESULT_OK) {
            showPWDialog(1);
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

    private void showPWDialog(final int type) {
        pwdDialog = new PWDDialog(this);
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
        pwdDialog.showDialog(getString(R.string.verify_pay_pw), null, null, null, null);
    }


    private void closeGesture(String password) {
        mUpdateLogPresenter.setGesture(password, 0, "", new UpdateLogPresenter.CallBack2() {
            @Override
            public void send(int status) {
                if (status == 0) {
                    if (mCode != 0) {
                        setResult(RESULT_OK);
                        finish();
                    }
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
