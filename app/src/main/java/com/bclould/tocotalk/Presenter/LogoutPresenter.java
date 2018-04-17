package com.bclould.tocotalk.Presenter;

import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.widget.Toast;

import com.bclould.tocotalk.R;
import com.bclould.tocotalk.base.MyApp;
import com.bclould.tocotalk.model.BaseInfo;
import com.bclould.tocotalk.network.RetrofitUtil;
import com.bclould.tocotalk.ui.activity.LoginActivity;
import com.bclould.tocotalk.ui.activity.SystemSetActivity;
import com.bclould.tocotalk.ui.widget.LoadingProgressDialog;
import com.bclould.tocotalk.utils.MySharedPreferences;
import com.bclould.tocotalk.utils.UtilTool;
import com.bclould.tocotalk.xmpp.XmppConnection;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.bclould.tocotalk.Presenter.LoginPresenter.TOKEN;

/**
 * Created by GA on 2017/11/21.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class LogoutPresenter {

    private final SystemSetActivity mSystemSetActivity;
    private LoadingProgressDialog mProgressDialog;

    public LogoutPresenter(SystemSetActivity systemSetActivity) {
        mSystemSetActivity = systemSetActivity;
    }

    private void showDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = LoadingProgressDialog.createDialog(mSystemSetActivity);
            mProgressDialog.setMessage(mSystemSetActivity.getString(R.string.kitson));
        }

        mProgressDialog.show();
    }

    private void hideDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

    public void logout() {
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
                            Toast.makeText(mSystemSetActivity, mSystemSetActivity.getString(R.string.toast_network_error), Toast.LENGTH_SHORT).show();
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
