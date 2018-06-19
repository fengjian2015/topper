package com.bclould.tea.topperchat;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.bclould.tea.utils.Constants;
import com.bclould.tea.utils.UtilTool;
import com.bclould.tea.xmpp.RoomManage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.koushikdutta.async.ByteBufferList;
import com.koushikdutta.async.DataEmitter;
import com.koushikdutta.async.callback.CompletedCallback;
import com.koushikdutta.async.callback.DataCallback;
import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.WebSocket;
import org.msgpack.jackson.dataformat.MessagePackFactory;

import java.util.HashMap;
import java.util.Map;

import static com.bclould.tea.topperchat.WsContans.CONTENT;
import static com.bclould.tea.topperchat.WsContans.DEVICE;
import static com.bclould.tea.topperchat.WsContans.DEVICE_ID;
import static com.bclould.tea.topperchat.WsContans.PASSWORD;
import static com.bclould.tea.topperchat.WsContans.TOCOID;
import static com.bclould.tea.topperchat.WsContans.TYPE;

/**
 * Created by GIjia on 2018/6/11.
 */
@RequiresApi(api = Build.VERSION_CODES.N)
public class WsOfflineConnection {
    private static WsOfflineConnection mInstance;
    private static Context mContext;
    private WebSocket ws;
    private boolean isLogin=false;
    private boolean isConnection=false;
    public static WsOfflineConnection getInstance(){
        if(mInstance == null){
            synchronized (WsOfflineConnection.class){
                if(mInstance == null){
                    mInstance = new WsOfflineConnection();
                }
            }
        }
        return mInstance;
    }

    public synchronized WebSocket get(Context context){
        mContext=context;
        try {
            if((ws==null||!ws.isOpen())&&!isConnection&&!WsConnection.getInstance().getOutConnection()){
                isConnection=true;
                AsyncHttpClient.getDefaultInstance().websocket(Constants.MSG_OFFLINE, "8443", new AsyncHttpClient.WebSocketConnectCallback() {
                    @Override
                    public void onCompleted(Exception ex, WebSocket webSocket) {
                        isConnection=false;
                        if (ex != null) {
                            setIsLogin(false);
                            UtilTool.Log("fengjian","離線连接服務器失敗");
                            ex.printStackTrace();
                            return;
                        }
                        ws=webSocket;
                        if(WsConnection.getInstance().isLogin()){
                            try {
                                login();
                            } catch (JsonProcessingException e) {
                                e.printStackTrace();
                            }
                        }
                        UtilTool.Log("fengjian","離線连接服務器成功");
                        webSocket.setDataCallback(new DataCallback() {
                            public void onDataAvailable(DataEmitter emitter, ByteBufferList byteBufferList) {
                                try {
                                    byte[] bytes=byteBufferList.getAllByteArray();
                                    ObjectMapper objectMapper =  new ObjectMapper(new MessagePackFactory());
                                    Map<Object, Object> deserialized = objectMapper.readValue(bytes, new TypeReference<Map<String, Object>>() {});
                                    Map<Object, Object> content;
                                    UtilTool.Log("fengjian","接受到 離線 消息：type="+deserialized.get("type"));
                                    isLogin=true;
                                    switch ((int)deserialized.get("type")) {
                                        case 15:
                                            content = objectMapper.readValue((byte[]) deserialized.get("content"), new TypeReference<Map<String, Object>>() {});
                                            // TODO: 2018/6/12 這裡是 index   需要發送反饋
                                            sendOfflineBack(content);
                                            savaOffline(content);
                                            break;
                                    }
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                            }
                        });

                        webSocket.setClosedCallback(new CompletedCallback() {
                            @Override
                            public void onCompleted(Exception ex) {
                                UtilTool.Log("fengjian","離線断开连接");
                                setIsLogin(false);
                            }
                        });
                        webSocket.setEndCallback(new CompletedCallback() {
                            @Override
                            public void onCompleted(Exception ex) {
                                UtilTool.Log("fengjian","離線断开连接");
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

    public void setContext(Context context) {
        mContext = context;
    }

    public void setIsLogin(boolean isLogin){
        this.isLogin=isLogin;
    }

    public synchronized void login() throws JsonProcessingException {
        if(isLogin)return;
        if(ws==null||!ws.isOpen()||WsConnection.getInstance().getOutConnection()){
            return;
        }
        UtilTool.Log("fengjian","離線服務登錄中");
        ObjectMapper objectMapper =  new ObjectMapper(new MessagePackFactory());
        Map<Object,Object> contentMap = new HashMap<>();
        contentMap.put(PASSWORD,UtilTool.getToken());
        contentMap.put(TOCOID,UtilTool.getTocoId());
        contentMap.put(DEVICE,DEVICE_ID);
        Map<Object,Object> map = new HashMap<>();
        map.put(CONTENT,objectMapper.writeValueAsBytes(contentMap));
        map.put(TYPE,15);
        ws.send(objectMapper.writeValueAsBytes(map));
    }

    /**
     * 关闭连接
     */
    public void closeConnection() {
        setIsLogin(false);
        if (ws != null) {
            if (ws.isOpen()) {
                ws.close();
                ws.end();
            }
        }
        UtilTool.Log("fengjian", "離線关闭连接");
    }

    private void sendOfflineBack(Map<Object, Object> content) throws Exception {
        ObjectMapper objectMapper =  new ObjectMapper(new MessagePackFactory());
        Map<Object,Object> map=new HashMap<>();
        map.put("index", Long.parseLong(content.get("index")+""));

        Map<Object,Object> sendMap = new HashMap<>();
        sendMap.put("type",25);
        sendMap.put("content",objectMapper.writeValueAsBytes(map));
        ws.send(objectMapper.writeValueAsBytes(sendMap));
    }

    private void savaOffline(Map<Object, Object> content) throws Exception{

        ObjectMapper objectMapper =  new ObjectMapper(new MessagePackFactory());
        Map<Object, Object>  messageMap1 = objectMapper.readValue((byte[]) content.get("content"), new TypeReference<Map<String, Object>>() {});
        Map<Object, Object>  messageMap2 = objectMapper.readValue((byte[]) messageMap1.get("content"), new TypeReference<Map<String, Object>>() {});
        Map<Object, Object>  messageMap3 = objectMapper.readValue((byte[]) messageMap2.get("content"), new TypeReference<Map<String, Object>>() {});
        // TODO: 2018/6/12 這裡是獲取到的消息 type=3的content
        if((int)messageMap3.get("type")==3){
            Map<Object, Object>  messageMap4 = objectMapper.readValue((byte[]) messageMap3.get("content"), new TypeReference<Map<String, Object>>() {});
            com.bclould.tea.topperchat.SocketListener.getInstance(mContext).messageFeedback(messageMap4,false,RoomManage.ROOM_TYPE_SINGLE);
        }
    }

    public void senPing(Context context){
        try {
            if(!WsConnection.getInstance().isLogin())return;
            if(ws==null||!ws.isOpen()){
                get(context);
                return;
            }
            if(!isLogin){login();}
            ObjectMapper objectMapper =  new ObjectMapper(new MessagePackFactory());
            Map<Object,Object> sendMap = new HashMap<>();
            sendMap.put("type",4);
            sendMap.put("content",new byte[]{});
            try {
                ws.send(objectMapper.writeValueAsBytes(sendMap));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
