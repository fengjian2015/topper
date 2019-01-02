package com.bclould.tea.Presenter;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.widget.Toast;

import com.bclould.tea.R;
import com.bclould.tea.model.OSSInfo;
import com.bclould.tea.model.InOutInfo;
import com.bclould.tea.model.TransferInfo;
import com.bclould.tea.model.UrlInfo;
import com.bclould.tea.network.RetrofitUtil;
import com.bclould.tea.ui.widget.LoadingProgressDialog;
import com.bclould.tea.utils.UtilTool;

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
            mProgressDialog.hideDialog();
        }
    }

    public void getTransfer(final CallBack callBack) {
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
                        callBack.error();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void getInOutData(String type, String id,int page,String keyword,String date, final CallBack2 callBack) {
        UtilTool.Log("充提幣", id);
        showDialog();
        RetrofitUtil.getInstance(mContext)
                .getServer()
                .coinOutLog(UtilTool.getToken(), type, id,page,keyword,date)
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
                        } else {
                            Toast.makeText(mContext, inOutInfo.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        hideDialog();
                        callBack.error();
                        UtilTool.Log("充提幣", e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void getSessionToken(final CallBack3 callBack3) {
        RetrofitUtil.getInstance(mContext)
                .getServer()
                .getSessionToken(UtilTool.getToken())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                .subscribe(new Observer<OSSInfo>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull OSSInfo OSSInfo) {
                        if (OSSInfo.getStatus() == 1) {
                            callBack3.send(OSSInfo.getData());
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        UtilTool.Log("oss", e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    //定义接口
    public interface CallBack {
        void send(List<TransferInfo.DataBean> data);

        void error();
    }

    //定义接口
    public interface CallBack2 {
        void send(List<InOutInfo.DataBean> data);

        void error();
    }

    //定义接口
    public interface CallBack3 {
        void send(OSSInfo.DataBean data);
    }

    //定义接口
    public interface CallBack4 {
        void send(UrlInfo.DataBean data);
    }
}
