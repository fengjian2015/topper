package com.bclould.tocotalk.topperchat;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.NotificationCompat;
import android.util.Base64;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.bclould.tocotalk.Presenter.OfflineChatPresenter;
import com.bclould.tocotalk.R;
import com.bclould.tocotalk.crypto.otr.OtrChatListenerManager;
import com.bclould.tocotalk.history.DBManager;
import com.bclould.tocotalk.history.DBRoomManage;
import com.bclould.tocotalk.history.DBRoomMember;
import com.bclould.tocotalk.model.ConversationInfo;
import com.bclould.tocotalk.model.MessageInfo;
import com.bclould.tocotalk.ui.activity.ConversationActivity;
import com.bclould.tocotalk.ui.activity.OrderCloseActivity;
import com.bclould.tocotalk.ui.activity.PayDetailsActivity;
import com.bclould.tocotalk.utils.Constants;
import com.bclould.tocotalk.utils.MessageEvent;
import com.bclould.tocotalk.utils.MySharedPreferences;
import com.bclould.tocotalk.utils.StringUtils;
import com.bclould.tocotalk.utils.UtilTool;
import com.bclould.tocotalk.xmpp.ConnectStateChangeListenerManager;
import com.bclould.tocotalk.xmpp.RoomManage;
import com.bclould.tocotalk.xmpp.XmppConnection;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFrame;

import org.greenrobot.eventbus.EventBus;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.roster.Roster;
import org.jxmpp.jid.impl.JidCreate;
import org.msgpack.jackson.dataformat.MessagePackFactory;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static android.content.Context.NOTIFICATION_SERVICE;
import static com.bclould.tocotalk.Presenter.LoginPresenter.CURRENCY;
import static com.bclould.tocotalk.Presenter.LoginPresenter.STATE;
import static com.bclould.tocotalk.topperchat.WsContans.BC_AUTH_STATUS;
import static com.bclould.tocotalk.topperchat.WsContans.BC_COIN_IN_BROAD;
import static com.bclould.tocotalk.topperchat.WsContans.BC_FRIEND_COMMIT;
import static com.bclould.tocotalk.topperchat.WsContans.BC_FRIEND_REJECT;
import static com.bclould.tocotalk.topperchat.WsContans.BC_FRIEND_REQUEST;
import static com.bclould.tocotalk.topperchat.WsContans.BC_INOUT_COIN_INFORM;
import static com.bclould.tocotalk.topperchat.WsContans.BC_OFFLINE;
import static com.bclould.tocotalk.topperchat.WsContans.BC_OTC_ORDER;
import static com.bclould.tocotalk.topperchat.WsContans.BC_QRCODE_RECEIPT_PAYMENT;
import static com.bclould.tocotalk.topperchat.WsContans.BC_RED_PACKET_EXPIRED;
import static com.bclould.tocotalk.topperchat.WsContans.BC_TRANSFER_INFORM;
import static com.bclould.tocotalk.ui.activity.ConversationActivity.ACCESSKEYID;
import static com.bclould.tocotalk.ui.activity.ConversationActivity.SECRETACCESSKEY;
import static com.bclould.tocotalk.ui.activity.ConversationActivity.SESSIONTOKEN;
import static com.bclould.tocotalk.ui.activity.SystemSetActivity.INFORM;
import static com.bclould.tocotalk.ui.adapter.ChatAdapter.ADMINISTRATOR_AUTH_STATUS_MSG;
import static com.bclould.tocotalk.ui.adapter.ChatAdapter.ADMINISTRATOR_IN_OUT_COIN_MSG;
import static com.bclould.tocotalk.ui.adapter.ChatAdapter.ADMINISTRATOR_OTC_ORDER_MSG;
import static com.bclould.tocotalk.ui.adapter.ChatAdapter.ADMINISTRATOR_RECEIPT_PAY_MSG;
import static com.bclould.tocotalk.ui.adapter.ChatAdapter.ADMINISTRATOR_RED_PACKET_EXPIRED_MSG;
import static com.bclould.tocotalk.ui.adapter.ChatAdapter.ADMINISTRATOR_TRANSFER_MSG;
import static com.bclould.tocotalk.ui.adapter.ChatAdapter.FROM_CARD_MSG;
import static com.bclould.tocotalk.ui.adapter.ChatAdapter.FROM_GUESS_MSG;
import static com.bclould.tocotalk.ui.adapter.ChatAdapter.FROM_IMG_MSG;
import static com.bclould.tocotalk.ui.adapter.ChatAdapter.FROM_LINK_MSG;
import static com.bclould.tocotalk.ui.adapter.ChatAdapter.FROM_LOCATION_MSG;
import static com.bclould.tocotalk.ui.adapter.ChatAdapter.FROM_RED_MSG;
import static com.bclould.tocotalk.ui.adapter.ChatAdapter.FROM_TEXT_MSG;
import static com.bclould.tocotalk.ui.adapter.ChatAdapter.FROM_TRANSFER_MSG;
import static com.bclould.tocotalk.ui.adapter.ChatAdapter.FROM_VIDEO_MSG;
import static com.bclould.tocotalk.ui.adapter.ChatAdapter.FROM_VOICE_MSG;
import static com.bclould.tocotalk.ui.fragment.FriendListFragment.NEWFRIEND;
import static com.bclould.tocotalk.utils.MySharedPreferences.SETTING;

