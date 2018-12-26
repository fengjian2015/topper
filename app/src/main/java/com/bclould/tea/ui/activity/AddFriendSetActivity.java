package com.bclould.tea.ui.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;

import com.bclould.tea.R;
import com.bclould.tea.base.BaseActivity;
import com.bclould.tea.base.MyApp;
import com.bclould.tea.utils.AppLanguageUtils;
import com.bclould.tea.utils.MySharedPreferences;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by GA on 2017/10/11.
 */

public class AddFriendSetActivity extends BaseActivity {

    private static final String NICKNAME = "nickname";
    private static final String PHONENUMBER = "phone_number";
    private static final String DYNAMIC = "dynamic";
    private static final String DYNAMIC2 = "dynamic2";
    public static final String QRCODE = "qr_code";
    private static final String GROUPCHAT = "group_chat";
    @Bind(R.id.bark)
    ImageView mBark;
    @Bind(R.id.nickname)
    ImageView mNickname;
    @Bind(R.id.phone_number)
    ImageView mPhoneNumber;
    @Bind(R.id.dynamic)
    ImageView mDynamic;
    @Bind(R.id.qr_code)
    ImageView mQrCode;
    @Bind(R.id.dynamic2)
    ImageView mDynamic2;
    @Bind(R.id.group_chat)
    ImageView mGroupChat;
    boolean isClick = false;
    boolean isClick2 = false;
    boolean isClick3 = false;
    boolean isClick4 = false;
    boolean isClick5 = false;
    boolean isClick6 = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend_set);
        ButterKnife.bind(this);
        setTitle(getString(R.string.add_friend_setting));
        initInterface();
        MyApp.getInstance().addActivity(this);

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(AppLanguageUtils.attachBaseContext(newBase, MySharedPreferences.getInstance().getString(newBase.getString(R.string.language_pref_key))));
    }

    //初始化界面
    private void initInterface() {

        boolean b = MySharedPreferences.getInstance().getBoolean(NICKNAME);

        isClick = b;

        mNickname.setSelected(b);

        boolean b1 = MySharedPreferences.getInstance().getBoolean(PHONENUMBER);

        isClick2 = b1;

        mPhoneNumber.setSelected(b1);

        boolean b2 = MySharedPreferences.getInstance().getBoolean(DYNAMIC);

        isClick3 = b2;

        mDynamic.setSelected(b2);

        boolean b3 = MySharedPreferences.getInstance().getBoolean(QRCODE);

        isClick4 = b3;

        mQrCode.setSelected(b3);

        boolean b4 = MySharedPreferences.getInstance().getBoolean(DYNAMIC2);

        isClick5 = b4;

        mDynamic2.setSelected(b4);

        boolean b5 = MySharedPreferences.getInstance().getBoolean(GROUPCHAT);

        isClick6 = b5;

        mGroupChat.setSelected(b5);

    }

    @OnClick({R.id.bark, R.id.nickname, R.id.phone_number, R.id.dynamic, R.id.qr_code, R.id.dynamic2, R.id.group_chat})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:

                finish();

                break;
            case R.id.nickname:

                isClick = !isClick;

                setOnOff(isClick, NICKNAME);

                break;
            case R.id.phone_number:

                isClick2 = !isClick2;

                setOnOff(isClick2, PHONENUMBER);

                break;
            case R.id.dynamic:

                isClick3 = !isClick3;

                setOnOff(isClick3, DYNAMIC);

                break;
            case R.id.qr_code:

                isClick4 = !isClick4;

                setOnOff(isClick4, QRCODE);

                break;
            case R.id.dynamic2:

                isClick5 = !isClick5;

                setOnOff(isClick5, DYNAMIC2);

                break;
            case R.id.group_chat:

                isClick6 = !isClick6;

                setOnOff(isClick6, GROUPCHAT);

                break;
        }
    }

    /**
     * 设置开关状态
     *
     * @param isClick 开关状态
     * @param str     开关类型
     */
    private void setOnOff(boolean isClick, String str) {

        if (str.equals(NICKNAME)) {

            if (isClick) {

                mNickname.setSelected(true);

            } else {

                mNickname.setSelected(false);

            }

            MySharedPreferences.getInstance().setBoolean(NICKNAME, isClick);

        } else if (str.equals(PHONENUMBER)) {

            if (isClick) {

                mPhoneNumber.setSelected(true);

            } else {

                mPhoneNumber.setSelected(false);

            }

            MySharedPreferences.getInstance().setBoolean(PHONENUMBER, isClick);

        } else if (str.equals(DYNAMIC)) {

            if (isClick) {

                mDynamic.setSelected(true);

            } else {

                mDynamic.setSelected(false);

            }

            MySharedPreferences.getInstance().setBoolean(DYNAMIC, isClick);

        } else if (str.equals(QRCODE)) {

            if (isClick) {

                mQrCode.setSelected(true);

            } else {

                mQrCode.setSelected(false);

            }

            MySharedPreferences.getInstance().setBoolean(QRCODE, isClick);

        } else if (str.equals(DYNAMIC2)) {

            if (isClick) {

                mDynamic2.setSelected(true);

            } else {

                mDynamic2.setSelected(false);

            }

            MySharedPreferences.getInstance().setBoolean(DYNAMIC2, isClick);

        } else {

            if (isClick) {

                mGroupChat.setSelected(true);

            } else {

                mGroupChat.setSelected(false);

            }

            MySharedPreferences.getInstance().setBoolean(GROUPCHAT, isClick);

        }

    }

}
