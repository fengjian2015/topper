package com.bclould.tea.ui.activity;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bclould.tea.Presenter.FileDownloadPresenter;
import com.bclould.tea.R;
import com.bclould.tea.base.BaseActivity;
import com.bclould.tea.model.DownloadInfo;
import com.bclould.tea.utils.ActivityUtil;
import com.bclould.tea.utils.Constants;
import com.bclould.tea.utils.MySharedPreferences;
import com.bclould.tea.utils.UtilTool;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by GA on 2018/7/11.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class VersionsUpdateActivity extends BaseActivity {

    @Bind(R.id.bark)
    ImageView mBark;
    @Bind(R.id.title)
    TextView mTitle;
    @Bind(R.id.rl_title)
    RelativeLayout mRlTitle;
    @Bind(R.id.xx)
    TextView mXx;
    @Bind(R.id.progressBar)
    ProgressBar mProgressBar;
    @Bind(R.id.tv_body)
    TextView mTvBody;
    @Bind(R.id.tv_new_update)
    TextView mTvNewUpdate;
    @Bind(R.id.tv_app_name)
    TextView mTvAppName;
    @Bind(R.id.tv_new_versions_tag)
    TextView mTvNewVersionsTag;
    @Bind(R.id.ll_apk_name)
    LinearLayout mLlApkName;
    @Bind(R.id.btn_download)
    Button mBtnDownload;
    @Bind(R.id.btn_stop)
    Button mBtnStop;
    @Bind(R.id.btn_finish)
    Button mBtnFinish;
    private String mUrl;
    private String mApkName;
    private String mBody;
    private String mVersions_tag;
    private FileDownloadPresenter mFileDownloadPresenter;
    private File mFile;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_versions_update);
        ButterKnife.bind(this);
        mFileDownloadPresenter = FileDownloadPresenter.getInstance(this);
        mFileDownloadPresenter.setOnDownloadCallbackListener(mDownloadCallback);
        initData();
    }

    private void initData() {
        mUrl = MySharedPreferences.getInstance().getString(Constants.NEW_APK_URL);
        mApkName = MySharedPreferences.getInstance().getString(Constants.NEW_APK_NAME);
        mBody = MySharedPreferences.getInstance().getString(Constants.NEW_APK_BODY);
        mVersions_tag = MySharedPreferences.getInstance().getString(Constants.APK_VERSIONS_TAG);
        mTvBody.setText(mBody);
        mTvAppName.setText(mApkName);
        mTvNewVersionsTag.setText(mVersions_tag);
        mProgressBar.setMax(100);
        mFile = new File(Constants.DOWNLOAD + "topperchat_" + MySharedPreferences.getInstance().getString(Constants.APK_VERSIONS_TAG) + ".apk");
        if (mFile.exists()) {
            if (MySharedPreferences.getInstance().getLong(Constants.NEW_APK_KEY) == mFile.length()) {
                mBtnDownload.setVisibility(View.GONE);
                mBtnStop.setVisibility(View.GONE);
                mBtnFinish.setVisibility(View.VISIBLE);
                mProgressBar.setProgress(100);
            } else {
                    double currentSize = Double.parseDouble(mFile.length() + "");
                    double totleSize = Double.parseDouble(MySharedPreferences.getInstance().getLong(Constants.NEW_APK_KEY) + "");
                    int progress = (int) (currentSize / totleSize * 100);
                    mProgressBar.setProgress(progress);
                    mBtnDownload.setVisibility(View.VISIBLE);
                    mBtnStop.setVisibility(View.GONE);
                    mBtnFinish.setVisibility(View.GONE);
            }
        } else {
            mBtnDownload.setVisibility(View.VISIBLE);
            mBtnStop.setVisibility(View.GONE);
            mBtnFinish.setVisibility(View.GONE);
        }
    }

    @OnClick({R.id.bark, R.id.btn_download, R.id.btn_finish, R.id.btn_stop})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;
            case R.id.btn_download:
                download();
                break;
            case R.id.btn_finish:
                UtilTool.install(this, mFile);
                break;
            case R.id.btn_stop:
                if (FileDownloadPresenter.getInstance(this).mTask != null) {
                    FileDownloadPresenter.getInstance(this).mTask.cancel();
                }
                break;
        }
    }

    private void download() {
        mBtnDownload.setVisibility(View.GONE);
        mBtnStop.setVisibility(View.VISIBLE);
        mBtnFinish.setVisibility(View.GONE);
        final File file = new File(Constants.DOWNLOAD + "topperchat_" + MySharedPreferences.getInstance().getString(Constants.APK_VERSIONS_TAG) + ".apk");
        mFileDownloadPresenter.dowbloadFile(Constants.APK_BUCKET_NAME, Constants.NEW_APK_KEY, file);
    }

    FileDownloadPresenter.downloadCallback mDownloadCallback = new FileDownloadPresenter.downloadCallback() {
        @Override
        public void onSuccess(File file) {
            mHandler.sendEmptyMessage(2);
        }

        @Override
        public void onFailure() {
            mHandler.sendEmptyMessage(1);
        }

        @Override
        public void onSuccsetProgressListeneress(long currentSize, long totalSize) {
            if (!mFile.exists()) {
                MySharedPreferences.getInstance().setLong(Constants.NEW_APK_KEY, totalSize);
            }else {
                if(mFile.length() == 0){
                    MySharedPreferences.getInstance().setLong(Constants.NEW_APK_KEY, totalSize);
                }
            }
            Message message = new Message();
            DownloadInfo downloadInfo = new DownloadInfo();
            downloadInfo.setCurrent_size(currentSize);
            downloadInfo.setTotal_size(totalSize);
            message.obj = downloadInfo;
            message.what = 0;
            mHandler.sendMessage(message);
        }
    };

    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    if (ActivityUtil.isActivityOnTop(VersionsUpdateActivity.this)) {
                        if(mBtnStop.getVisibility() != View.VISIBLE){
                            mBtnStop.setVisibility(View.VISIBLE);
                            mBtnDownload.setVisibility(View.GONE);
                            mBtnFinish.setVisibility(View.GONE);
                        }
                        DownloadInfo downloadInfo = (DownloadInfo) msg.obj;
                        int progress = (int) (Double.parseDouble(downloadInfo.getCurrent_size() + MySharedPreferences.getInstance().getLong(Constants.NEW_APK_KEY) + "") / Double.parseDouble(downloadInfo.getTotal_size() + MySharedPreferences.getInstance().getLong(Constants.NEW_APK_KEY) + "") * 100);
                        UtilTool.Log("下載apk", progress + "");
                        UtilTool.Log("下載apk", "當前進度：" + downloadInfo.getCurrent_size() + "-----總進度：" + downloadInfo.getTotal_size());
                        mProgressBar.setProgress(progress);
                    }
                    break;
                case 1:
                    if (ActivityUtil.isActivityOnTop(VersionsUpdateActivity.this)) {
                        mBtnDownload.setVisibility(View.VISIBLE);
                        mBtnStop.setVisibility(View.GONE);
                        mBtnFinish.setVisibility(View.GONE);
                    }
                    break;
                case 2:
                    if (ActivityUtil.isActivityOnTop(VersionsUpdateActivity.this)) {
                        mBtnDownload.setVisibility(View.GONE);
                        mBtnStop.setVisibility(View.GONE);
                        mBtnFinish.setVisibility(View.VISIBLE);
                    }
                    break;
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        mFileDownloadPresenter.removeDownloadCallbackListener(mDownloadCallback);
    }
}
