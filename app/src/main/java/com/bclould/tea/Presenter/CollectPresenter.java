package com.bclould.tea.Presenter;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.bclould.tea.R;
import com.bclould.tea.model.BaseInfo;
import com.bclould.tea.model.CollectInfo;
import com.bclould.tea.network.RetrofitUtil;
import com.bclould.tea.ui.widget.LoadingProgressDialog;
import com.bclould.tea.utils.ToastShow;
import com.bclould.tea.utils.UtilTool;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by GA on 2018/7/13.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class CollectPresenter {

    private final Context mContext;
    private LoadingProgressDialog mProgressDialog;

    public CollectPresenter(Context context) {
        mContext = context;
    }

    private void showDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = LoadingProgressDialog.createDialog(mContext);
            mProgressDialog.setMessage(mContext.getString(R.string.loading));
        }

        mProgressDialog.show();
    }

    private void hideDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

    public void getCollectList(final CallBack callBack) {
        showDialog();
        RetrofitUtil.getInstance(mContext)
                .getServer()
                .CollectList(UtilTool.getToken())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                .subscribe(new Observer<CollectInfo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(CollectInfo collectInfo) {
                        hideDialog();
                        if (collectInfo.getStatus() == 1) {
                            callBack.send(collectInfo.getData());
                        }else {
                            callBack.finishRefresh();
                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                        hideDialog();
                        callBack.error();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void deleteCollect(int id, final CallBack2 callBack2) {
        RetrofitUtil.getInstance(mContext)
                .getServer()
                .deleteCollect(UtilTool.getToken(), id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                .subscribe(new Observer<BaseInfo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(BaseInfo baseInfo) {
                        ToastShow.showToast2((Activity)mContext, baseInfo.getMessage());
                        if(baseInfo.getStatus() == 1){
                            callBack2.send();
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

    public void addCollect(String title, String url, final CallBack2 callBack2) {
        RetrofitUtil.getInstance(mContext)
                .getServer()
                .addCollect(UtilTool.getToken(), title, url)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                .subscribe(new Observer<BaseInfo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(BaseInfo baseInfo) {
                        ToastShow.showToast2((Activity)mContext, baseInfo.getMessage());
                        if(baseInfo.getStatus() == 1){
                            callBack2.send();
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
        void send(List<CollectInfo.DataBean> data);
        void error();
        void finishRefresh();
    }

    //定义接口
    public interface CallBack2 {
        void send();
    }
}
