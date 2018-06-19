package com.bclould.tea.xmpp;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.XMPPConnection;

import java.util.Timer;

/**
 * 连接监听类
 */
@RequiresApi(api = Build.VERSION_CODES.N)
public class XMConnectionListener implements ConnectionListener {
    private final Context mContext;
    private Timer tExit;
    private int loginTime = 1000;



    public XMConnectionListener(Context context) {
        super();
        mContext = context;
    }

    @Override
    public void connected(XMPPConnection xmppConnection) {
        Log.i("XMConnectionListener", "connected");
    }

    @Override
    public void authenticated(XMPPConnection xmppConnection, boolean b) {
        Log.i("XMConnectionListener", "authenticated" + b);
    }

    @Override
    public void connectionClosed() {
        /*Log.i("XMConnectionListener", "连接关闭");
        // 关闭连接
        XmppConnection.getInstance().closeConnection();
        // 重连服务器
        if (tExit == null) {
            tExit = new Timer();
            tExit.schedule(new TimeTask(), loginTime);
        }*/
    }

    @Override
    public void connectionClosedOnError(Exception e) {
        Log.i("XMConnectionListener", "连接关闭异常");

        // 判断账号已被登录
        boolean error = e.getMessage().equals("stream:error (conflict)");
        if (!error) {
//            XmppConnection.logoutService(mContext);
            // 关闭连接
            XmppConnection.getInstance().closeConnection();
        } else {
            // TODO: 2018/5/14 這裡需要做的是踢出登錄
            // 退出登录
        }
    }

    @Override
    public void reconnectingIn(int in) {
        Log.i("XMConnectionListener", "reconnectingIn" + in);
    }

    @Override
    public void reconnectionFailed(Exception e) {
        Log.i("XMConnectionListener", "reconnectionFailed" + e.getMessage());
    }

    @Override
    public void reconnectionSuccessful() {
        Log.i("XMConnectionListener", "reconnectionSuccessful");
    }

}
