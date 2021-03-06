package com.bclould.tea.Presenter;

import android.app.Activity;
import android.content.Context;
import com.bclould.tea.R;
import com.bclould.tea.model.BaseInfo;
import com.bclould.tea.model.CollectInfo;
import com.bclould.tea.network.RetrofitUtil;
import com.bclould.tea.ui.activity.my.addcollect.AddCollectActivity;
import com.bclould.tea.ui.widget.LoadingProgressDialog;
import com.bclould.tea.utils.ActivityUtil;
import com.bclould.tea.utils.MySharedPreferences;
import com.bclould.tea.utils.ToastShow;
import com.bclould.tea.utils.UtilTool;
import com.google.gson.Gson;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.bclould.tea.ui.activity.CollectActivity.COLLECT_JOSN;

/**
 * Created by GA on 2018/7/13.
 */

public class CollectPresenter {

    private final Context mContext;
    private LoadingProgressDialog mProgressDialog;

    public CollectPresenter(Context context) {
        mContext = context;
    }

    private void showDialog() {
        if (ActivityUtil.isActivityOnTop((Activity) mContext)) {
            if (mProgressDialog == null) {
                mProgressDialog = LoadingProgressDialog.createDialog(mContext);
                mProgressDialog.setMessage(mContext.getString(R.string.loading));
            }
            mProgressDialog.show();
        }
    }

    private void hideDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.hideDialog();
        }
    }

    public void getCollectList(final CallBack callBack) {
        showDialog();
        RetrofitUtil.getInstance(mContext)
                .getServer()
                .CollectList(UtilTool.getToken())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                .subscribe(new Observer<CollectInfo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(CollectInfo collectInfo) {
                        hideDialog();
                        if (collectInfo.getStatus() == 1) {
                            callBack.send(collectInfo.getData());
                            Gson gson = new Gson();
                            MySharedPreferences.getInstance().setString(COLLECT_JOSN, gson.toJson(collectInfo));
                        } else {
                            callBack.finishRefresh();
                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                        hideDialog();
                        callBack.error();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void deleteCollect(int id, final CallBack2 callBack2) {
        showDialog();
        RetrofitUtil.getInstance(mContext)
                .getServer()
                .deleteCollect(UtilTool.getToken(), id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                .subscribe(new Observer<BaseInfo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(BaseInfo baseInfo) {
                        hideDialog();
                        ToastShow.showToast2((Activity) mContext, baseInfo.getMessage());
                        if (baseInfo.getStatus() == 1) {
                            callBack2.send();
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

    public void addCollect(String title, String url, String iconUrl, final CallBack2 callBack2) {
        RetrofitUtil.getInstance(mContext)
                .getServer()
                .addCollect(UtilTool.getToken(), title, url, iconUrl)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                .subscribe(new Observer<BaseInfo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(BaseInfo baseInfo) {
                        AddCollectActivity activity = (AddCollectActivity) mContext;
                        activity.hideDialog();
                        ToastShow.showToast2((Activity) mContext, baseInfo.getMessage());
                        if (baseInfo.getStatus() == 1) {
                            callBack2.send();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        AddCollectActivity activity = (AddCollectActivity) mContext;
                        activity.hideDialog();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void saveSequence(String sequenceStr, final CallBack2 callBack2) {
        showDialog();
        RetrofitUtil.getInstance(mContext)
                .getServer()
                .saveSequence(UtilTool.getToken(), sequenceStr)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                .subscribe(new Observer<BaseInfo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(BaseInfo baseInfo) {
                        hideDialog();
                        ToastShow.showToast2((Activity) mContext, baseInfo.getMessage());
                        if (baseInfo.getStatus() == 1) {
                            callBack2.send();
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
        void send(List<CollectInfo.DataBean> data);

        void error();

        void finishRefresh();
    }

    //定义接口
    public interface CallBack2 {
        void send();
    }
}
