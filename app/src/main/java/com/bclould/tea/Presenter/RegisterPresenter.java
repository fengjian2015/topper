package com.bclould.tea.Presenter;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.bclould.tea.R;
import com.bclould.tea.model.BaseInfo;
import com.bclould.tea.network.RetrofitUtil;
import com.bclould.tea.ui.activity.LoginActivity;
import com.bclould.tea.ui.activity.ServiceAgreementActivity;
import com.bclould.tea.ui.widget.LoadingProgressDialog;
import com.bclould.tea.utils.UtilTool;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by GA on 2017/11/15.
 */
public class RegisterPresenter {

    private final Context mContext;
    private LoadingProgressDialog mProgressDialog;

    public RegisterPresenter(Context context) {
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
            mProgressDialog.hideDialog();
        }
    }

    public void sendRegcode(final String email, final CallBack callBack) {
        showDialog();
        RetrofitUtil.getInstance(mContext)
                .getServer()
                .sendRegcode(email, UtilTool.getLanguage(mContext))
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
                            callBack.send();
                        }
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

    public void register(String user, String email, String emailCode, String password,String inviter_id) {
        showDialog();
        RetrofitUtil.getInstance(mContext)
                .getServer()
                .signUp(user, email, password, emailCode,inviter_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                .subscribe(new Observer<BaseInfo>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull BaseInfo baseInfo) {
                        hideDialog();
                        if(baseInfo.getStatus()==1){
                            mContext.startActivity(new Intent(mContext, LoginActivity.class));
                            ServiceAgreementActivity serviceAgreementActivity = (ServiceAgreementActivity) mContext;
                            serviceAgreementActivity.finish();
                        }
                        Toast.makeText(mContext, baseInfo.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        hideDialog();

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void signUpValidator(final String email, final String user,String inviter_id, final CallBack2 callBack2) {
        showDialog();
        RetrofitUtil.getInstance(mContext)
                .getServer()
                .signUpValidator(email, user,inviter_id)
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
                            callBack2.send();
                        } else {
                            Toast.makeText(mContext, baseInfo.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        hideDialog();

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    //定义接口
    public interface CallBack {
        void send();
    }

    //定义接口
    public interface CallBack2 {
        void send();
    }
}
