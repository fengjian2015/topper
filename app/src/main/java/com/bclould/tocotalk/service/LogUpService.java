package com.bclould.tocotalk.service;

import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;

import com.bclould.tocotalk.utils.Constants;
import com.bclould.tocotalk.utils.UtilTool;

import java.io.File;

/**
 * Created by GA on 2018/4/4.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class LogUpService extends Service {
    File logFile = new File(Constants.LOG_DIR + UtilTool.getUserId() + UtilTool.createtFileName() + ".txt");

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        upLoadErrorFileToServer(logFile);
    }

    private void upLoadErrorFileToServer(final File logFile) {
        /*new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //连接aws
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
                            MyApp.getInstance().RestartApp();
                            startActivity(new Intent(getApplicationContext(), StartActivity.class));
                            Intent intent = new Intent("com.bclould.tocotalk.service");
                            stopService(intent);
                        }

                        @Override
                        public void afterError(Request<?> request, Response<?> response, Exception e) {

                        }
                    });
                    //实例化上传请求
                    PutObjectRequest por = new PutObjectRequest(Constants.BUCKET_NAME, logFile.getName(), logFile);
                    //开始上传
                    s3Client.putObject(por);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();*/
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
