package com.bclould.tocotalk.xmpp;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.Toast;

import com.bclould.tocotalk.R;
import com.bclould.tocotalk.crypto.otr.OtrChatListenerManager;
import com.bclould.tocotalk.history.DBManager;
import com.bclould.tocotalk.history.DBRoomManage;
import com.bclould.tocotalk.history.DBRoomMember;
import com.bclould.tocotalk.model.ConversationInfo;
import com.bclould.tocotalk.model.MessageInfo;
import com.bclould.tocotalk.model.RoomManageInfo;
import com.bclould.tocotalk.model.UserInfo;
import com.bclould.tocotalk.utils.Constants;
import com.bclould.tocotalk.utils.MessageEvent;
import com.bclould.tocotalk.utils.StringUtils;
import com.bclould.tocotalk.utils.UtilTool;

import org.greenrobot.eventbus.EventBus;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.FromMatchesFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketIDFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.muc.MUCAffiliation;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jivesoftware.smackx.muc.packet.MUCAdmin;
import org.jivesoftware.smackx.muc.packet.MUCInitialPresence;
import org.jivesoftware.smackx.muc.packet.MUCItem;
import org.jivesoftware.smackx.xdata.Form;
import org.jivesoftware.smackx.xdata.FormField;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.jid.parts.Resourcepart;
import org.jxmpp.stringprep.XmppStringprepException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import static com.bclould.tocotalk.ui.adapter.ChatAdapter.TO_TEXT_MSG;
import static com.bclould.tocotalk.ui.adapter.ChatAdapter.TO_TRANSFER_MSG;

/**
 * Created by GIjia on 2018/5/23.
 */
@RequiresApi(api = Build.VERSION_CODES.N)
public class MultiManage implements Room{

    private DBManager mMgr;
    private String roomId;
    private Context context;
    private String roomName;
    private MessageManageListener messageManageListener;
    private DBRoomMember dbRoomMember;
    private DBRoomManage dbRoomManage;

    public MultiManage(DBManager mMgr, DBRoomMember dbRoomMember, DBRoomManage dbRoomManage, String roomId, Context context, String roomName) {
        this.mMgr=mMgr;
        this.roomId=roomId;
        this.context=context;
        this.roomName=roomName;
        this.dbRoomManage=dbRoomManage;
        this.dbRoomMember=dbRoomMember;
    }

    @Override
    public void addMessageManageListener(MessageManageListener messageManageListener) {
        this.messageManageListener=messageManageListener;
    }

    @Override
    public void sendVoice(int duration, String fileName) {

    }

