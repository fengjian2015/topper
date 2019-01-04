package com.bclould.tea.xmpp;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.bclould.tea.service.IMService;
import com.bclould.tea.topperchat.WsConnection;
import com.bclould.tea.utils.UtilTool;

public class LoginThread extends Thread {
	private Context context;
	public static boolean isStartExReconnect = false;
	private int loginNumber;
	public LoginThread(Context context,int loginNumber) {
		this.context = context;
		this.loginNumber=loginNumber;
	}

	@Override
	public void run() {
		while (true&&loginNumber==WsConnection.loginNumber) {
			if (WsConnection.getInstance().getOutConnection()) {
				UtilTool.Log("fengjian","關閉連接");
				WsConnection.getInstance().goMainActivity(2);
				break;
			}
			if (IMLogin.loginServer(context)) {
				ConnectStateChangeListenerManager.get().notifyListener(
						ConnectStateChangeListenerManager.RECEIVING);
				if (!isStartExReconnect) {
					isStartExReconnect = true;
					((IMService) this.context).exReconnect(1000);
				}
				break;
			} else {
				if (!IMLogin.isNetworkActivity(context)) {
					ConnectStateChangeListenerManager.get().notifyListener(
							ConnectStateChangeListenerManager.DISCONNECT);
				} else {
					ConnectStateChangeListenerManager.get().notifyListener(
							ConnectStateChangeListenerManager.CONNECTING);
				}
				UtilTool.Log("fengjian","login------未登陆上");
				try {
					sleep(5 * 1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
