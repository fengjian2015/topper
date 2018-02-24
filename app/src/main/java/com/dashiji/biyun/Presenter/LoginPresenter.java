package com.dashiji.biyun.Presenter;

import android.content.Intent;
import android.widget.Toast;

import com.dashiji.biyun.R;
import com.dashiji.biyun.model.LoginInfo;
import com.dashiji.biyun.network.RetrofitUtil;
import com.dashiji.biyun.ui.activity.LoginActivity;
import com.dashiji.biyun.ui.activity.MainActivity;
import com.dashiji.biyun.ui.widget.LoadingProgressDialog;
import com.dashiji.biyun.utils.Constants;
import com.dashiji.biyun.utils.MySharedPreferences;
import com.dashiji.biyun.utils.UtilTool;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by GA on 2017/11/15.
 */

public class LoginPresenter {

    public static final String TOKEN = "token";
    public static final String USERID = "user_id";
    public static final String LOGINPW = "login_pw";
    private final LoginActivity mLoginActivity;
    private LoadingProgressDialog mProgressDialog;
    public static final String MYUSERNAME = "my_username";
    public static final String EMIL = "emil";

    public LoginPresenter(LoginActivity loginActivity) {
        mLoginActivity = loginActivity;
    }

    private void showDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = LoadingProgressDialog.createDialog(mLoginActivity);
            mProgressDialog.setMessage("登录中...");
        }

        mProgressDialog.show();
    }

    private void hideDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

    public void Login(final String emil, final String password) {

        if (UtilTool.isNetworkAvailable(mLoginActivity)) {
            showDialog();
            RetrofitUtil.getInstance(mLoginActivity)
                    .getServer()
                    .login(emil, password)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                    .subscribe(new Observer<LoginInfo>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(LoginInfo baseInfo) {
                            if (baseInfo.getStatus() != 1) {
                                hideDialog();
                                Toast.makeText(mLoginActivity, baseInfo.getMessage(), Toast.LENGTH_SHORT).show();
//                                getCaptcha();
                            } else {
                                UtilTool.Log("日志", baseInfo.getData().getName());
                                MySharedPreferences.getInstance().setString(TOKEN, baseInfo.getMessage());
                                MySharedPreferences.getInstance().setInteger(USERID, baseInfo.getData().getUser_id());
                                MySharedPreferences.getInstance().setString(MYUSERNAME, baseInfo.getData().getName() + "@" + Constants.DOMAINNAME);
                                MySharedPreferences.getInstance().setString(EMIL, emil);
                                MySharedPreferences.getInstance().setString(LOGINPW, password);
                                hideDialog();
                                mLoginActivity.startActivity(new Intent(mLoginActivity, MainActivity.class));
                                mLoginActivity.finish();
                                Toast.makeText(mLoginActivity, mLoginActivity.getString(R.string.toast_succeed), Toast.LENGTH_SHORT).show();
//                                imLogin(password, baseInfo.getData().getName());
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            hideDialog();
                            Toast.makeText(mLoginActivity, "网络连接失败，请稍后重试", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        } else {

            Toast.makeText(mLoginActivity, mLoginActivity.getString(R.string.toast_network_error), Toast.LENGTH_SHORT).show();

        }
    }

    /*private void imLogin(final String password, final String user) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                AbstractXMPPConnection connection = XmppConnection.getInstance().getConnection();
                try {
                    if (connection.isConnected()) {
                        connection.login(user, password);
                        Message message = new Message();
                        mHandler.sendMessage(message);
                    }
                } catch (Exception e) {
                    UtilTool.Log("fsdafa", e.getMessage());
                    e.printStackTrace();
                }
            }
        }).start();
    }

    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            UtilTool.Log("fsdafa", "登录成功");
            EventBus.getDefault().post(new MessageEvent("登录成功"));

        }
    };

    public void getCaptcha() {

        if (UtilTool.isNetworkAvailable(mLoginActivity)) {
            showDialog();
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(Constants.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            MyService myService = retrofit.create(MyService.class);

            myService.getCaptcha("https://www.bclould.com:8098/api/captcha")
                    .enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            if (response.isSuccessful()) {
                                hideDialog();
                                mLoginActivity.setData(response);
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            hideDialog();
                        }
                    });
        } else {

            Toast.makeText(mLoginActivity, mLoginActivity.getString(R.string.toast_network_error), Toast.LENGTH_SHORT).show();

        }
    }*/
}
