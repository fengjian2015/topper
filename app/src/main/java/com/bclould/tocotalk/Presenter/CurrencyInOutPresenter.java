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
import com.bclould.tocotalk.network.RetrofitUtil;
import com.bclould.tocotalk.ui.activity.BankCardActivity;
import com.bclould.tocotalk.ui.activity.BankCardBindingActivity;
import com.bclould.tocotalk.ui.activity.GoogleVerificationActivity;
import com.bclould.tocotalk.ui.activity.OutCoinActivity;
import com.bclould.tocotalk.ui.activity.PayPasswordActivity;
import com.bclould.tocotalk.ui.activity.PayPwSelectorActivity;
import com.bclould.tocotalk.ui.activity.RealNameC1Activity;
import com.bclould.tocotalk.ui.activity.TransferAccountsActivity;
import com.bclould.tocotalk.ui.widget.DeleteCacheDialog;
import com.bclould.tocotalk.ui.widget.LoadingProgressDialog;
import com.bclould.tocotalk.utils.MessageEvent;
import com.bclould.tocotalk.utils.UtilTool;

import org.greenrobot.eventbus.EventBus;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by GA on 2017/11/17.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class CurrencyInOutPresenter {

    private final Context mContext;
    private LoadingProgressDialog mProgressDialog;

    public CurrencyInOutPresenter(Context context) {
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

    public void coinOutAction(String id, String address, String count, String googleCode, String password, String mark) {

        if (UtilTool.isNetworkAvailable(mContext)) {
            showDialog();
            RetrofitUtil.getInstance(mContext)
                    .getServer()
                    .coinOutAction(UtilTool.getToken(), id, address, count, googleCode, password, mark)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                    .subscribe(new Observer<BaseInfo>() {
                        @Override
                        public void onSubscribe(@NonNull Disposable d) {

                        }

                        @Override
                        public void onNext(@NonNull BaseInfo baseInfo) {
                            if (baseInfo.getStatus() == 1) {
                                EventBus.getDefault().post(new MessageEvent(mContext.getString(R.string.transfer)));
                                OutCoinActivity activity = (OutCoinActivity) mContext;
                                activity.finish();
                            } else if (baseInfo.getType() == 4) {
                                showSetPwDialog();
                            } else if (baseInfo.getType() == 3) {
                                mContext.startActivity(new Intent(mContext, GoogleVerificationActivity.class));
                            } else if (baseInfo.getType() == 6) {
                                OutCoinActivity activity = (OutCoinActivity) mContext;
                                activity.showHintDialog();
                            } else if (baseInfo.getMessage().equals(mContext.getString(R.string.real_name_authentication_hint))) {
                                showHintDialog();
                            }
                            hideDialog();
                            Toast.makeText(mContext, baseInfo.getMessage(), Toast.LENGTH_SHORT).show();
                            UtilTool.Log(this.getClass().getName(), baseInfo.getMessage());
                        }

                        @Override
                        public void onError(@NonNull Throwable e) {
                            hideDialog();
                            UtilTool.Log(this.getClass().getName(), e.getMessage());
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

    private void showSetPwDialog() {
        final DeleteCacheDialog deleteCacheDialog = new DeleteCacheDialog(R.layout.dialog_delete_cache, mContext, R.style.dialog);
        deleteCacheDialog.show();
        deleteCacheDialog.setTitle(mContext.getString(R.string.set_pay_pw_hint));
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

    public void transfer(String coinName, String email, double count, String google, String payPassword) {
        if (UtilTool.isNetworkAvailable(mContext)) {
            showDialog();
            RetrofitUtil.getInstance(mContext)
                    .getServer()
                    .transfer(UtilTool.getToken(), coinName, email, count, google, payPassword)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                    .subscribe(new Observer<BaseInfo>() {
                        @Override
                        public void onSubscribe(@NonNull Disposable d) {

                        }

                        @Override
                        public void onNext(@NonNull BaseInfo baseInfo) {
                            hideDialog();
                            if (baseInfo.getStatus() == 1) {
                                EventBus.getDefault().post(new MessageEvent(mContext.getString(R.string.transfer)));
                                TransferAccountsActivity activity = (TransferAccountsActivity) mContext;
                                activity.finish();
                            } else if (baseInfo.getMessage().equals(mContext.getString(R.string.binding_google_hint1))) {
                                mContext.startActivity(new Intent(mContext, GoogleVerificationActivity.class));
                            } else if (baseInfo.getType() == 4) {
                                showSetPwDialog();
                            } else if (baseInfo.getType() == 6) {
                                TransferAccountsActivity activity = (TransferAccountsActivity) mContext;
                                activity.showHintDialog();
                            } else if (baseInfo.getMessage().equals(mContext.getString(R.string.real_name_authentication_hint))) {
                                showHintDialog();
                            }
                            Toast.makeText(mContext, baseInfo.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(@NonNull Throwable e) {
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

    public void inCoin(int id, final CallBack callBack) {
        if (UtilTool.isNetworkAvailable(mContext)) {
            showDialog();
            RetrofitUtil.getInstance(mContext)
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
                                callBack.send(baseInfo.getData());
                        }

                        @Override
                        public void onError(@NonNull Throwable e) {
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

    private void showHintDialog() {
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

    public void check(String password) {
        if (UtilTool.isNetworkAvailable(mContext)) {
            showDialog();
            RetrofitUtil.getInstance(mContext)
                    .getServer()
                    .verifySecondPassword(UtilTool.getToken(), password)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                    .subscribe(new Observer<BaseInfo>() {
                        @Override
                        public void onSubscribe(@NonNull Disposable d) {

                        }

                        @Override
                        public void onNext(@NonNull BaseInfo baseInfo) {
                            hideDialog();
                            if (baseInfo.getType() == 4) {
                                showSetPwDialog();
                            } else if (baseInfo.getType() == 6) {
                                if (mContext instanceof BankCardActivity) {
                                    BankCardActivity activity = (BankCardActivity) mContext;
                                    activity.showHintDialog();
                                } else {
                                    PayPwSelectorActivity activity = (PayPwSelectorActivity) mContext;
                                    activity.showHintDialog();
                                }
                            } else if (baseInfo.getStatus() == 1) {
                                if (mContext instanceof BankCardActivity) {
                                    BankCardActivity activity = (BankCardActivity) mContext;
                                    mContext.startActivity(new Intent(activity, BankCardBindingActivity.class));
                                } else {
                                    PayPwSelectorActivity activity = (PayPwSelectorActivity) mContext;
                                    activity.setData();
                                }
                            } else {
                                Toast.makeText(mContext, baseInfo.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onError(@NonNull Throwable e) {
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

    public void outCoinDesc(int id, final CallBack callBack) {
        if (UtilTool.isNetworkAvailable(mContext)) {
            showDialog();
            RetrofitUtil.getInstance(mContext)
                    .getServer()
                    .outCoinDesc(UtilTool.getToken(), id)
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
                                callBack.send(baseInfo.getData());
                        }

                        @Override
                        public void onError(@NonNull Throwable e) {
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

    //定义接口
    public interface CallBack {

        void send(BaseInfo.DataBean data);
    }
}
