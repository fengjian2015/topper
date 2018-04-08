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
import com.bclould.tocotalk.model.ModeOfPaymentInfo;
import com.bclould.tocotalk.model.RedRecordInfo;
import com.bclould.tocotalk.network.RetrofitUtil;
import com.bclould.tocotalk.ui.activity.BankCardActivity;
import com.bclould.tocotalk.ui.activity.PayPasswordActivity;
import com.bclould.tocotalk.ui.activity.PushBuyingActivity;
import com.bclould.tocotalk.ui.widget.DeleteCacheDialog;
import com.bclould.tocotalk.ui.widget.LoadingProgressDialog;
import com.bclould.tocotalk.utils.MessageEvent;
import com.bclould.tocotalk.utils.UtilTool;

import org.greenrobot.eventbus.EventBus;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by GA on 2018/1/19.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class PushBuyingPresenter {
    private final Context mContext;
    private LoadingProgressDialog mProgressDialog;

    public PushBuyingPresenter(Context context) {
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

    public void pushing(int type, String coin, String state, String price, String count, String paymentTime, String payment, String minLimit, String maxLimit, String remark, String password) {
        double priced = Double.parseDouble(price);
        double countd = Double.parseDouble(count);
        double mind = Double.parseDouble(minLimit);
        double maxd = Double.parseDouble(maxLimit);
        String s = paymentTime.substring(0, paymentTime.indexOf("分"));
        int time = Integer.parseInt(s);

        if (UtilTool.isNetworkAvailable(mContext)) {
            showDialog();
            RetrofitUtil.getInstance(mContext)
                    .getServer()
                    .publishDeal(UtilTool.getToken(), type, coin, state, "CNY", priced, countd, time, payment, mind, maxd, remark, password, "1")
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                    .subscribe(new Observer<BaseInfo>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(BaseInfo baseInfo) {
                            if (baseInfo.getStatus() == 1) {
                                PushBuyingActivity activity = (PushBuyingActivity) mContext;
                                activity.finish();
                                EventBus.getDefault().post(new MessageEvent("发布交易"));
                            } else if (baseInfo.getMessage().equals("尚未设置交易密码")) {
                                showHintDialog(1);
                            } else if (baseInfo.getMessage().equals("交易密码不正确")) {
                                PushBuyingActivity activity = (PushBuyingActivity) mContext;
                                activity.showHintDialog();
                            } else if (baseInfo.getMessage().equals("请先绑定银行卡")) {
                                showHintDialog(0);
                            }
                            hideDialog();
                            UtilTool.Log("PushBuyingPresenter", baseInfo.getMessage());
                            Toast.makeText(mContext, baseInfo.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(Throwable e) {
                            hideDialog();
                            Toast.makeText(mContext, "网络连接失败，请稍后重试", Toast.LENGTH_SHORT).show();
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
                deleteCacheDialog.setTitle("请先绑定银行卡！");
                break;
            case 1:
                deleteCacheDialog.setTitle("请先设置交易密码！");
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

    public void getModeOfPayment(final SubscribeCoinPresenter.CallBack2 callBack2) {
        if (UtilTool.isNetworkAvailable(mContext)) {
            RetrofitUtil.getInstance(mContext)
                    .getServer()
                    .getModeOfPayment(UtilTool.getToken())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                    .subscribe(new Observer<ModeOfPaymentInfo>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(ModeOfPaymentInfo modeOfPaymentInfo) {
                            if (modeOfPaymentInfo.getStatus() == 1) {
                                callBack2.send(modeOfPaymentInfo.getData());
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            hideDialog();
                            Toast.makeText(mContext, "网络连接失败，请稍后重试", Toast.LENGTH_SHORT).show();
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
        void send(RedRecordInfo.DataBean data);
    }

    //定义接口
    public interface CallBack2 {
        void send(RedRecordInfo.DataBean data);
    }


}
