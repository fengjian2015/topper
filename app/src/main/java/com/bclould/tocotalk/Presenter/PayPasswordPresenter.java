package com.bclould.tocotalk.Presenter;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.widget.Toast;

import com.bclould.tocotalk.R;
import com.bclould.tocotalk.model.BaseInfo;
import com.bclould.tocotalk.network.RetrofitUtil;
import com.bclould.tocotalk.ui.activity.GoogleVerificationActivity;
import com.bclould.tocotalk.ui.activity.PayPasswordActivity;
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
public class PayPasswordPresenter {

    private final PayPasswordActivity mPayPasswordActivity;
    private LoadingProgressDialog mProgressDialog;

    public PayPasswordPresenter(PayPasswordActivity payPasswordActivity) {
        mPayPasswordActivity = payPasswordActivity;
    }

    private void showDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = LoadingProgressDialog.createDialog(mPayPasswordActivity);
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

    //提交修改的密码
    public void submit(String vcode, String password, String password2) {
        if (UtilTool.isNetworkAvailable(mPayPasswordActivity)) {
            showDialog();
            RetrofitUtil.getInstance(mPayPasswordActivity)
                    .getServer()
                    .modifySecondPassword(UtilTool.getToken(), vcode, password, password2)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                    .subscribe(new Observer<BaseInfo>() {
                        @Override
                        public void onSubscribe(@NonNull Disposable d) {

                        }

                        @Override
                        public void onNext(@NonNull BaseInfo baseInfo) {
                            if (baseInfo.getStatus() == 1)
                                mPayPasswordActivity.finish();
                            if (baseInfo.getMessage().equals("请先绑定谷歌验证"))
                                mPayPasswordActivity.startActivity(new Intent(mPayPasswordActivity, GoogleVerificationActivity.class));
                            hideDialog();
                            Toast.makeText(mPayPasswordActivity, baseInfo.getMessage(), Toast.LENGTH_SHORT).show();

                        }

                        @Override
                        public void onError(@NonNull Throwable e) {
                            hideDialog();
                            Toast.makeText(mPayPasswordActivity, "网络连接失败，请稍后重试", Toast.LENGTH_SHORT).show();

                        }

                        @Override
                        public void onComplete() {

                        }
                    });


        } else {

            Toast.makeText(mPayPasswordActivity, mPayPasswordActivity.getString(R.string.toast_network_error), Toast.LENGTH_SHORT).show();
        }

    }

}
