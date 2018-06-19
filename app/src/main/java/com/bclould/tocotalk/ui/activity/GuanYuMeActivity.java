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
import com.bclould.tocotalk.model.GitHubInfo;
import com.bclould.tocotalk.network.DownLoadApk;
import com.bclould.tocotalk.network.RetrofitUtil;
import com.bclould.tocotalk.ui.widget.DeleteCacheDialog;
import com.bclould.tocotalk.utils.Constants;
import com.bclould.tocotalk.utils.MessageEvent;
import com.bclould.tocotalk.utils.MySharedPreferences;
import com.bclould.tocotalk.utils.UtilTool;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

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
                String url = MySharedPreferences.getInstance().getString(Constants.NEW_APK_URL);
                String apkName = MySharedPreferences.getInstance().getString(Constants.NEW_APK_NAME);
                String body = MySharedPreferences.getInstance().getString(Constants.NEW_APK_BODY);
                if (!url.isEmpty()) {
                    showDialog(url, apkName, body);
                } else {
                    checkVersion();
                }
                break;
        }
    }

    //检测版本更新
    private void checkVersion() {
        //判断是否开启网络
        if (UtilTool.isNetworkAvailable(this)) {
            RetrofitUtil.getInstance(this)
                    .getServer()
                    .checkVersion(Constants.VERSION_URL)//githua获取版本更新
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                    .subscribe(new Observer<GitHubInfo>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(GitHubInfo baseInfo) {
                            //判断是否需要更新
                            float version = Float.parseFloat(UtilTool.getVersionCode(GuanYuMeActivity.this));
                            String tag_version = "";
                            if (baseInfo.getTag_name().contains("v")) {
                                tag_version = baseInfo.getTag_name().replace("v", "");
                            } else {
                                tag_version = baseInfo.getTag_name();
                            }
                            float tag = Float.parseFloat(tag_version);
                            if (version < tag) {
                                MySharedPreferences.getInstance().setString(Constants.NEW_APK_URL, baseInfo.getAssets().get(0).getBrowser_download_url());
                                MySharedPreferences.getInstance().setString(Constants.NEW_APK_NAME, baseInfo.getName());
                                MySharedPreferences.getInstance().setString(Constants.NEW_APK_BODY, baseInfo.getBody());
                                showDialog(baseInfo.getAssets().get(0).getBrowser_download_url(), baseInfo.getName(), baseInfo.getBody());
                                mTvNewUpdate.setVisibility(View.VISIBLE);
                                EventBus.getDefault().post(new MessageEvent(getString(R.string.check_new_version)));
                            } else {
                                mTvNewUpdate.setVisibility(View.GONE);
                                MySharedPreferences.getInstance().setString(Constants.NEW_APK_URL, "");
                                MySharedPreferences.getInstance().setString(Constants.NEW_APK_NAME, "");
                                MySharedPreferences.getInstance().setString(Constants.NEW_APK_BODY, "");
                                Toast.makeText(GuanYuMeActivity.this, getString(R.string.already_new_version), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            UtilTool.Log("日志", e.getMessage());
                            Toast.makeText(GuanYuMeActivity.this, getString(R.string.toast_network_error), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        } else {
            Toast.makeText(GuanYuMeActivity.this, getString(R.string.toast_network_error), Toast.LENGTH_SHORT).show();
        }
    }

    private void showDialog(final String url, final String apkName, final String body) {
        //显示更新dialog
        final DeleteCacheDialog deleteCacheDialog = new DeleteCacheDialog(R.layout.dialog_delete_cache, this, R.style.dialog);
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
