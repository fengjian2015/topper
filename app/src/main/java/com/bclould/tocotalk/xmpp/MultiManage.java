package com.bclould.tocotalk.xmpp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.amazonaws.Request;
import com.amazonaws.Response;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.handlers.RequestHandler2;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.bclould.tocotalk.Presenter.GroupPresenter;
import com.bclould.tocotalk.R;
import com.bclould.tocotalk.history.DBManager;
import com.bclould.tocotalk.history.DBRoomManage;
import com.bclould.tocotalk.history.DBRoomMember;
import com.bclould.tocotalk.model.ConversationInfo;
import com.bclould.tocotalk.model.MessageInfo;
import com.bclould.tocotalk.model.RoomManageInfo;
import com.bclould.tocotalk.model.UserInfo;
import com.bclould.tocotalk.model.VoiceInfo;
import com.bclould.tocotalk.ui.activity.CreateGroupRoomActivity;
import com.bclould.tocotalk.utils.Constants;
import com.bclould.tocotalk.utils.MessageEvent;
import com.bclould.tocotalk.utils.StringUtils;
import com.bclould.tocotalk.utils.UtilTool;

import org.greenrobot.eventbus.EventBus;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.util.stringencoder.Base64;
import org.jivesoftware.smackx.muc.MUCAffiliation;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jivesoftware.smackx.xdata.Form;
import org.jivesoftware.smackx.xdata.FormField;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.jid.parts.Resourcepart;
import org.jxmpp.stringprep.XmppStringprepException;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

import static com.bclould.tocotalk.ui.adapter.ChatAdapter.TO_CARD_MSG;
import static com.bclould.tocotalk.ui.adapter.ChatAdapter.TO_FILE_MSG;
import static com.bclould.tocotalk.ui.adapter.ChatAdapter.TO_GUESS_MSG;
import static com.bclould.tocotalk.ui.adapter.ChatAdapter.TO_IMG_MSG;
import static com.bclould.tocotalk.ui.adapter.ChatAdapter.TO_LINK_MSG;
import static com.bclould.tocotalk.ui.adapter.ChatAdapter.TO_LOCATION_MSG;
import static com.bclould.tocotalk.ui.adapter.ChatAdapter.TO_RED_MSG;
import static com.bclould.tocotalk.ui.adapter.ChatAdapter.TO_TEXT_MSG;
import static com.bclould.tocotalk.ui.adapter.ChatAdapter.TO_TRANSFER_MSG;
import static com.bclould.tocotalk.ui.adapter.ChatAdapter.TO_VIDEO_MSG;
import static com.bclould.tocotalk.ui.adapter.ChatAdapter.TO_VOICE_MSG;

/**
 * Created by GIjia on 2018/5/23.
 */
@RequiresApi(api = Build.VERSION_CODES.N)
public class MultiManage implements Room{

    private DBManager mMgr;
    private String roomId;
    private Context context;
    private String roomName;
    private ArrayList<MessageManageListener> listeners=new ArrayList<>();
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
    public void addMessageManageListener(MessageManageListener messageManageListener){
        listeners.add(messageManageListener);
    }

    @Override
    public void removerMessageManageListener(MessageManageListener messageManageListener) {
        if(listeners.contains(messageManageListener)){
            listeners.remove(messageManageListener);
        }
    }

    private void refreshAddData(MessageInfo messageInfo){
        if(listeners!=null){
            for (MessageManageListener messageManageListener:listeners){
                messageManageListener.refreshAddData(messageInfo);
            }
        }
    }
    private void sendFileResults(String newFile2,boolean isSuccess){
        if(listeners!=null){
            for (MessageManageListener messageManageListener:listeners){
                messageManageListener.sendFileResults(newFile2,isSuccess);
            }
        }
    }
    private void sendError(int id){
        if(listeners!=null){
            for (MessageManageListener messageManageListener:listeners){
                messageManageListener.sendError(id);
            }
        }
    }

