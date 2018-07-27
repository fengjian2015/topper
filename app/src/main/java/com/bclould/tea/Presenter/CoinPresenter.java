package com.bclould.tea.Presenter;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.widget.Toast;

import com.bclould.tea.R;
import com.bclould.tea.base.MyApp;
import com.bclould.tea.model.BaseInfo;
import com.bclould.tea.model.CoinListInfo;
import com.bclould.tea.model.ExchangeOrderInfo;
import com.bclould.tea.model.StateInfo;
import com.bclould.tea.network.RetrofitUtil;
import com.bclould.tea.ui.activity.ExpectCoinActivity;
import com.bclould.tea.ui.widget.LoadingProgressDialog;
import com.bclould.tea.utils.UtilTool;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by GA on 2018/2/26.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class CoinPresenter {
    private final Context mContext;
    private LoadingProgressDialog mProgressDialog;

    public CoinPresenter(Context context) {
        mContext = context;
    }

    private void showDialog(String message) {
        if (mProgressDialog == null) {
            mProgressDialog = LoadingProgressDialog.createDialog(mContext);
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setMessage(message);
        }

        mProgressDialog.show();
    }

    private void hideDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

    public void getCoin() {
//            showDialog();
        RetrofitUtil.getInstance(mContext)
                .getServer()
                .AssetName(UtilTool.getToken())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                .subscribe(new Observer<CoinListInfo>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull CoinListInfo coinListInfo) {
                        if (coinListInfo.getStatus() == 1) {
                            MyApp.getInstance().mCoinList.clear();
                            MyApp.getInstance().mCoinList.addAll(coinListInfo.getData());
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void getCoinPrice(String name, final CallBack2 callBack2) {
        RetrofitUtil.getInstance(mContext)
                .getServer()
                .assetsValuation(UtilTool.getToken(), name)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                .subscribe(new Observer<BaseInfo>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(BaseInfo baseInfo) {
                        if (baseInfo.getStatus() == 1) {
                            callBack2.send(baseInfo.getData());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        callBack2.error();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void getState() {
        RetrofitUtil.getInstance(mContext)
                .getServer()
                .getCountryList(UtilTool.getToken())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                .subscribe(new Observer<StateInfo>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull StateInfo stateInfo) {
                        hideDialog();
                        if (stateInfo.getStatus() == 1) {
                            MyApp.getInstance().mStateList.clear();
                            MyApp.getInstance().mStateList.addAll(stateInfo.getData());
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void coinLists(String type, final CallBack callBack) {
        showDialog(mContext.getString(R.string.loading));
        RetrofitUtil.getInstance(mContext)
                .getServer()
                .coinLists(UtilTool.getToken(), type)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                .subscribe(new Observer<CoinListInfo>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull CoinListInfo coinListInfo) {
                        hideDialog();
                        if (coinListInfo.getStatus() == 1) {
                            callBack.send(coinListInfo.getData());
                        }
                        UtilTool.Log("日志1", coinListInfo.getMessage());
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        hideDialog();
                        callBack.error();
                        UtilTool.Log("日志1", e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void exchange(String price, String number, String market_coin_nam, String trade_coin_name, String password, final CallBack4 callBack4) {
        showDialog(mContext.getString(R.string.exchange_underway));
        RetrofitUtil.getInstance(mContext)
                .getServer()
                .exchangeSale(UtilTool.getToken(), price, number, market_coin_nam, trade_coin_name, password)
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
                            Toast.makeText(mContext, mContext.getString(R.string.exchange_succeed), Toast.LENGTH_SHORT).show();
                            callBack4.send();
                        } else {
                            Toast.makeText(mContext, baseInfo.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        hideDialog();
                        UtilTool.Log("日志1", e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void exchangeOrder(String market_coin_nam, String trade_coin_name, int page, int pageSize, final CallBack3 callBack3) {
        RetrofitUtil.getInstance(mContext)
                .getServer()
                .exchangeOrders(UtilTool.getToken(), market_coin_nam, trade_coin_name, page, pageSize)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                .subscribe(new Observer<ExchangeOrderInfo>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull ExchangeOrderInfo exchangeOrderInfo) {
                        if (exchangeOrderInfo.getStatus() == 1) {
                            callBack3.send(exchangeOrderInfo.getData());
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        callBack3.error();
                        UtilTool.Log("日志1", e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void hopeCoin(String content, String contact) {
        RetrofitUtil.getInstance(mContext)
                .getServer()
                .hopeCoin(UtilTool.getToken(), content, contact)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                .subscribe(new Observer<BaseInfo>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull BaseInfo baseInfo) {
                        if (baseInfo.getStatus() == 1) {
                            ExpectCoinActivity activity = (ExpectCoinActivity) mContext;
                            activity.finish();
                        }
                        Toast.makeText(mContext, baseInfo.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        UtilTool.Log("日志1", e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    //定义接口
    public interface CallBack {
        void send(List<CoinListInfo.DataBean> data);

        void error();
    }

    //定义接口
    public interface CallBack2 {
        void send(BaseInfo.DataBean data);

        void error();
    }

    //定义接口
    public interface CallBack3 {
        void send(List<ExchangeOrderInfo.DataBean> data);

        void error();
    }

    //定义接口
    public interface CallBack4 {
        void send();
    }
}
