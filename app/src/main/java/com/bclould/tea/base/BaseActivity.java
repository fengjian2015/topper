package com.bclould.tea.base;

import android.app.ActivityManager;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.bclould.tea.R;
import com.bclould.tea.ui.activity.ConversationActivity;
import com.bclould.tea.ui.activity.DeblockingFingerprintActivity;
import com.bclould.tea.ui.activity.DeblockingGestureActivity;
import com.bclould.tea.ui.widget.GestureLockViewGroup;
import com.bclould.tea.utils.AppLanguageUtils;
import com.bclould.tea.utils.MySharedPreferences;
import com.bclould.tea.utils.StringUtils;
import com.bclould.tea.utils.UtilTool;
import com.umeng.analytics.MobclickAgent;
import java.util.List;

import butterknife.ButterKnife;

import static com.bclould.tea.ui.activity.PayPwSelectorActivity.FINGERPRINT_PW_SELE;
import static com.bclould.tea.ui.activity.PayPwSelectorActivity.GESTURE_PW_SELE;

/**
 * Created by GA on 2017/9/22.
 */

public class BaseActivity extends SwipeActivity {

    private static boolean isActive;
    private Dialog mFingerprintdialog;
    private Dialog mGestureDialog;
    private TextView mTvHint;
    private GestureLockViewGroup mGestureView;
    protected TextView mTvTitleTop;
    protected TextView mTvAdd;
    protected TextView mTvAdd1;
    protected ImageView mImageView;
    protected ImageView mIvFinish;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyApp.getInstance().addActivity(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(getResources().getColor(R.color.app_bg_color));
        }
    }

    protected void setTitle(String content){
        mTvTitleTop=(TextView)findViewById(R.id.tv_title_top);
        mTvTitleTop.setText(content);
    }

    protected void setHtmlTitle(String content){
        mTvTitleTop=(TextView)findViewById(R.id.tv_title_top);
        mIvFinish=(ImageView)findViewById(R.id.iv_finish);
        mTvTitleTop.setText(content);
    }

    protected void setTitle(String content, String rightContent){
        mTvTitleTop=(TextView)findViewById(R.id.tv_title_top);
        mTvAdd=(TextView)findViewById(R.id.tv_add);
        mTvTitleTop.setText(content);
        mTvAdd.setText(rightContent);
        mTvAdd.setVisibility(View.VISIBLE);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(AppLanguageUtils.attachBaseContext(newBase, MySharedPreferences.getInstance().getString(newBase.getString(R.string.language_pref_key))));
    }

    /**
     * rightContent1红色右边字体
     * @param content
     * @param rightContent
     * @param rightContent1
     */
    protected void setTitle(String content, String rightContent,String rightContent1){
        mTvTitleTop=(TextView)findViewById(R.id.tv_title_top);
        mTvAdd=(TextView)findViewById(R.id.tv_add);
        mTvAdd1=(TextView)findViewById(R.id.tv_add1);
        mTvTitleTop.setText(content);
        if(!StringUtils.isEmpty(rightContent)) {
            mTvAdd.setText(rightContent);
            mTvAdd.setVisibility(View.VISIBLE);
        }
        if(!StringUtils.isEmpty(rightContent1)) {
            mTvAdd1.setVisibility(View.VISIBLE);
            mTvAdd1.setText(rightContent1);
        }
    }

    protected void setTitle(String content,int id){
        mTvTitleTop=(TextView)findViewById(R.id.tv_title_top);
        mTvAdd=(TextView)findViewById(R.id.tv_add);
        mTvAdd1=(TextView)findViewById(R.id.tv_add1);
        mTvTitleTop.setText(content);
        mImageView=(ImageView)findViewById(R.id.iv_more);
        mImageView.setVisibility(View.VISIBLE);
        mImageView.setImageResource(id);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            if (this.getClass().getName().equals(ConversationActivity.class.getName()))
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
                    startActivity(new Intent(this, DeblockingFingerprintActivity.class));
                }
                if (MySharedPreferences.getInstance().getBoolean(GESTURE_PW_SELE)) {
                    startActivity(new Intent(this, DeblockingGestureActivity.class));
                }
            }
            UtilTool.Log("ACTIVITY", "程序从后台唤醒");
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        if (!isAppOnForeground()) {
            //app 进入后台
            isActive = false;//记录当前已经进入后台
            UtilTool.Log("ACTIVITY", "程序进入后台");

        }
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.finish();
        MyApp.getInstance().removeActivity(this);
        ButterKnife.unbind(this);
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