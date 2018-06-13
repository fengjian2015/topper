package com.bclould.tocotalk.xmpp;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.bclould.tocotalk.R;
import com.bclould.tocotalk.history.DBManager;
import com.bclould.tocotalk.model.ConversationInfo;
import com.bclould.tocotalk.model.MessageInfo;
import com.bclould.tocotalk.model.UserInfo;
import com.bclould.tocotalk.service.IMCoreService;
import com.bclould.tocotalk.service.IMService;
import com.bclould.tocotalk.utils.Constants;
import com.bclould.tocotalk.utils.MessageEvent;
import com.bclould.tocotalk.utils.StringUtils;
import com.bclould.tocotalk.utils.UtilTool;

import org.greenrobot.eventbus.EventBus;
import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat2.Chat;
import org.jivesoftware.smack.chat2.ChatManager;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.roster.RosterGroup;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smack.util.stringencoder.Base64;
import org.jivesoftware.smackx.filetransfer.FileTransferManager;
import org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer;
import org.jivesoftware.smackx.iqregister.AccountManager;
import org.jivesoftware.smackx.muc.HostedRoom;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jivesoftware.smackx.offline.OfflineMessageManager;
import org.jivesoftware.smackx.search.ReportedData;
import org.jivesoftware.smackx.search.UserSearchManager;
import org.jivesoftware.smackx.vcardtemp.VCardManager;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;
import org.jivesoftware.smackx.xdata.Form;
import org.jivesoftware.smackx.xdata.FormField;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.EntityFullJid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.jid.parts.Localpart;
import org.jxmpp.jid.parts.Resourcepart;
import org.jxmpp.stringprep.XmppStringprepException;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

import static org.jivesoftware.smack.provider.ProviderManager.addIQProvider;

/**
 * XmppConnection 工具类
 */
@RequiresApi(api = Build.VERSION_CODES.N)
public class XmppConnection {
    private int SERVER_PORT = Constants.OPENFIRE_PORT;
    private String SERVER_HOST = Constants.DOMAINNAME2;
    private String SERVER_NAME = Constants.DOMAINNAME2;
    private AbstractXMPPConnection connection = null;
    private static XmppConnection xmppConnection = new XmppConnection();
    private XMConnectionListener connectionListener;
    private DBManager mMgr;
    private Context mContext;

    /**
     * 单例模式
     *
     * @return XmppConnection
     */
    public synchronized static XmppConnection getInstance() {
        return xmppConnection;
    }

    /**
     * 创建连接
     */
    public AbstractXMPPConnection getConnection() {
        if (connection == null||!connection.isConnected()) {
            synchronized (XmppConnection.this) {
                // 开线程打开连接，避免在主线程里面执行HTTP请求
                // Caused by: android.os.NetworkOnMainThreadException
//            openConnection();
                new Thread(){
                    @Override
                    public void run() {
                        openConnection();
                    }
                }.start();
            }
        }
        return connection;
    }

    public static void loginService(Context context) {
        Intent intent = new Intent();
        intent.setAction(IMCoreService.ACTION_LOGIN);
        context.sendBroadcast(intent);
//		IMCoreService.startService = true;
//		MyLogger.xuxLog().i("将静态startService变为了login-"+IMCoreService.startService);
    }

    //退出登錄用
    public static void logoutService(Context context) {
        Intent intent = new Intent();
        intent.setAction(IMCoreService.ACTION_LOGOUT);
        context.sendBroadcast(intent);
        context.stopService(new Intent(context, IMService.class));
        LoginThread.isStartExReconnect = false;
        stopAllIMCoreService(context);
        UtilTool.Log("fengjina","退出");
    }

    /***
     *
     * 通过广播去关闭service
     *
     */
    public static void stopAllIMCoreService(Context context){
        Intent intent = new Intent();
        intent.setAction(IMCoreService.ACTION_LOGOUT);
        context.sendBroadcast(intent);
    }

    /**
     * 判断是否已连接
     */
    public boolean checkConnection() {
        return null != connection && connection.isConnected();
    }

