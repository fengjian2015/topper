package com.bclould.tocotalk.Presenter;

import android.content.Context;
import android.widget.Toast;

import com.bclould.tocotalk.R;
import com.bclould.tocotalk.model.GrabRedInfo;
import com.bclould.tocotalk.network.RetrofitUtil;
import com.bclould.tocotalk.ui.adapter.ExampleAdapter;
import com.bclould.tocotalk.ui.widget.LoadingProgressDialog;
import com.bclould.tocotalk.utils.UtilTool;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by GA on 2018/1/16.
 */

public class GrabRedPresenter {

    private final ExampleAdapter mExampleAdapter;
    private final Context mContext;
    private LoadingProgressDialog mProgressDialog;

    public GrabRedPresenter(ExampleAdapter exampleAdapter, Context context) {
        mExampleAdapter = exampleAdapter;
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

    public void grabRedPacket(int redId, final String id, final int position, final int type) {
        if (UtilTool.isNetworkAvailable(mContext)) {
            showDialog();
            RetrofitUtil.getInstance(mContext)
                    .getServer()
                    .grabRedPacket(UtilTool.getToken(), redId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                    .subscribe(new Observer<GrabRedInfo>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(GrabRedInfo baseInfo) {
                            if (baseInfo.getStatus() != 2)
                                mExampleAdapter.setData(baseInfo, id, position, type);
                            if (baseInfo.getStatus() == 4)
                                Toast.makeText(mContext, baseInfo.getMessage(), Toast.LENGTH_SHORT).show();
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
}
