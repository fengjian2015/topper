package com.bclould.tea.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.bclould.tea.R;
import com.bclould.tea.base.BaseActivity;
import com.bclould.tea.utils.Constants;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by GA on 2018/5/9.
 */

public class GonggaoDetailActivity extends BaseActivity {

    @Bind(R.id.progressBar)
    ProgressBar mProgressBar;
    @Bind(R.id.web_view)
    WebView mWebView;
    @Bind(R.id.comment_et)
    EditText mCommentEt;
    @Bind(R.id.send)
    TextView mSend;
    @Bind(R.id.rl_edit)
    RelativeLayout mRlEdit;
    @Bind(R.id.rl_web_view)
    RelativeLayout mRlWebView;
    @Bind(R.id.ll_load_error)
    LinearLayout mLlLoadError;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_details);
        ButterKnife.bind(this);
        setTitle(getString(R.string.gongao_details));
        initIntent();
    }

    private void initIntent() {
        int id = getIntent().getIntExtra("id", 0);
        mWebView.loadUrl(Constants.BASE_URL + Constants.GONGGAO_WEB_URL + id);
    }

    @OnClick(R.id.bark)
    public void onViewClicked() {
        finish();
    }
}
