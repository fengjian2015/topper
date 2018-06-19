package com.bclould.tea.Presenter;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.widget.Toast;

import com.bclould.tea.R;
import com.bclould.tea.model.UpdateLogInfo;
import com.bclould.tea.network.RetrofitUtil;
import com.bclould.tea.utils.UtilTool;

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
        if (UtilTool.isNetworkAvailable(mContext)) {
//            showDialog();
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
                            Toast.makeText(mContext, mContext.getString(R.string.toast_network_error), Toast.LENGTH_SHORT).show();
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
        void send(List<UpdateLogInfo.DataBean> data);
    }
}
