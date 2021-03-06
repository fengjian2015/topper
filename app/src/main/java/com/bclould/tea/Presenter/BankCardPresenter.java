package com.bclould.tea.Presenter;

import android.content.Context;
import android.widget.Toast;

import com.bclould.tea.R;
import com.bclould.tea.model.BankCardInfo;
import com.bclould.tea.model.BaseInfo;
import com.bclould.tea.model.CardListInfo;
import com.bclould.tea.network.RetrofitUtil;
import com.bclould.tea.ui.widget.LoadingProgressDialog;
import com.bclould.tea.utils.UtilTool;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by GA on 2018/3/21.
 */

public class BankCardPresenter {

    private final Context mContext;
    private LoadingProgressDialog mProgressDialog;

    public BankCardPresenter(Context context) {
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

    public void bankCardInfo(String cardNumber, int stateId, final CallBack callBack) {
        showDialog();
        RetrofitUtil.getInstance(mContext)
                .getServer()
                .bankCardInfo(UtilTool.getToken(), cardNumber, stateId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                .subscribe(new Observer<BankCardInfo>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull BankCardInfo bankCardInfo) {
                        hideDialog();
                        if (bankCardInfo.getStatus() == 1) {
                            callBack.send(bankCardInfo.getData());
                        } else {
                            Toast.makeText(mContext, bankCardInfo.getMessage(), Toast.LENGTH_SHORT).show();
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

    public void bindBankCard(String truename, String bank, String openingBank, String cardNumber, int state_id, final CallBack3 callBack3) {
        showDialog();
        RetrofitUtil.getInstance(mContext)
                .getServer()
                .bindBankCard(UtilTool.getToken(), truename, bank, openingBank, cardNumber, state_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                .subscribe(new Observer<BaseInfo>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull BaseInfo baseInfo) {
                        hideDialog();
                        callBack3.send(baseInfo);

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

    public void bankCardList(final CallBack2 callBack2) {
        RetrofitUtil.getInstance(mContext)
                .getServer()
                .bankCardList(UtilTool.getToken())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                .subscribe(new Observer<CardListInfo>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull CardListInfo cardListInfo) {
                        if (cardListInfo.getStatus() == 1) {
                            callBack2.send(cardListInfo.getData());
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        callBack2.error();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void unBindBankCard(int id, final CallBack3 callBack3) {
        showDialog();
        RetrofitUtil.getInstance(mContext)
                .getServer()
                .unBindBankCard(UtilTool.getToken(), id + "")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                .subscribe(new Observer<BaseInfo>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull BaseInfo baseInfo) {
                        hideDialog();
                        callBack3.send(baseInfo);
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

    public void setDefaultBankCard(int id, final CallBack3 callBack3) {
        RetrofitUtil.getInstance(mContext)
                .getServer()
                .setDefaultBankCard(UtilTool.getToken(), id + "")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                .subscribe(new Observer<BaseInfo>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull BaseInfo baseInfo) {
                        callBack3.send(baseInfo);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    //定义接口
    public interface CallBack {
        void send(BankCardInfo.DataBean data);
    }

    //定义接口
    public interface CallBack3 {
        void send(BaseInfo data);
    }


    //定义接口
    public interface CallBack2 {
        void send(List<CardListInfo.DataBean> data);

        void error();
    }
}
