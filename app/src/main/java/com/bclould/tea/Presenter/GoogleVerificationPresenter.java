package com.bclould.tea.Presenter;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.widget.Toast;

import com.bclould.tea.R;
import com.bclould.tea.model.BaseInfo;
import com.bclould.tea.model.GoogleInfo;
import com.bclould.tea.network.RetrofitUtil;
import com.bclould.tea.ui.activity.GoogleVerificationActivity;
import com.bclould.tea.ui.widget.LoadingProgressDialog;
import com.bclould.tea.utils.UtilTool;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by GA on 2017/11/29.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class GoogleVerificationPresenter {

    private final GoogleVerificationActivity mGoogleVerificationActivity;
    private LoadingProgressDialog mProgressDialog;

    public GoogleVerificationPresenter(GoogleVerificationActivity googleVerificationActivity) {
        mGoogleVerificationActivity = googleVerificationActivity;
    }

    private void showDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = LoadingProgressDialog.createDialog(mGoogleVerificationActivity);
            mProgressDialog.setMessage(mGoogleVerificationActivity.getString(R.string.loading));
        }

        mProgressDialog.show();
    }

    private void hideDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

    public void getGoogleKey(final CallBack2 callBack2) {
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
                            callBack2.send(googleInfo);
                        }

                        @Override
                        public void onError(@NonNull Throwable e) {
                            hideDialog();
                            callBack2.error();
                            Toast.makeText(mGoogleVerificationActivity, mGoogleVerificationActivity.getString(R.string.toast_network_error), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        } else {
            callBack2.error();
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
                            Toast.makeText(mGoogleVerificationActivity, mGoogleVerificationActivity.getString(R.string.toast_network_error), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        } else {
            Toast.makeText(mGoogleVerificationActivity, mGoogleVerificationActivity.getString(R.string.toast_network_error), Toast.LENGTH_SHORT).show();
        }
    }

    public void unBinding(String vcode, final CallBack callBack) {
        if (UtilTool.isNetworkAvailable(mGoogleVerificationActivity)) {
            showDialog();
            RetrofitUtil.getInstance(mGoogleVerificationActivity)
                    .getServer()
                    .unBindGoogle(UtilTool.getToken(), vcode)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                    .subscribe(new Observer<BaseInfo>() {
                        @Override
                        public void onSubscribe(@NonNull Disposable d) {
                        }

                        @Override
                        public void onNext(@NonNull BaseInfo baseInfo) {
                            if (baseInfo.getStatus() == 1)
                                callBack.send();
                            hideDialog();
                            Toast.makeText(mGoogleVerificationActivity, baseInfo.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(@NonNull Throwable e) {
                            hideDialog();
                            Toast.makeText(mGoogleVerificationActivity, mGoogleVerificationActivity.getString(R.string.toast_network_error), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        } else {
            Toast.makeText(mGoogleVerificationActivity, mGoogleVerificationActivity.getString(R.string.toast_network_error), Toast.LENGTH_SHORT).show();
        }
    }

    //定义接口
    public interface CallBack {
        void send();
    }

    //定义接口
    public interface CallBack2 {
        void send(GoogleInfo googleInfo);
        void error();
    }
}
