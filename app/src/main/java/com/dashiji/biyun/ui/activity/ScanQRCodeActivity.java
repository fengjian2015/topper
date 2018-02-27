package com.dashiji.biyun.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.dashiji.biyun.R;
import com.dashiji.biyun.base.MyApp;
import com.dashiji.biyun.ui.fragment.CloudMessageFragment;
import com.dashiji.biyun.utils.UtilTool;
import com.dashiji.biyun.xmpp.XmppConnection;

import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.util.stringencoder.Base64;
import org.jxmpp.jid.impl.JidCreate;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bingoogolapple.qrcode.core.QRCodeView;
import cn.bingoogolapple.qrcode.zxing.ZXingView;


/**
 * Created by GA on 2017/9/15.
 */

public class ScanQRCodeActivity extends AppCompatActivity implements QRCodeView.Delegate {

    @Bind(R.id.zxingview)
    ZXingView mZxingview;
    @Bind(R.id.flashlight)
    ImageView mFlashlight;
    @Bind(R.id.flashlight_rl)
    RelativeLayout mFlashlightRl;
    @Bind(R.id.bark)
    ImageView mBark;
    private int mCode;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_qr_code);
        ButterKnife.bind(this);
        initView();
        MyApp.getInstance().addActivity(this);
        Intent intent = getIntent();
        mCode = intent.getIntExtra("code", 0);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //打开后置摄像头预览,但并没有开始扫描
        mZxingview.startCamera();
        //开启扫描框
        mZxingview.showScanRect();
        mZxingview.startSpot();
    }

    @Override
    protected void onStop() {
        mZxingview.stopCamera();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        mZxingview.onDestroy();
        super.onDestroy();
    }

    private void initView() {
        /*//隐藏toolbar上面的二维码图标
        scanner.setVisibility(View.GONE);
        btnPhoto.setVisibility(View.VISIBLE);*/
        //设置二维码的代理
        mZxingview.setDelegate(this);
    }

    //扫描成功解析二维码成功后调用,可做对应的操作
    @Override
    public void onScanQRCodeSuccess(String result) {
        //扫描成功后调用震动器
        vibrator();
        //显示扫描结果
//        Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
        if (mCode == 0) {
            Intent intent = new Intent(ScanQRCodeActivity.this, AddOutCoinSiteActivity.class);
            intent.putExtra("result", result);
            setResult(RESULT_OK, intent);
            UtilTool.Log("日志", result);
            finish();
        } else {
            String jsonresult = Base64.decodeToString(result);
            UtilTool.Log("日志", jsonresult);
            if (jsonresult.contains("message") && jsonresult.contains("名片")) {
                try {
                    String name = jsonresult.substring(jsonresult.indexOf(":") + 2, jsonresult.indexOf(",")- 1);
                    UtilTool.Log("日志", name);
                    Roster.getInstanceFor(XmppConnection.getInstance().getConnection()).createEntry(JidCreate.entityBareFrom(name), null, new String[]{"Friends"});
                    Toast.makeText(this, "发送请求成功", Toast.LENGTH_SHORT).show();
                    finish();
                } catch (Exception e) {
                    Toast.makeText(this, "发送请求失败", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
            Intent intent = new Intent(ScanQRCodeActivity.this, CloudMessageFragment.class);
            intent.putExtra("result", result);
            setResult(RESULT_OK, intent);
            finish();
        }
        //再次延时1.5秒后启动
        mZxingview.startSpot();
    }

    private void vibrator() {
        //获取系统震动服务
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(200);
    }

    //扫描失败后调用的方法
    @Override
    public void onScanQRCodeOpenCameraError() {
        Toast.makeText(this, "相机打开失败", Toast.LENGTH_SHORT).show();
    }

    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mZxingview.showScanRect();
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_CHOOSE_QRCODE_FROM_GALLERY) {
            final String picturePath = BGAPhotoPickerActivity.getSelectedImages(data).get(0);

            *//*
            这里为了偷懒，就没有处理匿名 AsyncTask 内部类导致 Activity 泄漏的问题
            请开发在使用时自行处理匿名内部类导致Activity内存泄漏的问题，处理方式可参考
             *//*
            new AsyncTask<Void, Void, String>() {
                @Override
                protected String doInBackground(Void... params) {
                    return QRCodeDecoder.syncDecodeQRCode(picturePath);
                }

                @Override
                protected void onPostExecute(String result) {
                    if (TextUtils.isEmpty(result)) {
                        Toast.makeText(QRCodeActivity.this, "未发现二维码", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(QRCodeActivity.this, result, Toast.LENGTH_SHORT).show();
                    }
                }
            }.execute();
        }
    }*/

    boolean isClick = false;

    @OnClick({R.id.bark, R.id.flashlight_rl})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;
            case R.id.flashlight_rl:
                isClick = !isClick;

                if (isClick) {

                    mZxingview.openFlashlight();

                    mFlashlight.setSelected(true);

                } else {

                    //关闭闪光灯
                    mZxingview.closeFlashlight();

                    mFlashlight.setSelected(false);

                }
                break;
        }
    }
}
