package com.bclould.tea.Presenter;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bclould.tea.R;
import com.bclould.tea.model.BaseInfo;
import com.bclould.tea.model.DynamicListInfo;
import com.bclould.tea.model.ExternalTokenInfo;
import com.bclould.tea.model.ExternalUserInfo;
import com.bclould.tea.network.RetrofitUtil;
import com.bclould.tea.network.https.HttpUtils;
import com.bclould.tea.ui.widget.LoadingProgressDialog;
import com.bclould.tea.utils.ActivityUtil;
import com.bclould.tea.utils.Constants;
import com.bclould.tea.utils.MessageEvent;
import com.bclould.tea.utils.UtilTool;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by GIjia on 2018/12/21.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class ExternalPresenter {
    private final Context mContext;
    private LoadingProgressDialog mProgressDialog;

    public ExternalPresenter(Context context) {
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

    public void publicsh(String grant_type,String client_id,String client_secret,String scope,final CallBack callBack) {
        showDialog();
        RetrofitUtil.getInstance(mContext)
                .getServer()
                .getExToken(UtilTool.getToken(),grant_type,client_id,client_secret,scope)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                .subscribe(new Observer<ExternalTokenInfo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ExternalTokenInfo baseInfo) {
                        if(!ActivityUtil.isActivityOnTop(mContext))return;
                        hideDialog();
                        callBack.send(baseInfo);
                    }

                    @Override
                    public void onError(Throwable e) {
                        if(!ActivityUtil.isActivityOnTop(mContext))return;
                        hideDialog();
                        callBack.error();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

//    public void getExUser(String access_token,final CallBack1 callBack) {
//        RetrofitUtil.getInstance(mContext)
//                .getServer()
//                .getExUser(UtilTool.getToken(),access_token)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
//                .subscribe(new Observer<ExternalUserInfo>() {
//                    @Override
//                    public void onSubscribe(Disposable d) {
//
//                    }
//
//                    @Override
//                    public void onNext(ExternalUserInfo baseInfo) {
//                        if(!ActivityUtil.isActivityOnTop(mContext))return;
//                        callBack.send(baseInfo);
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        if(!ActivityUtil.isActivityOnTop(mContext))return;
//                        hideDialog();
//                        callBack.error();
//                    }
//
//                    @Override
//                    public void onComplete() {
//
//                    }
//                });
//    }

    public void getExUsers(final String access_token, final CallBack1 callBack){
        hideDialog();
        new Thread(){
            public ExternalUserInfo mExternalUserInfo;
            @Override
            public void run() {
                Map<String,String> params = new HashMap<String,String>();
                params.put("access_token", access_token);
                String strResult= HttpUtils.submitPostData(Constants.BASE_URL+"oauth/api",params, "utf-8");
                try {
                    mExternalUserInfo= JSONObject.parseObject(strResult,ExternalUserInfo.class);
                }catch (Exception e){
                    mExternalUserInfo=new ExternalUserInfo();
                    mExternalUserInfo.setError(strResult);
                    mExternalUserInfo.setStatus(2);
                }
                if(!ActivityUtil.isActivityOnTop(mContext))return;
                callBack.send(mExternalUserInfo);
                hideDialog();
            }
        }.start();
    }


    //定义接口
    public interface CallBack {
        void send(ExternalTokenInfo data);

        void error();
    }

    //定义接口
    public interface CallBack1 {
        void send(ExternalUserInfo data);

        void error();
    }

}
