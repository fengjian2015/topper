package com.dashiji.biyun.ui.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dashiji.biyun.Presenter.AddOutCoinSitePresenter;
import com.dashiji.biyun.R;
import com.dashiji.biyun.base.BaseActivity;
import com.dashiji.biyun.base.MyApp;
import com.dashiji.biyun.utils.AnimatorTool;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.dashiji.biyun.R.style.BottomDialog;

/**
 * Created by GA on 2017/11/17.
 */

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
    private int mId;
    private Dialog mBottomDialog;
    private int QRCODE = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_out_coin_site);
        ButterKnife.bind(this);
        initInterface();
        MyApp.getInstance().addActivity(this);
    }

    private void initInterface() {
        mId = getIntent().getIntExtra("id", 0);
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
                Intent intent = new Intent(this, ScanQRCodeActivity.class);
                intent.putExtra("code", QRCODE);
                startActivityForResult(intent, QRCODE);
                break;
            case R.id.btn_preserve:
                //检测输入框
                if (checkEdit2())
                    //添加提币地址
                    showGoogleDialog();
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            String result = data.getStringExtra("result");
            mEtAddress.setText(result);
        }
    }

    //显示谷歌验证弹窗
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
    }

    //添加地址网络请求
    private void addCoinOutAddress() {
        EditText etGoogleCode = (EditText) mBottomDialog.findViewById(R.id.et_google_code);
        String googleCode = etGoogleCode.getText().toString().trim();
        String address = mEtAddress.getText().toString().trim();
        String memo = mEtRemark.getText().toString().trim();
        if (!googleCode.isEmpty()) {
            AddOutCoinSitePresenter addOutCoinSitePresenter = new AddOutCoinSitePresenter(this);
            addOutCoinSitePresenter.addCoinOutAddress(mId, memo, address, googleCode);
        } else {
            AnimatorTool.getInstance().editTextAnimator(etGoogleCode);
            Toast.makeText(this, getString(R.string.toast_google_code), Toast.LENGTH_SHORT).show();
        }
    }

    //判断输入框是否有内容
    private boolean checkEdit2() {
        if (mEtAddress.getText().toString().trim().equals("")) {
            Toast.makeText(this, getResources().getString(R.string.toast_address), Toast.LENGTH_SHORT).show();
            AnimatorTool.getInstance().editTextAnimator(mEtAddress);
        } else if (mEtRemark.getText().toString().trim().equals("")) {
            Toast.makeText(this, getResources().getString(R.string.toast_vcode), Toast.LENGTH_SHORT).show();
            AnimatorTool.getInstance().editTextAnimator(mEtRemark);
        } else {
            return true;
        }
        return false;
    }
}
