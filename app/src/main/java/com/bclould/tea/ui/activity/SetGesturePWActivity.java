package com.bclould.tea.ui.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bclould.tea.Presenter.UpdateLogPresenter;
import com.bclould.tea.R;
import com.bclould.tea.base.BaseActivity;
import com.bclould.tea.base.MyApp;
import com.bclould.tea.ui.widget.GestureLockViewGroup;
import com.bclould.tea.ui.widget.PWDDialog;
import com.bclould.tea.utils.AppLanguageUtils;
import com.bclould.tea.utils.MySharedPreferences;
import com.bclould.tea.utils.ToastShow;
import com.bclould.tea.utils.UtilTool;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by GA on 2018/5/22.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class SetGesturePWActivity extends BaseActivity {

    public static final String GESTURE_ANSWER = "gesture_answer";
    @Bind(R.id.gesture_view)
    GestureLockViewGroup mGestureView;
    @Bind(R.id.tv_hint)
    TextView mTvHint;
    private String mAnswerstr = "";
    private int[] mAnswerarr;
    private PWDDialog pwdDialog;
    private String mGesturePW;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_gesture);
        ButterKnife.bind(this);
        setTitle(getString(R.string.set_gesture));
        initIntent();
        initGesture();
    }

    private void initIntent() {
        mAnswerarr = getIntent().getIntArrayExtra("answer");
        if (mAnswerarr != null) {
            mGestureView.setAnswer(mAnswerarr);
            mTvHint.setText(getString(R.string.set_gesture_hint3));
        }
    }

    private void initGesture() {
        mGestureView.setUnMatchExceedBoundary(5);
        mGestureView.setOnGestureLockViewListener(new GestureLockViewGroup.OnGestureLockViewListener() {
            @Override
            public void onBlockSelected(int position) {
                UtilTool.Log("手勢", position + "   ：onBlockSelected");
                mAnswerstr += position;
            }

            @SuppressLint("HandlerLeak")
            @Override
            public void onGestureEvent(boolean matched) {
                if (mAnswerarr == null) {
                    if (mAnswerstr.length() >= 4) {
                        Intent intent = new Intent(SetGesturePWActivity.this, SetGesturePWActivity.class);
                        int[] arr = new int[mAnswerstr.length()];
                        for (int i = 0; i < mAnswerstr.length(); i++) {
                            arr[i] = Character.getNumericValue(mAnswerstr.charAt(i));
                        }
                        intent.putExtra("answer", arr);
                        startActivity(intent);
                        mGestureView.reset();
                        mAnswerstr = "";
                    } else {
                        mAnswerstr = "";
                        mTvHint.setTextColor(getResources().getColor(R.color.red));
                        mTvHint.setText(getString(R.string.set_gesture_hint2));
                    }
                } else {
                    if (matched) {
                        showPWDialog();
                    } else {
                        new Handler() {
                            public void handleMessage(Message msg) {
                                finish();
                            }
                        }.sendEmptyMessageDelayed(0, 1500);
                        mTvHint.setTextColor(getResources().getColor(R.color.red));
                        mTvHint.setText(getString(R.string.set_gesture_hint4));
                    }
                }
                UtilTool.Log("手勢", matched + "   ：onGestureEvent");
            }

            @Override
            public void onUnmatchedExceedBoundary() {
                UtilTool.Log("手勢", "onUnmatchedExceedBoundary");
            }
        });
    }

    private void showPWDialog() {
        pwdDialog=new PWDDialog(this);
        pwdDialog.setOnPWDresult(new PWDDialog.OnPWDresult() {
            @Override
            public void success(String password) {
                setGesture(password);
            }
        });
        pwdDialog.showDialog(getString(R.string.verify_pay_pw),null,null,null,null);
    }

    private void setGesture(String password) {
        mGesturePW = "";
        for (int i = 0; i < mAnswerarr.length; i++) {
            mGesturePW += mAnswerarr[i];
        }
        UpdateLogPresenter updateLogPresenter = new UpdateLogPresenter(SetGesturePWActivity.this);
        updateLogPresenter.setGesture(password, 1, mGesturePW, new UpdateLogPresenter.CallBack2() {
            @Override
            public void send(int type) {
                if (type == 1) {
                    ToastShow.showToast2(SetGesturePWActivity.this, getString(R.string.set_succeed));
                    MyApp.getInstance().exit(SetGesturePWActivity.class.getName());

                    UtilTool.Log("手勢", mGesturePW);
                    MySharedPreferences.getInstance().setString(GESTURE_ANSWER, mGesturePW);
                }
            }

            @Override
            public void error() {

            }
        });
    }


    @OnClick(R.id.bark)
    public void onViewClicked() {
        finish();
    }
}
