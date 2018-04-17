package com.bclould.tocotalk.network;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.bclould.tocotalk.R;

/**
 * Created by lenovo on 2018/2/1 0001.
 */

public class ApkInstallReceiver extends BroadcastReceiver {

    private Context mContext;

    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        if(intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)){
            long downloadApkId =intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            installApk(context, downloadApkId);
        }
    }

    /**
     * 安装apk
     */
    private void installApk(Context context, long downloadApkId) {
        // 获取存储ID
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        long downId =sp.getLong(DownloadManager.EXTRA_DOWNLOAD_ID,-1L);
        if(downloadApkId == downId){
            DownloadManager downManager= (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            Uri downloadFileUri = downManager.getUriForDownloadedFile(downloadApkId);
            if (downloadFileUri != null) {
                Intent install= new Intent(Intent.ACTION_VIEW);
                install.setDataAndType(downloadFileUri, "application/vnd.android.package-archive");
                install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(install);
            }else{
                Toast.makeText(context, mContext.getString(R.string.download_failed), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
