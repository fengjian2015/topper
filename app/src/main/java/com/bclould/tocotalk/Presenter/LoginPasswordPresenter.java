package com.bclould.tocotalk.Presenter;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.widget.Toast;

import com.bclould.tocotalk.R;
import com.bclould.tocotalk.model.BaseInfo;
import com.bclould.tocotalk.network.RetrofitUtil;
import com.bclould.tocotalk.ui.activity.GoogleVerificationActivity;
import com.bclould.tocotalk.ui.activity.LoginPasswordActivity;
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
public class LoginPasswordPresenter {

    private LoginPasswordActivity mLoginPasswordActivity;
    private LoadingProgressDialog mProgressDialog;

    public LoginPasswordPresenter(LoginPasswordActivity loginPasswordActivity) {
        mLoginPasswordActivity = loginPasswordActivity;
    }

    private void showDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = LoadingProgressDialog.createDialog(mLoginPasswordActivity);
            mProgressDialog.setMessage(mLoginPasswordActivity.getString(R.string.loading));
        }

        mProgressDialog.show();
    }

    private void hideDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

    //提交修改的密码
    public void submit(String vcode, String password, String password2) {
        if (UtilTool.isNetworkAvailable(mLoginPasswordActivity)) {
            showDialog();
            RetrofitUtil.getInstance(mLoginPasswordActivity)
                    .getServer()
                    .modifyPassword(UtilTool.getToken(), vcode, password, password2)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                    .subscribe(new Observer<BaseInfo>() {
                        @Override
                        public void onSubscribe(@NonNull Disposable d) {

                        }

                        @Override
                        public void onNext(@NonNull BaseInfo baseInfo) {
                            if (baseInfo.getStatus() == 1)
                                mLoginPasswordActivity.finish();
                            if (baseInfo.getType() == 3)
                                mLoginPasswordActivity.startActivity(new Intent(mLoginPasswordActivity, GoogleVerificationActivity.class));
                            hideDialog();
                            Toast.makeText(mLoginPasswordActivity, baseInfo.getMessage(), Toast.LENGTH_SHORT).show();

                        }

                        @Override
                        public void onError(@NonNull Throwable e) {
                            hideDialog();
                            Toast.makeText(mLoginPasswordActivity, mLoginPasswordActivity.getString(R.string.toast_network_error), Toast.LENGTH_SHORT).show();

                        }

                        @Override
                        public void onComplete() {

                        }
                    });


        } else {

            Toast.makeText(mLoginPasswordActivity, mLoginPasswordActivity.getString(R.string.toast_network_error), Toast.LENGTH_SHORT).show();
        }

    }
}
