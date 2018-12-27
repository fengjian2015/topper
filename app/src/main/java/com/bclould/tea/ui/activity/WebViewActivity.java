package com.bclould.tea.ui.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bclould.tea.R;
import com.bclould.tea.base.BaseActivity;
import com.bclould.tea.base.MyApp;
import com.bclould.tea.utils.AppLanguageUtils;
import com.bclould.tea.utils.MySharedPreferences;
import com.bclould.tea.utils.UtilTool;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by GA on 2018/5/8.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class WebViewActivity extends BaseActivity {

    @Bind(R.id.progressBar)
    ProgressBar mProgressBar;
    @Bind(R.id.web_view)
    WebView mWebView;
    @Bind(R.id.ll_load_error)
    LinearLayout mLlLoadError;
    private String mUrl;
    private String mTitle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        ButterKnife.bind(this);
        setTitle("");
        initIntent();
        initWebView();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(AppLanguageUtils.attachBaseContext(newBase, MySharedPreferences.getInstance().getString(newBase.getString(R.string.language_pref_key))));
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initWebView() {
        //设置WebView支持JavaScript
        mWebView.getSettings().setJavaScriptEnabled(true);
        if (UtilTool.isNetworkAvailable(WebViewActivity.this)) {
            if (mTitle.equals(getString(R.string.hash_details))) {
                mWebView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
            } else {
                mWebView.getSettings().setCacheMode(
                        WebSettings.LOAD_CACHE_ELSE_NETWORK);
            }
        } else {
            mWebView.getSettings().setCacheMode(
                    WebSettings.LOAD_CACHE_ELSE_NETWORK);
        }

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
                if(isFinishing()){
                    return;
                }
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
                if(isFinishing()){
                    return;
                }
                mLlLoadError.setVisibility(View.VISIBLE);
                mWebView.setVisibility(View.GONE);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                if(isFinishing()){
                    return;
                }
                mProgressBar.setVisibility(View.VISIBLE);
            }
        });
        mWebView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                if(mProgressBar==null) return;
                mProgressBar.setMax(100);
                mProgressBar.setProgress(progress);
            }
        });
        mWebView.loadUrl(mUrl);
    }

    private void initIntent() {
        mUrl = getIntent().getStringExtra("url");
        mTitle = getIntent().getStringExtra("title");
        mTvTitleTop.setText(mTitle);
    }

    @OnClick({R.id.bark, R.id.ll_load_error})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;
            case R.id.ll_load_error:
                mWebView.loadUrl(mUrl);
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {//监听返回键
        if (keyCode == event.KEYCODE_BACK) {
            if (mWebView.canGoBack()) {
                mWebView.goBack();
            } else {
                finish();
            }
        }
        return true;
    }
}
