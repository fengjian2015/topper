package com.bclould.tocotalk.service;

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

import com.amazonaws.AmazonClientException;
import com.amazonaws.Request;
import com.amazonaws.Response;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.handlers.RequestHandler2;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.bclould.tocotalk.Presenter.DynamicPresenter;
import com.bclould.tocotalk.R;
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
            checkFile();
        } else {
            onDestroy();
        }
        return super.onStartCommand(intent, flags, startId);
    }


    private void checkFile() {
        if (mPathList.size() != 0) {
            if (mType) {
                File file = new File(mPathList.get(0));
                Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(mPathList.get(0)
                        , MediaStore.Video.Thumbnails.MINI_KIND);
                //缩略图储存路径
                final String key = UtilTool.getUserId() + UtilTool.createtFileName() + UtilTool.getPostfix2(file.getName());
                final String keyCompress = UtilTool.getUserId() + UtilTool.createtFileName() + "compress.jpg";
                final File newFile = new File(Constants.PUBLICDIR + keyCompress);
                UtilTool.comp(bitmap, newFile);//压缩图片
                upVoide(key, file, true);
                upVoide(keyCompress, newFile, false);
            } else {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            for (int i = 0; i < mPathList.size(); i++) {
                                File file = new File(mPathList.get(i));
                                final String key = UtilTool.getUserId() + UtilTool.createtFileName() + UtilTool.getPostfix2(file.getName());
                                final String keyCompress = UtilTool.getUserId() + UtilTool.createtFileName() + "compress" + UtilTool.getPostfix2(file.getName());
                                //缩略图储存路径
                                final File newFile = new File(Constants.PUBLICDIR + keyCompress);
                                UtilTool.comp(BitmapFactory.decodeFile(mPathList.get(i)), newFile);//压缩图片
                                upImage(key, file, true);
                                upImage(keyCompress, newFile, false);
                            }
                        } catch (Exception e) {
                            UtilTool.Log("錯誤", e.getMessage());
                            handler.sendEmptyMessage(2);
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        } else {
            publicshDynamic("0", mKeyList, mkeyCompressList);
        }
    }

    private void publicshDynamic(String type, String keyList, String mkeyCompressList) {
        mDynamicPresenter.publicsh(mText, type, keyList, mkeyCompressList, "", new DynamicPresenter.CallBack() {
            @Override
            public void send() {
                EventBus.getDefault().post(new MessageEvent(getString(R.string.publish_dynamic)));
                UtilTool.Log("發佈動態", "發佈成功");
                onDestroy();
            }
        });
    }

    private void upImage(final String key, File file, final boolean type) {
        UtilTool.Log("aws", Constants.ACCESS_KEY_ID);
        UtilTool.Log("aws", Constants.SECRET_ACCESS_KEY);
        UtilTool.Log("aws", Constants.SESSION_TOKEN);
        BasicSessionCredentials sessionCredentials = new BasicSessionCredentials(
                Constants.ACCESS_KEY_ID,
                Constants.SECRET_ACCESS_KEY,
                Constants.SESSION_TOKEN);
        AmazonS3Client s3Client = new AmazonS3Client(
                sessionCredentials);
        /*Regions regions = Regions.fromName("ap-northeast-2");
        Region region = Region.getRegion(regions);
        s3Client.setRegion(region);*/
        s3Client.addRequestHandler(new RequestHandler2() {
            @Override
            public void beforeRequest(Request<?> request) {

            }

            @Override
            public void afterResponse(Request<?> request, Response<?> response) {
                Message message = new Message();
                message.obj = key;
                if (type) {
                    message.arg1 = 0;
                } else {
                    message.arg1 = 1;
                }
                message.what = 1;
                handler.sendMessage(message);
            }

            @Override
            public void afterError(Request<?> request, Response<?> response, Exception e) {

            }
        });
        PutObjectRequest por = new PutObjectRequest(Constants.BUCKET_NAME, key, file);
        s3Client.putObject(por);
    }

    private void upVoide(final String key, final File file, final boolean type) {
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
                            message.what = 0;
                            if (type) {
                                mKeyList = key;
                            } else {
                                mkeyCompressList = key;
                            }
                            handler.sendMessage(message);
                        }

                        @Override
                        public void afterError(Request<?> request, Response<?> response, Exception e) {

                        }
                    });
                    PutObjectRequest por = new PutObjectRequest(Constants.BUCKET_NAME, key, file);
                    s3Client.putObject(por);
                } catch (AmazonClientException e) {
                    UtilTool.Log("錯誤", e.getMessage());
                    handler.sendEmptyMessage(2);
                    e.printStackTrace();
                }
            }
        }).start();
    }

    String mKeyList = "";
    String mkeyCompressList = "";
    private int count = 0;
    private int keyCount = 0;
    private int keyCompress = 0;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    count++;
                    if (count == 2) {
                        publicshDynamic("3", mKeyList, mkeyCompressList);
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
                        publicshDynamic("1", mKeyList, mkeyCompressList);
                    }
                    break;
                case 2:
                    Toast.makeText(ImageUpService.this, getString(R.string.up_error), Toast.LENGTH_SHORT).show();
                    onDestroy();
                    break;
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().post(new MessageEvent(getString(R.string.destroy_service)));
        UtilTool.Log("發佈動態", "銷毀掉服務");
    }
}
