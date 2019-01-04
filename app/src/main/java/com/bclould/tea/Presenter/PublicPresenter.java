package com.bclould.tea.Presenter;

import android.app.Activity;
import android.content.Context;
import com.bclould.tea.R;
import com.bclould.tea.model.BaseInfo;
import com.bclould.tea.model.PublicDetailsInfo;
import com.bclould.tea.model.PublicInfo;
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
 * Created by GIjia on 2018/7/27.
 */
public class PublicPresenter {
    private final Context mContext;
    private LoadingProgressDialog mProgressDialog;

    public PublicPresenter(Context context) {
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
    public void publicList(final CallBack callBack) {
        showDialog();
        RetrofitUtil.getInstance(mContext)
                .getServer()
                .publicList(UtilTool.getToken())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                .subscribe(new Observer<PublicInfo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(PublicInfo publicInfo) {
                        if (!ActivityUtil.isActivityOnTop((Activity) mContext)) return;
                        hideDialog();
                        if (publicInfo.getStatus() == 1) {
                            callBack.send(publicInfo);
                        } else {
                            ToastShow.showToast2((Activity) mContext, publicInfo.getMessage());
                            callBack.error();
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

    public void searchList(String name,final CallBack callBack) {
        showDialog();
        RetrofitUtil.getInstance(mContext)
                .getServer()
                .searchList(UtilTool.getToken(),name)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                .subscribe(new Observer<PublicInfo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(PublicInfo publicInfo) {
                        if (!ActivityUtil.isActivityOnTop((Activity) mContext)) return;
                        hideDialog();
                        if (publicInfo.getStatus() == 1) {
                            callBack.send(publicInfo);
                        } else {
                            ToastShow.showToast2((Activity) mContext, publicInfo.getMessage());
                            callBack.error();
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

    public void publicDeltails(int id,final CallBack1 callBack) {
        showDialog();
        RetrofitUtil.getInstance(mContext)
                .getServer()
                .publicDeltails(UtilTool.getToken(),id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                .subscribe(new Observer<PublicDetailsInfo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(PublicDetailsInfo publicDetailsInfo) {
                        if (!ActivityUtil.isActivityOnTop((Activity) mContext)) return;
                        hideDialog();
                        if (publicDetailsInfo.getStatus() == 1) {
                            callBack.send(publicDetailsInfo);
                        } else {
                            ToastShow.showToast2((Activity) mContext, publicDetailsInfo.getMessage());
                            callBack.error();
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

    public void publicAdd(int id,final CallBack1 callBack) {
        showDialog();
        RetrofitUtil.getInstance(mContext)
                .getServer()
                .publicAdd(UtilTool.getToken(),id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                .subscribe(new Observer<PublicDetailsInfo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(PublicDetailsInfo publicDetailsInfo) {
                        if (!ActivityUtil.isActivityOnTop((Activity) mContext)) return;
                        hideDialog();
                        if (publicDetailsInfo.getStatus() == 1) {
                            callBack.send(publicDetailsInfo);
                        } else {
                            ToastShow.showToast2((Activity) mContext, publicDetailsInfo.getMessage());
                            callBack.error();
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

    public void publicUnfollow(int id,final CallBack2 callBack) {
        showDialog();
        RetrofitUtil.getInstance(mContext)
                .getServer()
                .publicUnfollow(UtilTool.getToken(),id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                .subscribe(new Observer<BaseInfo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(BaseInfo baseInfo) {
                        if (!ActivityUtil.isActivityOnTop((Activity) mContext)) return;
                        hideDialog();
                        if (baseInfo.getStatus() == 1) {
                            callBack.send();
                        } else {
                            callBack.error();
                        }
                        ToastShow.showToast2((Activity) mContext, baseInfo.getMessage());
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



    //定义接口
    public interface CallBack {
        void send(PublicInfo dataBean);
        void error();
    }

    //定义接口
    public interface CallBack1 {
        void send(PublicDetailsInfo publicDetailsInfo);
        void error();
    }

    //定义接口
    public interface CallBack2{
        void send();
        void error();
    }

}
