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
import android.widget.TextView;
import android.widget.Toast;

import com.bclould.tea.Presenter.CurrencyInOutPresenter;
import com.bclould.tea.R;
import com.bclould.tea.base.BaseActivity;
import com.bclould.tea.base.MyApp;
import com.bclould.tea.ui.widget.DeleteCacheDialog;
import com.bclould.tea.ui.widget.PWDDialog;
import com.bclould.tea.utils.AnimatorTool;
import com.bclould.tea.utils.AppLanguageUtils;
import com.bclould.tea.utils.MySharedPreferences;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Created by GA on 2018/3/15.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class TransferAccountsActivity extends BaseActivity {
    @Bind(R.id.et_email)
    EditText mEtEmail;
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
    private int mId;
    private int mSiteId;
    private String mCoinName;
    private String mOver;
    private PWDDialog pwdDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer_accounts);
        ButterKnife.bind(this);
        setTitle(getString(R.string.transfer),getString(R.string.record));
        MyApp.getInstance().addActivity(this);
        initIntent();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(AppLanguageUtils.attachBaseContext(newBase, MySharedPreferences.getInstance().getString(newBase.getString(R.string.language_pref_key))));
    }

    private void initIntent() {
        Intent intent = getIntent();
        mId = intent.getIntExtra("id", 0);
        mCoinName = intent.getStringExtra("coinName");
        mOver = intent.getStringExtra("over");
        mTvCoinCount.setText(mOver);
        mTvCoinName.setText(mCoinName);
    }

    @OnClick({R.id.bark, R.id.tv_add, R.id.btn_confirm})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;
            case R.id.tv_add:
                Intent intent = new Intent(this, BillDetailsActivity.class);
                intent.putExtra("type", 2);
                startActivity(intent);
                break;
            case R.id.btn_confirm:
                if (editCheck()) {
                    showPWDialog();
                }
                break;
        }
    }

    public void showHintDialog() {
        final DeleteCacheDialog deleteCacheDialog = new DeleteCacheDialog(R.layout.dialog_pw_hint, this,R.style.dialog);
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
                startActivity(new Intent(TransferAccountsActivity.this, PayPasswordActivity.class));
            }
        });
    }

    private void showPWDialog() {
        pwdDialog=new PWDDialog(this);
        pwdDialog.setOnPWDresult(new PWDDialog.OnPWDresult() {
            @Override
            public void success(String password) {
                transferAccounts(password);
            }
        });
        String count = mEtCoinCount.getText().toString();
        pwdDialog.showDialog(count,mCoinName,mCoinName + getString(R.string.transfer),null,null);
    }

    private void transferAccounts(String password) {
        String email = mEtEmail.getText().toString().trim();
        String count = mEtCoinCount.getText().toString().trim();
        Double countf = Double.parseDouble(count);
        String google = mEtGoogleCode.getText().toString().trim();
        String remark = mEtRemark.getText().toString().trim();
        CurrencyInOutPresenter currencyInOutPresenter = new CurrencyInOutPresenter(this);
        currencyInOutPresenter.transfer(mCoinName, email, countf, google, password);
    }

    private boolean editCheck() {
        if (mEtEmail.getText().toString().isEmpty()) {
            Toast.makeText(this, getString(R.string.toast_address), Toast.LENGTH_SHORT).show();
            AnimatorTool.getInstance().editTextAnimator(mEtEmail);
        } else if (mEtCoinCount.getText().toString().isEmpty()) {
            Toast.makeText(this, getString(R.string.toast_count), Toast.LENGTH_SHORT).show();
            AnimatorTool.getInstance().editTextAnimator(mEtCoinCount);
        } else if (mEtGoogleCode.getText().toString().isEmpty()) {
            Toast.makeText(this, getString(R.string.toast_google_code), Toast.LENGTH_SHORT).show();
            AnimatorTool.getInstance().editTextAnimator(mEtGoogleCode);
        } else {
            return true;
        }
        return false;
    }


}
