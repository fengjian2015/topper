package com.bclould.tea.xmpp;

import org.jivesoftware.smack.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ConnectStateChangeListenerManager {
	public final static int RECEIVING_TIME = 10 * 1000;//收取中最多持续的时间

	public final static int DISCONNECT = 0;// 未连接
	public final static int CONNECTING = 1;// 连接中
	public final static int CONNECTED = 2;// 已连接
	public final static int CONFLICT = 3;// 登录冲突
	public final static int RECEIVING = 4;//收取中

	private int currentState = DISCONNECT;// 当前连接IM服务器状态

	private static ConnectStateChangeListenerManager manager;

	private List<IConnectStateChangeListener> listeners;// 设置状态改变的监听

	private Timer timer;
	private static Object lock = new Object();

	private ConnectStateChangeListenerManager() {
		listeners = new ArrayList<IConnectStateChangeListener>();
	}

	public static ConnectStateChangeListenerManager get() {
		if (manager == null) {
			synchronized (ConnectStateChangeListenerManager.class) {
				if (manager == null) {
					manager = new ConnectStateChangeListenerManager();
				}
			}
		}
		return manager;
	}

	public void changeToCennectedAtTime(int time){
		synchronized (manager) {
			if (time <= 0) {
				time = RECEIVING_TIME;
			}
			if (timer != null) {//防止产生多个timer
				timer.cancel();
				timer = null;
			}
			timer = new Timer();
			timer.schedule(new TimerTask() {
				public void run() {
					if (currentState == RECEIVING) {
//						MyLogger.xuxLog().i("---join完房间后10s，并没有收到1005，自动改为已连接状态");
						notifyListener(CONNECTED);
						timer = null;
					}
				}
			}, time);
		}
	}

	public void cancelTimer(){
		if(timer != null) {
			timer.cancel();
		}
	}

	public void registerStateChangeListener(IConnectStateChangeListener listener) {
		synchronized (listeners) {
			if(!listeners.contains(listener)) {
				listeners.add(listener);
			}
		}
	}

	public void unregisterStateChangeListener(
			IConnectStateChangeListener listener) {
		if(listener==null)return;
			synchronized (listeners) {
				if(listeners.contains(listener)) {
					listeners.remove(listener);
				}
		}
	}

	public void unregisterStateChangeListener() {
		synchronized (listeners) {
			listeners.clear();
		}
	}

	public void notifyListener(int state) {
		synchronized (lock) {
			if(StringUtils.isEmpty(state+""))return;
			currentState = state;
			if (state == ConnectStateChangeListenerManager.RECEIVING) {
				ConnectStateChangeListenerManager.get().changeToCennectedAtTime(-1);
			}
			for (IConnectStateChangeListener lis : listeners) {
				lis.onStateChange(state);
			}
		}
	}

	public int getCurrentState() {
		return currentState;
	}

	public void setCurrentState(int currentState){
		this.currentState=currentState;
	}

	public void clear() {
		synchronized (lock) {
			unregisterStateChangeListener();
			currentState = DISCONNECT;
			listeners = null;
			manager = null;
		}
	}

}
