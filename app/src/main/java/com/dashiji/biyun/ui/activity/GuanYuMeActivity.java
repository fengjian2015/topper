package com.dashiji.biyun.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dashiji.biyun.base.MyApp;
import com.dashiji.biyun.R;
import com.dashiji.biyun.base.BaseActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by GA on 2017/9/22.
 */

public class GuanYuMeActivity extends BaseActivity {

    @Bind(R.id.bark)
    ImageView mBark;
    @Bind(R.id.versions)
    TextView mVersions;
    @Bind(R.id.rl_function_introduce)
    RelativeLayout mRlFunctionIntroduce;
    @Bind(R.id.rl_versions_number)
    RelativeLayout mRlVersionsNumber;
    @Bind(R.id.rl_versions_update)
    RelativeLayout mRlVersionsUpdate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guanyu_me);
        ButterKnife.bind(this);
        MyApp.getInstance().addActivity(this);
    }

    @OnClick(R.id.bark)
    public void onViewClicked() {
        finish();
    }
}
