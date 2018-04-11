package com.bclould.tocotalk.ui.activity;

import android.app.Dialog;
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

import com.bclould.tocotalk.Presenter.AddOutCoinSitePresenter;
import com.bclould.tocotalk.R;
import com.bclould.tocotalk.base.BaseActivity;
import com.bclould.tocotalk.base.MyApp;
import com.bclould.tocotalk.utils.AnimatorTool;

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
                    addCoinOutAddress();
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
        AddOutCoinSitePresenter addOutCoinSitePresenter = new AddOutCoinSitePresenter(this);
        addOutCoinSitePresenter.addCoinOutAddress(mId, memo, address, googleCode);
    }

    //判断输入框是否有内容
    private boolean checkEdit2() {
        if (mEtAddress.getText().toString().trim().equals("")) {
            Toast.makeText(this, getResources().getString(R.string.toast_address), Toast.LENGTH_SHORT).show();
            AnimatorTool.getInstance().editTextAnimator(mEtAddress);
        } else if (mEtRemark.getText().toString().trim().equals("")) {
            Toast.makeText(this, "备注不能为空", Toast.LENGTH_SHORT).show();
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
