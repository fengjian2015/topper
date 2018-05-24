package com.bclould.tocotalk.ui.activity;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bclould.tocotalk.R;
import com.bclould.tocotalk.base.BaseActivity;
import com.bclould.tocotalk.utils.Constants;
import com.bclould.tocotalk.utils.UtilTool;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by GA on 2018/5/8.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class NewsDetailsActivity extends BaseActivity {
    @Bind(R.id.bark)
    ImageView mBark;
    @Bind(R.id.title_name)
    TextView mTitleName;
    @Bind(R.id.title)
    RelativeLayout mTitle;
    @Bind(R.id.xx)
    TextView mXx;
    @Bind(R.id.web_view)
    WebView mWebView;
    @Bind(R.id.comment_et)
    EditText mCommentEt;
    @Bind(R.id.send)
    TextView mSend;
    @Bind(R.id.rl_edit)
    RelativeLayout mRlEdit;
    @Bind(R.id.progressBar)
    ProgressBar mProgressBar;
    @Bind(R.id.rl_web_view)
    RelativeLayout mRlWebView;
    @Bind(R.id.ll_load_error)
    LinearLayout mLlLoadError;
    private int mId;
    private boolean mLoadError = false;
    private int mType;
    private String mUrl;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_details);
        ButterKnife.bind(this);
        initIntent();
        initWebView();
        initView();
    }

    private void initView() {
        mLlLoadError.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mWebView.loadUrl(Constants.BASE_URL + Constants.NEWS_WEB_URL + mId + "/" + UtilTool.getUserId());
            }
        });
    }

    private void initWebView() {
        //设置WebView支持JavaScript
        mWebView.getSettings().setJavaScriptEnabled(true);
        if (UtilTool.isNetworkAvailable(NewsDetailsActivity.this)) {
            mWebView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        } else {
            mWebView.getSettings().setCacheMode(
                    WebSettings.LOAD_CACHE_ELSE_NETWORK);
        }
        mWebView.addJavascriptInterface(this, "callback");
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
                    mRlWebView.setVisibility(View.GONE);
                } else {
                    mLlLoadError.setVisibility(View.GONE);
                    mRlWebView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                mLlLoadError.setVisibility(View.VISIBLE);
                mRlWebView.setVisibility(View.GONE);
                mLoadError = true;
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

    @JavascriptInterface
    public void showToast(final int status, final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (status == 1) {
                    mCommentEt.setText("");
                }
                Toast.makeText(NewsDetailsActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initIntent() {
        mId = getIntent().getIntExtra("id", 0);
        mType = getIntent().getIntExtra("type", 0);
        UtilTool.Log("新聞id", mId + "");
        UtilTool.Log("新聞type", mType + "");
        if (mType == Constants.GONGGAO_TYPE) {
            mTitleName.setText(getString(R.string.gongao_details));
            mRlEdit.setVisibility(View.GONE);
            mUrl = Constants.BASE_URL + Constants.GONGGAO_WEB_URL + mId;
        } else {
            if(mType == Constants.NEW_MY_TYPE || mType == Constants.NEW_DRAFTS_TYPE){
                mRlEdit.setVisibility(View.GONE);
            }
            mTitleName.setText(getString(R.string.news_details));
            mUrl = Constants.BASE_URL + Constants.NEWS_WEB_URL + mId + "/" + UtilTool.getUserId();
        }
    }


    @OnClick({R.id.bark, R.id.send})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;
            case R.id.send:
                review();
                break;
        }
    }

    private void review() {
        final String content = mCommentEt.getText().toString();
        if (content.isEmpty()) {
            Toast.makeText(this, getString(R.string.toast_content), Toast.LENGTH_SHORT).show();
        } else {
            try {
                JSONObject json = new JSONObject();
                json.put("news_id", mId);
                json.put("id", UtilTool.getUserId());
                json.put("content", content);
                UtilTool.Log("新聞", json.toString());
                mWebView.evaluateJavascript("javascript:review('" + json.toString() + "')", new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String value) {
                        UtilTool.Log("新聞", value);
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mWebView.removeAllViews();
        mWebView.destroy();
    }
}
