package com.bclould.tea.ui.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bclould.tea.Presenter.UpdateLogPresenter;
import com.bclould.tea.R;
import com.bclould.tea.base.BaseActivity;
import com.bclould.tea.base.MyApp;
import com.bclould.tea.utils.Constants;
import com.bclould.tea.utils.MySharedPreferences;
import com.bclould.tea.utils.UtilTool;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.bclould.tea.Presenter.LoginPresenter.IS_UPDATE;

/**
 * Created by GA on 2017/9/22.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class GuanYuMeActivity extends BaseActivity {

    @Bind(R.id.bark)
    ImageView mBark;
    @Bind(R.id.tv)
    TextView mTv;
    @Bind(R.id.tv_new_update)
    TextView mTvNewUpdate;
    @Bind(R.id.rl_new)
    RelativeLayout mRlNew;
    @Bind(R.id.tv_version)
    TextView mTvVersion;
    @Bind(R.id.cv_check_update)
    CardView mCvCheckUpdate;
    @Bind(R.id.rl_update_log)
    RelativeLayout mRlUpdateLog;
    private UpdateLogPresenter mUpdateLogPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guanyu_me);
        ButterKnife.bind(this);
        if (UtilTool.compareVersion(this)) {
            mTvNewUpdate.setVisibility(View.VISIBLE);
        } else {
            mTvNewUpdate.setVisibility(View.GONE);
        }
        if (MySharedPreferences.getInstance().getInteger(IS_UPDATE) == 1) {
            mCvCheckUpdate.setVisibility(View.VISIBLE);
        } else {
            mCvCheckUpdate.setVisibility(View.GONE);
        }
        mUpdateLogPresenter = new UpdateLogPresenter(this);
        String versionCode = UtilTool.getVersionCode(this);
        mTvVersion.setText(getString(R.string.app_name) + " v" + versionCode);
        MyApp.getInstance().addActivity(this);
    }

    @OnClick({R.id.bark, R.id.rl_new, R.id.rl_update_log})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;
            case R.id.rl_update_log:
                startActivity(new Intent(this, UpdateLogActivity.class));
                break;
            case R.id.rl_new:
                startActivity(new Intent(GuanYuMeActivity.this, VersionsUpdateActivity.class));
                /*String url = MySharedPreferences.getInstance().getString(Constants.NEW_APK_URL);
                String apkName = MySharedPreferences.getInstance().getString(Constants.NEW_APK_NAME);
                String body = MySharedPreferences.getInstance().getString(Constants.NEW_APK_BODY);
                if (!url.isEmpty()) {
                    startActivity(new Intent(GuanYuMeActivity.this, VersionsUpdateActivity.class));
                } else {
                    if (!UtilTool.isFastClick()) {
                        checkVersion();
                    } else {
                        Toast.makeText(this, getString(R.string.toast_after_time), Toast.LENGTH_SHORT).show();
                    }
                }*/
                break;
        }
    }

    private void checkVersion() {
        mUpdateLogPresenter.checkVersion(new UpdateLogPresenter.CallBack2() {
            @Override
            public void send(int type) {
                if (type == 1) {
                    mTvNewUpdate.setVisibility(View.VISIBLE);
                } else {
                    mTvNewUpdate.setVisibility(View.GONE);
                }
            }
        });
    }
}
