package com.dashiji.biyun.ui.activity;

import android.content.Intent;
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

public class PrivateRightsActivity extends BaseActivity {

    private static final String PHONEADDRESSLIST = "phone_address_list";
    @Bind(R.id.bark)
    ImageView mBark;
    @Bind(R.id.rl_add_friend)
    RelativeLayout mRlAddFriend;
    @Bind(R.id.on_off)
    ImageView mOnOff;
    @Bind(R.id.rl_matching_phone)
    RelativeLayout mRlMatchingPhone;
    @Bind(R.id.rl_dynamic_set)
    RelativeLayout mRlDynamicSet;
    private boolean isClick = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_private_rights);
        ButterKnife.bind(this);
        initInterface();
        MyApp.getInstance().addActivity(this);
    }

    //初始化界面
    private void initInterface() {

        boolean b = MySharedPreferences.getInstance().getBoolean(PHONEADDRESSLIST);

        isClick = b;

        mOnOff.setSelected(b);

    }

    @OnClick({R.id.bark, R.id.rl_add_friend, R.id.on_off, R.id.rl_dynamic_set})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;
            case R.id.rl_add_friend:

                startActivity(new Intent(this, AddFriendSetActivity.class));

                break;
            case R.id.on_off:

                setOnOff();

                break;
            case R.id.rl_dynamic_set:

                startActivity(new Intent(this, DynamicSetActivity.class));

                break;
        }
    }

    //按钮处理
    private void setOnOff() {
        isClick = !isClick;

        if (isClick) {

            mOnOff.setSelected(true);

        } else {

            mOnOff.setSelected(false);

        }

        MySharedPreferences.getInstance().setBoolean(PHONEADDRESSLIST, isClick);
    }

}
