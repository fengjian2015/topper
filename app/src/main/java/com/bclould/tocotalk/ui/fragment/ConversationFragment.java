package com.bclould.tocotalk.ui.fragment;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.bclould.tocotalk.R;
import com.bclould.tocotalk.base.MyApp;
import com.bclould.tocotalk.crypto.otr.OtrChatListenerManager;
import com.bclould.tocotalk.crypto.otr.OtrChatManager;
import com.bclould.tocotalk.history.DBManager;
import com.bclould.tocotalk.model.AuthStatusInfo;
import com.bclould.tocotalk.model.ConversationInfo;
import com.bclould.tocotalk.model.MessageInfo;
import com.bclould.tocotalk.model.OtcOrderStatusInfo;
import com.bclould.tocotalk.model.QrcodeReceiptPayInfo;
import com.bclould.tocotalk.model.RedExpiredInfo;
import com.bclould.tocotalk.ui.activity.OrderCloseActivity;
import com.bclould.tocotalk.ui.activity.OrderDetailsActivity;
import com.bclould.tocotalk.ui.activity.PayDetailsActivity;
import com.bclould.tocotalk.ui.adapter.ConversationAdapter;
import com.bclould.tocotalk.ui.widget.LoadingProgressDialog;
import com.bclould.tocotalk.utils.Constants;
import com.bclould.tocotalk.utils.MessageEvent;
import com.bclould.tocotalk.utils.MySharedPreferences;
import com.bclould.tocotalk.utils.UtilTool;
import com.bclould.tocotalk.xmpp.XMConnectionListener;
import com.bclould.tocotalk.xmpp.XmppConnection;
import com.google.gson.Gson;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.chat.ChatManagerListener;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.filter.StanzaFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smackx.offline.OfflineMessageManager;
import org.jivesoftware.smackx.ping.PingFailedListener;
import org.jivesoftware.smackx.ping.PingManager;
import org.jxmpp.jid.impl.JidCreate;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

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
import static com.bclould.tocotalk.ui.adapter.ChatAdapter.FROM_RED_MSG;
import static com.bclould.tocotalk.ui.adapter.ChatAdapter.FROM_TEXT_MSG;
import static com.bclould.tocotalk.ui.adapter.ChatAdapter.FROM_TRANSFER_MSG;
import static com.bclould.tocotalk.ui.adapter.ChatAdapter.FROM_VIDEO_MSG;
import static com.bclould.tocotalk.ui.adapter.ChatAdapter.FROM_VOICE_MSG;
import static com.bclould.tocotalk.utils.Constants.ACCESS_KEY_ID;
import static com.bclould.tocotalk.utils.Constants.SECRET_ACCESS_KEY;
import static com.bclould.tocotalk.utils.Constants.SESSION_TOKEN;
import static com.bclould.tocotalk.utils.MySharedPreferences.SETTING;

