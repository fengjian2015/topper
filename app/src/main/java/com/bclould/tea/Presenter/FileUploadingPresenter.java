package com.bclould.tea.Presenter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.widget.Toast;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.callback.OSSProgressCallback;
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.bclould.tea.R;
import com.bclould.tea.network.OSSupload;
import com.bclould.tea.utils.Constants;
import com.bclould.tea.utils.MessageEvent;
import com.bclould.tea.utils.UtilTool;
import com.iceteck.silicompressorr.SiliCompressor;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by GA on 2018/7/18.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class FileUploadingPresenter {

    private final Context mContext;
    private String mText;
    private String mLocation;
    private boolean mType;
    private ArrayList<String> mPathList;
    private OSSClient mOssClient;
    private Thread mThread;
    private OSSAsyncTask<PutObjectResult> mTask;
    private DynamicPresenter mDynamicPresenter;
    private static FileUploadingPresenter instance;

    public FileUploadingPresenter(Context context) {
        mContext = context;
    }

    //单例
    public static FileUploadingPresenter getInstance(Context context) {
        if (instance == null) {
            instance = new FileUploadingPresenter(context);
        }
        return instance;
    }

    public boolean isUploading = false;
    public void setData(String text, String location, boolean type, ArrayList<String> pathList) {
        isUploading = true;
        mDynamicPresenter = new DynamicPresenter(mContext);
        EventBus.getDefault().post(new MessageEvent(mContext.getString(R.string.start_service)));
        mText = text;
        mLocation = location;
        mType = type;
        mPathList = pathList;
        checkFile();
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
                                    String filePath = SiliCompressor.with(mContext).compressVideo(file.getPath(), Constants.PUBLICDIR);
                                    Message message = new Message();
                                    Bundle bundle = new Bundle();
                                    bundle.putString("key", key);
                                    bundle.putString("file", filePath);
                                    message.obj = bundle;
                                    message.what = 0;
                                    handler.sendMessage(message);
                                } catch (Exception e) {
                                    onDestroy();
                                }
                            }
                        });
                        mThread.start();
                    } else if (UtilTool.getFolderSize(file) > (1048576 * 20)) {
                        Toast.makeText(mContext, mContext.getString(R.string.video_big_hint), Toast.LENGTH_SHORT).show();
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
            Toast.makeText(mContext, mContext.getString(R.string.up_error), Toast.LENGTH_SHORT).show();
            onDestroy();
            e.printStackTrace();
        }
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
                    Toast.makeText(mContext, mContext.getString(R.string.up_error), Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };


    private void upImage(final String key, final File file, final boolean type, OSSClient ossClient) {
        synchronized (FileUploadingPresenter.class) {
            UtilTool.Log("動態", key);
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
            PutObjectRequest put = new PutObjectRequest(Constants.BUCKET_NAME, key, file.getPath());
            put.setProgressCallback(new OSSProgressCallback<PutObjectRequest>() {
                @Override
                public void onProgress(PutObjectRequest request, long currentSize, long totalSize) {
                    onSuccsetProgressListeneress(currentSize, totalSize);
                }
            });
            mTask = ossClient.asyncPutObject(put, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
                @Override
                public void onSuccess(PutObjectRequest putObjectRequest, PutObjectResult putObjectResult) {
                    onFinish();
                    count++;
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
    }

    private void publicshDynamic(String type) {
        UtilTool.Log("動態", mKeyList);
        UtilTool.Log("動態", mkeyCompressList);
        mDynamicPresenter.publicsh(mText, type, mKeyList, mkeyCompressList, mLocation, new DynamicPresenter.CallBack6() {
            @Override
            public void send(int status) {
                if (status == 1) {
                    Toast.makeText(mContext, mContext.getString(R.string.publish_succeed), Toast.LENGTH_SHORT).show();
                    EventBus.getDefault().post(new MessageEvent(mContext.getString(R.string.publish_dynamic)));
                    UtilTool.Log("發佈動態", "發佈成功");
                } else {
                    Toast.makeText(mContext, mContext.getString(R.string.publish_error), Toast.LENGTH_SHORT).show();
                }
                onDestroy();
            }
        });
    }

    private void upVoide(final String key, final File file, final boolean type, OSSClient ossClient) {
        PutObjectRequest put = new PutObjectRequest(Constants.BUCKET_NAME, key, file.getPath());
        put.setProgressCallback(new OSSProgressCallback<PutObjectRequest>() {
            @Override
            public void onProgress(PutObjectRequest request, long currentSize, long totalSize) {
                onSuccsetProgressListeneress(currentSize, totalSize);
            }
        });
        mTask = ossClient.asyncPutObject(put, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
            @Override
            public void onSuccess(PutObjectRequest putObjectRequest, PutObjectResult putObjectResult) {
                onFinish();
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

    private void onDestroy() {
        isUploading = false;
        onError();
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
        mListeners.clear();
        EventBus.getDefault().post(new MessageEvent(mContext.getString(R.string.destroy_service)));
        UtilTool.Log("發佈動態", "銷毀掉服務");
        EventBus.getDefault().unregister(this);
    }

    ArrayList<UploadingCallback> mListeners = new ArrayList<>();

    public void setOnUploadingCallbackListener(UploadingCallback listener) {
        mListeners.add(listener);
        listener.onStart(mText, mPathList, mType);
    }


    public void removeDownloadCallbackListener(UploadingCallback downloadCallback) {
        if (mListeners != null) {
            mListeners.remove(downloadCallback);
        }
    }

    private void onFinish() {
        for (UploadingCallback uploadingCallback : mListeners) {
            uploadingCallback.onSuccess();
        }
    }

    private void onError() {
        for (UploadingCallback uploadingCallback : mListeners) {
            uploadingCallback.onFailure();
        }
    }

    private void onSuccsetProgressListeneress(long currentSize, long totalSize) {
        for (UploadingCallback uploadingCallback : mListeners) {
            uploadingCallback.onSuccsetProgressListeneress(currentSize, totalSize);
        }
    }

    //定义接口
    public interface UploadingCallback {

        void onSuccess();

        void onStart(String text, List<String> pathList, boolean type);

        void onSuccsetProgressListeneress(long currentSize, long totalSize);

        void onFailure();

    }
}
