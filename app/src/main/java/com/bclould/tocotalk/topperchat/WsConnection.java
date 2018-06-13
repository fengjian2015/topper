package com.bclould.tocotalk.topperchat;


import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.bclould.tocotalk.service.IMCoreService;
import com.bclould.tocotalk.service.IMService;
import com.bclould.tocotalk.utils.Constants;
import com.bclould.tocotalk.utils.UtilTool;
import com.bclould.tocotalk.xmpp.LoginThread;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.koushikdutta.async.ByteBufferList;
import com.koushikdutta.async.DataEmitter;
import com.koushikdutta.async.callback.CompletedCallback;
import com.koushikdutta.async.callback.DataCallback;
import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.WebSocket;

import org.msgpack.jackson.dataformat.MessagePackFactory;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static com.bclould.tocotalk.topperchat.WsContans.CONTENT;
import static com.bclould.tocotalk.topperchat.WsContans.PASSWORD;
import static com.bclould.tocotalk.topperchat.WsContans.TOCOID;
import static com.bclould.tocotalk.topperchat.WsContans.TYPE;

/**
 * Created by fengjian on 2018/6/5.
 */
@RequiresApi(api = Build.VERSION_CODES.N)
public class WsConnection {
    private static WsConnection mInstance;
    private static Context mContext;
    private WebSocket ws;
    private boolean isLogin=false;
    private boolean isConnection=false;
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
        mContext=context;
        try {
            if((ws==null||!ws.isOpen())&&!isConnection){
                isConnection=true;
                AsyncHttpClient.getDefaultInstance().websocket(Constants.DOMAINNAME3, "2087", new AsyncHttpClient.WebSocketConnectCallback() {
                    @Override
                    public void onCompleted(Exception ex, com.koushikdutta.async.http.WebSocket webSocket) {
                        isConnection=false;
                        if (ex != null) {
                            setIsLogin(false);
                            UtilTool.Log("fengjian","连接服務器失敗"+ex.getMessage());
                            ex.printStackTrace();
                            return;
                        }
                        ws=webSocket;
                        UtilTool.Log("fengjian","连接服務器成功");
                        webSocket.setDataCallback(new DataCallback() {
                            public void onDataAvailable(DataEmitter emitter, ByteBufferList byteBufferList) {
                                byte[] bytes=byteBufferList.getAllByteArray();
                                SocketListener.getInstance(mContext).onBinaryMessage(bytes);
                            }
                        });

                        webSocket.setClosedCallback(new CompletedCallback() {
                            @Override
                            public void onCompleted(Exception ex) {
                                UtilTool.Log("fengjian","断开连接");
                                setIsLogin(false);
                            }
                        });
                        webSocket.setEndCallback(new CompletedCallback() {
                            @Override
                            public void onCompleted(Exception ex) {
                                UtilTool.Log("fengjian","断开连接");
                                setIsLogin(false);
                            }
                        });
                    }
                });
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return ws;
    }

    public synchronized void login() throws Exception {
        if(isLogin)return;
        UtilTool.Log("fengjian","TOCOID:"+UtilTool.getTocoId());
        ObjectMapper objectMapper =  new ObjectMapper(new  MessagePackFactory());
        Map<Object,Object> contentMap = new HashMap<>();
        contentMap.put(PASSWORD,UtilTool.getToken());
        contentMap.put(TOCOID,UtilTool.getTocoId());
        Map<Object,Object> map = new HashMap<>();
        map.put(CONTENT,objectMapper.writeValueAsBytes(contentMap));
        map.put(TYPE,1);
        ws.send(objectMapper.writeValueAsBytes(map));
    }

    public synchronized void sendMessage(byte[] bytes) throws Exception{
        if(ws==null||!ws.isOpen()||!isLogin){
            get(mContext);
            throw new NullPointerException();
        }
        ws.send(bytes);
    }

    public boolean isLogin(){
        return isLogin;
    }

    public void setIsLogin(boolean isLogin){
       this.isLogin=isLogin;
    }

    public void setContext(Context context) {
        mContext = context;
    }

    public void senPing(){
        try {
            ObjectMapper objectMapper =  new ObjectMapper(new MessagePackFactory());
            Map<Object,Object> sendMap = new HashMap<>();
            sendMap.put("type",4);
            sendMap.put("content",new byte[]{});
            try {
                sendMessage(objectMapper.writeValueAsBytes(sendMap));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    /**
     * 关闭连接
     */
    public void closeConnection() {
        setIsLogin(false);
        LoginThread.isStartExReconnect = false;
        if (ws != null) {
            // 移除连接监听
            if (ws.isOpen())
                ws.close();
        }
        Log.i("fengjian", "关闭连接");
    }

    //退出登錄用
    public void logoutService(Context context) {
        setIsLogin(false);
        closeConnection();
        stopAllIMCoreService(context);
        context.stopService(new Intent(context, IMService.class));
        LoginThread.isStartExReconnect = false;
        UtilTool.Log("fengjian","退出");
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
}
