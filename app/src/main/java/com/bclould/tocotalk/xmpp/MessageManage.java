package com.bclould.tocotalk.xmpp;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.widget.Toast;

import com.bclould.tocotalk.R;
import com.bclould.tocotalk.crypto.otr.OtrChatListenerManager;
import com.bclould.tocotalk.history.DBManager;
import com.bclould.tocotalk.model.ConversationInfo;
import com.bclould.tocotalk.model.MessageInfo;
import com.bclould.tocotalk.utils.MessageEvent;
import com.bclould.tocotalk.utils.UtilTool;

import org.greenrobot.eventbus.EventBus;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jxmpp.jid.impl.JidCreate;

import java.text.SimpleDateFormat;
import java.util.Date;

import static com.bclould.tocotalk.ui.adapter.ChatAdapter.TO_TEXT_MSG;

/**
 * Created by GIjia on 2018/5/17.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class MessageManage {
    private DBManager mMgr;
    private String mUser;
    private Context context;
    private String mName;
    public MessageManage(DBManager mMgr,String mUser,Context context,String mName){
        this.mMgr=mMgr;
        this.mUser=mUser;
        this.context=context;
        this.mName=mName;
    }
    //发送文本消息
    public MessageInfo sendMessage(String message) {
        try {
            ChatManager manager = ChatManager.getInstanceFor(XmppConnection.getInstance().getConnection());
            Chat chat = manager.createChat(JidCreate.entityBareFrom(mUser), null);
            chat.sendMessage(OtrChatListenerManager.getInstance().sentMessagesChange(message,
                    OtrChatListenerManager.getInstance().sessionID(UtilTool.getJid(), String.valueOf(JidCreate.entityBareFrom(mUser)))));
            MessageInfo messageInfo = new MessageInfo();
            messageInfo.setUsername(mUser);
            messageInfo.setMessage(message);
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date curDate = new Date(System.currentTimeMillis());
            String time = formatter.format(curDate);
            messageInfo.setTime(time);
            messageInfo.setType(0);
            messageInfo.setMsgType(TO_TEXT_MSG);
            messageInfo.setSend(UtilTool.getJid());
            mMgr.addMessage(messageInfo);
            if (mMgr.findConversation(mUser)) {
                mMgr.updateConversation(mUser, 0, message, time);
            } else {
                ConversationInfo info = new ConversationInfo();
                info.setTime(time);
                info.setFriend(mName);
                info.setUser(mUser);
                info.setMessage(message);
                mMgr.addConversation(info);
            }
            EventBus.getDefault().post(new MessageEvent(context.getString(R.string.oneself_send_msg)));
            return messageInfo;
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, context.getString(R.string.send_error), Toast.LENGTH_SHORT).show();
            MessageInfo messageInfo = new MessageInfo();
            messageInfo.setUsername(mUser);
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

            if (mMgr.findConversation(mUser)) {
                mMgr.updateConversation(mUser, 0, message, time);
            } else {
                ConversationInfo info = new ConversationInfo();
                info.setTime(time);
                info.setFriend(mName);
                info.setUser(mUser);
                info.setMessage(message);
                mMgr.addConversation(info);
            }
            EventBus.getDefault().post(new MessageEvent(context.getString(R.string.oneself_send_msg)));
            return  messageInfo;
        }
    }
}
