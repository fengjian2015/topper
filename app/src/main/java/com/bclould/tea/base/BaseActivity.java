package com.bclould.tea.base;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bclould.tea.R;
import com.bclould.tea.topperchat.WsConnection;
import com.bclould.tea.ui.activity.ConversationActivity;
import com.bclould.tea.ui.widget.DeleteCacheDialog;
import com.bclould.tea.ui.widget.GestureLockViewGroup;
import com.bclould.tea.utils.AnimatorTool;
import com.bclould.tea.utils.FingerprintUtil;
import com.bclould.tea.utils.MySharedPreferences;
import com.bclould.tea.utils.UtilTool;
import com.umeng.message.PushAgent;

import java.util.List;

import static com.bclould.tea.ui.activity.PayPwSelectorActivity.FINGERPRINT_PW_SELE;
import static com.bclould.tea.ui.activity.PayPwSelectorActivity.GESTURE_PW_SELE;
import static com.bclould.tea.ui.activity.SetGesturePWActivity.GESTURE_ANSWER;

/**
 * Created by GA on 2017/9/22.
 */

@android.support.annotation.RequiresApi(api = Build.VERSION_CODES.N)
public class BaseActivity extends SwipeActivity {

    private static boolean isActive;
    private Dialog mFingerprintdialog;
    private Dialog mGestureDialog;
    private TextView mTvHint;
    private GestureLockViewGroup mGestureView;

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
            if(this.getClass().getName().equals(ConversationActivity.class.getName()))
                return super.dispatchTouchEvent(ev);
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
            if (!UtilTool.getToken().equals("bearer")) {
                if (MySharedPreferences.getInstance().getBoolean(FINGERPRINT_PW_SELE)) {
                    checkFingerprint();
                }
                if (MySharedPreferences.getInstance().getBoolean(GESTURE_PW_SELE)) {
                    showGestureDialog();
                }
            }
            UtilTool.Log("ACTIVITY", "程序从后台唤醒");
        }
