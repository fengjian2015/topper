package com.bclould.tea.topperchat;

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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.sdk.android.oss.OSSClient;
import com.bclould.tea.R;
import com.bclould.tea.base.MyApp;
import com.bclould.tea.crypto.otr.OtrChatListenerManager;
import com.bclould.tea.history.DBManager;
import com.bclould.tea.history.DBRoomManage;
import com.bclould.tea.history.DBRoomMember;
import com.bclould.tea.model.ConversationInfo;
import com.bclould.tea.model.MessageInfo;
import com.bclould.tea.model.UserInfo;
import com.bclould.tea.network.OSSupload;
import com.bclould.tea.ui.activity.ConversationActivity;
import com.bclould.tea.ui.activity.InitialActivity;
import com.bclould.tea.ui.activity.OrderCloseActivity;
import com.bclould.tea.ui.activity.PayDetailsActivity;
import com.bclould.tea.utils.Constants;
import com.bclould.tea.utils.MessageEvent;
import com.bclould.tea.utils.MySharedPreferences;
import com.bclould.tea.utils.StringUtils;
import com.bclould.tea.utils.UtilTool;
import com.bclould.tea.xmpp.ConnectStateChangeListenerManager;
import com.bclould.tea.xmpp.RoomManage;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.greenrobot.eventbus.EventBus;
import org.msgpack.jackson.dataformat.MessagePackFactory;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static android.content.Context.NOTIFICATION_SERVICE;
import static com.bclould.tea.Presenter.LoginPresenter.CURRENCY;
import static com.bclould.tea.Presenter.LoginPresenter.STATE;
import static com.bclould.tea.Presenter.LoginPresenter.TOKEN;
import static com.bclould.tea.topperchat.WsContans.BC_AUTH_STATUS;
import static com.bclould.tea.topperchat.WsContans.BC_COIN_IN_BROAD;
import static com.bclould.tea.topperchat.WsContans.BC_FRIEND_COMMIT;
import static com.bclould.tea.topperchat.WsContans.BC_FRIEND_REJECT;
import static com.bclould.tea.topperchat.WsContans.BC_FRIEND_REQUEST;
import static com.bclould.tea.topperchat.WsContans.BC_INOUT_COIN_INFORM;
import static com.bclould.tea.topperchat.WsContans.BC_OFFLINE;
import static com.bclould.tea.topperchat.WsContans.BC_OTC_ORDER;
import static com.bclould.tea.topperchat.WsContans.BC_QRCODE_RECEIPT_PAYMENT;
import static com.bclould.tea.topperchat.WsContans.BC_RED_GET;
import static com.bclould.tea.topperchat.WsContans.BC_RED_PACKET_EXPIRED;
import static com.bclould.tea.topperchat.WsContans.BC_TRANSFER_INFORM;
import static com.bclould.tea.ui.activity.SystemSetActivity.INFORM;
import static com.bclould.tea.ui.adapter.ChatAdapter.ADMINISTRATOR_AUTH_STATUS_MSG;
import static com.bclould.tea.ui.adapter.ChatAdapter.ADMINISTRATOR_IN_OUT_COIN_MSG;
import static com.bclould.tea.ui.adapter.ChatAdapter.ADMINISTRATOR_OTC_ORDER_MSG;
import static com.bclould.tea.ui.adapter.ChatAdapter.ADMINISTRATOR_RECEIPT_PAY_MSG;
import static com.bclould.tea.ui.adapter.ChatAdapter.ADMINISTRATOR_RED_PACKET_EXPIRED_MSG;
import static com.bclould.tea.ui.adapter.ChatAdapter.ADMINISTRATOR_TRANSFER_MSG;
import static com.bclould.tea.ui.adapter.ChatAdapter.FROM_CARD_MSG;
import static com.bclould.tea.ui.adapter.ChatAdapter.FROM_GUESS_MSG;
import static com.bclould.tea.ui.adapter.ChatAdapter.FROM_IMG_MSG;
import static com.bclould.tea.ui.adapter.ChatAdapter.FROM_LINK_MSG;
import static com.bclould.tea.ui.adapter.ChatAdapter.FROM_LOCATION_MSG;
import static com.bclould.tea.ui.adapter.ChatAdapter.FROM_RED_MSG;
import static com.bclould.tea.ui.adapter.ChatAdapter.FROM_TEXT_MSG;
import static com.bclould.tea.ui.adapter.ChatAdapter.FROM_TRANSFER_MSG;
import static com.bclould.tea.ui.adapter.ChatAdapter.FROM_VIDEO_MSG;
import static com.bclould.tea.ui.adapter.ChatAdapter.FROM_VOICE_MSG;
import static com.bclould.tea.ui.adapter.ChatAdapter.RED_GET_MSG;
import static com.bclould.tea.ui.adapter.ChatAdapter.TO_CARD_MSG;
import static com.bclould.tea.ui.adapter.ChatAdapter.TO_GUESS_MSG;
import static com.bclould.tea.ui.adapter.ChatAdapter.TO_IMG_MSG;
import static com.bclould.tea.ui.adapter.ChatAdapter.TO_LINK_MSG;
import static com.bclould.tea.ui.adapter.ChatAdapter.TO_LOCATION_MSG;
import static com.bclould.tea.ui.adapter.ChatAdapter.TO_RED_MSG;
import static com.bclould.tea.ui.adapter.ChatAdapter.TO_TEXT_MSG;
import static com.bclould.tea.ui.adapter.ChatAdapter.TO_TRANSFER_MSG;
import static com.bclould.tea.ui.adapter.ChatAdapter.TO_VIDEO_MSG;
import static com.bclould.tea.ui.adapter.ChatAdapter.TO_VOICE_MSG;
import static com.bclould.tea.ui.fragment.FriendListFragment.NEWFRIEND;
import static com.bclould.tea.utils.MySharedPreferences.SETTING;

