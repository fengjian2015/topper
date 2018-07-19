package com.bclould.tea.topperchat;


import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;

import com.bclould.tea.Presenter.LoginPresenter;
import com.bclould.tea.R;
import com.bclould.tea.base.MyApp;
import com.bclould.tea.history.DBManager;
import com.bclould.tea.model.MessageInfo;
import com.bclould.tea.service.IMCoreService;
import com.bclould.tea.service.IMService;
import com.bclould.tea.ui.activity.MainActivity;
import com.bclould.tea.utils.Constants;
import com.bclould.tea.utils.MessageEvent;
import com.bclould.tea.utils.MySharedPreferences;
import com.bclould.tea.utils.StringUtils;
import com.bclould.tea.utils.UtilTool;
import com.bclould.tea.xmpp.ConnectStateChangeListenerManager;
import com.bclould.tea.xmpp.LoginThread;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.koushikdutta.async.ByteBufferList;
import com.koushikdutta.async.DataEmitter;
import com.koushikdutta.async.callback.CompletedCallback;
import com.koushikdutta.async.callback.DataCallback;
import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.WebSocket;

import org.greenrobot.eventbus.EventBus;
import org.msgpack.jackson.dataformat.MessagePackFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.bclould.tea.Presenter.LoginPresenter.TOKEN;
import static com.bclould.tea.topperchat.WsContans.CONTENT;
import static com.bclould.tea.topperchat.WsContans.DEVICE;
import static com.bclould.tea.topperchat.WsContans.DEVICE_ID;
import static com.bclould.tea.topperchat.WsContans.PASSWORD;
import static com.bclould.tea.topperchat.WsContans.TOCOID;
import static com.bclould.tea.topperchat.WsContans.TYPE;

/**
 * Created by fengjian on 2018/6/5.
 */
@RequiresApi(api = Build.VERSION_CODES.N)
public class WsConnection {
    private static WsConnection mInstance;
    private static Context mContext;
    public WebSocket ws;
    private boolean isLogin=false;
    private boolean isConnection=false;
    private boolean isOutConnection=false;//是否已經退出登錄，到登錄界面
    private boolean isLoginConnection=false;//是否登錄鏈接中，避免重複登錄導致一直斷開
    private static Object lock = new Object();
    private DBManager mManager;
    private ArrayList<WebSocket> mWebSocketArrayList=new ArrayList<>();
    private Handler mHandler=new Handler();
    public static WsConnection getInstance(){
        if(mInstance == null){
            synchronized (WsConnection.class){
                if(mInstance == null){
                    mInstance = new WsConnection();
                }
            }
        }
        return mInstance;
    }

    private WsConnection() {}

