package com.bclould.tea.base;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bclould.tea.R;
import com.bclould.tea.topperchat.WsConnection;
import com.bclould.tea.ui.activity.PayPwSelectorActivity;
import com.bclould.tea.ui.widget.DeleteCacheDialog;
import com.bclould.tea.utils.FingerprintUtil;
import com.bclould.tea.utils.MySharedPreferences;
import com.bclould.tea.utils.UtilTool;
import com.umeng.message.PushAgent;

import java.util.List;

import static com.bclould.tea.Presenter.LoginPresenter.FINGERPRINT_PW;

/**
 * Created by GA on 2017/9/22.
 */

@android.support.annotation.RequiresApi(api = Build.VERSION_CODES.N)
public class BaseActivity extends AppCompatActivity {

    private static boolean isActive;
    private DeleteCacheDialog mDeleteCacheDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PushAgent.getInstance(this).onAppStart();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(getResources().getColor(R.color.white));
        }
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

    @Override
    public void onResume() {
        super.onResume();
        if (!isActive) {
            //app 从后台唤醒，进入前台
            isActive = true;
            if (MySharedPreferences.getInstance().getInteger(FINGERPRINT_PW) == 1 && !UtilTool.getToken().equals("bearer")) {
                checkFingerprint();
            }
            UtilTool.Log("ACTIVITY", "程序从后台唤醒");
        }
//        UmManage.getInstance().mobclickAgent.onResume(this);
    }

    @Override
    public void onPause() {
        super.onPause();
//        UmManage.getInstance().mobclickAgent.onPause(this);
    }

    @Override
    protected void onStop() {
        if (!isAppOnForeground()) {
            //app 进入后台
            isActive = false;//记录当前已经进入后台
            UtilTool.Log("ACTIVITY", "程序进入后台");
            if (mDeleteCacheDialog != null) {
                if (mDeleteCacheDialog.isShowing()) {
                    mDeleteCacheDialog.dismiss();
                }
                mDeleteCacheDialog = null;
            }
        }
        super.onStop();
    }

    private void checkFingerprint() {
        FingerprintUtil.callFingerPrint(mOnCallBackListenr);
    }

    FingerprintUtil.OnCallBackListenr mOnCallBackListenr = new FingerprintUtil.OnCallBackListenr() {

        private TextView mCheck;

        @Override
        public void onSupportFailed() {
            Toast.makeText(BaseActivity.this, getString(R.string.nonsupport_fingerprint), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onInsecurity() {
            Toast.makeText(BaseActivity.this, getString(R.string.insecurity), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onEnrollFailed() {
            Toast.makeText(BaseActivity.this, getString(R.string.no_set_fingerprint), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onAuthenticationStart() {
            if (mDeleteCacheDialog == null) {
                mDeleteCacheDialog = new DeleteCacheDialog(R.layout.dialog_fingerprint_pw, BaseActivity.this, R.style.dialog);
            }
            if (!mDeleteCacheDialog.isShowing()) {
                mDeleteCacheDialog.show();
                mDeleteCacheDialog.setCancelable(false);
                TextView cancel = (TextView) mDeleteCacheDialog.findViewById(R.id.tv_cancel);
                mCheck = (TextView) mDeleteCacheDialog.findViewById(R.id.tv_check);
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        FingerprintUtil.cancel();
                        mDeleteCacheDialog.dismiss();
                        MyApp.getInstance().exit();
                    }
                });
            }
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
            Toast.makeText(BaseActivity.this, getString(R.string.finger_move_fast), Toast.LENGTH_SHORT).show();
        }

        @SuppressLint("HandlerLeak")
        @Override
        public void onAuthenticationSucceeded(FingerprintManagerCompat.AuthenticationResult result) {
            if (mDeleteCacheDialog != null && mDeleteCacheDialog.isShowing()) {
                mCheck.setText(getString(R.string.check_fingerprint_succeed));
                mCheck.setTextColor(getResources().getColor(R.color.blue2));
                new Handler() {
                    public void handleMessage(Message msg) {
                        mDeleteCacheDialog.dismiss();
                    }
                }.sendEmptyMessageDelayed(0, 1500);
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.finish();
        MyApp.getInstance().mActivityList.remove(this);
    }

    /**
     * APP是否处于前台唤醒状态
     *
     * @return
     */
    public boolean isAppOnForeground() {
        ActivityManager activityManager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        String packageName = getApplicationContext().getPackageName();
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager
                .getRunningAppProcesses();
        if (appProcesses == null)
            return false;

        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            // The name of the process that this object is associated with.
            if (appProcess.processName.equals(packageName)
                    && appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return true;
            }
        }

        return false;
    }
}