    @Override
    public MessageInfo sendMessage(String message) {
        try {
            //创建jid实体
            EntityBareJid groupJid = JidCreate.entityBareFrom(roomId);
            //群管理对象
            MultiUserChatManager multiUserChatManager = MultiUserChatManager.getInstanceFor(XmppConnection.getInstance().getConnection());
            MultiUserChat multiUserChat = multiUserChatManager.getMultiUserChat(groupJid);
            //发送信息
            multiUserChat.sendMessage(message);
            MessageInfo messageInfo = new MessageInfo();
            messageInfo.setUsername(roomId);
            messageInfo.setMessage(message);
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date curDate = new Date(System.currentTimeMillis());
            String time = formatter.format(curDate);
            messageInfo.setTime(time);
            messageInfo.setType(0);
            messageInfo.setMsgType(TO_TEXT_MSG);
            messageInfo.setSend(UtilTool.getJid());
            mMgr.addMessage(messageInfo);
            if (mMgr.findConversation(roomId)) {
                mMgr.updateConversation(roomId, 0, message, time);
            } else {
                ConversationInfo info = new ConversationInfo();
                info.setTime(time);
                info.setFriend(roomName);
                info.setUser(roomId);
                info.setMessage(message);
                info.setChatType(RoomManage.ROOM_TYPE_SINGLE);
                mMgr.addConversation(info);
            }
            EventBus.getDefault().post(new MessageEvent(context.getString(R.string.oneself_send_msg)));
            if(messageManageListener!=null){
                messageManageListener.refreshAddData(messageInfo);
            }
            return messageInfo;
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, context.getString(R.string.send_error), Toast.LENGTH_SHORT).show();
            MessageInfo messageInfo = new MessageInfo();
            messageInfo.setUsername(roomId);
            messageInfo.setMessage(message);
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date curDate = new Date(System.currentTimeMillis());
            String time = formatter.format(curDate);
            messageInfo.setTime(time);
            messageInfo.setType(0);
            messageInfo.setMsgType(TO_TEXT_MSG);
            messageInfo.setSendStatus(1);
            messageInfo.setSend(UtilTool.getJid());
            mMgr.addMessage(messageInfo);

            if (mMgr.findConversation(roomId)) {
                mMgr.updateConversation(roomId, 0, message, time);
            } else {
                ConversationInfo info = new ConversationInfo();
                info.setTime(time);
                info.setFriend(roomName);
                info.setUser(roomId);
                info.setMessage(message);
                info.setChatType(RoomManage.ROOM_TYPE_SINGLE);
                mMgr.addConversation(info);
            }
            EventBus.getDefault().post(new MessageEvent(context.getString(R.string.oneself_send_msg)));
            if(messageManageListener!=null){
                messageManageListener.refreshAddData(messageInfo);
            }
            return messageInfo;
        }
    }

    @Override
    public void Upload(String path) {

    }

    @Override
    public void sendTransfer(String mRemark, String mCoin, String mCount) {

    }

    @Override
    public void sendRed(String mRemark,String mCoin,double mCount,int id) {

    }

    @Override
    public void sendLocationMessage(Bitmap bitmap, String title, String address, float lat, float lng) {

    }

    @Override
    public boolean anewSendText(String user, String message, int id) {
        return false;
    }

    @Override
    public boolean anewSendVoice(MessageInfo messageInfo) {
        return false;
    }

    @Override
    public MultiUserChat createRoom(String roomName, String nickName, List<UserInfo> users) {
        if (XmppConnection.getInstance().getConnection() == null)
            return null;
        String roomJid=UtilTool.getUser()+System.currentTimeMillis() + "@conference." + XmppConnection.getInstance().getConnection().getServiceName();
        MultiUserChat muc = null;
        try {
            if(StringUtils.isEmpty(roomName)){
                roomName="群聊";
            }
            // 创建一个MultiUserChat
            muc = MultiUserChatManager.getInstanceFor(XmppConnection.getInstance().getConnection())
                    .getMultiUserChat(JidCreate.entityBareFrom(roomJid));

            // 创建聊天室
            muc.create(Resourcepart.from(nickName));
            muc.changeSubject(roomName);
            // 获得聊天室的配置表单
            Form form = muc.getConfigurationForm();
            // 根据原始表单创建一个要提交的新表单。
            Form submitForm = form.createAnswerForm();
            // 向要提交的表单添加默认答复
            for (FormField formField : form.getFields()) {
                if (FormField.Type.hidden == formField.getType()
                        && formField.getVariable() != null) {
                    // 设置默认值作为答复
                    submitForm.setDefaultAnswer(formField.getVariable());
                }
            }
            // 设置聊天室的新拥有者
            List<String> owners = new ArrayList<>();
            owners.add(UtilTool.getJid());

            //这里的用户实体我要说一下，因为这是我这个项目的实体，实际上这里只需要知道用户的jid获者名称就可以了
            if (users != null && !users.isEmpty()) {
                for (int i = 0; i < users.size(); i++) {  //添加群成员,用户jid格式和之前一样 用户名@openfire服务器名称
                    EntityBareJid userJid = JidCreate.entityBareFrom(users.get(i).getUser());
                    muc.invite(userJid, "欢迎加入群聊");
                }
            }
            //设置房间名字
            submitForm.setAnswer("muc#roomconfig_roomname", roomName);
            //设置最大群人数
            List<String> maxuser=new ArrayList<>();
            maxuser.add("200");
            submitForm.setAnswer("muc#roomconfig_maxusers", maxuser);

            // 设置聊天室是持久聊天室，即将要被保存下来
            submitForm.setAnswer("muc#roomconfig_persistentroom", true);
            // 房间仅对成员开放
            submitForm.setAnswer("muc#roomconfig_membersonly", false);
            // 允许占有者邀请其他人
            submitForm.setAnswer("muc#roomconfig_allowinvites", true);
            // if (!password.equals("")) {
            // // 进入是否需要密码
//			 submitForm.setAnswer("muc#roomconfig_passwordprotectedroom",false);
            // // 设置进入密码
            // submitForm.setAnswer("muc#roomconfig_roomsecret", password);
            // }
            // 能够发现占有者真实 JID 的角色
            // submitForm.setAnswer("muc#roomconfig_whois", "anyone");
//             设置描述
            submitForm.setAnswer("muc#roomconfig_roomdesc", "mulchat");
            // 登录房间对话
            submitForm.setAnswer("muc#roomconfig_enablelogging", true);
            // 仅允许注册的昵称登录
            submitForm.setAnswer("x-muc#roomconfig_reservednick", false);
            // 允许使用者修改昵称
            submitForm.setAnswer("x-muc#roomconfig_canchangenick", true);
            // 允许用户注册房间
            submitForm.setAnswer("x-muc#roomconfig_registration", true);
            // 发送已完成的表单（有默认值）到服务器来配置聊天室
            muc.sendConfigurationForm(submitForm);
            muc.join(Resourcepart.from(nickName));
            grantOwnership(owners,roomJid,roomName);
            dbRoomManage.addRoom(createRoomInfo(roomJid,roomName));
        } catch (XMPPException | XmppStringprepException | SmackException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
        return muc;
    }
    public static boolean grantOwnership(Collection<String> jids, String roomJid,String roomName) throws XMPPException {
        if (XmppConnection.getInstance().getConnection() == null || !XmppConnection.getInstance().getConnection().isConnected()
                || !XmppConnection.getInstance().getConnection().isAuthenticated()) {
            return false;
        }
        try {
            MUCRoomAdmin iq = new MUCRoomAdmin();
            iq.setTo(roomJid);
            iq.setType(IQ.Type.set);
            for (String jid : jids) {
                // Set the new affiliation.
                MUCRoomItem item = new MUCRoomItem(MUCAffiliation.member,JidCreate.from(jid),"欢迎加入群聊",roomName);
                iq.addItem(item);
            }
            XmppConnection.getInstance().getConnection().sendPacket(iq);
            return true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 加入会议室
     *
     * @param user      昵称
     * @param roomsName 会议室名
     * @param mgr
     */
    public MultiUserChat joinMultiUserChat(String user, String roomJid) {
        if (XmppConnection.getInstance().getConnection() == null)
            return null;
        try {
            // 使用XMPPConnection创建一个MultiUserChat窗口
            MultiUserChat muc = MultiUserChatManager.getInstanceFor(XmppConnection.getInstance().getConnection()).getMultiUserChat(
                    JidCreate.entityBareFrom(roomJid ));
            // 用户加入聊天室
            muc.join(Resourcepart.from(user));
            Log.i("fengjian", "会议室【" + roomJid + "】加入成功........"+user);
            dbRoomManage.addRoom(createRoomInfo(roomJid,roomId.split("@")[0]));
            return muc;
        } catch (XMPPException | XmppStringprepException | InterruptedException | SmackException e) {
            e.printStackTrace();
            Log.i("fengjian", "会议室【" + roomJid + "】加入失败........");
            return null;
        }
    }

    private RoomManageInfo createRoomInfo(String roomId,String roomName){
        RoomManageInfo roomManageInfo=new RoomManageInfo();
        roomManageInfo.setRoomId(roomId);
        roomManageInfo.setRoomName(roomName);
        return roomManageInfo;
    }

    @Override
    public void changeName(String name) {
        this.roomName=name;
    }

}