package com.bclould.tocotalk.Presenter;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.bclould.tocotalk.R;
import com.bclould.tocotalk.model.DealListInfo;
import com.bclould.tocotalk.model.OrderInfo;
import com.bclould.tocotalk.model.OrderListInfo;
import com.bclould.tocotalk.network.RetrofitUtil;
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
            mProgressDialog.setMessage("加载中...");
        }

        mProgressDialog.show();
    }

    private void hideDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

    public void getDealList(int type, final String coin, final CallBack callBack) {
        if (UtilTool.isNetworkAvailable(mContext)) {
            showDialog();
            RetrofitUtil.getInstance(mContext)
                    .getServer()
                    .getDealList(UtilTool.getToken(), type, coin)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                    .subscribe(new Observer<DealListInfo>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(DealListInfo baseInfo) {
                            hideDialog();
                            if (baseInfo.getStatus() == 1)
                                callBack.send(baseInfo.getData(), coin);
                        }

                        @Override
                        public void onError(Throwable e) {
                            hideDialog();
                            Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        } else {
            Toast.makeText(mContext, mContext.getString(R.string.toast_network_error), Toast.LENGTH_SHORT).show();
        }
    }

    public void getOrderList(int type, final String coin, final CallBack3 callBack) {
        if (UtilTool.isNetworkAvailable(mContext)) {
            showDialog();
            RetrofitUtil.getInstance(mContext)
                    .getServer()
                    .getOrderList(UtilTool.getToken(), coin, type)
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
                            Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
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
            showDialog();
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
                            } else if (baseInfo.getMassage().equals("尚未设置交易密码")) {
                                showSetPwDialog();
                            } else if (baseInfo.getMassage().equals("交易密码不正确")) {
                                BuySellActivity activity = (BuySellActivity) mContext;
                                activity.showHintDialog();
                            } else {
                                Toast.makeText(mContext, baseInfo.getMassage() + "", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            hideDialog();
                            Toast.makeText(mContext, "网络连接错误，请稍后重试", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        } else {
            Toast.makeText(mContext, mContext.getString(R.string.toast_network_error), Toast.LENGTH_SHORT).show();
        }
    }

    private void showSetPwDialog() {
        final DeleteCacheDialog deleteCacheDialog = new DeleteCacheDialog(R.layout.dialog_set_pw, mContext);
        deleteCacheDialog.show();
        deleteCacheDialog.setCanceledOnTouchOutside(false);
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
                mContext.startActivity(new Intent(mContext, PayPasswordActivity.class));
            }
        });
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
    }
}
