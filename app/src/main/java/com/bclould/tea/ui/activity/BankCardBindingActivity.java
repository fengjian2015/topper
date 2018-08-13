package com.bclould.tea.ui.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.ocr.sdk.OCR;
import com.baidu.ocr.sdk.OnResultListener;
import com.baidu.ocr.sdk.exception.OCRError;
import com.baidu.ocr.sdk.model.AccessToken;
import com.baidu.ocr.sdk.model.BankCardParams;
import com.baidu.ocr.sdk.model.BankCardResult;
import com.bclould.tea.Presenter.BankCardPresenter;
import com.bclould.tea.Presenter.RealNamePresenter;
import com.bclould.tea.R;
import com.bclould.tea.base.BaseActivity;
import com.bclould.tea.base.MyApp;
import com.bclould.tea.model.BankCardInfo;
import com.bclould.tea.ui.adapter.BottomDialogRVAdapter3;
import com.bclould.tea.ui.widget.DeleteCacheDialog;
import com.bclould.tea.ui.widget.LoadingProgressDialog;
import com.bclould.tea.utils.AnimatorTool;
import com.bclould.tea.utils.AppLanguageUtils;
import com.bclould.tea.utils.Constants;
import com.bclould.tea.utils.ToastShow;
import com.bclould.tea.utils.UtilTool;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.bclould.tea.R.style.BottomDialog;

