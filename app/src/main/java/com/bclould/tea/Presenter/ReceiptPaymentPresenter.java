package com.bclould.tea.Presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.bclould.tea.R;
import com.bclould.tea.model.BaseInfo;
import com.bclould.tea.model.ReceiptInfo;
import com.bclould.tea.model.TransferListInfo;
import com.bclould.tea.network.RetrofitUtil;
import com.bclould.tea.ui.activity.PayPasswordActivity;
import com.bclould.tea.ui.activity.PaymentActivity;
import com.bclould.tea.ui.activity.RealNameC1Activity;
import com.bclould.tea.ui.widget.DeleteCacheDialog;
import com.bclould.tea.ui.widget.LoadingProgressDialog;
import com.bclould.tea.utils.ActivityUtil;
import com.bclould.tea.utils.ToastShow;
import com.bclould.tea.utils.UtilTool;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by GA on 2018/3/22.
 */

public class ReceiptPaymentPresenter {

    private final Context mContext;
    private LoadingProgressDialog mProgressDialog;

    public ReceiptPaymentPresenter(Context context) {
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


    public void generateReceiptQrCode(String coinId, String count, String remark, final CallBack callBack) {
        RetrofitUtil.getInstance(mContext)
                .getServer()
                .generateReceiptQrCode(UtilTool.getToken(), coinId, count, remark)
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
                        } else if (baseInfo.getType() == 1) {
                            showHintDialog(0);
                        } else {
                            Toast.makeText(mContext, baseInfo.getMessage(), Toast.LENGTH_SHORT).show();
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

    public void payment(String userId, String count, int id, String password, final CallBack2 callBack2) {
        UtilTool.Log("日志", userId + "," + id);
        RetrofitUtil.getInstance(mContext)
                .getServer()
                .payment(UtilTool.getToken(), userId, count, id + "", password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                .subscribe(new Observer<ReceiptInfo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ReceiptInfo receiptInfo) {
                        if (receiptInfo.getStatus() == 1) {
                            callBack2.send(receiptInfo.getData());
                        } else if (receiptInfo.getType() == 4) {
                            showHintDialog(1);
                        } else if (receiptInfo.getType() == 6) {
                            PaymentActivity activity = (PaymentActivity) mContext;
                            activity.showHintDialog();
                        } else if (receiptInfo.getType() == 1) {
                            showHintDialog(0);
                        } else {
                            Toast.makeText(mContext, receiptInfo.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        hideDialog();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void showHintDialog(final int type) {
        if(!ActivityUtil.isActivityOnTop(mContext))return;
        final DeleteCacheDialog deleteCacheDialog = new DeleteCacheDialog(R.layout.dialog_delete_cache, mContext, R.style.dialog);
        deleteCacheDialog.show();
        switch (type) {
            case 0:
                deleteCacheDialog.setTitle(mContext.getString(R.string.real_name_authentication_hint));
                break;
            case 1:
                deleteCacheDialog.setTitle(mContext.getString(R.string.set_pay_pw_hint));
                break;
        }
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
                switch (type) {
                    case 0:
                        mContext.startActivity(new Intent(mContext, RealNameC1Activity.class));
                        break;
                    case 1:
                        mContext.startActivity(new Intent(mContext, PayPasswordActivity.class));
                        break;
                }
            }
        });
    }

    public void generatePaymentQrCode(String count, int id, String password, final CallBack3 callBack3) {
        RetrofitUtil.getInstance(mContext)
                .getServer()
                .generatePaymentQrCode(UtilTool.getToken(), count, id + "", password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                .subscribe(new Observer<BaseInfo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(BaseInfo baseInfo) {
                        if (baseInfo.getStatus() == 1) {
                            callBack3.send(baseInfo.getData().getUrl());
                        } else if (baseInfo.getType() == 4) {
                            showHintDialog(1);
                        } else if (baseInfo.getType() == 6) {
                            PaymentActivity activity = (PaymentActivity) mContext;
                            activity.showHintDialog();
                        } else if (baseInfo.getType() == 1) {
                            showHintDialog(0);
                        }
                        Toast.makeText(mContext, baseInfo.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Throwable e) {
                        hideDialog();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void transRecord(int page, int pageSize, String type, String date, final CallBack4 callBack4) {
        RetrofitUtil.getInstance(mContext)
                .getServer()
                .getTransRecord(UtilTool.getToken(), page, pageSize, type, date)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                .subscribe(new Observer<TransferListInfo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(TransferListInfo transferListInfo) {
                        if (transferListInfo.getStatus() == 1) {
                            callBack4.send(transferListInfo.getData());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        hideDialog();
                        callBack4.error();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void receipt(String data, final CallBack5 callBack5) {
        RetrofitUtil.getInstance(mContext)
                .getServer()
                .receipt(UtilTool.getToken(), data)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                .subscribe(new Observer<ReceiptInfo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ReceiptInfo receiptInfo) {
                        if (receiptInfo.getStatus() == 1) {
                            callBack5.send(receiptInfo.getData());
                        } else if (receiptInfo.getType() == 1) {
                            showHintDialog(0);
                        }
                        Toast.makeText(mContext, receiptInfo.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Throwable e) {
                        hideDialog();
                        UtilTool.Log("错误", e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void payMerchant(String code, String number, String remark, String password, final PersonalDetailsPresenter.CallBack callBack) {
        showDialog();
        RetrofitUtil.getInstance(mContext)
                .getServer()
                .payMerchant(UtilTool.getToken(), code, number, password, remark)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                .subscribe(new Observer<BaseInfo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(BaseInfo baseInfo) {
                        hideDialog();
                        if (baseInfo.getStatus() == 1) {
                            callBack.send();
                        }else {
                            ToastShow.showToast2((Activity)mContext, baseInfo.getMessage());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        hideDialog();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    //定义接口
    public interface CallBack {
        void send(BaseInfo.DataBean data);

        void error();
    }

    //定义接口
    public interface CallBack2 {
        void send(ReceiptInfo.DataBean data);
    }

    //定义接口
    public interface CallBack3 {
        void send(String url);
    }

    //定义接口
    public interface CallBack4 {
        void send(List<TransferListInfo.DataBean> data);

        void error();

        void finishRefresh();
    }

    //定义接口
    public interface CallBack5 {
        void send(ReceiptInfo.DataBean data);
    }
}
