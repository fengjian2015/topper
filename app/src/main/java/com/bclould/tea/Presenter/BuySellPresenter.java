package com.bclould.tea.Presenter;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.bclould.tea.R;
import com.bclould.tea.model.BaseInfo;
import com.bclould.tea.model.DealListInfo;
import com.bclould.tea.model.MyAdListInfo;
import com.bclould.tea.model.OrderInfo;
import com.bclould.tea.model.OrderListInfo;
import com.bclould.tea.model.OrderStatisticsInfo;
import com.bclould.tea.model.TransRecordInfo;
import com.bclould.tea.network.RetrofitUtil;
import com.bclould.tea.ui.activity.BankCardActivity;
import com.bclould.tea.ui.activity.BuySellActivity;
import com.bclould.tea.ui.activity.PayPasswordActivity;
import com.bclould.tea.ui.widget.DeleteCacheDialog;
import com.bclould.tea.ui.widget.LoadingProgressDialog;
import com.bclould.tea.utils.UtilTool;

import java.util.List;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by GA on 2018/1/19.
 */

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
            mProgressDialog.hideDialog();
        }
    }

    public void getDealList(int page, int pageSize, int type, String coinName, final int state_id, final CallBack callBack) {
        RetrofitUtil.getInstance(mContext)
                .getServer()
                .getDealList(UtilTool.getToken(), type, coinName, state_id, page, pageSize)
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
                            callBack.send(baseInfo.getData());
                        }else {
                            callBack.finishRefresh();
                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                        hideDialog();
                        callBack.error();
                        UtilTool.Log("otc", e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void getOrderList(int page, int pageSize, String coinName, final String filtrate, String user, final CallBack3 callBack) {
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
                        else
                            callBack.finishRefresh();
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

    public void getMyOrderList(String coinName, int type, int page, int pageSize, final CallBack3 callBack) {
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
                        else
                            callBack.finishRefresh();
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

    public void createOrder(int id, String coinCount, String price, String money, final CallBack2 callBack2, String password) {
        UtilTool.Log("日志", "调了几次");
        double count = Double.parseDouble(coinCount);
        double priced = Double.parseDouble(price);
        double moneyd = Double.parseDouble(money);
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
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }


    public void bindBankStatus() {
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
                    }

                    @Override
                    public void onComplete() {

                    }
                });
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

    public void transRecordInfo(String log_id, String id, String type_number, final CallBack7 callBack7) {
        showDialog();
        RetrofitUtil.getInstance(mContext)
                .getServer()
                .transRecord(UtilTool.getToken(), log_id, type_number, id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                .subscribe(new Observer<TransRecordInfo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(TransRecordInfo transRecordInfo) {
                        hideDialog();
                        if (transRecordInfo.getStatus() == 1) {
                            callBack7.send(transRecordInfo.getData());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        hideDialog();
                        callBack7.error();
                        UtilTool.Log("错误", e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void cancelAd(int id, final CallBack4 callBack4) {
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
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void getUserAdList(int type, int page, int page_size, int status, String coinName, final CallBack5 callBack5) {
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
                            callBack5.finishRefresh();
                            Toast.makeText(mContext, myAdListInfo.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        callBack5.error();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void getTotal(String coinName, final CallBack6 callBack6) {
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
                        callBack6.error();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    //定义接口
    public interface CallBack {
        void send(List<DealListInfo.DataBean> dataBean);

        void error();
        void finishRefresh();
    }

    //定义接口
    public interface CallBack2 {
        void send(OrderInfo.DataBean data);
    }

    //定义接口
    public interface CallBack3 {
        void send(List<OrderListInfo.DataBean> data);

        void error();
        void finishRefresh();
    }

    //定义接口
    public interface CallBack4 {
        void send();
    }

    //定义接口
    public interface CallBack5 {

        void send(List<MyAdListInfo.DataBean> data);

        void error();
        void finishRefresh();
    }

    //定义接口
    public interface CallBack6 {
        void send(OrderStatisticsInfo.DataBean data);

        void error();
    }

    //定义接口
    public interface CallBack7 {
        void send(TransRecordInfo.DataBean data);

        void error();
    }
}
