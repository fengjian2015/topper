package com.bclould.tea.network;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.v4.content.FileProvider;
import android.util.Log;

import com.bclould.tea.utils.UtilTool;

import java.io.File;

/**
 * Created by lenovo on 2018/2/1 0001.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class DownLoadApk {
    public static final String TAG = DownLoadApk.class.getSimpleName();

    public static void download(Context context, String url, String title, final String appName) {
        try {
            // 获取存储ID
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
            long downloadId = sp.getLong(DownloadManager.EXTRA_DOWNLOAD_ID, -1L);
            if (downloadId != -1L) {
                FileDownloadManager fdm = FileDownloadManager.getInstance(context);
                int status = fdm.getDownloadStatus(downloadId);
                if (status == DownloadManager.STATUS_SUCCESSFUL) {
                    //启动更新界面
                    Uri uri = fdm.getDownloadUri(downloadId);
                    if (uri != null) {
                        if (compare(getApkInfo(context, uri.getPath()), context)) {
                            startInstall(context, uri);
                            return;
                        } else {
                            fdm.getDownloadManager().remove(downloadId);
                        }
                    }
                    start(context, url, title, appName);
                } else if (status == DownloadManager.STATUS_FAILED) {
                    start(context, url, title, appName);
                } else {
                    start(context, url, title, appName);
                }
            } else {
                start(context, url, title, appName);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static void start(Context context, String url, String title, String appName) {
        try {
            UtilTool.Log("更新", appName);
            FileDownloadManager manager = FileDownloadManager.getInstance(context);
            DownloadManager downloadManager = manager.getDownloadManager();
            Uri uri = Uri.parse(url);
            DownloadManager.Request request = new DownloadManager.Request(uri);
            request.setTitle(title);
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, appName + ".apk");
            long id = downloadManager.enqueue(request);
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
            sp.edit().putLong(DownloadManager.EXTRA_DOWNLOAD_ID, id).commit();
            Log.d(TAG, "apk start download " + id);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void startInstall(Context context, Uri uri) {
        try {
            UtilTool.Log("更新", uri.toString());
            if (Build.VERSION.SDK_INT >= 24) {
                String path = uri.getPath();
                File file= new File(path);
                Uri apkUri = FileProvider.getUriForFile(context, "com.bclould.tea.provider", file);//在AndroidManifest中的android:authorities值
                Intent install = new Intent(Intent.ACTION_VIEW);
                install.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);//添加这一句表示对目标应用临时授权该Uri所代表的文件
                install.setDataAndType(apkUri, "application/vnd.android.package-archive");
                context.startActivity(install);
            } else {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                intent.setType("application/vnd.android.package-archive");
                intent.setData(uri);
                intent.setDataAndType(uri, "application/vnd.android.package-archive");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    /**
     * 获取apk程序信息[packageName,versionName...]
     *
     * @param context Context
     * @param path    apk path
     */
    private static PackageInfo getApkInfo(Context context, String path) throws Exception{
        PackageManager pm = context.getPackageManager();
        PackageInfo info = null;
        try {
            info = pm.getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {

        }
        if (info != null) {
            return info;
        }
        return null;
    }


    /**
     * 下载的apk和当前程序版本比较
     *
     * @param apkInfo apk file's packageInfo
     * @param context Context
     * @return 如果当前应用版本小于apk的版本则返回true
     */
    private static boolean compare(PackageInfo apkInfo, Context context) throws Exception{
        if (apkInfo == null) {
            return false;
        }
        String localPackage = context.getPackageName();
        if (apkInfo.packageName.equals(localPackage)) {
            try {
                PackageInfo packageInfo = context.getPackageManager().getPackageInfo(localPackage, 0);
                if (apkInfo.versionCode > packageInfo.versionCode) {
                    return true;
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
