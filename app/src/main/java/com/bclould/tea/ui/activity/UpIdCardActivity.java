package com.bclould.tea.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.bclould.tea.Presenter.RealNamePresenter;
import com.bclould.tea.R;
import com.bclould.tea.base.BaseActivity;
import com.bclould.tea.base.MyApp;
import com.bclould.tea.network.OSSupload;
import com.bclould.tea.ui.widget.LoadingProgressDialog;
import com.bclould.tea.utils.AppLanguageUtils;
import com.bclould.tea.utils.Constants;
import com.bclould.tea.utils.MySharedPreferences;
import com.bclould.tea.utils.UtilTool;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.compress.Luban;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

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
    private List<LocalMedia> selectList;
    private OSSAsyncTask mTask;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_up_idcard);
        ButterKnife.bind(this);
        setTitle(getString(R.string.up_id_card));
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
            mTvTitleTop.setText(getString(R.string.up_hu_zhao));
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

    private void submit(String keyList) {
        String keyLists = keyList.substring(keyList.indexOf(",") + 1, keyList.length());
        UtilTool.Log("日志", keyLists);
        RealNamePresenter realNamePresenter = new RealNamePresenter(this);
        realNamePresenter.UpImage(keyLists, new RealNamePresenter.CallBack() {
            @Override
            public void send(int status) {
                if (status == 1) {
                    Toast.makeText(UpIdCardActivity.this, getString(R.string.up_succeed), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(UpIdCardActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(new Intent(intent));
                }
                hideDialog();
            }
        });
    }

    private void upImage(final Map<Integer, String> map) {
        showDialog();
        OSSClient ossClient = OSSupload.getInstance().visitOSS();
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
            PutObjectRequest put = new PutObjectRequest(Constants.BUCKET_NAME, key, file.getPath());
            mTask = ossClient.asyncPutObject(put, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
                @Override
                public void onSuccess(PutObjectRequest putObjectRequest, PutObjectResult putObjectResult) {
                    String key = putObjectRequest.getObjectKey();
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
                }

                @Override
                public void onFailure(PutObjectRequest putObjectRequest, ClientException e, ServiceException e1) {
                    UtilTool.Log("oss", e.getMessage());
                    UtilTool.Log("oss", e1.getMessage());
                    hideDialog();
                    Toast.makeText(UpIdCardActivity.this, getString(R.string.up_error), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


    //拿到选中的图片
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //获取图片路径
        if (resultCode == Activity.RESULT_OK) {
            selectList = PictureSelector.obtainMultipleResult(data);
            showImage(selectList.get(0).getCompressPath(), requestCode);
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
        // 进入相册 以下是例子：不需要的api可以不写
        PictureSelector.create(UpIdCardActivity.this)
                .openGallery(PictureMimeType.ofImage())// 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
                .maxSelectNum(1)// 最大图片 选择数量
                .minSelectNum(1)// 最小选择数量
                .imageSpanCount(3)// 每行显示个数
                .selectionMode(PictureConfig.MULTIPLE)// 多选 or 单选
                .previewImage(true)// 是否可预览图片
                .compressGrade(Luban.THIRD_GEAR)// luban压缩档次，默认3档 Luban.FIRST_GEAR、Luban.CUSTOM_GEAR
                .isCamera(true)// 是否显示拍照按钮
                .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
                .compress(true)// 是否压缩
                .compressMode(PictureConfig.SYSTEM_COMPRESS_MODE)//系统自带 or 鲁班压缩 PictureConfig.SYSTEM_COMPRESS_MODE or LUBAN_COMPRESS_MODE
                .glideOverride(200, 200)// glide 加载宽高，越小图片列表越流畅，但会影响列表图片浏览的清晰度
                .openClickSound(false)// 是否开启点击声音
                .forResult(code);//结果回调onActivityResult code

    }

    @Override
    protected void onDestroy() {
        ButterKnife.unbind(this);
        if (mTask != null) {
            mTask.cancel();
        }
        super.onDestroy();
    }
}
