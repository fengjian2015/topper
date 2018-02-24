package com.dashiji.biyun.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.dashiji.biyun.R;
import com.dashiji.biyun.base.MyApp;
import com.dashiji.biyun.model.BaseInfo;
import com.dashiji.biyun.network.RetrofitUtil;
import com.dashiji.biyun.utils.MySharedPreferences;
import com.dashiji.biyun.utils.UtilTool;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.dashiji.biyun.Presenter.LoginPresenter.TOKEN;


/**
 * Created by GA on 2017/11/8.
 */

public class StartActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        new Handler() {
            public void handleMessage(Message msg) {
                if (UtilTool.getToken().equals("bearer")) {
                    startActivity(new Intent(StartActivity.this, LoginActivity.class));
                    finish();
                } else {
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
                                        MySharedPreferences.getInstance().setString(TOKEN, baseInfo.getMessage());
                                        UtilTool.Log("日志", baseInfo.getMessage());
                                        startActivity(new Intent(StartActivity.this, MainActivity.class));
                                        finish();
                                    }
                                }

                                @Override
                                public void onError(@NonNull Throwable e) {
                                    if (e.getMessage().equals("HTTP 401 Unauthorized")) {
                                        finish();
                                        MySharedPreferences.getInstance().setString(TOKEN, "");
                                        startActivity(new Intent(StartActivity.this, LoginActivity.class));
                                    }else if(e.getMessage().equals("connect timed out")){
                                        finish();
                                        startActivity(new Intent(StartActivity.this, MainActivity.class));
                                    }
                                    Toast.makeText(StartActivity.this, "网络连接错误，请重启重试", Toast.LENGTH_SHORT).show();
                                    UtilTool.Log("日志", e.getMessage());
                                }

                                @Override
                                public void onComplete() {

                                }
                            });
                }
            }
        }.sendEmptyMessageDelayed(0, 2000);
        MyApp.getInstance().addActivity(this);
    }
}
