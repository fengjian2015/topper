package com.dashiji.biyun.Presenter;

import android.app.Activity;
import android.widget.Toast;

import com.dashiji.biyun.R;
import com.dashiji.biyun.model.MyAssetsInfo;
import com.dashiji.biyun.network.RetrofitUtil;
import com.dashiji.biyun.ui.fragment.CloudCoinFragment;
import com.dashiji.biyun.ui.widget.LoadingProgressDialog;
import com.dashiji.biyun.utils.UtilTool;

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

public class CloudCoinPresenter {

    private final CloudCoinFragment mCloudCoinFragment;
    private final Activity mActivity;
    private LoadingProgressDialog mProgressDialog;

    public CloudCoinPresenter(CloudCoinFragment cloudCoinFragment, Activity activity) {
        mCloudCoinFragment = cloudCoinFragment;
        mActivity = activity;
    }

    private void showDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = LoadingProgressDialog.createDialog(mActivity);
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
                            if (myAssetsInfo != null) {
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
                                mCloudCoinFragment.setData(ltcBeanList);
                            }
                            hideDialog();
                        }

                        @Override
                        public void onError(@NonNull Throwable e) {
                            hideDialog();
                            /*if (e.getMessage().equals("HTTP 401 Unauthorized")) {
                                MyApp.getInstance().exit();
                                mActivity.finish();
                                MySharedPreferences.getInstance().setString(TOKEN, "");
                                mActivity.startActivity(new Intent(mActivity, LoginActivity.class));
                            }*/
                            Toast.makeText(mActivity, "网络连接错误，请稍后重试", Toast.LENGTH_SHORT).show();
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
