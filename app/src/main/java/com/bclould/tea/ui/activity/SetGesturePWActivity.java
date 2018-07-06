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
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bclould.tea.Presenter.UpdateLogPresenter;
import com.bclould.tea.R;
import com.bclould.tea.base.BaseActivity;
import com.bclould.tea.base.MyApp;
import com.bclould.tea.ui.widget.GestureLockViewGroup;
import com.bclould.tea.ui.widget.VirtualKeyboardView;
import com.bclould.tea.utils.MySharedPreferences;
import com.bclould.tea.utils.ToastShow;
import com.bclould.tea.utils.UtilTool;
import com.maning.pswedittextlibrary.MNPasswordEditText;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.bclould.tea.R.style.BottomDialog;

/**
 * Created by GA on 2018/5/22.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class SetGesturePWActivity extends BaseActivity {

    public static final String GESTURE_ANSWER = "gesture_answer";
    @Bind(R.id.bark)
    ImageView mBark;
    @Bind(R.id.rl_title)
    RelativeLayout mRlTitle;
    @Bind(R.id.gesture_view)
    GestureLockViewGroup mGestureView;
    @Bind(R.id.tv_hint)
    TextView mTvHint;
    private String mAnswerstr = "";
    private int[] mAnswerarr;
    private ArrayList<Map<String, String>> valueList;
    private Animation mEnterAnim;
    private Animation mExitAnim;
    private Dialog mRedDialog;
    private GridView mGridView;
    private MNPasswordEditText mEtPassword;
    private String mGesturePW;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyApp.getInstance().addActivity(this);
        setContentView(R.layout.activity_set_gesture);
        ButterKnife.bind(this);
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
                    setGesture(password);
                }
            }
        });
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
        });
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

    @OnClick(R.id.bark)
    public void onViewClicked() {
        finish();
    }
}
