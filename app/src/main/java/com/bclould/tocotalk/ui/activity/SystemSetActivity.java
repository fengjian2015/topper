package com.bclould.tocotalk.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bclould.tocotalk.base.MyApp;
import com.bclould.tocotalk.Presenter.LogoutPresenter;
import com.bclould.tocotalk.R;
import com.bclould.tocotalk.base.BaseActivity;
import com.bclould.tocotalk.ui.widget.DeleteCacheDialog;
import com.bclould.tocotalk.utils.MySharedPreferences;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by GA on 2017/9/22.
 */

public class SystemSetActivity extends BaseActivity {

    private static final String DISTURB = "disturb";
    @Bind(R.id.bark)
    ImageView mBark;
    @Bind(R.id.rl_id_safety)
    RelativeLayout mRlIdSafety;
    @Bind(R.id.rl_function_set)
    RelativeLayout mRlFunctionSet;
    @Bind(R.id.on_off)
    ImageView mOnOff;
    @Bind(R.id.rl_disturb)
    RelativeLayout mRlDisturb;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system_set);
        ButterKnife.bind(this);
        initInterface();
        MyApp.getInstance().addActivity(this);
    }

    //初始化界面
    private void initInterface() {

        boolean b = MySharedPreferences.getInstance().getBoolean(DISTURB);

        isClick = b;

        mOnOff.setSelected(b);

    }

    boolean isClick = false;

    //点击事件的处理
    @OnClick({R.id.bark, R.id.rl_id_safety, R.id.rl_function_set, R.id.on_off, R.id.rl_disturb, R.id.btn_brak})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:

                finish();

                break;
            case R.id.rl_id_safety:

                startActivity(new Intent(this, UserSafetyActivity.class));

                break;
            case R.id.rl_function_set:

                startActivity(new Intent(this, FunctionSetActivity.class));

                break;
            case R.id.on_off:

                setDisturb();

                break;
            case R.id.rl_disturb:

                break;
            case R.id.btn_brak:
                showDialog();
                break;
        }
    }

    //显示退出弹框
    private void showDialog() {
        final DeleteCacheDialog deleteCacheDialog = new DeleteCacheDialog(R.layout.dialog_delete_cache, this);
        deleteCacheDialog.show();
        deleteCacheDialog.setTitle("确定要退出登录吗？");
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
                logout();
            }
        });

    }

    //退出登录
    private void logout() {
        LogoutPresenter logoutPresenter = new LogoutPresenter(this);
    }

    //勿扰按钮处理
    private void setDisturb() {
        isClick = !isClick;

        if (isClick) {

            mOnOff.setSelected(true);

        } else {

            mOnOff.setSelected(false);

        }

        MySharedPreferences.getInstance().setBoolean(DISTURB, isClick);
    }
}
