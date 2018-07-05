package com.bclould.tea.service;

import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.RequiresApi;

import com.bclould.tea.topperchat.WsConnection;
import com.bclould.tea.topperchat.WsOfflineConnection;
import com.bclould.tea.utils.CheckClassIsWork;
import com.bclould.tea.utils.UtilTool;
import com.bclould.tea.xmpp.ConnectStateChangeListenerManager;
import com.bclould.tea.xmpp.IMLogin;
import com.bclould.tea.xmpp.RoomManage;

@RequiresApi(api = Build.VERSION_CODES.N)
public class IMService extends Service{
    private final static int RELOGIN = 0;
    private final static int EXLOGIN = 1;
    private Handler handler = null;
    Thread thread1;
    @Override
    public void onCreate() {
        super.onCreate();
        UtilTool.Log("fengjian","----IMService oncreate");
        RoomManage.getInstance().setContext(this);
        WsConnection.getInstance().setContext(this);
        WsOfflineConnection.getInstance().setContext(this);
        if(handler==null) {
            instanceHandler();
        }
        loginIM();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        UtilTool.Log("fengjian","----Service onDestroy");
        handler.removeMessages(RELOGIN);
        handler.removeMessages(EXLOGIN);
        handler = null;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    private void loginIM() {
        fistlogIm();
    }

    private void fistlogIm() {
        if (WsConnection.getInstance().getOutConnection()) {
            this.stopService(new Intent(this, IMService.class));
            return;
        }
        WsOfflineConnection.getInstance().get(IMService.this);
        if (WsConnection.getInstance().get(IMService.this)!=null&&WsConnection.getInstance().get(IMService.this).isOpen()|| WsConnection.getInstance().isLogin()) {
            ConnectStateChangeListenerManager.get().notifyListener(ConnectStateChangeListenerManager.CONNECTED);
            handler.removeMessages(EXLOGIN);
            exReconnect(1000);
        }else {
            ConnectStateChangeListenerManager.get().notifyListener(ConnectStateChangeListenerManager.CONNECTING);
            IMLogin.login(this);
        }
    }

    public void exReconnect(long delayMillis) {
        if (handler == null) {
            instanceHandler();
        }
        handler.sendEmptyMessageDelayed(EXLOGIN, delayMillis);
    }

    private Handler instanceHandler(){
        handler = new Handler(getMainLooper()) {
            public void handleMessage(android.os.Message msg) {
                switch (msg.what) {
                    case RELOGIN:
                        if(!IMLogin.isNetworkActivity(IMService.this)){
                            ConnectStateChangeListenerManager.get().notifyListener(
                                    ConnectStateChangeListenerManager.DISCONNECT);
                        }
                        if (IMLogin.isNetworkActivity(IMService.this)
                                && !WsConnection.getInstance().isLogin()) {
                            ConnectStateChangeListenerManager.get().notifyListener(
                                    ConnectStateChangeListenerManager.CONNECTING);
                            fistlogIm();
                        } else {
                            this.sendEmptyMessageDelayed(RELOGIN, 5 * 1000);
                        }
                        break;
                    case EXLOGIN: {
                        synchronized (this) {
                            if (WsConnection.getInstance().getOutConnection()) {
                                WsConnection.getInstance().get(IMService.this).close();
                                WsOfflineConnection.getInstance().closeConnection();
                                break;
                            }
                            WsOfflineConnection.getInstance().get(IMService.this);
                            if(WsConnection.getInstance().get(IMService.this)==null){
                                exReconnect(2000);
                                break;
                            }
                            if (WsConnection.getInstance().get(IMService.this).isOpen()) {
//                            MyLogger.xuxLog().i("-----EXLOGIN1");
                                if (ConnectStateChangeListenerManager.get().getCurrentState()
                                        != ConnectStateChangeListenerManager.CONNECTED && ConnectStateChangeListenerManager.get().getCurrentState()
                                        != ConnectStateChangeListenerManager.RECEIVING&&
                                        !WsConnection.getInstance().getLoginConnection()) {
                                    disconnect();
                                    UtilTool.Log("fengjian","目前登錄狀態不符合："+ConnectStateChangeListenerManager.get().getCurrentState()+"    "+WsConnection.getInstance().get(IMService.this).isOpen());
                                    exReconnect(2000);
                                    break;
                                }
                                if(!WsConnection.getInstance().isLogin()&&!WsConnection.getInstance().getLoginConnection()){
                                    disconnect();
                                    UtilTool.Log("fengjian","目前登錄狀態不符合："+WsConnection.getInstance().isLogin());
                                }
                                exReconnect(2000);
                                break;
                            }
                            if (!IMLogin.isNetworkActivity(IMService.this)) {
//                            MyLogger.xuxLog().i("-----EXLOGIN2");
                                exReconnect(3 * 1000);
                                ConnectStateChangeListenerManager.get().notifyListener(
                                        ConnectStateChangeListenerManager.DISCONNECT);
                                break;
                            }
//                        MyLogger.xuxLog().i("-----EXLOGIN3");
                            ConnectStateChangeListenerManager.get().notifyListener(
                                    ConnectStateChangeListenerManager.CONNECTING);
                            thread1=new Thread(){
                                @Override
                                public void run() {
                                    exloginIM();
                                }
                            };
                            thread1.start();
                        }
                        break;
                    }
                    default:
                        break;
                }
            };
        };
        return handler;
    }


    private void disconnect(){
        new Thread(){
            @Override
            public void run() {
                UtilTool.Log("fengjian","關閉連接");
                WsConnection.getInstance().closeConnection();
                ConnectStateChangeListenerManager.get().notifyListener(
                        ConnectStateChangeListenerManager.CONNECTING);
            }
        }.start();
    }

    private void setConnectState() {
        if (!IMLogin.isNetworkActivity(this)) {
            ConnectStateChangeListenerManager.get().notifyListener(
                    ConnectStateChangeListenerManager.DISCONNECT);
        } else {
            ConnectStateChangeListenerManager.get().notifyListener(
                    ConnectStateChangeListenerManager.CONNECTING);
        }
    }

    private void exloginIM() {
        synchronized (this) {
            if (WsConnection.getInstance().getOutConnection()) {
                this.stopService(new Intent(this, IMService.class));
                return;
            }
            UtilTool.Log("---------","重連："+WsConnection.getInstance().get(IMService.this).isOpen()+"   "+
                    WsConnection.getInstance().isLogin());
            try {
                UtilTool.Log("fengjian","ConnectionListener：开始准备重连IM");
                setConnectState();
                 if (!IMLogin.isNetworkActivity(this)) {
                    UtilTool.Log("fengjian","----沒網絡");
                    exReconnect(1000);
                    return;
                }
                WsConnection.getInstance().get(IMService.this);
                thread1.sleep(10000);

                UtilTool.Log("fengjian","ConnectionListener：重连IM");
            }catch (Exception e) {
                UtilTool.Log("fengjian","----拋異常");
                exReconnect(1000);
                e.printStackTrace();
            }
            if (!WsConnection.getInstance().get(IMService.this).isOpen()) {
                UtilTool.Log("fengjian","----未連接");
                exReconnect(1000);
                return;
            }
            if (IMLogin.loginAction()) {
                ConnectStateChangeListenerManager.get().notifyListener(
                        ConnectStateChangeListenerManager.RECEIVING);
                UtilTool.Log("fengjian","----鏈接成功");
                exReconnect(1000);
            }else{
                UtilTool.Log("fengjian","----鏈接異常");
                exReconnect(1000);
            }
        }
    }
}
