package com.bclould.tea.Presenter;

import android.content.Context;
import com.bclould.tea.model.BaseInfo;
import com.bclould.tea.network.RetrofitUtil;
import com.bclould.tea.utils.UtilTool;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by GA on 2017/12/8.
 */

public class CloudMessagePresenter {

    private final Context mContext;

    public CloudMessagePresenter(Context context) {
        mContext = context;
    }


    public void searchUser(final String user, final CallBack callBack) {
        RetrofitUtil.getInstance(mContext)
                .getServer()
                .searchUser(UtilTool.getToken(), user)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                .subscribe(new Observer<BaseInfo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(BaseInfo baseInfo) {
                        if (baseInfo.getStatus() == 1) {
                            callBack.send(baseInfo.getData());
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
        void send(BaseInfo.DataBean data);
    }
}
