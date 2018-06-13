package com.bclould.tocotalk.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.widget.Toast;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.bclould.tocotalk.Presenter.DynamicPresenter;
import com.bclould.tocotalk.R;
import com.bclould.tocotalk.network.OSSupload;
import com.bclould.tocotalk.utils.Constants;
import com.bclould.tocotalk.utils.MessageEvent;
import com.bclould.tocotalk.utils.UtilTool;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by GA on 2018/5/18.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class ImageUpService extends Service {
    private String mText;
    private DynamicPresenter mDynamicPresenter;
    private List<String> mPathList = new ArrayList<>();
    private boolean mType;
    private String mLocation;
    private OSSAsyncTask<PutObjectResult> mTask;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        UtilTool.Log("發佈動態", "啟動服務");
        EventBus.getDefault().post(new MessageEvent(getString(R.string.start_service)));
        mPathList.clear();
        mDynamicPresenter = new DynamicPresenter(this);
        Bundle bundle = null;
        if (intent != null) {
            bundle = intent.getExtras();
        } else {
            onDestroy();
        }
        if (bundle != null) {
            if (bundle.containsKey("imageList")) {
                mPathList = bundle.getStringArrayList("imageList");
            }
            if (bundle.containsKey("text")) {
                mText = bundle.getString("text");
            }
            if (bundle.containsKey("type")) {
                mType = bundle.getBoolean("type", false);
            }
            if (bundle.containsKey("location")) {
                mLocation = bundle.getString("location");
            }
            checkFile();
        } else {
            onDestroy();
        }
        return super.onStartCommand(intent, flags, startId);
    }


    private void checkFile() {
        try {
            if (mPathList.size() != 0) {
                OSSClient ossClient = OSSupload.getInstance().visitOSS();
                if (mType) {
                    File file = new File(mPathList.get(0));
                    Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(mPathList.get(0)
                            , MediaStore.Video.Thumbnails.MINI_KIND);
                    //缩略图储存路径
                    final String key = UtilTool.getUserId() + UtilTool.createtFileName() + UtilTool.getPostfix2(file.getName());
                    final String keyCompress = UtilTool.getUserId() + UtilTool.createtFileName() + "compress.jpg";
                    final File newFile = new File(Constants.PUBLICDIR + keyCompress);
                    UtilTool.comp(bitmap, newFile);//压缩图片
                    upVoide(key, file, true, ossClient);
                    upVoide(keyCompress, newFile, false, ossClient);
                } else {
                    for (int i = 0; i < mPathList.size(); i++) {
                        File file = new File(mPathList.get(i));
                        final String key = UtilTool.getUserId() + UtilTool.createtFileName() + UtilTool.getPostfix2(file.getName());
                        final String keyCompress = UtilTool.getUserId() + UtilTool.createtFileName() + "compress" + UtilTool.getPostfix2(file.getName());
                        //缩略图储存路径
                        final File newFile = new File(Constants.PUBLICDIR + keyCompress);
                        UtilTool.comp(BitmapFactory.decodeFile(mPathList.get(i)), newFile);//压缩图片
                        upImage(key, file, true, ossClient);
                        upImage(keyCompress, newFile, false, ossClient);
                    }
                }
            } else {
                publicshDynamic("0");
            }
        } catch (Exception e) {
            UtilTool.Log("錯誤", e.getMessage());
            Toast.makeText(ImageUpService.this, getString(R.string.up_error), Toast.LENGTH_SHORT).show();
            onDestroy();
            e.printStackTrace();
        }
    }

    private void publicshDynamic(String type) {
        UtilTool.Log("動態", mKeyList);
        UtilTool.Log("動態", mkeyCompressList);
        mDynamicPresenter.publicsh(mText, type, mKeyList, mkeyCompressList, mLocation, new DynamicPresenter.CallBack() {
            @Override
            public void send() {
                EventBus.getDefault().post(new MessageEvent(getString(R.string.publish_dynamic)));
                UtilTool.Log("發佈動態", "發佈成功");
                onDestroy();
            }
        });
    }

    private void upImage(final String key, File file, final boolean type, OSSClient ossClient) {
        UtilTool.Log("動態", key);
        PutObjectRequest put = new PutObjectRequest(Constants.BUCKET_NAME, key, file.getPath());
        mTask = ossClient.asyncPutObject(put, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
            @Override
            public void onSuccess(PutObjectRequest putObjectRequest, PutObjectResult putObjectResult) {
                String key = putObjectRequest.getObjectKey();
                count++;
                if (type) {
                    keyCount++;
                    if (keyCount >= 2)
                        mKeyList += "," + key;
                    else
                        mKeyList = key;
                } else {
                    keyCompress++;
                    if (keyCompress >= 2)
                        mkeyCompressList += "," + key;
                    else
                        mkeyCompressList = key;
                }
                if (count == mPathList.size() * 2) {
                    publicshDynamic("1");
                }
            }

            @Override
            public void onFailure(PutObjectRequest putObjectRequest, ClientException e, ServiceException e1) {
                Toast.makeText(ImageUpService.this, getString(R.string.up_error), Toast.LENGTH_SHORT).show();
                onDestroy();
            }
        });
    }

    private void upVoide(final String key, final File file, final boolean type, OSSClient ossClient) {
        PutObjectRequest put = new PutObjectRequest(Constants.BUCKET_NAME, key, file.getPath());
        mTask = ossClient.asyncPutObject(put, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
            @Override
            public void onSuccess(PutObjectRequest putObjectRequest, PutObjectResult putObjectResult) {
                String body = putObjectResult.getServerCallbackReturnBody();
                String key = putObjectRequest.getObjectKey();
                UtilTool.Log("oss", body);
                if (type) {
                    mKeyList = key;
                } else {
                    mkeyCompressList = key;
                }
                count++;
                if (count == 2) {
                    publicshDynamic("3");
                }
            }

            @Override
            public void onFailure(PutObjectRequest putObjectRequest, ClientException e, ServiceException e1) {
                Toast.makeText(ImageUpService.this, getString(R.string.up_error), Toast.LENGTH_SHORT).show();
                onDestroy();
            }
        });
    }

    String mKeyList = "";
    String mkeyCompressList = "";
    private int count = 0;
    private int keyCount = 0;
    private int keyCompress = 0;
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    count++;
                    if (count == 2) {
                        publicshDynamic("3");
                    }
                    break;
                case 1:
                    count++;
                    if (msg.arg1 == 0) {
                        keyCount++;
                        String key = (String) msg.obj;
                        if (keyCount >= 2)
                            mKeyList += "," + key;
                        else
                            mKeyList = key;
                    } else {
                        keyCompress++;
                        String key = (String) msg.obj;
                        if (keyCompress >= 2)
                            mkeyCompressList += "," + key;
                        else
                            mkeyCompressList = key;
                    }
                    if (count == mPathList.size() * 2) {
                        publicshDynamic("1");
                    }
                    break;
                case 2:
                    break;
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        mTask.cancel();
        mKeyList = "";
        mkeyCompressList = "";
        count = 0;
        keyCount = 0;
        keyCompress = 0;
        EventBus.getDefault().post(new MessageEvent(getString(R.string.destroy_service)));
        UtilTool.Log("發佈動態", "銷毀掉服務");
        stopSelf();
    }
}
