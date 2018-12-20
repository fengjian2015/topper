package com.bclould.tea.ui.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Base64;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bclould.tea.Presenter.NewsNoticePresenter;
import com.bclould.tea.R;
import com.bclould.tea.base.BaseActivity;
import com.bclould.tea.base.MyApp;
import com.bclould.tea.model.BaseInfo;
import com.bclould.tea.ui.widget.DeleteCacheDialog;
import com.bclould.tea.ui.widget.LoadingProgressDialog;
import com.bclould.tea.utils.AppLanguageUtils;
import com.bclould.tea.utils.Constants;
import com.bclould.tea.utils.MySharedPreferences;
import com.bclould.tea.utils.UtilTool;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.compress.Luban;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.entity.LocalMedia;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.luck.picture.lib.config.PictureMimeType.ofImage;

/**
 * Created by GA on 2018/5/8.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class NewsEditActivity extends BaseActivity {

    @Bind(R.id.progressBar)
    ProgressBar mProgressBar;
    @Bind(R.id.web_view)
    WebView mWebView;
    @Bind(R.id.ll_load_error)
    LinearLayout mLlLoadError;
    private int mId;
    private int mType;
    private String mUrl;
    private LoadingProgressDialog mProgressDialog;
    private String mCoin_name;
    private String mAd_cost;

    private void showDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = LoadingProgressDialog.createDialog(this);
            mProgressDialog.setMessage(getString(R.string.uploading));
        }
        mProgressDialog.show();
    }

    private void hideDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_edit2);
        ButterKnife.bind(this);
        setTitle(getString(R.string.ad_edit),getString(R.string.push));
        MyApp.getInstance().addActivity(this);
        initData();
        initIntent();
        initWebView();
        initView();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(AppLanguageUtils.attachBaseContext(newBase, MySharedPreferences.getInstance().getString(newBase.getString(R.string.language_pref_key))));
    }

    private void initData() {
        NewsNoticePresenter newsNoticePresenter = new NewsNoticePresenter(this);
        newsNoticePresenter.getAdCost(new NewsNoticePresenter.CallBack3() {
            @Override
            public void send(BaseInfo.DataBean data) {
                mCoin_name = data.getCoin_name();
                mAd_cost = data.getAd_cost();
            }
        });

    }

    private void initIntent() {
        Intent intent = getIntent();
        mId = intent.getIntExtra("id", 0);
        mType = intent.getIntExtra("type", 0);
        if (mType == Constants.NEW_DRAFTS_TYPE) {
            mUrl = Constants.BASE_URL + Constants.NEWS_DRAFTS_URL + mId + "/" + UtilTool.getUserId();
        } else {
            mUrl = Constants.BASE_URL + Constants.NEWS_EDIT_WEB_URL;
        }
    }

    @OnClick({R.id.bark, R.id.tv_add})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                showSaveDialog();
                break;
            case R.id.tv_add:
                showServiceChargeDialog();
                break;
        }
    }

    private void initView() {
        mLlLoadError.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mWebView.loadUrl(mUrl);
            }
        });
    }

    private void publish(int isDraft) {
        try {
            JSONObject json = new JSONObject();
            json.put("is_draft", isDraft);
            json.put("user_id", UtilTool.getUserId());
            UtilTool.Log("新聞", json.toString());
            mWebView.evaluateJavascript("javascript:publish('" + json.toString() + "')", new ValueCallback<String>() {
                @Override
                public void onReceiveValue(String value) {
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initWebView() {
        //设置WebView支持JavaScript
        mWebView.getSettings().setJavaScriptEnabled(true);
        if (UtilTool.isNetworkAvailable(NewsEditActivity.this)) {
            mWebView.getSettings().setCacheMode(
                    WebSettings.LOAD_CACHE_ELSE_NETWORK);
        } else {
            mWebView.getSettings().setCacheMode(
                    WebSettings.LOAD_CACHE_ELSE_NETWORK);
        }
        mWebView.addJavascriptInterface(this, "publishNews");
        // 把图片加载放在最后来加载渲染
        mWebView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        // 支持多窗口
        mWebView.getSettings().setSupportMultipleWindows(true);
        // 开启 DOM storage API 功能
        mWebView.getSettings().setDomStorageEnabled(true);
        // 开启 Application Caches 功能
        mWebView.getSettings().setAppCacheEnabled(true);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                mProgressBar.setVisibility(View.GONE);
                if (mProgressBar.getProgress() != 100) {
                    mLlLoadError.setVisibility(View.VISIBLE);
                    mWebView.setVisibility(View.GONE);
                } else {
                    mLlLoadError.setVisibility(View.GONE);
                    mWebView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError
                    error) {
                super.onReceivedError(view, request, error);
                mLlLoadError.setVisibility(View.VISIBLE);
                mWebView.setVisibility(View.GONE);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                mProgressBar.setVisibility(View.VISIBLE);
            }
        });
        mWebView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                mProgressBar.setMax(100);
                mProgressBar.setProgress(progress);
            }
        });
        mWebView.loadUrl(mUrl);
    }

    private void showSaveDialog() {
        final DeleteCacheDialog deleteCacheDialog = new DeleteCacheDialog(R.layout.dialog_delete_cache, this, R.style.dialog);
        deleteCacheDialog.show();
        deleteCacheDialog.setTitle(getString(R.string.whether_save_draft));
        Button cancel = (Button) deleteCacheDialog.findViewById(R.id.btn_cancel);
        cancel.setText(getString(R.string.no_save));
        cancel.setTextColor(getResources().getColor(R.color.red));
        Button confirm = (Button) deleteCacheDialog.findViewById(R.id.btn_confirm);
        confirm.setText(R.string.save);
        confirm.setTextColor(getResources().getColor(R.color.black));
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteCacheDialog.dismiss();
                finish();
            }
        });
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
                publish(1);
                deleteCacheDialog.dismiss();
            }
        });
    }

    private void showServiceChargeDialog() {
        final DeleteCacheDialog deleteCacheDialog = new DeleteCacheDialog(R.layout.dialog_delete_cache, this, R.style.dialog);
        deleteCacheDialog.show();
        deleteCacheDialog.setTitle(getString(R.string.publish_news_hint) + mAd_cost + mCoin_name + getString(R.string.ggf));
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
                showDialog();
                publish(2);
                deleteCacheDialog.dismiss();
            }
        });
    }

    @JavascriptInterface
    public void showToast(final int status, final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                hideDialog();
                if (status == 1) {
                    finish();
                }
                Toast.makeText(NewsEditActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }


    private List<LocalMedia> selectList = new ArrayList<>();

    @JavascriptInterface
    public void selectorImage() {
        PictureSelector.create(this)
                .openGallery(ofImage())//全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()
//                        .theme(R.style.picture_white_style)
                .maxSelectNum(1)// 最大图片选择数量 int
                .imageSpanCount(3)// 每行显示个数 int
                .selectionMode(PictureConfig.SINGLE)// 多选 or 单选 PictureConfig.MULTIPLE or PictureConfig.SINGLE
                .previewImage(true)// 是否可预览图片 true or false
                .previewVideo(true)// 是否可预览视频 true or false
                .enablePreviewAudio(true) // 是否可播放音频 true or false
                .compressGrade(Luban.THIRD_GEAR)// luban压缩档次，默认3档 Luban.THIRD_GEAR、Luban.FIRST_GEAR、Luban.CUSTOM_GEAR
                .isCamera(true)// 是否显示拍照按钮 true or false
                .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
                .sizeMultiplier(0.5f)// glide 加载图片大小 0~1之间 如设置 .glideOverride()无效
                .setOutputCameraPath("/CustomPath")// 自定义拍照保存路径,可不填
                .enableCrop(false)// 是否裁剪 true or false
                .compress(true)// 是否压缩 true or false
                .compressMode(PictureConfig.SYSTEM_COMPRESS_MODE)//系统自带 or 鲁班压缩 PictureConfig.SYSTEM_COMPRESS_MODE or LUBAN_COMPRESS_MODE
                .glideOverride(160, 160)// int glide 加载宽高，越小图片列表越流畅，但会影响列表图片浏览的清晰度
                .withAspectRatio(1, 1)// int 裁剪比例 如16:9 3:2 3:4 1:1 可自定义
                .hideBottomControls(true)// 是否显示uCrop工具栏，默认不显示 true or false
                .isGif(false)// 是否显示gif图片 true or false
                .freeStyleCropEnabled(true)// 裁剪框是否可拖拽 true or false
                .circleDimmedLayer(false)// 是否圆形裁剪 true or false
                .showCropFrame(true)// 是否显示裁剪矩形边框 圆形裁剪时建议设为false   true or false
                .showCropGrid(true)// 是否显示裁剪矩形网格 圆形裁剪时建议设为false    true or false
                .openClickSound(true)// 是否开启点击声音 true or false
                .selectionMedia(selectList)// 是否传入已选图片 List<LocalMedia> list
                .rotateEnabled(true) // 裁剪是否可旋转图片 true or false
                .scaleEnabled(true)// 裁剪是否可放大缩小图片 true or false
                .forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code
    }

    //拿到选择的图片
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                    // 图片选择结果回调
                    try {
                        selectList = PictureSelector.obtainMultipleResult(data);
                        insertImg(selectList.get(0).getCompressPath());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }

    private void insertImg(String compressPath) {
        File file = new File(compressPath);
        final String keyCut = UtilTool.getUserId() + UtilTool.createtFileName() + "compress" + UtilTool.getPostfix2(file.getName());
        final File newFile = new File(Constants.PUBLICDIR + keyCut);
        Bitmap cutImg = BitmapFactory.decodeFile(compressPath);
        UtilTool.comp(cutImg, newFile);
        byte[] bytes = UtilTool.getFileToByte(newFile);
        String base64Str = Base64.encodeToString(bytes, Base64.DEFAULT);
        try {
            JSONObject json = new JSONObject();
            json.put("img", "data:image/png;base64," + base64Str);
            mWebView.evaluateJavascript("javascript:insertImg('" + json.toString() + "')", new ValueCallback<String>() {
                @Override
                public void onReceiveValue(String value) {

                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        showSaveDialog();
    }
}
