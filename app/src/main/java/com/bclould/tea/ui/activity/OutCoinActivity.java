package com.bclould.tea.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bclould.tea.Presenter.CurrencyInOutPresenter;
import com.bclould.tea.R;
import com.bclould.tea.base.BaseActivity;
import com.bclould.tea.base.MyApp;
import com.bclould.tea.model.BaseInfo;
import com.bclould.tea.model.InCoinInfo;
import com.bclould.tea.ui.widget.DeleteCacheDialog;
import com.bclould.tea.ui.widget.PWDDialog;
import com.bclould.tea.utils.ActivityUtil;
import com.bclould.tea.utils.AnimatorTool;
import com.bclould.tea.utils.AppLanguageUtils;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by GA on 2017/11/3.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class OutCoinActivity extends BaseActivity {

    private static final int SELECTORSITE = 1;
    private static final int SCANOUTSITE = 3;
    @Bind(R.id.bark)
    ImageView mBark;
    @Bind(R.id.tv_record)
    TextView mTvRecord;
    @Bind(R.id.tv_out_coin_site)
    EditText mTvOutCoinSite;
    @Bind(R.id.iv_qr_code)
    ImageView mIvQrCode;
    @Bind(R.id.iv_selector_site)
    ImageView mIvSelectorSite;
    @Bind(R.id.et_coin_count)
    EditText mEtCoinCount;
    @Bind(R.id.tv_yu_e)
    TextView mTvYuE;
    @Bind(R.id.tv_coin_count)
    TextView mTvCoinCount;
    @Bind(R.id.tv_coin_name)
    TextView mTvCoinName;
    @Bind(R.id.et_google_code)
    EditText mEtGoogleCode;
    @Bind(R.id.et_remark)
    EditText mEtRemark;
    @Bind(R.id.btn_confirm)
    Button mBtnConfirm;
    @Bind(R.id.iv)
    ImageView mIv;
    @Bind(R.id.tv_desc)
    TextView mTvDesc;
    @Bind(R.id.ll_data)
    LinearLayout mLlData;
    @Bind(R.id.iv2)
    ImageView mIv2;
    @Bind(R.id.ll_error)
    LinearLayout mLlError;


    private int mId;
    private int mSiteId;
    private String mCoinName;
    private String mOver;
    private String mSite;
    private PWDDialog pwdDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_out_coin);
        ButterKnife.bind(this);
        MyApp.getInstance().addActivity(this);
        initIntent();
        initData();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(AppLanguageUtils.attachBaseContext(newBase, newBase.getString(R.string.language_pref_key)));
    }

    private void initData() {
        CurrencyInOutPresenter currencyInOutPresenter = new CurrencyInOutPresenter(this);
        currencyInOutPresenter.outCoinDesc(mId, new CurrencyInOutPresenter.CallBack() {
            @Override
            public void send(BaseInfo.DataBean data) {
                if (ActivityUtil.isActivityOnTop(OutCoinActivity.this)) {
                    mLlData.setVisibility(View.VISIBLE);
                    mLlError.setVisibility(View.GONE);
                    if (data.getDesc() != null) {
                        String desc = data.getDesc().replace("\\n", "\n");
                        mTvDesc.setText(desc);
                    }
                }
            }

            @Override
            public void error() {
                if (ActivityUtil.isActivityOnTop(OutCoinActivity.this)) {
                    mLlData.setVisibility(View.GONE);
                    mLlError.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void initIntent() {
        Intent intent = getIntent();
        mId = intent.getIntExtra("id", 0);
        mCoinName = intent.getStringExtra("coinName");
        mOver = intent.getStringExtra("over");
        mTvCoinName.setText(mCoinName);
        mTvCoinCount.setText(mOver);
    }

    @OnClick({R.id.ll_error,R.id.iv_qr_code, R.id.bark, R.id.tv_record, R.id.iv_selector_site, R.id.btn_confirm})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;case R.id.ll_error:
                initData();
                break;
            case R.id.iv_qr_code:
                Intent intent = new Intent(this, ScanQRCodeActivity.class);
                intent.putExtra("code", SCANOUTSITE);
                startActivityForResult(intent, SCANOUTSITE);
                break;
            case R.id.tv_record:
                skipRecord();
                break;
            case R.id.iv_selector_site:
                selectorSite();
                break;
            case R.id.btn_confirm:
                if (editCheck()) {
                    showPWDialog();
                }
                break;
        }
    }

    private void selectorSite() {
        Intent intent = new Intent(this, OutCoinSiteActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("id", mId);
        bundle.putString("coinName", mCoinName);
        intent.putExtras(bundle);
        startActivityForResult(intent, SELECTORSITE);
    }

    private void skipRecord() {
        Intent intent = new Intent(this, BillDetailsActivity.class);
        intent.putExtra("type", 1);
        intent.putExtra("coin_id", mId + "");
        intent.putExtra("coin_name", mCoinName);
        startActivity(intent);
    }

    private void showPWDialog() {
        pwdDialog=new PWDDialog(this);
        pwdDialog.setOnPWDresult(new PWDDialog.OnPWDresult() {
            @Override
            public void success(String password) {
                outCoin(password);
            }
        });
        String count = mEtCoinCount.getText().toString();
        pwdDialog.showDialog(count,mCoinName,mCoinName + "提币",null,null);
    }

    public void showHintDialog() {
        final DeleteCacheDialog deleteCacheDialog = new DeleteCacheDialog(R.layout.dialog_pw_hint, this, R.style.dialog);
        deleteCacheDialog.show();
        deleteCacheDialog.setCanceledOnTouchOutside(false);
        TextView retry = (TextView) deleteCacheDialog.findViewById(R.id.tv_retry);
        TextView findPassword = (TextView) deleteCacheDialog.findViewById(R.id.tv_find_password);
        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteCacheDialog.dismiss();
                pwdDialog.show();
            }
        });
        findPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteCacheDialog.dismiss();
                startActivity(new Intent(OutCoinActivity.this, PayPasswordActivity.class));
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECTORSITE) {
                mSiteId = data.getIntExtra("siteId", 0);
                mSite = data.getStringExtra("address");
                mTvOutCoinSite.setText(mSite);
            } else if (requestCode == SCANOUTSITE) {
                try {
                    String result = data.getStringExtra("address");
                    Gson gson = new Gson();
                    InCoinInfo inCoinInfo = gson.fromJson(result, InCoinInfo.class);
                    if (inCoinInfo.getCoin().equals(mCoinName)) {
                        mSite = inCoinInfo.getAddress();
                        mTvOutCoinSite.setText(mSite);
                    } else {
                        Toast.makeText(this, getString(R.string.scan_no) + mCoinName + getString(R.string.address), Toast.LENGTH_SHORT).show();
                    }
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                    Toast.makeText(this, getString(R.string.qr_code_error), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void outCoin(String password) {
        mSite = mTvOutCoinSite.getText().toString().trim();
        String count = mEtCoinCount.getText().toString().trim();
        String googleCode = mEtGoogleCode.getText().toString().trim();
        String remark = mEtRemark.getText().toString().trim();
        CurrencyInOutPresenter currencyInOutPresenter = new CurrencyInOutPresenter(this);
        currencyInOutPresenter.coinOutAction(mId + "", mSite, count, googleCode, password, remark);
    }

    private boolean editCheck() {
        if (mTvOutCoinSite.getText().toString().isEmpty()) {
            Toast.makeText(this, getString(R.string.toast_out_coin_site), Toast.LENGTH_SHORT).show();
            AnimatorTool.getInstance().editTextAnimator(mTvOutCoinSite);
        } else if (mEtCoinCount.getText().toString().isEmpty()) {
            Toast.makeText(this, getString(R.string.toast_count), Toast.LENGTH_SHORT).show();
            AnimatorTool.getInstance().editTextAnimator(mEtCoinCount);
        } else if (mEtGoogleCode.getText().toString().isEmpty()) {
            Toast.makeText(this, getString(R.string.toast_google_code), Toast.LENGTH_SHORT).show();
            AnimatorTool.getInstance().editTextAnimator(mEtGoogleCode);
        } else if (mEtRemark.getText().toString().isEmpty()) {
            Toast.makeText(this, getString(R.string.toast_tag), Toast.LENGTH_SHORT).show();
            AnimatorTool.getInstance().editTextAnimator(mEtRemark);
        } else {
            return true;
        }
        return false;
    }
}
