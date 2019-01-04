package com.bclould.tea.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bclould.tea.R;
import com.bclould.tea.base.BaseActivity;
import com.bclould.tea.utils.MySharedPreferences;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by GA on 2017/10/11.
 */

public class DynamicSetActivity extends BaseActivity {

    private static final String DYNAMICSHOW = "dynamic_show";
    private static final String DYNAMICREMIND = "dynamic_remind";
    @Bind(R.id.bark)
    ImageView mBark;
    @Bind(R.id.on_off)
    ImageView mOnOff;
    @Bind(R.id.rl_dynamic_show)
    RelativeLayout mRlDynamicShow;
    @Bind(R.id.on_off2)
    ImageView mOnOff2;
    @Bind(R.id.rl_dynamic_remind)
    RelativeLayout mRlDynamicRemind;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dynamic_set);
        ButterKnife.bind(this);
        initInterface();
    }

    //初始化界面
    private void initInterface() {

        boolean b = MySharedPreferences.getInstance().getBoolean(DYNAMICSHOW);

        isClick = b;

        mOnOff.setSelected(b);

        boolean b2 = MySharedPreferences.getInstance().getBoolean(DYNAMICREMIND);

        isClick2 = b2;

        mOnOff2.setSelected(b2);

    }


    boolean isClick = false;
    boolean isClick2 = false;

    @OnClick({R.id.bark, R.id.on_off, R.id.on_off2})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:

                finish();

                break;
            case R.id.on_off:

                isClick = !isClick;

                setOnOff(isClick, DYNAMICSHOW);

                break;
            case R.id.on_off2:

                isClick2 = !isClick2;

                setOnOff(isClick2, DYNAMICREMIND);

                break;
        }
    }

    /**
     * 设置开关
     *
     * @param isClick 开关状态
     * @param str     开关的类型
     */

    private void setOnOff(boolean isClick, String str) {

        if (str.equals(DYNAMICSHOW)) {

            if (isClick) {

                mOnOff.setSelected(true);

            } else {

                mOnOff.setSelected(false);

            }

            MySharedPreferences.getInstance().setBoolean(DYNAMICSHOW, isClick);

        } else {

            if (isClick) {

                mOnOff2.setSelected(true);

            } else {

                mOnOff2.setSelected(false);

            }

            MySharedPreferences.getInstance().setBoolean(DYNAMICREMIND, isClick);

        }
    }
}
