package com.bclould.tocotalk.Presenter;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.widget.Toast;

import com.bclould.tocotalk.R;
import com.bclould.tocotalk.base.MyApp;
import com.bclould.tocotalk.model.CoinInfo;
import com.bclould.tocotalk.model.StateInfo;
import com.bclould.tocotalk.network.RetrofitUtil;
import com.bclould.tocotalk.ui.widget.LoadingProgressDialog;
import com.bclould.tocotalk.utils.UtilTool;

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

    public void getCoin() {
        if (UtilTool.isNetworkAvailable(mContext)) {
//            showDialog();
            RetrofitUtil.getInstance(mContext)
                    .getServer()
                    .AssetName(UtilTool.getToken())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                    .subscribe(new Observer<CoinInfo>() {
                        @Override
                        public void onSubscribe(@NonNull Disposable d) {

                        }

                        @Override
                        public void onNext(@NonNull CoinInfo coinInfo) {
                            if (coinInfo.getStatus() == 1) {
                                MyApp.getInstance().mCoinList.addAll(coinInfo.getData());
                            }
                        }

                        @Override
                        public void onError(@NonNull Throwable e) {
                            UtilTool.Log("日志1", e.getMessage());
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        } else {

            Toast.makeText(mContext, mContext.getString(R.string.toast_network_error), Toast.LENGTH_SHORT).show();

        }
    }

    public void getState() {
        if (UtilTool.isNetworkAvailable(mContext)) {
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
                                MyApp.getInstance().mStateList.addAll(stateInfo.getData());
                            }
                        }

                        @Override
                        public void onError(@NonNull Throwable e) {
                            UtilTool.Log("日志1", e.getMessage());
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        } else {

            Toast.makeText(mContext, mContext.getString(R.string.toast_network_error), Toast.LENGTH_SHORT).show();

        }
    }
}
