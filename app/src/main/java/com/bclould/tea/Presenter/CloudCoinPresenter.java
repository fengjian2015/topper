package com.bclould.tea.Presenter;

import android.app.Activity;
import com.bclould.tea.R;
import com.bclould.tea.model.MyAssetsInfo;
import com.bclould.tea.network.RetrofitUtil;
import com.bclould.tea.ui.fragment.WalletFragment;
import com.bclould.tea.ui.widget.LoadingProgressDialog;
import com.bclould.tea.utils.UtilTool;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by GA on 2017/11/15.
 */

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
            mProgressDialog.hideDialog();
        }
    }

    public void getMyAssets() {
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
                        if (myAssetsInfo.getStatus() == 1) {
//                                mWalletFragment.setData(myAssetsInfo.getData());
                        }
                        hideDialog();
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
}
