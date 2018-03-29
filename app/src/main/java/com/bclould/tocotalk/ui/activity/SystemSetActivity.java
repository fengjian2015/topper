package com.bclould.tocotalk.ui.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bclould.tocotalk.Presenter.LogoutPresenter;
import com.bclould.tocotalk.R;
import com.bclould.tocotalk.base.BaseActivity;
import com.bclould.tocotalk.base.MyApp;
import com.bclould.tocotalk.ui.widget.DeleteCacheDialog;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by GA on 2017/9/22.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class SystemSetActivity extends BaseActivity {

    private static final String DISTURB = "disturb";
    @Bind(R.id.bark)
    ImageView mBark;
    @Bind(R.id.iv_concern_we)
    ImageView mIvConcernWe;
    @Bind(R.id.rl_concern_we)
    RelativeLayout mRlConcernWe;
    @Bind(R.id.iv_inform)
    ImageView mIvInform;
    @Bind(R.id.tv_inform)
    TextView mTvInform;
    @Bind(R.id.on_off_inform)
    ImageView mOnOffInform;
    @Bind(R.id.rl_inform)
    RelativeLayout mRlInform;
    @Bind(R.id.iv_private)
    ImageView mIvPrivate;
    @Bind(R.id.tv_private)
    TextView mTvPrivate;
    @Bind(R.id.on_off_private)
    ImageView mOnOffPrivate;
    @Bind(R.id.rl_private)
    RelativeLayout mRlPrivate;
    @Bind(R.id.iv_help)
    ImageView mIvHelp;
    @Bind(R.id.tv_help)
    TextView mTvHelp;
    @Bind(R.id.rl_help)
    RelativeLayout mRlHelp;
    @Bind(R.id.iv_cache)
    ImageView mIvCache;
    @Bind(R.id.tv_cache)
    TextView mTvCache;
    @Bind(R.id.rl_cache)
    RelativeLayout mRlCache;
    @Bind(R.id.btn_brak)
    Button mBtnBrak;
    @Bind(R.id.tv_cache_count)
    TextView mTvCacheCount;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system_set);
        ButterKnife.bind(this);
        finish();
        MyApp.getInstance().addActivity(this);
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
        logoutPresenter.logout();
    }


    @OnClick({R.id.btn_brak, R.id.bark, R.id.rl_concern_we, R.id.rl_inform, R.id.rl_private, R.id.rl_help, R.id.rl_cache})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_brak:
                showDialog();
                break;
            case R.id.bark:
                finish();
                break;
            case R.id.rl_concern_we:
                startActivity(new Intent(this, GuanYuMeActivity.class));
                break;
            case R.id.rl_inform:
                setOnOff();
                break;
            case R.id.rl_private:
                setOnOff();
                break;
            case R.id.rl_help:
                startActivity(new Intent(this, ProblemFeedBackActivity.class));
                break;
            case R.id.rl_cache:
                showCacheDialog();
                break;
        }
    }

    //显示Dialog
    private void showCacheDialog() {

        DeleteCacheDialog deleteCacheDialog = new DeleteCacheDialog(R.layout.dialog_delete_cache, this);

        deleteCacheDialog.show();

        dialogClick(deleteCacheDialog);

    }

    //Dialog的点击事件处理
    private void dialogClick(final DeleteCacheDialog dialog) {

        Button cancel = (Button) dialog.findViewById(R.id.btn_cancel);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();

            }
        });

        Button confirm = (Button) dialog.findViewById(R.id.btn_confirm);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTvCacheCount.setText("0");
                dialog.dismiss();

            }
        });

    }

    private void setOnOff() {

    }
}
