package com.bclould.tea.ui.activity;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bclould.tea.R;
import com.bclould.tea.base.BaseActivity;
import com.bclould.tea.base.MyApp;
import com.bclould.tea.utils.AppLanguageUtils;
import com.bclould.tea.utils.Constants;
import com.bclould.tea.utils.MySharedPreferences;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by GA on 2018/5/9.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
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

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_details);
        ButterKnife.bind(this);
        setTitle(getString(R.string.gongao_details));
        MyApp.getInstance().addActivity(this);
        initIntent();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(AppLanguageUtils.attachBaseContext(newBase, MySharedPreferences.getInstance().getString(newBase.getString(R.string.language_pref_key))));
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
