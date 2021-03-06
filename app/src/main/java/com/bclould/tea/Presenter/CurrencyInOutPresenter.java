package com.bclould.tea.Presenter;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.bclould.tea.R;
import com.bclould.tea.model.BaseInfo;
import com.bclould.tea.model.base.BaseMapInfo;
import com.bclould.tea.network.RetrofitUtil;
import com.bclould.tea.ui.activity.BankCardActivity;
import com.bclould.tea.ui.activity.BankCardBindingActivity;
import com.bclould.tea.ui.activity.GoogleVerificationActivity;
import com.bclould.tea.ui.activity.OutCoinActivity;
import com.bclould.tea.ui.activity.PayPasswordActivity;
import com.bclould.tea.ui.activity.PayPwSelectorActivity;
import com.bclould.tea.ui.activity.RealNameC1Activity;
import com.bclould.tea.ui.activity.TransferAccountsActivity;
import com.bclould.tea.ui.widget.DeleteCacheDialog;
import com.bclould.tea.ui.widget.LoadingProgressDialog;
import com.bclould.tea.utils.ActivityUtil;
import com.bclould.tea.utils.MessageEvent;
import com.bclould.tea.utils.UtilTool;

import org.greenrobot.eventbus.EventBus;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by GA on 2017/11/17.
 */

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
            mProgressDialog.hideDialog();
        }
    }

    public void coinOutAction(String id, String address, String count, String googleCode, String password, String mark) {

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
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void showSetPwDialog() {
        if(!ActivityUtil.isActivityOnTop(mContext))return;
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
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void inCoin(int id, final CallBack callBack) {
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

    public void check(String password) {
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
                                BankCardActivity activity = (BankCardActivity) mContext;
                                mContext.startActivity(new Intent(activity, BankCardBindingActivity.class));

                        } else {
                            Toast.makeText(mContext, baseInfo.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        hideDialog();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void outCoinDesc(int id, final CallBack callBack) {
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
                        callBack.error();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void coinNumber(int id, final CallBack1 callBack) {
        RetrofitUtil.getInstance(mContext)
                .getServer()
                .coinNumber(UtilTool.getToken(), id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                .subscribe(new Observer<BaseMapInfo>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull BaseMapInfo baseInfo) {
                        if (baseInfo.getStatus() == 1)
                            callBack.send(baseInfo);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        callBack.error();
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
    public interface CallBack1 {

        void send(BaseMapInfo data);

        void error();
    }
}
