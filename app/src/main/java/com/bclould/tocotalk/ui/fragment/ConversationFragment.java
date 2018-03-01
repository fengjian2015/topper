package com.bclould.tocotalk.ui.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.bclould.tocotalk.R;
import com.bclould.tocotalk.history.DBManager;
import com.bclould.tocotalk.model.ConversationInfo;
import com.bclould.tocotalk.model.MessageInfo;
import com.bclould.tocotalk.ui.adapter.ConversationAdapter;
import com.bclould.tocotalk.utils.Constants;
import com.bclould.tocotalk.utils.MessageEvent;
import com.bclould.tocotalk.utils.UtilTool;
import com.bclould.tocotalk.xmpp.XmppConnection;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.chat.ChatManagerListener;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.offline.OfflineMessageManager;

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

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by GA on 2017/12/12.
 */

public class ConversationFragment extends Fragment {

    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @Bind(R.id.iv)
    ImageView mIv;
    @Bind(R.id.ll_no_data)
    LinearLayout mLlNoData;
    @Bind(R.id.rl_ununited)
    RelativeLayout mRlUnunited;
    private FragmentOneHandler handler;
    private List<Map<String, Object>> list = new ArrayList<>();
    private List<ConversationInfo> showlist = new ArrayList<>();
    private DBManager mgr;
    public static ConversationFragment instance = null;
    private ConversationAdapter mConversationAdapter;
    private MyReceiver receiver;

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
        mgr = new DBManager(getActivity());
        handler = new FragmentOneHandler();
        initRecyclerView();
        initData();
        return view;
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
            if (type) {
                mRlUnunited.setVisibility(View.GONE);
            } else {
                mRlUnunited.setVisibility(View.VISIBLE);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        String msg = event.getMsg();
        if (msg.equals("登录成功")) {
            getOfflineMessage();
            initListener();
            initData();
        } else if (msg.equals("自己发了消息")) {
            initData();
        } else if (msg.equals("发红包了")) {
            initData();
        } else if (msg.equals("处理未读消息")) {
            initData();
        } else if (msg.equals("新的好友")) {
            initData();
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void initData() {
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
//        mConversationAdapter.notifyDataSetChanged();
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
                UtilTool.playHint(getContext());
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i) != null && list.get(i).getBody() != null && !list.get(i).getBody().equals("null")) {
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
                    UtilTool.playHint(getContext());
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
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            //获取接收的好友名及聊天消息
            Message message = (Message) msg.obj;
            String msgXML = message.toXML().toString();
            String startTag = "<attachment xmlns='jabber:client' xmlns:stream='http://etherx.jabber.org/streams'>&lt;";
            String endTag = "</attachment>";
            UtilTool.Log("语音", msgXML);
            try {
                if (msgXML.contains(startTag)) {
                    String voiceBase64 = msgXML.substring(msgXML.indexOf(startTag) + startTag.length(), msgXML.length());
//                    byte[] bytes = Base64.decode(voiceBase64, Base64.DEFAULT);
                    byte[] bytes = voiceBase64.getBytes();
                    if (bytes != null && bytes.length != 0) {
                        InputStream in = new ByteArrayInputStream(bytes);
                        String path = UtilTool.createtFileName() + ".wav";
                        File file = new File("/sdcard/", path);
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
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            String chatMsg = message.getBody();
            String remark = null;
            String coin = null;
            String count = null;
            int redId = 0;
            String redpacket = "";
            if (chatMsg.contains(Constants.CHUANCODE)) {
                String s = chatMsg.replace(Constants.CHUANCODE, ",");
                String[] split = s.split(",");
                remark = split[1];
                coin = split[2];
                count = split[3];
                redId = Integer.parseInt(split[4]);
                redpacket = "[" + coin + "红包]" + remark;
            } else {
                redpacket = chatMsg;
            }
            String from = message.getFrom().toString();
            if (from.contains("/"))
                from = from.substring(0, from.indexOf("/"));
            String friend = from.substring(0, from.indexOf("@"));

            //获取当前时间
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date curDate = new Date(System.currentTimeMillis());
            String time = formatter.format(curDate);

            //添加数据库
            MessageInfo messageInfo = new MessageInfo();
            messageInfo.setUsername(from);
            messageInfo.setMessage(chatMsg);
            messageInfo.setTime(time);
            messageInfo.setType(1);
            messageInfo.setCount(count);
            messageInfo.setCoin(coin);
            messageInfo.setRemark(remark);
            messageInfo.setState(0);
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
            EventBus.getDefault().post(new MessageEvent("消息数据库更新"));
            initData();
        }
    }

    private void sort() {
        //将showlist按时间排成倒序
        Collections.sort(showlist, new Comparator<ConversationInfo>() {
            @Override
            public int compare(ConversationInfo conversationInfo, ConversationInfo conversationInfo2) {
                String at = conversationInfo.getTime();
                String bt = conversationInfo2.getTime();
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                try {
                    if (formatter.parse(bt).getTime() > formatter.parse(at).getTime()) {
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
