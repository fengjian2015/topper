package com.bclould.tea.ui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.Toast;

import com.bclould.tea.R;
import com.bclould.tea.base.MyApp;
import com.bclould.tea.model.BaseInfo;
import com.bclould.tea.network.RetrofitUtil;
import com.bclould.tea.topperchat.WsConnection;
import com.bclould.tea.utils.MySharedPreferences;
import com.bclould.tea.utils.UtilTool;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.bclould.tea.Presenter.LoginPresenter.TOKEN;


/**
 * Created by GA on 2017/11/8.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class StartActivity extends AppCompatActivity {
    @Bind(R.id.iv_start)
    ImageView mIvStart;

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        ButterKnife.bind(this);
        WsConnection.getInstance().setOutConnection(true);
        new Handler() {
            public void handleMessage(Message msg) {
                if (UtilTool.getToken().equals("bearer")) {
                    startActivity(new Intent(StartActivity.this, MainActivity.class));
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
                                            UtilTool.Log("日志", baseInfo.getMessage());
                                            startActivity(new Intent(StartActivity.this, MainActivity.class));
                                            finish();
                                        } else {
                                            MySharedPreferences.getInstance().setString(TOKEN, "");
                                            startActivity(new Intent(StartActivity.this, MainActivity.class));
                                            finish();
                                        }
                                    }

                                    @Override
                                    public void onError(@NonNull Throwable e) {
                                        MySharedPreferences.getInstance().setString(TOKEN, "");
                                        startActivity(new Intent(StartActivity.this, MainActivity.class));
                                        /*if (e.getMessage().equals("HTTP 401 Unauthorized")) {
                                            finish();
                                            MySharedPreferences.getInstance().setString(TOKEN, "");
                                            startActivity(new Intent(StartActivity.this, MainActivity.class));
                                        } else if (e.getMessage().equals("connect timed out")) {
                                            finish();
                                            startActivity(new Intent(StartActivity.this, MainActivity.class));
                                        } else {
                                            finish();
                                            MySharedPreferences.getInstance().setString(TOKEN, "");
                                            startActivity(new Intent(StartActivity.this, InitialActivity.class));
                                        }*/
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
                            startActivity(new Intent(StartActivity.this, MainActivity.class));
                        }*/
                        MySharedPreferences.getInstance().setString(TOKEN, "");
                        startActivity(new Intent(StartActivity.this, MainActivity.class));
                        finish();
                    }
                }
            }
        }.sendEmptyMessageDelayed(0, 2000);
        MyApp.getInstance().addActivity(this);
    }
}