/**
 * Created by GIjia on 2018/6/5.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class SocketListener {
    private ObjectMapper objectMapper = new ObjectMapper(new MessagePackFactory());
    private Context context;
    private NotificationManager mNotificationManager;
    private PendingIntent mResultIntent;
    private NotificationCompat.Builder mBuilder;
    private DBManager mgr;
    private DBRoomManage mdbRoomManage;
    private DBRoomMember mdbRoomMember;
    private int IMSequence = 1000;
    ExecutorService executorService;//以後用於群聊功能
    private static SocketListener mInstance;

    public static SocketListener getInstance(Context context) {
        if (mInstance == null) {
            synchronized (SocketListener.class) {
                if (mInstance == null) {
                    mInstance = new SocketListener(context);
                }
            }
        }
        return mInstance;
    }

    private SocketListener(Context context) {
        this.context = context;
        executorService = Executors.newFixedThreadPool(5);
        mgr = new DBManager(context);
        mdbRoomManage = new DBRoomManage(context);
        mdbRoomMember = new DBRoomMember(context);
        mNotificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(context);
    }

    public void onBinaryMessage(byte[] binary) {
        try {
            ObjectMapper objectMapper = new ObjectMapper(new MessagePackFactory());
            Map<Object, Object> deserialized = objectMapper.readValue(binary, new TypeReference<Map<String, Object>>() {
            });
            Map<Object, Object> content;
            UtilTool.Log("fengjian", "接受到消息：type=" + deserialized.get("type"));
            switch ((int) deserialized.get("type")) {
                case 16:
                    //登錄反饋
                    LoginFeedback(binary);
                    break;
                case 3:
                    //消息
                    content = objectMapper.readValue((byte[]) deserialized.get("content"), new TypeReference<Map<String, Object>>() {
                    });
                    UtilTool.Log("fengjian", "聊天消息message：to：" + content.get("to") + "   from:" + content.get("from") + "   crypt:" + content.get("crypt") + "   message：" + content.get("message") + "   type：" + content.get("type")+"   id:"+content.get("id"));
                    messageFeedback(content, true, RoomManage.ROOM_TYPE_SINGLE);
                    break;
                case 13:
                    //群組消息
                    content = objectMapper.readValue((byte[]) deserialized.get("content"), new TypeReference<Map<String, Object>>() {
                    });
                    messageFeedback(content, true, RoomManage.ROOM_TYPE_MULTI);
                    break;
                case 18:
                    //廣播消息
                    friendRequest((byte[]) deserialized.get("content"));
                    break;
                case 19:
                    //創建群組反饋
                    content = objectMapper.readValue((byte[]) deserialized.get("content"), new TypeReference<Map<String, Object>>() {});
                    UtilTool.Log("fengjian", "群組消息message：group_name：" + content.get("group_name") + "  group_id:" + content.get("group_id") + "    status:" + content.get("status"));
                    break;
                case 4:
                    //ping反饋
                    MySharedPreferences.getInstance().setInteger("ping", 1);
                    break;
                case 31:
                    //其他賬號登錄
                    logout();
                    break;
                case 32:
                    content = objectMapper.readValue((byte[]) deserialized.get("content"), new TypeReference<Map<String, Object>>() {});
                    //消息回執，改變消息狀態
                    changeMsgState(content);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //其他賬號登錄
    private void logout(){
        WsConnection.getInstance().logoutService(context);
        Intent intent= new Intent(context, InitialActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        MyApp.getInstance().exit();
        MySharedPreferences.getInstance().setString(TOKEN, "");
        UtilTool.Log("fengjian","強制退出");
    }

    private void changeMsgState(Map<Object, Object> content){
        UtilTool.Log("fengjian","接受到消息反饋"+content.toString());
        MessageEvent messageEvent=new MessageEvent(context.getString(R.string.change_msg_state));
        String id=(String) content.get("id");
        messageEvent.setId(id);
        mgr.updateMessageStatus(id,1);
//        String time=content.get("Time")+"";//
//        mgr.updateMessageCreateTime(id, UtilTool.stringToLong(time));
        mgr.deleteSingleMsgId(id);
        EventBus.getDefault().post(messageEvent);
    }

    /**
     * 登錄反饋
     *
     * @param binary
     */
    private void LoginFeedback(byte[] binary) throws IOException {
        if(WsConnection.getInstance().getOutConnection()){
            UtilTool.Log("fengjian", "已經退出登錄，斷開鏈接");
            WsConnection.getInstance().logoutService(context);
        }
        WsConnection.getInstance().setIsLogin(true);
        UtilTool.Log("fengjian", "登錄成功");
        ConnectStateChangeListenerManager.get().notifyListener(ConnectStateChangeListenerManager.RECEIVING);
        WsConnection.getInstance(). setLoginConnection(false);
        new PingThread(context).start();
        new PingThreadRequest(context).start();

    }

    /**
     * 處理鈴聲
     *
     * @param from
     * @param chatmesssage
     * @param crypt
     * @return false表示攔截消息
     */
    private String bellJudgment(String from, String chatmesssage, boolean crypt, boolean isPlayHint) {
        //鈴聲必須放在處理消息類前面
        SharedPreferences sp = context.getSharedPreferences(SETTING, 0);
        boolean free = MySharedPreferences.getInstance().getBoolean(SETTING + from + UtilTool.getTocoId());
        if (crypt) {
            if (OtrChatListenerManager.getInstance().isOtrEstablishMessage(chatmesssage,
                    OtrChatListenerManager.getInstance().sessionID(UtilTool.getTocoId(), from), context)) {
                return null;
            }
            if (OtrChatListenerManager.getInstance().isExist(OtrChatListenerManager.getInstance().sessionID(UtilTool.getTocoId(), from))) {
                chatmesssage = OtrChatListenerManager.getInstance().receivedMessagesChange(chatmesssage,
                        OtrChatListenerManager.getInstance().sessionID(UtilTool.getTocoId(), from));
            }
        }
        if (isPlayHint) {
            if (free) {

            } else if (sp.contains(INFORM)) {
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
     *
     * @param content
     * @param isPlayHint
     * @param roomType
     */
    public void messageFeedback(Map<Object, Object> content, boolean isPlayHint, String roomType) {
        try {
            Map<Object, Object> messageMap = objectMapper.readValue((byte[]) content.get("message"), new TypeReference<Map<String, Object>>() {});
            //處理多終端登錄，自己消息同步問題
            String from = (String) content.get("from");
            boolean isMe=false;
            if(UtilTool.getTocoId().equals(from)){
                from= (String) content.get("to");
                isMe=true;
                isPlayHint=false;
            }

            String message = (String) messageMap.get("body");
            boolean crypt = (boolean) content.get("crypt");
            String msgId= (String) content.get("id");
            if(StringUtils.isEmpty(msgId)){
                msgId=UtilTool.createMsgId(UtilTool.getTocoId());
            }
            long createTime=UtilTool.stringToLong(content.get("time")+"");
            String otr = bellJudgment(from, message, crypt, isPlayHint);
            if (StringUtils.isEmpty(otr)) return;
            //默認文本類型
            MessageInfo messageInfo = new MessageInfo();
            if (crypt) {
                if (context.getString(R.string.otr_error).equals(otr)) {
                    messageInfo.setMessage(otr);
                    content.put("type", WsContans.MSG_TEXT);
                } else {
                    messageInfo = JSONObject.parseObject(otr, MessageInfo.class);
                }
            } else {
                if (!StringUtils.isEmpty(message)) {
                    messageInfo = JSONObject.parseObject(message, MessageInfo.class);
                }
            }
            int msgType;
            if(isMe){
                msgType = TO_TEXT_MSG;
            }else {
                msgType = FROM_TEXT_MSG;
            }
            String time = UtilTool.createChatTime();
            // TODO: 2018/6/6 sendFrom 群聊需要修改
            String sendFrom = from;
            // TODO: 2018/6/6 friend發送者名字 群聊通過群成員獲取，單聊通過好友獲取
            String friend;
            if (RoomManage.ROOM_TYPE_MULTI.equals(roomType)) {
                friend = mdbRoomManage.findRoomName(from);
            } else {
                friend = mgr.findUserName(from);
            }
            if (StringUtils.isEmpty(friend)) friend = from;
            int status = 0;
            int type = 0;
            String redpacket = messageInfo.getMessage();
            File file = null;
            //根據消息類型處理對應的消息
            switch ((int) content.get("type")) {
                case WsContans.MSG_AUDIO:
                    //語音
                    redpacket = "[" + context.getString(R.string.voice) + "]";
                    if(isMe){
                        msgType = TO_VOICE_MSG;
                    }else {
                        msgType = FROM_VOICE_MSG;
                    }
                    goChat(from, context.getString(R.string.voice), roomType);
                    String fileName = UtilTool.createtFileName() + ".amr";
                    String path = context.getFilesDir().getAbsolutePath() + File.separator + "RecordRemDir";
                    file = saveFile((byte[]) messageMap.get("attachment"), fileName, path);
                    if (file != null) {
                        messageInfo.setVoice(file.getAbsolutePath());
                        messageInfo.setVoiceTime(UtilTool.getFileDuration(file.getAbsolutePath(), context) + "");
                    }
                    break;
                case WsContans.MSG_IMAGE:
                    //圖片
                    String url = downFile(messageInfo.getKey());
                    messageInfo.setMessage(url);
                    redpacket = "[" + context.getString(R.string.image) + "]";
                    if(isMe){
                        msgType = TO_IMG_MSG;
                    }else {
                        msgType = FROM_IMG_MSG;
                    }
                    fileName = UtilTool.createtFileName() + ".jpg";
                    path = context.getFilesDir().getAbsolutePath() + File.separator
                            + "images";
                    file = saveFile((byte[]) messageMap.get("attachment"), fileName, path);
                    if (file != null) {
                        messageInfo.setVoice(file.getAbsolutePath());
                    }
                    goChat(from, context.getString(R.string.image), roomType);
                    break;
                case WsContans.MSG_VIDEO:
                    //視頻
                    String key = messageInfo.getKey();
                    if (key.startsWith("https://")) {
                        url = key;
                    } else {
                        url = downFile(key);
                    }
                    messageInfo.setMessage(url);
                    redpacket = "[" + context.getString(R.string.video) + "]";
                    if(isMe){
                        msgType = TO_VIDEO_MSG;
                    }else {
                        msgType = FROM_VIDEO_MSG;
                    }
                    fileName = UtilTool.createtFileName() + ".mp4";
                    path = context.getFilesDir().getAbsolutePath() + File.separator + "images";
                    file = saveFile((byte[]) messageMap.get("attachment"), fileName, path);
                    if (file != null) {
                        messageInfo.setVoice(file.getAbsolutePath());
                    }
                    goChat(from, context.getString(R.string.video), roomType);
                    break;

                case WsContans.MSG_LOCATION:
                    //定位
                    redpacket = "[" + context.getString(R.string.location) + "]";
                    if(isMe){
                        msgType = TO_LOCATION_MSG;
                    }else {
                        msgType = FROM_LOCATION_MSG;
                    }
                    fileName = UtilTool.createtFileName() + ".jpg";
                    path = context.getFilesDir().getAbsolutePath() + File.separator + "images";
                    file = saveFile((byte[]) messageMap.get("attachment"), fileName, path);
                    if (file != null) {
                        messageInfo.setVoice(file.getAbsolutePath());
                    }
                    messageInfo.setMessage(messageInfo.getTitle());
                    goChat(from, context.getString(R.string.location), roomType);
                    break;

                case WsContans.MSG_SHARE_GUESS:
                    //競猜分享
                    redpacket = "[" + context.getString(R.string.share_guess) + "]";
                    if(isMe){
                        msgType = TO_GUESS_MSG;
                    }else {
                        msgType = FROM_GUESS_MSG;
                    }
                    goChat(from, context.getString(R.string.share_guess), roomType);
                    break;
                case WsContans.MSG_SHARE_LINK:
                    //鏈接分享
                    redpacket = "[" + context.getString(R.string.share) + "]";
                    if(isMe){
                        msgType = TO_LINK_MSG;
                    }else {
                        msgType = FROM_LINK_MSG;
                    }
                    goChat(from, context.getString(R.string.share), roomType);
                    break;
                case WsContans.MSG_CARD:
                    //個人名片
                    redpacket = "[" + context.getString(R.string.person_business_card) + "]";
                    if(isMe){
                        msgType = TO_CARD_MSG;
                    }else {
                        msgType = FROM_CARD_MSG;
                    }
                    goChat(from, context.getString(R.string.person_business_card), roomType);
                    break;
                case WsContans.MSG_REDBAG:
                    //紅包
                    redpacket = "[" + context.getString(R.string.red_package) + "]";
                    if(isMe){
                        msgType = TO_RED_MSG;
                    }else {
                        msgType = FROM_RED_MSG;
                    }
                    goChat(from, messageInfo.getRemark(), roomType);
                    break;
                case WsContans.MSG_TRANSFER:
                    //轉賬
                    redpacket = "[" + context.getString(R.string.transfer) + "]";
                    if(isMe){
                        msgType = TO_TRANSFER_MSG;
                    }else {
                        msgType = FROM_TRANSFER_MSG;
                    }
                    goChat(from, messageInfo.getRemark(), roomType);
                    break;
                default:
                    goChat(from, messageInfo.getMessage(), roomType);
                    break;
            }
            //添加数据库from
            //添加数据库from
            if (RoomManage.ROOM_TYPE_MULTI.equals(roomType)) {
                messageInfo.setSend(sendFrom);
            } else {
                messageInfo.setSend(from);
            }
            messageInfo.setUsername(from);
            messageInfo.setTime(time);
            messageInfo.setType(type);
            messageInfo.setMsgType(msgType);
            messageInfo.setStatus(status);
            messageInfo.setConverstaion(redpacket);
            messageInfo.setMsgId(msgId);
            messageInfo.setSendStatus(1);
            messageInfo.setCreateTime(createTime);
            mgr.addMessage(messageInfo);
            int number = mgr.queryNumber(from);
            if (mgr.findConversation(from)) {
                if (!isMe){
                    number++;
                }
                mgr.updateConversation(from, number, redpacket, time,createTime);
            } else {
                ConversationInfo info = new ConversationInfo();
                info.setTime(time);
                if (RoomManage.ROOM_TYPE_MULTI.equals(roomType)) {
                    info.setChatType(RoomManage.ROOM_TYPE_MULTI);
                } else {
                    info.setChatType(RoomManage.ROOM_TYPE_SINGLE);
                }
                info.setFriend(friend);
                info.setUser(from);
                info.setNumber(1);
                info.setMessage(redpacket);
                info.setCreateTime(UtilTool.createChatCreatTime());
                mgr.addConversation(info);
            }
            MessageEvent messageEvent=new MessageEvent(context.getString(R.string.msg_database_update));
            messageEvent.setId(msgId);
            EventBus.getDefault().post(messageEvent);
            EventBus.getDefault().post(new MessageEvent(context.getString(R.string.dispose_unread_msg)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String downFile(String key) throws ParseException {
        OSSClient ossClient = OSSupload.getInstance().visitOSS();
        String url = null;
        url = ossClient.presignPublicObjectURL(Constants.BUCKET_NAME2, key);
        return url;
    }

    private File saveFile(byte[] attachment, String fileName, String path) throws IOException {
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
     *
     * @param binary
     */
    private void friendRequest(byte[] binary) {
        try {
            Map<Object, Object> contentMap = objectMapper.readValue(binary, new TypeReference<Map<String, Object>>() {
            });
            Map<Object, Object> jsonMap = JSON.parseObject(new String((byte[]) contentMap.get("message")), HashMap.class);
            Map<Object, Object> messageMap = JSON.parseObject((String) jsonMap.get("message"), HashMap.class);
            int type = (int) messageMap.get("type");
            switch (type) {
                case BC_FRIEND_REQUEST:
                case BC_FRIEND_COMMIT:
                case BC_FRIEND_REJECT:
                    //好友相關
                    friends(type, messageMap);
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
                case BC_RED_GET:
                    //紅包領取通知
                    redGet(messageMap);
                    break;

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 紅包被領取通知
     *
     * @param messageMap
     */
    private void redGet(Map<Object, Object> messageMap) {
        mgr.updateMessageRedState(messageMap.get("rp_id") + "", 1);
        MessageInfo messageInfo = new MessageInfo();
        messageInfo.setSend((String) messageMap.get("toco_id"));
        messageInfo.setUsername((String) messageMap.get("toco_id"));
        messageInfo.setMsgType(RED_GET_MSG);
        messageInfo.setConverstaion((String) messageMap.get("desc"));
        messageInfo.setTime(UtilTool.createChatTime());
        messageInfo.setRedId((Integer) messageMap.get("rp_id"));
        messageInfo.setMessage((String) messageMap.get("desc"));
        addMessage(messageInfo);
    }

    /**
     * 提筆通知
     *
     * @param messageMap
     */
    private void inoutCoinInform(Map<Object, Object> messageMap) {
        Intent intent = new Intent(context, PayDetailsActivity.class);
        intent.putExtra("id", messageMap.get("id") + "");
        intent.putExtra("type_number", messageMap.get("type_number") + "");
        goActivity(intent, context.getString(R.string.out_coin_inform), context.getString(R.string.out_coin_inform_hint));

        MessageInfo messageInfo = new MessageInfo();
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
     *
     * @param messageMap
     */
    private void transferInform(Map<Object, Object> messageMap) {
        MessageInfo messageInfo = new MessageInfo();
        messageInfo.setSend(Constants.ADMINISTRATOR_NAME);
        messageInfo.setUsername(Constants.ADMINISTRATOR_NAME);
        messageInfo.setMsgType(ADMINISTRATOR_TRANSFER_MSG);
        if ((int) messageMap.get("type") == 1) {
            messageInfo.setConverstaion("[" + context.getString(R.string.in_account_inform) + "]");
            Intent intent = new Intent(context, PayDetailsActivity.class);
            intent.putExtra("id", messageMap.get("id") + "");
            intent.putExtra("type_number", messageMap.get("type_number") + "");
            goActivity(intent, context.getString(R.string.transfer_inform), context.getString(R.string.transfer_inform_hint));
        } else {
            messageInfo.setConverstaion("[" + context.getString(R.string.transfer_inform) + "]");
        }
        messageInfo.setTime((String) messageMap.get("created_at"));
        messageInfo.setRedId((Integer) messageMap.get("id"));
        messageInfo.setCount((String) messageMap.get("number"));
        messageInfo.setCoin((String) messageMap.get("coin_name"));
        messageInfo.setStatus((Integer) messageMap.get("type"));
        messageInfo.setRemark((String) messageMap.get("name"));
        messageInfo.setBetId(messageMap.get("log_id") + "");
        messageInfo.setType((Integer) messageMap.get("type_number"));
        addMessage(messageInfo);
    }

    /**
     * 收付款通知
     *
     * @param messageMap
     */
    private void qecodeReceipiPaument(Map<Object, Object> messageMap) {
        MessageInfo messageInfo = new MessageInfo();
        messageInfo.setSend(Constants.ADMINISTRATOR_NAME);
        messageInfo.setUsername(Constants.ADMINISTRATOR_NAME);
        messageInfo.setMsgType(ADMINISTRATOR_RECEIPT_PAY_MSG);
        if ((int) messageMap.get("type_number") == 5) {
            messageInfo.setConverstaion("[" + context.getString(R.string.receipt_inform) + "]");
        } else {
            messageInfo.setConverstaion("[" + context.getString(R.string.pay_inform) + "]");
        }
        messageInfo.setTime((String) messageMap.get("created_at"));
        messageInfo.setRedId((Integer) messageMap.get("id"));
        messageInfo.setCount((String) messageMap.get("number"));
        messageInfo.setCoin((String) messageMap.get("coin_name"));
        messageInfo.setStatus((Integer) messageMap.get("type"));
        messageInfo.setRemark((String) messageMap.get("name"));
        messageInfo.setBetId(messageMap.get("log_id") + "");
        messageInfo.setType((Integer) messageMap.get("type_number"));
        addMessage(messageInfo);
    }

    /**
     * 實名認證通知
     *
     * @param messageMap
     */
    private void authStatus(Map<Object, Object> messageMap) {
        MessageInfo messageInfo = new MessageInfo();
        messageInfo.setSend(Constants.ADMINISTRATOR_NAME);
        messageInfo.setUsername(Constants.ADMINISTRATOR_NAME);
        messageInfo.setConverstaion("[" + context.getString(R.string.real_name_verify) + "]");
        messageInfo.setMsgType(ADMINISTRATOR_AUTH_STATUS_MSG);
        messageInfo.setTime((String) messageMap.get("created_at"));
        if ((int) messageMap.get("status") == 3) {
            MySharedPreferences.getInstance().setString(STATE, (String) messageMap.get("country"));
            MySharedPreferences.getInstance().setString(CURRENCY, (String) messageMap.get("currency"));
        }
        messageInfo.setStatus((Integer) messageMap.get("status"));
        addMessage(messageInfo);
    }

    /**
     * 紅包過期通知
     *
     * @param messageMap
     */
    private void redPacketExpired(Map<Object, Object> messageMap) {
        MessageInfo messageInfo = new MessageInfo();
        messageInfo.setSend(Constants.ADMINISTRATOR_NAME);
        messageInfo.setUsername(Constants.ADMINISTRATOR_NAME);
        messageInfo.setConverstaion("[" + context.getString(R.string.red_expired) + "]");
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
     *
     * @param messageMap
     */
    private void otcOrder(Map<Object, Object> messageMap) {
        MessageInfo messageInfo = new MessageInfo();
        messageInfo.setSend(Constants.ADMINISTRATOR_NAME);
        messageInfo.setUsername(Constants.ADMINISTRATOR_NAME);
        messageInfo.setConverstaion("[" + context.getString(R.string.order) + "]");
        messageInfo.setMsgType(ADMINISTRATOR_OTC_ORDER_MSG);
        messageInfo.setTime((String) messageMap.get("created_at"));

        messageInfo.setRedId((int) messageMap.get("id"));
        messageInfo.setCount((String) messageMap.get("order_no"));
        messageInfo.setCoin((String) messageMap.get("coin_name"));
        messageInfo.setStatus((Integer) messageMap.get("status"));
        messageInfo.setType((Integer) messageMap.get("type"));
        Intent intent;
        if ((int) messageMap.get("status") == 1) {
            intent = new Intent(context, PayDetailsActivity.class);
            intent.putExtra("id", (String) messageMap.get("id"));
            intent.putExtra("type", context.getString(R.string.order));
        } else {
            intent = new Intent(context, OrderCloseActivity.class);
            intent.putExtra("id", messageMap.get("id") + "");
            intent.putExtra("status", messageMap.get("type") + "");
        }
        goActivity(intent, context.getString(R.string.order_inform), context.getString(R.string.order_inform_hint));
        addMessage(messageInfo);
    }


    /**
     * 離線消息
     *
     * @param messageMap
     */
    private void offlineChat(Map<Object, Object> messageMap) {
        UtilTool.Log("fengjian", "接受到離線消息" + messageMap.toString());
        List<Map> mapList = JSON.parseArray(messageMap.get("data").toString(), Map.class);
        for (Map<Object, Object> map : mapList) {
            Map<Object, Object> contentmap = JSON.parseObject((String) map.get("content"), Map.class);
            byte[] bytes = Base64.decode((String) contentmap.get("message"), Base64.DEFAULT);
            contentmap.put("message", bytes);
            messageFeedback(contentmap, false, "");
        }
    }

    /**
     * 好友請求相關
     *
     * @param type
     * @param messageMap
     */
    private void friends(int type, Map<Object, Object> messageMap) {
        if (type == BC_FRIEND_REQUEST) {
            String from = (String) messageMap.get("toco_id");
            //請求好友
            int mNewFriend;
            mNewFriend = MySharedPreferences.getInstance().getInteger(NEWFRIEND);
            mNewFriend++;
            MySharedPreferences.getInstance().setInteger(NEWFRIEND, mNewFriend);
            if (!mgr.findUser(from)) {
                if (!mgr.findRequest(from)) {
                    mgr.addRequest(from, 0, (String) messageMap.get("user_name"));
                } else if (mgr.queryRequest(from).getType() == 1) {
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
        } else if (type == BC_FRIEND_COMMIT) {
            String from = (String) messageMap.get("toco_id");
            //確認請求
            String response;
            if ("1".equals(messageMap.get("status"))) {
                response = context.getString(R.string.ta_consent_add_friend);
                Intent intent = new Intent();
                intent.putExtra("response", response);
                intent.setAction("com.example.eric_jqm_chat.SearchActivity");
                UtilTool.Log("fengjian", "恭喜，对方同意添加好友！");
                UserInfo userInfo = new UserInfo();
                userInfo.setUser(from);
                userInfo.setUserName(" ");
                userInfo.setRemark(" ");
                mgr.addUser(userInfo);
                EventBus.getDefault().post(new MessageEvent(context.getString(R.string.new_friend)));
                context.sendBroadcast(intent);
            } else if ("2".equals(messageMap.get("status"))) {
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
        } else if (type == BC_FRIEND_REJECT) {
            String from = (String) messageMap.get("toco_id");
            //接受到刪除好友
            mgr.deleteUser(from);
            mgr.deleteConversation(from);
            mgr.deleteMessage(from);
            EventBus.getDefault().post(new MessageEvent(context.getString(R.string.delete_friend)));
            UtilTool.Log("fengjian", "删除成功！");
        }
    }

    private void addMessage(MessageInfo messageInfo) {
        //添加数据库from
        String from = messageInfo.getSend();
        String redpacket = messageInfo.getConverstaion();
        String time = messageInfo.getTime();
        messageInfo.setCreateTime(UtilTool.createChatCreatTime());
        mgr.addMessage(messageInfo);
        int number = mgr.queryNumber(from);
        if (mgr.findConversation(from)) {
            mgr.updateConversation(from, number + 1, redpacket, time,messageInfo.getCreateTime());
        } else {
            ConversationInfo info = new ConversationInfo();
            info.setTime(time);
            info.setFriend(from);
            info.setChatType(RoomManage.ROOM_TYPE_SINGLE);
            info.setUser(from);
            info.setNumber(1);
            info.setMessage(redpacket);
            info.setCreateTime(messageInfo.getCreateTime());
            mgr.addConversation(info);
        }
        EventBus.getDefault().post(new MessageEvent(context.getString(R.string.msg_database_update)));
        EventBus.getDefault().post(new MessageEvent(context.getString(R.string.dispose_unread_msg)));
    }

    /**
     * 跳轉到指定的activity
     *
     * @param intent
     * @param title
     * @param content
     */
    private void goActivity(Intent intent, String title, String content) {
        mResultIntent = PendingIntent.getActivity(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setSmallIcon(R.mipmap.logo);
        mBuilder.setContentTitle(title);
        mBuilder.setContentText(content);
        mBuilder.setContentIntent(mResultIntent);
        mBuilder.setDefaults(Notification.DEFAULT_ALL);
        mBuilder.setAutoCancel(true);
        Notification notification = mBuilder.build();
        mNotificationManager.notify(0, notification);
        bellJudgment(Constants.ADMINISTRATOR_NAME, "", false, true);
    }

    private void goChat(String from, String message, String roomType) {
        if (!isApplicationBroughtToBackground(context)) {
            return;
        }
        if (from.isEmpty()) {
            from = "";
        }
        ++IMSequence;
        Intent intent = new Intent();
        intent.setClass(context, ConversationActivity.class);
        Bundle bundle = new Bundle();
        if (RoomManage.ROOM_TYPE_MULTI.equals(roomType)) {
            bundle.putString("name", mdbRoomManage.findRoomName(from));
        } else {
            bundle.putString("name", mgr.findUserName(from));
        }
        bundle.putString("user", from);
        intent.putExtras(bundle);
        mResultIntent = PendingIntent.getActivity(context, 1, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setSmallIcon(R.mipmap.logo);
        if (RoomManage.ROOM_TYPE_MULTI.equals(roomType)) {
            mBuilder.setContentTitle(mdbRoomManage.findRoomName(from));
        } else {
            String remark = mgr.queryRemark(from);
            if (!StringUtils.isEmpty(remark)) {
                mBuilder.setContentTitle(remark);
            } else {
                if (RoomManage.ROOM_TYPE_MULTI.equals(roomType)) {
                    mBuilder.setContentTitle(mdbRoomManage.findRoomName(from));
                } else {
                    mBuilder.setContentTitle(mgr.findUserName(from));
                }
            }
        }
        mBuilder.setContentText(message);
        mBuilder.setContentIntent(mResultIntent);
        mBuilder.setAutoCancel(true);
        mBuilder.build().sound = null;
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