    /**
     * 打开连接
     */
    public AbstractXMPPConnection openConnection() {

        try {
            if (null == connection || !connection.isAuthenticated() || !connection.isConnected()) {
                SmackConfiguration.DEBUG = true;
                XMPPTCPConnectionConfiguration.Builder config = XMPPTCPConnectionConfiguration.builder();
                //设置openfire主机IP
                config.setHostAddress(InetAddress.getByName(Constants.DOMAINNAME2));
                //设置openfire服务器名称
                config.setXmppDomain(Constants.DOMAINNAME2);
                UtilTool.Log("服務器", Constants.DOMAINNAME2);
                //设置端口号：默认5288
                config.setPort(Constants.OPENFIRE_PORT);
//                config.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);

                SSLContext sslContext = SSLContext.getInstance("TLS");
                MyX509TrustManager myX509TrustManager = new MyX509TrustManager(mContext.getResources().getAssets().open("keystore.bks"), "changeit");
                sslContext.init(null, new TrustManager[]{myX509TrustManager},
                        new SecureRandom());
                config.setCustomSSLContext(sslContext);
//                config.setSecurityMode(ConnectionConfiguration.SecurityMode.required);
                config.setSecurityMode(ConnectionConfiguration.SecurityMode.ifpossible);
//                设置Debug

                config.setDebuggerEnabled(true);
                //设置离线状态
                config.setSendPresence(false);
                //设置开启压缩，可以节省流量
                config.setCompressionEnabled(true);

                //需要经过同意才可以添加好友
                Roster.setDefaultSubscriptionMode(Roster.SubscriptionMode.manual);

                // 将相应机制隐掉
                //SASLAuthentication.blacklistSASLMechanism("SCRAM-SHA-1");
                //SASLAuthentication.blacklistSASLMechanism("DIGEST-MD5");

                connection = new XMPPTCPConnection(config.build());
                connection.setReplyTimeout(10000);
                connection.connect();// 连接到服务器
                ProviderManager.addIQProvider("muc", "MZH", new MUCPacketExtensionProvider());
                UtilTool.Log("fsdafa", "连接成功");
            }
            return connection;
        } catch (Exception xe) {
            EventBus.getDefault().post(new MessageEvent(mContext.getString(R.string.login_error)));
            mHandler.sendEmptyMessage(0);
            UtilTool.Log("fsdafa", "连接失败 " + xe.getMessage());
            xe.printStackTrace();
        }
        return connection;
    }

