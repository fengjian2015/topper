package com.bclould.tea.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bclould.tea.R;
import com.bclould.tea.base.BaseActivity;
import com.bclould.tea.topperchat.WsConnection;
import com.bclould.tea.ui.widget.DeleteCacheDialog;
import com.bclould.tea.ui.widget.GestureLockViewGroup;
import com.bclould.tea.utils.AppLanguageUtils;
import com.bclould.tea.utils.MySharedPreferences;
import com.bclould.tea.utils.UtilTool;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.bclould.tea.ui.activity.SetGesturePWActivity.GESTURE_ANSWER;

/**
 * Created by GA on 2018/8/2.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class DeblockingGestureActivity extends BaseActivity {
    @Bind(R.id.iv_logo)
    ImageView mIvLogo;
    @Bind(R.id.tv_app_name)
    TextView mTvAppName;
    @Bind(R.id.tv_hint)
    TextView mTvHint;
    @Bind(R.id.gesture_view)
    GestureLockViewGroup mGestureView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_gesture_pw);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        mTvHint.setText(getString(R.string.import_gesture));
        String mAnswerstr = MySharedPreferences.getInstance().getString(GESTURE_ANSWER);
        int[] arr = new int[mAnswerstr.length()];
        for (int i = 0; i < mAnswerstr.length(); i++) {
            arr[i] = Character.getNumericValue(mAnswerstr.charAt(i));
        }
        mGestureView.setAnswer(arr);
        mGestureView.setOnGestureLockViewListener(mOnGestureLockViewListener);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(AppLanguageUtils.attachBaseContext(newBase, MySharedPreferences.getInstance().getString(newBase.getString(R.string.language_pref_key))));
    }

    GestureLockViewGroup.OnGestureLockViewListener mOnGestureLockViewListener = new GestureLockViewGroup.OnGestureLockViewListener() {

        public int mCount = 5;

        @Override
        public void onBlockSelected(int position) {

        }

        @Override
        public void onGestureEvent(boolean matched) {
            if (mCount > 0) {
                if (matched) {
                    if (mCount > 0) {
                        finish();
                        mCount = 5;
                    }
                } else {
                    mCount--;
                    mTvHint.setText(getString(R.string.set_gesture_hint5) + getString(R.string.hai_sheng) + mCount + getString(R.string.chance));
                    mTvHint.setTextColor(getResources().getColor(R.color.red));
                }
            } else {
                showHintDialog();
            }
        }

        @Override
        public void onUnmatchedExceedBoundary() {
            UtilTool.Log("手勢", "onUnmatchedExceedBoundary");
            if (mCount == 0) {
                showHintDialog();
            }
        }

        private void showHintDialog() {
            final DeleteCacheDialog deleteCacheDialog = new DeleteCacheDialog(R.layout.dialog_delete_cache, DeblockingGestureActivity.this, R.style.dialog);
            deleteCacheDialog.show();
            deleteCacheDialog.setTitle(getString(R.string.gesture_count_exceed_hint));
            Button cancel = (Button) deleteCacheDialog.findViewById(R.id.btn_cancel);
            cancel.setText(getString(R.string.again_login));
            Button confirm = (Button) deleteCacheDialog.findViewById(R.id.btn_confirm);
            confirm.setText(getString(R.string.cancel_gesture));
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    deleteCacheDialog.dismiss();
                    finish();
                    WsConnection.getInstance().goMainActivity();
                }
            });
            confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    deleteCacheDialog.dismiss();
                    Intent intent = new Intent(DeblockingGestureActivity.this, PayPwSelectorActivity.class);
                    intent.putExtra("code", 1);
                    startActivityForResult(intent, 1);
                }
            });
        }
    };


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            finish();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
