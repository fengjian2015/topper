package com.bclould.tea.Presenter;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.bclould.tea.R;
import com.bclould.tea.model.base.BaseListInfo;
import com.bclould.tea.model.base.BaseMapInfo;
import com.bclould.tea.network.RetrofitUtil;
import com.bclould.tea.ui.widget.LoadingProgressDialog;
import com.bclould.tea.utils.ActivityUtil;
import com.bclould.tea.utils.ToastShow;
import com.bclould.tea.utils.UtilTool;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by fengjian on 2019/1/14.
 */

public class TaskPersenter {
    private final Context mContext;
    private LoadingProgressDialog mProgressDialog;

    public TaskPersenter(Context context) {
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

    public void taskRecords(int page,int page_size,String date,final CallBack1 callBack) {
        showDialog();
        RetrofitUtil.getInstance(mContext)
                .getServer()
                .taskRecords(UtilTool.getToken(),page,page_size,date)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                .subscribe(new Observer<BaseListInfo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(BaseListInfo baseInfo) {
                        if (!ActivityUtil.isActivityOnTop((Activity) mContext)) return;
                        hideDialog();
                        if (baseInfo.getStatus() == 1) {
                            callBack.send(baseInfo);
                        } else {
                            callBack.error();
                            ToastShow.showToast2((Activity) mContext, baseInfo.getMessage());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (!ActivityUtil.isActivityOnTop((Activity) mContext)) return;
                        hideDialog();
                        callBack.error();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void taskLists(final CallBack1 callBack) {
        showDialog();
        RetrofitUtil.getInstance(mContext)
                .getServer()
                .taskLists(UtilTool.getToken())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                .subscribe(new Observer<BaseListInfo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(BaseListInfo baseInfo) {
                        if (!ActivityUtil.isActivityOnTop((Activity) mContext)) return;
                        hideDialog();
                        if (baseInfo.getStatus() == 1) {
                            callBack.send(baseInfo);
                        } else {
                            callBack.error();
                            ToastShow.showToast2((Activity) mContext, baseInfo.getMessage());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (!ActivityUtil.isActivityOnTop((Activity) mContext)) return;
                        hideDialog();
                        callBack.error();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void taskReward(String code,boolean isShow,final CallBack2 callBack) {
        if (isShow)
        showDialog();
        RetrofitUtil.getInstance(mContext)
                .getServer()
                .taskReward(UtilTool.getToken(),code)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                .subscribe(new Observer<BaseMapInfo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(BaseMapInfo baseInfo) {
                        hideDialog();
                        if (baseInfo.getStatus() == 1) {
                            callBack.send(baseInfo);
                        } else {
                            callBack.error();
                            Toast.makeText(mContext, baseInfo.getMessage(),Toast.LENGTH_SHORT).show();
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

    //定义接口
    public interface CallBack1{
        void send(BaseListInfo baseInfo);
        void error();
    }


    //定义接口
    public interface CallBack2{
        void send(BaseMapInfo baseInfo);
        void error();
    }
}
