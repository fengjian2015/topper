package com.bclould.tea.xmpp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.bclould.tea.topperchat.WsConnection;
import com.bclould.tea.topperchat.WsOfflineConnection;
import com.bclould.tea.utils.UtilTool;

public class IMLogin {
	public static void login(Context context) {
		new LoginThread(context,++WsConnection.loginNumber).start();
	}

	public static boolean loginServer(Context context) {
		if (!isNetworkActivity(context)) {
			UtilTool.Log("fengjian","无网络，login登录失败");
			return false;
		}
		if (WsConnection.getInstance().get(context)==null||!WsConnection.getInstance().get(context).isOpen()) {
			UtilTool.Log("fengjian","WebSocket未连接");
			return false;
		}
		return loginAction();
	}

	public static boolean isNetworkActivity(Context context) {
		ConnectivityManager mConnectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = mConnectivityManager.getActiveNetworkInfo();
		if (netInfo != null) {
			return netInfo.isAvailable();
		}
		return false;
	}

	public static boolean loginAction() {
		try {
			WsConnection.getInstance().login();
			WsOfflineConnection.getInstance().login();
		}catch (Exception e) {
			e.printStackTrace();
		}
		return WsConnection.getInstance().isLogin();
	}
}
