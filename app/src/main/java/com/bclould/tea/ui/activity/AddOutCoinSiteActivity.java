package com.bclould.tea.ui.activity;

import android.app.Dialog;
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

import com.bclould.tea.Presenter.OutCoinSitePresenter;
import com.bclould.tea.R;
import com.bclould.tea.base.BaseActivity;
import com.bclould.tea.base.MyApp;
import com.bclould.tea.model.InCoinInfo;
import com.bclould.tea.utils.AnimatorTool;
import com.bclould.tea.utils.AppLanguageUtils;
import com.bclould.tea.utils.MySharedPreferences;
import com.bclould.tea.utils.UtilTool;
import com.bclould.tea.utils.permissions.AuthorizationUserTools;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by GA on 2017/11/17.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class AddOutCoinSiteActivity extends BaseActivity {

    @Bind(R.id.bark)
    ImageView mBark;
    @Bind(R.id.et_address)
    EditText mEtAddress;
    @Bind(R.id.xx)
    TextView mXx;
    @Bind(R.id.iv_sao_ma)
    ImageView mIvSaoMa;
    @Bind(R.id.et_remark)
    EditText mEtRemark;
    @Bind(R.id.btn_preserve)
    Button mBtnPreserve;
    @Bind(R.id.et_google_code)
    EditText mEtGoogleCode;
    private int mId;
    private Dialog mBottomDialog;
    private int QRCODE = 0;
    private String mCoinName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_out_coin_site);
        ButterKnife.bind(this);
        initInterface();
        MyApp.getInstance().addActivity(this);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(AppLanguageUtils.attachBaseContext(newBase, MySharedPreferences.getInstance().getString(newBase.getString(R.string.language_pref_key))));
    }

    private void initInterface() {
        mId = getIntent().getIntExtra("id", 0);
        mCoinName = getIntent().getStringExtra("coinName");
        UtilTool.Log("地址", mCoinName);
    }

    //点击事件处理
    @OnClick({R.id.bark, R.id.iv_sao_ma, R.id.btn_preserve})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;
            case R.id.iv_sao_ma:
                //跳转扫描二维码页面
                if (!AuthorizationUserTools.isCameraCanUse(this))
                    return;
                Intent intent = new Intent(this, ScanQRCodeActivity.class);
                intent.putExtra("code", QRCODE);
                startActivityForResult(intent, QRCODE);
                break;
            case R.id.btn_preserve:
                //检测输入框
                if (checkEdit2())
                    addCoinOutAddress();
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            try {
                String result = data.getStringExtra("result");
                Gson gson = new Gson();
                InCoinInfo inCoinInfo = gson.fromJson(result, InCoinInfo.class);
                if (inCoinInfo.getCoin().equals(mCoinName)) {
                    mEtAddress.setText(inCoinInfo.getAddress());
                } else {
                    Toast.makeText(this, getString(R.string.scan_no) + mCoinName + getString(R.string.address), Toast.LENGTH_SHORT).show();
                }
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
                Toast.makeText(this, getString(R.string.qr_code_error), Toast.LENGTH_SHORT).show();
            }
        }
    }

    /*//显示谷歌验证弹窗
    private void showGoogleDialog() {
        mBottomDialog = new Dialog(this, R.style.BottomDialog2);
        View contentView = LayoutInflater.from(this).inflate(R.layout.dialog_google_code, null);
        //获得dialog的window窗口
        Window window = mBottomDialog.getWindow();
        window.getDecorView().setPadding(0, 0, 0, 0);
        //获得window窗口的属性
        WindowManager.LayoutParams lp = window.getAttributes();
        //设置窗口宽度为充满全屏
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        //将设置好的属性set回去
        window.setAttributes(lp);
        window.setGravity(Gravity.BOTTOM);
        window.setWindowAnimations(BottomDialog);
        mBottomDialog.setContentView(contentView);
        mBottomDialog.show();
        Button confirm = (Button) mBottomDialog.findViewById(R.id.btn_confirm);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addCoinOutAddress();
            }
        });
    }*/

    //添加地址网络请求
    private void addCoinOutAddress() {
        String address = mEtAddress.getText().toString().trim();
        String memo = mEtRemark.getText().toString().trim();
        String googleCode = mEtGoogleCode.getText().toString();
        OutCoinSitePresenter outCoinSitePresenter = new OutCoinSitePresenter(this);
        outCoinSitePresenter.addCoinOutAddress(mId, memo, address, googleCode);
    }

    //判断输入框是否有内容
    private boolean checkEdit2() {
        if (mEtAddress.getText().toString().trim().equals("")) {
            Toast.makeText(this, getResources().getString(R.string.toast_address), Toast.LENGTH_SHORT).show();
            AnimatorTool.getInstance().editTextAnimator(mEtAddress);
        } else if (mEtRemark.getText().toString().trim().equals("")) {
            Toast.makeText(this, getString(R.string.toast_tag), Toast.LENGTH_SHORT).show();
            AnimatorTool.getInstance().editTextAnimator(mEtRemark);
        } else if (mEtGoogleCode.getText().toString().trim().equals("")) {
            Toast.makeText(this, getString(R.string.toast_google_code), Toast.LENGTH_SHORT).show();
            AnimatorTool.getInstance().editTextAnimator(mEtGoogleCode);
        } else {
            return true;
        }
        return false;
    }
}
