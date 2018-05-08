package com.bclould.tocotalk.ui.activity;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bclould.tocotalk.R;
import com.bclould.tocotalk.utils.Constants;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by GA on 2018/5/8.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class NewsDetailsActivity extends AppCompatActivity {
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
    private int mId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_details);
        ButterKnife.bind(this);
        initIntent();
        initWebView();
    }

    private void initWebView() {
        mWebView.loadUrl(Constants.BASE_URL + Constants.NEWS_WEB_URL + mId);
    }

    private void initIntent() {
        mId = getIntent().getIntExtra("id", 0);
    }

    @OnClick(R.id.bark)
    public void onViewClicked() {
        finish();
    }
}
