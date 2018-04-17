package com.bclould.tocotalk.Presenter;

import android.content.Intent;
import android.widget.Toast;

import com.bclould.tocotalk.R;
import com.bclould.tocotalk.model.BaseInfo;
import com.bclould.tocotalk.network.RetrofitUtil;
import com.bclould.tocotalk.ui.activity.AddOutCoinSiteActivity;
import com.bclould.tocotalk.ui.activity.GoogleVerificationActivity;
import com.bclould.tocotalk.ui.widget.LoadingProgressDialog;
import com.bclould.tocotalk.utils.UtilTool;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by GA on 2017/11/17.
 */

public class AddOutCoinSitePresenter {

    private final AddOutCoinSiteActivity mAddOutCoinSiteActivity;
    private LoadingProgressDialog mProgressDialog;

    public AddOutCoinSitePresenter(AddOutCoinSiteActivity addOutCoinSiteActivity) {
        mAddOutCoinSiteActivity = addOutCoinSiteActivity;
    }

    private void showDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = LoadingProgressDialog.createDialog(mAddOutCoinSiteActivity);
            mProgressDialog.setMessage(mAddOutCoinSiteActivity.getString(R.string.loading));
        }

        mProgressDialog.show();
    }

    private void hideDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }


    public void addCoinOutAddress(int id, String memo, String address, String vcode) {
        if (UtilTool.isNetworkAvailable(mAddOutCoinSiteActivity)) {
            showDialog();
            RetrofitUtil.getInstance(mAddOutCoinSiteActivity)
                    .getServer()
                    .addCoinOutAddress(UtilTool.getToken(), id, memo, address, vcode)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                    .subscribe(new Observer<BaseInfo>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(BaseInfo baseInfo) {
                            hideDialog();
                            Toast.makeText(mAddOutCoinSiteActivity, baseInfo.getMessage(), Toast.LENGTH_SHORT).show();
                            if (baseInfo.getStatus() == 1) {
                                mAddOutCoinSiteActivity.finish();
                            }
//                            if (baseInfo.getMessage().equals("请先绑定谷歌验证"))
                            if (baseInfo.getMessage().equals(mAddOutCoinSiteActivity.getString(R.string.binding_google_hint1)))
                                mAddOutCoinSiteActivity.startActivity(new Intent(mAddOutCoinSiteActivity, GoogleVerificationActivity.class));
                        }

                        @Override
                        public void onError(Throwable e) {
                            hideDialog();
                            Toast.makeText(mAddOutCoinSiteActivity, mAddOutCoinSiteActivity.getString(R.string.toast_network_error), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        } else {
            Toast.makeText(mAddOutCoinSiteActivity, mAddOutCoinSiteActivity.getString(R.string.toast_network_error), Toast.LENGTH_SHORT).show();
        }
    }
}
