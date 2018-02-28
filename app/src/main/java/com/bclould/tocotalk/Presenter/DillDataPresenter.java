package com.bclould.tocotalk.Presenter;

import android.content.Context;
import android.widget.Toast;

import com.bclould.tocotalk.R;
import com.bclould.tocotalk.model.TransferInfo;
import com.bclould.tocotalk.network.RetrofitUtil;
import com.bclould.tocotalk.ui.widget.LoadingProgressDialog;
import com.bclould.tocotalk.utils.UtilTool;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by GA on 2018/2/28.
 */

public class DillDataPresenter {

    private final Context mContext;
    private LoadingProgressDialog mProgressDialog;

    public DillDataPresenter(Context context) {
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

    public void getDillList(final CallBack callBack) {
        if (UtilTool.isNetworkAvailable(mContext)) {
            showDialog();
            RetrofitUtil.getInstance(mContext)
                    .getServer()
                    .getTransfer(UtilTool.getToken())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                    .subscribe(new Observer<TransferInfo>() {
                        @Override
                        public void onSubscribe(@NonNull Disposable d) {

                        }

                        @Override
                        public void onNext(@NonNull TransferInfo transferInfo) {
                            hideDialog();
                            if (transferInfo.getStatus() == 1) {
                                callBack.send(transferInfo.getData());
                            }
                        }

                        @Override
                        public void onError(@NonNull Throwable e) {
                            hideDialog();
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
        void send(List<TransferInfo.DataBean> data);
    }
}
