package com.bclould.tocotalk.topperchat;


import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import com.bclould.tocotalk.utils.Constants;
import com.bclould.tocotalk.utils.UtilTool;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketFactory;
import org.msgpack.jackson.dataformat.MessagePackFactory;
import java.util.HashMap;
import java.util.Map;


import static com.bclould.tocotalk.topperchat.WsContans.CONTENT;
import static com.bclould.tocotalk.topperchat.WsContans.PASSWORD;
import static com.bclould.tocotalk.topperchat.WsContans.TOCOID;
import static com.bclould.tocotalk.topperchat.WsContans.TYPE;

/**
 * Created by GIjia on 2018/6/5.
 */
@RequiresApi(api = Build.VERSION_CODES.N)
public class WsConnection {
    private static final int FRAME_QUEUE_SIZE = 5;
    private static final int CONNECT_TIMEOUT = 5000;
    private static WsConnection mInstance;
    private static Context mContext;
    private WebSocket ws;
    private boolean isLogin=false;
    private SocketListener mSocketListener;
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
            if(ws==null||!ws.isOpen()){
                if(mSocketListener!=null)ws.removeListener(mSocketListener);
                ws = new WebSocketFactory().createSocket(Constants.DOMAINNAME3, CONNECT_TIMEOUT)
                        .setFrameQueueSize(FRAME_QUEUE_SIZE)//设置帧队列最大值为5
                        .setMissingCloseFrameAllowed(false)//设置不允许服务端关闭连接却未发送关闭帧
                        .addListener(mSocketListener=new SocketListener(mContext))//添加回调监听
                        .connectAsynchronously();//异步连接
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return ws;
    }

    public synchronized void login() throws JsonProcessingException {
        if(isLogin)return;
        UtilTool.Log("fengjian","TOCOID:"+UtilTool.getTocoId());
        ObjectMapper objectMapper =  new ObjectMapper(new  MessagePackFactory());
        Map<Object,Object> contentMap = new HashMap<>();
        contentMap.put(PASSWORD,UtilTool.getToken());
        contentMap.put(TOCOID,UtilTool.getTocoId());
        Map<Object,Object> map = new HashMap<>();
        map.put(CONTENT,objectMapper.writeValueAsBytes(contentMap));
        map.put(TYPE,1);
        ws.sendBinary(objectMapper.writeValueAsBytes(map));
    }

    public boolean isLogin(){
        return isLogin;
    }

    public void setIsLogin(boolean isLogin){
       this.isLogin=isLogin;
    }

}
