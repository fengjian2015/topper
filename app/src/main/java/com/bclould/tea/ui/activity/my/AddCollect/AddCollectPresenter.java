package com.bclould.tea.ui.activity.my.AddCollect;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;

import com.bclould.tea.Presenter.CollectPresenter;
import com.bclould.tea.R;
import com.bclould.tea.base.BaseView;
import com.bclould.tea.model.TitleIConInfo;
import com.bclould.tea.ui.widget.LoadingProgressDialog;
import com.bclould.tea.utils.ActivityUtil;
import com.bclould.tea.utils.MessageEvent;
import com.bclould.tea.utils.StringUtils;
import com.bclould.tea.utils.ToastShow;
import com.bclould.tea.utils.UtilTool;

import org.greenrobot.eventbus.EventBus;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Created by fengjian on 2018/12/27.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class AddCollectPresenter implements AddCollectContacts.Presenter{
    private AddCollectContacts.View mView;
    private Activity mActivity;

    private CollectPresenter mCollectPresenter;
    private String mUrl;
    private LoadingProgressDialog mProgressDialog;
    @Override
    public void bindView(BaseView view) {
        mView= (AddCollectContacts.View) view;
    }

    @Override
    public <T extends Context> void start(T context) {
        mActivity= (Activity) context;
        initIntent();
        mCollectPresenter=new CollectPresenter(mActivity);
    }

    @Override
    public void release() {

    }

    private void initIntent() {
        mUrl = mActivity.getIntent().getStringExtra("url");
        if (mUrl != null) {
            mView.setEtUrl(mUrl);
        }
    }

    private boolean checkEdit() {
        if (StringUtils.isEmpty(mView.getEtUrl())) {
            ToastShow.showToast2(mActivity, mActivity.getString(R.string.toast_url));
            mView.animatorEtUrl();
            return false;
        }
        return true;
    }


    private void checkUrl() {
        mUrl =mView.getEtUrl();
        if (!UtilTool.checkLinkedExe(mUrl)) {
            mUrl = "http://" + mUrl;
        }
        final String title = mView.getEtTitles();
        if (title.isEmpty()) {
            startCheck("");
        } else {
            startCheck(title);
        }
    }

    @Override
    public void tvAddOnClick() {
        if(checkEdit()){
            checkUrl();
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
                    ToastShow.showToast2(mActivity, mActivity.getString(R.string.check_url_error));
                    break;
            }
        }
    };


    private void showDialog() {
        if (ActivityUtil.isActivityOnTop(mActivity)) {
            if (mProgressDialog == null) {
                mProgressDialog = LoadingProgressDialog.createDialog(mActivity);
                mProgressDialog.setMessage(mActivity.getString(R.string.loading));
            }
            mProgressDialog.show();
        }
    }

    @Override
    public void hideDialog() {
        if (ActivityUtil.isActivityOnTop(mActivity)) {
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
                mActivity.finish();
                EventBus.getDefault().post(new MessageEvent(mActivity.getString(R.string.add_collect)));
            }
        });
    }
}
