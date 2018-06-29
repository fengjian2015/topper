package com.bclould.tea.Presenter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.widget.Toast;

import com.bclould.tea.R;
import com.bclould.tea.model.BaseInfo;
import com.bclould.tea.model.GonggaoListInfo;
import com.bclould.tea.model.NewsListInfo;
import com.bclould.tea.network.RetrofitUtil;
import com.bclould.tea.utils.ActivityUtil;
import com.bclould.tea.utils.Constants;
import com.bclould.tea.utils.MessageEvent;
import com.bclould.tea.utils.MySharedPreferences;
import com.bclould.tea.utils.UtilTool;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by GA on 2018/5/8.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class NewsNoticePresenter {

    private static final String NEWS_JSON = UtilTool.getTocoId() + "news_json";
    private final Context mContext;

    public NewsNoticePresenter(Context context) {
        mContext = context;
    }

    public void getNewsList(final int page, int pageSize, final CallBack callBack) {

        RetrofitUtil.getInstance(mContext)
                .getServer()
                .getNewsList(UtilTool.getToken(), page, pageSize)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                .subscribe(new Observer<NewsListInfo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(NewsListInfo newsListInfo) {
                        if (newsListInfo.getStatus() == 1) {
                            if (page == 1) {
                                Gson gson = new Gson();
                                UtilTool.Log("動態", gson.toJson(newsListInfo));
                                MySharedPreferences.getInstance().setString(NEWS_JSON, gson.toJson(newsListInfo));
                            }
                            callBack.send(newsListInfo.getLists(), newsListInfo.getTop());
                        }else{
                            callBack.finishRefresh();
                        }
                    }
                    @Override
                    public void onError(Throwable e) {
                        if (page == 1) {
                            SharedPreferences sp = MySharedPreferences.getInstance().getSp();
                            if (sp.contains(NEWS_JSON)) {
                                Gson gson = new Gson();
                                NewsListInfo newsListInfo = gson.fromJson(MySharedPreferences.getInstance().getString(NEWS_JSON), NewsListInfo.class);
                                callBack.send(newsListInfo.getLists(), newsListInfo.getTop());
                            } else {
                                callBack.error();
                            }
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void getMyNewsList(String filtrate, int page, int pageSize, final CallBack2 callBack2) {
        RetrofitUtil.getInstance(mContext)
                .getServer()
                .myNewsList(UtilTool.getToken(), page, pageSize, Integer.parseInt(filtrate))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                .subscribe(new Observer<GonggaoListInfo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(GonggaoListInfo gonggaoListInfo) {
                        if (gonggaoListInfo.getStatus() == 1) {
                            callBack2.send(gonggaoListInfo.getData());
                        }else{
                            callBack2.finishRefresh();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        callBack2.error();
                        UtilTool.Log("新聞", e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void getBrowseRecord(int page, int pageSize, final CallBack2 callBack2) {
        RetrofitUtil.getInstance(mContext)
                .getServer()
                .NewsHistoryList(UtilTool.getToken(), page, pageSize)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                .subscribe(new Observer<GonggaoListInfo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(GonggaoListInfo gonggaoListInfo) {
                        if (gonggaoListInfo.getStatus() == 1) {
                            callBack2.send(gonggaoListInfo.getData());
                        }else {
                            callBack2.finishRefresh();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        callBack2.error();
                        UtilTool.Log("新聞", e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void getDraftList(int page, int pageSize, final CallBack2 callBack2) {
        RetrofitUtil.getInstance(mContext)
                .getServer()
                .NewsDraftList(UtilTool.getToken(), page, pageSize)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                .subscribe(new Observer<GonggaoListInfo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(GonggaoListInfo gonggaoListInfo) {
                        if (gonggaoListInfo.getStatus() == 1) {
                            callBack2.send(gonggaoListInfo.getData());
                        }else {
                            callBack2.finishRefresh();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        callBack2.error();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void deleteNews(int id, final int type) {
        RetrofitUtil.getInstance(mContext)
                .getServer()
                .deleteNews(UtilTool.getToken(), id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                .subscribe(new Observer<BaseInfo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(BaseInfo baseInfo) {
                        if (baseInfo.getStatus() == 1) {
                            if (type == Constants.NEW_DRAFTS_TYPE) {
                                EventBus.getDefault().post(new MessageEvent(mContext.getString(R.string.delete_news_drafts)));
                            } else if (type == Constants.NEW_MY_TYPE) {
                                EventBus.getDefault().post(new MessageEvent(mContext.getString(R.string.delete_news_my)));
                            }
                            Toast.makeText(mContext, mContext.getString(R.string.delete_succeed), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(mContext, baseInfo.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        UtilTool.Log("新聞", e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void deleteDrowsingHistory() {
        RetrofitUtil.getInstance(mContext)
                .getServer()
                .deleteDrowsingHistory(UtilTool.getToken())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                .subscribe(new Observer<BaseInfo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(BaseInfo baseInfo) {
                        if (baseInfo.getStatus() == 1) {
                            EventBus.getDefault().post(new MessageEvent(mContext.getString(R.string.empty_news_browsing_history)));
                        }
                        Toast.makeText(mContext, baseInfo.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Throwable e) {
                        UtilTool.Log("新聞", e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void getGonggaoList(int page, int pageSize, int status, final CallBack2 callBack2) {
        RetrofitUtil.getInstance(mContext)
                .getServer()
                .GonggaoList(UtilTool.getToken(), page, pageSize, status)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                .subscribe(new Observer<GonggaoListInfo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(GonggaoListInfo gonggaoListInfo) {
                        if (gonggaoListInfo.getStatus() == 1) {
                            callBack2.send(gonggaoListInfo.getData());
                        }else {
                            callBack2.finishRefresh();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        callBack2.error();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void getAdCost(final CallBack3 callBack3) {
        RetrofitUtil.getInstance(mContext)
                .getServer()
                .getAdCost(UtilTool.getToken())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                .subscribe(new Observer<BaseInfo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(BaseInfo baseInfo) {
                        if (baseInfo.getStatus() == 1) {
                            callBack3.send(baseInfo.getData());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    //定义接口
    public interface CallBack {
        void send(List<NewsListInfo.ListsBean> lists, List<NewsListInfo.TopBean> top);

        void error();
        void finishRefresh();
    }

    //定义接口
    public interface CallBack2 {
        void send(List<GonggaoListInfo.DataBean> data);

        void error();
        void finishRefresh();
    }

    //定义接口
    public interface CallBack3 {
        void send(BaseInfo.DataBean data);
    }
}
