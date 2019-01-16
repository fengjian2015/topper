package com.bclould.tea.network;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.bclould.tea.R;
import com.bclould.tea.utils.Constants;
import com.bclould.tea.utils.MySharedPreferences;
import com.bclould.tea.utils.UtilTool;

import java.io.File;

import static android.os.Build.VERSION_CODES.M;

/**
 * Created by lenovo on 2018/2/1 0001.
 */

public class ApkInstallReceiver extends BroadcastReceiver {

    private Context mContext;
    private File mFile;

    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        if (intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
            long downloadApkId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            installApk(context, downloadApkId);
        }
    }

    /**
     * 安装apk
     */
    private void installApk(Context context, long downloadApkId) {
        try {
            // 获取存储ID
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
            mFile = new File(Constants.DOWNLOAD + "topperchat_" + MySharedPreferences.getInstance().getString(Constants.APK_VERSIONS_TAG) + ".apk");
            UtilTool.Log("fengjian","安装路径"+mFile.getAbsolutePath());
            UtilTool.install(context,mFile);
//            long downId = sp.getLong(DownloadManager.EXTRA_DOWNLOAD_ID, -1L);
//            if (downloadApkId == downId) {
//                DownloadManager downManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
//                Uri downloadFileUri = downManager.getUriForDownloadedFile(downloadApkId);
//                if (downloadFileUri != null) {
//                    if (Build.VERSION.SDK_INT >= M) {
//                        Intent install = new Intent(Intent.ACTION_VIEW);
//                        install.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);//添加这一句表示对目标应用临时授权该Uri所代表的文件
//                        install.setDataAndType(downloadFileUri, "application/vnd.android.package-archive");
//                        context.startActivity(install);
//                    } else {
//                        Intent install = new Intent(Intent.ACTION_VIEW);
//                        install.setDataAndType(downloadFileUri, "application/vnd.android.package-archive");
//                        install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        context.startActivity(install);
//                    }
//
//                } else {
//                    Toast.makeText(context, mContext.getString(R.string.download_failed), Toast.LENGTH_SHORT).show();
//                }
//            }
        }catch (Exception e){
            Toast.makeText(context, mContext.getString(R.string.installation_failed), Toast.LENGTH_SHORT).show();
        }
    }
}
