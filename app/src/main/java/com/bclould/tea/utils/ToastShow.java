package com.bclould.tea.utils;

import android.app.Activity;
import android.view.Gravity;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

public class ToastShow {
	private static Map<String, Long> map = new HashMap<String, Long>();
	private static long MAX_SHOW_TIME = 3 * 1000;

	public static void showToast(final Activity act, final String message, boolean isShowToast) {
		if(act==null)return;
		if(!isShowToast){
			return;
		}
		act.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(act, message, Toast.LENGTH_SHORT).show();
			}
		});
	}

	public static void showToast(final Activity act, final String message) {
		if(act==null)return;
		act.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(act, message, Toast.LENGTH_SHORT).show();
			}
		});
	}

	public static void showToastLength(final Activity act, final String message) {
		if(act==null)return;
		act.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(act, message, Toast.LENGTH_LONG).show();
			}
		});
	}
	public static void showToast2(final Activity act, final String message) {
		if(act==null)return;
		act.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				show(act, message);
			}
		});
	}
	public static void showToastShowCenter(final Activity act, final String message) {
		if(act==null)return;
		act.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				showTime(act, message);
			}
		});
	}
	private static boolean isShow(String message) {
		synchronized (map) {
			if (!map.containsKey(message)) {
				return true;
			}

			long lastShowTime = map.get(message);
			if (System.currentTimeMillis() - lastShowTime > MAX_SHOW_TIME) {
				return true;
			}

			return false;
		}
	}

	private static void show(Activity act, String message) {
		if (!isShow(message)) {
			return;
		}
		synchronized (map) {
			map.put(message, System.currentTimeMillis());
			Toast.makeText(act, message, Toast.LENGTH_SHORT).show();
		}
	}

	private static void showTime(Activity act, String message) {
		if (!isShow(message)) {
			return;
		}
		synchronized (map) {
			map.put(message, System.currentTimeMillis());
			Toast toast = Toast.makeText(act,message, Toast.LENGTH_LONG);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
		}
	}


}
