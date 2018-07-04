package com.bclould.tea.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bclould.tea.Presenter.LogoutPresenter;
import com.bclould.tea.R;
import com.bclould.tea.base.BaseActivity;
import com.bclould.tea.base.MyApp;
import com.bclould.tea.ui.widget.DeleteCacheDialog;
import com.bclould.tea.utils.Constants;
import com.bclould.tea.utils.MySharedPreferences;
import com.bclould.tea.utils.ToastShow;
import com.bclould.tea.utils.UtilTool;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.bclould.tea.utils.MySharedPreferences.SETTING;

/**
 * Created by GA on 2017/9/22.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class SystemSetActivity extends BaseActivity {
    public static final String INFORM = "inform";
    public static final String PRIVATE = "private";
    @Bind(R.id.bark)
    ImageView mBark;
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
    @Bind(R.id.tv_cache_count)
    TextView mTvCacheCount;
    @Bind(R.id.rl_cache)
    RelativeLayout mRlCache;
    @Bind(R.id.btn_brak)
    Button mBtnBrak;

    private long mFolderSize;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system_set);
        ButterKnife.bind(this);
        MyApp.getInstance().addActivity(this);
        init();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    private void init() {
        boolean privateStatus = MySharedPreferences.getInstance().getBoolean(PRIVATE);
        mOnOffPrivate.setSelected(privateStatus);
        isOnOff2 = privateStatus;
        SharedPreferences sp = getSharedPreferences(SETTING, 0);
        if (sp.contains(INFORM)) {
            boolean informStatus = MySharedPreferences.getInstance().getBoolean(INFORM);
            mOnOffInform.setSelected(informStatus);
            isOnOff = informStatus;
        } else {
            mOnOffInform.setSelected(true);
            isOnOff = true;
        }

        countCache();
    }

    private void countCache() {
        mFolderSize = UtilTool.getFolderSize(new File(Constants.LOG_DIR));
        String fileSize = UtilTool.FormetFileSize(mFolderSize);
        mTvCacheCount.setText(fileSize);
    }


    //显示退出弹框
    private void showDialog() {
        final DeleteCacheDialog deleteCacheDialog = new DeleteCacheDialog(R.layout.dialog_delete_cache, this, R.style.dialog);
        deleteCacheDialog.show();
        deleteCacheDialog.setTitle(getString(R.string.quit_hint));
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

    boolean isOnOff = false;
    boolean isOnOff2 = false;

    @OnClick({R.id.btn_brak, R.id.bark, R.id.rl_inform, R.id.rl_private, R.id.rl_help, R.id.rl_cache})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_brak:
                showDialog();
                break;
            case R.id.bark:
                finish();
                break;
            case R.id.rl_inform:
                isOnOff = !isOnOff;
                setOnOff(INFORM, isOnOff);
                break;
            case R.id.rl_private:
                isOnOff2 = !isOnOff2;
                setOnOff(PRIVATE, isOnOff2);
                break;
            case R.id.rl_help:
                startActivity(new Intent(this, ProblemFeedBackActivity.class));
                break;
            case R.id.rl_cache:
                if (mFolderSize != 0) {
                    showCacheDialog();
                } else {
                    ToastShow.showToast2(this, getString(R.string.no_cache));
                }
                break;
        }
    }

    //显示Dialog
    private void showCacheDialog() {

        DeleteCacheDialog deleteCacheDialog = new DeleteCacheDialog(R.layout.dialog_delete_cache, this, R.style.dialog);

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
                UtilTool.deleteFolderFile(Constants.LOG_DIR, false);
                mTvCacheCount.setText("0M");
                ToastShow.showToast2(SystemSetActivity.this, getString(R.string.delete_cache_succeed));
                dialog.dismiss();
            }
        });

    }

    private void setOnOff(String key, boolean status) {
        if (key.equals(PRIVATE)) {
            if (status) {
                mOnOffPrivate.setSelected(true);
            } else {
                mOnOffPrivate.setSelected(false);
            }
            MySharedPreferences.getInstance().setBoolean(PRIVATE, status);
        } else {
            if (status) {
                mOnOffInform.setSelected(true);
            } else {
                mOnOffInform.setSelected(false);
            }
            MySharedPreferences.getInstance().setBoolean(INFORM, status);
        }
    }
}