/**
 * Created by GA on 2017/9/26.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class BankCardBindingActivity extends BaseActivity {

    private static final int REQUEST_CODE_CAMERA = 1;
    @Bind(R.id.bark)
    ImageView mBark;
    @Bind(R.id.btn_next)
    Button mBtnNext;
    @Bind(R.id.et_card_number)
    EditText mEtCardNumber;
    @Bind(R.id.tv3)
    TextView mTv3;
    @Bind(R.id.tv_state)
    TextView mTvState;
    @Bind(R.id.iv_jiantou2)
    ImageView mIvJiantou2;
    @Bind(R.id.rl_selector_state)
    RelativeLayout mRlSelectorState;
    @Bind(R.id.tv)
    TextView mTv;
    @Bind(R.id.iv_scan)
    ImageView mIvScan;
    private BankCardPresenter mBankCardPresenter;
    private Dialog mStateDialog;
    private int mStateId;
    private LoadingProgressDialog mProgressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_card_binding);
        ButterKnife.bind(this);
        MyApp.getInstance().addActivity(this);
        mBankCardPresenter = new BankCardPresenter(this);
        initData();
        initORC();
    }

    private void initORC() {
        OCR.getInstance(this).initAccessTokenWithAkSk(new OnResultListener<AccessToken>() {
            @Override
            public void onResult(AccessToken result) {
                UtilTool.Log("bank", result.getAccessToken());
            }

            @Override
            public void onError(OCRError error) {
                error.printStackTrace();
            }
        }, getApplicationContext(), Constants.ORC_AK, Constants.ORC_SK);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(AppLanguageUtils.attachBaseContext(newBase, newBase.getString(R.string.language_pref_key)));
    }

    private void initData() {
        RealNamePresenter realNamePresenter = new RealNamePresenter(this);
        realNamePresenter.realNameInfo(new RealNamePresenter.CallBack2() {
            @Override
            public void send(int type, String mark) {
                if (type == 1) {
                    showDialog();
                    mBtnNext.setClickable(false);
                } else if (type == 4) {
                    showDialog();
                    mBtnNext.setClickable(false);
                    Toast.makeText(BankCardBindingActivity.this, getString(R.string.verify_error_hint), Toast.LENGTH_SHORT).show();
                } else if (type == 2) {
                    mBtnNext.setClickable(false);
                    Toast.makeText(BankCardBindingActivity.this, getString(R.string.verify_check_pending), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void error() {

            }
        });
    }


    //验证手机号和密码
    private boolean checkEdit() {
        if (mEtCardNumber.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, getString(R.string.toast_card_number), Toast.LENGTH_SHORT).show();
            AnimatorTool.getInstance().editTextAnimator(mEtCardNumber);
        }
        if (mTvState.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, getString(R.string.toast_state), Toast.LENGTH_SHORT).show();
            AnimatorTool.getInstance().editTextAnimator(mRlSelectorState);
        } else {
            return true;
        }
        return false;
    }

    @OnClick({R.id.bark, R.id.btn_next, R.id.rl_selector_state, R.id.iv_scan})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;
            case R.id.iv_scan:
                Intent intent = new Intent(BankCardBindingActivity.this, com.baidu.ocr.ui.camera.CameraActivity.class);
                intent.putExtra(com.baidu.ocr.ui.camera.CameraActivity.KEY_OUTPUT_FILE_PATH,
                        UtilTool.getSaveFile(getApplication()).getAbsolutePath());
                intent.putExtra(com.baidu.ocr.ui.camera.CameraActivity.KEY_CONTENT_TYPE,
                        com.baidu.ocr.ui.camera.CameraActivity.CONTENT_TYPE_BANK_CARD);
                startActivityForResult(intent, REQUEST_CODE_CAMERA);
                break;
            case R.id.rl_selector_state:
                showStateDialog();
                break;
            case R.id.btn_next:
                if (checkEdit()) {
                    next();
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CAMERA && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                String contentType = data.getStringExtra(com.baidu.ocr.ui.camera.CameraActivity.KEY_CONTENT_TYPE);
                String filePath = UtilTool.getSaveFile(getApplicationContext()).getAbsolutePath();
                if (!TextUtils.isEmpty(contentType)) {
                    if (com.baidu.ocr.ui.camera.CameraActivity.CONTENT_TYPE_BANK_CARD.equals(contentType)) {
                        recIDCard(filePath);
                    }
                }
            }
        }
    }

    private void recIDCard(String filePath) {
        showLoadingDialog();
        BankCardParams param = new BankCardParams();
        param.setImageFile(new File(filePath));
        OCR.getInstance(this).recognizeBankCard(param, new OnResultListener<BankCardResult>() {
            @Override
            public void onResult(BankCardResult result) {
                hideLoadingDialog();
                // 调用成功，返回BankCardResult对象
                if (result != null) {
                    mEtCardNumber.setText(result.getBankCardNumber());
                    /*Intent intent = new Intent(BankCardBindingActivity.this, BankCardBindingActivity2.class);
                    intent.putExtra("type", false);
                    intent.putExtra("card_number", result.getBankCardNumber());
                    intent.putExtra("card_type", result.getBankCardType());
                    intent.putExtra("bank_name", result.getBankName());
                    intent.putExtra("country_id", mStateId);
                    startActivity(intent);*/
                }
            }

            @Override
            public void onError(OCRError error) {
                hideLoadingDialog();
                // 调用失败，返回OCRError对象
                ToastShow.showToast(BankCardBindingActivity.this, getString(R.string.ocr_error));
            }
        });
    }

    private void showLoadingDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = LoadingProgressDialog.createDialog(this);
            mProgressDialog.setMessage(getString(R.string.discern));
        }

        mProgressDialog.show();
    }

    private void hideLoadingDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

    private void showStateDialog() {
        mStateDialog = new Dialog(this, R.style.BottomDialog2);
        View contentView = LayoutInflater.from(this).inflate(R.layout.dialog_bottom, null);
        //获得dialog的window窗口
        Window window = mStateDialog.getWindow();
        window.getDecorView().setPadding(0, 0, 0, 0);
        //获得window窗口的属性
        WindowManager.LayoutParams lp = window.getAttributes();
        //设置窗口宽度为充满全屏
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        //将设置好的属性set回去
        window.setAttributes(lp);
        window.setGravity(Gravity.BOTTOM);
        window.setWindowAnimations(BottomDialog);
        mStateDialog.setContentView(contentView);
        mStateDialog.show();
        RecyclerView recyclerView = (RecyclerView) mStateDialog.findViewById(R.id.recycler_view);
        TextView tvTitle = (TextView) mStateDialog.findViewById(R.id.tv_title);
        Button addCoin = (Button) mStateDialog.findViewById(R.id.btn_add_coin);
        Button cancel = (Button) mStateDialog.findViewById(R.id.btn_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mStateDialog.dismiss();
            }
        });
        addCoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(BankCardBindingActivity.this, MyAssetsActivity.class));
                mStateDialog.dismiss();
            }
        });
        tvTitle.setText(getString(R.string.selecotr_state));
        if (MyApp.getInstance().mStateList.size() != 0) {
            recyclerView.setVisibility(View.VISIBLE);
            addCoin.setVisibility(View.GONE);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(new BottomDialogRVAdapter3(this, MyApp.getInstance().mStateList));
        } else {
            recyclerView.setVisibility(View.GONE);
            addCoin.setVisibility(View.VISIBLE);
        }
    }

    public void hideDialog2(int id, String name) {
        mStateDialog.dismiss();
        mStateId = id;
        mTvState.setText(name);
        if (mStateId == 44) {
            mIvScan.setVisibility(View.VISIBLE);
        } else {
            mIvScan.setVisibility(View.GONE);
        }
    }

    private void next() {
        final String cardNumber = mEtCardNumber.getText().toString().replace(" ", "");
        mBankCardPresenter.bankCardInfo(cardNumber, mStateId, new BankCardPresenter.CallBack() {
            @Override
            public void send(BankCardInfo.DataBean data) {
                if (!BankCardBindingActivity.this.isDestroyed()) {
                    Intent intent = new Intent(BankCardBindingActivity.this, BankCardBindingActivity2.class);
                    if (data.getTruename() != null && data.getTruename().isEmpty()) {
                        showDialog();
                    } else {
                        intent.putExtra("data", data);
                        intent.putExtra("card_number", cardNumber);
                        intent.putExtra("country_id", mStateId);
                        intent.putExtra("type", true);
                        startActivity(intent);
                        finish();
                    }
                }
            }
        });
    }

    private void showDialog() {
        final DeleteCacheDialog deleteCacheDialog = new DeleteCacheDialog(R.layout.dialog_delete_cache, this, R.style.dialog);
        deleteCacheDialog.show();
        deleteCacheDialog.setCanceledOnTouchOutside(false);
        deleteCacheDialog.setTitle(getString(R.string.real_name_authentication_hint));
        Button cancel = (Button) deleteCacheDialog.findViewById(R.id.btn_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteCacheDialog.dismiss();
            }
        });
        Button confirm = (Button) deleteCacheDialog.findViewById(R.id.btn_confirm);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteCacheDialog.dismiss();
                startActivity(new Intent(BankCardBindingActivity.this, RealNameC1Activity.class));
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        OCR.getInstance(this).release();
    }
}
