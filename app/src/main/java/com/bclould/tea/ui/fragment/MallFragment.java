package com.bclould.tea.ui.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.bclould.tea.R;
import com.bclould.tea.history.DBManager;
import com.bclould.tea.model.H5AuthrizationInfo;
import com.bclould.tea.topperchat.WsConnection;
import com.bclould.tea.ui.activity.InitialActivity;
import com.bclould.tea.ui.widget.AuthorizationDialog;
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


/**
 * Created by GA on 2017/9/19.
 */

public class MallFragment extends Fragment {

    public static MallFragment instance = null;
    @Bind(R.id.progressBar)
    ProgressBar mProgressBar;
    @Bind(R.id.web_view)
    WebView mWebView;
    @Bind(R.id.tv_title)
    TextView mTvTitle;
    @Bind(R.id.ll_error)
    LinearLayout mLlError;
    @Bind(R.id.bark)
    ImageView mBark;

    private String html5Url;
    private DBManager mMgr;

    public static MallFragment getInstance() {
        if (instance == null) {
            instance = new MallFragment();
        }
        return instance;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_mall, container, false);
        ButterKnife.bind(this, view);
        init();
        return view;
    }

    private void init() {
        mProgressBar.setMax(100);
        html5Url = Constants.WEB_MALL;
        if (!UtilTool.checkLinkedExe(html5Url)) {
            html5Url = "http://" + html5Url;
        }
        initWebView();
    }

    private void initWebView() {
        //设置WebView支持JavaScript
        mWebView.getSettings().setJavaScriptEnabled(true);
        if (UtilTool.isNetworkAvailable(getActivity())) {
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
                if (url.contains("TopperChatOauth")) {
                    topperChatOauth(url);
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
                mProgressBar.setVisibility(View.GONE);
                mWebView.setVisibility(View.VISIBLE);
                mLlError.setVisibility(View.GONE);
                if (mWebView.canGoBack()) {
                    mBark.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError
                    error) {
                super.onReceivedError(view, request, error);
                if (mProgressBar == null) {
                    return;
                }
                mProgressBar.setVisibility(View.GONE);
                mWebView.setVisibility(View.GONE);
                mLlError.setVisibility(View.VISIBLE);
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
                if (!StringUtils.isEmpty(title) && mTvTitle != null) {
                    mTvTitle.setText(title);
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
     * 授权 http://www.bclould.com:8195/oauth/TopperChatOauth?avatar=w2&name=sdfsdf&text=sdfdsf
     *
     * @param url
     */
    private void topperChatOauth(String url) {
        try {
            if (mMgr == null) {
                mMgr = new DBManager(getContext());
            }
            if (WsConnection.getInstance().getOutConnection()) {
                MySharedPreferences.getInstance().setString(SharedPreferencesUtil.WEB_LOGIN,Constants.WEB_MALL+"tmpl/member/login.html");
                startActivity(new Intent(getActivity(), InitialActivity.class));
                return;
            }
            String content = url.substring(url.indexOf("TopperChatOauth?") + "TopperChatOauth?".length(), url.length());
            HashMap hashMap = UtilTool.getCutting(content);

            AuthorizationDialog dialog = new AuthorizationDialog(getActivity());
            dialog.show();
            dialog.setOnClickListener(new AuthorizationDialog.OnClickListener() {
                @Override
                public void onClick() {
                    H5AuthrizationInfo h5AuthrizationInfo = new H5AuthrizationInfo();
                    h5AuthrizationInfo.setAvatar(UtilTool.getImageUrl(mMgr,UtilTool.getTocoId()));
                    h5AuthrizationInfo.setOpenid(UtilTool.getTocoId());
                    h5AuthrizationInfo.setUser_name(UtilTool.getUser());
                    h5AuthrizationInfo.setEmail(UtilTool.getEmail());
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
            ToastShow.showToast2(getActivity(), getString(R.string.error));
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick({R.id.ll_error,R.id.bark})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_error:
                mWebView.loadUrl(html5Url);
                break;
            case R.id.bark:
                goBack();
                break;
        }
    }

    private void goBack() {
        if (mWebView.canGoBack()) {
            mBark.setVisibility(View.VISIBLE);
            mWebView.goBack();
        }
    }

}
