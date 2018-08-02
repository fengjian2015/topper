package com.bclould.tea.Presenter;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.widget.Toast;

import com.bclould.tea.R;
import com.bclould.tea.base.MyApp;
import com.bclould.tea.model.BaseInfo;
import com.bclould.tea.network.RetrofitUtil;
import com.bclould.tea.topperchat.WsConnection;
import com.bclould.tea.ui.activity.MainActivity;
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

    private final Activity mActivity;
    private LoadingProgressDialog mProgressDialog;

    public LogoutPresenter(Activity activity) {
        mActivity = activity;
    }

    private void showDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = LoadingProgressDialog.createDialog(mActivity);
            mProgressDialog.setMessage(mActivity.getString(R.string.kitson));
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
        showDialog();
        RetrofitUtil.getInstance(mActivity)
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
                        imLogout(mActivity.getString(R.string.out_group_success));
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void imLogout(final String message) {
        UtilTool.Log("fengjian", "退出成功");
        new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                Toast.makeText(mActivity,message, Toast.LENGTH_SHORT).show();
                WsConnection.getInstance().goMainActivity();
            }
        }.sendEmptyMessage(0);
//            MyApp.getInstance().exit();
    }


}
