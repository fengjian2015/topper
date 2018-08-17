package com.bclould.tea.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bclould.tea.Presenter.UpdateLogPresenter;
import com.bclould.tea.R;
import com.bclould.tea.base.BaseActivity;
import com.bclould.tea.base.MyApp;
import com.bclould.tea.utils.AppLanguageUtils;
import com.bclould.tea.utils.MessageEvent;
import com.bclould.tea.utils.MySharedPreferences;
import com.bclould.tea.utils.UtilTool;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

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
    @Bind(R.id.tv_version)
    TextView mTvVersion;
    @Bind(R.id.tv)
    TextView mTv;
    @Bind(R.id.tv_new_update)
    TextView mTvNewUpdate;
    @Bind(R.id.rl_check_update)
    RelativeLayout mRlCheckUpdate;
    @Bind(R.id.rl_update_log)
    RelativeLayout mRlUpdateLog;
    private UpdateLogPresenter mUpdateLogPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guanyu_me);
        ButterKnife.bind(this);
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
        if (UtilTool.compareVersion(this)) {
            mTvNewUpdate.setVisibility(View.VISIBLE);
        } else {
            mTvNewUpdate.setVisibility(View.GONE);
        }
        if (MySharedPreferences.getInstance().getInteger(IS_UPDATE) == 1) {
            mRlCheckUpdate.setVisibility(View.VISIBLE);
        } else {
            mRlCheckUpdate.setVisibility(View.GONE);
        }
        mUpdateLogPresenter = new UpdateLogPresenter(this);
        String versionCode = UtilTool.getVersionCode(this);
        mTvVersion.setText(getString(R.string.app_name) + " v" + versionCode);
        MyApp.getInstance().addActivity(this);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(AppLanguageUtils.attachBaseContext(newBase, MySharedPreferences.getInstance().getString(newBase.getString(R.string.language_pref_key))));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        String msg = event.getMsg();
        if (msg.equals(getString(R.string.check_new_version))) {
            mRlCheckUpdate.setVisibility(View.VISIBLE);
        }
    }

    @OnClick({R.id.bark, R.id.rl_check_update, R.id.rl_update_log})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;
            case R.id.rl_update_log:
                startActivity(new Intent(this, UpdateLogActivity.class));
                break;
            case R.id.rl_check_update:
                startActivity(new Intent(GuanYuMeActivity.this, VersionsUpdateActivity.class));
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        EventBus.getDefault().unregister(this);
    }
}
