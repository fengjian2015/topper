package com.dashiji.biyun.Presenter;

import android.widget.Button;
import android.widget.Toast;

import com.dashiji.biyun.R;
import com.dashiji.biyun.model.BaseInfo;
import com.dashiji.biyun.model.MyAssetsInfo;
import com.dashiji.biyun.network.RetrofitUtil;
import com.dashiji.biyun.ui.activity.SubscribeCoinActivity;
import com.dashiji.biyun.ui.widget.LoadingProgressDialog;
import com.dashiji.biyun.utils.MessageEvent;
import com.dashiji.biyun.utils.UtilTool;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by GA on 2017/11/15.
 */

public class SubscribeCoinPresenter {

    private final SubscribeCoinActivity mSubscribeCoinActivity;
    private LoadingProgressDialog mProgressDialog;

    public SubscribeCoinPresenter(SubscribeCoinActivity subscribeCoinActivity) {
        mSubscribeCoinActivity = subscribeCoinActivity;
    }

    private void showDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = LoadingProgressDialog.createDialog(mSubscribeCoinActivity);
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

    public void getMyAssets() {

            showDialog();
            RetrofitUtil.getInstance(mSubscribeCoinActivity)
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
                            List<MyAssetsInfo.LtcBean> ltcBeanList = new ArrayList<>();
                            ltcBeanList.add(myAssetsInfo.getTpc());
                            ltcBeanList.add(myAssetsInfo.getLtc());
                            ltcBeanList.add(myAssetsInfo.getDoge());
                            ltcBeanList.add(myAssetsInfo.getZec());
                            ltcBeanList.add(myAssetsInfo.getLsk());
                            ltcBeanList.add(myAssetsInfo.getMaid());
                            ltcBeanList.add(myAssetsInfo.getShc());
                            ltcBeanList.add(myAssetsInfo.getBtc());
                            ltcBeanList.add(myAssetsInfo.getAns());
                            mSubscribeCoinActivity.setData(ltcBeanList);
                            hideDialog();
                        }

                        @Override
                        public void onError(@NonNull Throwable e) {
                            hideDialog();
                            Toast.makeText(mSubscribeCoinActivity, "网络连接失败，请稍后重试", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onComplete() {

                        }
                    });


    }

    public void unSubscribeAsset(int id, final Button btcAdd) {
        if (UtilTool.isNetworkAvailable(mSubscribeCoinActivity)) {
            showDialog();
            RetrofitUtil.getInstance(mSubscribeCoinActivity)
                    .getServer()
                    .unSubscribeAsset(UtilTool.getToken(), id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                    .subscribe(new Observer<BaseInfo>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(BaseInfo baseInfo) {
                            hideDialog();
                            Toast.makeText(mSubscribeCoinActivity, baseInfo.getMessage(), Toast.LENGTH_SHORT).show();
                            if (baseInfo.getMessage().contains("取消订阅成功")) {
                                mSubscribeCoinActivity.setUi(false, btcAdd);
                                EventBus.getDefault().post(new MessageEvent("取消或者订阅成功"));
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            hideDialog();
                            Toast.makeText(mSubscribeCoinActivity, "网络连接失败，请稍后重试", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        } else {

            Toast.makeText(mSubscribeCoinActivity, mSubscribeCoinActivity.getString(R.string.toast_network_error), Toast.LENGTH_SHORT).show();

        }
    }

    public void subscribeAsset(int id, final Button btcAdd) {
        if (UtilTool.isNetworkAvailable(mSubscribeCoinActivity)) {
            showDialog();
            RetrofitUtil.getInstance(mSubscribeCoinActivity)
                    .getServer()
                    .subscribeAsset(UtilTool.getToken(), id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                    .subscribe(new Observer<BaseInfo>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(BaseInfo baseInfo) {
                            hideDialog();
                            Toast.makeText(mSubscribeCoinActivity, baseInfo.getMessage(), Toast.LENGTH_SHORT).show();
                            if (baseInfo.getMessage().contains("订阅成功")) {
                                mSubscribeCoinActivity.setUi(true, btcAdd);
                                EventBus.getDefault().post(new MessageEvent("取消或者订阅成功"));
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            hideDialog();
                            Toast.makeText(mSubscribeCoinActivity, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        } else {

            Toast.makeText(mSubscribeCoinActivity, mSubscribeCoinActivity.getString(R.string.toast_network_error), Toast.LENGTH_SHORT).show();

        }
    }
}
