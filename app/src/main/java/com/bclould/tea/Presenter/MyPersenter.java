package com.bclould.tea.Presenter;

import android.content.Context;

import com.bclould.tea.R;
import com.bclould.tea.model.BaseInfo;
import com.bclould.tea.model.base.BaseMapInfo;
import com.bclould.tea.network.RetrofitUtil;
import com.bclould.tea.ui.widget.LoadingProgressDialog;
import com.bclould.tea.utils.ActivityUtil;
import com.bclould.tea.utils.ToastShow;
import com.bclould.tea.utils.UtilTool;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by fengjian on 2019/1/23.
 */

public class MyPersenter {
    private final Context mContext;
    private LoadingProgressDialog mProgressDialog;

    public MyPersenter(Context context) {
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
            mProgressDialog.hideDialog();
        }
    }

    public void activityInfo(final CallBack callBack) {
        showDialog();
        RetrofitUtil.getInstance(mContext)
                .getServer()
                .activityInfo(UtilTool.getToken())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                .subscribe(new Observer<BaseMapInfo>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull BaseMapInfo baseInfo) {
                        if(!ActivityUtil.isActivityOnTop(mContext))return;
                        hideDialog();
                        if (baseInfo.getStatus() == 1)
                            callBack.send(baseInfo);
                        else
                            ToastShow.showToast2(mContext, baseInfo.getMessage());
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        if(!ActivityUtil.isActivityOnTop(mContext))return;
                        callBack.error();
                        hideDialog();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void activityReceive(final CallBack callBack) {
        showDialog();
        RetrofitUtil.getInstance(mContext)
                .getServer()
                .activityReceive(UtilTool.getToken())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                .subscribe(new Observer<BaseMapInfo>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull BaseMapInfo baseInfo) {
                        if(!ActivityUtil.isActivityOnTop(mContext))return;
                        hideDialog();
                        callBack.send(baseInfo);
                        ToastShow.showToast2(mContext, baseInfo.getMessage());

                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        if(!ActivityUtil.isActivityOnTop(mContext))return;
                        callBack.error();
                        hideDialog();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }


    //定义接口
    public interface CallBack {

        void send(BaseMapInfo data);

        void error();
    }
}
