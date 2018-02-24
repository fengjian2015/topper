package com.dashiji.biyun.utils;

import android.util.Log;

import com.dashiji.biyun.xmpp.XmppConnection;

import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.XMPPConnection;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by GA on 2017/12/20.
 */
public class TaxiConnectionListener implements ConnectionListener {

    private Timer mTimer;
    private int logintime = 2000;

    @Override
    public void connected(XMPPConnection connection) {

    }

    @Override
    public void authenticated(XMPPConnection connection, boolean resumed) {

    }

    @Override
    public void connectionClosed() {
        Log.i("fsdafa", "連接關閉");
        // 關閉連接
        XmppConnection.getInstance().getConnection().disconnect();
        // 重连服务器
        mTimer = new Timer();
        mTimer.schedule(new timetask(), logintime);
    }

    @Override
    public void connectionClosedOnError(Exception e) {
        Log.i("fsdafa", "連接關閉異常");
        // 判斷為帳號已被登錄
        boolean error = e.getMessage().equals("stream:error (conflict)");
        if (!error) {
            // 關閉連接
            XmppConnection.getInstance().getConnection().disconnect();
            // 重连服务器
            mTimer = new Timer();
            mTimer.schedule(new timetask(), logintime);
        }
    }

    class timetask extends TimerTask {
        @Override
        public void run() {
            try {
                XmppConnection.getInstance().getConnection().connect();
                if (!XmppConnection.getInstance().getConnection().isAuthenticated()) {
                    XmppConnection.getInstance().getConnection().login("tester_001", "tester_001");
                    Log.i("fsdafa", "登錄成功");
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.i("fsdafa", "重新登錄");
                mTimer.schedule(new timetask(), logintime);
            }
        }
    }

    @Override
    public void reconnectingIn(int arg0) {
    }

    @Override
    public void reconnectionFailed(Exception arg0) {
    }

    @Override
    public void reconnectionSuccessful() {
    }

}
