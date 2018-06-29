package com.bclould.tea.Presenter;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.bclould.tea.R;
import com.bclould.tea.model.BaseInfo;
import com.bclould.tea.model.GrabRedInfo;
import com.bclould.tea.network.RetrofitUtil;
import com.bclould.tea.ui.activity.ChatTransferActivity;
import com.bclould.tea.ui.activity.PayPasswordActivity;
import com.bclould.tea.ui.activity.RealNameC1Activity;
import com.bclould.tea.ui.activity.SendQRCodeRedActivity;
import com.bclould.tea.ui.activity.SendRedGroupActivity;
import com.bclould.tea.ui.activity.SendRedPacketActivity;
import com.bclould.tea.ui.widget.DeleteCacheDialog;
import com.bclould.tea.ui.widget.LoadingProgressDialog;
import com.bclould.tea.utils.UtilTool;

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

    public void sendRedPacket(String user, int type, String coin, String remark, int rp_type, int redCount, double redSum, double count, String password, final CallBack callBack) {
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
                            Toast.makeText(mContext, mContext.getString(R.string.sent), Toast.LENGTH_SHORT).show();
                        } else if (baseInfo.getType() == 6) {
                            if (mContext instanceof SendRedPacketActivity) {
                                SendRedPacketActivity activity = (SendRedPacketActivity) mContext;
                                activity.showHintDialog();
                            } else if (mContext instanceof SendRedGroupActivity) {
                                SendRedGroupActivity activity = (SendRedGroupActivity) mContext;
                                activity.showHintDialog();
                            } else {
                                SendQRCodeRedActivity activity = (SendQRCodeRedActivity) mContext;
                                activity.showHintDialog();
                            }
                        } else if (baseInfo.getType() == 1) {
                            showHintDialog();
                        } else if (baseInfo.getType() == 4) {
                            showSetPwDialog();
                        } else {
                            Toast.makeText(mContext, baseInfo.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        hideDialog();
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

    private void showHintDialog() {
        final DeleteCacheDialog deleteCacheDialog = new DeleteCacheDialog(R.layout.dialog_delete_cache, mContext, R.style.dialog);
        deleteCacheDialog.show();
        deleteCacheDialog.setTitle(mContext.getString(R.string.real_name_authentication_hint));
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
                mContext.startActivity(new Intent(mContext, RealNameC1Activity.class));

            }
        });
    }

    private void showSetPwDialog() {
        final DeleteCacheDialog deleteCacheDialog = new DeleteCacheDialog(R.layout.dialog_delete_cache, mContext, R.style.dialog);
        deleteCacheDialog.show();
        deleteCacheDialog.setTitle(mContext.getString(R.string.set_pay_pw_hint));
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
                        } else if (baseInfo.getStatus() == 4) {
                            Toast.makeText(mContext, baseInfo.getMessage(), Toast.LENGTH_SHORT).show();
                        } else if (baseInfo.getStatus() == 2) {
                            Toast.makeText(mContext, baseInfo.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        hideDialog();

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

    public void transgerfriend(String coin, String user, double count, String password, String remark) {
        showDialog();
        RetrofitUtil.getInstance(mContext)
                .getServer()
                .friendTransfer(UtilTool.getToken(), coin, user, count, password, remark)
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
                            ChatTransferActivity activity = (ChatTransferActivity) mContext;
                            activity.sendMessage();
                        } else if (baseInfo.getType() == 4) {
                            showSetPwDialog();
                        } else if (baseInfo.getType() == 6) {
                            ChatTransferActivity activity = (ChatTransferActivity) mContext;
                            activity.showHintDialog();
                        } else if (baseInfo.getMessage().equals(mContext.getString(R.string.real_name_authentication_hint))) {
                            showHintDialog();
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

    //定义接口
    public interface CallBack {
        void send(int id);
    }

    //定义接口
    public interface CallBack3 {
        void send(GrabRedInfo.DataBean dataBean);
    }

}
