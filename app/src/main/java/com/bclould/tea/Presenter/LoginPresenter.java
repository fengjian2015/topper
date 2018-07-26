package com.bclould.tea.Presenter;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bclould.tea.R;
import com.bclould.tea.history.DBUserCode;
import com.bclould.tea.model.BaseInfo;
import com.bclould.tea.model.LoginInfo;
import com.bclould.tea.model.LoginRecordInfo;
import com.bclould.tea.model.UserCodeInfo;
import com.bclould.tea.network.RetrofitUtil;
import com.bclould.tea.topperchat.WsConnection;
import com.bclould.tea.ui.activity.LoginActivity;
import com.bclould.tea.ui.activity.LoginSetActivity;
import com.bclould.tea.ui.activity.MainActivity;
import com.bclould.tea.ui.activity.SelectorLanguageActivity;
import com.bclould.tea.ui.widget.DeleteCacheDialog;
import com.bclould.tea.ui.widget.LoadingProgressDialog;
import com.bclould.tea.utils.MessageEvent;
import com.bclould.tea.utils.MySharedPreferences;
import com.bclould.tea.utils.UtilTool;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.bclould.tea.ui.activity.PayPwSelectorActivity.FINGERPRINT_PW_SELE;
import static com.bclould.tea.ui.activity.PayPwSelectorActivity.GESTURE_PW_SELE;
import static com.bclould.tea.ui.activity.SetGesturePWActivity.GESTURE_ANSWER;

