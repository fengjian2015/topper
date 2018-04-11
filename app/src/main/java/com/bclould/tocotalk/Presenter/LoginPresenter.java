package com.bclould.tocotalk.Presenter;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bclould.tocotalk.R;
import com.bclould.tocotalk.model.BaseInfo;
import com.bclould.tocotalk.model.LoginInfo;
import com.bclould.tocotalk.model.LoginRecordInfo;
import com.bclould.tocotalk.network.RetrofitUtil;
import com.bclould.tocotalk.ui.activity.LoginActivity;
import com.bclould.tocotalk.ui.activity.LoginSetActivity;
import com.bclould.tocotalk.ui.activity.MainActivity;
import com.bclould.tocotalk.ui.widget.DeleteCacheDialog;
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
    public static final String STATE = "state";
    private static final String CURRENCY = "currency";
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

    public void Login(final String email, final String password, final String code) {
        if (UtilTool.isNetworkAvailable(mContext)) {
            showDialog();
            RetrofitUtil.getInstance(mContext)
                    .getServer()
                    .login(email, password, code)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                    .subscribe(new Observer<LoginInfo>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(LoginInfo baseInfo) {
                            hideDialog();
                            if (baseInfo.getStatus() == 2) {
                                if (baseInfo.getData() != null) {
                                    if (baseInfo.getData().getValidate_type() == 1) {
                                        sendVcode(email);
                                        showEmailDialog(email, password);
                                    } else {
                                        showGoogleDialog(email, password);
                                    }
                                } else {
                                    Toast.makeText(mContext, baseInfo.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            } else if (baseInfo.getStatus() == 1) {
                                UtilTool.Log("日志", baseInfo.getData().getName());
                                MySharedPreferences.getInstance().setString(TOKEN, baseInfo.getMessage());
                                MySharedPreferences.getInstance().setInteger(USERID, baseInfo.getData().getUser_id());
                                MySharedPreferences.getInstance().setString(MYUSERNAME, baseInfo.getData().getName() + "@" + Constants.DOMAINNAME);
                                MySharedPreferences.getInstance().setString(EMAIL, email);
                                MySharedPreferences.getInstance().setString(LOGINPW, password);
                                MySharedPreferences.getInstance().setString(CURRENCY, baseInfo.getData().getCurrency());
                                if (baseInfo.getData().getCountry() == null) {
                                    MySharedPreferences.getInstance().setString(STATE, "中国 - 大陆");
                                } else {
                                    MySharedPreferences.getInstance().setString(STATE, baseInfo.getData().getCountry());
                                }
                                hideDialog();
                                mContext.startActivity(new Intent(mContext, MainActivity.class));
                                LoginActivity activity = (LoginActivity) mContext;
                                activity.finish();
                                Toast.makeText(mContext, mContext.getString(R.string.toast_succeed), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            hideDialog();
                            UtilTool.Log("登录", e.getMessage());
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

    private void sendVcode(String email) {
        if (UtilTool.isNetworkAvailable(mContext)) {
            RetrofitUtil.getInstance(mContext)
                    .getServer()
                    .sendRegcode(email)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                    .subscribe(new Observer<BaseInfo>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(BaseInfo baseInfo) {
                            Toast.makeText(mContext, baseInfo.getMessage(), Toast.LENGTH_SHORT).show();
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

    private void showGoogleDialog(final String email, final String password) {
        DeleteCacheDialog deleteCacheDialog = new DeleteCacheDialog(R.layout.dialog_google_code, mContext);
        deleteCacheDialog.show();
        final EditText etGoogle = (EditText) deleteCacheDialog.findViewById(R.id.et_google_code);
        Button btnConfirm = (Button) deleteCacheDialog.findViewById(R.id.btn_confirm);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String googleCode = etGoogle.getText().toString();
                if (googleCode.isEmpty()) {
                    Toast.makeText(mContext, mContext.getString(R.string.toast_vcode), Toast.LENGTH_SHORT).show();
                } else {
                    Login(email, password, googleCode);
                }
            }
        });
    }

    private void showEmailDialog(final String email, final String password) {
        DeleteCacheDialog deleteCacheDialog = new DeleteCacheDialog(R.layout.dialog_google_code, mContext);
        deleteCacheDialog.show();
        final EditText etGoogle = (EditText) deleteCacheDialog.findViewById(R.id.et_google_code);
        etGoogle.setHint(mContext.getString(R.string.et_vcode));
        TextView tvTitle = (TextView) deleteCacheDialog.findViewById(R.id.tv_title);
        tvTitle.setText("邮箱验证码");
        Button btnConfirm = (Button) deleteCacheDialog.findViewById(R.id.btn_confirm);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String googleCode = etGoogle.getText().toString();
                if (googleCode.isEmpty()) {
                    Toast.makeText(mContext, mContext.getString(R.string.toast_vcode), Toast.LENGTH_SHORT).show();
                } else {
                    Login(email, password, googleCode);
                }
            }
        });
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

    //定义接口
    public interface CallBack2 {
        void send(List<LoginRecordInfo.DataBean> data);
    }
}
