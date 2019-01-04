package com.bclould.tea.utils.permissions;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.content.PermissionChecker;

import com.bclould.tea.R;
import com.bclould.tea.base.MyApp;
import com.bclould.tea.ui.widget.ConfirmCancelDialog;
import com.bclould.tea.ui.widget.ConfirmDialog;
import com.bclould.tea.utils.ToastShow;


/**
 * Created by Administrator on 2017/3/20.
 * 权限校验 开启应用
 * 本类不提供静态方法，需要使用前先实例化
 */

public class AuthorizationCheck{
    public String ACCESS_NETWORK_STATE = "应用程序获取网络状态信息";
    public String ACCESS_WIFI_STATE = "应用程序获取WI-FI网络状态信息";
    public String BATTERY_STATES = "应用程序获取电池状态信息";
    public String BLUETOOTH = "应用程序链接匹配的蓝牙设备";
    public String BLUETOOTH_ADMIN = "应用程序发现匹配的蓝牙设备";
    public String BROADCAST_SMS = "应用程序广播收到短信提醒";
    public String CALL_PHONE = "应用程序拨打电话";
    public String CAMERA = "应用程序使用照相机";
    public String CHANGE_NETWORK_STATE = "应用程序改变网络连接状态";
    public String CHANGE_WIFI_STATE = "应用程序改变WIFI网络连接状态";
    public String DELETE_CACHE_FILES = "应用程序删除缓存文件";
    public String DELETE_PACKAGES = "应用程序删除安装包";
    public String FLASHLIGHT = "应用程序访问闪光灯";
    public String INTERNET = "应用程序打开网络Socket";
    public String MODIFY_AUDIO_SETTINGS = "应用程序修改全局声音设置";
    public String PROCESS_OUTGOING_CALLS = "应用程序监听、控制、取消呼出电话";
    public String READ_CONTACTS = "应用程序读取联系人信息";
    public String READ_HISTORY_BOOKMARKS = "应用程序读取历史书签";
    public String READ_OWNER_DATA = "应用程序读取用户数据";
    public String READ_PHONE_STATE = "应用读取手机状态";
    public String READ_PHONE_SMS = "应用程序读取短信";
    public String REBOOT = "应用程序重启系统";
    public String RECEIVE_MMS = "应用程序接受、监控、处理彩信";
    public String RECEIVE_SMS = "应用程序接受、监控、处理短信";
    public String RECORD_AUDIO = "应用程序录音";
    public String SEND_SMS = "应用程序发送短信";
    public String SET_ORIENTATION = "应用程序旋转屏幕";
    public String SET_TIME = "应用程序设置时间";
    public String SET_TIME_ZONE = "应用程序设置时区";
    public String SET_WALLPAPER = "应用程序设置桌面壁纸";
    public String VIBRATE = "应用程序控制震动器";
    public String WRITE_CONTACTS = "应用程序写入用户联系人";
    public String WRITE_HISTORY_BOOKMARKS = "应用程序写入历史书签";
    public String WRITE_OWNER_DATA = "应用程序写入用户数据";
    public String WRITE_SMS = "应用程序写短信";
    public String READ_EXTERNAL_STORAGE = "应用存储空间";
    public String WRITE_EXTERNAL_STORAGE = "应用存储空间";
//    public String READ_PHONE_STATE = "应用程序读取本机识别码";



    /**
     * 权限校验  只校验系统版本为6.0及以上时，默认6.0以下的校验权限为true
     * @param checkAuthorization   被校验 Manifest.permission.READ_CONTACTS
     * @param activity              当前Activty
     * @return  true 为有权限   false 没有权限
     */

    public boolean authorizationCheck(String checkAuthorization, Activity activity){
            int targetSdkVersion = 0;
            try {
                final PackageInfo info = activity.getPackageManager().getPackageInfo(
                        activity.getPackageName(), 0);
                targetSdkVersion = info.applicationInfo.targetSdkVersion;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (targetSdkVersion >= Build.VERSION_CODES.M) {
                    if (activity.checkSelfPermission(checkAuthorization) != PackageManager.PERMISSION_GRANTED) {
                        return false;
                    } else {
                        return true;
                    }
                } else {
                    if (PermissionChecker.checkSelfPermission(activity, checkAuthorization) != PermissionChecker.PERMISSION_GRANTED) {
                        return false;
                    } else {
                        return true;
                    }
                }
            }
        return true;
    }

    /**
     * 权限提示
     * @param activity  当前Activity
     * @param authorizationPromot  权限名称
     * @param withDiaolg   是否为Dialog提示，true为Dialog ，false 为 Toast
     * @param isMustOpen   当前权限是否必须开启
     * @param applicationExit  当前权限是否必须开启，如果不开启是否需要将应用整体退出
     */
    public void authorizationPromot(final Activity activity, String authorizationPromot,
                                    boolean withDiaolg, final  boolean isMustOpen,
                                    final boolean applicationExit){
        String promotWithMustOpen = activity.getString(R.string.not_obtainable)+authorizationPromot +activity.getString(R.string.open_access_path);
        String promotWithRead = activity.getString(R.string.not_obtainable1)+authorizationPromot+ activity.getString(R.string.open_access_path1);
        if(!withDiaolg){
            if(isMustOpen){
                ToastShow.showToastShowCenter(activity,promotWithMustOpen);
                openApplication(activity);
            }else{
                ToastShow.showToastShowCenter(activity,promotWithRead);
            }
            return ;
        }
        if(isMustOpen){
            promotDialog(activity,promotWithMustOpen,isMustOpen,applicationExit);
        }else{
            promotDialog(activity,promotWithRead,isMustOpen,applicationExit);
        }
    }

    private void promotDialog(final Activity activity, String promotContent, final  boolean isMustOpen,
                              final boolean applicationExit){
        if(activity.isFinishing()){
            return;
        }
        ;
        if(isMustOpen){
            ConfirmCancelDialog diaolg=new ConfirmCancelDialog(activity);
            diaolg.show();
            diaolg.setTvTitle(activity.getString(R.string.accessibility));
            diaolg.setTvContent(promotContent);
            diaolg.setOnClickListener(new ConfirmCancelDialog.OnClickListener() {
                @Override
                public void onClick() {
                    if(isMustOpen) {
                        openApplication(activity);
                    }
                    if(applicationExit){
                        activity.finish();
                        MyApp.getInstance().exit();
                    }
                }

                @Override
                public void onCancel() {
                    if(applicationExit){
                        activity.finish();
                        MyApp.getInstance().exit();
                    }
                }
            });

        }else{
            ConfirmDialog diaolg = new ConfirmDialog(activity);
            diaolg.show();
            diaolg.setTvTitle(activity.getString(R.string.accessibility));
            diaolg.setTvContent(promotContent);

        }

    }

    public void authorizationPromotPhoneState(Activity activity){
        String promotContent = "银盛通使用电话权限确定本机号码和设备ID，以保证账号登录的安全性。" + "银盛通不会拨打其它号码或终止通话。\n" + "请在设置->应用->银盛通->权限中开启电话权限，以正常使用银盛通。";
        promotDialog(activity,promotContent,true,true);
    }

    private void openApplication(Activity activity){
        Intent i = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
        String pkg = "com.android.settings";
        String cls = "com.android.settings.applications.InstalledAppDetails";
        i.setComponent(new ComponentName(pkg, cls));
        i.setData(Uri.parse("package:" + activity.getPackageName()));
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(i);
    }
}