    android.os.Handler mHandler = new android.os.Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            Intent intent = new Intent();
            intent.setAction("XMPPConnectionListener");
            intent.putExtra("type", false);
            mContext.sendBroadcast(intent);
        }
    };

    /**
     * 关闭连接
     */
    public void closeConnection() {
        if (connection != null) {
            // 移除连接监听
            connection.removeConnectionListener(connectionListener);
            if (connection.isConnected())
                connection.disconnect();
        }
        Log.i("XmppConnection", "关闭连接");
    }

    /**
     * 判断连接是否通过了身份验证
     * 即是否已登录
     *
     * @return
     */
    public boolean isAuthenticated() {
        return connection != null && connection.isConnected() && connection.isAuthenticated();
    }


    public XMConnectionListener getXMConnectionListener(){
        if(connectionListener==null){
            connectionListener=new XMConnectionListener(mContext);
        }
        return connectionListener;
    }

    /**
     * 注册
     *
     * @param account  注册帐号
     * @param password 注册密码
     * @return 1、注册成功 0、注册失败
     */
    public String register(String account, String password) {
        if (getConnection() == null)
            return "0";
        try {
            AccountManager.getInstance(connection).createAccount(Localpart.from(account), password);
        } catch (XmppStringprepException | InterruptedException | XMPPException | SmackException e) {
            e.printStackTrace();
            return "0";
        }

        return "1";
    }

    /**
     * 更改用户状态
     */
    public void setPresence(int code) {
        XMPPConnection con = getConnection();
        if (con == null)
            return;
        Presence presence;
        try {
            switch (code) {
                case 0:
                    presence = new Presence(Presence.Type.available);
                    con.sendStanza(presence);
                    Log.v("state", "设置在线");
                    break;
                case 1:
                    presence = new Presence(Presence.Type.available);
                    presence.setMode(Presence.Mode.chat);
                    con.sendStanza(presence);
                    Log.v("state", "设置Q我吧");
                    break;
                case 2:
                    presence = new Presence(Presence.Type.available);
                    presence.setMode(Presence.Mode.dnd);
                    con.sendStanza(presence);
                    Log.v("state", "设置忙碌");
                    break;
                case 3:
                    presence = new Presence(Presence.Type.available);
                    presence.setMode(Presence.Mode.away);
                    con.sendStanza(presence);
                    Log.v("state", "设置离开");
                    break;
                case 4:
//                    Roster roster = con.getRoster();
//                    Collection<RosterEntry> entries = roster.getEntries();
//                    for (RosterEntry entry : entries) {
//                        presence = new Presence(Presence.Type.unavailable);
//                        presence.setPacketID(Packet.ID_NOT_AVAILABLE);
//                        presence.setFrom(con.getUser());
//                        presence.setTo(entry.getUser());
//                        con.sendPacket(presence);
//                        Log.v("state", presence.toXML());
//                    }
//                    // 向同一用户的其他客户端发送隐身状态
//                    presence = new Presence(Presence.Type.unavailable);
//                    presence.setPacketID(Packet.ID_NOT_AVAILABLE);
//                    presence.setFrom(con.getUser());
//                    presence.setTo(StringUtils.parseBareAddress(con.getUser()));
//                    con.sendStanza(presence);
//                    Log.v("state", "设置隐身");
//                    break;
                case 5:
                    presence = new Presence(Presence.Type.unavailable);
                    con.sendStanza(presence);
                    Log.v("state", "设置离线");
                    break;
                default:
                    break;
            }
        } catch (SmackException.NotConnectedException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取所有组
     *
     * @return 所有组集合
     */
    public List<RosterGroup> getGroups() {
        if (getConnection() == null)
            return null;
        List<RosterGroup> groupList = new ArrayList<>();
        Collection<RosterGroup> rosterGroup = Roster.getInstanceFor(connection).getGroups();
        for (RosterGroup aRosterGroup : rosterGroup) {
            groupList.add(aRosterGroup);
        }
        return groupList;
    }

    /**
     * 获取某个组里面的所有好友
     *
     * @param groupName 组名
     * @return List<RosterEntry>
     */
    public List<RosterEntry> getEntriesByGroup(String groupName) {
        if (getConnection() == null)
            return null;
        List<RosterEntry> EntriesList = new ArrayList<>();
        RosterGroup rosterGroup = Roster.getInstanceFor(connection).getGroup(groupName);
        Collection<RosterEntry> rosterEntry = rosterGroup.getEntries();
        for (RosterEntry aRosterEntry : rosterEntry) {
            EntriesList.add(aRosterEntry);
        }
        return EntriesList;
    }

    /**
     * 获取所有好友信息
     *
     * @return List<RosterEntry>
     */
    public List<RosterEntry> getAllEntries() {
        if (getConnection() == null)
            return null;
        List<RosterEntry> Enlist = new ArrayList<>();
        Collection<RosterEntry> rosterEntry = Roster.getInstanceFor(connection).getEntries();
        for (RosterEntry aRosterEntry : rosterEntry) {
            Enlist.add(aRosterEntry);
        }
        return Enlist;
    }

    /**
     * 获取用户VCard信息
     *
     * @param user user
     * @return VCard
     */
    public VCard getUserVCard(String user) {
        if (getConnection() == null)
            return null;
        VCard vcard = new VCard();
        try {
            vcard = VCardManager.getInstanceFor(getConnection()).loadVCard(JidCreate.entityBareFrom(user));
        } catch (XmppStringprepException | SmackException | InterruptedException | XMPPException.XMPPErrorException e) {
            e.printStackTrace();
        }

        return vcard;
    }

    /**
     * 获取用户头像信息
     *
     * @param user user
     * @return Drawable
     */
    public Drawable getUserImage(String user) {
        if (getConnection() == null)
            return null;
        ByteArrayInputStream bais = null;
        try {
            VCard vcard = new VCard();
            // 加入这句代码，解决No VCard for
            addIQProvider("vCard", "vcard-temp",
                    new org.jivesoftware.smackx.vcardtemp.provider.VCardProvider());
            if (user == null || user.equals("") || user.trim().length() <= 0) {
                return null;
            }
            try {
                VCardManager.getInstanceFor(getConnection()).loadVCard(
                        JidCreate.entityBareFrom(user + "@" + getConnection().getServiceName()));
            } catch (XmppStringprepException | SmackException | InterruptedException | XMPPException.XMPPErrorException e) {
                e.printStackTrace();
                Drawable drawable = mContext.getResources().getDrawable(R.mipmap.img_nfriend_headshot1);
                return drawable;
            }

            if (vcard.getAvatar() == null) {
                Drawable drawable = mContext.getResources().getDrawable(R.mipmap.img_nfriend_headshot1);
                return drawable;
            }
            bais = new ByteArrayInputStream(vcard.getAvatar());
        } catch (Exception e) {
            Drawable drawable = mContext.getResources().getDrawable(R.mipmap.img_nfriend_headshot1);
            e.printStackTrace();
            return drawable;
        }
        return FormatTools.getInstance().InputStream2Drawable(bais);
    }

    /**
     * 添加一个分组
     *
     * @param groupName groupName
     * @return boolean
     */
    public boolean addGroup(String groupName) {
        if (getConnection() == null)
            return false;
        try {
            Roster.getInstanceFor(connection).createGroup(groupName);
            Log.v("addGroup", groupName + "創建成功");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 删除分组
     *
     * @param groupName groupName
     * @return boolean
     */
    public boolean removeGroup(String groupName) {
        return true;
    }

    /**
     * 添加好友 无分组
     *
     * @param userName userName
     * @param name     name
     * @return boolean
     */
    public boolean addUser(String userName, String name) {
        if (getConnection() == null)
            return false;
        try {
            Roster.getInstanceFor(connection).createEntry(JidCreate.entityBareFrom(userName), name, null);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 添加好友 有分组
     *
     * @param userName  userName
     * @param name      name
     * @param groupName groupName
     * @return boolean
     */
    public boolean addUser(String userName, String name, String groupName) {
        if (getConnection() == null)
            return false;
        try {
            Presence subscription = new Presence(Presence.Type.subscribed);
            subscription.setTo(JidCreate.entityBareFrom(userName));
            userName += "@" + getConnection().getServiceName();
            getConnection().sendStanza(subscription);
            Roster.getInstanceFor(connection).createEntry(JidCreate.entityBareFrom(userName), name,
                    new String[]{groupName});
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 删除好友
     *
     * @param userName userName
     * @return boolean
     */
    public boolean removeUser(String userName) {
        if (getConnection() == null)
            return false;
        try {
            RosterEntry entry = null;
            if (userName.contains("@"))
                entry = Roster.getInstanceFor(connection).getEntry(JidCreate.entityBareFrom(userName));
            else
                entry = Roster.getInstanceFor(connection).getEntry(JidCreate.entityBareFrom(
                        userName + "@" + getConnection().getServiceName()));
            if (entry == null)
                entry = Roster.getInstanceFor(connection).getEntry(JidCreate.entityBareFrom(userName));
            Roster.getInstanceFor(connection).removeEntry(entry);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 查询用户
     *
     * @param userName userName
     * @return List<HashMap<String, String>>
     */
    public List<HashMap<String, String>> searchUsers(String userName) {
        if (getConnection() == null)
            return null;
        HashMap<String, String> user;
        List<HashMap<String, String>> results = new ArrayList<>();
        try {
            UserSearchManager usm = new UserSearchManager(getConnection());

            Form searchForm = usm.getSearchForm(getConnection().getServiceName());
            if (searchForm == null)
                return null;

            Form answerForm = searchForm.createAnswerForm();
            answerForm.setAnswer("userAccount", true);
            answerForm.setAnswer("userPhote", userName);
            ReportedData data = usm.getSearchResults(answerForm, JidCreate.domainBareFrom("search" + getConnection().getServiceName()));

            List<ReportedData.Row> rowList = data.getRows();
            for (ReportedData.Row row : rowList) {
                user = new HashMap<>();
                user.put("userAccount", row.getValues("userAccount").toString());
                user.put("userPhote", row.getValues("userPhote").toString());
                results.add(user);
                // 若存在，则有返回,UserName一定非空，其他两个若是有设，一定非空
            }
        } catch (SmackException | InterruptedException | XmppStringprepException | XMPPException e) {
            e.printStackTrace();
        }
        return results;
    }

    /**
     * 修改心情
     *
     * @param status
     */
    public void changeStateMessage(String status) {
        if (getConnection() == null)
            return;
        Presence presence = new Presence(Presence.Type.available);
        presence.setStatus(status);
        try {
            getConnection().sendStanza(presence);
        } catch (SmackException.NotConnectedException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 修改用户头像
     */
    public boolean changeImage(byte[] bytes) {
        if (getConnection() == null)
            return false;
        try {
            VCard vcard = new VCard();
            vcard.load(getConnection());

            String encodedImage = Base64.encodeToString(bytes);
            vcard.setAvatar(bytes, encodedImage);
            vcard.setEncodedImage(encodedImage);
            vcard.setField("PHOTO", "<TYPE>image/jpg</TYPE><BINVAL>"
                    + encodedImage + "</BINVAL>", true);

            ByteArrayInputStream bais = new ByteArrayInputStream(
                    vcard.getAvatar());
            FormatTools.getInstance().InputStream2Bitmap(bais);

            vcard.save(getConnection());

            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

            UtilTool.saveImages(bitmap, Constants.MYUSER, mContext, mMgr);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 文件转字节
     *
     * @param file file
     * @return byte[]
     * @throws IOException
     */
    private byte[] getFileBytes(File file) throws IOException {
        BufferedInputStream bis = null;
        try {
            bis = new BufferedInputStream(new FileInputStream(file));
            int bytes = (int) file.length();
            byte[] buffer = new byte[bytes];
            int readBytes = bis.read(buffer);
            if (readBytes != buffer.length) {
                throw new IOException("Entire file not read");
            }
            return buffer;
        } finally {
            if (bis != null) {
                bis.close();
            }
        }
    }

    /**
     * 删除当前用户
     *
     * @return true成功
     */
    public boolean deleteAccount() {
        if (getConnection() == null)
            return false;
        try {
            AccountManager.getInstance(connection).deleteAccount();
            return true;
        } catch (XMPPException | SmackException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 修改密码
     *
     * @return true成功
     */
    public boolean changePassword(String pwd) {
        if (getConnection() == null)
            return false;
        try {
            AccountManager.getInstance(connection).changePassword(pwd);
            return true;
        } catch (SmackException | InterruptedException | XMPPException.XMPPErrorException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 初始化会议室列表
     */
    public List<HostedRoom> getHostRooms() {
        if (getConnection() == null)
            return null;
        Collection<HostedRoom> hostrooms;
        List<HostedRoom> roominfos = new ArrayList<>();
        try {
            hostrooms = MultiUserChatManager.getInstanceFor(XmppConnection.getInstance().getConnection()).getHostedRooms(
                    JidCreate.domainBareFrom(Constants.DOMAINNAME));
            for (HostedRoom entry : hostrooms) {
                roominfos.add(entry);
                Log.i("room", "名字：" + entry.getName() + " - ID:" + entry.getJid());
            }
            Log.i("room", "服务会议数量:" + roominfos.size());
        } catch (XMPPException | XmppStringprepException | InterruptedException | SmackException e) {
            e.printStackTrace();
            UtilTool.Log("错误", e.getMessage());
            return null;
        }
        return roominfos;
    }

    /**
     * 初始化加入的会议室
     */
    public List<String> getJoinedRooms() {
        if (getConnection() == null)
            return null;
        List<EntityBareJid> hostrooms;
        List<String> roominfos = new ArrayList<>();
        try {
            List<EntityBareJid> joinedRooms = MultiUserChatManager.getInstanceFor(XmppConnection.getInstance().getConnection()).getJoinedRooms(JidCreate.entityBareFrom(UtilTool.getJid()));
            for (EntityBareJid entry : joinedRooms) {
                roominfos.add(entry.toString());
                Log.i("fengjian", "名字：" + entry.toString());
            }
            Log.i("fengjian", "服务会议数量:" + roominfos.size());
        } catch (XMPPException | XmppStringprepException | InterruptedException | SmackException e) {
            UtilTool.Log("错误", e.getMessage());
            return null;
        }
        return roominfos;
    }

    /**
     * 发送群组聊天消息
     *
     * @param muc     muc
     * @param message 消息文本
     */
    public void sendGroupMessage(MultiUserChat muc, String message) {
        try {
            muc.sendMessage(message);
        } catch (SmackException.NotConnectedException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 查询会议室成员名字
     *
     * @param muc
     */
    public List<String> findMulitUser(MultiUserChat muc) {
        if (getConnection() == null)
            return null;
        List<String> listUser = new ArrayList<>();
        List<EntityFullJid> it = muc.getOccupants();
        // 遍历出聊天室人员名称
        for (EntityFullJid entityFullJid : it) {
            // 聊天室成员名字
            String name = entityFullJid.toString();
            listUser.add(name);
        }
        return listUser;
    }

    /**
     * 创建聊天窗口
     *
     * @param JID JID
     * @return Chat
     */
    public Chat getFriendChat(String JID) {
        try {
            return ChatManager.getInstanceFor(XmppConnection.getInstance().getConnection())
                    .chatWith(JidCreate.entityBareFrom(JID));
        } catch (XmppStringprepException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 发送单人聊天消息
     *
     * @param chat    chat
     * @param message 消息文本
     */
    public void sendSingleMessage(Chat chat, String message) {
        try {
            chat.send(message);
        } catch (SmackException.NotConnectedException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 发消息
     *
     * @param chat    chat
     * @param muc     muc
     * @param message message
     */
    public void sendMessage(Chat chat, MultiUserChat muc, String message) {
        if (chat != null) {
            sendSingleMessage(chat, message);
        } else if (muc != null) {
            sendGroupMessage(muc, message);
        }
    }

    /**
     * 发送文件
     *
     * @param user
     * @param filePath
     */
    public void sendFile(String user, String filePath) {
        if (getConnection() == null)
            return;
        // 创建文件传输管理器
        FileTransferManager manager = FileTransferManager.getInstanceFor(getConnection());

        // 创建输出的文件传输
        OutgoingFileTransfer transfer = null;
        try {
            transfer = manager.createOutgoingFileTransfer(JidCreate.entityFullFrom(user));
        } catch (XmppStringprepException e) {
            e.printStackTrace();
        }

        // 发送文件
        try {
            if (transfer != null)
                transfer.sendFile(new File(filePath), "You won't believe this!");
        } catch (SmackException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取离线消息
     *
     * @return
     */
    public Map<String, List<HashMap<String, String>>> getHisMessage() {
        if (getConnection() == null)
            return null;
        Map<String, List<HashMap<String, String>>> offlineMsgs = null;

        try {
            OfflineMessageManager offlineManager = new OfflineMessageManager(getConnection());
            List<Message> messageList = offlineManager.getMessages();

            int count = offlineManager.getMessageCount();
            if (count <= 0)
                return null;
            offlineMsgs = new HashMap<>();

            for (Message message : messageList) {
                String fromUser = message.getFrom().toString();
                HashMap<String, String> history = new HashMap<>();
                history.put("useraccount", getConnection().getUser().asEntityBareJidString());
                history.put("friendaccount", fromUser);
                history.put("info", message.getBody());
                history.put("type", "left");
                if (offlineMsgs.containsKey(fromUser)) {
                    offlineMsgs.get(fromUser).add(history);
                } else {
                    List<HashMap<String, String>> temp = new ArrayList<HashMap<String, String>>();
                    temp.add(history);
                    offlineMsgs.put(fromUser, temp);
                }
            }
            offlineManager.deleteMessages();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return offlineMsgs;
    }

    /**
     * 判断OpenFire用户的状态 strUrl :
     * url格式 - http://my.openfire.com:9090/plugins/presence
     * /status?jid=user1@SERVER_NAME&type=xml
     * 返回值 : 0 - 用户不存在; 1 - 用户在线; 2 - 用户离线
     * 说明 ：必须要求 OpenFire加载 presence 插件，同时设置任何人都可以访问
     */
    public int IsUserOnLine(String user) {
        String url = "http://" + SERVER_HOST + ":9090/plugins/presence/status?" +
                "jid=" + user + "@" + SERVER_NAME + "&type=xml";
        int shOnLineState = 0; // 不存在
        try {
            URL oUrl = new URL(url);
            URLConnection oConn = oUrl.openConnection();
            if (oConn != null) {
                BufferedReader oIn = new BufferedReader(new InputStreamReader(
                        oConn.getInputStream()));
                String strFlag = oIn.readLine();
                oIn.close();
                System.out.println("strFlag" + strFlag);
                if (strFlag.contains("type=\"unavailable\"")) {
                    shOnLineState = 2;
                }
                if (strFlag.contains("type=\"error\"")) {
                    shOnLineState = 0;
                } else if (strFlag.contains("priority") || strFlag.contains("id=\"")) {
                    shOnLineState = 1;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return shOnLineState;
    }

    public void setDB(DBManager dbManager) {
        mMgr = dbManager;
    }

    public void setContext(Context context) {
        mContext = context;
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

}

