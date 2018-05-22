package com.bclould.tocotalk.xmpp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.amazonaws.Request;
import com.amazonaws.Response;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.handlers.RequestHandler2;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.bclould.tocotalk.R;
import com.bclould.tocotalk.crypto.otr.OtrChatListenerManager;
import com.bclould.tocotalk.history.DBManager;
import com.bclould.tocotalk.model.ConversationInfo;
import com.bclould.tocotalk.model.MessageInfo;
import com.bclould.tocotalk.model.VoiceInfo;
import com.bclould.tocotalk.service.IMService;
import com.bclould.tocotalk.ui.activity.ConversationActivity;
import com.bclould.tocotalk.utils.Constants;
import com.bclould.tocotalk.utils.MessageEvent;
import com.bclould.tocotalk.utils.UtilTool;

import org.greenrobot.eventbus.EventBus;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.util.stringencoder.Base64;
import org.json.JSONObject;
import org.jxmpp.jid.impl.JidCreate;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

import static com.bclould.tocotalk.ui.adapter.ChatAdapter.TO_FILE_MSG;
import static com.bclould.tocotalk.ui.adapter.ChatAdapter.TO_IMG_MSG;
import static com.bclould.tocotalk.ui.adapter.ChatAdapter.TO_LOCATION_MSG;
import static com.bclould.tocotalk.ui.adapter.ChatAdapter.TO_TEXT_MSG;
import static com.bclould.tocotalk.ui.adapter.ChatAdapter.TO_VIDEO_MSG;
import static com.bclould.tocotalk.ui.adapter.ChatAdapter.TO_VOICE_MSG;

