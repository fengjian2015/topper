/*
package com.bclould.tocotalk.listener;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.bclould.tocotalk.Presenter.DillDataPresenter;
import com.bclould.tocotalk.model.OSSInfo;
import com.bclould.tocotalk.ui.activity.MainActivity;
import com.bclould.tocotalk.utils.Constants;
import com.bclould.tocotalk.utils.MySharedPreferences;
import com.bclould.tocotalk.utils.UtilTool;

import java.io.File;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import static com.bclould.tocotalk.ui.activity.ConversationActivity.ACCESSKEYID;
import static com.bclould.tocotalk.ui.activity.ConversationActivity.SECRETACCESSKEY;
import static com.bclould.tocotalk.ui.activity.ConversationActivity.SESSIONTOKEN;

*/
/**
 * Created by GA on 2018/4/3.
 *//*


@RequiresApi(api = Build.VERSION_CODES.N)
public class CrashHandler implements Thread.UncaughtExceptionHandler {

    private final Context mContext;
    private final Thread.UncaughtExceptionHandler defaultUncaught;
    File logFile = new File(Constants.LOG_DIR + "ExceptionLog" + UtilTool.getUserId() + UtilTool.createtFileName() + ".txt");

    public CrashHandler(Context context) {
        super();
        mContext = context;
        defaultUncaught = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this); // 设置为当前线程默认的异常处理器
        initAWS();
    }

    private void initAWS() {
        DillDataPresenter dillDataPresenter = new DillDataPresenter(mContext);
        dillDataPresenter.getSessionToken(new DillDataPresenter.CallBack3() {
            @Override
            public void send(OSSInfo.DataBean data) {
                MySharedPreferences.getInstance().setString(ACCESSKEYID, data.getAccessKeyId());
                MySharedPreferences.getInstance().setString(SECRETACCESSKEY, data.getSecretAccessKey());
                MySharedPreferences.getInstance().setString(SESSIONTOKEN, data.getSessionToken());
            }
        });
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
// 打印当前的异常信息
        ex.printStackTrace();

        // 如果我们没处理异常，并且系统默认的异常处理器不为空，则交给系统来处理
        if (!handlelException(ex) && defaultUncaught != null) {
            defaultUncaught.uncaughtException(thread, ex);
        } else {

            // 已经记录完log, 提交服务器
//            upLoadErrorFileToServer(logFile);

            Intent in = new Intent(mContext, MainActivity.class);
            in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // 如果设置了此标志，这个activity将成为一个新task的历史堆栈中的第一个activity
            mContext.startActivity(in);

            // 杀死我们的进程
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {

                @Override
                public void run() {
                    android.os.Process.killProcess(android.os.Process.myPid()); // 杀死线程
                }
            }, 2 * 1000);
        }
    }

    private void upLoadErrorFileToServer(final File logFile) {
        new Thread(new Runnable() {
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
                    //实例化上传请求
                    PutObjectRequest por = new PutObjectRequest(Constants.BUCKET_NAME, logFile.getName(), logFile);
                    //开始上传
                    s3Client.putObject(por);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    */
/**
     * 记录异常信息
     *//*

    private boolean handlelException(Throwable ex) {
        // TODO Auto-generated method stub
        if (ex == null) {
            return false;
        }

        PrintWriter pw = null;
        try {
            File file = new File(Constants.LOG_DIR);
            if (!file.exists()) {
                file.mkdirs();
            }
            if (!logFile.exists()) {
                logFile.createNewFile();
            }
            pw = new PrintWriter(logFile);

            // 收集手机及错误信息
            collectInfoToSDCard(pw, ex);
            pw.close();
        } catch (Exception e) {

            e.printStackTrace();
        }
        return true;
    }

    */
/**
     * 收集记录错误信息
     *
     * @throws PackageManager.NameNotFoundException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     *//*

    private void collectInfoToSDCard(PrintWriter pw, Throwable ex) throws PackageManager.NameNotFoundException, IllegalAccessException, IllegalArgumentException, PackageManager.NameNotFoundException {
        // TODO Auto-generated method stub
        PackageManager pm = mContext.getPackageManager();
        PackageInfo mPackageInfo = pm.getPackageInfo(mContext.getPackageName(), PackageManager.GET_ACTIVITIES);

        pw.println("time: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())); // 记录错误发生的时间
        pw.println("versionCode: " + mPackageInfo.versionCode); // 版本号
        pw.println("versionName: " + mPackageInfo.versionName); // 版本名称

        Field[] fields = Build.class.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            pw.print(field.getName() + " : ");
            pw.println(field.get(null).toString());
        }
        ex.printStackTrace(pw);
    }
}
*/
