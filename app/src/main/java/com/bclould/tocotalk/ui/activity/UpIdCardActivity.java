package com.bclould.tocotalk.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.amazonaws.AmazonClientException;
import com.amazonaws.Request;
import com.amazonaws.Response;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.handlers.RequestHandler2;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.bclould.tocotalk.Presenter.RealNamePresenter;
import com.bclould.tocotalk.R;
import com.bclould.tocotalk.base.BaseActivity;
import com.bclould.tocotalk.base.MyApp;
import com.bclould.tocotalk.ui.widget.LoadingProgressDialog;
import com.bclould.tocotalk.utils.Constants;
import com.bclould.tocotalk.utils.UtilTool;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

/**
 * Created by GA on 2017/9/26.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class UpIdCardActivity extends BaseActivity {

    private static final int ZHENGMIAN = 0;
    private static final int FANMIAN = 1;
    private static final int SHOUCHI = 2;
    private static final int HUZHENGMIAN = 3;
    private static final int HUSHOUCHI = 4;
    @Bind(R.id.bark)
    ImageView mBark;
    @Bind(R.id.zhengmian_iv)
    ImageView mZhengmianIv;
    @Bind(R.id.zhengmian)
    RelativeLayout mZhengmian;
    @Bind(R.id.fanmian_iv)
    ImageView mFanmianIv;
    @Bind(R.id.fanmian)
    RelativeLayout mFanmian;
    @Bind(R.id.shouchi_iv)
    ImageView mShouchiIv;
    @Bind(R.id.shouchi)
    RelativeLayout mShouchi;
    @Bind(R.id.up_check)
    Button mUpCheck;
    @Bind(R.id.ll_shenfen)
    LinearLayout mLlShenfen;
    @Bind(R.id.iv_hu_zhengmian)
    ImageView mIvHuZhengmian;
    @Bind(R.id.hu_zhengmian)
    RelativeLayout mHuZhengmian;
    @Bind(R.id.iv_hu_fanmian)
    ImageView mIvHuFanmian;
    @Bind(R.id.hu_fanmian)
    RelativeLayout mHuFanmian;
    @Bind(R.id.ll_huzhao)
    LinearLayout mLlHuzhao;
    private String mType;
    private Map<Integer, String> mShenfenMap = new HashMap<>();
    private Map<Integer, String> mHuzhaoMap = new HashMap<>();
    int count = 0;
    String keyList = "";
    private LoadingProgressDialog mProgressDialog;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_up_idcard);
        ButterKnife.bind(this);
        MyApp.getInstance().addActivity(this);
        initIntent();
        initMap();
    }

    private void initMap() {
        mShenfenMap.put(ZHENGMIAN, null);
        mShenfenMap.put(FANMIAN, null);
        mShenfenMap.put(SHOUCHI, null);
        mHuzhaoMap.put(HUSHOUCHI, null);
        mHuzhaoMap.put(HUZHENGMIAN, null);
    }

    private void initIntent() {
        Intent intent = getIntent();
        mType = intent.getStringExtra("type");
        if (mType.equals("1")) {
            mLlShenfen.setVisibility(View.VISIBLE);
            mLlHuzhao.setVisibility(View.GONE);
        } else {
            mLlShenfen.setVisibility(View.GONE);
            mLlHuzhao.setVisibility(View.VISIBLE);
        }
    }

    //点击事件的处理
    @OnClick({R.id.bark, R.id.zhengmian, R.id.fanmian, R.id.shouchi, R.id.up_check, R.id.hu_zhengmian, R.id.hu_fanmian})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:

                finish();

                break;
            case R.id.zhengmian:

                callPhotoAlbum(ZHENGMIAN);

                break;
            case R.id.fanmian:

                callPhotoAlbum(FANMIAN);

                break;
            case R.id.shouchi:

                callPhotoAlbum(SHOUCHI);

                break;
            case R.id.hu_zhengmian:
                callPhotoAlbum(HUZHENGMIAN);
                break;
            case R.id.hu_fanmian:
                callPhotoAlbum(HUSHOUCHI);
                break;
            case R.id.up_check:
                if (mType.equals("1")) {
                    if (ShenfenCheck()) {
                        upImage(mShenfenMap);
                    }
                } else {
                    if (HuzhaoCheck()) {
                        upImage(mHuzhaoMap);
                    }
                }
                break;
        }
    }

    private boolean HuzhaoCheck() {
        if (mHuzhaoMap.get(HUZHENGMIAN) == null) {
            Toast.makeText(this, getString(R.string.toast_huzhao_zhengmian), Toast.LENGTH_SHORT).show();
        } else if (mHuzhaoMap.get(HUSHOUCHI) == null) {
            Toast.makeText(this, getString(R.string.toast_huzhao_shouchi), Toast.LENGTH_SHORT).show();
        } else {
            return true;
        }
        return false;
    }

    private boolean ShenfenCheck() {
        if (mShenfenMap.get(ZHENGMIAN) == null) {
            Toast.makeText(this, getString(R.string.toast_idcard_zhengmian), Toast.LENGTH_SHORT).show();
        } else if (mShenfenMap.get(FANMIAN) == null) {
            Toast.makeText(this, getString(R.string.toast_idcard_fanmian), Toast.LENGTH_SHORT).show();
        } else if (mShenfenMap.get(SHOUCHI) == null) {
            Toast.makeText(this, getString(R.string.toast_idcard_shouchi), Toast.LENGTH_SHORT).show();
        } else {
            return true;
        }
        return false;
    }

    private void showDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = LoadingProgressDialog.createDialog(this);
            mProgressDialog.setMessage(getString(R.string.uploading));
        }

        mProgressDialog.show();
    }

    private void hideDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    String key = (String) msg.obj;
                    keyList += "," + key;
                    count++;
                    if (mType.equals("1")) {
                        if (count == 3) {
                            submit(keyList);
                        }
                    } else {
                        if (count == 2) {
                            submit(keyList);
                        }
                    }
                    break;
                case 1:
                    Toast.makeText(UpIdCardActivity.this, getString(R.string.up_error), Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    private void submit(String keyList) {
        String keyLists = keyList.substring(keyList.indexOf(",") + 1, keyList.length());
        UtilTool.Log("日志", keyLists);
        RealNamePresenter realNamePresenter = new RealNamePresenter(this);
        realNamePresenter.UpImage(keyLists, new RealNamePresenter.CallBack() {
            @Override
            public void send(int status) {
                if (status == 1) {
                    Toast.makeText(UpIdCardActivity.this, getString(R.string.up_succeed), Toast.LENGTH_SHORT).show();
                    finish();
                }
                hideDialog();
            }
        });
    }

    private void upImage(Map<Integer, String> map) {
        showDialog();
        for (Map.Entry<Integer, String> entry : map.entrySet()) {
            File file = new File(entry.getValue());
            String key = "";
            switch (entry.getKey()) {
                case ZHENGMIAN:
                    key = "" + 1 + UtilTool.getUserId() + UtilTool.createtFileName() + UtilTool.getPostfix2(file.getName());
                    break;
                case FANMIAN:
                    key = "" + 2 + UtilTool.getUserId() + UtilTool.createtFileName() + UtilTool.getPostfix2(file.getName());
                    break;
                case SHOUCHI:
                    key = "" + 3 + UtilTool.getUserId() + UtilTool.createtFileName() + UtilTool.getPostfix2(file.getName());
                    break;
                case HUZHENGMIAN:
                    key = "" + 1 + UtilTool.getUserId() + UtilTool.createtFileName() + UtilTool.getPostfix2(file.getName());
                    break;
                case HUSHOUCHI:
                    key = "" + 2 + UtilTool.getUserId() + UtilTool.createtFileName() + UtilTool.getPostfix2(file.getName());
                    break;
            }
            final String key2 = key;
            Luban.with(UpIdCardActivity.this)
                    .load(file)                                   // 传人要压缩的图片列表
                    .ignoreBy(100)                                  // 忽略不压缩图片的大小
                    .setCompressListener(new OnCompressListener() { //设置回调
                        @Override
                        public void onStart() {
                        }

                        @Override
                        public void onSuccess(final File file) {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        BasicSessionCredentials sessionCredentials = new BasicSessionCredentials(
                                                Constants.ACCESS_KEY_ID,
                                                Constants.SECRET_ACCESS_KEY,
                                                Constants.SESSION_TOKEN);
                                        AmazonS3Client s3Client = new AmazonS3Client(
                                                sessionCredentials);
                                        Regions regions = Regions.fromName("ap-northeast-2");
                                        Region region = Region.getRegion(regions);
                                        s3Client.setRegion(region);
                                        s3Client.addRequestHandler(new RequestHandler2() {
                                            @Override
                                            public void beforeRequest(Request<?> request) {
                                            }
                                            @Override
                                            public void afterResponse(Request<?> request, Response<?> response) {
                                                Message message = new Message();
                                                message.obj = key2;
                                                message.what = 0;
                                                handler.sendMessage(message);
                                            }
                                            @Override
                                            public void afterError(Request<?> request, Response<?> response, Exception e) {
                                            }
                                        });
                                        PutObjectRequest por = new PutObjectRequest(Constants.BUCKET_NAME, key2, file);
                                        s3Client.putObject(por);
                                    } catch (AmazonClientException e) {
                                        hideDialog();
                                        handler.sendEmptyMessage(1);
                                        e.printStackTrace();
                                    }
                                }
                            }).start();
                        }

                        @Override
                        public void onError(Throwable e) {
                        }
                    }).launch();    //启动压缩
        }
    }


    //拿到选中的图片
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //获取图片路径
        if (resultCode == Activity.RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            String[] filePathColumns = {MediaStore.Images.Media.DATA};
            Cursor c = getContentResolver().query(selectedImage, filePathColumns, null, null, null);
            c.moveToFirst();
            int columnIndex = c.getColumnIndex(filePathColumns[0]);
            String imagePath = c.getString(columnIndex);
            showImage(imagePath, requestCode);
            c.close();
        }
    }

    //把选中的图片设置到ImageView
    private void showImage(String imagePath, int requestCode) {
        Drawable drawable = Drawable.createFromPath(imagePath);
        switch (requestCode) {
            case ZHENGMIAN:
                mZhengmianIv.setImageDrawable(drawable);
                mShenfenMap.put(ZHENGMIAN, imagePath);
                break;
            case FANMIAN:
                mFanmianIv.setImageDrawable(drawable);
                mShenfenMap.put(FANMIAN, imagePath);
                break;
            case SHOUCHI:
                mShouchiIv.setImageDrawable(drawable);
                mShenfenMap.put(SHOUCHI, imagePath);
                break;
            case HUSHOUCHI:
                mIvHuFanmian.setImageDrawable(drawable);
                mHuzhaoMap.put(HUSHOUCHI, imagePath);
                break;
            case HUZHENGMIAN:
                mIvHuZhengmian.setImageDrawable(drawable);
                mHuzhaoMap.put(HUZHENGMIAN, imagePath);
                break;
        }
    }

    //调用系统相册
    private void callPhotoAlbum(int code) {

        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, code);

    }
}
