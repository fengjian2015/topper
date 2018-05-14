package com.bclould.tocotalk.xmpp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.annotation.RequiresApi;
import com.bclould.tocotalk.utils.UtilTool;
import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Presence.Mode;
import org.jivesoftware.smackx.ping.PingManager;

@RequiresApi(api = Build.VERSION_CODES.N)
public class IMLogin {
	public final static String LOGIN_SUCCESS = "Y";
	public final static String LOGIN_FAIL = "N";
	private final static int PING_TIME = 180* 1000;

	public static void login(Context context) {
		new LoginThread(context).start();
	}

	public static boolean loginServer(Context context) {
		AbstractXMPPConnection connection = XmppConnection.getInstance().getConnection();
		if (!isNetworkActivity(context)) {
			UtilTool.Log("fengjian","无网络，login登录失败");
			return false;
		}
		if (connection==null||!connection.isConnected()) {
			UtilTool.Log("fengjian","login 还是未连接");
			return false;
		}
		return loginAction(context, connection);
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

	public static boolean loginAction(final Context context, AbstractXMPPConnection connection) {
		try {
			connection.removeConnectionListener(XmppConnection.getInstance().getXMConnectionListener());
			connection.addConnectionListener(XmppConnection.getInstance().getXMConnectionListener());
		}catch (Exception e){
			return false;
		}
		try {
			UtilTool.Log("fengjian","IMLOGIN START");
			String myUser = UtilTool.getJid();
			String user = myUser.substring(0, myUser.indexOf("@"));
			connection.login(user, UtilTool.getpw());
			UtilTool.Log("fengjian","IMLOGIN END");

			// 在线状态
			Presence presence = new Presence(Presence.Type.available);
			Mode mode = Mode.valueOf("available");
			presence.setMode(mode);
			presence.setStatus("在线");
			presence.setPriority(0);
			connection.sendPacket(presence);
			PingManager.getInstanceFor(connection).setPingInterval(PING_TIME);
		} catch (XMPPException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return connection.isAuthenticated();
	}
}
