package com.bclould.tea.ui.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bclould.tea.Presenter.FileUploadingPresenter;
import com.bclould.tea.R;
import com.bclould.tea.base.BaseActivity;
import com.bclould.tea.base.MyApp;
import com.bclould.tea.utils.AppLanguageUtils;
import com.bclould.tea.utils.MySharedPreferences;
import com.bclould.tea.utils.UtilTool;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by GA on 2018/7/18.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class FileUploadingActivity extends BaseActivity {
    @Bind(R.id.iv_img)
    ImageView mIvImg;
    @Bind(R.id.tv_count)
    TextView mTvCount;
    @Bind(R.id.tv_title)
    TextView mTvTitle;
    @Bind(R.id.progressBar)
    ProgressBar mProgressBar;
    @Bind(R.id.tv_progress)
    TextView mTvProgress;
    private FileUploadingPresenter mFileUploadingPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_uploading);
        ButterKnife.bind(this);
        setTitle(getString(R.string.dynamic_uploading));
        mFileUploadingPresenter = FileUploadingPresenter.getInstance(MyApp.getInstance().app());
        mFileUploadingPresenter.setOnUploadingCallbackListener(mUploadingCallback);
        mProgressBar.setMax(100);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(AppLanguageUtils.attachBaseContext(newBase, MySharedPreferences.getInstance().getString(newBase.getString(R.string.language_pref_key))));
    }

    FileUploadingPresenter.UploadingCallback mUploadingCallback = new FileUploadingPresenter.UploadingCallback() {


        @Override
        public void onSuccess() {
        }

        @Override
        public void onStart(String text, List<String> pathList, boolean type) {
            if (text != null && !text.isEmpty()) {
                mTvTitle.setText(text);
            }
            if (pathList.size() != 0) {
                if (type) {
                    Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(pathList.get(0)
                            , MediaStore.Video.Thumbnails.MINI_KIND);
                    mIvImg.setImageBitmap(bitmap);
                } else {
                    mIvImg.setImageBitmap(BitmapFactory.decodeFile(pathList.get(0)));
                }
                mTvCount.setText(pathList.size() + "");
            }
        }

        @Override
        public void onSuccsetProgressListeneress(long currentSize, long totalSize) {
            final int progress = (int) (Double.parseDouble(currentSize + "") / Double.parseDouble(totalSize + "") * 100);
            UtilTool.Log("動態", "當前進度：" + currentSize + "===總進度：" + totalSize);
            FileUploadingActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mProgressBar.setProgress(progress);
                    mTvProgress.setText(progress + "%");
                }
            });
        }

        @Override
        public void onFailure() {
            finish();
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        mFileUploadingPresenter.removeDownloadCallbackListener(mUploadingCallback);
    }

    @OnClick(R.id.bark)
    public void onViewClicked() {
        finish();
    }
}