/**
 * Created by GIjia on 2018/5/17.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class MessageManage {
    private DBManager mMgr;
    private String mUser;
    private Context context;
    private String mName;
    private int mId;
    private Room room;

    public MessageManage(DBManager mMgr,String mUser,Context context,String mName){
        this.mMgr=mMgr;
        this.mUser=mUser;
        this.context= context;
        this.mName=mName;
    }

    public void addRoom(Room room){
        this.room=room;
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
            if(room!=null){
                room.refreshAddData(messageInfo);
            }
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
            if(room!=null){
                room.refreshAddData(messageInfo);
            }
            return messageInfo;
        }
    }

    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    Bundle bundle = (Bundle) msg.obj;
                    String key = bundle.getString("key");
                    String path = bundle.getString("path");
                    String postfix = bundle.getString("postfix");
                    String newFile = bundle.getString("newFile");
                    sendFileMessage(path, postfix, key, newFile);
                    break;
                case 2:
                    //文件上传成功发送消息
                    Bundle bundle2 = (Bundle) msg.obj;
                    String key2 = bundle2.getString("key");
                    String postfix2 = bundle2.getString("postfix");
                    String newFile2 = bundle2.getString("newFile");
                    try {
                        ChatManager manager = ChatManager.getInstanceFor(XmppConnection.getInstance().getConnection());
                        Chat chat = manager.createChat(JidCreate.entityBareFrom(mUser), null);
                        org.jivesoftware.smack.packet.Message message = new org.jivesoftware.smack.packet.Message();
                        VoiceInfo voiceInfo = new VoiceInfo();
                        byte[] bytes = UtilTool.readStream(newFile2);
                        String base64 = Base64.encodeToString(bytes);
                        voiceInfo.setElementText(base64);
                        message.addExtension(voiceInfo);
                        message.setBody(OtrChatListenerManager.getInstance().sentMessagesChange("[" + postfix2 + "]:" + key2,
                                OtrChatListenerManager.getInstance().sessionID(UtilTool.getJid(), String.valueOf(JidCreate.entityBareFrom(mUser)))));
                        chat.sendMessage(message);
                        if(room!=null){
                            mMgr.updateMessageHint(mId, 1);
                            room.sendFileResults(newFile2,true);
                        }
                        return;
                    } catch (Exception e) {
                        if(room!=null){
                            mMgr.updateMessageHint(mId, 2);
                            room.sendFileResults(newFile2,false);
                        }
                    }
                    break;
            }
        }
    };

    //上传文件到aws
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
                                Message message = new Message();
                                Bundle bundle = new Bundle();
                                bundle.putString("key", key);
                                bundle.putString("newFile", newFile.getPath());
                                bundle.putString("path", path);
                                bundle.putString("postfix", postfix);
                                message.obj = bundle;
                                message.what = 1;
                                handler.sendMessage(message);
                            }

                            //上传文件完成
                            @Override
                            public void afterResponse(Request<?> request, Response<?> response) {
                                Message message = new Message();
                                Bundle bundle = new Bundle();
                                bundle.putString("key", key);
                                bundle.putString("newFile", newFile.getPath());
                                bundle.putString("postfix", postfix);
                                message.obj = bundle;
                                message.what = 2;
                                handler.sendMessage(message);
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
                        mMgr.updateMessageHint(mId, 2);
                        if(room!=null){
                            room.sendError(mId);
                        }
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
                                                Message message = new Message();
                                                Bundle bundle = new Bundle();
                                                bundle.putString("key", key);
                                                bundle.putString("newFile", newFile.getPath());
                                                bundle.putString("path", path);
                                                bundle.putString("postfix", postfix);
                                                message.obj = bundle;
                                                message.what = 1;
                                                handler.sendMessage(message);
                                            }

                                            @Override
                                            public void afterResponse(Request<?> request, Response<?> response) {
                                                Message message = new Message();
                                                Bundle bundle = new Bundle();
                                                bundle.putString("key", key);
                                                bundle.putString("newFile", newFile.getPath());
                                                bundle.putString("postfix", postfix);
                                                message.obj = bundle;
                                                message.what = 2;
                                                handler.sendMessage(message);
                                            }

                                            @Override
                                            public void afterError(Request<?> request, Response<?> response, Exception e) {
                                                UtilTool.Log("aws", "错误");
                                            }
                                        });
                                        PutObjectRequest por = new PutObjectRequest(Constants.BUCKET_NAME, key, file);
                                        s3Client.putObject(por);
                                    } catch (Exception e) {
                                        mMgr.updateMessageHint(mId, 2);
                                        if(room!=null){
                                            room.sendError(mId);
                                        }
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

    public void sendLocationMessage(Bitmap bitmap, String title, String address, float lat, float lng){
        final String postfix = "LOCATION";//获取文件后缀
        final String key = UtilTool.getUserId() + UtilTool.createtFileName() + ".AN.jpg";//命名aws文件名
        //缩略图储存路径
        final File newFile = new File(Constants.PUBLICDIR + key);
        UtilTool.comp(bitmap, newFile);//压缩图片
        try {
            ChatManager manager = ChatManager.getInstanceFor(XmppConnection.getInstance().getConnection());
            Chat chat = manager.createChat(JidCreate.entityBareFrom(mUser), null);
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
            message.setBody(OtrChatListenerManager.getInstance().sentMessagesChange("[" + postfix + "]:" + JSON.toJSONString(messageInfo),
                    OtrChatListenerManager.getInstance().sessionID(UtilTool.getJid(), String.valueOf(JidCreate.entityBareFrom(mUser)))));
            chat.sendMessage(message);

            //把消息添加进数据库
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date curDate = new Date(System.currentTimeMillis());
            String time = formatter.format(curDate);

            messageInfo.setVoice(newFile.getAbsolutePath());
            messageInfo.setType(0);
            messageInfo.setUsername(mUser);
            messageInfo.setTime(time);
            messageInfo.setMessage(title);
            messageInfo.setMsgType(TO_LOCATION_MSG);
            messageInfo.setSend(UtilTool.getJid());
            mId= mMgr.addMessage(messageInfo);
            if(room!=null){
                room.refreshAddData(messageInfo);
            }
            //给聊天列表更新消息
            if (mMgr.findConversation(mUser)) {
                mMgr.updateConversation(mUser, 0, "[" + context.getString(R.string.location) + "]", time);
            } else {
                ConversationInfo info = new ConversationInfo();
                info.setTime(time);
                info.setFriend(mName);
                info.setUser(mUser);
                info.setMessage("[" + context.getString(R.string.location) + "]");
                mMgr.addConversation(info);
            }
            //发送消息通知
            EventBus.getDefault().post(new MessageEvent(context.getString(R.string.oneself_send_msg)));
            if(room!=null){
                mMgr.updateMessageHint(mId, 1);
                room.sendFileResults(newFile.getAbsolutePath(),true);
            }
            return;
        } catch (Exception e) {
            Toast.makeText(context, context.getString(R.string.send_error), Toast.LENGTH_SHORT).show();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date curDate = new Date(System.currentTimeMillis());
            String time = formatter.format(curDate);
            MessageInfo messageInfo = new MessageInfo();
            messageInfo.setVoice(newFile.getAbsolutePath());
            messageInfo.setMessage(title);
            messageInfo.setType(0);
            messageInfo.setUsername(mUser);
            messageInfo.setTime(time);
            messageInfo.setMsgType(TO_LOCATION_MSG);
            messageInfo.setSendStatus(2);
            mId=mMgr.addMessage(messageInfo);
            if(room!=null){
                room.refreshAddData(messageInfo);
            }
            if (mMgr.findConversation(mUser)) {
                mMgr.updateConversation(mUser, 0, "[" + context.getString(R.string.location) + "]", time);
            } else {
                ConversationInfo info = new ConversationInfo();
                info.setTime(time);
                info.setFriend(mName);
                info.setUser(mUser);
                info.setMessage("[" + context.getString(R.string.location) + "]");
                mMgr.addConversation(info);
            }
            EventBus.getDefault().post(new MessageEvent(context.getString(R.string.oneself_send_msg)));

            if(room!=null){
                mMgr.updateMessageHint(mId, 2);
                room.sendFileResults(newFile.getAbsolutePath(),false);
            }
        }
    }

    //发送文件消息
    public void sendFileMessage(String path, String postfix, String key, String newFile) {
        try {
            MessageInfo messageInfo = new MessageInfo();
            messageInfo.setUsername(mUser);
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
            mId = mMgr.addMessage(messageInfo);
            messageInfo.setId(mId);
            if(room!=null){
                room.refreshAddData(messageInfo);
            }
            if (mMgr.findConversation(mUser)) {
                if (postfix.equals("Image"))
                    mMgr.updateConversation(mUser, 0, "[" + context.getString(R.string.image) + "]", time);
                else if (postfix.equals("Video"))
                    mMgr.updateConversation(mUser, 0, "[" + context.getString(R.string.video) + "]", time);
                else
                    mMgr.updateConversation(mUser, 0, "[" + context.getString(R.string.file) + "]", time);
            } else {
                ConversationInfo info = new ConversationInfo();
                info.setTime(time);
                info.setFriend(mName);
                info.setUser(mUser);
                if (postfix.equals("Image"))
                    info.setMessage("[" + context.getString(R.string.image) + "]");
                else if (postfix.equals("Video"))
                    info.setMessage("[" + context.getString(R.string.video) + "]");
                else
                    info.setMessage("[" + context.getString(R.string.file) + "]");
                mMgr.addConversation(info);
            }
            EventBus.getDefault().post(new MessageEvent(context.getString(R.string.oneself_send_msg)));
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, context.getString(R.string.send_error), Toast.LENGTH_SHORT).show();
            MessageInfo messageInfo = new MessageInfo();
            messageInfo.setUsername(mUser);
            messageInfo.setVoice(path);
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
            mMgr.addMessage(messageInfo);
            if(room!=null){
                room.refreshAddData(messageInfo);
            }
            if (mMgr.findConversation(mUser)) {
                if (postfix.equals("Image"))
                    mMgr.updateConversation(mUser, 0, context.getString(R.string.image), time);
                else if (postfix.equals("Video"))
                    mMgr.updateConversation(mUser, 0, context.getString(R.string.video), time);
                else
                    mMgr.updateConversation(mUser, 0, context.getString(R.string.file), time);
            } else {
                ConversationInfo info = new ConversationInfo();
                info.setTime(time);
                info.setFriend(mName);
                info.setUser(mUser);
                if (postfix.equals("Image"))
                    info.setMessage("[" + context.getString(R.string.image) + "]");
                else if (postfix.equals("Video"))
                    info.setMessage("[" + context.getString(R.string.video) + "]");
                else
                    info.setMessage("[" + context.getString(R.string.file) + "]");
                mMgr.addConversation(info);
            }
            EventBus.getDefault().post(new MessageEvent(context.getString(R.string.oneself_send_msg)));
        }
    }


    //发送录音
    public void sendVoice(int duration, String fileName) {
        try {
            //实例化聊天管理类
            ChatManager manager = ChatManager.getInstanceFor(XmppConnection.getInstance().getConnection());
            Chat chat = manager.createChat(JidCreate.entityBareFrom(mUser), null);//实例化聊天类
            org.jivesoftware.smack.packet.Message message = new org.jivesoftware.smack.packet.Message();//实例化消息
            VoiceInfo voiceInfo = new VoiceInfo();//实例化语音model
            byte[] bytes = UtilTool.readStream(fileName);//把语音文件转换成二进制
            String base64 = Base64.encodeToString(bytes);//二进制转成Base64
            voiceInfo.setElementText(base64);//设置进model
            message.setBody(OtrChatListenerManager.getInstance().sentMessagesChange("[audio]:" + duration + context.getString(R.string.second),
                    OtrChatListenerManager.getInstance().sessionID(UtilTool.getJid(), String.valueOf(JidCreate.entityBareFrom(mUser)))));
            message.addExtension(voiceInfo);//扩展message,把语音添加进标签
            chat.sendMessage(message);//发送消息

            //把消息添加进数据库
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date curDate = new Date(System.currentTimeMillis());
            String time = formatter.format(curDate);
            MessageInfo messageInfo = new MessageInfo();
            messageInfo.setMessage(message.getBody());
            messageInfo.setType(0);
            messageInfo.setUsername(mUser);
            messageInfo.setVoice(fileName);
            messageInfo.setTime(time);
            messageInfo.setMsgType(TO_VOICE_MSG);
            messageInfo.setVoiceTime(duration + "");
            messageInfo.setVoiceStatus(1);
            messageInfo.setSend(UtilTool.getJid());
            mMgr.addMessage(messageInfo);
            if(room!=null){
                room.refreshAddData(messageInfo);
            }
            //给聊天列表更新消息
            if (mMgr.findConversation(mUser)) {
                mMgr.updateConversation(mUser, 0, "[" + context.getString(R.string.voice) + "]", time);
            } else {
                ConversationInfo info = new ConversationInfo();
                info.setTime(time);
                info.setFriend(mName);
                info.setUser(mUser);
                info.setMessage("[" + context.getString(R.string.voice) + "]");
                mMgr.addConversation(info);
            }
            //发送消息通知
            EventBus.getDefault().post(new MessageEvent(context.getString(R.string.oneself_send_msg)));
        } catch (Exception e) {
            //发送失败处理
            e.printStackTrace();
            Toast.makeText(context, context.getString(R.string.send_error), Toast.LENGTH_SHORT).show();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date curDate = new Date(System.currentTimeMillis());
            String time = formatter.format(curDate);
            MessageInfo messageInfo = new MessageInfo();
            messageInfo.setMessage("[audio]:" + duration + context.getString(R.string.second));
            messageInfo.setType(0);
            messageInfo.setUsername(mUser);
            messageInfo.setVoice(fileName);
            messageInfo.setTime(time);
            messageInfo.setMsgType(TO_VOICE_MSG);
            messageInfo.setVoiceTime(duration + "");
            messageInfo.setVoiceStatus(1);
            messageInfo.setSendStatus(1);
            mMgr.addMessage(messageInfo);
            if(room!=null){
                room.refreshAddData(messageInfo);
            }
            if (mMgr.findConversation(mUser)) {
                mMgr.updateConversation(mUser, 0, "[" + context.getString(R.string.voice) + "]", time);
            } else {
                ConversationInfo info = new ConversationInfo();
                info.setTime(time);
                info.setFriend(mName);
                info.setUser(mUser);
                info.setMessage("[" + context.getString(R.string.voice) + "]");
                mMgr.addConversation(info);
            }
            EventBus.getDefault().post(new MessageEvent(context.getString(R.string.oneself_send_msg)));
        }

    }
}