//        UmManage.getInstance().mobclickAgent.onResume(this);
    }

    private void showGestureDialog() {
        if (mGestureDialog == null) {
            mGestureDialog = new Dialog(BaseActivity.this, R.style.dialog2);
            View contentView = LayoutInflater.from(BaseActivity.this).inflate(R.layout.dialog_gesture_pw, null);
            //获得dialog的window窗口
            Window window = mGestureDialog.getWindow();
            window.getDecorView().setPadding(0, 0, 0, 0);
            //获得window窗口的属性
            WindowManager.LayoutParams lp = window.getAttributes();
            //设置窗口宽度为充满全屏
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            //将设置好的属性set回去
            window.setAttributes(lp);
            mGestureDialog.setContentView(contentView);
            mGestureDialog.setCancelable(false);
        }
        if (!mGestureDialog.isShowing()) {
            mGestureDialog.show();
            mGestureView = (GestureLockViewGroup) mGestureDialog.findViewById(R.id.gesture_view);
            mTvHint = (TextView) mGestureDialog.findViewById(R.id.tv_hint);
            mTvHint.setText(getString(R.string.import_gesture));
            String mAnswerstr = MySharedPreferences.getInstance().getString(GESTURE_ANSWER);
            int[] arr = new int[mAnswerstr.length()];
            for (int i = 0; i < mAnswerstr.length(); i++) {
                arr[i] = Character.getNumericValue(mAnswerstr.charAt(i));
            }
            mGestureView.setAnswer(arr);
            mGestureView.setOnGestureLockViewListener(new GestureLockViewGroup.OnGestureLockViewListener() {

                public int mCount = 5;

                @Override
                public void onBlockSelected(int position) {

                }

                @Override
                public void onGestureEvent(boolean matched) {
                    if (matched) {
                        mGestureDialog.dismiss();
                        mCount = 5;
                    } else {
                        mCount--;
                        mTvHint.setText(getString(R.string.set_gesture_hint5) + getString(R.string.hai_sheng) + mCount + getString(R.string.chance));
                        mTvHint.setTextColor(getResources().getColor(R.color.red));
                    }
                }

                @Override
                public void onUnmatchedExceedBoundary() {
                    UtilTool.Log("手勢", "onUnmatchedExceedBoundary");
                    if (mCount == 0) {
                        mGestureDialog.dismiss();
                        WsConnection.getInstance().logoutService(BaseActivity.this);
                        WsConnection.getInstance().goMainActivity();
                    }
                }
            });
        }
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
            FingerprintUtil.cancel();
            if (mFingerprintdialog != null) {
                if (mFingerprintdialog.isShowing()) {
                    mFingerprintdialog.dismiss();
                }
                mFingerprintdialog = null;
            }
            if (mGestureDialog != null) {
                if (mGestureDialog.isShowing()) {
                    mGestureDialog.dismiss();
                }
                mGestureDialog = null;
            }
        }
        super.onStop();
    }

    private void checkFingerprint() {
        FingerprintUtil.callFingerPrint(mOnCallBackListenr);
    }

    FingerprintUtil.OnCallBackListenr mOnCallBackListenr = new FingerprintUtil.OnCallBackListenr() {

        private TextView mCheck;
        private int mCount = 5;

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
            if (mFingerprintdialog == null) {
                mFingerprintdialog = new Dialog(BaseActivity.this, R.style.dialog2);
                View contentView = LayoutInflater.from(BaseActivity.this).inflate(R.layout.dialog_fingerprint_pw, null);
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
                cancel.setText(getString(R.string.exit));
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showQuitDialog();
                    }
                });
            }
        }

        @Override
        public void onAuthenticationError(int errMsgId, CharSequence errString) {
            UtilTool.Log("指紋", errMsgId + ":" + errString);
            if (mFingerprintdialog != null && mFingerprintdialog.isShowing() && errMsgId != 5) {
                if (mCount > 0) {
                    mCount--;
                    mCheck.setText(getString(R.string.check_fingerprint_error) + getString(R.string.hai_sheng) + mCount + getString(R.string.chance));
                    mCheck.setTextColor(getResources().getColor(R.color.red));
                    AnimatorTool.getInstance().editTextAnimator(mCheck);
                } else {
                    mFingerprintdialog.dismiss();
                    WsConnection.getInstance().logoutService(BaseActivity.this);
                    WsConnection.getInstance().goMainActivity();
                    mCount = 5;
                }
            }

        }

        @Override
        public void onAuthenticationFailed() {
            if (mFingerprintdialog != null && mFingerprintdialog.isShowing()) {
                if (mCount > 0) {
                    mCount--;
                    mCheck.setText(getString(R.string.check_fingerprint_error) + getString(R.string.hai_sheng) + mCount + getString(R.string.chance));
                    mCheck.setTextColor(getResources().getColor(R.color.red));
                    AnimatorTool.getInstance().editTextAnimator(mCheck);
                } else {
                    mFingerprintdialog.dismiss();
                    WsConnection.getInstance().logoutService(BaseActivity.this);
                    WsConnection.getInstance().goMainActivity();
                    mCount = 5;
                }
            }
        }

        @Override
        public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
            Toast.makeText(BaseActivity.this, getString(R.string.finger_move_fast), Toast.LENGTH_SHORT).show();
        }

        @SuppressLint("HandlerLeak")
        @Override
        public void onAuthenticationSucceeded(FingerprintManagerCompat.AuthenticationResult result) {
            if (mFingerprintdialog != null && mFingerprintdialog.isShowing()) {
                mCount = 5;
                mCheck.setText(getString(R.string.check_fingerprint_succeed));
                mCheck.setTextColor(getResources().getColor(R.color.blue2));
                new Handler() {
                    public void handleMessage(Message msg) {
                        mFingerprintdialog.dismiss();
                    }
                }.sendEmptyMessageDelayed(0, 1500);
            }
        }
    };

    private void showQuitDialog() {
        final DeleteCacheDialog deleteCacheDialog = new DeleteCacheDialog(R.layout.dialog_delete_cache, this, R.style.dialog);
        deleteCacheDialog.show();
        deleteCacheDialog.setTitle(getString(R.string.logout_hint));
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
                FingerprintUtil.cancel();
                mFingerprintdialog.dismiss();
                WsConnection.getInstance().logoutService(BaseActivity.this);
                WsConnection.getInstance().goMainActivity();
            }
        });

    }

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