package com.bclould.tea.Presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;

import com.bclould.tea.R;
import com.bclould.tea.model.BaseInfo;
import com.bclould.tea.model.FGCInfo;
import com.bclould.tea.model.FinanciaProductInfo;
import com.bclould.tea.model.FinancialCoinInfo;
import com.bclould.tea.model.FinancialInfo;
import com.bclould.tea.model.HistoryInfo;
import com.bclould.tea.model.TransferRecordInfo;
import com.bclould.tea.model.base.BaseMapInfo;
import com.bclould.tea.network.RetrofitUtil;
import com.bclould.tea.ui.activity.RealNameC1Activity;
import com.bclould.tea.ui.widget.DeleteCacheDialog;
import com.bclould.tea.ui.widget.LoadingProgressDialog;
import com.bclould.tea.utils.ActivityUtil;
import com.bclould.tea.utils.ToastShow;
import com.bclould.tea.utils.UtilTool;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by GIjia on 2018/10/12.
 */
public class FinanciaPresenter {
    private final Context mContext;
    private LoadingProgressDialog mProgressDialog;

    public FinanciaPresenter(Context context) {
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

    public void assets(int id,final CallBack callBack) {
        showDialog();
        RetrofitUtil.getInstance(mContext)
                .getServer()
                .assets(UtilTool.getToken(),id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                .subscribe(new Observer<FinancialInfo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(FinancialInfo baseInfo) {
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

    public void productIn(int coin_id,String number,String second_password,final CallBack1 callBack) {
        showDialog();
        RetrofitUtil.getInstance(mContext)
                .getServer()
                .productIn(UtilTool.getToken(),coin_id,number,second_password)
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

    public void productOut(int coin_id,String number,String second_password,final CallBack1 callBack) {
        showDialog();
        RetrofitUtil.getInstance(mContext)
                .getServer()
                .productOut(UtilTool.getToken(),coin_id,number,second_password)
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

    public void coinList(String product_id,String type,final CallBack2 callBack) {
        showDialog();
        RetrofitUtil.getInstance(mContext)
                .getServer()
                .coinList(UtilTool.getToken(),type,product_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                .subscribe(new Observer<FinancialCoinInfo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(FinancialCoinInfo baseInfo) {
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


    public void financialBuy(int coin_id,String number,String product_id,String second_password,final CallBack1 callBack) {
        showDialog();
        RetrofitUtil.getInstance(mContext)
                .getServer()
                .financialBuy(UtilTool.getToken(),coin_id,number,product_id,second_password)
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

    public void incomeList(int coin_id,int page,int id,final CallBack3 callBack) {
        showDialog();
        RetrofitUtil.getInstance(mContext)
                .getServer()
                .incomeList(UtilTool.getToken(),coin_id,page,id)
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


    public void buyList(int coin_id,int page,int id,final CallBack3 callBack) {
        showDialog();
        RetrofitUtil.getInstance(mContext)
                .getServer()
                .buyList(UtilTool.getToken(),coin_id,page,id)
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

    public void financialInOut(int coin_id,int page,int type,final CallBack4 callBack) {
        showDialog();
        RetrofitUtil.getInstance(mContext)
                .getServer()
                .financialInOut(UtilTool.getToken(),coin_id,page,type)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                .subscribe(new Observer<TransferRecordInfo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(TransferRecordInfo baseInfo) {
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

    public void financialProduct(int coin_id,final CallBack5 callBack) {
        showDialog();
        RetrofitUtil.getInstance(mContext)
                .getServer()
                .financialProduct(UtilTool.getToken(),coin_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                .subscribe(new Observer<FinanciaProductInfo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(FinanciaProductInfo baseInfo) {
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


    public void exchangeFGC(int page,final CallBack6 callBack) {
        showDialog();
        RetrofitUtil.getInstance(mContext)
                .getServer()
                .exchangeFGC(UtilTool.getToken(),page,10)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                .subscribe(new Observer<FGCInfo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(FGCInfo baseInfo) {
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

    public void exchange(String money,String password,final CallBack1 callBack) {
        showDialog();
        RetrofitUtil.getInstance(mContext)
                .getServer()
                .exchangeFGC1(UtilTool.getToken(),money,password)
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

    public void exchangeRecordFRC(int page ,int page_size,final CallBack7 callBack) {
        showDialog();
        RetrofitUtil.getInstance(mContext)
                .getServer()
                .exchangeRecordFRC(UtilTool.getToken(),page,page_size)
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
                            if (baseInfo.getType() == 1) {
                                showHintDialog();
                                return;
                            }
                            callBack.error();
                            ToastShow.showToast2((Activity) mContext, baseInfo.getMessage());
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

    public void exchangeActionFRC(String number ,String password,final CallBack7 callBack) {
        showDialog();
        RetrofitUtil.getInstance(mContext)
                .getServer()
                .exchangeActionFRC(UtilTool.getToken(),number,password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                .subscribe(new Observer<BaseMapInfo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(BaseMapInfo baseInfo) {
                        if(!ActivityUtil.isActivityOnTop(mContext))return;
                        hideDialog();
                        if (baseInfo.getStatus() == 1) {
                            callBack.send(baseInfo);
                        } else {
                            if (baseInfo.getType() == 1) {
                                showHintDialog();
                                return;
                            }
                            callBack.error();
                        }
                        ToastShow.showToast2((Activity) mContext, baseInfo.getMessage());
                    }

                    @Override
                    public void onError(Throwable e) {
                        if(!ActivityUtil.isActivityOnTop(mContext))return;
                        hideDialog();
                        callBack.error();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void showHintDialog() {
        if(!ActivityUtil.isActivityOnTop(mContext))return;
        final DeleteCacheDialog deleteCacheDialog = new DeleteCacheDialog(R.layout.dialog_delete_cache, mContext, R.style.dialog);
        deleteCacheDialog.show();
        deleteCacheDialog.setTitle(mContext.getString(R.string.real_name_authentication_hint));
        Button retry = (Button) deleteCacheDialog.findViewById(R.id.btn_cancel);
        Button findPassword = (Button) deleteCacheDialog.findViewById(R.id.btn_confirm);
        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteCacheDialog.dismiss();
            }
        });
        findPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteCacheDialog.dismiss();
                mContext.startActivity(new Intent(mContext, RealNameC1Activity.class));

            }
        });
    }

    //定义接口
    public interface CallBack {
        void send(FinancialInfo baseInfo);
        void error();
    }

    //定义接口
    public interface CallBack1 {
        void send(BaseInfo baseInfo);
        void error();
    }

    //定义接口
    public interface CallBack2 {
        void send(FinancialCoinInfo baseInfo);
        void error();
    }

    //定义接口
    public interface CallBack3 {
        void send(HistoryInfo baseInfo);
        void error();
    }

    //定义接口
    public interface CallBack4 {
        void send(TransferRecordInfo baseInfo);
        void error();
    }

    //定义接口
    public interface CallBack5 {
        void send(FinanciaProductInfo baseInfo);
        void error();
    }

    //定义接口
    public interface CallBack6 {
        void send(FGCInfo baseInfo);
        void error();
    }

    //定义接口
    public interface CallBack7 {
        void send(BaseMapInfo baseInfo);
        void error();
    }
}
