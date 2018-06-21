package com.bclould.tea.Presenter;

import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.widget.Toast;

import com.bclould.tea.R;
import com.bclould.tea.base.MyApp;
import com.bclould.tea.model.BaseInfo;
import com.bclould.tea.network.RetrofitUtil;
import com.bclould.tea.topperchat.WsConnection;
import com.bclould.tea.ui.activity.InitialActivity;
import com.bclould.tea.ui.activity.SystemSetActivity;
import com.bclould.tea.ui.widget.LoadingProgressDialog;
import com.bclould.tea.utils.MySharedPreferences;
import com.bclould.tea.utils.UtilTool;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.bclould.tea.Presenter.LoginPresenter.TOCOID;
import static com.bclould.tea.Presenter.LoginPresenter.TOKEN;

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
                                imLogout(baseInfo.getMessage());
                            }

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

    public void imLogout(final String message) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    WsConnection.getInstance().senLogout();
                    WsConnection.getInstance().logoutService(mSystemSetActivity);
                    Thread.sleep(1000);
                    UtilTool.Log("fengjian", "退出成功");
                    Message msg = new Message();
                    msg.obj = message;
                    myHandler.sendMessage(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                    UtilTool.Log("fengjian", "退出失败");
                }
            }
        }).start();
    }

    Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Toast.makeText(mSystemSetActivity, (String) msg.obj, Toast.LENGTH_SHORT).show();
            MyApp.getInstance().exit();
            mSystemSetActivity.finish();
            MySharedPreferences.getInstance().setString(TOKEN, "");
            MySharedPreferences.getInstance().setString(TOCOID, "");
            MyApp.getInstance().mCoinList.clear();
            MyApp.getInstance().mPayCoinList.clear();
            MyApp.getInstance().mOtcCoinList.clear();
            MyApp.getInstance().mBetCoinList.clear();
            mSystemSetActivity.startActivity(new Intent(mSystemSetActivity, InitialActivity.class));
        }
    };
}
