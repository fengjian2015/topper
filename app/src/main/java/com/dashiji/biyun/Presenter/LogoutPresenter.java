package com.dashiji.biyun.Presenter;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.dashiji.biyun.R;
import com.dashiji.biyun.base.MyApp;
import com.dashiji.biyun.model.BaseInfo;
import com.dashiji.biyun.network.RetrofitUtil;
import com.dashiji.biyun.ui.activity.LoginActivity;
import com.dashiji.biyun.ui.activity.SystemSetActivity;
import com.dashiji.biyun.ui.widget.LoadingProgressDialog;
import com.dashiji.biyun.utils.MySharedPreferences;
import com.dashiji.biyun.utils.UtilTool;
import com.dashiji.biyun.xmpp.XmppConnection;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.dashiji.biyun.Presenter.LoginPresenter.TOKEN;

/**
 * Created by GA on 2017/11/21.
 */

public class LogoutPresenter {

    private final SystemSetActivity mSystemSetActivity;
    private LoadingProgressDialog mProgressDialog;

    public LogoutPresenter(SystemSetActivity systemSetActivity) {
        mSystemSetActivity = systemSetActivity;
        logout();
    }

    private void showDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = LoadingProgressDialog.createDialog(mSystemSetActivity);
            mProgressDialog.setMessage("退出中...");
        }

        mProgressDialog.show();
    }

    private void hideDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

    private void logout() {
        if (UtilTool.isNetworkAvailable(mSystemSetActivity)) {
            showDialog();
            RetrofitUtil.getInstance(mSystemSetActivity)
                    .getServer()
                    .logout(UtilTool.getToken())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                    .subscribe(new Observer<BaseInfo>() {
                        @Override
                        public void onSubscribe(@NonNull Disposable d) {

                        }

                        @Override
                        public void onNext(@NonNull BaseInfo baseInfo) {
                            hideDialog();
                            if (baseInfo.getStatus() == 1) {
                                imLogout();
                            }
                            Toast.makeText(mSystemSetActivity, baseInfo.getMessage(), Toast.LENGTH_SHORT).show();

                        }

                        @Override
                        public void onError(@NonNull Throwable e) {
                            hideDialog();
                            Toast.makeText(mSystemSetActivity, "网络连接失败，请稍后重试", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        } else {

            Toast.makeText(mSystemSetActivity, mSystemSetActivity.getString(R.string.toast_network_error), Toast.LENGTH_SHORT).show();

        }
    }

    public void imLogout() {
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    XmppConnection.getInstance().closeConnection();
                    UtilTool.Log("fsdafa", "退出成功");
                    Message msg = new Message();
                    myHandler.sendMessage(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                    UtilTool.Log("fsdafa", "退出失败");
                }
            }
        }).start();
    }

    Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            MyApp.getInstance().exit();
            mSystemSetActivity.finish();
            MySharedPreferences.getInstance().setString(TOKEN, "");
            mSystemSetActivity.startActivity(new Intent(mSystemSetActivity, LoginActivity.class));
        }
    };
}
