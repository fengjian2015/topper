package com.bclould.tea.Presenter;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.bclould.tea.R;
import com.bclould.tea.model.BaseInfo;
import com.bclould.tea.model.BindingInfo;
import com.bclould.tea.model.HistoryInfo;
import com.bclould.tea.model.MyTeamInfo;
import com.bclould.tea.model.NodeInfo;
import com.bclould.tea.model.UpgradeInfo;
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
 * Created by GIjia on 2018/9/25.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class DistributionPresenter {
    private final Context mContext;
    private LoadingProgressDialog mProgressDialog;

    public DistributionPresenter(Context context) {
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


    public void bindTeam(String email,String password,final CallBack callBack) {
        showDialog();
        RetrofitUtil.getInstance(mContext)
                .getServer()
                .bingTeam(UtilTool.getToken(),email,password)
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


    public void bindDesc(final CallBack callBack) {
        showDialog();
        RetrofitUtil.getInstance(mContext)
                .getServer()
                .bingDesc(UtilTool.getToken())
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


    public void infoFTC(final CallBack1 callBack) {
        showDialog();
        RetrofitUtil.getInstance(mContext)
                .getServer()
                .infoFTC(UtilTool.getToken())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                .subscribe(new Observer<BindingInfo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(BindingInfo baseInfo) {
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


    public void myTeam(int user_id,int page,final CallBack2 callBack) {
        showDialog();
        RetrofitUtil.getInstance(mContext)
                .getServer()
                .myTeam(UtilTool.getToken(),user_id,page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                .subscribe(new Observer<MyTeamInfo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(MyTeamInfo baseInfo) {
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

    public void nodeList(int type,int page,final CallBack3 callBack) {
        showDialog();
        RetrofitUtil.getInstance(mContext)
                .getServer()
                .nodelist(UtilTool.getToken(),type,page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                .subscribe(new Observer<NodeInfo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(NodeInfo baseInfo) {
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

    public void nodeBuy(final CallBack4 callBack) {
        showDialog();
        RetrofitUtil.getInstance(mContext)
                .getServer()
                .nodeBuy(UtilTool.getToken())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                .subscribe(new Observer<UpgradeInfo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(UpgradeInfo baseInfo) {
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

    public void nodeBuyAction(String password,String number,int type,final CallBack5 callBack) {
        showDialog();
        RetrofitUtil.getInstance(mContext)
                .getServer()
                .nodeBuyAction(UtilTool.getToken(),password,number,type)
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
                            callBack.send(baseInfo);
                        } else {
                            callBack.error(baseInfo.getMessage());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (!ActivityUtil.isActivityOnTop((Activity) mContext)) return;
                        hideDialog();
                        callBack.error(mContext.getString(R.string.toast_network_error));
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void historyFtc(int page,final CallBack6 callBack) {
        showDialog();
        RetrofitUtil.getInstance(mContext)
                .getServer()
                .historyFtc(UtilTool.getToken(),page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                .subscribe(new Observer<HistoryInfo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(HistoryInfo baseInfo) {
                        if (!ActivityUtil.isActivityOnTop((Activity) mContext)) return;
                        hideDialog();
                        if (baseInfo.getStatus() == 1) {
                            callBack.send(baseInfo);
                        } else {
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

    //定义接口
    public interface CallBack {
        void send(BaseInfo baseInfo);
        void error();
    }


    //定义接口
    public interface CallBack1 {
        void send(BindingInfo baseInfo);
        void error();
    }

    //定义接口
    public interface CallBack2 {
        void send(MyTeamInfo baseInfo);
        void error();
    }

    //定义接口
    public interface CallBack3 {
        void send(NodeInfo baseInfo);
        void error();
    }

    //定义接口
    public interface CallBack4 {
        void send(UpgradeInfo baseInfo);
        void error();
    }

    //定义接口
    public interface CallBack5 {
        void send(BaseInfo baseInfo);
        void error(String message);
    }


    //定义接口
    public interface CallBack6 {
        void send(HistoryInfo baseInfo);
        void error();
    }
}
