package com.bclould.tocotalk.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.RequiresApi;

import com.bclould.tocotalk.utils.CheckClassIsWork;
import com.bclould.tocotalk.utils.UtilTool;
import com.bclould.tocotalk.xmpp.XmppConnection;
import com.yyh.fork.NativeRuntime;

@RequiresApi(api = Build.VERSION_CODES.N)
public class IMCoreService extends Service {
    private final static int time = 15 * 1000;
    private final static String SERVICE_NAME = "com.bclould.tocotalk.service.IMService";
    public final static String ACTION_LOGIN = "com.bclould.tocotalk.action.ACTION_LOGIN";
    public final static String ACTION_LOGOUT = "com.bclould.tocotalk.action.ACTION_LOGOUT";
    public final static String ACTION_STOP = "com.bclould.tocotalk.action.ACTION_STOP";
    public final static String ACTION_START_IMSERVICE = "com.bclould.tocotalk.action.ACTION_START_IMSERVICE";
    private BroadcastReceiver broadcast;
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
        broadcast = new StaticBroadcastReceiver();
        try {
            this.registerReceiver(broadcast, filter);
        }
        catch(IllegalArgumentException e) {
            e.printStackTrace();
        }

//        NativeRuntime.getInstance().stringFromJNI();
//        (new Thread(new Runnable() {
//
//            @Override
//            public void run() {
//                try {
//                    String executable = "libhelper.so";
//                    String aliasfile = "helper";
//                    NativeRuntime.getInstance().RunExecutable(IMCoreService.this.getPackageName(),
//                            executable, aliasfile, IMCoreService.this.getPackageName() +
//                                    "/com.ysepay.mobileportal.im.IMCoreService");
//                    NativeRuntime.getInstance().startService(IMCoreService.this.getPackageName()
//                                    + "/com.ysepay.mobileportal.im.IMCoreService",
//                            FileUtils.createRootPath(IMCoreService.this));
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        })).start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        flags = START_STICKY;
        UtilTool.Log("fengjian","service core onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
//            this.unregisterReceiver(broadcast);
            handler.removeMessages(0);
            handler = null;
            UtilTool.Log("fengjian","core service destory");
        } catch (Exception e) {
            e.printStackTrace();
        }
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
                        UtilTool.Log("--------","service-- 没登录");
                        if (XmppConnection.isServiceWork(IMCoreService.this, SERVICE_NAME)) {
                            IMCoreService.this.stopService(new Intent(IMCoreService.this, IMService.class));
                        }
                        this.sendEmptyMessageDelayed(0, time);
                        return;
                    }
                    if (XmppConnection.isServiceWork(IMCoreService.this, SERVICE_NAME)) {
                        UtilTool.Log("--------","service-- 打开了！");
                        if (CheckClassIsWork.isTopActivity(IMCoreService.this, "LoginActivity")) {
                            startService = false;
                            UtilTool.Log("--------","關閉服務");
                            IMCoreService.this.stopService(new Intent(IMCoreService.this, IMService.class));
                        }
                        this.sendEmptyMessageDelayed(0, time);
                        return;
                    }
                    UtilTool.Log("--------","service-- 没打开");
                    Intent intent = new Intent();
                    intent.setAction(IMCoreService.ACTION_START_IMSERVICE);
                    IMCoreService.this.sendBroadcast(intent);
                    this.sendEmptyMessageDelayed(0, time);
                    this.sendEmptyMessageDelayed(1,time);
                    break;
                case 1:
//                    if(!hasStartChildProcess){
//                        hasStartChildProcess = true;
//                        UtilTool.Log("---------","調用JNI");
//                        NativeRuntime.getInstance().stringFromJNI();
//                        (new Thread(new Runnable() {
//
//                            @Override
//                            public void run() {
//                                try {
//                                    String executable = "libhelper.so";
//                                    String aliasfile = "helper";
//                                    NativeRuntime.getInstance().RunExecutable(IMCoreService.this.getPackageName(),
//                                            executable, aliasfile, IMCoreService.this.getPackageName() +
//                                                    "/com.bclould.tocotalk.service.IMCoreService");
//                                    NativeRuntime.getInstance().startService(IMCoreService.this.getPackageName()
//                                                    + "/com.bclould.tocotalk.service.IMCoreService",
//                                            UtilTool.createRootPath(IMCoreService.this));
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
            }
        }
	}
}
