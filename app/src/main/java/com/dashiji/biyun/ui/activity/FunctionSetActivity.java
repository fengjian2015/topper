package com.dashiji.biyun.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.dashiji.biyun.base.MyApp;
import com.dashiji.biyun.R;
import com.dashiji.biyun.base.BaseActivity;
import com.dashiji.biyun.utils.MySharedPreferences;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by GA on 2017/10/11.
 */

public class FunctionSetActivity extends BaseActivity {

    private static final String SAVE_PASSWORD = "save_password";
    private static final String SELF_MOTION = "self_motion";
    private static final String MULTI_LANGUAGE = "multi_language";
    private static final String NEW_MSG_HINT = "new_msg_hint";
    @Bind(R.id.bark)
    ImageView mBark;
    @Bind(R.id.on_off)
    ImageView mOnOff;
    @Bind(R.id.rl_save_password)
    RelativeLayout mRlSavePassword;
    @Bind(R.id.on_off2)
    ImageView mOnOff2;
    @Bind(R.id.rl_self_motion)
    RelativeLayout mRlSelfMotion;
    @Bind(R.id.on_off3)
    ImageView mOnOff3;
    @Bind(R.id.rl_multi_language)
    RelativeLayout mRlMultiLanguage;
    @Bind(R.id.on_off4)
    ImageView mOnOff4;
    @Bind(R.id.rl_new_msg_hint)
    RelativeLayout mRlNewMsgHint;
    boolean isClick = false;
    boolean isClick2 = false;
    boolean isClick3 = false;
    boolean isClick4 = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_function_set);
        ButterKnife.bind(this);
        initInterface();
        MyApp.getInstance().addActivity(this);
    }

    //初始化界面
    private void initInterface() {

        boolean b = MySharedPreferences.getInstance().getBoolean(SAVE_PASSWORD);

        isClick = b;

        mOnOff.setSelected(b);

        boolean b1 = MySharedPreferences.getInstance().getBoolean(SELF_MOTION);

        isClick2 = b1;

        mOnOff2.setSelected(b1);

        boolean b2 = MySharedPreferences.getInstance().getBoolean(MULTI_LANGUAGE);

        isClick3 = b2;

        mOnOff3.setSelected(b2);

        boolean b3 = MySharedPreferences.getInstance().getBoolean(NEW_MSG_HINT);

        isClick4 = b3;

        mOnOff4.setSelected(b3);

    }

    @OnClick({R.id.bark, R.id.rl_save_password, R.id.rl_self_motion, R.id.rl_multi_language, R.id.rl_new_msg_hint})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:

                finish();

                break;
            case R.id.rl_save_password:

                isClick = !isClick;

                setOnOff(isClick, SAVE_PASSWORD);

                break;
            case R.id.rl_self_motion:

                isClick2 = !isClick2;

                setOnOff(isClick2, SELF_MOTION);

                break;
            case R.id.rl_multi_language:

                isClick3 = !isClick3;

                setOnOff(isClick3, MULTI_LANGUAGE);

                break;
            case R.id.rl_new_msg_hint:

                isClick4 = !isClick4;

                setOnOff(isClick4, NEW_MSG_HINT);

                break;
        }
    }

    private void setOnOff(boolean isClick, String str) {

        if (str.equals(SAVE_PASSWORD)) {

            if (isClick) {

                mOnOff.setSelected(true);

            } else {

                mOnOff.setSelected(false);

            }

            MySharedPreferences.getInstance().setBoolean(SAVE_PASSWORD, isClick);

        } else if (str.equals(SELF_MOTION)) {

            if (isClick) {

                mOnOff2.setSelected(true);

            } else {

                mOnOff2.setSelected(false);

            }

            MySharedPreferences.getInstance().setBoolean(SELF_MOTION, isClick);

        } else if (str.equals(MULTI_LANGUAGE)) {

            if (isClick) {

                mOnOff3.setSelected(true);

            } else {

                mOnOff3.setSelected(false);

            }

            MySharedPreferences.getInstance().setBoolean(MULTI_LANGUAGE, isClick);

        } else {

            if (isClick) {

                mOnOff4.setSelected(true);

            } else {

                mOnOff4.setSelected(false);

            }

            MySharedPreferences.getInstance().setBoolean(NEW_MSG_HINT, isClick);

        }
    }

}
