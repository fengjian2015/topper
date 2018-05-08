package com.bclould.tocotalk.Presenter;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.widget.Toast;

import com.bclould.tocotalk.R;
import com.bclould.tocotalk.model.NewsListInfo;
import com.bclould.tocotalk.network.RetrofitUtil;
import com.bclould.tocotalk.utils.UtilTool;

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

    private final Context mContext;

    public NewsNoticePresenter(Context context) {
        mContext = context;
    }

    public void getNewsList(int page, int pageSize, final CallBack callBack) {
        if (UtilTool.isNetworkAvailable(mContext)) {
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
                                UtilTool.Log("新聞", "成功");
                                callBack.send(newsListInfo.getLists(), newsListInfo.getTop());
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            UtilTool.Log("新聞", e.getMessage());
                            Toast.makeText(mContext, mContext.getString(R.string.toast_network_error), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        } else {
            Toast.makeText(mContext, mContext.getString(R.string.toast_network_error), Toast.LENGTH_SHORT).show();
        }
    }

    //定义接口
    public interface CallBack {
        void send(List<NewsListInfo.ListsBean> lists, List<NewsListInfo.TopBean> top);
    }
}
