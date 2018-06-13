package com.bclould.tocotalk.Presenter;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.widget.Toast;

import com.bclould.tocotalk.R;
import com.bclould.tocotalk.model.BaseInfo;
import com.bclould.tocotalk.model.IndividualInfo;
import com.bclould.tocotalk.network.RetrofitUtil;
import com.bclould.tocotalk.ui.widget.LoadingProgressDialog;
import com.bclould.tocotalk.utils.ToastShow;
import com.bclould.tocotalk.utils.UtilTool;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by GA on 2017/11/15.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class IndividualDetailsPresenter {

    private Context context;
    private LoadingProgressDialog mProgressDialog;

    public IndividualDetailsPresenter(Context context) {
        this.context = context;
    }

    private void showDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = LoadingProgressDialog.createDialog(context);
            mProgressDialog.setMessage(context.getString(R.string.loading));
        }

        mProgressDialog.show();
    }

    private void hideDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

    //獲取個人信息
    public void getIndividual(String toco_id,boolean isShow, final CallBack callBack) {
        if (UtilTool.isNetworkAvailable(context)) {
            if(isShow){
                showDialog();
            }
            RetrofitUtil.getInstance(context)
                    .getServer()
                    .getIndividual(UtilTool.getToken(), toco_id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                    .subscribe(new Observer<IndividualInfo>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(IndividualInfo individualInfo) {
                            callBack.send(individualInfo.getData());
                            hideDialog();
                        }

                        @Override
                        public void onError(Throwable e) {
                            hideDialog();
                            UtilTool.Log("信息", e.getMessage());
                            Toast.makeText(context, context.getString(R.string.toast_network_error), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        } else {
            Toast.makeText(context, context.getString(R.string.toast_network_error), Toast.LENGTH_SHORT).show();
        }
    }

    //修改備註
    public void getChangeRemark(String toco_id, String remark, final CallBack callBack) {
        if (UtilTool.isNetworkAvailable(context)) {
            showDialog();
            RetrofitUtil.getInstance(context)
                    .getServer()
                    .getChangeRemark(UtilTool.getToken(), toco_id, remark)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                    .subscribe(new Observer<IndividualInfo>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(IndividualInfo individualInfo) {
                            if (individualInfo.getStatus() == 1) {
                                callBack.send(individualInfo.getData());
                                Toast.makeText(context, context.getString(R.string.xg_succeed), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, context.getString(R.string.xg_error), Toast.LENGTH_SHORT).show();
                            }
                            hideDialog();
                        }

                        @Override
                        public void onError(Throwable e) {
                            hideDialog();
                            Toast.makeText(context, context.getString(R.string.toast_network_error), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        } else {
            Toast.makeText(context, context.getString(R.string.toast_network_error), Toast.LENGTH_SHORT).show();
        }
    }

    public void noLookTaDy(String name, int type, final CallBack2 callBack2) {
        if (UtilTool.isNetworkAvailable(context)) {
            RetrofitUtil.getInstance(context)
                    .getServer()
                    .noLookTaDy(UtilTool.getToken(), type, name)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                    .subscribe(new Observer<BaseInfo>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(BaseInfo baseInfo) {
                            if (baseInfo.getStatus() == 1) {
                                callBack2.send();
                            } else {
                                ToastShow.showToast2((Activity) context, baseInfo.getMessage());
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            hideDialog();
                            Toast.makeText(context, context.getString(R.string.toast_network_error), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        } else {
            Toast.makeText(context, context.getString(R.string.toast_network_error), Toast.LENGTH_SHORT).show();
        }
    }

    //定义接口
    public interface CallBack {
        void send(IndividualInfo.DataBean data);
    }

    //定义接口
    public interface CallBack2 {
        void send();
    }

}
