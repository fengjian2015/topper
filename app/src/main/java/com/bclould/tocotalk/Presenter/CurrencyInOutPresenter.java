package com.bclould.tocotalk.Presenter;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.bclould.tocotalk.R;
import com.bclould.tocotalk.model.BaseInfo;
import com.bclould.tocotalk.network.RetrofitUtil;
import com.bclould.tocotalk.ui.activity.CurrencyInOutActivity;
import com.bclould.tocotalk.ui.activity.GoogleVerificationActivity;
import com.bclould.tocotalk.ui.activity.PayPasswordActivity;
import com.bclould.tocotalk.ui.widget.DeleteCacheDialog;
import com.bclould.tocotalk.ui.widget.LoadingProgressDialog;
import com.bclould.tocotalk.utils.UtilTool;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by GA on 2017/11/17.
 */

public class CurrencyInOutPresenter {

    private final CurrencyInOutActivity mCurrencyInOutActivity;
    private LoadingProgressDialog mProgressDialog;

    public CurrencyInOutPresenter(CurrencyInOutActivity currencyInOutActivity) {
        mCurrencyInOutActivity = currencyInOutActivity;
    }

    private void showDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = LoadingProgressDialog.createDialog(mCurrencyInOutActivity);
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

    public void coinOutAction(int id, int address, int count, String google_code) {

        if (UtilTool.isNetworkAvailable(mCurrencyInOutActivity)) {
            showDialog();
            RetrofitUtil.getInstance(mCurrencyInOutActivity)
                    .getServer()
                    .coinOutAction(UtilTool.getToken(), id, address, count, google_code)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                    .subscribe(new Observer<BaseInfo>() {
                        @Override
                        public void onSubscribe(@NonNull Disposable d) {

                        }

                        @Override
                        public void onNext(@NonNull BaseInfo baseInfo) {
                            if (baseInfo.getMessage().equals("请先绑定谷歌验证"))
                                mCurrencyInOutActivity.startActivity(new Intent(mCurrencyInOutActivity, GoogleVerificationActivity.class));
                            hideDialog();
                            Toast.makeText(mCurrencyInOutActivity, baseInfo.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(@NonNull Throwable e) {
                            hideDialog();
                            Toast.makeText(mCurrencyInOutActivity, "网络连接失败，请稍后重试", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        } else {

            Toast.makeText(mCurrencyInOutActivity, mCurrencyInOutActivity.getString(R.string.toast_network_error), Toast.LENGTH_SHORT).show();

        }
    }

    private void showSetPwDialog() {
        final DeleteCacheDialog deleteCacheDialog = new DeleteCacheDialog(R.layout.dialog_set_pw, mCurrencyInOutActivity);
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
                mCurrencyInOutActivity.startActivity(new Intent(mCurrencyInOutActivity, PayPasswordActivity.class));
            }
        });
    }

    public void transfer(String name, String userName, double count, String payPassword) {
        if (UtilTool.isNetworkAvailable(mCurrencyInOutActivity)) {
            showDialog();
            RetrofitUtil.getInstance(mCurrencyInOutActivity)
                    .getServer()
                    .transfer(UtilTool.getToken(), name, userName, count, payPassword)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                    .subscribe(new Observer<BaseInfo>() {
                        @Override
                        public void onSubscribe(@NonNull Disposable d) {

                        }

                        @Override
                        public void onNext(@NonNull BaseInfo baseInfo) {
                            hideDialog();
                             if (baseInfo.getMessage().equals("尚未设置交易密码")) {
                                showSetPwDialog();
                            }
                            Toast.makeText(mCurrencyInOutActivity, baseInfo.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(@NonNull Throwable e) {
                            hideDialog();
                            Toast.makeText(mCurrencyInOutActivity, "网络连接失败，请稍后重试", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        } else {

            Toast.makeText(mCurrencyInOutActivity, mCurrencyInOutActivity.getString(R.string.toast_network_error), Toast.LENGTH_SHORT).show();

        }
    }

    public void inCoin(int id, final CallBack callBack) {
        if (UtilTool.isNetworkAvailable(mCurrencyInOutActivity)) {
            showDialog();
            RetrofitUtil.getInstance(mCurrencyInOutActivity)
                    .getServer()
                    .getAddress(UtilTool.getToken(), id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                    .subscribe(new Observer<BaseInfo>() {
                        @Override
                        public void onSubscribe(@NonNull Disposable d) {

                        }

                        @Override
                        public void onNext(@NonNull BaseInfo baseInfo) {
                            hideDialog();
                            if (baseInfo.getStatus() == 1)
                                callBack.send(baseInfo.getData().getAddress());
                            else
                                Toast.makeText(mCurrencyInOutActivity, baseInfo.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(@NonNull Throwable e) {
                            hideDialog();
                            Toast.makeText(mCurrencyInOutActivity, "网络连接失败，请稍后重试", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        } else {

            Toast.makeText(mCurrencyInOutActivity, mCurrencyInOutActivity.getString(R.string.toast_network_error), Toast.LENGTH_SHORT).show();

        }
    }

    //定义接口
    public interface CallBack {
        void send(String address);
    }
}
