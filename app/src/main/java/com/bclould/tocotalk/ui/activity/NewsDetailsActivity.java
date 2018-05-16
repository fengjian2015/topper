package com.bclould.tocotalk.ui.activity;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ImageView;
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
    private int mId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_details);
        ButterKnife.bind(this);
        initIntent();
        initWebView();
    }

    @SuppressLint("JavascriptInterface")
    private void initWebView() {
        //设置WebView支持JavaScript
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.addJavascriptInterface(this, "callback");
        mWebView.loadUrl(Constants.BASE_URL + Constants.NEWS_WEB_URL + mId + "/" + UtilTool.getUserId());
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
                mWebView.loadUrl("javascript:review('" + json.toString() + "')");
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
}
