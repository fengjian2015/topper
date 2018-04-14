package com.bclould.tocotalk.Presenter;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.bclould.tocotalk.R;
import com.bclould.tocotalk.model.BaseInfo;
import com.bclould.tocotalk.model.ReceiptInfo;
import com.bclould.tocotalk.model.TransferListInfo;
import com.bclould.tocotalk.network.RetrofitUtil;
import com.bclould.tocotalk.ui.activity.PayPasswordActivity;
import com.bclould.tocotalk.ui.activity.PaymentActivity;
import com.bclould.tocotalk.ui.activity.RealNameC1Activity;
import com.bclould.tocotalk.ui.widget.DeleteCacheDialog;
import com.bclould.tocotalk.ui.widget.LoadingProgressDialog;
import com.bclould.tocotalk.utils.UtilTool;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by GA on 2018/3/22.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
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
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }


    public void generateReceiptQrCode(String coinId, String count, String remark, final CallBack callBack) {
        if (UtilTool.isNetworkAvailable(mContext)) {
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
                            } else if (baseInfo.getMessage().equals(mContext.getString(R.string.real_name_authentication_hint))) {
                                showHintDialog(0);
                            } else {
                                Toast.makeText(mContext, baseInfo.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            hideDialog();
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

    public void payment(String userId, String count, int id, String password, final CallBack2 callBack2) {
        UtilTool.Log("日志", userId + "," + id);
        if (UtilTool.isNetworkAvailable(mContext)) {
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
                            if (receiptInfo.getMessage().equals(mContext.getString(R.string.set_pay_pw_hint))) {
                                showHintDialog(1);
                            } else if (receiptInfo.getMessage().equals(mContext.getString(R.string.payment_pw_error))) {
                                PaymentActivity activity = (PaymentActivity) mContext;
                                activity.showHintDialog();
                            } else if (receiptInfo.getStatus() == 1) {
                                callBack2.send(receiptInfo.getData());
                            } else if (receiptInfo.getMessage().equals(mContext.getString(R.string.real_name_authentication_hint))) {
                                showHintDialog(0);
                            } else {
                                Toast.makeText(mContext, receiptInfo.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            hideDialog();
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

    private void showHintDialog(final int type) {
        final DeleteCacheDialog deleteCacheDialog = new DeleteCacheDialog(R.layout.dialog_delete_cache, mContext);
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
        if (UtilTool.isNetworkAvailable(mContext)) {
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
                            if (baseInfo.getMessage().equals(mContext.getString(R.string.set_pay_pw_hint))) {
                                showHintDialog(1);
                            } else if (baseInfo.getMessage().equals(mContext.getString(R.string.payment_pw_error))) {
                                PaymentActivity activity = (PaymentActivity) mContext;
                                activity.showHintDialog();
                            } else if (baseInfo.getStatus() == 1) {
                                callBack3.send(baseInfo.getData().getUrl());
                            } else if (baseInfo.getMessage().equals(mContext.getString(R.string.real_name_authentication_hint))) {
                                showHintDialog(0);
                            }
                            Toast.makeText(mContext, baseInfo.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(Throwable e) {
                            hideDialog();
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

    public void transRecord(String page, String pageSize, String type, String date, final CallBack4 callBack4) {
        if (UtilTool.isNetworkAvailable(mContext)) {
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

    public void receipt(String data, final CallBack5 callBack5) {
        if (UtilTool.isNetworkAvailable(mContext)) {
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
                            } else if (receiptInfo.getMessage().equals(mContext.getString(R.string.real_name_authentication_hint))) {
                                showHintDialog(0);
                            }
                            Toast.makeText(mContext, receiptInfo.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(Throwable e) {
                            hideDialog();
                            UtilTool.Log("错误", e.getMessage());
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
        void send(BaseInfo.DataBean data);
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
    }

    //定义接口
    public interface CallBack5 {
        void send(ReceiptInfo.DataBean data);
    }
}
