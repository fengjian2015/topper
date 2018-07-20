package com.bclould.tea.ui.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.text.Editable;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bclould.tea.R;
import com.bclould.tea.base.BaseActivity;
import com.bclould.tea.base.MyApp;
import com.bclould.tea.ui.widget.VirtualKeyboardView;
import com.bclould.tea.utils.StringUtils;
import com.bclould.tea.utils.UtilTool;
import com.maning.pswedittextlibrary.MNPasswordEditText;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.bclould.tea.R.style.BottomDialog;

@RequiresApi(api = Build.VERSION_CODES.N)
public class HTMLActivity extends BaseActivity {

    @Bind(R.id.bark)
    ImageView bark;
    @Bind(R.id.tv_title)
    TextView tvTitle;
    @Bind(R.id.progressBar)
    ProgressBar mProgressBar;
    @Bind(R.id.web_view)
    WebView mWebView;
    @Bind(R.id.iv_finish)
    ImageView mIvFinish;
    private String html5Url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_html);
        ButterKnife.bind(this);
        MyApp.getInstance().app().addActivity(this);
        init();
    }

    private void init() {
        mProgressBar.setMax(100);
        html5Url = getIntent().getStringExtra("html5Url");
        if (!UtilTool.checkLinkedExe(html5Url)) {
            html5Url = "http://" + html5Url;
        }
        initWebView();
    }

    private void initWebView() {
        //设置WebView支持JavaScript
        mWebView.getSettings().setJavaScriptEnabled(true);
        if (UtilTool.isNetworkAvailable(HTMLActivity.this)) {
            mWebView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        } else {
            mWebView.getSettings().setCacheMode(
                    WebSettings.LOAD_CACHE_ELSE_NETWORK);
        }
        // 把图片加载放在最后来加载渲染
        mWebView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        mWebView.addJavascriptInterface(new payview(),"callback");
        // 支持多窗口
        mWebView.getSettings().setSupportMultipleWindows(true);
        // 开启 DOM storage API 功能
        mWebView.getSettings().setDomStorageEnabled(true);
        // 开启 Application Caches 功能
        mWebView.getSettings().setAppCacheEnabled(true);
        mWebView.getSettings().setBlockNetworkImage(false);
        mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            mWebView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                mProgressBar.setVisibility(View.GONE);
                if (mWebView.canGoBack()) {
                    mIvFinish.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError
                    error) {
                super.onReceivedError(view, request, error);
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

            @Override
            public void onReceivedTitle(WebView view, String title) {
                if (!StringUtils.isEmpty(title)) {
//                    if (!(Tools.checkHttp(title) || Tools.checkLinkedExe(title))) {
                    tvTitle.setText(title);
//                    }
                }
                super.onReceivedTitle(view, title);
                if (view == null) {
                    return;
                }
            }

        });
        mWebView.loadUrl(html5Url);
    }

    public final class payview {
        @JavascriptInterface
        public void showPay(final int status, final String message) {
            UtilTool.Log("fengjian","彈出成功");
            HTMLActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                }
            });
        }
    }

    private void tradeSuccessfully() {
        HTMLActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    mWebView.evaluateJavascript("javascript:payCallBack()", new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String value) {
                            UtilTool.Log("fengjian","回調成功" );
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    @OnClick({R.id.bark,R.id.iv_finish})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                goBack();
                break;
            case R.id.iv_finish:
                finish();
                break;
        }
    }

    private void goBack(){
        if (mWebView.canGoBack()) {
            mIvFinish.setVisibility(View.VISIBLE);
            mWebView.goBack();
        } else {
            this.finish();
        }
    }
}
