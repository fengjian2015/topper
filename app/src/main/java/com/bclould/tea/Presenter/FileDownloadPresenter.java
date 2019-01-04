package com.bclould.tea.Presenter;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.callback.OSSProgressCallback;
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
import com.alibaba.sdk.android.oss.model.GetObjectRequest;
import com.alibaba.sdk.android.oss.model.GetObjectResult;
import com.alibaba.sdk.android.oss.model.Range;
import com.bclould.tea.network.OSSupload;
import com.bclould.tea.utils.Constants;
import com.bclould.tea.utils.MySharedPreferences;
import com.bclould.tea.utils.UtilTool;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Created by GA on 2018/7/11.
 */

public class FileDownloadPresenter {

    private static FileDownloadPresenter instance;
    private static Context sContext;
    private long mApk_download_progress;
    public OSSAsyncTask<GetObjectResult> mTask;
    private List<String> keyList=new ArrayList<>();
    //单例
    public static FileDownloadPresenter getInstance(Context context) {
        if (instance == null) {
            sContext = context;
            instance = new FileDownloadPresenter();
        }
        return instance;
    }


    public void dowbloadFile(String bucketName, final String key, final File file) {
        if(keyList.contains(key)){
            UtilTool.Log("fengjian","正在下載中，去掉本次請求");
            return;
        }
        keyList.add(key);
        mApk_download_progress = file.length();
        GetObjectRequest get = new GetObjectRequest(bucketName, key);
        if (mApk_download_progress != 0) {
            get.setRange(new Range(mApk_download_progress, Range.INFINITE));
        }
        //设置下载进度回调
        get.setProgressListener(new OSSProgressCallback<GetObjectRequest>() {
            @Override
            public void onProgress(GetObjectRequest request, long currentSize, long totalSize) {
                onSuccsetProgressListeneress(currentSize, totalSize,key);
            }
        });

        OSSClient ossClient = OSSupload.getInstance().visitOSS();
        mTask = ossClient.asyncGetObject(get, new OSSCompletedCallback<GetObjectRequest, GetObjectResult>() {

            @Override
            public void onSuccess(GetObjectRequest request, GetObjectResult result) {
                keyList.remove(key);
                try {// 请求成功
                    InputStream inputStream = result.getObjectContent();
                    RandomAccessFile raf = null;
                    FileOutputStream fos = null;
                    boolean isExists;
                    if (file.exists()) {
                        isExists = true;
                        raf = new RandomAccessFile(file, "rw");
                        raf.seek(file.length());
                    } else {
                        isExists = false;
                        fos = new FileOutputStream(file);
                    }
                    byte[] buffer = new byte[2048];
                    int len;
                    while ((len = inputStream.read(buffer)) != -1) {
                        // 处理下载的数据
                        if (isExists) {
                            raf.write(buffer, 0, len);
                        } else {
                            fos.write(buffer, 0, len);
                        }
                    }
                    inputStream.close();
                    if (fos != null) {
                        fos.close();
                    }
                    if (raf != null) {
                        raf.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                UtilTool.Log("下載apk", "下載成功");
                if (file.length() == MySharedPreferences.getInstance().getLong(key)) {
                    onFinish(file,key);

                } else {
                    onError(key);
                }
            }

            @Override
            public void onFailure(GetObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                keyList.remove(key);
                onError(key);
                // 请求异常
                if (clientExcepion != null) {
                    // 本地异常如网络异常等
                    clientExcepion.printStackTrace();
                }
                if (serviceException != null) {
                    // 服务异常
                    UtilTool.Log("ErrorCode", serviceException.getErrorCode());
                    UtilTool.Log("RequestId", serviceException.getRequestId());
                    UtilTool.Log("HostId", serviceException.getHostId());
                    UtilTool.Log("RawMessage", serviceException.getRawMessage());
                }
            }
        });
    }

    ArrayList<downloadCallback> mListeners = new ArrayList<>();

    public void setOnDownloadCallbackListener(downloadCallback listener) {
        mListeners.add(listener);
    }

    public void removeDownloadCallbackListener(downloadCallback downloadCallback) {
        if (mListeners != null) {
            mListeners.remove(downloadCallback);
        }
    }

    private void onFinish(File file,String key) {
        for (downloadCallback downloadCallback : mListeners) {
            downloadCallback.onSuccess(file,key);
        }
    }

    private void onError(String key) {
        for (downloadCallback downloadCallback : mListeners) {
            downloadCallback.onFailure(key);
        }
    }

    private void onSuccsetProgressListeneress(long currentSize, long totalSize,String key) {
        for (downloadCallback downloadCallback : mListeners) {
            downloadCallback.onSuccsetProgressListeneress(currentSize, totalSize,key);
        }
    }

    //定义接口
    public interface downloadCallback {
        void onSuccess(File file,String key);

        void onFailure(String key);

        void onSuccsetProgressListeneress(long currentSize, long totalSize,String key);
    }
}