    @Override
    public boolean anewSendVoice(MessageInfo messageInfo) {
        try {
            mMgr.deleteSingleMessage(roomId,messageInfo.getId()+"");
            sendVoice(UtilTool.getFileDuration(messageInfo.getVoice(), context),messageInfo.getVoice());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void sendVoice(int duration, String fileName) {
        String converstaion="[" + context.getString(R.string.voice) + "]";
        try {
            //创建jid实体
            EntityBareJid groupJid = JidCreate.entityBareFrom(roomId);
            //群管理对象
            MultiUserChatManager multiUserChatManager = MultiUserChatManager.getInstanceFor(XmppConnection.getInstance().getConnection());
            MultiUserChat multiUserChat = multiUserChatManager.getMultiUserChat(groupJid);
            //发送信息
            org.jivesoftware.smack.packet.Message message = new org.jivesoftware.smack.packet.Message();//实例化消息
            VoiceInfo voiceInfo = new VoiceInfo();//实例化语音model
            byte[] bytes = UtilTool.readStream(fileName);//把语音文件转换成二进制
            String base64 = Base64.encodeToString(bytes);//二进制转成Base64
            voiceInfo.setElementText(base64);//设置进model
            message.setBody("[audio]:" + duration + context.getString(R.string.second));
            message.addExtension(voiceInfo);//扩展message,把语音添加进标签
            multiUserChat.sendMessage(message);//发送消息

            //把消息添加进数据库
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date curDate = new Date(System.currentTimeMillis());
            String time = formatter.format(curDate);
            MessageInfo messageInfo = new MessageInfo();
            messageInfo.setMessage(message.getBody());
            messageInfo.setType(0);
            messageInfo.setUsername(roomId);
            messageInfo.setVoice(fileName);
            messageInfo.setTime(time);
            messageInfo.setMsgType(TO_VOICE_MSG);
            messageInfo.setVoiceTime(duration + "");
            messageInfo.setVoiceStatus(1);
            messageInfo.setSend(UtilTool.getJid());
            messageInfo.setConverstaion(converstaion);
            messageInfo.setId(mMgr.addMessage(messageInfo));
            refreshAddData(messageInfo);

            //给聊天列表更新消息
            if (mMgr.findConversation(roomId)) {
                mMgr.updateConversation(roomId, 0, converstaion, time);
            } else {
                ConversationInfo info = new ConversationInfo();
                info.setTime(time);
                info.setFriend(roomName);
                info.setUser(roomId);
                info.setMessage(converstaion);
                info.setChatType(RoomManage.ROOM_TYPE_SINGLE);
                mMgr.addConversation(info);
            }
            //发送消息通知
            EventBus.getDefault().post(new MessageEvent(context.getString(R.string.oneself_send_msg)));
        } catch (Exception e) {
            //发送失败处理
            e.printStackTrace();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date curDate = new Date(System.currentTimeMillis());
            String time = formatter.format(curDate);
            MessageInfo messageInfo = new MessageInfo();
            messageInfo.setMessage("[audio]:" + duration + context.getString(R.string.second));
            messageInfo.setType(0);
            messageInfo.setUsername(roomId);
            messageInfo.setVoice(fileName);
            messageInfo.setTime(time);
            messageInfo.setMsgType(TO_VOICE_MSG);
            messageInfo.setVoiceTime(duration + "");
            messageInfo.setVoiceStatus(1);
            messageInfo.setSendStatus(1);
            messageInfo.setConverstaion(converstaion);
            messageInfo.setId(mMgr.addMessage(messageInfo));
            refreshAddData(messageInfo);
            if (mMgr.findConversation(roomId)) {
                mMgr.updateConversation(roomId, 0, converstaion, time);
            } else {
                ConversationInfo info = new ConversationInfo();
                info.setTime(time);
                info.setFriend(roomName);
                info.setUser(roomId);
                info.setMessage(converstaion);
                info.setChatType(RoomManage.ROOM_TYPE_SINGLE);
                mMgr.addConversation(info);
            }
            EventBus.getDefault().post(new MessageEvent(context.getString(R.string.oneself_send_msg)));
        }
    }


    @Override
    public boolean anewSendText(String message, int id) {
        try {
            mMgr.deleteSingleMessage(roomId,id+"");
            sendMessage(message);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
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
            messageInfo.setConverstaion(message);
            messageInfo.setId(mMgr.addMessage(messageInfo));
            if (mMgr.findConversation(roomId)) {
                mMgr.updateConversation(roomId, 0, message, time);
            } else {
                ConversationInfo info = new ConversationInfo();
                info.setTime(time);
                info.setFriend(roomName);
                info.setUser(roomId);
                info.setMessage(message);
                info.setChatType(RoomManage.ROOM_TYPE_MULTI);
                mMgr.addConversation(info);
            }
            EventBus.getDefault().post(new MessageEvent(context.getString(R.string.oneself_send_msg)));
            refreshAddData(messageInfo);
            return messageInfo;
        } catch (Exception e) {
            e.printStackTrace();
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
            messageInfo.setConverstaion(message);
            messageInfo.setId(mMgr.addMessage(messageInfo));

            if (mMgr.findConversation(roomId)) {
                mMgr.updateConversation(roomId, 0, message, time);
            } else {
                ConversationInfo info = new ConversationInfo();
                info.setTime(time);
                info.setFriend(roomName);
                info.setUser(roomId);
                info.setMessage(message);
                info.setChatType(RoomManage.ROOM_TYPE_MULTI);
                mMgr.addConversation(info);
            }
            EventBus.getDefault().post(new MessageEvent(context.getString(R.string.oneself_send_msg)));
            refreshAddData(messageInfo);
            return messageInfo;
        }
    }

    private void sendFileAfterMessage(String key,String postfix,String newFile,int mId){
        try {
            //创建jid实体
            EntityBareJid groupJid = JidCreate.entityBareFrom(roomId);
            //群管理对象
            MultiUserChatManager multiUserChatManager = MultiUserChatManager.getInstanceFor(XmppConnection.getInstance().getConnection());
            MultiUserChat multiUserChat = multiUserChatManager.getMultiUserChat(groupJid);
            org.jivesoftware.smack.packet.Message message = new org.jivesoftware.smack.packet.Message();
            VoiceInfo voiceInfo = new VoiceInfo();
            byte[] bytes = UtilTool.readStream(newFile);
            String base64 = Base64.encodeToString(bytes);
            voiceInfo.setElementText(base64);
            message.addExtension(voiceInfo);
            message.setBody("[" + postfix + "]:" + key);
            //发送信息
            multiUserChat.sendMessage(message);
            mMgr.updateMessageHint(mId, 1);
            sendFileResults(newFile,true);
            return;
        } catch (Exception e) {
            mMgr.updateMessageHint(mId, 2);
            sendFileResults(newFile,false);

        }
    }


    @Override
    public void anewSendUpload(MessageInfo messageInfo) {
        mMgr.deleteSingleMessage(roomId,messageInfo.getId()+"");
        Upload(messageInfo.getVoice());
    }

    @Override
    public void Upload(final String path) {
        final File file = new File(path);//获取文件
        final String postfix = UtilTool.getPostfix(file.getName());//获取文件后缀
        final String key = UtilTool.getUserId() + UtilTool.createtFileName() + ".AN." + UtilTool.getPostfix2(file.getName());//命名aws文件名
        Bitmap bitmap = null;
        //获取发送图片或视频第一帧的bitmap
        if (postfix.equals("Video")) {
            bitmap = ThumbnailUtils.createVideoThumbnail(path
                    , MediaStore.Video.Thumbnails.MINI_KIND);
        } else {
            bitmap = BitmapFactory.decodeFile(path);
        }
        //缩略图储存路径
        final File newFile = new File(Constants.PUBLICDIR + key);
        String postfixs = file.getName().substring(file.getName().lastIndexOf("."));
        if(!".gif".equals(postfixs)&&!".GIF".equals(postfixs)){
            UtilTool.comp(bitmap, newFile);//压缩图片
        }else{
            UtilTool.copyFile(file.getAbsolutePath(),newFile.getAbsolutePath());
        }

        //上传视频到aws
        if (postfix.equals("Video")) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final int[] mId = new int[1];
                    try {
                        //连接aws
                        BasicSessionCredentials sessionCredentials = new BasicSessionCredentials(
                                Constants.ACCESS_KEY_ID,
                                Constants.SECRET_ACCESS_KEY,
                                Constants.SESSION_TOKEN);
                        AmazonS3Client s3Client = new AmazonS3Client(
                                sessionCredentials);
                        Regions regions = Regions.fromName("ap-northeast-2");
                        Region region = Region.getRegion(regions);
                        s3Client.setRegion(region);
                        //上传监听
                        s3Client.addRequestHandler(new RequestHandler2() {
                            @Override
                            //上传之前把消息储存数据库
                            public void beforeRequest(Request<?> request) {
                                UtilTool.Log("aws", "之前");
                                mId[0]=  sendFileMessage(path,postfix,key,newFile.getPath());
                            }

                            //上传文件完成
                            @Override
                            public void afterResponse(Request<?> request, Response<?> response) {
                                sendFileAfterMessage(key,postfix,newFile.getPath(), mId[0]);
                            }

                            @Override
                            public void afterError(Request<?> request, Response<?> response, Exception e) {
                                UtilTool.Log("aws", "错误");
                            }
                        });
                        //实例化上传请求
                        PutObjectRequest por = new PutObjectRequest(Constants.BUCKET_NAME, key, file);
                        //开始上传
                        s3Client.putObject(por);
                    } catch (Exception e) {
                        e.printStackTrace();
                        mMgr.updateMessageHint( mId[0], 2);
                        sendError( mId[0]);
                    }
                }
            }).start();

        } else {
            //压缩图片
            Luban.with(context)
                    .load(file)                                   // 传人要压缩的图片列表
                    .ignoreBy(100)                                  // 忽略不压缩图片的大小
                    .setCompressListener(new OnCompressListener() { //设置回调
                        @Override
                        public void onStart() {
                        }

                        @Override
                        public void onSuccess(final File file) {
                            //上传图片到aws
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    final int[] mId = new int[1];
                                    try {
                                        //连接aws
                                        BasicSessionCredentials sessionCredentials = new BasicSessionCredentials(
                                                Constants.ACCESS_KEY_ID,
                                                Constants.SECRET_ACCESS_KEY,
                                                Constants.SESSION_TOKEN);
                                        AmazonS3Client s3Client = new AmazonS3Client(
                                                sessionCredentials);
                                        Regions regions = Regions.fromName("ap-northeast-2");
                                        Region region = Region.getRegion(regions);
                                        s3Client.setRegion(region);
                                        s3Client.addRequestHandler(new RequestHandler2() {
                                            @Override
                                            public void beforeRequest(Request<?> request) {
                                                UtilTool.Log("aws", "之前");
                                                mId[0] = sendFileMessage(path,postfix,key,newFile.getPath());
                                            }

                                            @Override
                                            public void afterResponse(Request<?> request, Response<?> response) {
                                                sendFileAfterMessage(key,postfix,newFile.getPath(),mId[0]);
                                            }

                                            @Override
                                            public void afterError(Request<?> request, Response<?> response, Exception e) {
                                                UtilTool.Log("aws", "错误");
                                            }
                                        });
                                        PutObjectRequest por = new PutObjectRequest(Constants.BUCKET_NAME, key, file);
                                        s3Client.putObject(por);
                                    } catch (Exception e) {
                                        mMgr.updateMessageHint(mId[0], 2);
                                        sendError(mId[0]);
                                        e.printStackTrace();
                                    }
                                }
                            }).start();
                        }
                        @Override
                        public void onError(Throwable e) {
                        }
                    }).launch();    //启动压缩
        }
    }

    @Override
    public void sendTransfer(String mRemark, String mCoin, String mCount) {
        String converstaion="[" + context.getString(R.string.transfer) + "]";
        String message = Constants.TRANSFER + Constants.CHUANCODE + mRemark + Constants.CHUANCODE + mCoin + Constants.CHUANCODE + mCount;
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
            android.icu.text.SimpleDateFormat formatter = new android.icu.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date curDate = new Date(System.currentTimeMillis());
            String time = formatter.format(curDate);
            messageInfo.setTime(time);
            messageInfo.setRemark(mRemark);
            messageInfo.setCoin(mCoin);
            messageInfo.setMsgType(TO_TRANSFER_MSG);
            messageInfo.setCount(mCount);
            messageInfo.setStatus(0);
            messageInfo.setSend(UtilTool.getJid());
            messageInfo.setConverstaion(converstaion);
            mMgr.addMessage(messageInfo);

            if (mMgr.findConversation(roomId)) {
                mMgr.updateConversation(roomId, 0, converstaion, time);
            } else {
                ConversationInfo info = new ConversationInfo();
                info.setTime(time);
                info.setFriend(dbRoomManage.findRoomName(roomId));
                info.setUser(roomId);
                info.setMessage(converstaion);
                info.setChatType(RoomManage.ROOM_TYPE_MULTI);
                mMgr.addConversation(info);
            }
            EventBus.getDefault().post(new MessageEvent(context.getString(R.string.transfer)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendRed(String mRemark,String mCoin,double mCount,int id) {
        String converstaion="[" + context.getString(R.string.red_package) + "]";
        String message = Constants.REDBAG + Constants.CHUANCODE + mRemark + Constants.CHUANCODE + mCoin + Constants.CHUANCODE + mCount + Constants.CHUANCODE + id;
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
            android.icu.text.SimpleDateFormat formatter = new android.icu.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date curDate = new Date(System.currentTimeMillis());
            String time = formatter.format(curDate);
            messageInfo.setTime(time);
            messageInfo.setType(0);
            messageInfo.setRemark(mRemark);
            messageInfo.setCoin(mCoin);
            messageInfo.setMsgType(TO_RED_MSG);
            messageInfo.setCount(mCount + "");
            messageInfo.setStatus(0);
            messageInfo.setRedId(id);
            messageInfo.setSend(UtilTool.getJid());
            messageInfo.setConverstaion(converstaion);
            mMgr.addMessage(messageInfo);
            if (mMgr.findConversation(roomId)) {
                mMgr.updateConversation(roomId, 0, converstaion, time);
            } else {
                ConversationInfo info = new ConversationInfo();
                info.setTime(time);
                info.setFriend(dbRoomManage.findRoomName(roomId));
                info.setUser(roomId);
                info.setMessage(converstaion);
                info.setChatType(RoomManage.ROOM_TYPE_MULTI);
                mMgr.addConversation(info);
            }
            EventBus.getDefault().post(new MessageEvent(context.getString(R.string.send_red_packet_le)));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void anewSendLocation(MessageInfo messageInfo) {
        mMgr.deleteSingleMessage(roomId,messageInfo.getId()+"");
        sendLocationMessage(messageInfo.getVoice(),null,messageInfo.getTitle(),messageInfo.getAddress(),messageInfo.getLat(),messageInfo.getLng());
    }

    public void sendLocationMessage(String file,Bitmap bitmap, String title, String address, float lat, float lng){
        String converstaion="[" + context.getString(R.string.location) + "]";
        final String postfix = "LOCATION";//获取文件后缀
        final String key = UtilTool.getUserId() + UtilTool.createtFileName() + ".AN.jpg";//命名aws文件名
        //缩略图储存路径
        File newFile;
        if(org.jivesoftware.smack.util.StringUtils.isEmpty(file)) {
            newFile = new File(Constants.PUBLICDIR + key);
            UtilTool.comp(bitmap, newFile);//压缩图片
        }else{
            newFile=new File(file);
        }
        int mId;
        try {
            //创建jid实体
            EntityBareJid groupJid = JidCreate.entityBareFrom(roomId);
            //群管理对象
            MultiUserChatManager multiUserChatManager = MultiUserChatManager.getInstanceFor(XmppConnection.getInstance().getConnection());
            MultiUserChat multiUserChat = multiUserChatManager.getMultiUserChat(groupJid);

            org.jivesoftware.smack.packet.Message message = new org.jivesoftware.smack.packet.Message();
            VoiceInfo voiceInfo = new VoiceInfo();
            byte[] bytes = UtilTool.readStream(newFile.getAbsolutePath());
            String base64 = Base64.encodeToString(bytes);
            voiceInfo.setElementText(base64);
            message.addExtension(voiceInfo);
            MessageInfo messageInfo=new MessageInfo();
            messageInfo.setTitle(title);
            messageInfo.setAddress(address);
            messageInfo.setLat(lat);
            messageInfo.setLng(lng);
            message.setBody("[" + postfix + "]:" + JSON.toJSONString(messageInfo));
            //发送信息
            multiUserChat.sendMessage(message);

            //把消息添加进数据库
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date curDate = new Date(System.currentTimeMillis());
            String time = formatter.format(curDate);

            messageInfo.setVoice(newFile.getAbsolutePath());
            messageInfo.setType(0);
            messageInfo.setUsername(roomId);
            messageInfo.setTime(time);
            messageInfo.setMessage(title);
            messageInfo.setMsgType(TO_LOCATION_MSG);
            messageInfo.setSend(UtilTool.getJid());
            messageInfo.setConverstaion(converstaion);
            mId= mMgr.addMessage(messageInfo);
            messageInfo.setId(mId);
            refreshAddData(messageInfo);
            //给聊天列表更新消息
            if (mMgr.findConversation(roomId)) {
                mMgr.updateConversation(roomId, 0, converstaion, time);
            } else {
                ConversationInfo info = new ConversationInfo();
                info.setTime(time);
                info.setFriend(dbRoomManage.findRoomName(roomId));
                info.setUser(roomId);
                info.setMessage(converstaion);
                info.setChatType(RoomManage.ROOM_TYPE_MULTI);
                mMgr.addConversation(info);
            }
            //发送消息通知
            EventBus.getDefault().post(new MessageEvent(context.getString(R.string.oneself_send_msg)));
            mMgr.updateMessageHint(mId, 1);
            sendFileResults(newFile.getAbsolutePath(),true);

            return;
        } catch (Exception e) {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date curDate = new Date(System.currentTimeMillis());
            String time = formatter.format(curDate);
            MessageInfo messageInfo = new MessageInfo();
            messageInfo.setVoice(newFile.getAbsolutePath());
            messageInfo.setMessage(title);
            messageInfo.setTitle(title);
            messageInfo.setAddress(address);
            messageInfo.setLat(lat);
            messageInfo.setLng(lng);
            messageInfo.setType(0);
            messageInfo.setUsername(roomId);
            messageInfo.setTime(time);
            messageInfo.setMsgType(TO_LOCATION_MSG);
            messageInfo.setSendStatus(2);
            messageInfo.setConverstaion(converstaion);
            mId=mMgr.addMessage(messageInfo);
            messageInfo.setId(mId);
            refreshAddData(messageInfo);
            if (mMgr.findConversation(roomId)) {
                mMgr.updateConversation(roomId, 0, converstaion, time);
            } else {
                ConversationInfo info = new ConversationInfo();
                info.setTime(time);
                info.setFriend(dbRoomManage.findRoomName(roomId));
                info.setUser(roomId);
                info.setMessage(converstaion);
                info.setChatType(RoomManage.ROOM_TYPE_MULTI);
                mMgr.addConversation(info);
            }
            EventBus.getDefault().post(new MessageEvent(context.getString(R.string.oneself_send_msg)));
            mMgr.updateMessageHint(mId, 2);
            sendFileResults(newFile.getAbsolutePath(),false);

        }
    }

    //发送文件消息
    public int sendFileMessage(String path, String postfix, String key, String newFile) {
        int mId;
        try {
            MessageInfo messageInfo = new MessageInfo();
            messageInfo.setUsername(roomId);
            messageInfo.setVoice(newFile);
            messageInfo.setMessage(path);
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date curDate = new Date(System.currentTimeMillis());
            String time = formatter.format(curDate);
            messageInfo.setTime(time);
            messageInfo.setType(0);
            if (postfix.equals("Image")) {
                messageInfo.setMsgType(TO_IMG_MSG);
            } else if (postfix.equals("Video")) {
                messageInfo.setMsgType(TO_VIDEO_MSG);
            } else {
                messageInfo.setMsgType(TO_FILE_MSG);
            }
            messageInfo.setSend(UtilTool.getJid());
            if (mMgr.findConversation(roomId)) {
                if (postfix.equals("Image")) {
                    mMgr.updateConversation(roomId, 0, "[" + context.getString(R.string.image) + "]", time);
                    messageInfo.setConverstaion("[" + context.getString(R.string.image) + "]");
                } else if (postfix.equals("Video")){
                    mMgr.updateConversation(roomId, 0, "[" + context.getString(R.string.video) + "]", time);
                    messageInfo.setConverstaion("[" + context.getString(R.string.video) + "]");
                }else {
                    mMgr.updateConversation(roomId, 0, "[" + context.getString(R.string.file) + "]", time);
                    messageInfo.setConverstaion("[" + context.getString(R.string.file) + "]");
                }
            } else {
                ConversationInfo info = new ConversationInfo();
                info.setTime(time);
                info.setFriend(dbRoomManage.findRoomName(roomId));
                info.setUser(roomId);
                if (postfix.equals("Image")) {
                    info.setMessage("[" + context.getString(R.string.image) + "]");
                    messageInfo.setConverstaion("[" + context.getString(R.string.image) + "]");
                }else if (postfix.equals("Video")) {
                    info.setMessage("[" + context.getString(R.string.video) + "]");
                    messageInfo.setConverstaion("[" + context.getString(R.string.video) + "]");
                }else {
                    info.setMessage("[" + context.getString(R.string.file) + "]");
                    messageInfo.setConverstaion("[" + context.getString(R.string.file) + "]");
                }
                info.setChatType(RoomManage.ROOM_TYPE_MULTI);
                mMgr.addConversation(info);
            }
            mId = mMgr.addMessage(messageInfo);
            messageInfo.setId(mId);
            refreshAddData(messageInfo);
            EventBus.getDefault().post(new MessageEvent(context.getString(R.string.oneself_send_msg)));
        } catch (Exception e) {
            e.printStackTrace();
            MessageInfo messageInfo = new MessageInfo();
            messageInfo.setUsername(roomId);
            messageInfo.setVoice(path);
            messageInfo.setMessage(path);
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date curDate = new Date(System.currentTimeMillis());
            String time = formatter.format(curDate);
            messageInfo.setTime(time);
            messageInfo.setType(0);
            messageInfo.setSendStatus(2);
            if (postfix.equals("Image")) {
                messageInfo.setMsgType(TO_IMG_MSG);
            } else if (postfix.equals("Video")) {
                messageInfo.setMsgType(TO_VIDEO_MSG);
            } else {
                messageInfo.setMsgType(TO_FILE_MSG);
            }
            messageInfo.setSend(UtilTool.getJid());
            if (mMgr.findConversation(roomId)) {
                if (postfix.equals("Image")) {
                    mMgr.updateConversation(roomId, 0, "[" + context.getString(R.string.image) + "]", time);
                    messageInfo.setConverstaion("[" + context.getString(R.string.image) + "]");
                } else if (postfix.equals("Video")){
                    mMgr.updateConversation(roomId, 0, "[" + context.getString(R.string.video) + "]", time);
                    messageInfo.setConverstaion("[" + context.getString(R.string.video) + "]");
                }else {
                    mMgr.updateConversation(roomId, 0, "[" + context.getString(R.string.file) + "]", time);
                    messageInfo.setConverstaion("[" + context.getString(R.string.file) + "]");
                }
            } else {
                ConversationInfo info = new ConversationInfo();
                info.setTime(time);
                info.setFriend(dbRoomManage.findRoomName(roomId));
                info.setUser(roomId);
                if (postfix.equals("Image")) {
                    info.setMessage("[" + context.getString(R.string.image) + "]");
                    messageInfo.setConverstaion("[" + context.getString(R.string.image) + "]");
                }else if (postfix.equals("Video")) {
                    info.setMessage("[" + context.getString(R.string.video) + "]");
                    messageInfo.setConverstaion("[" + context.getString(R.string.video) + "]");
                }else {
                    info.setMessage("[" + context.getString(R.string.file) + "]");
                    messageInfo.setConverstaion("[" + context.getString(R.string.file) + "]");
                }
                info.setChatType(RoomManage.ROOM_TYPE_MULTI);
                mMgr.addConversation(info);
            }
            mId=mMgr.addMessage(messageInfo);
            messageInfo.setId(mId);
            refreshAddData(messageInfo);
            EventBus.getDefault().post(new MessageEvent(context.getString(R.string.oneself_send_msg)));
        }
        return mId;
    }



    @Override
    public MultiUserChat createRoom(String roomJid,String roomName, String nickName, List<UserInfo> users) {
        this.roomId=roomJid;
        if (XmppConnection.getInstance().getConnection() == null)
            return null;
        MultiUserChat muc = null;
        try {
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
            maxuser.add(RoomManage.ROOM_MAX_NUMBER+"");
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
            createRoom(users,roomJid,roomName,owners);

        } catch (XMPPException | XmppStringprepException | SmackException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
        return muc;
    }

    private void createRoom(List<UserInfo> mUserInfoList, final String roomJid, final String roomName, final List<String> owners){
        GroupPresenter presenter=new GroupPresenter(context);
        StringBuffer stringBuffer=new StringBuffer();
        stringBuffer.append(UtilTool.getUser()+"@"+Constants.DOMAINNAME+",");
        for (int i = 0; i < mUserInfoList.size(); i++) {  //添加群成员,用户jid格式和之前一样 用户名@openfire服务器名称
            stringBuffer.append(mUserInfoList.get(i).getUser());
            if(i!=mUserInfoList.size()-1){
                stringBuffer.append(",");
            }
        }
        presenter.createGroup(roomJid,roomName, RoomManage.ROOM_MAX_NUMBER, "", stringBuffer.toString(), new GroupPresenter.CallBack() {
            @Override
            public void send() {
                try {
                    dbRoomManage.addRoom(createRoomInfo(roomJid,roomName));
                    grantOwnership(owners,roomJid,roomName);
                } catch (XMPPException e) {
                    e.printStackTrace();
                }
            }
        });
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

    @Override
    public void anewSendCard(MessageInfo messageInfo) {
        mMgr.deleteSingleMessage(roomId,messageInfo.getId()+"");
        sendCaed(messageInfo);
    }

    @Override
    public boolean sendCaed(MessageInfo messageInfo) {
        String converstaion="[" + context.getString(R.string.person_business_card) + "]";
        try {
            //创建jid实体
            EntityBareJid groupJid = JidCreate.entityBareFrom(roomId);
            //群管理对象
            MultiUserChatManager multiUserChatManager = MultiUserChatManager.getInstanceFor(XmppConnection.getInstance().getConnection());
            MultiUserChat multiUserChat = multiUserChatManager.getMultiUserChat(groupJid);
            multiUserChat.sendMessage(Constants.CARD+":"+JSON.toJSONString(messageInfo));

            messageInfo.setUsername(roomId);
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date curDate = new Date(System.currentTimeMillis());
            String time = formatter.format(curDate);
            messageInfo.setTime(time);
            messageInfo.setType(0);
            messageInfo.setMsgType(TO_CARD_MSG);
            messageInfo.setSend(UtilTool.getJid());
            messageInfo.setConverstaion(converstaion);
            messageInfo.setId(mMgr.addMessage(messageInfo));
            if (mMgr.findConversation(roomId)) {
                mMgr.updateConversation(roomId, 0, converstaion, time);
            } else {
                ConversationInfo info = new ConversationInfo();
                info.setTime(time);
                info.setFriend(roomName);
                info.setUser(roomId);
                info.setMessage(converstaion);
                info.setChatType(RoomManage.ROOM_TYPE_MULTI);
                mMgr.addConversation(info);
            }
            EventBus.getDefault().post(new MessageEvent(context.getString(R.string.oneself_send_msg)));
            refreshAddData(messageInfo);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            messageInfo.setUsername(roomId);
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date curDate = new Date(System.currentTimeMillis());
            String time = formatter.format(curDate);
            messageInfo.setTime(time);
            messageInfo.setType(0);
            messageInfo.setMsgType(TO_CARD_MSG);
            messageInfo.setSendStatus(1);
            messageInfo.setSend(UtilTool.getJid());
            messageInfo.setConverstaion(converstaion);
            messageInfo.setId(mMgr.addMessage(messageInfo));

            if (mMgr.findConversation(roomId)) {
                mMgr.updateConversation(roomId, 0, converstaion, time);
            } else {
                ConversationInfo info = new ConversationInfo();
                info.setTime(time);
                info.setFriend(roomName);
                info.setUser(roomId);
                info.setMessage(converstaion);
                info.setChatType(RoomManage.ROOM_TYPE_MULTI);
                mMgr.addConversation(info);
            }
            EventBus.getDefault().post(new MessageEvent(context.getString(R.string.oneself_send_msg)));
            refreshAddData(messageInfo);
            return false;
        }
    }


    @Override
    public void anewSendShareLink(MessageInfo messageInfo) {
        mMgr.deleteSingleMessage(roomId,messageInfo.getId()+"");
        sendShareLink(messageInfo);
    }

    @Override
    public boolean sendShareLink(MessageInfo messageInfo) {
        String converstaion="[" + context.getString(R.string.share) + "]";
        try {
            //创建jid实体
            EntityBareJid groupJid = JidCreate.entityBareFrom(roomId);
            //群管理对象
            MultiUserChatManager multiUserChatManager = MultiUserChatManager.getInstanceFor(XmppConnection.getInstance().getConnection());
            MultiUserChat multiUserChat = multiUserChatManager.getMultiUserChat(groupJid);
            multiUserChat.sendMessage(Constants.SHARE_LINK+":"+JSON.toJSONString(messageInfo));

            messageInfo.setUsername(roomId);
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date curDate = new Date(System.currentTimeMillis());
            String time = formatter.format(curDate);
            messageInfo.setTime(time);
            messageInfo.setType(0);
            messageInfo.setMsgType(TO_LINK_MSG);
            messageInfo.setSend(UtilTool.getJid());
            messageInfo.setConverstaion(converstaion);
            messageInfo.setId(mMgr.addMessage(messageInfo));
            if (mMgr.findConversation(roomId)) {
                mMgr.updateConversation(roomId, 0, converstaion, time);
            } else {
                ConversationInfo info = new ConversationInfo();
                info.setTime(time);
                info.setFriend(roomName);
                info.setUser(roomId);
                info.setMessage(converstaion);
                info.setChatType(RoomManage.ROOM_TYPE_MULTI);
                mMgr.addConversation(info);
            }
            EventBus.getDefault().post(new MessageEvent(context.getString(R.string.oneself_send_msg)));
            refreshAddData(messageInfo);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            messageInfo.setUsername(roomId);
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date curDate = new Date(System.currentTimeMillis());
            String time = formatter.format(curDate);
            messageInfo.setTime(time);
            messageInfo.setType(0);
            messageInfo.setMsgType(TO_LINK_MSG);
            messageInfo.setSendStatus(1);
            messageInfo.setSend(UtilTool.getJid());
            messageInfo.setConverstaion(converstaion);
            messageInfo.setId(mMgr.addMessage(messageInfo));
            if (mMgr.findConversation(roomId)) {
                mMgr.updateConversation(roomId, 0, converstaion, time);
            } else {
                ConversationInfo info = new ConversationInfo();
                info.setTime(time);
                info.setFriend(roomName);
                info.setUser(roomId);
                info.setMessage(converstaion);
                info.setChatType(RoomManage.ROOM_TYPE_MULTI);
                mMgr.addConversation(info);
            }
            EventBus.getDefault().post(new MessageEvent(context.getString(R.string.oneself_send_msg)));
            refreshAddData(messageInfo);
            return false;
        }
    }

    @Override
    public void anewSendShareGuess(MessageInfo messageInfo) {
        mMgr.deleteSingleMessage(roomId,messageInfo.getId()+"");
        sendShareGuess(messageInfo);
    }

    @Override
    public void transmitVideo(MessageInfo messageInfo) {

    }


    @Override
    public boolean sendShareGuess(MessageInfo messageInfo) {
        String converstaion="[" + context.getString(R.string.share_guess) + "]";
        try {
            //创建jid实体
            EntityBareJid groupJid = JidCreate.entityBareFrom(roomId);
            //群管理对象
            MultiUserChatManager multiUserChatManager = MultiUserChatManager.getInstanceFor(XmppConnection.getInstance().getConnection());
            MultiUserChat multiUserChat = multiUserChatManager.getMultiUserChat(groupJid);
            multiUserChat.sendMessage(Constants.SHARE_GUESS+":"+JSON.toJSONString(messageInfo));

            messageInfo.setUsername(roomId);
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date curDate = new Date(System.currentTimeMillis());
            String time = formatter.format(curDate);
            messageInfo.setTime(time);
            messageInfo.setType(0);
            messageInfo.setMsgType(TO_GUESS_MSG);
            messageInfo.setSend(UtilTool.getJid());
            messageInfo.setConverstaion(converstaion);
            messageInfo.setId(mMgr.addMessage(messageInfo));
            if (mMgr.findConversation(roomId)) {
                mMgr.updateConversation(roomId, 0, converstaion, time);
            } else {
                ConversationInfo info = new ConversationInfo();
                info.setTime(time);
                info.setFriend(roomName);
                info.setUser(roomId);
                info.setMessage(converstaion);
                info.setChatType(RoomManage.ROOM_TYPE_MULTI);
                mMgr.addConversation(info);
            }
            EventBus.getDefault().post(new MessageEvent(context.getString(R.string.oneself_send_msg)));
            refreshAddData(messageInfo);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            messageInfo.setUsername(roomId);
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date curDate = new Date(System.currentTimeMillis());
            String time = formatter.format(curDate);
            messageInfo.setTime(time);
            messageInfo.setType(0);
            messageInfo.setMsgType(TO_GUESS_MSG);
            messageInfo.setSendStatus(1);
            messageInfo.setSend(UtilTool.getJid());
            messageInfo.setConverstaion(converstaion);
            messageInfo.setId(mMgr.addMessage(messageInfo));
            if (mMgr.findConversation(roomId)) {
                mMgr.updateConversation(roomId, 0, converstaion, time);
            } else {
                ConversationInfo info = new ConversationInfo();
                info.setTime(time);
                info.setFriend(roomName);
                info.setUser(roomId);
                info.setMessage(converstaion);
                info.setChatType(RoomManage.ROOM_TYPE_MULTI);
                mMgr.addConversation(info);
            }
            EventBus.getDefault().post(new MessageEvent(context.getString(R.string.oneself_send_msg)));
            refreshAddData(messageInfo);
            return false;
        }
    }

}
