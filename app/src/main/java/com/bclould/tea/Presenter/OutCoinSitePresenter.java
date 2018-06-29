package com.bclould.tea.Presenter;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.widget.Toast;

import com.bclould.tea.R;
import com.bclould.tea.model.BaseInfo;
import com.bclould.tea.model.OutCoinSiteInfo;
import com.bclould.tea.network.RetrofitUtil;
import com.bclould.tea.ui.activity.AddOutCoinSiteActivity;
import com.bclould.tea.ui.activity.GoogleVerificationActivity;
import com.bclould.tea.ui.widget.LoadingProgressDialog;
import com.bclould.tea.utils.MessageEvent;
import com.bclould.tea.utils.UtilTool;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by GA on 2017/11/17.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class OutCoinSitePresenter {

    private final Context mContext;
    private LoadingProgressDialog mProgressDialog;

    public OutCoinSitePresenter(Context context) {
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

    public void getSite(int id, final CallBack callBack) {
        showDialog();
        RetrofitUtil.getInstance(mContext)
                .getServer()
                .withdrawalAddresses(UtilTool.getToken(), id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                .subscribe(new Observer<OutCoinSiteInfo>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull OutCoinSiteInfo outCoinSiteInfo) {
                        hideDialog();
                        if (outCoinSiteInfo.getStatus() == 1) {
                            callBack.send(outCoinSiteInfo.getMessage());
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

    public void deleteSite(int id, int address_id, final CallBack2 callBack2) {
        showDialog();
        RetrofitUtil.getInstance(mContext)
                .getServer()
                .deleteCoinOutAddress(UtilTool.getToken(), id, address_id)
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

    public void addCoinOutAddress(int id, String memo, String address, String vcode) {
        showDialog();
        RetrofitUtil.getInstance(mContext)
                .getServer()
                .addCoinOutAddress(UtilTool.getToken(), id, memo, address, vcode)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                .subscribe(new Observer<BaseInfo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(BaseInfo baseInfo) {
                        hideDialog();
                        Toast.makeText(mContext, baseInfo.getMessage(), Toast.LENGTH_SHORT).show();
                        if (baseInfo.getStatus() == 1) {
                            AddOutCoinSiteActivity activity = (AddOutCoinSiteActivity) mContext;
                            activity.finish();
                            EventBus.getDefault().post(new MessageEvent(mContext.getString(R.string.add_site)));
                        } else if (baseInfo.getStatus() == 2) {
                            if (baseInfo.getType() == 3)
                                mContext.startActivity(new Intent(mContext, GoogleVerificationActivity.class));
                            Toast.makeText(mContext, baseInfo.getMessage(), Toast.LENGTH_SHORT).show();
                        }
//                            if (baseInfo.getMessage().equals("请先绑定谷歌验证"))
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

    //定义接口
    public interface CallBack {
        void send(List<OutCoinSiteInfo.MessageBean> data);
    }

    //定义接口
    public interface CallBack2 {
        void send();
    }
}
