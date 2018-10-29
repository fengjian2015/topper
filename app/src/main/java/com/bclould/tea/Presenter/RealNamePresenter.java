package com.bclould.tea.Presenter;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.widget.Toast;

import com.bclould.tea.R;
import com.bclould.tea.model.BaseInfo;
import com.bclould.tea.model.QuestionInfo;
import com.bclould.tea.network.RetrofitUtil;
import com.bclould.tea.ui.widget.LoadingProgressDialog;
import com.bclould.tea.utils.UtilTool;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by GA on 2018/3/19.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class RealNamePresenter {

    private final Context mContext;
    private LoadingProgressDialog mProgressDialog;

    public RealNamePresenter(Context context) {
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

    public void realNameVerify(String name, String cardNumber, int id, String cardType, final CallBack callBack) {
        showDialog();
        RetrofitUtil.getInstance(mContext)
                .getServer()
                .realNameVerify(UtilTool.getToken(), name, cardNumber, id + "", cardType)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                .subscribe(new Observer<BaseInfo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(BaseInfo baseInfo) {
                        hideDialog();
                        if (baseInfo.getStatus() == 1) {
                            callBack.send(baseInfo.getStatus());
                        } else {
                            Toast.makeText(mContext, baseInfo.getMessage(), Toast.LENGTH_SHORT).show();
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

    public void realNameInfo(final CallBack2 callBack) {
        showDialog();
        RetrofitUtil.getInstance(mContext)
                .getServer()
                .realNameInfo(UtilTool.getToken())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                .subscribe(new Observer<BaseInfo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(BaseInfo baseInfo) {
                        hideDialog();
                        if (baseInfo.getStatus() == 1) {
                            callBack.send(baseInfo.getData().getType(), baseInfo.getData().getRemark());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        hideDialog();
                        UtilTool.Log("實名認證", e.getMessage());
                        callBack.error();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void UpImage(String keyList, final CallBack callBack2) {
        RetrofitUtil.getInstance(mContext)
                .getServer()
                .bindRealName(UtilTool.getToken(), keyList)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                .subscribe(new Observer<BaseInfo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(BaseInfo baseInfo) {
                        callBack2.send(baseInfo.getStatus());
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void UpFeedBackImage(String content, String keyList, final CallBack callBack) {
        RetrofitUtil.getInstance(mContext)
                .getServer()
                .feedbacks(UtilTool.getToken(), content, keyList)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                .subscribe(new Observer<BaseInfo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(BaseInfo baseInfo) {
                        callBack.send(baseInfo.getStatus());
                        UtilTool.Log("上传", baseInfo.getMessage());
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void getQuestionList(final CallBack3 callBack3) {
        RetrofitUtil.getInstance(mContext)
                .getServer()
                .getQuestionList(UtilTool.getToken())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                .subscribe(new Observer<QuestionInfo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(QuestionInfo questionInfo) {
                        if (questionInfo.getStatus() == 1) {
                            callBack3.send(questionInfo.getData());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        callBack3.error();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    //定义接口
    public interface CallBack {
        void send(int status);
    }

    //定义接口
    public interface CallBack2 {
        void send(int type, String mark);

        void error();
    }

    //定义接口
    public interface CallBack3 {
        void send(List<QuestionInfo.DataBean> data);

        void error();
    }
}
