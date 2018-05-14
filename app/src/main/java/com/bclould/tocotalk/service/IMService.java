package com.bclould.tocotalk.service;

import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.RequiresApi;

import com.bclould.tocotalk.utils.CheckClassIsWork;
import com.bclould.tocotalk.utils.UtilTool;
import com.bclould.tocotalk.xmpp.ConnectStateChangeListenerManager;
import com.bclould.tocotalk.xmpp.IMLogin;
import com.bclould.tocotalk.xmpp.XmppConnection;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;

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
        if (CheckClassIsWork.isTopActivity(this, "LoginActivity")) {
            this.stopService(new Intent(this, IMService.class));
            return;
        }
        if ((XmppConnection.getInstance().getConnection() != null && XmppConnection.getInstance().getConnection()
                .isConnected()&&!XmppConnection.getInstance().getConnection()
                .isAuthenticated())) {
            UtilTool.Log("----------","首次登錄"+XmppConnection.getInstance().getConnection().isConnected()+"   "+
                    XmppConnection.getInstance().getConnection().isAuthenticated());
            IMLogin.login(this);
        } else if(XmppConnection.getInstance().getConnection() == null){
            ConnectStateChangeListenerManager.get().notifyListener(
                    ConnectStateChangeListenerManager.CONNECTING);
            handler.removeMessages(EXLOGIN);
            exReconnect(1000);
        }else{
            ConnectStateChangeListenerManager.get().notifyListener(
                    ConnectStateChangeListenerManager.CONNECTED);
            handler.removeMessages(EXLOGIN);
            exReconnect(1000);
        }
    }

    public void exReconnect(long delayMillis) {
        if (handler == null) {
            UtilTool.Log("fengjian","EXLOGIN--handler null");
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
                                && !XmppConnection.getInstance().getConnection().isConnected()) {
                            ConnectStateChangeListenerManager.get().notifyListener(
                                    ConnectStateChangeListenerManager.CONNECTING);
                            fistlogIm();
                        } else {
                            this.sendEmptyMessageDelayed(RELOGIN, 5 * 1000);
                        }
                        break;
                    case EXLOGIN: {
                        synchronized (this) {
                            if (CheckClassIsWork.isTopActivity(IMService.this, "LoginActivity")) {
                                XmppConnection.getInstance().closeConnection();
                                break;
                            }
                            if(XmppConnection.getInstance().getConnection()==null){
                                exReconnect(2000);
                                break;
                            }
                            if (XmppConnection.getInstance().getConnection().isConnected()) {
//                            MyLogger.xuxLog().i("-----EXLOGIN1");
                                if (ConnectStateChangeListenerManager.get().getCurrentState()
                                        != ConnectStateChangeListenerManager.CONNECTED && ConnectStateChangeListenerManager.get().getCurrentState()
                                        != ConnectStateChangeListenerManager.RECEIVING) {
                                    disconnect();
                                    UtilTool.Log("---------","目前登錄狀態不符合："+ConnectStateChangeListenerManager.get().getCurrentState()+"    "+XmppConnection.getInstance().getConnection().isConnected());
                                }
                                if(!XmppConnection.getInstance().getConnection().isAuthenticated()){
                                    disconnect();
                                    UtilTool.Log("---------","目前登錄狀態不符合："+XmppConnection.getInstance().getConnection().isAuthenticated());
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
                XmppConnection.getInstance().getConnection().disconnect();
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
            if (CheckClassIsWork.isTopActivity(this, "LoginActivity")) {
                this.stopService(new Intent(this, IMService.class));
                return;
            }
            UtilTool.Log("---------","重連："+XmppConnection.getInstance().getConnection().isConnected()+"   "+
                    XmppConnection.getInstance().getConnection().isAuthenticated());
            try {
                UtilTool.Log("fengjian","ConnectionListener：开始准备重连IM");
                setConnectState();
                 if (!IMLogin.isNetworkActivity(this)) {
                    UtilTool.Log("fengjian","----沒網絡");
                    exReconnect(1000);
                    return;
                }
                XmppConnection.getInstance().getConnection();
                thread1.sleep(10000);

                if(XmppConnection.getInstance().getConnection().isAuthenticated()){
                    Presence presence = new Presence(Presence.Type.unavailable);
                    presence.setPriority(0);
                    XmppConnection.getInstance().getConnection().sendPacket(presence);
                    XmppConnection.getInstance().getConnection().disconnect();
                }
                UtilTool.Log("fengjian","ConnectionListener：重连IM");
            }catch (Exception e) {
                UtilTool.Log("fengjian","----拋異常");
                exReconnect(1000);
                e.printStackTrace();
            }
            if (!XmppConnection.getInstance().getConnection().isConnected()) {
                UtilTool.Log("fengjian","----未連接");
                exReconnect(1000);
                return;
            }
            if (IMLogin.loginAction(this, XmppConnection.getInstance().getConnection())) {
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