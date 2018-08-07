package com.bclould.tea.ui.activity;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bclould.tea.R;
import com.bclould.tea.base.BaseActivity;
import com.bclould.tea.topperchat.WsConnection;
import com.bclould.tea.utils.AnimatorTool;
import com.bclould.tea.utils.FingerprintUtil;
import com.bclould.tea.utils.UtilTool;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by GA on 2018/8/2.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class DeblockingFingerprintActivity extends BaseActivity {
    @Bind(R.id.iv_logo)
    ImageView mIvLogo;
    @Bind(R.id.tv_app_name)
    TextView mTvAppName;
    @Bind(R.id.iv_fingerprint)
    ImageView mIvFingerprint;
    @Bind(R.id.tv_check)
    TextView mTvCheck;
    @Bind(R.id.xx)
    TextView mXx;
    @Bind(R.id.tv_cancel)
    TextView mTvCancel;
    private boolean mType;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSwipeEnabled(false);
        setContentView(R.layout.dialog_fingerprint_pw);
        ButterKnife.bind(this);
        initIntent();
        checkFingerprint();
    }

    private void initIntent() {
        mType = getIntent().getBooleanExtra("type", false);
        if (!mType) {
            mTvCancel.setVisibility(View.GONE);
            mXx.setVisibility(View.GONE);
        }
    }

    private void checkFingerprint() {
        FingerprintUtil.callFingerPrint(mOnCallBackListenr);
    }

    FingerprintUtil.OnCallBackListenr mOnCallBackListenr = new FingerprintUtil.OnCallBackListenr() {

        private int mCount = 5;

        @Override
        public void onSupportFailed() {
            Toast.makeText(DeblockingFingerprintActivity.this, getString(R.string.nonsupport_fingerprint), Toast.LENGTH_SHORT).show();
            FingerprintUtil.cancel();
        }

        @Override
        public void onInsecurity() {
            Toast.makeText(DeblockingFingerprintActivity.this, getString(R.string.insecurity), Toast.LENGTH_SHORT).show();
            FingerprintUtil.cancel();
        }

        @Override
        public void onEnrollFailed() {
            Toast.makeText(DeblockingFingerprintActivity.this, getString(R.string.no_set_fingerprint), Toast.LENGTH_SHORT).show();
            FingerprintUtil.cancel();
        }

        @Override
        public void onAuthenticationStart() {
        }

        @Override
        public void onAuthenticationError(int errMsgId, CharSequence errString) {
            UtilTool.Log("指紋", errMsgId + ":" + errString);
            if (errMsgId != 5) {
                if (mType) {
                    mTvCheck.setText(getString(R.string.check_fingerprint_error));
                    mTvCheck.setTextColor(getResources().getColor(R.color.red));
                    AnimatorTool.getInstance().editTextAnimator(mTvCheck);
                } else {
                    if (mCount > 0) {
                        mCount--;
                        mTvCheck.setText(getString(R.string.check_fingerprint_error) + getString(R.string.hai_sheng) + mCount + getString(R.string.chance));
                        mTvCheck.setTextColor(getResources().getColor(R.color.red));
                        AnimatorTool.getInstance().editTextAnimator(mTvCheck);
                    } else {
                        finish();
                        WsConnection.getInstance().goMainActivity();
                        mCount = 5;
                    }
                }
            }

        }

        @Override
        public void onAuthenticationFailed() {
            if (mType) {
                mTvCheck.setText(getString(R.string.check_fingerprint_error));
                mTvCheck.setTextColor(getResources().getColor(R.color.red));
                AnimatorTool.getInstance().editTextAnimator(mTvCheck);
            } else {
                if (mCount > 0) {
                    mCount--;
                    mTvCheck.setText(getString(R.string.check_fingerprint_error) + getString(R.string.hai_sheng) + mCount + getString(R.string.chance));
                    mTvCheck.setTextColor(getResources().getColor(R.color.red));
                    AnimatorTool.getInstance().editTextAnimator(mTvCheck);
                } else {
                    finish();
                    WsConnection.getInstance().goMainActivity();
                    mCount = 5;
                }
            }
        }


        @Override
        public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
            Toast.makeText(DeblockingFingerprintActivity.this, getString(R.string.finger_move_fast), Toast.LENGTH_SHORT).show();
        }

        @SuppressLint("HandlerLeak")
        @Override
        public void onAuthenticationSucceeded(FingerprintManagerCompat.AuthenticationResult result) {
            mCount = 5;
            mTvCheck.setText(getString(R.string.check_fingerprint_succeed));
            mTvCheck.setTextColor(getResources().getColor(R.color.blue2));
            new Handler() {
                public void handleMessage(Message msg) {
                    if (mType) {
                        setResult(RESULT_OK);
                        finish();
                    } else {
                        finish();
                    }
                }
            }.sendEmptyMessageDelayed(0, 1500);
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FingerprintUtil.cancel();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mType) {
                return super.onKeyDown(keyCode, event);
            } else {
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @OnClick(R.id.tv_cancel)
    public void onViewClicked() {
        finish();
    }
}