/**
 * Created by GA on 2017/12/12.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class ConversationFragment extends Fragment {

    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @Bind(R.id.iv)
    ImageView mIv;
    @Bind(R.id.ll_no_data)
    LinearLayout mLlNoData;
    @Bind(R.id.rl_ununited)
    RelativeLayout mRlUnunited;
    @Bind(R.id.iv_warning)
    ImageView mIvWarning;
    @Bind(R.id.refresh_layout)
    SmartRefreshLayout mRefreshLayout;
    private FragmentOneHandler handler;
    private List<Map<String, Object>> list = new ArrayList<>();
    private List<ConversationInfo> showlist = new ArrayList<>();
    private DBManager mgr;
    public static ConversationFragment instance = null;
    private ConversationAdapter mConversationAdapter;
    private MyReceiver receiver;
    private LoadingProgressDialog mProgressDialog;
    private NotificationManager mNotificationManager;
    private PendingIntent mResultIntent;
    private NotificationCompat.Builder mBuilder;

    public static ConversationFragment getInstance() {

        if (instance == null) {

            instance = new ConversationFragment();

        }

        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_conversation_list, container, false);
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
        if (receiver == null) {
            receiver = new MyReceiver();
            IntentFilter intentFilter = new IntentFilter("XMPPConnectionListener");
            getActivity().registerReceiver(receiver, intentFilter);
        }
        ButterKnife.bind(this, view);
        pullToRefresh();
        mgr = new DBManager(getActivity());
        handler = new FragmentOneHandler();
        initRecyclerView();
        initData();
//        initAWS();
        initNotification();
        return view;
    }

    private void initNotification() {
        mNotificationManager = (NotificationManager) getContext().getSystemService(NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(getContext());
    }


    private void pullToRefresh() {
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                refreshlayout.finishRefresh(2000);
                if (XmppConnection.getInstance().getConnection() != null) {
                    if (!XmppConnection.getInstance().getConnection().isAuthenticated()) {
                        loginIM();
                    }
                }
            }
        });
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    Toast.makeText(getContext(), getString(R.string.toast_network_error), Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    Intent intent = new Intent();
                    intent.setAction("XMPPConnectionListener");
                    intent.putExtra("type", true);
                    getContext().sendBroadcast(intent);
                    pingService();
                    break;
            }
        }
    };

    private Timer tExit;

    private void pingService() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                PingManager pingManager = PingManager.getInstanceFor(XmppConnection.getInstance().getConnection());
                pingManager.setPingInterval(60);
                try {
                    pingManager.pingMyServer();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                pingManager.registerPingFailedListener(new PingFailedListener() {
                    @Override
                    public void pingFailed() {
                        if (tExit == null) {
                            tExit = new Timer();
                            tExit.schedule(new TimeTask(), 1000);
                        }
                    }
                });
            }
        }).start();
    }

    private class TimeTask extends TimerTask {
        @Override
        public void run() {

            if (UtilTool.getUser() != null && UtilTool.getpw() != null) {
                Log.i("XMConnectionListener", "尝试登录");
                // 连接服务器
                try {
                    if (!XmppConnection.getInstance().isAuthenticated()) {// 用户未登录
                        if (XmppConnection.getInstance().login(UtilTool.getUser(), UtilTool.getpw())) {
                            Log.i("XMConnectionListener", "登录成功");
                            EventBus.getDefault().post(new MessageEvent(getString(R.string.login_succeed)));
                            Intent intent = new Intent();
                            intent.setAction("XMPPConnectionListener");
                            intent.putExtra("type", true);
                            getContext().sendBroadcast(intent);
                        } else {
                            Log.i("XMConnectionListener", "重新登录");
                            tExit.schedule(new TimeTask(), 1000);
                        }
                    }
                } catch (Exception e) {
                    Log.i("XMConnectionListener", "尝试登录,出现异常!");
                    Log.i("XMConnectionListener", e.getMessage());
                }
            }
        }
    }

    //登录即时通讯
    private void loginIM() {
//        showDialog();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //连接openfile
                    AbstractXMPPConnection connection = XmppConnection.getInstance().getConnection();
                    //判断是否连接
                    if (connection != null && connection.isConnected()) {
                        String myUser = UtilTool.getJid();
                        String user = myUser.substring(0, myUser.indexOf("@"));
                        connection.login(user, UtilTool.getpw());
                        connection.addConnectionListener(new XMConnectionListener(getContext()));
                        /*if (connection.isAuthenticated()) {//登录成功
                            PingManager.setDefaultPingInterval(10);
                            PingManager myPingManager = PingManager.getInstanceFor(connection);
                            myPingManager.registerPingFailedListener(new PingFailedListener() {
                                @Override
                                public void pingFailed() {
                                    Toast.makeText(MainActivity.this, "发送心跳包失败", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }*/
                        //登录成功发送通知
                        EventBus.getDefault().post(new MessageEvent(getString(R.string.login_succeed)));
                        UtilTool.Log("fsdafa", "登录成功");
                        android.os.Message message = new android.os.Message();
                        message.what = 1;
                        mHandler.sendMessage(message);
                    }
                } catch (Exception e) {
                    //发送登录失败通知
                    EventBus.getDefault().post(new MessageEvent(getString(R.string.login_error)));
                    android.os.Message message = new android.os.Message();
                    mHandler.sendMessage(message);
                    message.what = 0;
                    UtilTool.Log("日志", e.getMessage());
                    e.printStackTrace();
                }
            }
        }).start();
    }


    @OnClick(R.id.rl_ununited)
    public void onViewClicked() {
        startActivity(new Intent(Settings.ACTION_SETTINGS));
    }

    //广播接收器
    public class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            boolean type = intent.getBooleanExtra("type", false);
            if (mRlUnunited != null) {
                if (type) {
                    mRlUnunited.setVisibility(View.GONE);
                } else {
                    mRlUnunited.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        String msg = event.getMsg();
        if (msg.equals(getString(R.string.login_succeed))) {
            initBroadcastListener();
            getOfflineMessage();
//            initListener();
            initData();
        } else if (msg.equals(getString(R.string.oneself_send_msg))) {
            initData();
        } else if (msg.equals(getString(R.string.send_red_packet_le))) {
            initData();
        } else if (msg.equals(getString(R.string.dispose_unread_msg))) {
            initData();
        } else if (msg.equals(getString(R.string.new_friend))) {
            initData();
        } else if (msg.equals(getString(R.string.login_error))) {
            mRlUnunited.setVisibility(View.VISIBLE);
        }else if(msg.equals(getString(R.string.message_top_change))){
            initData();
        }

    }

    private void initBroadcastListener() {
        AbstractXMPPConnection connection = XmppConnection.getInstance().getConnection();
        connection.addAsyncStanzaListener(new StanzaListener() {
            @Override
            public void processStanza(Stanza packet) throws SmackException.NotConnectedException, InterruptedException {
                Message message = (Message) packet;
                if (message.getBody() != null) {
                    android.os.Message msg = new android.os.Message();
                    msg.obj = message;
                    handler.sendMessage(msg);
                    SharedPreferences sp = getContext().getSharedPreferences(SETTING, 0);
                    if (sp.contains(INFORM)) {
                        if (MySharedPreferences.getInstance().getBoolean(INFORM)) {
                            UtilTool.playHint(getContext());
                        }
                    } else {
                        UtilTool.playHint(getContext());
                    }
                }
            }
        }, new StanzaFilter() {
            @Override
            public boolean accept(Stanza stanza) {
                return stanza instanceof Message;
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void initData() {
        if (mRecyclerView != null) {
            List<ConversationInfo> conversationInfos = mgr.queryConversation();
            if (conversationInfos.size() == 0) {
                mLlNoData.setVisibility(View.VISIBLE);
                mRecyclerView.setVisibility(View.GONE);
            } else {
                mLlNoData.setVisibility(View.GONE);
                mRecyclerView.setVisibility(View.VISIBLE);
            }
            showlist.removeAll(showlist);
            showlist.addAll(conversationInfos);
            sort();
            initRecyclerView();
        }
    }

    private void initRecyclerView() {
        mConversationAdapter = new ConversationAdapter(this, getActivity(), getSimpleData(), mgr);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mConversationAdapter);
    }

    private void getOfflineMessage() {
        OfflineMessageManager offlineManager = new OfflineMessageManager(XmppConnection.getInstance().getConnection());
        try {
            List<Message> list = offlineManager.getMessages();
            if (list.size() != 0) {
                SharedPreferences sp = getContext().getSharedPreferences(SETTING, 0);
                if (sp.contains(INFORM)) {
                    if (MySharedPreferences.getInstance().getBoolean(INFORM)) {
                        UtilTool.playHint(getContext());
                    }
                } else {
                    UtilTool.playHint(getContext());
                }
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i) != null && list.get(i).getBody() != null && !list.get(i).getBody().equals("")) {
                        android.os.Message msg = new android.os.Message();
                        msg.obj = list.get(i);
                        handler.sendMessage(msg);
                    }
                }
                //删除离线消息
                offlineManager.deleteMessages();
            }
            //将状态设置成在线
            Presence presence = new Presence(Presence.Type.available);
            XmppConnection.getInstance().getConnection().sendStanza(presence);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
                //当消息返回为空的时候，表示用户正在聊天窗口编辑信息并未发出消
                if (!TextUtils.isEmpty(message.getBody())) {
                    //message为用户所收到的消息
                    android.os.Message msg = new android.os.Message();
                    msg.obj = message;
                    handler.sendMessage(msg);
                    SharedPreferences sp = getContext().getSharedPreferences(SETTING, 0);
                    if (sp.contains(INFORM)) {
                        if (MySharedPreferences.getInstance().getBoolean(INFORM)) {
                            UtilTool.playHint(getContext());
                        }
                    } else {
                        UtilTool.playHint(getContext());
                    }
                }
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

    private List<ConversationInfo> getSimpleData() {
        return showlist;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    public class FragmentOneHandler extends Handler {
        @SuppressLint("WrongConstant")
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
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

                if(OtrChatListenerManager.getInstance().isOtrEstablishMessage(chatMsg,
                        OtrChatListenerManager.getInstance().sessionID(Constants.MYUSER, from),getContext())){
                    return;
                }
                if(OtrChatListenerManager.getInstance().isExist(OtrChatListenerManager.getInstance().sessionID(Constants.MYUSER, from))){
                    chatMsg=OtrChatListenerManager.getInstance().receivedMessagesChange(chatMsg,
                            OtrChatListenerManager.getInstance().sessionID(Constants.MYUSER, from));
                }
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
                            redpacket = "[" + getString(R.string.voice) + "]";
                            msgType = FROM_VOICE_MSG;
                            fileName = UtilTool.createtFileName() + ".amr";
                            path = getContext().getFilesDir().getAbsolutePath() + File.separator
                                    + "RecordRemDir";
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
                            redpacket = "[" + getString(R.string.image) + "]";
                            msgType = FROM_IMG_MSG;
                            fileName = UtilTool.createtFileName() + ".jpg";
                            path = getContext().getFilesDir().getAbsolutePath() + File.separator
                                    + "images";
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
                            redpacket = "[" + getString(R.string.video) + "]";
                            msgType = FROM_VIDEO_MSG;
                            fileName = UtilTool.createtFileName() + ".mp4";
                            path = getContext().getFilesDir().getAbsolutePath() + File.separator
                                    + "images";
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
                            messageInfo.setVoiceTime(UtilTool.getFileDuration(file.getAbsolutePath(), getContext()) + "");
                    }
                } else if (chatMsg.contains(Constants.REDBAG)) {
                    String s = chatMsg.replace(Constants.CHUANCODE, ",");
                    String[] split = s.split(",");
                    remark = split[1];
                    coin = split[2];
                    count = split[3];
                    msgType = FROM_RED_MSG;
                    redId = Integer.parseInt(split[4]);
                    redpacket = "[" + getString(R.string.red_package) + "]";
                } else if (chatMsg.contains(Constants.TRANSFER)) {
                    String s = chatMsg.replace(Constants.CHUANCODE, ",");
                    String[] split = s.split(",");
                    remark = split[1];
                    coin = split[2];
                    count = split[3];
                    msgType = FROM_TRANSFER_MSG;
                    redpacket = "[" + getString(R.string.transfer) + "]";
                } else if (chatMsg.contains(Constants.OTC_ORDER)) {
                    EventBus.getDefault().post(new MessageEvent(getString(R.string.order_update)));
                    msgType = ADMINISTRATOR_OTC_ORDER_MSG;
                    String json = chatMsg.substring(chatMsg.indexOf(":") + 1, chatMsg.length());
                    Gson gson = new Gson();
                    OtcOrderStatusInfo otcOrderStatusInfo = gson.fromJson(json, OtcOrderStatusInfo.class);
                    if (otcOrderStatusInfo.getStatus() == 1) {
                        Intent intent = new Intent(getContext(), OrderDetailsActivity.class);
                        intent.putExtra("type", getString(R.string.order));
                        intent.putExtra("id", otcOrderStatusInfo.getId() + "");
                        mResultIntent = PendingIntent.getActivity(getContext(), 1, intent,
                                PendingIntent.FLAG_UPDATE_CURRENT);
                    } else {
                        Intent intent = new Intent(getContext(), OrderCloseActivity.class);
                        intent.putExtra("status", otcOrderStatusInfo.getStatus() + "");
                        intent.putExtra("id", otcOrderStatusInfo.getId() + "");
                        mResultIntent = PendingIntent.getActivity(getContext(), 1, intent,
                                PendingIntent.FLAG_UPDATE_CURRENT);
                    }
                    mBuilder.setSmallIcon(R.mipmap.logo);
                    mBuilder.setContentTitle(getString(R.string.order_inform));
                    mBuilder.setContentText(getString(R.string.order_inform_hint));
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
                    redpacket = "[" + getString(R.string.order) + "]";
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
                    redpacket = "[" + getString(R.string.red_expired) + "]";
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
                    redpacket = "[" + getString(R.string.real_name_verify) + "]";
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
                        redpacket = "[" + getString(R.string.receipt_inform) + "]";
                    } else {
                        redpacket = "[" + getString(R.string.pay_inform) + "]";
                    }
                } else if (chatMsg.contains(Constants.TRANSFER_INFORM)) {
                    msgType = ADMINISTRATOR_TRANSFER_MSG;
                    String json = chatMsg.substring(chatMsg.indexOf(":") + 1, chatMsg.length());
                    Gson gson = new Gson();
                    QrcodeReceiptPayInfo transferInformInfo = gson.fromJson(json, QrcodeReceiptPayInfo.class);
                    if (transferInformInfo.getType() == 1) {
                        Intent intent = new Intent(getContext(), PayDetailsActivity.class);
                        intent.putExtra("id", transferInformInfo.getId() + "");
                        intent.putExtra("type_number", transferInformInfo.getType_number() + "");
                        mResultIntent = PendingIntent.getActivity(getContext(), 1, intent,
                                PendingIntent.FLAG_UPDATE_CURRENT);
                        mBuilder.setSmallIcon(R.mipmap.logo);
                        mBuilder.setContentTitle(getString(R.string.transfer_inform));
                        mBuilder.setContentText(getString(R.string.transfer_inform_hint));
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
                        redpacket = "[" + getString(R.string.in_account_inform) + "]";
                    } else {
                        redpacket = "[" + getString(R.string.transfer_inform) + "]";
                    }
                } else if (chatMsg.contains(Constants.INOUT_COIN_INFORM)) {
                    msgType = ADMINISTRATOR_IN_OUT_COIN_MSG;
                    String json = chatMsg.substring(chatMsg.indexOf(":") + 1, chatMsg.length());
                    Gson gson = new Gson();
                    QrcodeReceiptPayInfo transferInformInfo = gson.fromJson(json, QrcodeReceiptPayInfo.class);
                    Intent intent = new Intent(getContext(), PayDetailsActivity.class);
                    intent.putExtra("id", transferInformInfo.getId() + "");
                    intent.putExtra("type_number", transferInformInfo.getType_number() + "");
                    mResultIntent = PendingIntent.getActivity(getContext(), 1, intent,
                            PendingIntent.FLAG_UPDATE_CURRENT);
                    mBuilder.setSmallIcon(R.mipmap.logo);
                    mBuilder.setContentTitle(getString(R.string.out_coin_inform));
                    mBuilder.setContentText(getString(R.string.out_coin_inform_hint));
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
                    redpacket = "[" + getString(R.string.out_coin_inform) + "]";
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
                    mgr.addConversation(info);
                }
                EventBus.getDefault().post(new MessageEvent(getString(R.string.msg_database_update)));
                initData();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private void downloadFile(final String key, final String fileName, final String path) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    BasicSessionCredentials sessionCredentials = new BasicSessionCredentials(
                            ACCESS_KEY_ID,
                            SECRET_ACCESS_KEY,
                            SESSION_TOKEN);

                    AmazonS3Client s3Client = new AmazonS3Client(
                            sessionCredentials);
                    Regions regions = Regions.fromName("ap-northeast-2");
                    Region region = Region.getRegion(regions);
                    s3Client.setRegion(region);
                    GetObjectRequest gor = new GetObjectRequest(Constants.BUCKET_NAME, key);
                    File file = new File(Environment.getExternalStorageDirectory().getPath(), fileName);
                    ObjectMetadata object = s3Client.getObject(gor, file);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    private void sort() {
        //将showlist按时间排成倒序
        Collections.sort(showlist, new Comparator<ConversationInfo>() {
            @Override
            public int compare(ConversationInfo conversationInfo, ConversationInfo conversationInfo2) {
                String at = conversationInfo.getTime();
                String bt = conversationInfo2.getTime();
                String atop=conversationInfo.getIstop();
                String btop=conversationInfo2.getIstop();
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                try {
                    if("true".equals(btop)&&!"true".equals(atop)){
                        return 1;
                    }else if(!"true".equals(btop)&&"true".equals(atop)){
                        return -1;
                    }else if (formatter.parse(bt).getTime() > formatter.parse(at).getTime()) {
                        return 1;
                    } else if (formatter.parse(bt).getTime() < formatter.parse(at).getTime()) {
                        return -1;
                    } else {
                        return 0;
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return 0;
            }
        });
    }
}
