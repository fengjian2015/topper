package com.bclould.tocotalk.ui.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bclould.tocotalk.R;
import com.bclould.tocotalk.base.BaseActivity;
import com.bclould.tocotalk.base.MyApp;
import com.bclould.tocotalk.network.DownLoadApk;
import com.bclould.tocotalk.ui.widget.DeleteCacheDialog;
import com.bclould.tocotalk.utils.Constants;
import com.bclould.tocotalk.utils.MySharedPreferences;
import com.bclould.tocotalk.utils.UtilTool;

import java.util.List;

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
    @Bind(R.id.tv)
    TextView mTv;
    @Bind(R.id.tv_new_update)
    TextView mTvNewUpdate;
    @Bind(R.id.rl_new)
    RelativeLayout mRlNew;
    @Bind(R.id.tv_version)
    TextView mTvVersion;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guanyu_me);
        ButterKnife.bind(this);
        if (!MySharedPreferences.getInstance().getString(Constants.NEW_APK_URL).isEmpty()) {
            mTvNewUpdate.setVisibility(View.VISIBLE);
        } else {
            mTvNewUpdate.setVisibility(View.GONE);
        }
        String versionCode = UtilTool.getVersionCode(this);
        mTvVersion.setText(getString(R.string.app_name) + " V" + versionCode);
        MyApp.getInstance().addActivity(this);
    }

    @OnClick({R.id.bark, R.id.rl_new})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;
            case R.id.rl_new:
                String url = MySharedPreferences.getInstance().getString(Constants.NEW_APK_URL);
                String apkName = MySharedPreferences.getInstance().getString(Constants.NEW_APK_NAME);
                String body = MySharedPreferences.getInstance().getString(Constants.NEW_APK_BODY);
                if (!url.isEmpty()) {
                    showDialog(url, apkName, body);
                }else {
                    Toast.makeText(this, getString(R.string.already_new_version), Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void showDialog(final String url, final String apkName, final String body) {
        //显示更新dialog
        final DeleteCacheDialog deleteCacheDialog = new DeleteCacheDialog(R.layout.dialog_delete_cache, this);
        deleteCacheDialog.show();
        deleteCacheDialog.setTitle(getString(R.string.check_new_version));
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

                if (!canDownloadState()) {
                    showDownloadSetting();
                    return;
                }
                UtilTool.Log("版本更新", url);
                DownLoadApk.download(GuanYuMeActivity.this, url, body, apkName);
                deleteCacheDialog.dismiss();
            }
        });
    }


    //获取intent意图
    private boolean intentAvailable(Intent intent) {
        PackageManager packageManager = getPackageManager();
        List list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    //更新完成弹出安装
    private void showDownloadSetting() {
        String packageName = "com.android.providers.downloads";
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + packageName));
        if (intentAvailable(intent)) {
            startActivity(intent);
        }
    }

    //下载状态
    private boolean canDownloadState() {
        try {
            int state = this.getPackageManager().getApplicationEnabledSetting("com.android.providers.downloads");

            if (state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED
                    || state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_USER
                    || state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_UNTIL_USED) {
                return false;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