/**
 * Created by GIjia on 2018/6/5.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class SocketListener extends WebSocketAdapter{
    private ObjectMapper  objectMapper =  new ObjectMapper(new  MessagePackFactory());
    private Context context;
    private NotificationManager mNotificationManager;
    private PendingIntent mResultIntent;
    private NotificationCompat.Builder mBuilder;
    private DBManager mgr;
    private DBRoomManage mdbRoomManage;
    private DBRoomMember mdbRoomMember;
    private int IMSequence = 1000;
    ExecutorService executorService;//以後用於群聊功能
    public SocketListener(Context context){
        this.context=context;
        executorService = Executors.newFixedThreadPool(5);
        mgr = new DBManager(context);
        mdbRoomManage=new DBRoomManage(context);
        mdbRoomMember=new DBRoomMember(context);
        mNotificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(context);
    }

    @Override
    public void onBinaryMessage(WebSocket websocket, byte[] binary) throws Exception {
        ObjectMapper objectMapper =  new ObjectMapper(new MessagePackFactory());
        Map<Object, Object> deserialized = objectMapper.readValue(binary, new TypeReference<Map<String, Object>>() {});
        UtilTool.Log("fengjian","接受到消息：type="+deserialized.get("type"));
        switch ((int)deserialized.get("type")){
            case 16:
                //登錄反饋
                LoginFeedback(binary);
                break;
            case 3:
                //消息
                Map<Object, Object> content = objectMapper.readValue((byte[]) deserialized.get("content"), new TypeReference<Map<String, Object>>() {});
                Log.i("fengjian","聊天消息message：to："+content.get("to")+"   from:"+content.get("from")+"   crypt:"+content.get("crypt")+"   message："+content.get("message")+"   type："+content.get("type"));
                messageFeedback(content,true);
                break;
            case 18:
                //廣播消息
                friendRequest((byte[]) deserialized.get("content"));
                break;
        }

    }

    @Override
    public void onTextMessage(WebSocket websocket, String text) throws Exception {
        super.onTextMessage(websocket, text);
        UtilTool.Log("fengjian","text");
    }

    @Override
    public void onConnected(WebSocket websocket, Map<String, List<String>> headers)
            throws Exception {
        super.onConnected(websocket, headers);
        UtilTool.Log("fengjian","连接服務器成功");
    }
    @Override
    public void onConnectError(WebSocket websocket, WebSocketException exception)
            throws Exception {
        super.onConnectError(websocket, exception);
        WsConnection.getInstance().setIsLogin(false);
        UtilTool.Log("fengjian","连接错误"+exception.getError().toString());
    }

    @Override
    public void onDisconnected(WebSocket websocket, WebSocketFrame serverCloseFrame, WebSocketFrame clientCloseFrame, boolean closedByServer)
            throws Exception {
        super.onDisconnected(websocket, serverCloseFrame, clientCloseFrame, closedByServer);
        WsConnection.getInstance().setIsLogin(false);
        UtilTool.Log("fengjian","断开连接");
    }

    /**
     * 登錄反饋
     * @param binary
     */
    private void LoginFeedback(byte[] binary) throws IOException {
//        Map<Object, Object> deserialized = objectMapper.readValue(binary, new TypeReference<Map<String, Object>>() {});
//        Map<Object, Object> content = objectMapper.readValue((byte[]) deserialized.get("content"), new TypeReference<Map<String, Object>>() {});
        WsConnection.getInstance().setIsLogin(true);
        UtilTool.Log("fengjian","登錄成功");
        ConnectStateChangeListenerManager.get().notifyListener(ConnectStateChangeListenerManager.RECEIVING);
        new OfflineChatPresenter(context).getOfflineChat();
    }

    /**
     * 處理鈴聲
     * @return false表示攔截消息
     * @param from
     * @param message
     * @param crypt
     */
    private String bellJudgment(String from, String chatmesssage, boolean crypt,boolean isPlayHint){
        //鈴聲必須放在處理消息類前面
        SharedPreferences sp = context.getSharedPreferences(SETTING, 0);
        boolean free= MySharedPreferences.getInstance().getBoolean(SETTING+from+UtilTool.getTocoId());
        if(crypt){
            if(OtrChatListenerManager.getInstance().isOtrEstablishMessage(chatmesssage,
                    OtrChatListenerManager.getInstance().sessionID(UtilTool.getTocoId(), from),context)){
                return null;
            }
            if(OtrChatListenerManager.getInstance().isExist(OtrChatListenerManager.getInstance().sessionID(UtilTool.getTocoId(), from))){
                chatmesssage=OtrChatListenerManager.getInstance().receivedMessagesChange(chatmesssage,
                        OtrChatListenerManager.getInstance().sessionID(UtilTool.getTocoId(), from));
            }
        }
        if(!isPlayHint){
            if(free){

            }else if (sp.contains(INFORM)) {
                if (MySharedPreferences.getInstance().getBoolean(INFORM)) {
                    UtilTool.playHint(context);
                }
            } else {
                UtilTool.playHint(context);
            }
        }
        return chatmesssage;
    }


    /**
     * 消息反饋
     * @param binary
     */
    private void messageFeedback(Map<Object,Object> content,boolean isPlayHint) {
        try {
            String roomType="";
            Map<Object, Object>  messageMap = objectMapper.readValue((byte[]) content.get("message"), new TypeReference<Map<String, Object>>() {});
            //處理消息鈴聲
            String from= (String) content.get("from");
            String message= (String) messageMap.get("body");
            boolean crypt= (boolean) content.get("crypt");
            String otr=bellJudgment(from,message,crypt,isPlayHint);
            if(StringUtils.isEmpty(otr))return;
            //默認文本類型
            MessageInfo messageInfo=new MessageInfo();
            if(crypt){
                if(context.getString(R.string.otr_error).equals(otr)){
                    messageInfo.setMessage(otr);
                    content.put("type",WsContans.MSG_TEXT);
                }else {
                    messageInfo = JSONObject.parseObject(otr, MessageInfo.class);
                }
            }else{
                if(!StringUtils.isEmpty(message)){
                    messageInfo = JSONObject.parseObject(message, MessageInfo.class);
                }
            }

            int msgType = FROM_TEXT_MSG;

            String time =UtilTool.createChatTime();
            // TODO: 2018/6/6 sendFrom 群聊需要修改
            String sendFrom=from;
            // TODO: 2018/6/6 friend發送者名字 群聊通過群成員獲取，單聊通過好友獲取
            String friend;
            if(RoomManage.ROOM_TYPE_MULTI.equals(roomType)){
                friend = mdbRoomManage.findRoomName(from);
            }else{
                friend = mgr.findUserName(from);
            }
            if(StringUtils.isEmpty(friend))friend=from;
            int status = 0;
            int type = 0;
            String redpacket = messageInfo.getMessage();
            File file=null;
            //根據消息類型處理對應的消息
            switch ((int)content.get("type")){
                case WsContans.MSG_AUDIO:
                    //語音
                    redpacket = "[" + context.getString(R.string.voice) + "]";
                    msgType = FROM_VOICE_MSG;
                    goChat(from,context.getString(R.string.voice),roomType);
                    String fileName = UtilTool.createtFileName() + ".amr";
                    String path = context.getFilesDir().getAbsolutePath() + File.separator + "RecordRemDir";
                    file= saveFile((byte[])messageMap.get("attachment"),fileName,path);
                    if (file != null) {
                        messageInfo.setVoice(file.getAbsolutePath());
                        messageInfo.setVoiceTime(UtilTool.getFileDuration(file.getAbsolutePath(), context) + "");
                    }
                    break;
                case WsContans.MSG_IMAGE:
                    //圖片
                    String url=uploadFile(messageInfo.getKey());
                    messageInfo.setMessage(url);
                    redpacket = "[" + context.getString(R.string.image) + "]";
                    msgType = FROM_IMG_MSG;
                    fileName = UtilTool.createtFileName() + ".jpg";
                    path = context.getFilesDir().getAbsolutePath() + File.separator
                            + "images";
                    file= saveFile((byte[])messageMap.get("attachment"),fileName,path);
                    if (file != null) {
                        messageInfo.setVoice(file.getAbsolutePath());
                    }
                    goChat(from,context.getString(R.string.image),roomType);
                    break;
                case WsContans.MSG_VIDEO:
                    //視頻
                    String key=messageInfo.getKey();
                    if (key.startsWith("https://")) {
                        url=key;
                    }else {
                       url=uploadFile(key);
                    }
                    messageInfo.setMessage(url);
                    redpacket = "[" + context.getString(R.string.video) + "]";
                    msgType = FROM_VIDEO_MSG;
                    fileName = UtilTool.createtFileName() + ".mp4";
                    path = context.getFilesDir().getAbsolutePath() + File.separator + "images";
                    file= saveFile((byte[])messageMap.get("attachment"),fileName,path);
                    if (file != null) {
                        messageInfo.setVoice(file.getAbsolutePath());
                    }
                    goChat(from,context.getString(R.string.video),roomType);
                    break;

                case WsContans.MSG_LOCATION:
                    //定位
                    redpacket = "[" + context.getString(R.string.location) + "]";
                    msgType = FROM_LOCATION_MSG;
                    fileName = UtilTool.createtFileName() + ".jpg";
                    path = context.getFilesDir().getAbsolutePath() + File.separator + "images";
                    file= saveFile((byte[])messageMap.get("attachment"),fileName,path);
                    if (file != null) {
                        messageInfo.setVoice(file.getAbsolutePath());
                    }
                    messageInfo.setMessage(messageInfo.getTitle());
                    goChat(from,context.getString(R.string.location),roomType);
                    break;

                case WsContans.MSG_SHARE_GUESS:
                    //競猜分享
                    redpacket = "[" + context.getString(R.string.share_guess) + "]";
                    msgType=FROM_GUESS_MSG;
                    goChat(from,context.getString(R.string.share_guess),roomType);
                    break;
                case WsContans.MSG_SHARE_LINK:
                    //鏈接分享
                    redpacket = "[" + context.getString(R.string.share) + "]";
                    msgType=FROM_LINK_MSG;
                    goChat(from,context.getString(R.string.share),roomType);
                    break;
                case WsContans.MSG_CARD:
                    //個人名片
                    redpacket = "[" + context.getString(R.string.person_business_card) + "]";
                    msgType=FROM_CARD_MSG;
                    goChat(from,context.getString(R.string.person_business_card),roomType);
                    break;
                case WsContans.MSG_REDBAG:
                    //紅包
                    redpacket = "[" + context.getString(R.string.red_package) + "]";
                    msgType=FROM_RED_MSG;
                    goChat(from,messageInfo.getRemark(),roomType);
                    break;
                case WsContans.MSG_TRANSFER:
                    //轉賬
                    redpacket = "[" + context.getString(R.string.transfer) + "]";
                    msgType=FROM_TRANSFER_MSG;
                    goChat(from,messageInfo.getRemark(),roomType);
                    break;
            }
            //添加数据库from
            //添加数据库from
            if(RoomManage.ROOM_TYPE_MULTI.equals(roomType)){
                messageInfo.setSend(sendFrom);
            }else{
                messageInfo.setSend(from);
            }
            messageInfo.setUsername(from);
            messageInfo.setTime(time);
            messageInfo.setType(type);
            messageInfo.setMsgType(msgType);
            messageInfo.setStatus(status);
            messageInfo.setConverstaion(redpacket);
            mgr.addMessage(messageInfo);
            int number = mgr.queryNumber(from);
            if (mgr.findConversation(from)) {
                mgr.updateConversation(from, number + 1, redpacket, time);
            } else {
                ConversationInfo info = new ConversationInfo();
                info.setTime(time);
                if(RoomManage.ROOM_TYPE_MULTI.equals(roomType)){
                    info.setChatType(RoomManage.ROOM_TYPE_MULTI);
                }else{
                    info.setChatType(RoomManage.ROOM_TYPE_SINGLE);
                }
                info.setFriend(friend);
                info.setUser(from);
                info.setNumber(1);
                info.setMessage(redpacket);
                mgr.addConversation(info);
            }
            EventBus.getDefault().post(new MessageEvent(context.getString(R.string.msg_database_update)));
            EventBus.getDefault().post(new MessageEvent(context.getString(R.string.dispose_unread_msg)));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private String uploadFile(String key) throws ParseException {
        BasicSessionCredentials sessionCredentials = new BasicSessionCredentials(
                MySharedPreferences.getInstance().getString(ACCESSKEYID),
                MySharedPreferences.getInstance().getString(SECRETACCESSKEY),
                MySharedPreferences.getInstance().getString(SESSIONTOKEN));
        AmazonS3Client s3Client = new AmazonS3Client(
                sessionCredentials);
        Regions regions = Regions.fromName("ap-northeast-2");
        Region region = Region.getRegion(regions);
        s3Client.setRegion(region);
        GeneratePresignedUrlRequest urlRequest = new GeneratePresignedUrlRequest(
                Constants.BUCKET_NAME, key);
        Date expirationDate = new SimpleDateFormat("yyyy-MM-dd").parse(UtilTool.getTitles());
        //设置过期时间
        urlRequest.setExpiration(expirationDate);
        //生成公用的url
        String url = s3Client.generatePresignedUrl(urlRequest).toString();
        return url;
    }

    private File saveFile(byte[] attachment,String fileName,String path) throws IOException {
        File file = null;
        byte[] bytes = attachment;
        if (bytes != null && bytes.length != 0) {
            InputStream in = new ByteArrayInputStream(bytes);

            File dir = new File(path);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            file = new File(dir, fileName);
            FileOutputStream fos = new FileOutputStream(file);
            byte[] b = new byte[1024];
            int nRead = 0;
            while ((nRead = in.read(b)) != -1) {
                fos.write(b, 0, nRead);
            }
            fos.flush();
            fos.close();
            in.close();
        }

        return file;
    }

    /**
     * 處理廣播消息
     * @param binary
     */
    private void friendRequest(byte[] binary) {
        try {
            Map<Object, Object> contentMap = objectMapper.readValue(binary, new TypeReference<Map<String, Object>>() {});
            Map<Object,Object> jsonMap=JSON.parseObject(new String((byte[]) contentMap.get("message")),HashMap.class);
            Map<Object,Object> messageMap= JSON.parseObject((String) jsonMap.get("message"),HashMap.class);
            bellJudgment(Constants.ADMINISTRATOR_NAME,"",false,true);
            int type= (int) messageMap.get("type");
            switch (type){
                case BC_FRIEND_REQUEST:
                case BC_FRIEND_COMMIT:
                case BC_FRIEND_REJECT:
                    //好友相關
                    friends(type,messageMap);
                    break;
                case BC_OFFLINE:
                    //離線消息
                    offlineChat(messageMap);
                    break;
                case BC_OTC_ORDER:
                    //OTC訂單信息
                    otcOrder(messageMap);
                    break;
                case BC_RED_PACKET_EXPIRED:
                    //紅包過期
                    redPacketExpired(messageMap);
                    break;
                case BC_TRANSFER_INFORM:
                    //轉賬通知
                    transferInform(messageMap);
                    break;
                case BC_QRCODE_RECEIPT_PAYMENT:
                    //收付款通知
                    qecodeReceipiPaument(messageMap);
                    break;
                case BC_COIN_IN_BROAD:
                    //充幣通知
                    break;
                case BC_INOUT_COIN_INFORM:
                    //提筆通知
                    inoutCoinInform(messageMap);
                    break;
                case BC_AUTH_STATUS:
                    //實名認證
                    authStatus(messageMap);
                    break;

            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }
    /**
     * 提筆通知
     * @param messageMap
     */
    private void inoutCoinInform(Map<Object, Object> messageMap){
        Intent intent = new Intent(context, PayDetailsActivity.class);
        intent.putExtra("id", messageMap.get("id")+"");
        intent.putExtra("type_number", messageMap.get("type_number")+"");
        goActivity(intent,context.getString(R.string.out_coin_inform),context.getString(R.string.out_coin_inform_hint));

        MessageInfo messageInfo=new MessageInfo();
        messageInfo.setSend(Constants.ADMINISTRATOR_NAME);
        messageInfo.setUsername(Constants.ADMINISTRATOR_NAME);
        messageInfo.setMsgType(ADMINISTRATOR_IN_OUT_COIN_MSG);
        messageInfo.setConverstaion("[" + context.getString(R.string.out_coin_inform) + "]");

        messageInfo.setTime((String) messageMap.get("created_at"));
        messageInfo.setRedId((Integer) messageMap.get("id"));
        messageInfo.setCount((String) messageMap.get("number"));
        messageInfo.setCoin((String) messageMap.get("coin_name"));
        messageInfo.setType((Integer) messageMap.get("type_number"));
        addMessage(messageInfo);
    }

    /**
     * 到賬、轉賬通知
     * @param messageMap
     */
    private void transferInform(Map<Object, Object> messageMap){
        MessageInfo messageInfo=new MessageInfo();
        messageInfo.setSend(Constants.ADMINISTRATOR_NAME);
        messageInfo.setUsername(Constants.ADMINISTRATOR_NAME);
        messageInfo.setMsgType(ADMINISTRATOR_TRANSFER_MSG);
        if((int)messageMap.get("type")==1){
            messageInfo.setConverstaion( "[" + context.getString(R.string.in_account_inform) + "]");
            Intent intent = new Intent(context, PayDetailsActivity.class);
            intent.putExtra("id", messageMap.get("id")+"");
            intent.putExtra("type_number", messageMap.get("type_number")+"");
            goActivity(intent,context.getString(R.string.transfer_inform),context.getString(R.string.transfer_inform_hint));
        }else{
            messageInfo.setConverstaion("[" + context.getString(R.string.transfer_inform) + "]");
        }
        messageInfo.setTime((String) messageMap.get("created_at"));
        messageInfo.setRedId((Integer) messageMap.get("id"));
        messageInfo.setCount((String) messageMap.get("number"));
        messageInfo.setCoin((String) messageMap.get("coin_name"));
        messageInfo.setStatus((Integer) messageMap.get("type"));
        messageInfo.setRemark((String) messageMap.get("name"));
        messageInfo.setType((Integer) messageMap.get("type_number"));
        addMessage(messageInfo);
    }

    /**
     * 收付款通知
     * @param messageMap
     */
    private void qecodeReceipiPaument(Map<Object, Object> messageMap){
        MessageInfo messageInfo=new MessageInfo();
        messageInfo.setSend(Constants.ADMINISTRATOR_NAME);
        messageInfo.setUsername(Constants.ADMINISTRATOR_NAME);
        messageInfo.setMsgType(ADMINISTRATOR_RECEIPT_PAY_MSG);
        if((int)messageMap.get("type")==1){
            messageInfo.setConverstaion("[" + context.getString(R.string.receipt_inform) + "]");
        }else{
            messageInfo.setConverstaion("[" + context.getString(R.string.pay_inform) + "]");
        }
        messageInfo.setTime((String) messageMap.get("created_at"));
        messageInfo.setRedId((Integer) messageMap.get("id"));
        messageInfo.setCount((String) messageMap.get("number"));
        messageInfo.setCoin((String) messageMap.get("coin_name"));
        messageInfo.setStatus((Integer) messageMap.get("type"));
        messageInfo.setRemark((String) messageMap.get("name"));
        messageInfo.setType((Integer) messageMap.get("type_number"));
        addMessage(messageInfo);
    }

    /**
     * 實名認證通知
     * @param messageMap
     */
    private void authStatus(Map<Object, Object> messageMap){
        MessageInfo messageInfo=new MessageInfo();
        messageInfo.setSend(Constants.ADMINISTRATOR_NAME);
        messageInfo.setUsername(Constants.ADMINISTRATOR_NAME);
        messageInfo.setConverstaion("[" + context.getString(R.string.real_name_verify) + "]");
        messageInfo.setMsgType(ADMINISTRATOR_AUTH_STATUS_MSG);
        messageInfo.setTime((String) messageMap.get("created_at"));
        if((int)messageMap.get("status")==3){
            MySharedPreferences.getInstance().setString(STATE, (String) messageMap.get("country"));
            MySharedPreferences.getInstance().setString(CURRENCY, (String) messageMap.get("currency"));
        }
        messageInfo.setStatus((Integer) messageMap.get("status"));
        addMessage(messageInfo);
    }

    /**
     * 紅包過期通知
     * @param messageMap
     */
    private void redPacketExpired(Map<Object, Object> messageMap){
        MessageInfo messageInfo=new MessageInfo();
        messageInfo.setSend(Constants.ADMINISTRATOR_NAME);
        messageInfo.setUsername(Constants.ADMINISTRATOR_NAME);
        messageInfo.setConverstaion( "[" + context.getString(R.string.red_expired) + "]");
        messageInfo.setMsgType(ADMINISTRATOR_RED_PACKET_EXPIRED_MSG);
        messageInfo.setTime((String) messageMap.get("created_at"));

        messageInfo.setRedId((Integer) messageMap.get("id"));
        messageInfo.setCount((String) messageMap.get("number"));
        messageInfo.setCoin((String) messageMap.get("coin_name"));
        messageInfo.setStatus((Integer) messageMap.get("rp_type"));
        addMessage(messageInfo);
    }
    /**
     * OTC消息
     * @param messageMap
     */
    private void otcOrder(Map<Object, Object> messageMap){
        MessageInfo messageInfo=new MessageInfo();
        messageInfo.setSend(Constants.ADMINISTRATOR_NAME);
        messageInfo.setUsername(Constants.ADMINISTRATOR_NAME);
        messageInfo.setConverstaion( "[" + context.getString(R.string.order) + "]");
        messageInfo.setMsgType(ADMINISTRATOR_OTC_ORDER_MSG);
        messageInfo.setTime((String) messageMap.get("created_at"));

        messageInfo.setRedId((int)messageMap.get("id"));
        messageInfo.setCount((String) messageMap.get("order_no"));
        messageInfo.setCoin((String) messageMap.get("coin_name"));
        messageInfo.setStatus((Integer) messageMap.get("status"));
        messageInfo.setType((Integer) messageMap.get("type"));
        Intent intent;
        if ((int)messageMap.get("status")== 1) {
            intent = new Intent(context, PayDetailsActivity.class);
            intent.putExtra("id", (String) messageMap.get("id"));
            intent.putExtra("type", context.getString(R.string.order));
        }else{
            intent = new Intent(context, OrderCloseActivity.class);
            intent.putExtra("id",  messageMap.get("id")+"");
            intent.putExtra("status", messageMap.get("type")+"");
        }
        goActivity(intent,context.getString(R.string.order_inform),context.getString(R.string.order_inform_hint));
        addMessage(messageInfo);
    }


    /**
     * 離線消息
     * @param messageMap
     */
    private void offlineChat(Map<Object, Object> messageMap){
        UtilTool.Log("fengjian","接受到離線消息"+messageMap.toString());
        new OfflineChatPresenter(context).getOfflineChatcallback();
        List<Map> mapList=JSON.parseArray(messageMap.get("data").toString(),Map.class);
        for (Map<Object,Object> map:mapList) {
            Map<Object,Object> contentmap=JSON.parseObject((String) map.get("content"),Map.class);
            byte[] bytes= Base64.decode((String)contentmap.get("message"),Base64.DEFAULT);
            contentmap.put("message",bytes);
            messageFeedback(contentmap, false);
        }
    }

    /**
     * 好友請求相關
     * @param type
     * @param messageMap
     */
    private void friends(int type, Map<Object, Object> messageMap){
        if(type==BC_FRIEND_REQUEST){
            String from = (String) messageMap.get("toco_id");
            //請求好友
            int mNewFriend;
            mNewFriend= MySharedPreferences.getInstance().getInteger(NEWFRIEND);
            mNewFriend++;
            MySharedPreferences.getInstance().setInteger(NEWFRIEND, mNewFriend);
            if (!mgr.findUser(from)) {
                if (!mgr.findRequest(from)) {
                    mgr.addRequest(from, 0,(String) messageMap.get("user_name"));
                }else if(mgr.queryRequest(from).getType() == 1){
                    int id = mgr.queryRequest(from).getId();
                    mgr.updateRequest(id, 0);
                }
                String acceptAdd = context.getString(R.string.receive_add_request);
                Intent intent = new Intent();
                intent.putExtra("fromName", from);
                intent.putExtra("acceptAdd", acceptAdd);
                intent.putExtra("alertSubName", (String) messageMap.get("user_name"));
                intent.setAction("com.example.eric_jqm_chat.SearchActivity");
                context.sendBroadcast(intent);
                EventBus.getDefault().post(new MessageEvent(context.getString(R.string.receive_add_request)));
            }
        }else if(type==BC_FRIEND_COMMIT){
            String from = (String) messageMap.get("toco_id");
            //確認請求
            String response;
            if("1".equals(messageMap.get("status"))){
                response = context.getString(R.string.ta_consent_add_friend);
                Intent intent = new Intent();
                intent.putExtra("response", response);
                intent.setAction("com.example.eric_jqm_chat.SearchActivity");
                UtilTool.Log("fengjian", "恭喜，对方同意添加好友！");
                EventBus.getDefault().post(new MessageEvent(context.getString(R.string.new_friend)));
                context.sendBroadcast(intent);
            }else if("2".equals(messageMap.get("status"))){
                //发送广播传递response字符串
                response = context.getString(R.string.ta_reject_add_friend);
                UtilTool.Log("fengjian", "删除成功！");
                mgr.deleteUser(from);
                mgr.deleteConversation(from);
                mgr.deleteMessage(from);
                EventBus.getDefault().post(new MessageEvent(context.getString(R.string.delete_friend)));
                Intent intent = new Intent();
                intent.putExtra("response", response);
                intent.setAction("com.example.eric_jqm_chat.SearchActivity");
                context.sendBroadcast(intent);
            }
        }else if(type==BC_FRIEND_REJECT){
            String from = (String) messageMap.get("toco_id");
            //接受到刪除好友
            mgr.deleteUser(from);
            mgr.deleteConversation(from);
            mgr.deleteMessage(from);
            EventBus.getDefault().post(new MessageEvent(context.getString(R.string.delete_friend)));
            UtilTool.Log("fengjian", "删除成功！");
        }
    }

    private void addMessage(MessageInfo messageInfo){
        //添加数据库from
        String from=messageInfo.getSend();
        String redpacket=messageInfo.getConverstaion();
        String time=messageInfo.getTime();
        mgr.addMessage(messageInfo);
        int number = mgr.queryNumber(from);
        if (mgr.findConversation(from)) {
            mgr.updateConversation(from, number + 1, redpacket, time);
        } else {
            ConversationInfo info = new ConversationInfo();
            info.setTime(time);
            info.setFriend(from);
            info.setChatType(RoomManage.ROOM_TYPE_SINGLE);
            info.setUser(from);
            info.setNumber(1);
            info.setMessage(redpacket);
            mgr.addConversation(info);
        }
        EventBus.getDefault().post(new MessageEvent(context.getString(R.string.msg_database_update)));
        EventBus.getDefault().post(new MessageEvent(context.getString(R.string.dispose_unread_msg)));
    }

    /**
     * 跳轉到指定的activity
     * @param intent
     * @param title
     * @param content
     */
    private void goActivity(Intent intent,String title,String content){
        mResultIntent = PendingIntent.getActivity(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setSmallIcon(R.mipmap.logo);
        mBuilder.setContentTitle(title);
        mBuilder.setContentText(content);
        mBuilder.setContentIntent(mResultIntent);
        mBuilder.setDefaults(Notification.DEFAULT_ALL);
        mBuilder.setAutoCancel(true);
        Notification notification = mBuilder.build();
        mNotificationManager.notify(0, notification);
    }

    private void goChat(String from, String message,String roomType){
        if(!isApplicationBroughtToBackground(context)){
            return;
        }
        if(from.isEmpty()){
            from="";
        }
        ++IMSequence;
        Intent intent = new Intent();
        intent.setClass(context, ConversationActivity.class);
        Bundle bundle = new Bundle();
        if(RoomManage.ROOM_TYPE_MULTI.equals(roomType)){
            bundle.putString("name", mdbRoomManage.findRoomName(from));
        }else{
            bundle.putString("name", mgr.findUserName(from));
        }
        bundle.putString("user", from);
        intent.putExtras(bundle);
        mResultIntent = PendingIntent.getActivity(context, 1, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setSmallIcon(R.mipmap.logo);
        if(RoomManage.ROOM_TYPE_MULTI.equals(roomType)){
            mBuilder.setContentTitle(mdbRoomManage.findRoomName(from));
        }else {
            String remark = mgr.queryRemark(from);
            if (!StringUtils.isEmpty(remark)) {
                mBuilder.setContentTitle(remark);
            } else {
                mBuilder.setContentTitle(from.split("@")[0]);
            }
        }
        mBuilder.setContentText(message);
        mBuilder.setContentIntent(mResultIntent);
        mBuilder.setAutoCancel(true);
        mBuilder.build().sound=null;
        Notification notification = mBuilder.build();
        mNotificationManager.notify(IMSequence, notification);
    }

    private boolean isApplicationBroughtToBackground(final Context context) {
        ActivityManager am = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
        if (!tasks.isEmpty()) {
            ComponentName topActivity = tasks.get(0).topActivity;
            if (!topActivity.getPackageName().equals(context.getPackageName())) {
                return true;
            }
        }
        return false;
    }
}
