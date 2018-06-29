package com.bclould.tea.Presenter;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.widget.Toast;

import com.bclould.tea.R;
import com.bclould.tea.model.BaseInfo;
import com.bclould.tea.network.RetrofitUtil;
import com.bclould.tea.ui.activity.GoogleVerificationActivity;
import com.bclould.tea.ui.activity.PayPasswordActivity;
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
            mProgressDialog.setMessage(mPayPasswordActivity.getString(R.string.loading));
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
                        if (baseInfo.getType() == 3)
                            mPayPasswordActivity.startActivity(new Intent(mPayPasswordActivity, GoogleVerificationActivity.class));
                        hideDialog();
                        Toast.makeText(mPayPasswordActivity, baseInfo.getMessage(), Toast.LENGTH_SHORT).show();

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
