package com.bclould.tea.Presenter;

import android.content.Context;
import android.widget.Toast;

import com.bclould.tea.R;
import com.bclould.tea.model.GrabRedInfo;
import com.bclould.tea.model.RedRecordInfo;
import com.bclould.tea.network.RetrofitUtil;
import com.bclould.tea.ui.widget.LoadingProgressDialog;
import com.bclould.tea.utils.UtilTool;

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
            mProgressDialog.setMessage(mContext.getString(R.string.loading));
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
//                            hideDialog();
//                            Toast.makeText(mContext, baseInfo.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Throwable e) {
//                            hideDialog();
                        callBack.error();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void signRpLog(int id, final CallBack2 callBack) {
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
                            callBack.send(baseInfo);
                        hideDialog();
//                            Toast.makeText(mContext, baseInfo.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Throwable e) {
                        hideDialog();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    //定义接口
    public interface CallBack {
        void send(RedRecordInfo.DataBean data);

        void error();
    }

    //定义接口
    public interface CallBack2 {
        void send(GrabRedInfo data);
    }
}
