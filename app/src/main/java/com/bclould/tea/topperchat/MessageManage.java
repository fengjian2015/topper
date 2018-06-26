package com.bclould.tea.topperchat;

import android.os.Build;
import android.support.annotation.RequiresApi;

import com.bclould.tea.crypto.otr.OtrChatListenerManager;
import com.bclould.tea.history.DBManager;
import com.bclould.tea.utils.UtilTool;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.msgpack.jackson.dataformat.MessagePackFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.bclould.tea.topperchat.WsContans.MSG_GROUP;
import static com.bclould.tea.topperchat.WsContans.MSG_SINGLER;
import static com.bclould.tea.topperchat.WsContans.MSG_STEANGER;

/**
 * Created by GIjia on 2018/6/25.
 */
@RequiresApi(api = Build.VERSION_CODES.N)
public class MessageManage {
    private static MessageManage mInstance;
    /** 每次只执行一个任务的线程池 */
    private static ExecutorService mSingleThreadExecutor = null;
    public static MessageManage getInstance(){
        if(mInstance == null){
            synchronized (MessageManage.class){
                if(mInstance == null){
                    mInstance = new MessageManage();
                    mSingleThreadExecutor = Executors.newSingleThreadExecutor();// 每次只执行一个线程任务的线程池
                }
            }
        }
        return mInstance;
    }

    public void sendSingLe(final String to, final byte[] attachment, final String body, final int msgType, final String msgId, final long time, final String roomId, final DBManager mMgr){
        mSingleThreadExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    ObjectMapper objectMapper = new ObjectMapper(new MessagePackFactory());
                    Map<Object, Object> messageMap = new HashMap<>();
                    messageMap.put("body", OtrChatListenerManager.getInstance().sentMessagesChange(body,
                            OtrChatListenerManager.getInstance().sessionID(UtilTool.getTocoId(), String.valueOf(roomId))));
                    messageMap.put("attachment", attachment);

                    Map<Object, Object> contentMap = new HashMap<>();
                    contentMap.put("to", to);
                    contentMap.put("from", UtilTool.getTocoId());
                    if ("true".equals(OtrChatListenerManager.getInstance().getOTRState(roomId.toString()))) {
                        contentMap.put("crypt", true);
                    } else {
                        contentMap.put("crypt", false);
                    }
                    contentMap.put("message", objectMapper.writeValueAsBytes(messageMap));
                    contentMap.put("type", msgType);
                    contentMap.put("id", msgId);
                    contentMap.put("time", time);

                    Map<Object, Object> sendMap = new HashMap<>();
                    if(mMgr.findUser(to)){
                        sendMap.put("type", MSG_SINGLER);
                    }else{
                        sendMap.put("type", MSG_STEANGER);
                    }
                    sendMap.put("content", objectMapper.writeValueAsBytes(contentMap));
                    mMgr.addMessageMsgId(msgId);
                    WsConnection.getInstance().sendMessage(objectMapper.writeValueAsBytes(sendMap));
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    public void sendMulti(String to, final byte[] attachment, final String body, final int msgType, final String msgId, final long time, final String roomId, final DBManager mMgr){
        mSingleThreadExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    ObjectMapper objectMapper = new ObjectMapper(new MessagePackFactory());
                    Map<Object, Object> messageMap = new HashMap<>();
                    messageMap.put("body", OtrChatListenerManager.getInstance().sentMessagesChange(body,
                            OtrChatListenerManager.getInstance().sessionID(UtilTool.getTocoId(), String.valueOf(roomId))));
                    messageMap.put("attachment", attachment);

                    Map<Object, Object> contentMap = new HashMap<>();
                    contentMap.put("group_id", Integer.parseInt(roomId));
                    contentMap.put("toco_id",UtilTool.getTocoId());
                    if ("true".equals(OtrChatListenerManager.getInstance().getOTRState(roomId.toString()))) {
                        contentMap.put("crypt", true);
                    } else {
                        contentMap.put("crypt", false);
                    }
                    contentMap.put("message", objectMapper.writeValueAsBytes(messageMap));
                    contentMap.put("type", msgType);
                    contentMap.put("id",msgId);
                    contentMap.put("time",time);

                    Map<Object, Object> sendMap = new HashMap<>();
                    sendMap.put("type", MSG_GROUP);
                    sendMap.put("content", objectMapper.writeValueAsBytes(contentMap));
                    mMgr.addMessageMsgId(msgId);
                    WsConnection.getInstance().sendMessage(objectMapper.writeValueAsBytes(sendMap));
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }
}
