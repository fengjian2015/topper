package com.bclould.tea.ui.activity;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bclould.tea.Presenter.GoogleVerificationPresenter;
import com.bclould.tea.Presenter.RegisterPresenter;
import com.bclould.tea.R;
import com.bclould.tea.base.BaseActivity;
import com.bclould.tea.base.MyApp;
import com.bclould.tea.model.GoogleInfo;
import com.bclould.tea.network.DownLoadApk;
import com.bclould.tea.ui.widget.CurrencyDialog;
import com.bclould.tea.ui.widget.DeleteCacheDialog;
import com.bclould.tea.utils.ActivityUtil;
import com.bclould.tea.utils.AnimatorTool;
import com.bclould.tea.utils.MySharedPreferences;
import com.bclould.tea.utils.UtilTool;
import com.bumptech.glide.Glide;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.bclould.tea.Presenter.LoginPresenter.EMAIL;

/**
 * Created by GA on 2017/11/29.
 */
@RequiresApi(api = Build.VERSION_CODES.N)
public class GoogleVerificationActivity extends BaseActivity {

    @Bind(R.id.bark)
    ImageView mBark;
    @Bind(R.id.rl_title)
    RelativeLayout mRlTitle;
    @Bind(R.id.xx)
    TextView mXx;
    @Bind(R.id.tv)
    TextView mTv;
    @Bind(R.id.tv_download)
    TextView mTvDownload;
    @Bind(R.id.iv_qr_code)
    ImageView mIvQrCode;
    @Bind(R.id.tv_email)
    TextView mTvEmail;
    @Bind(R.id.secret_key)
    TextView mSecretKey;
    @Bind(R.id.btn_copy)
    Button mBtnCopy;
    @Bind(R.id.et_code)
    EditText mEtCode;
    @Bind(R.id.btn_finish)
    Button mBtnFinish;
    @Bind(R.id.ll_binding)
    LinearLayout mLlBinding;
    @Bind(R.id.ll_binding_status)
    LinearLayout mLlBindingStatus;
    @Bind(R.id.tv_email2)
    TextView mTvEmail2;
    @Bind(R.id.tv2)
    TextView mTv2;
    @Bind(R.id.et_vcode)
    EditText mEtVcode;
    @Bind(R.id.tv_send)
    TextView mTvSend;
    @Bind(R.id.btn_unbinding)
    Button mBtnUnbinding;
    @Bind(R.id.ll_unbinding)
    LinearLayout mLlUnbinding;
    @Bind(R.id.tv_hint)
    TextView mTvHint;
    @Bind(R.id.tv_unbinding)
    TextView mTvUnbinding;
    @Bind(R.id.iv2)
    ImageView mIv2;
    @Bind(R.id.ll_error)
    LinearLayout mLlError;
    private GoogleVerificationPresenter mGoogleVerificationPresenter;
    private CurrencyDialog mCurrencyDialog;
    private RegisterPresenter mRegisterPresenter;
    private String mEmail;
    private DeleteCacheDialog mDeleteCacheDialog;
    private int mRecLen;
    Timer mTimer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_verification);
        ButterKnife.bind(this);
        mRegisterPresenter = new RegisterPresenter(this);
        MyApp.getInstance().addActivity(this);
        initInterface();
    }

    private void initInterface() {
        mEmail = MySharedPreferences.getInstance().getString(EMAIL);
        mTvEmail.setText(mEmail);
        mTvEmail2.setText(mEmail);
        getGoogleKey();
    }

    private void getGoogleKey() {
        mGoogleVerificationPresenter = new GoogleVerificationPresenter(this);
        mGoogleVerificationPresenter.getGoogleKey(new GoogleVerificationPresenter.CallBack2() {
            @Override
            public void send(GoogleInfo googleInfo) {
                if (ActivityUtil.isActivityOnTop(GoogleVerificationActivity.this)) {
                    mLlError.setVisibility(View.GONE);
                    UtilTool.Log("谷歌", googleInfo.getIs_google_verify() + "");
                    if (googleInfo.getIs_google_verify() == 1) {
                        mLlBindingStatus.setVisibility(View.VISIBLE);
                        mLlUnbinding.setVisibility(View.GONE);
                        mLlBinding.setVisibility(View.GONE);
                    } else {
                        mLlBinding.setVisibility(View.VISIBLE);
                        mLlUnbinding.setVisibility(View.GONE);
                        mLlBindingStatus.setVisibility(View.GONE);
                        mSecretKey.setText(googleInfo.getKey());
                        Glide.with(GoogleVerificationActivity.this).load(googleInfo.getImg()).into(mIvQrCode);
                    }
                }
            }

            @Override
            public void error() {
                if (ActivityUtil.isActivityOnTop(GoogleVerificationActivity.this)) {
                    mLlBindingStatus.setVisibility(View.GONE);
                    mLlUnbinding.setVisibility(View.GONE);
                    mLlBinding.setVisibility(View.GONE);
                    mLlError.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private boolean checkEdit2() {
        if (mEtVcode.getText().toString().trim().equals("")) {
            Toast.makeText(this, getResources().getString(R.string.toast_vcode), Toast.LENGTH_SHORT).show();
            AnimatorTool.getInstance().editTextAnimator(mEtVcode);
        } else {
            return true;
        }
        return false;
    }

    private void showHintDialog() {
        mCurrencyDialog = new CurrencyDialog(R.layout.dialog_query, this, R.style.dialog);
        mCurrencyDialog.show();
    }

    private void unBinding() {
        String email = mTvEmail2.getText().toString().trim();
        String vcode = mEtVcode.getText().toString().trim();
        mGoogleVerificationPresenter.unBinding(vcode, new GoogleVerificationPresenter.CallBack() {
            @Override
            public void send() {
                mLlBinding.setVisibility(View.VISIBLE);
                mLlUnbinding.setVisibility(View.GONE);
                mLlBindingStatus.setVisibility(View.GONE);
                getGoogleKey();
            }
        });
    }

    //绑定谷歌验证器
    private void bindGoogle() {
        String code = mEtCode.getText().toString().trim();
        mGoogleVerificationPresenter.bindGoogle(code);
    }

    //验证手机号和密码
    private boolean checkEdit() {
        if (mEtCode.getText().toString().trim().equals("")) {
            Toast.makeText(this, getResources().getString(R.string.get_code), Toast.LENGTH_SHORT).show();
            AnimatorTool.getInstance().editTextAnimator(mEtCode);
        } else {
            return true;
        }
        return false;
    }

    private void copySecretKey() {
        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        cm.setText(mSecretKey.getText());
        Toast.makeText(this, getString(R.string.copy_succeed), Toast.LENGTH_LONG).show();
    }


    @OnClick({R.id.ll_error, R.id.bark, R.id.tv_hint, R.id.tv_download, R.id.btn_copy, R.id.btn_finish, R.id.tv_send, R.id.btn_unbinding, R.id.tv_unbinding})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;
            case R.id.tv_hint:
                showHintDialog();
                break;
            case R.id.tv_download:
                http:
//qufenqi-static.oss-cn-hangzhou.aliyuncs.com/mobile/android/Authenticator_49.apk
                showDialog();
                break;
            case R.id.btn_copy:
                copySecretKey();
                break;
            case R.id.btn_finish:
                if (checkEdit())
                    bindGoogle();
                break;
            case R.id.tv_send:
                    sendVcode();
                break;
            case R.id.tv_unbinding:
                mLlBindingStatus.setVisibility(View.GONE);
                mLlBinding.setVisibility(View.GONE);
                mLlUnbinding.setVisibility(View.VISIBLE);
                break;
            case R.id.btn_unbinding:
                if (checkEdit2()) {
                    unBinding();
                }
                break;
            case R.id.ll_error:
                getGoogleKey();
                break;
        }
    }

    private void showDialog() {
        if (mDeleteCacheDialog == null)
            mDeleteCacheDialog = new DeleteCacheDialog(R.layout.dialog_delete_cache, this, R.style.dialog);
        mDeleteCacheDialog.show();
        mDeleteCacheDialog.setTitle(getString(R.string.download_google_verify));
        Button cancel = (Button) mDeleteCacheDialog.findViewById(R.id.btn_cancel);
        Button confirm = (Button) mDeleteCacheDialog.findViewById(R.id.btn_confirm);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDeleteCacheDialog.dismiss();
            }
        });
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!canDownloadState()) {
                    showDownloadSetting();
                    return;
                }
                DownLoadApk.download(GoogleVerificationActivity.this, "http://qufenqi-static.oss-cn-hangzhou.aliyuncs.com/mobile/android/Authenticator_49.apk", "用于身份验证", "谷歌验证器");
                mDeleteCacheDialog.dismiss();
            }
        });
    }

    //获取intent意图
    private boolean intentAvailable(Intent intent) {
        PackageManager packageManager = getPackageManager();
        List list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    //更新完成弹出安装
    private void showDownloadSetting() {
        String packageName = "com.android.providers.downloads";
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + packageName));
        if (intentAvailable(intent)) {
            startActivity(intent);
        }
    }

    //下载状态
    private boolean canDownloadState() {
        try {
            int state = this.getPackageManager().getApplicationEnabledSetting("com.android.providers.downloads");

            if (state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED
                    || state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_USER
                    || state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_UNTIL_USED) {
                return false;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private void sendVcode() {
        mRegisterPresenter.sendRegcode(mEmail, new RegisterPresenter.CallBack() {
            @Override
            public void send() {
                Toast.makeText(GoogleVerificationActivity.this, getString(R.string.send_succeed), Toast.LENGTH_SHORT).show();
                mRecLen = 60;
                mTimer = new Timer();
                mTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {      // UI thread
                            @Override
                            public void run() {
                                mRecLen--;
                                mTvSend.setText(mRecLen + "s" + getString(R.string.back_send));
                                if (mRecLen <= 0) {
                                    if (mTimer != null) {
                                        mTimer.cancel();
                                        mTimer = null;
                                    }
                                    mTvSend.setEnabled(true);
                                    mTvSend.setText(getString(R.string.send));
                                } else {
                                    mTvSend.setEnabled(false);
                                }
                            }
                        });
                    }
                }, 1000, 1000);
            }
        });
    }
}
