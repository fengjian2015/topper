package com.bclould.tocotalk.ui.activity;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bclould.tocotalk.R;
import com.bclould.tocotalk.base.BaseActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by GA on 2018/4/9.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class QuestionDetailsActivity extends BaseActivity {
    @Bind(R.id.web_view)
    WebView mWebView;
    @Bind(R.id.bark)
    ImageView mBark;
    @Bind(R.id.title_name)
    TextView mTitleName;
    @Bind(R.id.title)
    RelativeLayout mTitle;
    @Bind(R.id.xx)
    TextView mXx;
    private String mId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_details);
        ButterKnife.bind(this);
        initIntent();
        initWebView();
    }

    private void initWebView() {
        mWebView.loadUrl("https://www.bclould.com:8112/api/question/" + mId);
    }

    private void initIntent() {
        mId = getIntent().getStringExtra("id");
    }

    @OnClick(R.id.bark)
    public void onViewClicked() {
        finish();
    }
}
