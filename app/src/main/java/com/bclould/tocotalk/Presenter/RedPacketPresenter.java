package com.bclould.tocotalk.Presenter;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.bclould.tocotalk.R;
import com.bclould.tocotalk.model.BaseInfo;
import com.bclould.tocotalk.model.GrabRedInfo;
import com.bclould.tocotalk.network.RetrofitUtil;
import com.bclould.tocotalk.ui.activity.PayPasswordActivity;
import com.bclould.tocotalk.ui.activity.SendQRCodeRedActivity;
import com.bclould.tocotalk.ui.activity.SendRedPacketActivity;
import com.bclould.tocotalk.ui.widget.DeleteCacheDialog;
import com.bclould.tocotalk.ui.widget.LoadingProgressDialog;
import com.bclould.tocotalk.utils.UtilTool;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by GA on 2018/1/12.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class RedPacketPresenter {

    private final Context mContext;
    private LoadingProgressDialog mProgressDialog;

    public RedPacketPresenter(Context context) {
        mContext = context;
    }

    private void showDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = LoadingProgressDialog.createDialog(mContext);
            mProgressDialog.setMessage("加载中...");
        }

        mProgressDialog.show();
    }

    private void hideDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

    public void sendRedPacket(String user, int type, String coin, String remark, int rp_type, int redCount, double redSum, double count, String password, final CallBack callBack) {
        if (UtilTool.isNetworkAvailable(mContext)) {
            showDialog();
            RetrofitUtil.getInstance(mContext)
                    .getServer()
                    .sendRedPacket(UtilTool.getToken(), user, type, coin, remark, rp_type, redCount, redSum, count, password)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                    .subscribe(new Observer<BaseInfo>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(BaseInfo baseInfo) {
                            if (baseInfo.getStatus() == 1) {
                                callBack.send(baseInfo.getData().getId());
                                Toast.makeText(mContext, "已发送", Toast.LENGTH_SHORT).show();
                            } else if (baseInfo.getMessage().equals("交易密码不正确")) {
                                if (mContext instanceof SendRedPacketActivity) {
                                    SendRedPacketActivity activity = (SendRedPacketActivity) mContext;
                                    activity.showHintDialog();
                                } else {
                                    SendQRCodeRedActivity activity = (SendQRCodeRedActivity) mContext;
                                    activity.showHintDialog();
                                }
                            } else if (baseInfo.getMessage().equals("尚未设置交易密码")) {
                                showSetPwDialog();
                            } else {
                                Toast.makeText(mContext, baseInfo.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                            hideDialog();
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

    private void showSetPwDialog() {
        final DeleteCacheDialog deleteCacheDialog = new DeleteCacheDialog(R.layout.dialog_set_pw, mContext);
        deleteCacheDialog.show();
        deleteCacheDialog.setCanceledOnTouchOutside(false);
        Button retry = (Button) deleteCacheDialog.findViewById(R.id.btn_cancel);
        Button findPassword = (Button) deleteCacheDialog.findViewById(R.id.btn_confirm);
        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteCacheDialog.dismiss();
            }
        });
        findPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteCacheDialog.dismiss();
                mContext.startActivity(new Intent(mContext, PayPasswordActivity.class));
            }
        });
    }


    public void grabQrRed(int id, final CallBack3 callBack) {
        if (UtilTool.isNetworkAvailable(mContext)) {
            showDialog();
            RetrofitUtil.getInstance(mContext)
                    .getServer()
                    .grabRedPacket(UtilTool.getToken(), id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                    .subscribe(new Observer<GrabRedInfo>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(GrabRedInfo baseInfo) {
                            if (baseInfo.getStatus() != 2) {
                                callBack.send(baseInfo.getData());
                            }else if (baseInfo.getStatus() == 4) {
                                Toast.makeText(mContext, baseInfo.getMessage(), Toast.LENGTH_SHORT).show();
                            }else if(baseInfo.getStatus() == 2){
                                Toast.makeText(mContext, baseInfo.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                            hideDialog();

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

    //定义接口
    public interface CallBack {
        void send(int id);
    }

    //定义接口
    public interface CallBack3 {
        void send(GrabRedInfo.DataBean dataBean);
    }

}
