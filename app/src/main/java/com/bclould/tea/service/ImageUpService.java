package com.bclould.tea.service;

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
import com.bclould.tea.Presenter.DynamicPresenter;
import com.bclould.tea.R;
import com.bclould.tea.network.OSSupload;
import com.bclould.tea.utils.Constants;
import com.bclould.tea.utils.MessageEvent;
import com.bclould.tea.utils.UtilTool;
import com.iceteck.silicompressorr.SiliCompressor;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.net.URISyntaxException;
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
    private OSSClient mOssClient;
    private Thread mThread;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    //接受通知
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        String msg = event.getMsg();
        if (msg.equals(getString(R.string.connect_oss_error))) {
            stopSelf();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        UtilTool.Log("發佈動態", "啟動服務");
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
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
                mOssClient = OSSupload.getInstance().visitOSS();
                if (mType) {
                    final File file = new File(mPathList.get(0));
                    Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(mPathList.get(0)
                            , MediaStore.Video.Thumbnails.MINI_KIND);
                    //缩略图储存路径
                    final String keyCompress = UtilTool.getUserId() + UtilTool.createtFileName() + "compress.jpg";
                    final File newCompressImg = new File(Constants.PUBLICDIR + keyCompress);
                    UtilTool.comp(bitmap, newCompressImg);//压缩图片
                    upVoide(keyCompress, newCompressImg, false, mOssClient);
                    final String key = UtilTool.getUserId() + UtilTool.createtFileName() + UtilTool.getPostfix2(file.getName());
                    if (UtilTool.getFolderSize(file) > (1048576 * 5) && UtilTool.getFolderSize(file) < (1048576 * 20)) {
                        mThread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    String filePath = SiliCompressor.with(ImageUpService.this).compressVideo(file.getPath(), Constants.PUBLICDIR);
                                    Message message = new Message();
                                    Bundle bundle = new Bundle();
                                    bundle.putString("key", key);
                                    bundle.putString("file", filePath);
                                    message.obj = bundle;
                                    message.what = 0;
                                    handler.sendMessage(message);
                                } catch (URISyntaxException e) {
                                }
                            }
                        });
                        mThread.start();
                    } else if (UtilTool.getFolderSize(file) > (1048576 * 20)) {
                        Toast.makeText(this, getString(R.string.video_big_hint), Toast.LENGTH_SHORT).show();
                    } else {
                        upVoide(key, file, true, mOssClient);
                    }
                } else {
                    for (int i = 0; i < mPathList.size(); i++) {
                        File file = new File(mPathList.get(i));
                        final String key = UtilTool.getUserId() + UtilTool.createtFileName() + UtilTool.getPostfix2(file.getName());
                        final String keyCompress = UtilTool.getUserId() + UtilTool.createtFileName() + "compress" + UtilTool.getPostfix2(file.getName());
                        //缩略图储存路径
                        final File newFile = new File(Constants.PUBLICDIR + keyCompress);
                        UtilTool.comp(BitmapFactory.decodeFile(mPathList.get(i)), newFile);//压缩图片
                        upImage(key, file, true, mOssClient);
                        upImage(keyCompress, newFile, false, mOssClient);
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
                handler.sendEmptyMessage(2);
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
                handler.sendEmptyMessage(2);
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
                    Bundle bundle = (Bundle) msg.obj;
                    String key = bundle.getString("key");
                    String file = bundle.getString("file");
                    upVoide(key, new File(file), true, mOssClient);
                    break;
                case 2:
                    Toast.makeText(ImageUpService.this, getString(R.string.up_error), Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mTask != null) {
            mTask.cancel();
        }
        if (mThread != null) {
            mThread.interrupt();
        }
        mKeyList = "";
        mkeyCompressList = "";
        count = 0;
        keyCount = 0;
        keyCompress = 0;
        EventBus.getDefault().post(new MessageEvent(getString(R.string.destroy_service)));
        UtilTool.Log("發佈動態", "銷毀掉服務");
        EventBus.getDefault().unregister(this);
        stopSelf();
    }
}
