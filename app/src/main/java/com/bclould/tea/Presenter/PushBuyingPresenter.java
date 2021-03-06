package com.bclould.tea.Presenter;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.bclould.tea.R;
import com.bclould.tea.model.BaseInfo;
import com.bclould.tea.model.ModeOfPaymentInfo;
import com.bclould.tea.network.RetrofitUtil;
import com.bclould.tea.ui.activity.BankCardActivity;
import com.bclould.tea.ui.activity.PayPasswordActivity;
import com.bclould.tea.ui.activity.PushBuyingActivity;
import com.bclould.tea.ui.activity.RealNameC1Activity;
import com.bclould.tea.ui.widget.DeleteCacheDialog;
import com.bclould.tea.ui.widget.LoadingProgressDialog;
import com.bclould.tea.utils.ActivityUtil;
import com.bclould.tea.utils.MessageEvent;
import com.bclould.tea.utils.MySharedPreferences;
import com.bclould.tea.utils.UtilTool;

import org.greenrobot.eventbus.EventBus;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.bclould.tea.Presenter.LoginPresenter.CURRENCY;

/**
 * Created by GA on 2018/1/19.
 */

public class PushBuyingPresenter {
    private final Context mContext;
    private LoadingProgressDialog mProgressDialog;

    public PushBuyingPresenter(Context context) {
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

    public void pushing(int type, String coin, int state_id, String price, String count, String paymentTime, String payment, String minLimit, String maxLimit, String remark, String password, String phoneNumber) {
        double priced = Double.parseDouble(price);
        double countd = Double.parseDouble(count);
        double mind = Double.parseDouble(minLimit);
        double maxd = Double.parseDouble(maxLimit);
        String s = paymentTime.substring(0, paymentTime.indexOf(mContext.getString(R.string.fen)));
        int time = Integer.parseInt(s);
        showDialog();
        RetrofitUtil.getInstance(mContext)
                .getServer()
                .publishDeal(UtilTool.getToken(), type, coin, state_id, MySharedPreferences.getInstance().getString(CURRENCY), priced, countd, time, 1, mind, maxd, remark, password, phoneNumber)
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
                            EventBus.getDefault().post(new MessageEvent(mContext.getString(R.string.publish_deal)));
                        } else if (baseInfo.getType() == 4) {
                            showHintDialog(1);
                        } else if (baseInfo.getType() == 6) {
                            PushBuyingActivity activity = (PushBuyingActivity) mContext;
                            activity.showHintDialog();
                        } else if (baseInfo.getType() == 2) {
                            showHintDialog(0);
                        } else if (baseInfo.getType() == 1) {
                            showHintDialog(2);
                        }
                        hideDialog();
                        UtilTool.Log("PushBuyingPresenter", baseInfo.getMessage());
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


    private void showHintDialog(final int type) {
        if(!ActivityUtil.isActivityOnTop(mContext))return;
        final DeleteCacheDialog deleteCacheDialog = new DeleteCacheDialog(R.layout.dialog_delete_cache, mContext, R.style.dialog);
        deleteCacheDialog.show();
        switch (type) {
            case 0:
                deleteCacheDialog.setTitle(mContext.getString(R.string.binding_bank_hint));
                break;
            case 1:
                deleteCacheDialog.setTitle(mContext.getString(R.string.set_pay_pw_hint));
                break;
            case 2:
                deleteCacheDialog.setTitle(mContext.getString(R.string.real_name_authentication_hint));
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
                    case 2:
                        mContext.startActivity(new Intent(mContext, RealNameC1Activity.class));
                        break;
                }
            }
        });
    }

    public void getModeOfPayment(final SubscribeCoinPresenter.CallBack2 callBack2) {
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
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
