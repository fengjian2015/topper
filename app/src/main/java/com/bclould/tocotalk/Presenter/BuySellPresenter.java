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
import com.bclould.tocotalk.model.DealListInfo;
import com.bclould.tocotalk.model.MyAdListInfo;
import com.bclould.tocotalk.model.OrderInfo;
import com.bclould.tocotalk.model.OrderListInfo;
import com.bclould.tocotalk.model.OrderStatisticsInfo;
import com.bclould.tocotalk.model.TransRecordInfo;
import com.bclould.tocotalk.network.RetrofitUtil;
import com.bclould.tocotalk.ui.activity.BankCardActivity;
import com.bclould.tocotalk.ui.activity.BuySellActivity;
import com.bclould.tocotalk.ui.activity.PayPasswordActivity;
import com.bclould.tocotalk.ui.widget.DeleteCacheDialog;
import com.bclould.tocotalk.ui.widget.LoadingProgressDialog;
import com.bclould.tocotalk.utils.UtilTool;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by GA on 2018/1/19.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class BuySellPresenter {

    private final Context mContext;
    private LoadingProgressDialog mProgressDialog;

    public BuySellPresenter(Context context) {
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

    public void getDealList(int page, int pageSize, int type, String coinName, final String state, final CallBack callBack) {
        if (UtilTool.isNetworkAvailable(mContext)) {
            RetrofitUtil.getInstance(mContext)
                    .getServer()
                    .getDealList(UtilTool.getToken(), type, coinName, state, page, pageSize)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                    .subscribe(new Observer<DealListInfo>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(DealListInfo baseInfo) {
                            hideDialog();
                            if (baseInfo.getStatus() == 1) {
                                callBack.send(baseInfo.getData(), state);
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            hideDialog();
                            UtilTool.Log("otc", e.getMessage());
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

    public void getOrderList(int page, int pageSize, String coinName, final String filtrate, String user, final CallBack3 callBack) {
        if (UtilTool.isNetworkAvailable(mContext)) {
            RetrofitUtil.getInstance(mContext)
                    .getServer()
                    .getOrderList(UtilTool.getToken(), coinName, filtrate, user, page, pageSize)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                    .subscribe(new Observer<OrderListInfo>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(OrderListInfo baseInfo) {
                            hideDialog();
                            if (baseInfo.getStatus() == 1)
                                callBack.send(baseInfo.getData());
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

    public void getMyOrderList(String coinName, int type, int page, int pageSize, final CallBack3 callBack) {
        if (UtilTool.isNetworkAvailable(mContext)) {
            RetrofitUtil.getInstance(mContext)
                    .getServer()
                    .getMyOrderList(UtilTool.getToken(), type, page, pageSize, coinName)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                    .subscribe(new Observer<OrderListInfo>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(OrderListInfo baseInfo) {
                            hideDialog();
                            if (baseInfo.getStatus() == 1)
                                callBack.send(baseInfo.getData());
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

    public void createOrder(int id, String coinCount, String price, String money, final CallBack2 callBack2, String password) {
        UtilTool.Log("日志", "调了几次");
        double count = Double.parseDouble(coinCount);
        double priced = Double.parseDouble(price);
        double moneyd = Double.parseDouble(money);
        if (UtilTool.isNetworkAvailable(mContext)) {
            RetrofitUtil.getInstance(mContext)
                    .getServer()
                    .createOrder(UtilTool.getToken(), id, count, priced, moneyd, password)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                    .subscribe(new Observer<OrderInfo>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(OrderInfo baseInfo) {
                            hideDialog();
                            if (baseInfo.getStatus() == 1) {
                                callBack2.send(baseInfo.getData());
                            } else if (baseInfo.getStatus() == 2) {
                                if (baseInfo.getType() == 4) {
                                    showHintDialog(1);
                                } else if (baseInfo.getType() == 6) {
                                    BuySellActivity activity = (BuySellActivity) mContext;
                                    activity.showHintDialog();
                                } else if (baseInfo.getType() == 2) {
                                    showHintDialog(0);
                                } else {
                                    Toast.makeText(mContext, baseInfo.getMessage() + "", Toast.LENGTH_SHORT).show();
                                }
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


    public void bindBankStatus() {
        if (UtilTool.isNetworkAvailable(mContext)) {
            RetrofitUtil.getInstance(mContext)
                    .getServer()
                    .bindBankStatus(UtilTool.getToken())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                    .subscribe(new Observer<BaseInfo>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(BaseInfo baseInfo) {
                            if (baseInfo.getStatus() == 1) {
                            } else if (baseInfo.getType() == 2) {
                                showHintDialog(0);
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
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
        final DeleteCacheDialog deleteCacheDialog = new DeleteCacheDialog(R.layout.dialog_delete_cache, mContext, R.style.dialog);
        deleteCacheDialog.show();
        switch (type) {
            case 0:
                deleteCacheDialog.setTitle(mContext.getString(R.string.binding_bank_hint));
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
                        mContext.startActivity(new Intent(mContext, BankCardActivity.class));
                        break;
                    case 1:
                        mContext.startActivity(new Intent(mContext, PayPasswordActivity.class));
                        break;
                }
            }
        });
    }

    public void transRecordInfo(String id, String type_number, final CallBack3 callBack3) {
        if (UtilTool.isNetworkAvailable(mContext)) {
            RetrofitUtil.getInstance(mContext)
                    .getServer()
                    .transRecord(UtilTool.getToken(), id, type_number)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                    .subscribe(new Observer<TransRecordInfo>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(TransRecordInfo transRecordInfo) {
                            if (transRecordInfo.getStatus() == 1) {
                                callBack3.send2(transRecordInfo.getData());
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
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

    public void cancelAd(int id, final CallBack4 callBack4) {
        if (UtilTool.isNetworkAvailable(mContext)) {
            RetrofitUtil.getInstance(mContext)
                    .getServer()
                    .cancelTrans(UtilTool.getToken(), id + "")
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                    .subscribe(new Observer<BaseInfo>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(BaseInfo baseInfo) {
                            if (baseInfo.getStatus() == 1) {
                                callBack4.send();
                            }
                            Toast.makeText(mContext, baseInfo.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(Throwable e) {
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

    public void getUserAdList(int type, int page, int page_size, int status, String coinName, final CallBack5 callBack5) {
        if (UtilTool.isNetworkAvailable(mContext)) {
            RetrofitUtil.getInstance(mContext)
                    .getServer()
                    .getMyAdList(UtilTool.getToken(), type, page, page_size, coinName, status)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                    .subscribe(new Observer<MyAdListInfo>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(MyAdListInfo myAdListInfo) {
                            if (myAdListInfo.getStatus() == 1) {
                                callBack5.send(myAdListInfo.getData());
                            } else {
                                Toast.makeText(mContext, myAdListInfo.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
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

    public void getTotal(String coinName, final CallBack6 callBack6) {
        if (UtilTool.isNetworkAvailable(mContext)) {
            RetrofitUtil.getInstance(mContext)
                    .getServer()
                    .getTotal(UtilTool.getToken(), coinName)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                    .subscribe(new Observer<OrderStatisticsInfo>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(OrderStatisticsInfo orderStatisticsInfo) {
                            if (orderStatisticsInfo.getStatus() == 1) {
                                callBack6.send(orderStatisticsInfo.getData());
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
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
        void send(List<DealListInfo.DataBean> dataBean, String coin);
    }

    //定义接口
    public interface CallBack2 {
        void send(OrderInfo.DataBean data);
    }

    //定义接口
    public interface CallBack3 {
        void send(List<OrderListInfo.DataBean> data);

        void send2(TransRecordInfo.DataBean data);
    }

    //定义接口
    public interface CallBack4 {
        void send();
    }

    //定义接口
    public interface CallBack5 {

        void send(List<MyAdListInfo.DataBean> data);
    }

    //定义接口
    public interface CallBack6 {

        void send(OrderStatisticsInfo.DataBean data);
    }
}
