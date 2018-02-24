package com.dashiji.biyun.Presenter;

import android.widget.Toast;

import com.dashiji.biyun.R;
import com.dashiji.biyun.model.BaseInfo;
import com.dashiji.biyun.model.GoogleInfo;
import com.dashiji.biyun.network.RetrofitUtil;
import com.dashiji.biyun.ui.activity.GoogleVerificationActivity;
import com.dashiji.biyun.ui.widget.LoadingProgressDialog;
import com.dashiji.biyun.utils.UtilTool;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by GA on 2017/11/29.
 */

public class GoogleVerificationPresenter {

    private final GoogleVerificationActivity mGoogleVerificationActivity;
    private LoadingProgressDialog mProgressDialog;

    public GoogleVerificationPresenter(GoogleVerificationActivity googleVerificationActivity) {
        mGoogleVerificationActivity = googleVerificationActivity;
    }

    private void showDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = LoadingProgressDialog.createDialog(mGoogleVerificationActivity);
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

    public void getGoogleKey() {
        if (UtilTool.isNetworkAvailable(mGoogleVerificationActivity)) {
            showDialog();
            RetrofitUtil.getInstance(mGoogleVerificationActivity)
                    .getServer()
                    .getGoogleKey(UtilTool.getToken())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                    .subscribe(new Observer<GoogleInfo>() {
                        @Override
                        public void onSubscribe(@NonNull Disposable d) {

                        }

                        @Override
                        public void onNext(@NonNull GoogleInfo googleInfo) {
                            hideDialog();
                            mGoogleVerificationActivity.setData(googleInfo);
                        }

                        @Override
                        public void onError(@NonNull Throwable e) {
                            hideDialog();
                            Toast.makeText(mGoogleVerificationActivity, "网络连接失败，请稍后重试", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        } else {
            Toast.makeText(mGoogleVerificationActivity, mGoogleVerificationActivity.getString(R.string.toast_network_error), Toast.LENGTH_SHORT).show();
        }
    }

    public void bindGoogle(String googleCode) {
        if (UtilTool.isNetworkAvailable(mGoogleVerificationActivity)) {
            showDialog();
            RetrofitUtil.getInstance(mGoogleVerificationActivity)
                    .getServer()
                    .bindGoogle(UtilTool.getToken(), googleCode)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                    .subscribe(new Observer<BaseInfo>() {
                        @Override
                        public void onSubscribe(@NonNull Disposable d) {

                        }

                        @Override
                        public void onNext(@NonNull BaseInfo baseInfo) {
                            if (baseInfo.getStatus() == 1)
                                mGoogleVerificationActivity.finish();
                            hideDialog();
                            Toast.makeText(mGoogleVerificationActivity, baseInfo.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(@NonNull Throwable e) {
                            hideDialog();
                            Toast.makeText(mGoogleVerificationActivity, "网络连接失败，请稍后重试", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        } else {
            Toast.makeText(mGoogleVerificationActivity, mGoogleVerificationActivity.getString(R.string.toast_network_error), Toast.LENGTH_SHORT).show();
        }
    }
}
