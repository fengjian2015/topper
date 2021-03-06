package com.bclould.tea.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.alibaba.fastjson.JSONObject;
import com.bclould.tea.Presenter.IndividualDetailsPresenter;
import com.bclould.tea.R;
import com.bclould.tea.base.BaseActivity;
import com.bclould.tea.history.DBManager;
import com.bclould.tea.model.H5AuthrizationInfo;
import com.bclould.tea.model.IndividualInfo;
import com.bclould.tea.topperchat.WsConnection;
import com.bclould.tea.ui.widget.AuthorizationDialog;
import com.bclould.tea.utils.ActivityUtil;
import com.bclould.tea.utils.Constants;
import com.bclould.tea.utils.MySharedPreferences;
import com.bclould.tea.utils.SharedPreferencesUtil;
import com.bclould.tea.utils.StringUtils;
import com.bclould.tea.utils.ToastShow;
import com.bclould.tea.utils.UtilTool;

import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HTMLActivity extends BaseActivity {

    @Bind(R.id.progressBar)
    ProgressBar mProgressBar;
    @Bind(R.id.web_view)
    WebView mWebView;
    @Bind(R.id.ll_load_error)
    LinearLayout mLlLoadError;
    private String html5Url;
    private DBManager mMgr;

    private boolean isError=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_html);
        ButterKnife.bind(this);
        setHtmlTitle("");
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

    @Override
    public void onResume() {
        super.onResume();
        MySharedPreferences.getInstance().setString(SharedPreferencesUtil.WEB_LOGIN, "");
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
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                UtilTool.Log("fengjian", url);
                isError=false;
                if (url.contains("TopperChatOauth")) {
                    topperChatOauth(url);
                    return true;
                } else if (url.contains("TopperChatOppenId")) {
                    topperChatOppenId(url);
                    return true;
                }else if(url.contains("TopperChatShopIM")){
                    TopperChatShopIM(url);
                    return true;
                }else if(url.contains("wpa.qq.com")){
                    goQQ(url);
                    return true;
                }
                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (mProgressBar == null) {
                    return;
                }
                if(isError){
                    mProgressBar.setVisibility(View.GONE);
                }else {
                    mWebView.setVisibility(View.VISIBLE);
                    mLlLoadError.setVisibility(View.GONE);
                    mProgressBar.setVisibility(View.GONE);
                    if (mWebView.canGoBack()) {
                        mIvFinish.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                super.onReceivedHttpError(view, request, errorResponse);
                if (mLlLoadError == null) return;
                isError=true;
                mLlLoadError.setVisibility(View.VISIBLE);
                mWebView.setVisibility(View.GONE);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    return;
                }
                if (mLlLoadError == null) return;
                isError=true;
                mLlLoadError.setVisibility(View.VISIBLE);
                mWebView.setVisibility(View.GONE);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                if (mLlLoadError == null) return;
                isError=true;
                mLlLoadError.setVisibility(View.VISIBLE);
                mWebView.setVisibility(View.GONE);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                if (mProgressBar == null) {
                    return;
                }
                mProgressBar.setVisibility(View.VISIBLE);
            }


        });
        mWebView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                if (mProgressBar == null) {
                    return;
                }
                mProgressBar.setMax(100);
                mProgressBar.setProgress(progress);
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                if (!StringUtils.isEmpty(title) && mTvTitleTop != null) {
//                    if (!(Tools.checkHttp(title) || Tools.checkLinkedExe(title))) {
                    mTvTitleTop.setText(title);
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

    /**
     * 跳转到QQ聊天
     * @param url
     */
    private void goQQ(String url){
        try {
            String content = url.substring(url.indexOf("msgrd?") + "msgrd?".length(), url.length());
            HashMap hashMap = UtilTool.getCutting(content);
            if(UtilTool.isQQClientAvailable(this)){
                final String qqUrl = "mqqwpa://im/chat?chat_type=wpa&uin="+hashMap.get("uin")+"&version=1";
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(qqUrl)));
            }else{
                //请安装QQ客户端
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 网页在线聊天跳转到聊天界面
     * @param url
     */
    private void TopperChatShopIM(String url){
        try {
            String content = url.substring(url.indexOf("TopperChatShopIM?") + "TopperChatShopIM?".length(), url.length());
            HashMap hashMap = UtilTool.getCutting(content);
            String topperid=hashMap.get("toco_id")+"";
            new IndividualDetailsPresenter(this).getIndividual(topperid, true, new IndividualDetailsPresenter.CallBack() {
                @Override
                public void send(IndividualInfo.DataBean data) {
                    Intent intent = new Intent(HTMLActivity.this, ConversationActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("name", data.getName());
                    bundle.putString("user", data.getToco_id());
                    intent.putExtras(bundle);
                    startActivity(intent);
                }

                @Override
                public void error() {

                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 授权 http://www.bclould.com:8195/oauth/TopperChatOauth?avatar=w2&name=sdfsdf&text=sdfdsf
     *
     * @param url
     */
    private void topperChatOppenId(String url) {
        try {
            H5AuthrizationInfo h5AuthrizationInfo = new H5AuthrizationInfo();
            h5AuthrizationInfo.setOpenid(UtilTool.getTocoId());
//            h5AuthrizationInfo.setAvatar(UtilTool.getImageUrl(mMgr, UtilTool.getTocoId()));
            h5AuthrizationInfo.setOpenid(UtilTool.getTocoId());
            h5AuthrizationInfo.setUser_name(UtilTool.getUser());
            h5AuthrizationInfo.setEmail(UtilTool.getEmail());
            UtilTool.Log("fengjian", JSONObject.toJSONString(h5AuthrizationInfo));
            mWebView.loadUrl("javascript:show2('" + JSONObject.toJSONString(h5AuthrizationInfo) + " ');");
        } catch (Exception e) {
            e.printStackTrace();
            ToastShow.showToast2(HTMLActivity.this, getString(R.string.error));
        }
    }


    /**
     * 授权 http://www.bclould.com:8195/oauth/TopperChatOauth?avatar=w2&name=sdfsdf&text=sdfdsf
     *
     * @param url
     */
    private void topperChatOauth(String url) {
        try {
            if (mMgr == null) {
                mMgr = new DBManager(this);
            }
            if (WsConnection.getInstance().getOutConnection()) {
                MySharedPreferences.getInstance().setString(SharedPreferencesUtil.WEB_LOGIN, Constants.WEB_MALL + "tmpl/member/login.html");
                startActivity(new Intent(HTMLActivity.this, InitialActivity.class));
                return;
            }
            String content = url.substring(url.indexOf("TopperChatOauth?") + "TopperChatOauth?".length(), url.length());
            HashMap hashMap = UtilTool.getCutting(content);
            if (!ActivityUtil.isActivityOnTop(this)) return;
            AuthorizationDialog dialog = new AuthorizationDialog(this);
            dialog.show();
            dialog.setOnClickListener(new AuthorizationDialog.OnClickListener() {
                @Override
                public void onClick() {
                    H5AuthrizationInfo h5AuthrizationInfo = new H5AuthrizationInfo();
                    h5AuthrizationInfo.setAvatar(UtilTool.getImageUrl(mMgr, UtilTool.getTocoId()));
                    h5AuthrizationInfo.setOpenid(UtilTool.getTocoId());
                    h5AuthrizationInfo.setUser_name(UtilTool.getUser());
                    h5AuthrizationInfo.setEmail(UtilTool.getEmail());
                    UtilTool.Log("fengjian", JSONObject.toJSONString(h5AuthrizationInfo));
                    mWebView.loadUrl("javascript:show1('" + JSONObject.toJSONString(h5AuthrizationInfo) + " ');");
                }

                @Override
                public void onCancel() {

                }
            });
            dialog.setIvImage(hashMap.get("avatar") + "");
            dialog.setTvTitle(hashMap.get("name") + "");
            dialog.setTvContent(hashMap.get("text") + "");
        } catch (Exception e) {
            e.printStackTrace();
            ToastShow.showToast2(HTMLActivity.this, getString(R.string.error));
        }
    }


    @OnClick({R.id.bark, R.id.iv_finish, R.id.ll_load_error})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                goBack();
                break;
            case R.id.iv_finish:
                finish();
                break;
            case R.id.ll_load_error:
                againLoad();
                break;
        }
    }

    private void againLoad() {
        isError=false;
        mWebView.reload();
    }

    private void goBack() {
        if (mWebView.canGoBack()) {
            mIvFinish.setVisibility(View.VISIBLE);
            mWebView.goBack();
            mWebView.setVisibility(View.VISIBLE);
            mLlLoadError.setVisibility(View.GONE);
            isError=false;
        } else {
            this.finish();
        }
    }
}
