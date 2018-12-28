package com.bclould.tea.ui.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.widget.ImageView;

import com.alibaba.fastjson.JSON;
import com.bclould.tea.R;
import com.bclould.tea.base.LoginBaseActivity;
import com.bclould.tea.base.MyApp;
import com.bclould.tea.model.BaseInfo;
import com.bclould.tea.network.RetrofitUtil;
import com.bclould.tea.topperchat.WsConnection;
import com.bclould.tea.ui.activity.authorization.AuthorizationActivity;
import com.bclould.tea.utils.AppLanguageUtils;
import com.bclould.tea.utils.ExternalUtil;
import com.bclould.tea.utils.MySharedPreferences;
import com.bclould.tea.utils.SharedPreferencesUtil;
import com.bclould.tea.utils.StringUtils;
import com.bclould.tea.utils.UtilTool;

import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.bclould.tea.Presenter.LoginPresenter.TOKEN;
import static com.bclould.tea.Presenter.LoginPresenter.TOKEN_TIME;


/**
 * Created by GA on 2017/11/8.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class StartActivity extends LoginBaseActivity {
    @Bind(R.id.iv_start)
    ImageView mIvStart;

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        ButterKnife.bind(this);
        setSwipeEnabled(false);
        external();
    }

    private void external(){
        Intent intent = getIntent();
        Uri uri = intent.getData();
        if (uri != null) {
            String scheme = uri.getScheme();//csd
            String uriHost = uri.getHost();//pull.csd.demo
            String uriPath = uri.getPath();///cyn
            String type = uri.getQueryParameter("type");//110
            String content = uri.getQueryParameter("content");//120
            if (ExternalUtil.AUTHORIZATION.equals(type)) {//实际项目中可根据判断type值拉起相应的页面
                MySharedPreferences.getInstance().setBoolean(SharedPreferencesUtil.IS_EXTERNAL,true);
                MySharedPreferences.getInstance().setString(SharedPreferencesUtil.EXTERNAL_CONTENT,content);
                if(StringUtils.isEmpty(UtilTool.getTocoId())||StringUtils.isEmpty(UtilTool.getToken())||UtilTool.getToken().equals("bearer")){
                    WsConnection.getInstance().setOutConnection(true) ;
                    MySharedPreferences.getInstance().setString(TOKEN, "");
                    startLogin();
                }else {
                    startMain();
                }
                finish();
            }
        }else {
            start();
        }
    }

    private void start(){
        new Handler() {
            public void handleMessage(Message msg) {
                if(StringUtils.isEmpty(UtilTool.getTocoId())||StringUtils.isEmpty(UtilTool.getToken())){
                    WsConnection.getInstance().setOutConnection(true) ;
                    MySharedPreferences.getInstance().setString(TOKEN, "");
                    startMain();
                    finish();
                    return;
                }
                if(!UtilTool.compareTokenTime(MySharedPreferences.getInstance().getLong(TOKEN_TIME))){
                    startMain();
                    finish();
                    return;
                }
                if (UtilTool.getToken().equals("bearer")) {
                    startMain();
                    finish();
                } else {
                    if (UtilTool.isNetworkAvailable(StartActivity.this)) {
                        RetrofitUtil.getInstance(StartActivity.this)
                                .getServer()
                                .updataToken(UtilTool.getToken())
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                                .subscribe(new Observer<BaseInfo>() {
                                    @Override
                                    public void onSubscribe(@NonNull Disposable d) {

                                    }

                                    @Override
                                    public void onNext(@NonNull BaseInfo baseInfo) {
                                        if (baseInfo.getStatus() == 1) {
                                            WsConnection.getInstance().setOutConnection(false);
                                            MySharedPreferences.getInstance().setString(TOKEN, baseInfo.getMessage());
                                            MySharedPreferences.getInstance().setLong(TOKEN_TIME,System.currentTimeMillis());
                                            UtilTool.Log("日志", baseInfo.getMessage());
                                            startMain();
                                            finish();
                                        } else {
//                                            MySharedPreferences.getInstance().setString(TOKEN, "");
                                            startMain();
                                            finish();
                                        }
                                    }

                                    @Override
                                    public void onError(@NonNull Throwable e) {
//                                        MySharedPreferences.getInstance().setString(TOKEN, "");
                                        startMain();
                                        UtilTool.Log("日志", e.getMessage());
                                        finish();
                                    }

                                    @Override
                                    public void onComplete() {

                                    }
                                });
                    } else {
                        /*if (StringUtils.isEmpty(MySharedPreferences.getInstance().getString(TOCOID))) {
                            startActivity(new Intent(StartActivity.this, InitialActivity.class));
                        } else {
                           startMain();
                        }*/
//                        MySharedPreferences.getInstance().setString(TOKEN, "");
                        startMain();
                        finish();
                    }
                }
            }
        }.sendEmptyMessageDelayed(0, 2000);
    }

    private void startMain(){
        Intent intent=new Intent(StartActivity.this,MainActivity.class);
        Bundle bundle=getIntent().getExtras();
        if(bundle!=null)
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void startLogin(){
        Intent intent=new Intent(StartActivity.this,LoginActivity.class);
        startActivity(intent);
    }
}
