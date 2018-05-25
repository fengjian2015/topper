package com.bclould.tocotalk.xmpp;

import android.annotation.SuppressLint;
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
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.RequiresApi;
import android.support.v7.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Base64;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.bclould.tocotalk.R;
import com.bclould.tocotalk.crypto.otr.OtrChatListenerManager;
import com.bclould.tocotalk.history.DBManager;
import com.bclould.tocotalk.model.AuthStatusInfo;
import com.bclould.tocotalk.model.ConversationInfo;
import com.bclould.tocotalk.model.MessageInfo;
import com.bclould.tocotalk.model.OtcOrderStatusInfo;
import com.bclould.tocotalk.model.QrcodeReceiptPayInfo;
import com.bclould.tocotalk.model.RedExpiredInfo;
import com.bclould.tocotalk.ui.activity.ConversationActivity;
import com.bclould.tocotalk.ui.activity.OrderCloseActivity;
import com.bclould.tocotalk.ui.activity.OrderDetailsActivity;
import com.bclould.tocotalk.ui.activity.PayDetailsActivity;
import com.bclould.tocotalk.ui.fragment.ConversationFragment;
import com.bclould.tocotalk.utils.Constants;
import com.bclould.tocotalk.utils.MessageEvent;
import com.bclould.tocotalk.utils.MySharedPreferences;
import com.bclould.tocotalk.utils.StringUtils;
import com.bclould.tocotalk.utils.UtilTool;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.chat.ChatManagerListener;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.filter.StanzaFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jivesoftware.smackx.offline.OfflineMessageManager;
import org.xutils.common.util.LogUtil;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static android.content.Context.NOTIFICATION_SERVICE;
import static com.bclould.tocotalk.Presenter.LoginPresenter.CURRENCY;
import static com.bclould.tocotalk.Presenter.LoginPresenter.STATE;
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
import static com.bclould.tocotalk.ui.adapter.ChatAdapter.FROM_IMG_MSG;
import static com.bclould.tocotalk.ui.adapter.ChatAdapter.FROM_LOCATION_MSG;
import static com.bclould.tocotalk.ui.adapter.ChatAdapter.FROM_RED_MSG;
import static com.bclould.tocotalk.ui.adapter.ChatAdapter.FROM_TEXT_MSG;
import static com.bclould.tocotalk.ui.adapter.ChatAdapter.FROM_TRANSFER_MSG;
import static com.bclould.tocotalk.ui.adapter.ChatAdapter.FROM_VIDEO_MSG;
import static com.bclould.tocotalk.ui.adapter.ChatAdapter.FROM_VOICE_MSG;
import static com.bclould.tocotalk.utils.MySharedPreferences.SETTING;

