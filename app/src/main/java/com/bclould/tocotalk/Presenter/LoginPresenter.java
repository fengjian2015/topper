package com.bclould.tocotalk.Presenter;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.widget.Toast;

import com.bclould.tocotalk.R;
import com.bclould.tocotalk.model.BaseInfo;
import com.bclould.tocotalk.model.LoginInfo;
import com.bclould.tocotalk.model.LoginRecordInfo;
import com.bclould.tocotalk.network.RetrofitUtil;
import com.bclould.tocotalk.ui.activity.LoginActivity;
import com.bclould.tocotalk.ui.activity.LoginSetActivity;
import com.bclould.tocotalk.ui.activity.MainActivity;
import com.bclould.tocotalk.ui.widget.LoadingProgressDialog;
import com.bclould.tocotalk.utils.Constants;
import com.bclould.tocotalk.utils.MySharedPreferences;
import com.bclould.tocotalk.utils.UtilTool;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by GA on 2017/11/15.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class LoginPresenter {

    public static final String TOKEN = "token";
    public static final String USERID = "user_id";
    public static final String LOGINPW = "login_pw";
    public static final String LOGINSET = "login_set";
    private final Context mContext;
    private LoadingProgressDialog mProgressDialog;
    public static final String MYUSERNAME = "my_username";
    public static final String EMAIL = "email";

    public LoginPresenter(Context context) {
        mContext = context;
    }

    private void showDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = LoadingProgressDialog.createDialog(mContext);
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

    public void Login(final String email, final String password) {

        if (UtilTool.isNetworkAvailable(mContext)) {
            showDialog();
            RetrofitUtil.getInstance(mContext)
                    .getServer()
                    .login(email, password)
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
                                Toast.makeText(mContext, baseInfo.getMessage(), Toast.LENGTH_SHORT).show();
//                                getCaptcha();
                            } else {
                                UtilTool.Log("日志", baseInfo.getData().getName());
                                MySharedPreferences.getInstance().setString(TOKEN, baseInfo.getMessage());
                                MySharedPreferences.getInstance().setInteger(USERID, baseInfo.getData().getUser_id());
                                MySharedPreferences.getInstance().setString(MYUSERNAME, baseInfo.getData().getName() + "@" + Constants.DOMAINNAME);
                                MySharedPreferences.getInstance().setString(EMAIL, email);
                                MySharedPreferences.getInstance().setString(LOGINPW, password);
                                hideDialog();
                                mContext.startActivity(new Intent(mContext, MainActivity.class));
                                LoginActivity activity = (LoginActivity) mContext;
                                activity.finish();
                                Toast.makeText(mContext, mContext.getString(R.string.toast_succeed), Toast.LENGTH_SHORT).show();
//                                imLogin(password, baseInfo.getData().getName());
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            hideDialog();
                            Toast.makeText(mContext, "网络连接失败，请稍后重试", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        } else {

            Toast.makeText(mContext, mContext.getString(R.string.toast_network_error), Toast.LENGTH_SHORT).show();

        }
    }

    public void loginRecord(final CallBack callBack) {
        if (UtilTool.isNetworkAvailable(mContext)) {
            showDialog();
            RetrofitUtil.getInstance(mContext)
                    .getServer()
                    .loginRecord(UtilTool.getToken())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                    .subscribe(new Observer<LoginRecordInfo>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(LoginRecordInfo loginRecordInfo) {
                            hideDialog();
                            if (loginRecordInfo.getStatus() == 1) {
                                callBack.send(loginRecordInfo.getData());
                            }

                        }

                        @Override
                        public void onError(Throwable e) {
                            hideDialog();
                            Toast.makeText(mContext, "网络连接失败，请稍后重试", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        } else {
            Toast.makeText(mContext, mContext.getString(R.string.toast_network_error), Toast.LENGTH_SHORT).show();
        }
    }

    public void loginValidateTypeSetting(final int index, String pw, String googleCode) {
        if (UtilTool.isNetworkAvailable(mContext)) {
            showDialog();
            RetrofitUtil.getInstance(mContext)
                    .getServer()
                    .loginValidateTypeSetting(UtilTool.getToken(), index + "", pw, googleCode)
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
                                LoginSetActivity activity = (LoginSetActivity) mContext;
                                activity.finish();
                                MySharedPreferences.getInstance().setString(LOGINSET, index + "");
                            } else {
                                Toast.makeText(mContext, baseInfo.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            hideDialog();
                            UtilTool.Log("登录设置", e.getMessage());
                            Toast.makeText(mContext, "网络连接失败，请稍后重试", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        } else {
            Toast.makeText(mContext, mContext.getString(R.string.toast_network_error), Toast.LENGTH_SHORT).show();
        }
    }

    //定义接口
    public interface CallBack {
        void send(List<LoginRecordInfo.DataBean> data);
    }
}
