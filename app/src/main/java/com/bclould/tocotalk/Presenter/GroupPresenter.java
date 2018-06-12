package com.bclould.tocotalk.Presenter;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.bclould.tocotalk.R;
import com.bclould.tocotalk.model.BaseInfo;
import com.bclould.tocotalk.model.GroupCreateInfo;
import com.bclould.tocotalk.model.GroupInfo;
import com.bclould.tocotalk.network.RetrofitUtil;
import com.bclould.tocotalk.ui.widget.LoadingProgressDialog;
import com.bclould.tocotalk.utils.ActivityUtil;
import com.bclould.tocotalk.utils.ToastShow;
import com.bclould.tocotalk.utils.UtilTool;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by GIjia on 2018/5/30.
 * 房間的請求
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class GroupPresenter {
    private final Context mContext;
    private LoadingProgressDialog mProgressDialog;

    public GroupPresenter(Context context) {
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


    public void createGroup(String room_name,String toco_ids ,String description, final CallBack2 callBack) {
        if (UtilTool.isNetworkAvailable(mContext)) {
            showDialog();
            RetrofitUtil.getInstance(mContext)
                    .getServer()
                    .createGroup(UtilTool.getToken(), room_name,toco_ids,description)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                    .subscribe(new Observer<GroupCreateInfo>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(GroupCreateInfo groupCreateInfo) {
                            if(!ActivityUtil.isActivityOnTop((Activity) mContext))return;
                            hideDialog();
                            if (groupCreateInfo.getStatus()== 1) {
                                callBack.send(groupCreateInfo.getData().getGroup_id()+"");
                            } else {
                                ToastShow.showToast2((Activity) mContext, mContext.getString(R.string.create_failure));
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            hideDialog();
                            ToastShow.showToast2((Activity) mContext, e.getMessage());
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        } else {
            ToastShow.showToast2((Activity) mContext, mContext.getString(R.string.toast_network_error));
        }
    }

    public void getGroup(final CallBack1 callBack) {
        if (UtilTool.isNetworkAvailable(mContext)) {
            RetrofitUtil.getInstance(mContext)
                    .getServer()
                    .getGroup(UtilTool.getToken())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                    .subscribe(new Observer<GroupInfo>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(GroupInfo baseInfo) {
                            hideDialog();
                            if (baseInfo.getStatus() == 1) {
                                callBack.send(baseInfo);
                            } else {
                                ToastShow.showToast2((Activity) mContext, mContext.getString(R.string.create_failure));
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            hideDialog();
                            ToastShow.showToast2((Activity) mContext, e.getMessage());
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        } else {
            ToastShow.showToast2((Activity) mContext, mContext.getString(R.string.toast_network_error));
        }
    }

    //定义接口
    public interface CallBack {
        void send();
    }
    //定义接口
    public interface CallBack1 {
        void send(GroupInfo baseInfo);
    }
    //定义接口
    public interface CallBack2 {
        void send(String group_id);
    }
}