/**
 * Created by GIjia on 2018/5/14.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class XmppListener {
    private FragmentOneHandler handler;
    private Context context;
    private NotificationManager mNotificationManager;
    private PendingIntent mResultIntent;
    private NotificationCompat.Builder mBuilder;
    private DBManager mgr;

    private int IMSequence = 1000;
    ExecutorService executorService;//以後用於群聊功能
    public static XmppListener xmppListener;

    public static XmppListener get(Context context) {
        if (xmppListener == null) {
            xmppListener = new XmppListener(context);
        }
        return xmppListener;
    }

    public void remove() {
        xmppListener = null;
    }

    public XmppListener(Context context) {
        this.context = context;
        executorService = Executors.newFixedThreadPool(5);
        mgr = new DBManager(context);
        handler = new FragmentOneHandler();
        mNotificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(context);
        getOfflineMessage();
        initBroadcastListener();
        initListener();
//        initPacketListener();
//        initPresenceListener();
    }


    public void initBroadcastListener() {
        AbstractXMPPConnection connection = XmppConnection.getInstance().getConnection();
        connection.addAsyncStanzaListener(new StanzaListener() {
            @Override
            public void processStanza(Stanza packet) throws SmackException.NotConnectedException, InterruptedException {
                Message message = (Message) packet;
                if (message.getBody() != null) {
                    //获取Jid和用户名
                    String from = message.getFrom().toString();
                    if (!from.equals(Constants.DOMAINNAME)) {
                        return;
                    }
                    SharedPreferences sp = context.getSharedPreferences(SETTING, 0);
                    if (sp.contains(INFORM)) {
                        if (MySharedPreferences.getInstance().getBoolean(INFORM)) {
                            UtilTool.playHint(context);
                        }
                    } else {
                        UtilTool.playHint(context);
                    }
                    UtilTool.Log("fengjian---", "廣播消息");
                    android.os.Message msg = new android.os.Message();
                    msg.obj = message;
                    msg.what = 2;
                    handler.sendMessage(msg);
                }
            }
        }, new StanzaFilter() {
            @Override
            public boolean accept(Stanza stanza) {
                return stanza instanceof Message;
            }
        });
    }

    public void getOfflineMessage() {
        try {
//            Presence presence1 = new Presence(Presence.Type.unavailable);
//            XmppConnection.getInstance().getConnection().sendStanza(presence1);
            OfflineMessageManager offlineManager = new OfflineMessageManager(XmppConnection.getInstance().getConnection());
            List<Message> list = offlineManager.getMessages();
            if (list.size() != 0) {
                SharedPreferences sp = context.getSharedPreferences(SETTING, 0);
                if (sp.contains(INFORM)) {
                    if (MySharedPreferences.getInstance().getBoolean(INFORM)) {
                        UtilTool.playHint(context);
                    }
                } else {
                    UtilTool.playHint(context);
                }
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i) != null && list.get(i).getBody() != null && !list.get(i).getBody().equals("")) {
                        android.os.Message msg = new android.os.Message();
                        msg.obj = list.get(i);
                        msg.what = 2;
                        handler.sendMessage(msg);
                    }
                }
                //删除离线消息
                offlineManager.deleteMessages();
            }
            UtilTool.Log("fengjian---", "收到離線消息" + list.size());
            //将状态设置成在线
            Presence presence = new Presence(Presence.Type.available);
            XmppConnection.getInstance().getConnection().sendStanza(presence);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initPresenceListener() {
        StanzaFilter filter = new AndFilter(new PacketTypeFilter(Presence.class));
        PacketListener listener = new PacketListener() {
            @Override
            public void processStanza(Stanza packet) throws SmackException.NotConnectedException, InterruptedException {
//                Message message = (Message) packet;
//                String body = message.getBody();
//                UtilTool.Log("fengjian群日志","接受群的消息"+message.getType().name()+"    \n"+message.getType().toString()+"\n"+message.toXML());
            }
        };
        XmppConnection.getInstance().getConnection().addPacketListener(listener, filter);
    }

    private void initPacketListener() {
        StanzaFilter filter = new AndFilter(new PacketTypeFilter(Message.class));
        PacketListener listener = new PacketListener() {
            @Override
            public void processStanza(Stanza packet) throws SmackException.NotConnectedException, InterruptedException {
                Message message = (Message) packet;
                String body = message.getBody();
                if (message.getType() == Message.Type.normal) {
                    UtilTool.Log("fengjian群日志", "暂时表示收到邀请群聊");
                    createConversation(message);
                    EventBus.getDefault().post(new MessageEvent(context.getString(R.string.oneself_send_msg)));
                    return;
                }
                UtilTool.Log("fengjian群日志", "接受聊天消息" + message.getType().name() + "    \n" + message.getType().toString() + "\n" + body);
            }
        };
        XmppConnection.getInstance().getConnection().addPacketListener(listener, filter);
    }

    private void createConversation(Message message) {
        ConversationInfo info = new ConversationInfo();
        info.setChatType(RoomManage.ROOM_TYPE_MULTI);
        info.setIstop("false");
        info.setFriend(message.getFrom().toString().split("@")[0]);
        info.setUser(message.getFrom().toString());
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());
        String time = formatter.format(curDate);
        info.setTime(time);
        info.setMessage("加入群聊");
        mgr.addConversation(info);
    }

    /**
     * 初始化聊天消息监听
     */
    public void initListener() {
        ChatManager manager = ChatManager.getInstanceFor(XmppConnection.getInstance().getConnection());
        //设置信息的监听
        final ChatMessageListener messageListener = new ChatMessageListener() {
            @Override
            public void processMessage(Chat chat, Message message) {
                UtilTool.Log("fengjian---", "收到聊天消息");
                android.os.Message msg = new android.os.Message();
                msg.obj = message;
                msg.what = 1;
                handler.sendMessage(msg);
            }
        };
        ChatManagerListener chatManagerListener = new ChatManagerListener() {

            @Override
            public void chatCreated(Chat chat, boolean arg1) {
                chat.addMessageListener(messageListener);

            }
        };
        manager.addChatListener(chatManagerListener);
    }

    private Message bellJudgment(Message message) {
        //获取Jid和用户名
        String from = message.getFrom().toString();
        if (from.equals(Constants.DOMAINNAME)) {
            from = Constants.ADMINISTRATOR_NAME;
        }
        if (from.contains("/"))
            from = from.substring(0, from.indexOf("/"));
        String chatmesssage = message.getBody();
        //鈴聲必須放在處理消息類前面
        SharedPreferences sp = context.getSharedPreferences(SETTING, 0);

        boolean free = MySharedPreferences.getInstance().getBoolean(SETTING + from + UtilTool.getJid());
        if (OtrChatListenerManager.getInstance().isOtrEstablishMessage(chatmesssage,
                OtrChatListenerManager.getInstance().sessionID(UtilTool.getJid(), from), context)) {
            return null;
        }
        if (OtrChatListenerManager.getInstance().isExist(OtrChatListenerManager.getInstance().sessionID(UtilTool.getJid(), from))) {
            chatmesssage = OtrChatListenerManager.getInstance().receivedMessagesChange(chatmesssage,
                    OtrChatListenerManager.getInstance().sessionID(UtilTool.getJid(), from));
        }
        if (chatmesssage == null) {
            return null;
        }
        if (free) {

        } else if (sp.contains(INFORM)) {
            if (MySharedPreferences.getInstance().getBoolean(INFORM)) {
                UtilTool.playHint(context);
            }
        } else {
            UtilTool.playHint(context);
        }
        message.setBody(chatmesssage);
        return message;
    }

    public class FragmentOneHandler extends Handler {
        @SuppressLint("WrongConstant")
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    Message message1 = (Message) msg.obj;
                    message1 = bellJudgment(message1);
                    if (message1 == null) {
                        return;
                    }
                    //当消息返回为空的时候，表示用户正在聊天窗口编辑信息并未发出消
                    if (!TextUtils.isEmpty(message1.getBody())) {
                        //message为用户所收到的消息
                        android.os.Message msg1 = new android.os.Message();
                        msg1.obj = message1;
                        msg1.what = 2;
                        handler.sendMessage(msg1);
                    }
                    break;
                case 2:
                    //获取接收的好友名及聊天消息
                    try {
                        Message message = (Message) msg.obj;
                        int msgType = FROM_TEXT_MSG;

                        //获取文本消息
                        MessageInfo messageInfo = new MessageInfo();
                        String chatMsg = message.getBody();

                        //获取当前时间
                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Date curDate = new Date(System.currentTimeMillis());
                        String time = formatter.format(curDate);

                        //获取Jid和用户名
                        String from = message.getFrom().toString();
                        if (from.equals(Constants.DOMAINNAME)) {
                            from = Constants.ADMINISTRATOR_NAME;
                        }
                        String friend = from;
                        if (from.contains("/"))
                            from = from.substring(0, from.indexOf("/"));
                        if (from.contains("@"))
                            friend = from.substring(0, from.indexOf("@"));


                        String remark = null;
                        String coin = null;
                        String count = null;
                        int status = 0;
                        int redId = 0;
                        int type = 0;
                        String redpacket = null;
                        redpacket = chatMsg;
                        String msgXML = message.toXML().toString();
                        String startTag = "<attachment xmlns='jabber:client' xmlns:stream='http://etherx.jabber.org/streams'>";
                        String endTag = "</attachment>";
                        UtilTool.Log("语音", msgXML);
                        File file = null;
                        if (msgXML.contains(startTag)) {
                            String voiceBase64 = msgXML.substring(msgXML.indexOf(startTag) + startTag.length(), msgXML.indexOf(endTag));
                            UtilTool.Log("语音", voiceBase64);
                            byte[] bytes = Base64.decode(voiceBase64, Base64.DEFAULT);
                            if (bytes != null && bytes.length != 0) {
                                InputStream in = new ByteArrayInputStream(bytes);
                                String fileName = "";
                                String path = "";
                                if (chatMsg.contains("[audio]")) {
                                    redpacket = "[" + context.getString(R.string.voice) + "]";
                                    msgType = FROM_VOICE_MSG;
                                    fileName = UtilTool.createtFileName() + ".amr";
                                    path = context.getFilesDir().getAbsolutePath() + File.separator
                                            + "RecordRemDir";
                                    goChat(from, context.getString(R.string.voice));
                                } else if (chatMsg.contains("[LOCATION]")) {
                                    MessageInfo messageInfo1 = new MessageInfo();
                                    messageInfo1 = JSONObject.parseObject(chatMsg.substring(chatMsg.indexOf(":") + 1, chatMsg.length()), MessageInfo.class);
                                    chatMsg = messageInfo1.getTitle();
                                    messageInfo = messageInfo1;
                                    redpacket = "[" + context.getString(R.string.location) + "]";
                                    msgType = FROM_LOCATION_MSG;
                                    fileName = UtilTool.createtFileName() + ".jpg";
                                    path = context.getFilesDir().getAbsolutePath() + File.separator + "images";
                                    goChat(from, context.getString(R.string.location));
                                } else if (chatMsg.contains("[Image]")) {
                                    String key = chatMsg.substring(chatMsg.indexOf(":") + 1, chatMsg.length());

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

                                    chatMsg = url;
                                    redpacket = "[" + context.getString(R.string.image) + "]";
                                    msgType = FROM_IMG_MSG;
                                    fileName = UtilTool.createtFileName() + ".jpg";
                                    path = context.getFilesDir().getAbsolutePath() + File.separator
                                            + "images";
                                    goChat(from, context.getString(R.string.image));
                                } else if (chatMsg.contains("[Video]")) {
                                    String key = chatMsg.substring(chatMsg.indexOf(":") + 1, chatMsg.length());

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

                                    chatMsg = url;
                                    redpacket = "[" + context.getString(R.string.video) + "]";
                                    msgType = FROM_VIDEO_MSG;
                                    fileName = UtilTool.createtFileName() + ".mp4";
                                    path = context.getFilesDir().getAbsolutePath() + File.separator
                                            + "images";
                                    goChat(from, context.getString(R.string.video));
                                }
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
                            if (file != null) {
                                messageInfo.setVoice(file.getAbsolutePath());
                                if (msgType == FROM_VOICE_MSG)
                                    messageInfo.setVoiceTime(UtilTool.getFileDuration(file.getAbsolutePath(), context) + "");
                            }
                        } else if (chatMsg.contains(Constants.REDBAG)) {
                            String s = chatMsg.replace(Constants.CHUANCODE, ",");
                            String[] split = s.split(",");
                            remark = split[1];
                            coin = split[2];
                            count = split[3];
                            msgType = FROM_RED_MSG;
                            redId = Integer.parseInt(split[4]);
                            redpacket = "[" + context.getString(R.string.red_package) + "]";
                            goChat(from, remark);
                        } else if (chatMsg.contains(Constants.TRANSFER)) {
                            String s = chatMsg.replace(Constants.CHUANCODE, ",");
                            String[] split = s.split(",");
                            remark = split[1];
                            coin = split[2];
                            count = split[3];
                            msgType = FROM_TRANSFER_MSG;
                            redpacket = "[" + context.getString(R.string.transfer) + "]";
                            goChat(from, remark);
                        } else if (chatMsg.contains(Constants.OTC_ORDER)) {
                            EventBus.getDefault().post(new MessageEvent(context.getString(R.string.order_update)));
                            msgType = ADMINISTRATOR_OTC_ORDER_MSG;
                            String json = chatMsg.substring(chatMsg.indexOf(":") + 1, chatMsg.length());
                            Gson gson = new Gson();
                            OtcOrderStatusInfo otcOrderStatusInfo = gson.fromJson(json, OtcOrderStatusInfo.class);
                            if (otcOrderStatusInfo.getStatus() == 1) {
                                Intent intent = new Intent(context, OrderDetailsActivity.class);
                                intent.putExtra("type", context.getString(R.string.order));
                                intent.putExtra("id", otcOrderStatusInfo.getId() + "");
                                mResultIntent = PendingIntent.getActivity(context, 1, intent,
                                        PendingIntent.FLAG_UPDATE_CURRENT);
                            } else {
                                Intent intent = new Intent(context, OrderCloseActivity.class);
                                intent.putExtra("status", otcOrderStatusInfo.getStatus() + "");
                                intent.putExtra("id", otcOrderStatusInfo.getId() + "");
                                mResultIntent = PendingIntent.getActivity(context, 1, intent,
                                        PendingIntent.FLAG_UPDATE_CURRENT);
                            }
                            mBuilder.setSmallIcon(R.mipmap.logo);
                            mBuilder.setContentTitle(context.getString(R.string.order_inform));
                            mBuilder.setContentText(context.getString(R.string.order_inform_hint));
                            mBuilder.setContentIntent(mResultIntent);
                            mBuilder.setDefaults(Notification.DEFAULT_ALL);
                            mBuilder.setAutoCancel(true);
                            Notification notification = mBuilder.build();
                            mNotificationManager.notify(0, notification);
                            time = otcOrderStatusInfo.getCreated_at();
                            redId = otcOrderStatusInfo.getId();
                            count = otcOrderStatusInfo.getOrder_no();
                            coin = otcOrderStatusInfo.getCoin_name();
                            status = otcOrderStatusInfo.getStatus();
                            type = otcOrderStatusInfo.getType();
                            redpacket = "[" + context.getString(R.string.order) + "]";
                        } else if (chatMsg.contains(Constants.RED_PACKET_EXPIRED)) {
                            msgType = ADMINISTRATOR_RED_PACKET_EXPIRED_MSG;
                            String json = chatMsg.substring(chatMsg.indexOf(":") + 1, chatMsg.length());
                            Gson gson = new Gson();
                            RedExpiredInfo redExpiredInfo = gson.fromJson(json, RedExpiredInfo.class);
                            time = redExpiredInfo.getCreated_at();
                            redId = redExpiredInfo.getId();
                            count = redExpiredInfo.getNumber();
                            coin = redExpiredInfo.getCoin_name();
                            status = redExpiredInfo.getRp_type();
                            redpacket = "[" + context.getString(R.string.red_expired) + "]";
                        } else if (chatMsg.contains(Constants.AUTH_STATUS)) {
                            msgType = ADMINISTRATOR_AUTH_STATUS_MSG;
                            String json = chatMsg.substring(chatMsg.indexOf(":") + 1, chatMsg.length());
                            Gson gson = new Gson();
                            AuthStatusInfo authStatusInfo = gson.fromJson(json, AuthStatusInfo.class);
                            if (authStatusInfo.getStatus() == 3) {
                                MySharedPreferences.getInstance().setString(STATE, authStatusInfo.getCountry());
                                MySharedPreferences.getInstance().setString(CURRENCY, authStatusInfo.getCurrency());
                            }
                            time = authStatusInfo.getCreated_at();
                            status = authStatusInfo.getStatus();
                            redpacket = "[" + context.getString(R.string.real_name_verify) + "]";
                        } else if (chatMsg.contains(Constants.QRCODE_RECEIPT_PAYMENT)) {
                            msgType = ADMINISTRATOR_RECEIPT_PAY_MSG;
                            String json = chatMsg.substring(chatMsg.indexOf(":") + 1, chatMsg.length());
                            Gson gson = new Gson();
                            QrcodeReceiptPayInfo qrcodeReceiptPayInfo = gson.fromJson(json, QrcodeReceiptPayInfo.class);
                            time = qrcodeReceiptPayInfo.getCreated_at();
                            redId = qrcodeReceiptPayInfo.getId();
                            count = qrcodeReceiptPayInfo.getNumber();
                            coin = qrcodeReceiptPayInfo.getCoin_name();
                            status = qrcodeReceiptPayInfo.getType();
                            remark = qrcodeReceiptPayInfo.getName();
                            type = qrcodeReceiptPayInfo.getType_number();
                            if (qrcodeReceiptPayInfo.getType() == 1) {
                                redpacket = "[" + context.getString(R.string.receipt_inform) + "]";
                            } else {
                                redpacket = "[" + context.getString(R.string.pay_inform) + "]";
                            }
                        } else if (chatMsg.contains(Constants.TRANSFER_INFORM)) {
                            msgType = ADMINISTRATOR_TRANSFER_MSG;
                            String json = chatMsg.substring(chatMsg.indexOf(":") + 1, chatMsg.length());
                            Gson gson = new Gson();
                            QrcodeReceiptPayInfo transferInformInfo = gson.fromJson(json, QrcodeReceiptPayInfo.class);
                            if (transferInformInfo.getType() == 1) {
                                Intent intent = new Intent(context, PayDetailsActivity.class);
                                intent.putExtra("id", transferInformInfo.getId() + "");
                                intent.putExtra("type_number", transferInformInfo.getType_number() + "");
                                mResultIntent = PendingIntent.getActivity(context, 1, intent,
                                        PendingIntent.FLAG_UPDATE_CURRENT);
                                mBuilder.setSmallIcon(R.mipmap.logo);
                                mBuilder.setContentTitle(context.getString(R.string.transfer_inform));
                                mBuilder.setContentText(context.getString(R.string.transfer_inform_hint));
                                mBuilder.setContentIntent(mResultIntent);
                                mBuilder.setDefaults(Notification.DEFAULT_ALL);
                                mBuilder.setAutoCancel(true);
                                Notification notification = mBuilder.build();
                                mNotificationManager.notify(0, notification);
                            }
                            time = transferInformInfo.getCreated_at();
                            redId = transferInformInfo.getId();
                            count = transferInformInfo.getNumber();
                            coin = transferInformInfo.getCoin_name();
                            status = transferInformInfo.getType();
                            remark = transferInformInfo.getName();
                            type = transferInformInfo.getType_number();
                            if (transferInformInfo.getType() == 1) {
                                redpacket = "[" + context.getString(R.string.in_account_inform) + "]";
                            } else {
                                redpacket = "[" + context.getString(R.string.transfer_inform) + "]";
                            }
                        } else if (chatMsg.contains(Constants.INOUT_COIN_INFORM)) {
                            msgType = ADMINISTRATOR_IN_OUT_COIN_MSG;
                            String json = chatMsg.substring(chatMsg.indexOf(":") + 1, chatMsg.length());
                            Gson gson = new Gson();
                            QrcodeReceiptPayInfo transferInformInfo = gson.fromJson(json, QrcodeReceiptPayInfo.class);
                            Intent intent = new Intent(context, PayDetailsActivity.class);
                            intent.putExtra("id", transferInformInfo.getId() + "");
                            intent.putExtra("type_number", transferInformInfo.getType_number() + "");
                            mResultIntent = PendingIntent.getActivity(context, 1, intent,
                                    PendingIntent.FLAG_UPDATE_CURRENT);
                            mBuilder.setSmallIcon(R.mipmap.logo);
                            mBuilder.setContentTitle(context.getString(R.string.out_coin_inform));
                            mBuilder.setContentText(context.getString(R.string.out_coin_inform_hint));
                            mBuilder.setContentIntent(mResultIntent);
                            mBuilder.setDefaults(Notification.DEFAULT_ALL);
                            mBuilder.setAutoCancel(true);
                            Notification notification = mBuilder.build();
                            mNotificationManager.notify(0, notification);
                            time = transferInformInfo.getCreated_at();
                            redId = transferInformInfo.getId();
                            count = transferInformInfo.getNumber();
                            coin = transferInformInfo.getCoin_name();
                            type = transferInformInfo.getType_number();
                            redpacket = "[" + context.getString(R.string.out_coin_inform) + "]";
                        } else {
                            goChat(from, chatMsg);
                        }
                        //添加数据库from
                        messageInfo.setUsername(from);
                        messageInfo.setMessage(chatMsg);
                        messageInfo.setTime(time);
                        messageInfo.setType(type);
                        messageInfo.setCount(count);
                        messageInfo.setCoin(coin);
                        messageInfo.setMsgType(msgType);
                        messageInfo.setRemark(remark);
                        messageInfo.setStatus(status);
                        messageInfo.setRedId(redId);
                        messageInfo.setSend(from);
                        mgr.addMessage(messageInfo);
                        int number = mgr.queryNumber(from);
                        if (mgr.findConversation(from)) {
                            mgr.updateConversation(from, number + 1, redpacket, time);
                        } else {
                            ConversationInfo info = new ConversationInfo();
                            info.setTime(time);
                            info.setFriend(friend);
                            info.setUser(from);
                            info.setNumber(1);
                            info.setMessage(redpacket);
                            if (message.getType() == Message.Type.chat) {
                                info.setChatType(RoomManage.ROOM_TYPE_SINGLE);
                            } else {
                                info.setChatType(RoomManage.ROOM_TYPE_MULTI);
                            }
                            mgr.addConversation(info);
                        }
                        EventBus.getDefault().post(new MessageEvent(context.getString(R.string.msg_database_update)));
                        EventBus.getDefault().post(new MessageEvent(context.getString(R.string.dispose_unread_msg)));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }

    private void goChat(String from, String message) {
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
        bundle.putString("name", from.split("@")[0]);
        bundle.putString("user", from);
        intent.putExtras(bundle);
        mResultIntent = PendingIntent.getActivity(context, 1, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setSmallIcon(R.mipmap.logo);

        String remark = mgr.queryRemark(from);
        if (!StringUtils.isEmpty(remark)) {
            mBuilder.setContentTitle(remark);
        } else {
            mBuilder.setContentTitle(from.split("@")[0]);
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