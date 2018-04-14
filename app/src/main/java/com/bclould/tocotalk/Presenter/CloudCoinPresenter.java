package com.bclould.tocotalk.Presenter;

import android.app.Activity;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.widget.Toast;

import com.bclould.tocotalk.R;
import com.bclould.tocotalk.model.MyAssetsInfo;
import com.bclould.tocotalk.network.RetrofitUtil;
import com.bclould.tocotalk.ui.fragment.WalletFragment;
import com.bclould.tocotalk.ui.widget.LoadingProgressDialog;
import com.bclould.tocotalk.utils.UtilTool;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by GA on 2017/11/15.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class CloudCoinPresenter {

    private final WalletFragment mWalletFragment;
    private final Activity mActivity;
    private LoadingProgressDialog mProgressDialog;

    public CloudCoinPresenter(WalletFragment walletFragment, Activity activity) {
        mWalletFragment = walletFragment;
        mActivity = activity;
    }

    private void showDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = LoadingProgressDialog.createDialog(mActivity);
            mProgressDialog.setMessage(mActivity.getString(R.string.loading));
        }

        mProgressDialog.show();
    }

    private void hideDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

    public void getMyAssets() {

        if (UtilTool.isNetworkAvailable(mActivity)) {
            showDialog();
            RetrofitUtil.getInstance(mActivity)
                    .getServer()
                    .getMyAssets(UtilTool.getToken())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                    .subscribe(new Observer<MyAssetsInfo>() {
                        @Override
                        public void onSubscribe(@NonNull Disposable d) {

                        }

                        @Override
                        public void onNext(@NonNull MyAssetsInfo myAssetsInfo) {
                            if(myAssetsInfo.getStatus() == 1){
//                                mWalletFragment.setData(myAssetsInfo.getData());
                            }
                            hideDialog();
                        }

                        @Override
                        public void onError(@NonNull Throwable e) {
                            hideDialog();
                            Toast.makeText(mActivity, mActivity.getString(R.string.toast_network_error), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        } else {

            Toast.makeText(mActivity, mActivity.getString(R.string.toast_network_error), Toast.LENGTH_SHORT).show();

        }

    }
}
