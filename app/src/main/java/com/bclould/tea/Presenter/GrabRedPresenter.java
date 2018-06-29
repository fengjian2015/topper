package com.bclould.tea.Presenter;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.bclould.tea.R;
import com.bclould.tea.model.GrabRedInfo;
import com.bclould.tea.network.RetrofitUtil;
import com.bclould.tea.ui.widget.LoadingProgressDialog;
import com.bclould.tea.utils.UtilTool;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by GA on 2018/1/16.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class GrabRedPresenter {
    private final Context mContext;
    private LoadingProgressDialog mProgressDialog;

    public GrabRedPresenter(Context context) {
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

    public void grabRedPacket(int redId, final CallBack callBack) {
        showDialog();
        RetrofitUtil.getInstance(mContext)
                .getServer()
                .grabRedPacket(UtilTool.getToken(), redId)
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
        void send(GrabRedInfo info);
    }
}
