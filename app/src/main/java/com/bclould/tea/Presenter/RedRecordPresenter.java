package com.bclould.tea.Presenter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.Toast;

import com.bclould.tea.R;
import com.bclould.tea.model.GrabRedInfo;
import com.bclould.tea.model.RpRecordInfo;
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

@SuppressLint("NewApi")
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
            mProgressDialog.hideDialog();
        }
    }

    public void log(String type, int page_id, int pageSize, String year, final CallBack callBack) {
        RetrofitUtil.getInstance(mContext)
                .getServer()
                .redPacketLog(UtilTool.getToken(), type, page_id, pageSize, year)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                .subscribe(new Observer<RpRecordInfo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(RpRecordInfo rpRecordInfo) {
                        if (rpRecordInfo.getStatus() == 1)
                            callBack.send(rpRecordInfo.getData());
                        callBack.finishRefresh();
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
                        callBack.send(baseInfo);
                        hideDialog();
                        if (baseInfo.getStatus() == 2)
                            Toast.makeText(mContext, baseInfo.getMessage(), Toast.LENGTH_SHORT).show();
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

        void error();

        void finishRefresh();

        void send(RpRecordInfo.DataBean data);
    }

    //定义接口
    public interface CallBack2 {
        void send(GrabRedInfo data);
    }
}
