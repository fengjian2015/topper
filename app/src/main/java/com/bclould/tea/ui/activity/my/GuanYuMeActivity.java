package com.bclould.tea.ui.activity.my;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bclould.tea.R;
import com.bclould.tea.base.BaseActivity;
import com.bclould.tea.ui.activity.HTMLActivity;
import com.bclould.tea.ui.activity.UpdateLogActivity;
import com.bclould.tea.ui.activity.VersionsUpdateActivity;
import com.bclould.tea.utils.Constants;
import com.bclould.tea.utils.UtilTool;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by GA on 2017/9/22.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class GuanYuMeActivity extends BaseActivity {

    @Bind(R.id.bark)
    ImageView mBark;
    @Bind(R.id.tv_version)
    TextView mTvVersion;
    @Bind(R.id.tv_url)
    TextView mTvUrl;
    @Bind(R.id.tv3)
    TextView mTv3;
    @Bind(R.id.btn_check_update)
    Button mBtnCheckUpdate;
    @Bind(R.id.tv_log)
    TextView mTvLog;
    @Bind(R.id.tv_email)
    TextView mTvEmail;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guanyu_me);
        ButterKnife.bind(this);
        setTitle(getString(R.string.guanyu_me));
        init();
    }

    private void init() {
        String versionCode = UtilTool.getVersionCode(this);
        mTvVersion.setText(getString(R.string.version) + " v " + versionCode);
        mTvUrl.setText(getString(R.string.guanwang) + " " + Constants.OFFICIAL_WEBSITE);
        mTvUrl.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);//下划线
        mTvLog.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);//下划线
        mTvEmail.setText(Constants.CUSTOMER_SERVICE_EMAIL);
    }
    @OnClick({R.id.bark, R.id.tv_url, R.id.btn_check_update, R.id.tv_log})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;
            case R.id.tv_url:
                Intent intent = new Intent(this, HTMLActivity.class);
                intent.putExtra("html5Url", Constants.OFFICIAL_WEBSITE);
                startActivity(intent);
                break;
            case R.id.btn_check_update:
                startActivity(new Intent(GuanYuMeActivity.this, VersionsUpdateActivity.class));
                break;
            case R.id.tv_log:
                startActivity(new Intent(this, UpdateLogActivity.class));
                break;
        }
    }
}
