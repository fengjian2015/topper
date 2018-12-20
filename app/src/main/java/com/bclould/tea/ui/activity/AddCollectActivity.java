package com.bclould.tea.ui.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bclould.tea.Presenter.CollectPresenter;
import com.bclould.tea.R;
import com.bclould.tea.base.BaseActivity;
import com.bclould.tea.model.TitleIConInfo;
import com.bclould.tea.ui.widget.ClearEditText;
import com.bclould.tea.ui.widget.LoadingProgressDialog;
import com.bclould.tea.utils.ActivityUtil;
import com.bclould.tea.utils.AnimatorTool;
import com.bclould.tea.utils.AppLanguageUtils;
import com.bclould.tea.utils.MessageEvent;
import com.bclould.tea.utils.MySharedPreferences;
import com.bclould.tea.utils.ToastShow;
import com.bclould.tea.utils.UtilTool;

import org.greenrobot.eventbus.EventBus;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by GA on 2018/7/13.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class AddCollectActivity extends BaseActivity {
    @Bind(R.id.bark)
    ImageView mBark;
    @Bind(R.id.et_titles)
    ClearEditText mEtTitles;
    @Bind(R.id.et_url)
    ClearEditText mEtUrl;
    private CollectPresenter mCollectPresenter;
    private String mUrl;
    private LoadingProgressDialog mProgressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_collect);
        ButterKnife.bind(this);
        setTitle(getString(R.string.add_collect),getString(R.string.save));
        mCollectPresenter = new CollectPresenter(this);
        initIntent();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(AppLanguageUtils.attachBaseContext(newBase, MySharedPreferences.getInstance().getString(newBase.getString(R.string.language_pref_key))));
    }

    private void initIntent() {
        mUrl = getIntent().getStringExtra("url");
        if (mUrl != null) {
            mEtUrl.setText(mUrl);
        }
    }

    @OnClick({R.id.bark, R.id.tv_add})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;
            case R.id.tv_add:
                if (checkEdit()) {
                    checkUrl();
                }
                break;
        }
    }

    private boolean checkEdit() {
        if (mEtUrl.getText().toString().trim().isEmpty()) {
            ToastShow.showToast2(this, getString(R.string.toast_url));
            AnimatorTool.getInstance().editTextAnimator(mEtUrl);
            return false;
        }
        return true;
    }

    private void checkUrl() {
        mUrl = mEtUrl.getText().toString();
        if (!UtilTool.checkLinkedExe(mUrl)) {
            mUrl = "http://" + mUrl;
        }
        final String title = mEtTitles.getText().toString();
        if (title.isEmpty()) {
            startCheck("");
        } else {
            startCheck(title);
        }
    }

    private void startCheck(final String title) {
        showDialog();
        new Thread(new Runnable() {
            public void run() {
                try {
                    Document doc = Jsoup.connect(mUrl).get();
                    Message message = new Message();
                    TitleIConInfo titleIConInfo = new TitleIConInfo();
                    if (title.isEmpty()) {
                        titleIConInfo.setTitle(doc.title());
                    } else {
                        titleIConInfo.setTitle(title);
                    }
                    Elements link = doc.head().getElementsByTag("link");
                    for (int i = 0; i < link.size(); i++) {
                        Element element = link.get(i);
                        /*URL url = new URL(element.baseUri());
                        String host = url.getHost();
                        String iconUrl = null;
                        if (element.baseUri().startsWith("https://")) {
                            iconUrl = "https://" + host + "/favicon.ico";
                        }else {
                            iconUrl = "http://" + host + "/favicon.ico";
                        }
                        titleIConInfo.setIconUrl(iconUrl);
                        UtilTool.Log("連接", iconUrl);*/
                        String type = element.attr("type");
                        String rel = element.attr("rel");
                        if ("image/x-icon".equals(type) || "SHORTCUT ICON".equals(rel) || "shortcut icon".equals(rel) || "apple-touch-icon-precomposed".equals(rel)) {
                            if (!element.attr("href").startsWith("http") && !element.attr("href").startsWith("//")) {
                                titleIConInfo.setIconUrl(element.baseUri() + element.attr("href"));
                            } else if (element.attr("href").startsWith("//")) {
                                titleIConInfo.setIconUrl("https:" + element.attr("href"));
                            } else {
                                titleIConInfo.setIconUrl(element.attr("href"));
                            }
                        }
                    }
                    message.obj = titleIConInfo;
                    message.what = 0;
                    mHandler.sendMessage(message);
                } catch (Exception e) {
                    e.printStackTrace();
                    mHandler.sendEmptyMessage(1);
                    EventBus.getDefault().post(new MessageEvent(""));
                }
            }
        }).start();
    }

    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    TitleIConInfo info = (TitleIConInfo) msg.obj;
                    addCollect(info.getTitle(), info.getIconUrl());
                    break;
                case 1:
                    ToastShow.showToast2(AddCollectActivity.this, getString(R.string.check_url_error));
                    break;
            }
        }
    };

    private void showDialog() {
        if (ActivityUtil.isActivityOnTop(this)) {
            if (mProgressDialog == null) {
                mProgressDialog = LoadingProgressDialog.createDialog(this);
                mProgressDialog.setMessage(getString(R.string.loading));
            }
            mProgressDialog.show();
        }
    }

    public void hideDialog() {
        if (ActivityUtil.isActivityOnTop(this)) {
            if (mProgressDialog != null) {
                mProgressDialog.dismiss();
                mProgressDialog = null;
            }
        }
    }

    private void addCollect(String title, String iconUrl) {
        mCollectPresenter.addCollect(title, mUrl, iconUrl, new CollectPresenter.CallBack2() {
            @Override
            public void send() {
                finish();
                EventBus.getDefault().post(new MessageEvent(getString(R.string.add_collect)));
            }
        });
    }
}
