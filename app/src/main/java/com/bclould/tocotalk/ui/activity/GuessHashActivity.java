package com.bclould.tocotalk.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bclould.tocotalk.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by GA on 2018/5/8.
 */

public class GuessHashActivity extends AppCompatActivity {
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guess_hash);
        ButterKnife.bind(this);
        initIntent();
    }

    private void initIntent() {
        String url = getIntent().getStringExtra("url");
        mWebView.loadUrl(url);
    }

    @OnClick(R.id.bark)
    public void onViewClicked() {
        finish();
    }
}
