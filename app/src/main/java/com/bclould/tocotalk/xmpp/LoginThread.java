package com.bclould.tocotalk.xmpp;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.bclould.tocotalk.service.IMService;
import com.bclould.tocotalk.utils.UtilTool;

@RequiresApi(api = Build.VERSION_CODES.N)
public class LoginThread extends Thread {
	private Context context;
	public static boolean isStartExReconnect = false;

	public LoginThread(Context context) {
		this.context = context;
	}

	@Override
	public void run() {
		while (true) {
			if (IMLogin.loginServer(context)) {
				UtilTool.Log("fengjian","login------登陆成功");
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
				UtilTool.Log("fengjian","login------登陆失败");
				try {
					sleep(5 * 1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