    public synchronized WebSocket get(Context context){
        synchronized (lock) {
            mContext = context;
            try {
                if ((ws == null || !ws.isOpen()) && !isConnection && !isOutConnection) {
                    setIsConnection(true);
                    if(ws!=null){
                        UtilTool.Log("fengjian","進入打開websocket-------------"+ws.isOpen());
                    }
                    UtilTool.Log("fengjian","進入打開websocket-------------"+mWebSocketArrayList.size());
                    AsyncHttpClient.getDefaultInstance().getSocketMiddleware().setIdleTimeoutMs(20000);
                    AsyncHttpClient.getDefaultInstance().websocket(Constants.DOMAINNAME3, "2087", new AsyncHttpClient.WebSocketConnectCallback() {
                        @Override
                        public void onCompleted(Exception ex, final com.koushikdutta.async.http.WebSocket webSocket) {
                            if (ex != null) {
                                setIsLogin(false);
                                setLoginConnection(false);
                                setIsConnection(false);
                                UtilTool.Log("fengjian", "连接服務器失敗" + ex.getMessage());
                                ex.printStackTrace();
                                return;
                            }
                            if(mWebSocketArrayList.size()>0){
                                ws=mWebSocketArrayList.get(0);
                                closeConnection();
                                mWebSocketArrayList.clear();
                            }
                            ws = webSocket;
                            mWebSocketArrayList.add(ws);

                            UtilTool.Log("fengjian", "连接服務器成功-----"+mWebSocketArrayList.size());
                            try {
                                login();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            webSocket.setDataCallback(new DataCallback() {
                                public void onDataAvailable(DataEmitter emitter, ByteBufferList byteBufferList) {
                                    byte[] bytes = byteBufferList.getAllByteArray();
                                    SocketListener.getInstance(mContext).onBinaryMessage(bytes);
                                }
                            });

                            webSocket.setClosedCallback(new CompletedCallback() {
                                @Override
                                public void onCompleted(Exception ex) {
                                    UtilTool.Log("fengjian", "断开连接");
                                    setIsConnection(false);
                                    mWebSocketArrayList.remove(webSocket);
                                    setIsLogin(false);
                                    setLoginConnection(false);
                                }
                            });
                            webSocket.setEndCallback(new CompletedCallback() {
                                @Override
                                public void onCompleted(Exception ex) {
                                    UtilTool.Log("fengjian", "断开连接");
                                    setIsConnection(false);
                                    mWebSocketArrayList.remove(webSocket);
                                    setIsLogin(false);
                                    setLoginConnection(false);
                                }
                            });
                            webSocket.setPongCallback(new WebSocket.PongCallback() {
                                @Override
                                public void onPongReceived(String s) {
                                MySharedPreferences.getInstance().setInteger("ping", 1);
                                    UtilTool.Log("fengjian", "pong回調：" + s);
                                }
                            });
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return ws;
        }
    }

    public synchronized void login() throws Exception {
        if(StringUtils.isEmpty(UtilTool.getTocoId())){
            goMainActivity();
        }
        if(isLogin||isOutConnection||isLoginConnection){
            UtilTool.Log("fengjian", "暫時不讓登錄：isLogin："+isLogin +"    isOutConnection："+isOutConnection+"    isLoginConnection："+isLoginConnection);
            if(isOutConnection){
                stopAllIMCoreService(mContext);
            }
            return;
        }
        changeMsgState();
        setLoginConnection(true);
        Thread.sleep(2000);
        UtilTool.Log("fengjian","發送登錄消息：TOCOID:"+UtilTool.getTocoId());
        ObjectMapper objectMapper =  new ObjectMapper(new  MessagePackFactory());
        Map<Object,Object> contentMap = new HashMap<>();
        contentMap.put(PASSWORD,UtilTool.getToken());
        contentMap.put(TOCOID,UtilTool.getTocoId());
        contentMap.put(DEVICE,DEVICE_ID);

        Map<Object,Object> map = new HashMap<>();
        map.put(CONTENT,objectMapper.writeValueAsBytes(contentMap));
        map.put(TYPE,1);
        ws.send(objectMapper.writeValueAsBytes(map));
//        mHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                setLoginConnection(false);
//                if(!isLogin){
//                    closeConnection();
//                }
//            }
//        },60*1000);
    }

    public void senPing(){
        try {
            ws.ping("Android");
            ObjectMapper objectMapper =  new ObjectMapper(new MessagePackFactory());
            Map<Object,Object> sendMap = new HashMap<>();
            sendMap.put("type",4);
            sendMap.put("content",new byte[]{});
            try {
                send(objectMapper.writeValueAsBytes(sendMap));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void senLogout(){
        try {
            UtilTool.Log("fengjian","主動發送登出消息33");
            ObjectMapper objectMapper =  new ObjectMapper(new MessagePackFactory());
            Map<Object,Object> sendMap = new HashMap<>();
            sendMap.put("type",33);
            sendMap.put("content",new byte[]{});
            send(objectMapper.writeValueAsBytes(sendMap));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public synchronized void send(byte[] bytes) throws Exception{
        if(ws==null||!ws.isOpen()||!isLogin){
            ConnectStateChangeListenerManager.get().notifyListener(ConnectStateChangeListenerManager.CONNECTING);
            get(mContext);
            throw new NullPointerException();
        }
        ws.send(bytes);
    }

    public synchronized void sendMessage(byte[] bytes) throws Exception{
        if(ws==null||!ws.isOpen()||!isLogin){
            throw new NullPointerException();
        }
        UtilTool.Log("fengjian","發送消息");
        ws.send(bytes);
    }

    private void setIsConnection(boolean isConnection){
        this.isConnection=isConnection;
    }

    public void setLoginConnection(boolean isLoginConnection){
        this.isLoginConnection=isLoginConnection;
    }

    public boolean getLoginConnection(){
        return isLoginConnection;
    }

    public boolean isLogin(){
        return isLogin;
    }

    public void setIsLogin(boolean isLogin){
       this.isLogin=isLogin;
    }

    public void setOutConnection(boolean isOutConnection){
        this.isOutConnection=isOutConnection;
    }
    public boolean getOutConnection(){
        return isOutConnection;
    }

    public void setContext(Context context) {
        mContext = context;
    }


    public void changeMsgState() {
        if(mManager==null){
            mManager=new DBManager(mContext);
        }
        List<MessageInfo> list = mManager.queryAllMsgId();
        if (list != null && list.size() > 0) {
            for (MessageInfo messageInfo : list) {
                mManager.updateMessageStatus(messageInfo.getMsgId(), 2);
                if(!StringUtils.isEmpty(messageInfo.getRoomId())){
                    mManager.deleteSingleMessageMsgId(messageInfo.getMsgId());
                }
            }
            EventBus.getDefault().post(new MessageEvent(mContext.getString(R.string.msg_database_update)));
        }
        mManager.deleteAllMsgId();
    }

    public void changeMsgStateOvertime(){
        if(mManager==null){
            mManager=new DBManager(mContext);
        }
        List<String> list = mManager.queryAllOvertimeMsgId(System.currentTimeMillis()-(60*1000));
        if (list != null && list.size() > 0) {
            for (String string : list) {
                mManager.updateMessageStatus(string, 2);
            }
            EventBus.getDefault().post(new MessageEvent(mContext.getString(R.string.msg_database_update)));
        }
    }

    /**
     * 关闭连接
     */
    public void closeConnection(){
        senLogout();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        LoginThread.isStartExReconnect = false;
        if (ws != null) {
            // 移除连接监听
            ws.close();
            ws.end();
            ws=null;
        }
        setIsConnection(false);
        setIsLogin(false);
    }

    public void goMainActivity(){
        logoutService(mContext);
        MySharedPreferences.getInstance().setString(TOKEN, "");
        MySharedPreferences.getInstance().setString(LoginPresenter.TOCOID, "");
        MyApp.getInstance().mCoinList.clear();
        MyApp.getInstance().mPayCoinList.clear();
        MyApp.getInstance().mOtcCoinList.clear();
        MyApp.getInstance().mBetCoinList.clear();
        Intent intent = new Intent(mContext, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("whence", 2);
        mContext.startActivity(intent);
    }

    //退出登錄用
    public void logoutService(Context context) {
        closeConnection();
        setOutConnection(true);
        stopAllIMCoreService(context);
        context.stopService(new Intent(context, IMService.class));
        LoginThread.isStartExReconnect = false;
        WsOfflineConnection.getInstance().closeConnection();
        UtilTool.Log("fengjian", "关闭连接");
        XGManage.getInstance().deleteAlias();
    }

    /***
     * 通过广播去关闭service
     */
    public static void stopAllIMCoreService(Context context){
        Intent intent = new Intent();
        intent.setAction(IMCoreService.ACTION_LOGOUT);
        context.sendBroadcast(intent);
    }

    /**
     * 判断某个服务是否正在运行的方法
     *
     * @param mContext
     * @param serviceName
     *            是包名+服务的类名（例如：net.loonggg.testbackstage.TestService）
     * @return true代表正在运行，false代表服务没有正在运行
     */
    public static boolean isServiceWork(Context mContext, String serviceName) {
        boolean isWork = false;
        if(mContext==null)return isWork;
        try {
            ActivityManager myAM = (ActivityManager) mContext
                    .getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningServiceInfo> myList = myAM.getRunningServices(100);
            if (myList == null) {
                return false;
            }
            if (myList.size() <= 0) {
                return false;
            }
            for (int i = 0; i < myList.size(); i++) {
                if(myList.get(i).service==null)continue;
                String mName = myList.get(i).service.getClassName().toString();
                if (mName!=null&&mName.equals(serviceName)) {
                    isWork = true;
                    break;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return isWork;
    }

    public static void loginService(Context context) {
        Intent intent = new Intent();
        intent.setAction(IMCoreService.ACTION_LOGIN);
        context.sendBroadcast(intent);
    }
    public void close(){
        if(ws!=null){
            ws.close();
            ws.end();
        }
    }
}
