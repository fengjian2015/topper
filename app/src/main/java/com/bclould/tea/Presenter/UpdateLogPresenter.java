package com.bclould.tea.Presenter;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.bclould.tea.R;
import com.bclould.tea.model.BaseInfo;
import com.bclould.tea.model.GitHubInfo;
import com.bclould.tea.model.UpdateLogInfo;
import com.bclould.tea.network.DownLoadApk;
import com.bclould.tea.network.RetrofitUtil;
import com.bclould.tea.ui.activity.GuanYuMeActivity;
import com.bclould.tea.ui.widget.DeleteCacheDialog;
import com.bclould.tea.utils.Constants;
import com.bclould.tea.utils.MessageEvent;
import com.bclould.tea.utils.MySharedPreferences;
import com.bclould.tea.utils.UtilTool;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by GA on 2018/5/28.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class UpdateLogPresenter {

    private final Context mContext;

    public UpdateLogPresenter(Context context) {
        mContext = context;
    }

    public void getUpdateLogList(int type, final UpdateLogPresenter.CallBack callBack) {
        RetrofitUtil.getInstance(mContext)
                .getServer()
                .getUpdateLogList(UtilTool.getToken(), type)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                .subscribe(new Observer<UpdateLogInfo>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(UpdateLogInfo updateLogInfo) {
                        if (updateLogInfo.getStatus() == 1) {
                            callBack.send(updateLogInfo.getData());
                        } else {
                            Toast.makeText(mContext, updateLogInfo.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
//                            hideDialog();
                        UtilTool.Log("更新日誌", e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    //检测版本更新
    public void checkVersion(final CallBack2 callBack2) {
        RetrofitUtil.getInstance(mContext)
                .getServer()
                .checkVersion(Constants.VERSION_UPDATE_URL)//githua获取版本更新
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                .subscribe(new Observer<GitHubInfo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(GitHubInfo baseInfo) {
                        //判断是否需要更新
                        float version = Float.parseFloat(UtilTool.getVersionCode(mContext));
                        String tag_version = "";
                        if (baseInfo.getTag_name().contains("v")) {
                            tag_version = baseInfo.getTag_name().replace("v", "");
                        } else {
                            tag_version = baseInfo.getTag_name();
                        }
                        float tag = Float.parseFloat(tag_version);
                        if (version < tag) {
                            MySharedPreferences.getInstance().setString(Constants.NEW_APK_URL, Constants.DOWNLOAD_APK_URL);
                            MySharedPreferences.getInstance().setString(Constants.NEW_APK_NAME, baseInfo.getName());
                            MySharedPreferences.getInstance().setString(Constants.NEW_APK_BODY, baseInfo.getBody());
                            callBack2.send(1);
                            showDialog(Constants.DOWNLOAD_APK_URL, baseInfo.getName(), baseInfo.getBody());
                            EventBus.getDefault().post(new MessageEvent(mContext.getString(R.string.check_new_version)));
                        } else {
                            callBack2.send(2);
                            MySharedPreferences.getInstance().setString(Constants.NEW_APK_URL, "");
                            MySharedPreferences.getInstance().setString(Constants.NEW_APK_NAME, "");
                            MySharedPreferences.getInstance().setString(Constants.NEW_APK_BODY, "");
                            if (mContext instanceof GuanYuMeActivity) {
                                Toast.makeText(mContext, mContext.getString(R.string.already_new_version), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        UtilTool.Log("日志", e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }


    public void showDialog(final String url, final String apkName, final String body) {
        //显示更新dialog
        final DeleteCacheDialog deleteCacheDialog = new DeleteCacheDialog(R.layout.dialog_delete_cache, mContext, R.style.dialog);
        deleteCacheDialog.show();
        deleteCacheDialog.setTitle(mContext.getString(R.string.check_new_version));
        Button cancel = (Button) deleteCacheDialog.findViewById(R.id.btn_cancel);
        Button confirm = (Button) deleteCacheDialog.findViewById(R.id.btn_confirm);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteCacheDialog.dismiss();
            }
        });
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UtilTool.Log("版本更新", url);
                DownLoadApk.download(mContext, url, body, apkName);
                deleteCacheDialog.dismiss();
            }
        });
    }

    public void setFingerprint(String password, int status, final CallBack2 callBack2) {
        RetrofitUtil.getInstance(mContext)
                .getServer()
                .setFingerprint(UtilTool.getToken(), status, password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                .subscribe(new Observer<BaseInfo>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(BaseInfo baseInfo) {
                        if (baseInfo.getStatus() == 1) {
                            callBack2.send(baseInfo.getData().getFingerprint());
                        } else {
                            Toast.makeText(mContext, baseInfo.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
//                            hideDialog();
                        UtilTool.Log("更新日誌", e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    //定义接口
    public interface CallBack {
        void send(List<UpdateLogInfo.DataBean> data);
    }

    //定义接口
    public interface CallBack2 {
        void send(int type);
    }
}
