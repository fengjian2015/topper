package com.bclould.tocotalk.Presenter;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.bclould.tocotalk.R;
import com.bclould.tocotalk.model.BaseInfo;
import com.bclould.tocotalk.model.RedRecordInfo;
import com.bclould.tocotalk.network.RetrofitUtil;
import com.bclould.tocotalk.ui.activity.PayPasswordActivity;
import com.bclould.tocotalk.ui.activity.PushBuyingActivity;
import com.bclould.tocotalk.ui.widget.DeleteCacheDialog;
import com.bclould.tocotalk.ui.widget.LoadingProgressDialog;
import com.bclould.tocotalk.utils.UtilTool;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by GA on 2018/1/19.
 */

public class PushBuyingPresenter {
    private final PushBuyingActivity mPushBuyingActivity;
    private LoadingProgressDialog mProgressDialog;

    public PushBuyingPresenter(PushBuyingActivity pushBuyingActivity) {
        mPushBuyingActivity = pushBuyingActivity;
    }

    private void showDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = LoadingProgressDialog.createDialog(mPushBuyingActivity);
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

    public void pushing(int type, String coin, String state, String legalTender, String price, String count, String paymentTime, String payment, String minLimit, String maxLimit, String remark, String password) {
        double priced = Double.parseDouble(price);
        double countd = Double.parseDouble(count);
        double mind = Double.parseDouble(minLimit);
        double maxd = Double.parseDouble(maxLimit);
        String s = paymentTime.substring(0, paymentTime.indexOf("分"));
        int time = Integer.parseInt(s);

        if (UtilTool.isNetworkAvailable(mPushBuyingActivity)) {
            showDialog();
            RetrofitUtil.getInstance(mPushBuyingActivity)
                    .getServer()
                    .publishDeal(UtilTool.getToken(), type, coin, state, legalTender, 0, priced, countd, time, payment, mind, maxd, remark, password)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                    .subscribe(new Observer<BaseInfo>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(BaseInfo baseInfo) {
                            if (baseInfo.getStatus() == 1) {
                                mPushBuyingActivity.finish();
                            } else if (baseInfo.getMessage().equals("尚未设置交易密码")) {
                                showSetPwDialog();
                            }
                            hideDialog();
                            Toast.makeText(mPushBuyingActivity, baseInfo.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(Throwable e) {
                            hideDialog();
                            Toast.makeText(mPushBuyingActivity, "网络连接失败，请稍后重试", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        } else {
            Toast.makeText(mPushBuyingActivity, mPushBuyingActivity.getString(R.string.toast_network_error), Toast.LENGTH_SHORT).show();
        }
    }

    private void showSetPwDialog() {
        final DeleteCacheDialog deleteCacheDialog = new DeleteCacheDialog(R.layout.dialog_set_pw, mPushBuyingActivity);
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
                mPushBuyingActivity.startActivity(new Intent(mPushBuyingActivity, PayPasswordActivity.class));
            }
        });
    }

    public void getCoinPrice(String name) {

    }

    //定义接口
    public interface CallBack {
        void send(RedRecordInfo.DataBean data);
    }
}
