package com.bclould.tocotalk.xmpp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.bclould.tocotalk.topperchat.WsConnection;
import com.bclould.tocotalk.utils.UtilTool;

@RequiresApi(api = Build.VERSION_CODES.N)
public class IMLogin {
	public static void login(Context context) {
		new LoginThread(context).start();
	}

	public static boolean loginServer(Context context) {
		if (!isNetworkActivity(context)) {
			UtilTool.Log("fengjian","无网络，login登录失败");
			return false;
		}
		if (WsConnection.getInstance().get(context)==null||!WsConnection.getInstance().get(context).isOpen()) {
			UtilTool.Log("fengjian","login 还是未连接");
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
			UtilTool.Log("fengjian", "IMLOGIN START");
			WsConnection.getInstance().login();
			UtilTool.Log("fengjian", "IMLOGIN END");
		}catch (Exception e) {
			e.printStackTrace();
		}
		return WsConnection.getInstance().isLogin();
	}
}