/**
 * Created by GA on 2017/11/15.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class LoginPresenter {

    public static final String TOCOID = "toco_id";
    public static final String TOKEN = "token";
    public static final String USERID = "user_id";
    public static final String LOGINPW = "login_pw";
    public static final String LOGINSET = "login_set";
    public static final String STATE = "state";
    public static final String CURRENCY = "currency";
    public static final String IS_UPDATE = "is_update";
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
            mProgressDialog.setMessage(mContext.getString(R.string.login_in));
        }

        mProgressDialog.show();
    }

    private void hideDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

    public void Login(final String email, final String password, final String code, final DBUserCode dbUserCode, final String language) {
        showDialog();
        RetrofitUtil.getInstance(mContext)
                .getServer()
                .login(email, password, code, 1, language)
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
                                    showEmailDialog(email, password, dbUserCode, language);
                                } else {
                                    showGoogleDialog(email, password, dbUserCode, language);
                                }
                            } else {
                                if (baseInfo.getType() == 7) {
                                    showHintDialog(baseInfo.getMessage());
                                } else {
                                    Toast.makeText(mContext, baseInfo.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        } else if (baseInfo.getStatus() == 1) {
                            WsConnection.getInstance().setOutConnection(false);
                            UtilTool.Log("日志", baseInfo.getData().getName());
                            MySharedPreferences.getInstance().setString(TOKEN, baseInfo.getMessage());
                            MySharedPreferences.getInstance().setString(TOCOID, baseInfo.getData().getToco_id());
                            MySharedPreferences.getInstance().setInteger(USERID, baseInfo.getData().getUser_id());
                            MySharedPreferences.getInstance().setString(MYUSERNAME, baseInfo.getData().getName());
                            MySharedPreferences.getInstance().setString(EMAIL, email);
                            MySharedPreferences.getInstance().setString(LOGINPW, password);
                            MySharedPreferences.getInstance().setString(CURRENCY, baseInfo.getData().getCurrency());
                            MySharedPreferences.getInstance().setInteger(IS_UPDATE, baseInfo.getData().getIs_update());
                            if (baseInfo.getData().getFingerprint() == 1) {
                                MySharedPreferences.getInstance().setBoolean(FINGERPRINT_PW_SELE, true);
                            } else {
                                MySharedPreferences.getInstance().setBoolean(FINGERPRINT_PW_SELE, false);
                            }
                            if (baseInfo.getData().getGesture() == 1) {
                                MySharedPreferences.getInstance().setBoolean(GESTURE_PW_SELE, true);
                                MySharedPreferences.getInstance().setString(GESTURE_ANSWER, baseInfo.getData().getGesture_number());
                            } else {
                                MySharedPreferences.getInstance().setBoolean(GESTURE_PW_SELE, false);
                            }
                            if (!baseInfo.getData().getCountry().isEmpty()) {
                                MySharedPreferences.getInstance().setString(STATE, baseInfo.getData().getCountry());
                            }
                            UserCodeInfo userCodeInfo = new UserCodeInfo();
                            userCodeInfo.setEmail(email);
                            userCodeInfo.setPassword(password);
                            dbUserCode.addUserCode(userCodeInfo);
                            hideDialog();
                            Intent intent = new Intent(mContext, MainActivity.class);
                            intent.putExtra("whence", 1);
                            mContext.startActivity(intent);
                            LoginActivity activity = (LoginActivity) mContext;
                            EventBus.getDefault().post(new MessageEvent(mContext.getString(R.string.refresh_the_interface)));
                            activity.finish();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        hideDialog();
                        UtilTool.Log("登录", e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void showHintDialog(String message) {
        final DeleteCacheDialog deleteCacheDialog = new DeleteCacheDialog(R.layout.dialog_freeze, mContext, R.style.dialog);
        deleteCacheDialog.show();
        deleteCacheDialog.setCanceledOnTouchOutside(false);
        deleteCacheDialog.setCancelable(false);
        TextView content = (TextView) deleteCacheDialog.findViewById(R.id.tv_content);
        ImageView ivClose = (ImageView) deleteCacheDialog.findViewById(R.id.iv_close);
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteCacheDialog.dismiss();
            }
        });
        content.setText(message);

    }

    private void sendVcode(String email) {
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
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void showGoogleDialog(final String email, final String password, final DBUserCode dbUserCode, final String language) {
        DeleteCacheDialog deleteCacheDialog = new DeleteCacheDialog(R.layout.dialog_google_code, mContext, R.style.dialog);
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
                    Login(email, password, googleCode, dbUserCode, language);
                }
            }
        });
    }

    private void showEmailDialog(final String email, final String password, final DBUserCode dbUserCode, final String language) {
        DeleteCacheDialog deleteCacheDialog = new DeleteCacheDialog(R.layout.dialog_google_code, mContext, R.style.dialog);
        deleteCacheDialog.show();
        final EditText etGoogle = (EditText) deleteCacheDialog.findViewById(R.id.et_google_code);
        etGoogle.setHint(mContext.getString(R.string.et_vcode));
        TextView tvTitle = (TextView) deleteCacheDialog.findViewById(R.id.tv_title);
        tvTitle.setText(mContext.getString(R.string.vcode));
        Button btnConfirm = (Button) deleteCacheDialog.findViewById(R.id.btn_confirm);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String googleCode = etGoogle.getText().toString();
                if (googleCode.isEmpty()) {
                    Toast.makeText(mContext, mContext.getString(R.string.toast_vcode), Toast.LENGTH_SHORT).show();
                } else {
                    Login(email, password, googleCode, dbUserCode, language);
                }
            }
        });
    }

    public void loginRecord(final CallBack callBack) {
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
//                            hideDialog();
                        if (loginRecordInfo.getStatus() == 1) {
                            callBack.send(loginRecordInfo.getData());
                        }

                    }

                    @Override
                    public void onError(Throwable e) {
//                            hideDialog();
                        callBack.error();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void loginValidateTypeSetting(final int index, String pw, String googleCode) {

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
//                            hideDialog();
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
//                            hideDialog();
                        UtilTool.Log("登录设置", e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void postLanguage(String language, final CallBack3 callBack3) {
        UtilTool.Log("語言", "app语言切换===" + language);
        if (mContext instanceof SelectorLanguageActivity) {
            showDialog();
        }
        RetrofitUtil.getInstance(mContext)
                .getServer()
                .postLanguage(UtilTool.getToken(), language)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                .subscribe(new Observer<BaseInfo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(BaseInfo baseInfo) {
                        hideDialog();
                        callBack3.send();
                    }

                    @Override
                    public void onError(Throwable e) {
                        hideDialog();
                        callBack3.send();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    //定义接口
    public interface CallBack {
        void send(List<LoginRecordInfo.DataBean> data);

        void error();
    }

    //定义接口
    public interface CallBack2 {
        void send(List<LoginRecordInfo.DataBean> data);
    }

    //定义接口
    public interface CallBack3 {
        void send();
    }
}
