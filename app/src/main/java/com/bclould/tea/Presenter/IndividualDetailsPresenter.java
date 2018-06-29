package com.bclould.tea.Presenter;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.widget.Toast;

import com.bclould.tea.R;
import com.bclould.tea.model.BaseInfo;
import com.bclould.tea.model.IndividualInfo;
import com.bclould.tea.network.RetrofitUtil;
import com.bclould.tea.ui.widget.LoadingProgressDialog;
import com.bclould.tea.utils.ActivityUtil;
import com.bclould.tea.utils.ToastShow;
import com.bclould.tea.utils.UtilTool;

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
    public void getIndividual(String toco_id, boolean isShow, final CallBack callBack) {
        if (isShow) {
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
                        if (context instanceof Activity && !ActivityUtil.isActivityOnTop((Activity) context))
                            return;
                        callBack.send(individualInfo.getData());
                        hideDialog();
                    }

                    @Override
                    public void onError(Throwable e) {
                        callBack.error();
                        if (context instanceof Activity && !ActivityUtil.isActivityOnTop((Activity) context))
                            return;
                        hideDialog();
                        UtilTool.Log("信息", e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    //修改備註
    public void getChangeRemark(String toco_id, String remark, final CallBack callBack) {
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
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void noLookTaDy(String name, int type, final CallBack2 callBack2) {
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
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    //定义接口
    public interface CallBack {
        void send(IndividualInfo.DataBean data);

        void error();
    }

    //定义接口
    public interface CallBack2 {
        void send();
    }

}
