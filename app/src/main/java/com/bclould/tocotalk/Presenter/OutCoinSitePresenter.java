package com.bclould.tocotalk.Presenter;

import android.widget.Toast;

import com.bclould.tocotalk.R;
import com.bclould.tocotalk.model.BaseInfo;
import com.bclould.tocotalk.model.OutCoinSiteInfo;
import com.bclould.tocotalk.network.RetrofitUtil;
import com.bclould.tocotalk.ui.activity.OutCoinSiteActivity;
import com.bclould.tocotalk.ui.widget.LoadingProgressDialog;
import com.bclould.tocotalk.utils.UtilTool;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by GA on 2017/11/17.
 */

public class OutCoinSitePresenter {

    private final OutCoinSiteActivity mOutCoinSiteActivity;
    private LoadingProgressDialog mProgressDialog;

    public OutCoinSitePresenter(OutCoinSiteActivity outCoinSiteActivity) {
        mOutCoinSiteActivity = outCoinSiteActivity;
    }

    private void showDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = LoadingProgressDialog.createDialog(mOutCoinSiteActivity);
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

    public void getSite(int id) {

        if (UtilTool.isNetworkAvailable(mOutCoinSiteActivity)) {

            showDialog();

            RetrofitUtil.getInstance(mOutCoinSiteActivity)
                    .getServer()
                    .withdrawalAddresses(UtilTool.getToken(), id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                    .subscribe(new Observer<OutCoinSiteInfo>() {
                        @Override
                        public void onSubscribe(@NonNull Disposable d) {

                        }

                        @Override
                        public void onNext(@NonNull OutCoinSiteInfo outCoinSiteInfo) {

                            List<OutCoinSiteInfo.MessageBean> siteBeanList = outCoinSiteInfo.getMessage();
                            mOutCoinSiteActivity.setData(siteBeanList);
                            hideDialog();

                        }

                        @Override
                        public void onError(@NonNull Throwable e) {
                            hideDialog();
                            Toast.makeText(mOutCoinSiteActivity, "网络连接失败，请稍后重试", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        } else {
            Toast.makeText(mOutCoinSiteActivity, mOutCoinSiteActivity.getString(R.string.toast_network_error), Toast.LENGTH_SHORT).show();
        }
    }

    public void deleteSite(int id, int address_id) {
        if (UtilTool.isNetworkAvailable(mOutCoinSiteActivity)) {
            showDialog();
            RetrofitUtil.getInstance(mOutCoinSiteActivity)
                    .getServer()
                    .deleteCoinOutAddress(UtilTool.getToken(), id, address_id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                    .subscribe(new Observer<BaseInfo>() {
                        @Override
                        public void onSubscribe(@NonNull Disposable d) {

                        }

                        @Override
                        public void onNext(@NonNull BaseInfo baseInfo) {
                            Toast.makeText(mOutCoinSiteActivity, baseInfo.getMessage(), Toast.LENGTH_SHORT).show();
                            hideDialog();
                            if (baseInfo.getMessage().contains("删除成功！")) ;

                        }

                        @Override
                        public void onError(@NonNull Throwable e) {
                            hideDialog();
                            Toast.makeText(mOutCoinSiteActivity, "网络连接失败，请稍后重试", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        } else {
            Toast.makeText(mOutCoinSiteActivity, mOutCoinSiteActivity.getString(R.string.toast_network_error), Toast.LENGTH_SHORT).show();
        }

    }
}
