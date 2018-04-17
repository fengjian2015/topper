package com.bclould.tocotalk.Presenter;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.widget.Toast;

import com.bclould.tocotalk.R;
import com.bclould.tocotalk.model.AwsInfo;
import com.bclould.tocotalk.model.InOutInfo;
import com.bclould.tocotalk.model.TransferInfo;
import com.bclould.tocotalk.model.UrlInfo;
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

@RequiresApi(api = Build.VERSION_CODES.N)
public class DillDataPresenter {

    private final Context mContext;
    private LoadingProgressDialog mProgressDialog;

    public DillDataPresenter(Context context) {
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

    public void getTransfer(final CallBack callBack) {
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
                            Toast.makeText(mContext, mContext.getString(R.string.toast_network_error), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        } else {
            Toast.makeText(mContext, mContext.getString(R.string.toast_network_error), Toast.LENGTH_SHORT).show();
        }
    }

    public void getInOutData(String type, String id, final CallBack2 callBack) {
        UtilTool.Log("充提幣", id);
        if (UtilTool.isNetworkAvailable(mContext)) {
            showDialog();
            RetrofitUtil.getInstance(mContext)
                    .getServer()
                    .coinOutLog(UtilTool.getToken(), type, id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                    .subscribe(new Observer<InOutInfo>() {
                        @Override
                        public void onSubscribe(@NonNull Disposable d) {

                        }

                        @Override
                        public void onNext(@NonNull InOutInfo inOutInfo) {
                            hideDialog();
                            if (inOutInfo.getStatus() == 1) {
                                callBack.send(inOutInfo.getData());
                            }else {
                                Toast.makeText(mContext, inOutInfo.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onError(@NonNull Throwable e) {
                            hideDialog();
                            UtilTool.Log("充提幣", e.getMessage());
                            Toast.makeText(mContext, mContext.getString(R.string.toast_network_error), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        } else {
            Toast.makeText(mContext, mContext.getString(R.string.toast_network_error), Toast.LENGTH_SHORT).show();
        }
    }

    public void getSessionToken(final CallBack3 callBack3) {
        if (UtilTool.isNetworkAvailable(mContext)) {
            RetrofitUtil.getInstance(mContext)
                    .getServer()
                    .getSessionToken(UtilTool.getToken())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                    .subscribe(new Observer<AwsInfo>() {
                        @Override
                        public void onSubscribe(@NonNull Disposable d) {

                        }

                        @Override
                        public void onNext(@NonNull AwsInfo awsInfo) {
                            if (awsInfo.getStatus() == 1) {
                                callBack3.send(awsInfo.getData());
                                UtilTool.Log("日志", awsInfo.getData().getAccessKeyId());
                                UtilTool.Log("日志", awsInfo.getData().getSecretAccessKey());
                                UtilTool.Log("日志", awsInfo.getData().getSessionToken());
                            }
                        }

                        @Override
                        public void onError(@NonNull Throwable e) {
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

    //定义接口
    public interface CallBack2 {
        void send(List<InOutInfo.DataBean> data);
    }

    //定义接口
    public interface CallBack3 {
        void send(AwsInfo.DataBean data);
    }

    //定义接口
    public interface CallBack4 {
        void send(UrlInfo.DataBean data);
    }
}
