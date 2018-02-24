package com.dashiji.biyun.Presenter;

import android.content.Context;
import android.widget.Toast;

import com.dashiji.biyun.R;
import com.dashiji.biyun.model.GrabRedInfo;
import com.dashiji.biyun.model.RedRecordInfo;
import com.dashiji.biyun.network.RetrofitUtil;
import com.dashiji.biyun.ui.widget.LoadingProgressDialog;
import com.dashiji.biyun.utils.UtilTool;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by GA on 2018/1/18.
 */

public class RedRecordPresenter {

    private final Context mContext;
    private LoadingProgressDialog mProgressDialog;

    public RedRecordPresenter(Context context) {
        mContext = context;
    }

    private void showDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = LoadingProgressDialog.createDialog(mContext);
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

    public void log(String type, final CallBack callBack) {
        if (UtilTool.isNetworkAvailable(mContext)) {
            showDialog();
            RetrofitUtil.getInstance(mContext)
                    .getServer()
                    .redPacketLog(UtilTool.getToken(), type)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                    .subscribe(new Observer<RedRecordInfo>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(RedRecordInfo baseInfo) {
                            if (baseInfo.getStatus() == 1)
                                callBack.send(baseInfo.getData());
                            hideDialog();
//                            Toast.makeText(mContext, baseInfo.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(Throwable e) {
                            hideDialog();
                            Toast.makeText(mContext, "网络连接失败，请稍后重试", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        } else {
            Toast.makeText(mContext, mContext.getString(R.string.toast_network_error), Toast.LENGTH_SHORT).show();
        }
    }

    public void signRpLog (int id, final CallBack2 callBack) {
        if (UtilTool.isNetworkAvailable(mContext)) {
            showDialog();
            RetrofitUtil.getInstance(mContext)
                    .getServer()
                    .singleRpLog(UtilTool.getToken(), id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                    .subscribe(new Observer<GrabRedInfo>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(GrabRedInfo baseInfo) {
                            if (baseInfo.getStatus() == 1)
                                callBack.send(baseInfo.getData());
                            hideDialog();
//                            Toast.makeText(mContext, baseInfo.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(Throwable e) {
                            hideDialog();
//                            Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        } else {
            Toast.makeText(mContext, mContext.getString(R.string.toast_network_error), Toast.LENGTH_SHORT).show();
        }
    }

    //定义接口
    public interface CallBack {
        void send(RedRecordInfo.DataBean data);
    }

    //定义接口
    public interface CallBack2 {
        void send(GrabRedInfo.DataBean data);
    }
}
