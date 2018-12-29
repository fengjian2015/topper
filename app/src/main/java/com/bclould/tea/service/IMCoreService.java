package com.bclould.tea.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;

import com.bclould.tea.topperchat.WsConnection;
import com.bclould.tea.utils.UtilTool;

@RequiresApi(api = Build.VERSION_CODES.N)
public class IMCoreService extends Service {
    private final static int time = 15 * 1000;
    public final static String CORE_SERVICE_NAME="com.bclould.tea.service.IMCoreService";
    public final static String SERVICE_NAME = "com.bclould.tea.service.IMService";
    public final static String ACTION_LOGIN = "com.bclould.tea.action.ACTION_LOGIN";
    public final static String ACTION_LOGOUT = "com.bclould.tea.action.ACTION_LOGOUT";
    public final static String ACTION_STOP = "com.bclould.tea.action.ACTION_STOP";
    public final static String ACTION_START_IMSERVICE = "com.bclould.tea.action.ACTION_START_IMSERVICE";
    private BroadcastReceiver broadcast;
    private final static int GRAY_SERVICE_ID = 1001;
    public boolean startService = true;// 用于判断是否需要打开消息监听service

    public boolean hasStartChildProcess = false;

    @Override
    public void onCreate() {
        super.onCreate();
        UtilTool.Log("fengjian","service core start" + android.os.Process.myPid());
//		if (!"Coolpad 8297D".equals(android.os.Build.MODEL) && !"HUAWEI G750-T20".equals(android.os.Build.MODEL)) {
//			ServerConnection.startService(this);
//		}
        this.stopService(new Intent(this, IMService.class));
        handler.removeMessages(0);
        handler.sendEmptyMessage(0);
        UtilTool.Log("-----------","創建服務");
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_LOGIN);
        filter.addAction(ACTION_LOGOUT);
        filter.addAction(ACTION_STOP);
        filter.addAction(ACTION_START_IMSERVICE);
        broadcast = new StaticBroadcastReceiver();
        try {
            this.registerReceiver(broadcast, filter);
        }
        catch(IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        flags = START_STICKY;
        UtilTool.Log("fengjian","service core onStartCommand");
//        return super.onStartCommand(intent, flags, startId);
        if (Build.VERSION.SDK_INT < 18) {
            startForeground(GRAY_SERVICE_ID, new Notification());//API < 18 ，此方法能有效隐藏Notification上的图标
        } else {
            Intent innerIntent = new Intent(this, GrayInnerService.class);
            startService(innerIntent);
            startForeground(GRAY_SERVICE_ID, new Notification());
        }
        return Service.START_REDELIVER_INTENT;
    }

    public static class GrayInnerService extends Service {

        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            startForeground(GRAY_SERVICE_ID, new Notification());
            stopForeground(true);
            stopSelf();
            return super.onStartCommand(intent, flags, startId);
        }

        @Nullable
        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }

    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
//            this.unregisterReceiver(broadcast);
            handler.removeMessages(0);
            handler = null;
            UtilTool.Log("fengjian","core service destory");
            gcEnv();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void gcEnv() {
        Intent serviceTo = new Intent();
        serviceTo.setClass(this, IMCoreService.class);
        this.startService(serviceTo);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    if (!startService) {
                        UtilTool.Log("fengjian","service-- 没登录");
                        if (UtilTool.isServiceRunning(IMCoreService.this, SERVICE_NAME)) {
                            IMCoreService.this.stopService(new Intent(IMCoreService.this, IMService.class));
                        }
                        this.sendEmptyMessageDelayed(0, time);
                        return;
                    }
                    if (UtilTool.isServiceRunning(IMCoreService.this, SERVICE_NAME)) {
                        UtilTool.Log("fengjian","service-- 打开了！");
                        if (WsConnection.getInstance().getOutConnection()) {
                            startService = false;
                            UtilTool.Log("fengjian","關閉服務");
                            IMCoreService.this.stopService(new Intent(IMCoreService.this, IMService.class));
                        }
                        this.sendEmptyMessageDelayed(0, time);
                        return;
                    }
                    UtilTool.Log("fengjian","service-- 没打开服務");
                    Intent intent = new Intent();
                    intent.setAction(IMCoreService.ACTION_START_IMSERVICE);
                    sendBroadcast(intent);
                    this.sendEmptyMessageDelayed(0, time);
                    this.sendEmptyMessageDelayed(1,time);
                    break;
                case 1:
//                    if(!hasStartChildProcess){
//                        hasStartChildProcess = true;
//                        UtilTool.Log("fengjian","調用JNI"+IMCoreService.this.getPackageName());
//                        NativeRuntime.getInstance().stringFromJNI();
//                        (new Thread(new Runnable() {
//
//                            @Override
//                            public void run() {
//                                try {
//                                    String executable = "libhelper.so";
//                                    String aliasfile = "helper";
//                                    NativeRuntime.getInstance().RunExecutable(getPackageName(), executable, aliasfile, getPackageName()+"/com.bclould.tea.service.IMCoreService");
//                                    NativeRuntime.getInstance().startService(getPackageName()+"/com.bclould.tea.service.IMCoreService",UtilTool.createRootPath(IMCoreService.this));
//                                } catch (Exception e) {
//                                    e.printStackTrace();
//                                }
//                            }
//                        })).start();
//                    }
                    break;
                default:
                    break;
            }
        }

        ;
    };

    public class StaticBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ACTION_LOGIN)) {
                UtilTool.Log("fengjian","service收到登录的广播了~~");
                startService = true;
            } else if (intent.getAction().equals(ACTION_LOGOUT)) {
                UtilTool.Log("fengjian","service收到登出的广播了~~");
                if (handler != null) {
                    handler.removeMessages(0);
                }
                context.stopService(new Intent(context, IMService.class));
                // IMCoreService.this.stopSelf();
                startService = false;
            } else if (intent.getAction().equals(ACTION_STOP)) {
                IMCoreService.this.stopSelf();
                if (handler != null) {
                    handler.removeMessages(0);
                }
                startService = false;
            }else if(intent.getAction().equals(ACTION_START_IMSERVICE)){
                UtilTool.Log("fengjian","---收到打开IMService的广播");
                if (UtilTool.isServiceRunning(context, "com.bclould.tea.service.IMService")) {
                    return;
                }
                Intent startIntent = new Intent(context, IMService.class);
                context.startService(startIntent);
            }
        }
	}
}
