package com.bclould.tea.utils.permissions;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;

import com.bclould.tea.R;
import com.bclould.tea.ui.widget.ConfirmDialog;
import com.bclould.tea.utils.ToastShow;

/**
 * Created by Administrator on 2017/3/20.
 * 检测权限
 */
public class AuthorizationUserTools {
    /**
     * 拨打电话
     * @param activity  当前Activity
     * @param phoneNumber  拨打手机号
     */
    public static void dial(Activity activity, String phoneNumber){
//        AuthorizationCheck check = new AuthorizationCheck();
//        boolean haveAuthorization =  check.authorizationCheck(Manifest.permission.CALL_PHONE,activity);
//        if(haveAuthorization){
            try {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_DIAL);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setData(Uri.parse("tel:" + phoneNumber));
                activity.startActivity(intent);
            }catch (Exception e){
               e.printStackTrace();
            }
//        }else{
//            check.authorizationPromot(activity,check.CALL_PHONE,true,false,false);
//        }
    }

    /**
     * 读取手机联系人
     * @param activity   当前Activity
     * @param requestCode  回调编号
     */
    public static void read_contacts(Activity activity, int requestCode){
        AuthorizationCheck check = new AuthorizationCheck();
        boolean haveAuthorization =  check.authorizationCheck(Manifest.permission.READ_CONTACTS,activity);
        if(haveAuthorization){
            Intent intent = new Intent(Intent.ACTION_PICK,
                    android.provider.ContactsContract.Contacts.CONTENT_URI);
            activity.startActivityForResult(intent, requestCode);
        }else{
            check.authorizationPromot(activity,check.READ_CONTACTS,true,false,false);
        }
    }

    /**
     * 读取手机联系人
     * @param activity   当前Activity
     * @param isGetContacts  是否获取联系人
     * @param promot 没有权限时是否提示
     * @param requestCode  回调编号
     */
    public static boolean read_contacts(Activity activity, boolean isGetContacts, boolean promot, int requestCode){
        AuthorizationCheck check = new AuthorizationCheck();
        boolean haveAuthorization =  check.authorizationCheck(Manifest.permission.READ_CONTACTS,activity);
        if(haveAuthorization && isGetContacts){
            Intent intent = new Intent(Intent.ACTION_PICK,
                    android.provider.ContactsContract.Contacts.CONTENT_URI);
            activity.startActivityForResult(intent,requestCode);
            return true;
        }else if(!haveAuthorization && promot){
            check.authorizationPromot(activity,check.READ_CONTACTS,true,false,false);
            return false;
        }else if(haveAuthorization && !isGetContacts){
            return true;
        }else{
            return false;
        }
    }

    /**
     * 储存权限
     * @param activity   当前activity
     * @param needPromot  需要提示，提示为单确认框，只做确认
     * @return  添加返回值，因存储权限影响范围不确定，false没权限 true有权限
     */
    public static boolean externalStorage(Activity activity, boolean needPromot){
        AuthorizationCheck check = new AuthorizationCheck();
        boolean haveAuthorization =  (check.authorizationCheck(Manifest.permission.READ_EXTERNAL_STORAGE,activity)
        &&check.authorizationCheck(Manifest.permission.WRITE_EXTERNAL_STORAGE,activity));
        if(!haveAuthorization){
            if(needPromot){
                check.authorizationPromot(activity,activity.getString(R.string.application_storage_space),true,false,false);
            }
            return false;
        }else{
            return true;
        }
    }

    /**
     * 录音权限
     * @param activity   当前activity
     * @param needPromot  需要提示，提示为单确认框，只做确认
     * @return  添加返回值，因存储权限影响范围不确定，false没权限 true有权限
     */
    public static boolean externalRecording(Activity activity, boolean needPromot){
        AuthorizationCheck check = new AuthorizationCheck();
        boolean haveAuthorization =  (check.authorizationCheck(Manifest.permission.RECORD_AUDIO,activity));
        if(!haveAuthorization){
            if(needPromot){
                check.authorizationPromot(activity,activity.getString(R.string.application_recording),true,false,false);
            }
            return false;
        }else{
            return true;
        }
    }

    /**
     * 定位权限
     * @param activity   当前activity
     * @param needPromot  需要提示，提示为单确认框，只做确认
     * @return  添加返回值，因存储权限影响范围不确定，false没权限 true有权限
     */
    public static boolean externalLocation(Activity activity, boolean needPromot){
        AuthorizationCheck check = new AuthorizationCheck();
        boolean haveAuthorization =  (check.authorizationCheck(Manifest.permission.ACCESS_FINE_LOCATION,activity)
        &&check.authorizationCheck(Manifest.permission.ACCESS_COARSE_LOCATION,activity));
        if(!haveAuthorization){
            if(needPromot){
                check.authorizationPromot(activity,activity.getString(R.string.applied_position),true,false,false);
            }
            return false;
        }else{
            return true;
        }
    }

    /**
     * 储存权限
     * @param activity   当前activity
     * @param needPromot  需要提示，提示为单确认框，只做确认
     * @return  添加返回值，因存储权限影响范围不确定，false没权限 true有权限
     */

    public static boolean externalStorage(Activity activity, boolean needPromot, boolean isMustOpen, boolean applicationExit){
        AuthorizationCheck check = new AuthorizationCheck();
        boolean haveAuthorization =  (check.authorizationCheck(Manifest.permission.READ_EXTERNAL_STORAGE,activity)
                &&check.authorizationCheck(Manifest.permission.WRITE_EXTERNAL_STORAGE,activity));
        if(!haveAuthorization){
            if(needPromot){
                check.authorizationPromot(activity,activity.getString(R.string.application_storage_space),true,isMustOpen,applicationExit);
            }
            return false;
        }else{
            return true;
        }
    }

    /**
     * 储存权限  强制提醒
     * @param activity   当前activity
     * @param needPromot  需要提示，提示为单确认框，只做确认
     * @return  添加返回值，因存储权限影响范围不确定，false没权限 true有权限
     */
    public static boolean externalStorageForcePromot(Activity activity, boolean needPromot){
        AuthorizationCheck check = new AuthorizationCheck();
        boolean haveAuthorization =  (check.authorizationCheck(Manifest.permission.READ_EXTERNAL_STORAGE,activity)
                &&check.authorizationCheck(Manifest.permission.WRITE_EXTERNAL_STORAGE,activity));
        if(!haveAuthorization){
            if(needPromot){
                check.authorizationPromot(activity,activity.getString(R.string.application_storage_space),true,true,true);
            }
            return false;
        }else{
            return true;
        }
    }

    /**
     * 使用手机相册
     * @param activity
     * @param requestCode
     */
    public static void userPhonePhoto(Activity activity, String type, int requestCode){
        AuthorizationCheck check = new AuthorizationCheck();
        boolean haveAuthorization =  (check.authorizationCheck(Manifest.permission.READ_EXTERNAL_STORAGE,activity)
                &&check.authorizationCheck(Manifest.permission.WRITE_EXTERNAL_STORAGE,activity));
        if(haveAuthorization){
            Intent intent1 = new Intent(Intent.ACTION_PICK, null);
            intent1.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,type);
            activity.startActivityForResult(intent1, requestCode);
        }else{
            check.authorizationPromot(activity,activity.getString(R.string.application_storage_space),true,false,false);
        }
    }

    public static boolean getTellphoneState(Activity activity, boolean needPromot){
        AuthorizationCheck check = new AuthorizationCheck();
        boolean haveAuthorization =  check.authorizationCheck(Manifest.permission.READ_PHONE_STATE,activity);
        if(!haveAuthorization){
            if(needPromot){
                check.authorizationPromot(activity,activity.getString(R.string.application_storage_space),true,false,false);
            }
        }
        return haveAuthorization;
    }

    public static boolean getCallPhone(Activity activity, boolean needPromot){
        AuthorizationCheck check = new AuthorizationCheck();
        boolean haveAuthorization =  check.authorizationCheck(Manifest.permission.CALL_PHONE,activity);
        if(!haveAuthorization){
            if(needPromot){
                check.authorizationPromot(activity,activity.getString(R.string.application_calls),true,false,false);
            }
        }
        return haveAuthorization;
    }

    /**
     * 判断是否有摄像头权限
     * @param activity
     * @return
     */
    public static boolean isCameraCanUse(Activity activity){

        try {
            Camera camera = Camera.open();
            camera.release();
            camera = null;
            return true;
        } catch (Exception e) {
            if(activity.isFinishing()){
                ToastShow.showToast(activity,activity.getString(R.string.camera_permission_info));
                return false;
            }
            ConfirmDialog dialog=new ConfirmDialog(activity);
            dialog.show();
            dialog.setTvTitle(activity.getString(R.string.camera_permission_info));
            return false;
        }
    }
}
