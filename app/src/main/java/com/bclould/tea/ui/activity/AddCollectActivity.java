package com.bclould.tea.ui.activity;

import android.annotation.SuppressLint;
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
import com.bclould.tea.ui.widget.ClearEditText;
import com.bclould.tea.utils.AnimatorTool;
import com.bclould.tea.utils.MessageEvent;
import com.bclould.tea.utils.ToastShow;
import com.bclould.tea.utils.UtilTool;

import org.greenrobot.eventbus.EventBus;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

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
    @Bind(R.id.tv_add)
    TextView mTvAdd;
    @Bind(R.id.et_titles)
    ClearEditText mEtTitles;
    @Bind(R.id.et_url)
    ClearEditText mEtUrl;
    private CollectPresenter mCollectPresenter;
    private String mUrl;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_collect);
        ButterKnife.bind(this);
        mCollectPresenter = new CollectPresenter(this);
        initIntent();
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
        String title = mEtTitles.getText().toString();
        if (title.isEmpty()) {
            new Thread(new Runnable() {
                public void run() {
                    try {
                        Document doc = Jsoup.connect(mUrl).get();
                        if (!doc.title().isEmpty()) {
                            Message message = new Message();
                            message.obj = doc.title();
                            mHandler.sendMessage(message);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } else {
            addCollect(title);
        }
    }

    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String title = (String) msg.obj;
            addCollect(title);
        }
    };

    private void addCollect(String title) {
        mCollectPresenter.addCollect(title, mUrl, new CollectPresenter.CallBack2() {
            @Override
            public void send() {
                finish();
                EventBus.getDefault().post(new MessageEvent(getString(R.string.add_collect)));
            }
        });
    }
}
