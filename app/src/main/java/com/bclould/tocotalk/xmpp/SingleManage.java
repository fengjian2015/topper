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
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
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
import com.bclould.tocotalk.model.UserInfo;
import com.bclould.tocotalk.topperchat.WsConnection;
import com.bclould.tocotalk.topperchat.WsContans;
import com.bclould.tocotalk.utils.Constants;
import com.bclould.tocotalk.utils.MessageEvent;
import com.bclould.tocotalk.utils.StringUtils;
import com.bclould.tocotalk.utils.UtilTool;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.greenrobot.eventbus.EventBus;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.msgpack.jackson.dataformat.MessagePackFactory;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
 * Created by GIjia on 2018/5/17.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class SingleManage implements Room {
    private DBManager mMgr;
    private String mUser;//對方id
    private Context context;
    private String mName;
    private ArrayList<MessageManageListener> listeners = new ArrayList<>();

    public SingleManage(DBManager mMgr, String mUser, Context context, String name) {
        this.mMgr = mMgr;
        this.mUser = mUser;
        this.context = context;
        this.mName = name;
        if(StringUtils.isEmpty(mName)){
            this.mName=mMgr.findUserName(mUser);
        }
    }


    private boolean send(String to,byte[] attachment,String body,int msgType) throws Exception{
            ObjectMapper objectMapper =  new ObjectMapper(new MessagePackFactory());
            Map<Object,Object> messageMap = new HashMap<>();
            messageMap.put("body",OtrChatListenerManager.getInstance().sentMessagesChange(body,
                    OtrChatListenerManager.getInstance().sessionID(UtilTool.getTocoId(), String.valueOf(mUser))));
            messageMap.put("attachment",attachment);

            Map<Object,Object> contentMap = new HashMap<>();
            contentMap.put("to",to);
            contentMap.put("from",UtilTool.getTocoId());
            if("true".equals(OtrChatListenerManager.getInstance().getOTRState(mUser.toString()))){
                contentMap.put("crypt",true);
            }else{
                contentMap.put("crypt",false);
            }
            contentMap.put("message",objectMapper.writeValueAsBytes(messageMap));
            contentMap.put("type",msgType);

            Map<Object,Object> sendMap = new HashMap<>();
            sendMap.put("type",3);
            sendMap.put("content",objectMapper.writeValueAsBytes(contentMap));
            WsConnection.getInstance().sendMessage(objectMapper.writeValueAsBytes(sendMap));
            return true;
    }

    private boolean sendOTR(String to,byte[] attachment,String body,int msgType) throws Exception{
        ObjectMapper objectMapper =  new ObjectMapper(new MessagePackFactory());
        Map<Object,Object> messageMap = new HashMap<>();
        messageMap.put("body",body);
        messageMap.put("attachment",attachment);

        Map<Object,Object> contentMap = new HashMap<>();
        contentMap.put("to",to);
        contentMap.put("from",UtilTool.getTocoId());
        contentMap.put("crypt",true);
        contentMap.put("message",objectMapper.writeValueAsBytes(messageMap));
        contentMap.put("type",msgType);

        Map<Object,Object> sendMap = new HashMap<>();
        sendMap.put("type",3);
        sendMap.put("content",objectMapper.writeValueAsBytes(contentMap));
        WsConnection.getInstance().sendMessage(objectMapper.writeValueAsBytes(sendMap));
        return true;
    }

    @Override
    public void addMessageManageListener(MessageManageListener messageManageListener) {
        listeners.add(messageManageListener);
    }

    @Override
    public void removerMessageManageListener(MessageManageListener messageManageListener) {
        if (listeners.contains(messageManageListener)) {
            listeners.remove(messageManageListener);
        }
    }

    private void refreshAddData(MessageInfo messageInfo) {
        if (listeners != null) {
            for (MessageManageListener messageManageListener : listeners) {
                messageManageListener.refreshAddData(messageInfo);
            }
        }
    }

    private void sendFileResults(String newFile2, boolean isSuccess) {
        if (listeners != null) {
            for (MessageManageListener messageManageListener : listeners) {
                messageManageListener.sendFileResults(newFile2, isSuccess);
            }
        }
    }

    private void sendError(int id) {
        if (listeners != null) {
            for (MessageManageListener messageManageListener : listeners) {
                messageManageListener.sendError(id);
            }
        }
    }

    @Override
    public boolean anewSendText(String message, int id) {
        try {
            mMgr.deleteSingleMessage(mUser,id+"");
            sendMessage(message);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    //发送OTR加密請求
    public void sendOTR(String message) {
        try {
            sendOTR(mUser,null,message, WsContans.MSG_TEXT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //发送文本消息
    public MessageInfo sendMessage(String message) {
        try {
            MessageInfo sendMessage=new MessageInfo();
            sendMessage.setMessage(message);
            send(mUser,null,JSON.toJSONString(sendMessage), WsContans.MSG_TEXT);

            MessageInfo messageInfo = new MessageInfo();
            messageInfo.setUsername(mUser);
            messageInfo.setMessage(message);
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date curDate = new Date(System.currentTimeMillis());
            String time = formatter.format(curDate);
            messageInfo.setTime(time);
            messageInfo.setType(0);
            messageInfo.setMsgType(TO_TEXT_MSG);
            messageInfo.setSend(UtilTool.getTocoId());
            messageInfo.setConverstaion(message);
            messageInfo.setId(mMgr.addMessage(messageInfo));
            if (mMgr.findConversation(mUser)) {
                mMgr.updateConversation(mUser, 0, message, time);
            } else {
                ConversationInfo info = new ConversationInfo();
                info.setTime(time);
                info.setFriend(mName);
                info.setUser(mUser);
                info.setMessage(message);
                info.setChatType(RoomManage.ROOM_TYPE_SINGLE);
                mMgr.addConversation(info);
            }
            EventBus.getDefault().post(new MessageEvent(context.getString(R.string.oneself_send_msg)));
            refreshAddData(messageInfo);
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
            messageInfo.setSend(UtilTool.getTocoId());
            messageInfo.setConverstaion(message);
            messageInfo.setId(mMgr.addMessage(messageInfo));

            if (mMgr.findConversation(mUser)) {
                mMgr.updateConversation(mUser, 0, message, time);
            } else {
                ConversationInfo info = new ConversationInfo();
                info.setTime(time);
                info.setFriend(mName);
                info.setUser(mUser);
                info.setMessage(message);
                info.setChatType(RoomManage.ROOM_TYPE_SINGLE);
                mMgr.addConversation(info);
            }
            EventBus.getDefault().post(new MessageEvent(context.getString(R.string.oneself_send_msg)));
            refreshAddData(messageInfo);
            return messageInfo;
        }
    }

    @Override
    public void anewSendUpload(MessageInfo messageInfo) {
        mMgr.deleteSingleMessage(mUser,messageInfo.getId()+"");
        Upload(messageInfo.getMessage());
    }

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
        if (!".gif".equals(postfixs) && !".GIF".equals(postfixs)) {
            UtilTool.comp(bitmap, newFile);//压缩图片
        } else {
            UtilTool.copyFile(file.getAbsolutePath(), newFile.getAbsolutePath());
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
                                mId[0] = sendFileMessage(path, postfix, key, newFile.getPath());
                            }

                            //上传文件完成
                            @Override
                            public void afterResponse(Request<?> request, Response<?> response) {
                                sendFileAfterMessage(key, postfix, newFile.getPath(), mId[0]);
                            }

                            @Override
                            public void afterError(Request<?> request, Response<?> response, Exception e) {
                                UtilTool.Log("aws", "错误");
                            }
                        });
                        //实例化上传请求
                        PutObjectRequest por = new PutObjectRequest(Constants.BUCKET_NAME2, key, file);
                        //开始上传
                        s3Client.putObject(por);
                    } catch (Exception e) {
                        e.printStackTrace();
                        mMgr.updateMessageHint(mId[0], 2);
                        sendError(mId[0]);
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
                                            //上传之前把消息储存数据库
                                            public void beforeRequest(Request<?> request) {
                                                UtilTool.Log("aws", "之前");
                                                mId[0] = sendFileMessage(path, postfix, key, newFile.getPath());
                                            }

                                            //上传文件完成
                                            @Override
                                            public void afterResponse(Request<?> request, Response<?> response) {
                                                sendFileAfterMessage(key, postfix, newFile.getPath(), mId[0]);
                                            }

                                            @Override
                                            public void afterError(Request<?> request, Response<?> response, Exception e) {
                                                UtilTool.Log("aws", "错误");
                                            }
                                        });
                                        PutObjectRequest por = new PutObjectRequest(Constants.BUCKET_NAME2, key, file);
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

    //僅僅是url鏈接
    @Override
    public void transmitVideo(MessageInfo messageInfo) {
       int mid= sendFileMessage(messageInfo.getMessage(),"Video","",messageInfo.getVoice());
        sendFileAfterMessage(messageInfo.getMessage(),"Video",messageInfo.getVoice(),mid);
    }

    private void sendFileAfterMessage(String key, String postfix, String newFile, int mId) {
        try {
            byte[] bytes = UtilTool.readStream(newFile);
            MessageInfo sendMessage=new MessageInfo();
            sendMessage.setKey(key);
            int type = 0;
            if (postfix.equals("Image")) {
                type=WsContans.MSG_IMAGE;
            } else if (postfix.equals("Video")) {
                type=WsContans.MSG_VIDEO;
            }
            send(mUser,bytes,JSON.toJSONString(sendMessage),type);
            mMgr.updateMessageHint(mId, 1);
            sendFileResults(newFile, true);
            return;
        } catch (Exception e) {
            mMgr.updateMessageHint(mId, 2);
            sendFileResults(newFile, false);
        }
    }

    //发送文件消息
    public int sendFileMessage(String path, String postfix, String key, String newFile) {
        int mId;
        MessageInfo messageInfo = new MessageInfo();
        try {
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
            messageInfo.setSend(UtilTool.getTocoId());
            if (mMgr.findConversation(mUser)) {
                if (postfix.equals("Image")) {
                    mMgr.updateConversation(mUser, 0, "[" + context.getString(R.string.image) + "]", time);
                    messageInfo.setConverstaion("[" + context.getString(R.string.image) + "]");
                } else if (postfix.equals("Video")) {
                    mMgr.updateConversation(mUser, 0, "[" + context.getString(R.string.video) + "]", time);
                    messageInfo.setConverstaion("[" + context.getString(R.string.video) + "]");
                } else {
                    mMgr.updateConversation(mUser, 0, "[" + context.getString(R.string.file) + "]", time);
                    messageInfo.setConverstaion("[" + context.getString(R.string.file) + "]");
                }
            } else {
                ConversationInfo info = new ConversationInfo();
                info.setTime(time);
                info.setFriend(mName);
                info.setUser(mUser);
                if (postfix.equals("Image")) {
                    info.setMessage("[" + context.getString(R.string.image) + "]");
                    messageInfo.setConverstaion("[" + context.getString(R.string.image) + "]");
                } else if (postfix.equals("Video")) {
                    info.setMessage("[" + context.getString(R.string.video) + "]");
                    messageInfo.setConverstaion("[" + context.getString(R.string.video) + "]");
                } else {
                    info.setMessage("[" + context.getString(R.string.file) + "]");
                    messageInfo.setConverstaion("[" + context.getString(R.string.file) + "]");
                }
                info.setChatType(RoomManage.ROOM_TYPE_SINGLE);
                mMgr.addConversation(info);
            }
            mId = mMgr.addMessage(messageInfo);
            messageInfo.setId(mId);
            refreshAddData(messageInfo);
            EventBus.getDefault().post(new MessageEvent(context.getString(R.string.oneself_send_msg)));
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, context.getString(R.string.send_error), Toast.LENGTH_SHORT).show();
            messageInfo.setUsername(mUser);
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
            messageInfo.setSend(UtilTool.getTocoId());
            if (mMgr.findConversation(mUser)) {
                if (postfix.equals("Image")) {
                    mMgr.updateConversation(mUser, 0, "[" + context.getString(R.string.image) + "]", time);
                    messageInfo.setConverstaion("[" + context.getString(R.string.image) + "]");
                } else if (postfix.equals("Video")) {
                    mMgr.updateConversation(mUser, 0, "[" + context.getString(R.string.video) + "]", time);
                    messageInfo.setConverstaion("[" + context.getString(R.string.video) + "]");
                } else {
                    mMgr.updateConversation(mUser, 0, "[" + context.getString(R.string.file) + "]", time);
                    messageInfo.setConverstaion("[" + context.getString(R.string.file) + "]");
                }
            } else {
                ConversationInfo info = new ConversationInfo();
                info.setTime(time);
                info.setFriend(mName);
                info.setUser(mUser);
                if (postfix.equals("Image")) {
                    info.setMessage("[" + context.getString(R.string.image) + "]");
                    messageInfo.setConverstaion("[" + context.getString(R.string.image) + "]");
                } else if (postfix.equals("Video")) {
                    info.setMessage("[" + context.getString(R.string.video) + "]");
                    messageInfo.setConverstaion("[" + context.getString(R.string.video) + "]");
                } else {
                    info.setMessage("[" + context.getString(R.string.file) + "]");
                    messageInfo.setConverstaion("[" + context.getString(R.string.file) + "]");
                }
                info.setChatType(RoomManage.ROOM_TYPE_SINGLE);
                mMgr.addConversation(info);
            }
            mId = mMgr.addMessage(messageInfo);
            messageInfo.setId(mId);
            refreshAddData(messageInfo);
            EventBus.getDefault().post(new MessageEvent(context.getString(R.string.oneself_send_msg)));
        }
        return mId;
    }


    @Override
    public void anewSendLocation(MessageInfo messageInfo) {
        mMgr.deleteSingleMessage(mUser,messageInfo.getId()+"");
        sendLocationMessage(messageInfo.getVoice(),null,messageInfo.getTitle(),messageInfo.getAddress(),messageInfo.getLat(),messageInfo.getLng());
    }

    public void sendLocationMessage(String file,Bitmap bitmap, String title, String address, float lat, float lng){
        String converstaion="[" + context.getString(R.string.location) + "]";
        final String postfix = "LOCATION";//获取文件后缀
        final String key = UtilTool.getUserId() + UtilTool.createtFileName() + ".AN.jpg";//命名aws文件名
        //缩略图储存路径
        File newFile;
        if(StringUtils.isEmpty(file)) {
            newFile = new File(Constants.PUBLICDIR + key);
            UtilTool.comp(bitmap, newFile);//压缩图片
        }else{
            newFile=new File(file);
        }
        int mId;
        try {
            MessageInfo messageInfo = new MessageInfo();
            messageInfo.setTitle(title);
            messageInfo.setAddress(address);
            messageInfo.setLat(lat);
            messageInfo.setLng(lng);
            byte[] bytes = UtilTool.readStream(newFile.getAbsolutePath());
            send(mUser,bytes,JSON.toJSONString(messageInfo),WsContans.MSG_LOCATION);

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
            messageInfo.setSend(UtilTool.getTocoId());
            messageInfo.setConverstaion(converstaion);
            mId = mMgr.addMessage(messageInfo);
            messageInfo.setId(mId);
            refreshAddData(messageInfo);
            //给聊天列表更新消息
            if (mMgr.findConversation(mUser)) {
                mMgr.updateConversation(mUser, 0, converstaion, time);
            } else {
                ConversationInfo info = new ConversationInfo();
                info.setTime(time);
                info.setFriend(mName);
                info.setUser(mUser);
                info.setMessage(converstaion);
                info.setChatType(RoomManage.ROOM_TYPE_SINGLE);
                mMgr.addConversation(info);
            }
            //发送消息通知
            EventBus.getDefault().post(new MessageEvent(context.getString(R.string.oneself_send_msg)));
            mMgr.updateMessageHint(mId, 1);
            sendFileResults(newFile.getAbsolutePath(), true);
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
            messageInfo.setTitle(title);
            messageInfo.setAddress(address);
            messageInfo.setLat(lat);
            messageInfo.setLng(lng);
            messageInfo.setMsgType(TO_LOCATION_MSG);
            messageInfo.setSendStatus(2);
            messageInfo.setConverstaion(converstaion);
            mId = mMgr.addMessage(messageInfo);
            messageInfo.setId(mId);
            refreshAddData(messageInfo);

            if (mMgr.findConversation(mUser)) {
                mMgr.updateConversation(mUser, 0, converstaion, time);
            } else {
                ConversationInfo info = new ConversationInfo();
                info.setTime(time);
                info.setFriend(mName);
                info.setUser(mUser);
                info.setMessage(converstaion);
                info.setChatType(RoomManage.ROOM_TYPE_SINGLE);
                mMgr.addConversation(info);
            }
            EventBus.getDefault().post(new MessageEvent(context.getString(R.string.oneself_send_msg)));

            mMgr.updateMessageHint(mId, 2);
            sendFileResults(newFile.getAbsolutePath(), false);

        }
    }


    @Override
    public void createRoom(String roomJid, String roomName, List<UserInfo> users) {

    }

    @Override
    public void joinMultiUserChat(String user, String roomJid) {
    }

    @Override
    public void changeName(String name) {

    }

    @Override
    public void anewSendCard(MessageInfo messageInfo) {
        mMgr.deleteSingleMessage(mUser,messageInfo.getId()+"");
        sendCaed(messageInfo);
    }

    @Override
    public boolean sendCaed(MessageInfo messageInfo) {
        String converstaion = "[" + context.getString(R.string.person_business_card) + "]";
        try {
            send(mUser,null,JSON.toJSONString(messageInfo),WsContans.MSG_CARD);
            messageInfo.setUsername(mUser);
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date curDate = new Date(System.currentTimeMillis());
            String time = formatter.format(curDate);
            messageInfo.setTime(time);
            messageInfo.setType(0);
            messageInfo.setMsgType(TO_CARD_MSG);
            messageInfo.setSend(UtilTool.getTocoId());
            messageInfo.setSendStatus(1);
            messageInfo.setConverstaion(converstaion);
            messageInfo.setId(mMgr.addMessage(messageInfo));
            if (mMgr.findConversation(mUser)) {
                mMgr.updateConversation(mUser, 0, converstaion, time);
            } else {
                ConversationInfo info = new ConversationInfo();
                info.setTime(time);
                info.setFriend(mName);
                info.setUser(mUser);
                info.setMessage(converstaion);
                info.setChatType(RoomManage.ROOM_TYPE_SINGLE);
                mMgr.addConversation(info);
            }
            EventBus.getDefault().post(new MessageEvent(context.getString(R.string.oneself_send_msg)));
            refreshAddData(messageInfo);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, context.getString(R.string.send_error), Toast.LENGTH_SHORT).show();
            messageInfo.setUsername(mUser);
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date curDate = new Date(System.currentTimeMillis());
            String time = formatter.format(curDate);
            messageInfo.setTime(time);
            messageInfo.setType(2);
            messageInfo.setMsgType(TO_CARD_MSG);
            messageInfo.setSendStatus(2);
            messageInfo.setSend(UtilTool.getTocoId());
            messageInfo.setConverstaion(converstaion);
            messageInfo.setId(mMgr.addMessage(messageInfo));

            if (mMgr.findConversation(mUser)) {
                mMgr.updateConversation(mUser, 0, converstaion, time);
            } else {
                ConversationInfo info = new ConversationInfo();
                info.setTime(time);
                info.setFriend(mName);
                info.setUser(mUser);
                info.setMessage(converstaion);
                info.setChatType(RoomManage.ROOM_TYPE_SINGLE);
                mMgr.addConversation(info);
            }
            EventBus.getDefault().post(new MessageEvent(context.getString(R.string.oneself_send_msg)));
            refreshAddData(messageInfo);
            return false;
        }
    }


    @Override
    public void anewSendShareLink(MessageInfo messageInfo) {
        mMgr.deleteSingleMessage(mUser,messageInfo.getId()+"");
        sendShareLink(messageInfo);
    }


    @Override
    public boolean sendShareLink(MessageInfo messageInfo) {
        String converstaion = "[" + context.getString(R.string.share) + "]";
        try {
            send(mUser,null,JSON.toJSONString(messageInfo),WsContans.MSG_SHARE_LINK);

            messageInfo.setUsername(mUser);
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date curDate = new Date(System.currentTimeMillis());
            String time = formatter.format(curDate);
            messageInfo.setTime(time);
            messageInfo.setType(0);
            messageInfo.setMsgType(TO_LINK_MSG);
            messageInfo.setSend(UtilTool.getTocoId());
            messageInfo.setSendStatus(1);
            messageInfo.setConverstaion(converstaion);
            messageInfo.setId(mMgr.addMessage(messageInfo));
            if (mMgr.findConversation(mUser)) {
                mMgr.updateConversation(mUser, 0, converstaion, time);
            } else {
                ConversationInfo info = new ConversationInfo();
                info.setTime(time);
                info.setFriend(mName);
                info.setUser(mUser);
                info.setMessage(converstaion);
                info.setChatType(RoomManage.ROOM_TYPE_SINGLE);
                mMgr.addConversation(info);
            }
            EventBus.getDefault().post(new MessageEvent(context.getString(R.string.oneself_send_msg)));
            refreshAddData(messageInfo);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, context.getString(R.string.send_error), Toast.LENGTH_SHORT).show();
            messageInfo.setUsername(mUser);
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date curDate = new Date(System.currentTimeMillis());
            String time = formatter.format(curDate);
            messageInfo.setTime(time);
            messageInfo.setType(2);
            messageInfo.setMsgType(TO_LINK_MSG);
            messageInfo.setSendStatus(2);
            messageInfo.setSend(UtilTool.getTocoId());
            messageInfo.setConverstaion(converstaion);
            messageInfo.setId(mMgr.addMessage(messageInfo));
            if (mMgr.findConversation(mUser)) {
                mMgr.updateConversation(mUser, 0, converstaion, time);
            } else {
                ConversationInfo info = new ConversationInfo();
                info.setTime(time);
                info.setFriend(mName);
                info.setUser(mUser);
                info.setMessage(converstaion);
                info.setChatType(RoomManage.ROOM_TYPE_SINGLE);
                mMgr.addConversation(info);
            }
            EventBus.getDefault().post(new MessageEvent(context.getString(R.string.oneself_send_msg)));
            refreshAddData(messageInfo);
            return false;
        }
    }

    @Override
    public void anewSendShareGuess(MessageInfo messageInfo) {
        mMgr.deleteSingleMessage(mUser,messageInfo.getId()+"");
        sendShareGuess(messageInfo);
    }


    @Override
    public boolean sendShareGuess(MessageInfo messageInfo) {
        String converstaion = "[" + context.getString(R.string.share_guess) + "]";
        try {
            send(mUser,null,JSON.toJSONString(messageInfo),WsContans.MSG_SHARE_GUESS);
            messageInfo.setUsername(mUser);
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date curDate = new Date(System.currentTimeMillis());
            String time = formatter.format(curDate);
            messageInfo.setTime(time);
            messageInfo.setType(0);
            messageInfo.setMsgType(TO_GUESS_MSG);
            messageInfo.setSend(UtilTool.getTocoId());
            messageInfo.setSendStatus(1);
            messageInfo.setConverstaion(converstaion);
            messageInfo.setId(mMgr.addMessage(messageInfo));
            if (mMgr.findConversation(mUser)) {
                mMgr.updateConversation(mUser, 0, converstaion, time);
            } else {
                ConversationInfo info = new ConversationInfo();
                info.setTime(time);
                info.setFriend(mName);
                info.setUser(mUser);
                info.setMessage(converstaion);
                info.setChatType(RoomManage.ROOM_TYPE_SINGLE);
                mMgr.addConversation(info);
            }
            EventBus.getDefault().post(new MessageEvent(context.getString(R.string.oneself_send_msg)));
            refreshAddData(messageInfo);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, context.getString(R.string.send_error), Toast.LENGTH_SHORT).show();
            messageInfo.setUsername(mUser);
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date curDate = new Date(System.currentTimeMillis());
            String time = formatter.format(curDate);
            messageInfo.setTime(time);
            messageInfo.setType(2);
            messageInfo.setMsgType(TO_GUESS_MSG);
            messageInfo.setSendStatus(2);
            messageInfo.setSend(UtilTool.getTocoId());
            messageInfo.setConverstaion(converstaion);
            messageInfo.setId(mMgr.addMessage(messageInfo));
            if (mMgr.findConversation(mUser)) {
                mMgr.updateConversation(mUser, 0, converstaion, time);
            } else {
                ConversationInfo info = new ConversationInfo();
                info.setTime(time);
                info.setFriend(mName);
                info.setUser(mUser);
                info.setMessage(converstaion);
                info.setChatType(RoomManage.ROOM_TYPE_SINGLE);
                mMgr.addConversation(info);
            }
            EventBus.getDefault().post(new MessageEvent(context.getString(R.string.oneself_send_msg)));
            refreshAddData(messageInfo);
            return false;
        }
    }


    @Override
    public boolean anewSendVoice(MessageInfo messageInfo) {
        try {
            mMgr.deleteSingleMessage(mUser,messageInfo.getId()+"");
            sendVoice(UtilTool.getFileDuration(messageInfo.getVoice(), context),messageInfo.getVoice());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    //发送录音
    public void sendVoice(int duration, String fileName) {
        String converstaion = "[" + context.getString(R.string.voice) + "]";
        try {
            byte[] bytes = UtilTool.readStream(fileName);//把语音文件转换成二进制
            MessageInfo messageInfo = new MessageInfo();
            messageInfo.setMessage(context.getString(R.string.voice));
            send(mUser,bytes, JSON.toJSONString(messageInfo),WsContans.MSG_AUDIO);

            //把消息添加进数据库
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date curDate = new Date(System.currentTimeMillis());
            String time = formatter.format(curDate);

            messageInfo.setType(0);
            messageInfo.setUsername(mUser);
            messageInfo.setVoice(fileName);
            messageInfo.setTime(time);
            messageInfo.setMsgType(TO_VOICE_MSG);
            messageInfo.setVoiceTime(duration + "");
            messageInfo.setVoiceStatus(1);
            messageInfo.setSend(UtilTool.getTocoId());
            messageInfo.setConverstaion(converstaion);
            messageInfo.setId(mMgr.addMessage(messageInfo));
            refreshAddData(messageInfo);

            //给聊天列表更新消息
            if (mMgr.findConversation(mUser)) {
                mMgr.updateConversation(mUser, 0, converstaion, time);
            } else {
                ConversationInfo info = new ConversationInfo();
                info.setTime(time);
                info.setFriend(mName);
                info.setUser(mUser);
                info.setMessage(converstaion);
                info.setChatType(RoomManage.ROOM_TYPE_SINGLE);
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
            messageInfo.setConverstaion(converstaion);
            messageInfo.setId(mMgr.addMessage(messageInfo));
            refreshAddData(messageInfo);
            if (mMgr.findConversation(mUser)) {
                mMgr.updateConversation(mUser, 0, converstaion, time);
            } else {
                ConversationInfo info = new ConversationInfo();
                info.setTime(time);
                info.setFriend(mName);
                info.setUser(mUser);
                info.setMessage(converstaion);
                info.setChatType(RoomManage.ROOM_TYPE_SINGLE);
                mMgr.addConversation(info);
            }
            EventBus.getDefault().post(new MessageEvent(context.getString(R.string.oneself_send_msg)));
        }

    }

    public void sendTransfer(String mRemark, String mCoin, String mCount) {
        try {
          String converstaion = "[" + context.getString(R.string.transfer) + "]";
          String message = Constants.TRANSFER + Constants.CHUANCODE + mRemark + Constants.CHUANCODE + mCoin + Constants.CHUANCODE + mCount;
            MessageInfo sendMessage=new MessageInfo();
            sendMessage.setRemark(mRemark);
            sendMessage.setCoin(mCoin);
            sendMessage.setCount(mCount);
            sendMessage.setMessage(message);
            send(mUser,null,JSON.toJSONString(sendMessage), WsContans.MSG_TRANSFER);

            MessageInfo messageInfo = new MessageInfo();
            messageInfo.setUsername(mUser);
            messageInfo.setMessage(message);
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date curDate = new Date(System.currentTimeMillis());
            String time = formatter.format(curDate);
            messageInfo.setTime(time);
            messageInfo.setRemark(mRemark);
            messageInfo.setCoin(mCoin);
            messageInfo.setMsgType(TO_TRANSFER_MSG);
            messageInfo.setCount(mCount);
            messageInfo.setStatus(0);
            messageInfo.setSend(UtilTool.getTocoId());
            messageInfo.setConverstaion(converstaion);
            mMgr.addMessage(messageInfo);
            if (mMgr.findConversation(mUser)) {
                mMgr.updateConversation(mUser, 0, converstaion, time);
            } else {
                ConversationInfo info = new ConversationInfo();
                info.setTime(time);
                info.setFriend(mName);
                info.setUser(mUser);
                info.setMessage(converstaion);
                info.setChatType(RoomManage.ROOM_TYPE_SINGLE);
                mMgr.addConversation(info);
            }
            EventBus.getDefault().post(new MessageEvent(context.getString(R.string.transfer)));
            EventBus.getDefault().post(new MessageEvent(context.getString(R.string.oneself_send_msg)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendRed(String mRemark, String mCoin, double mCount, int id) {
        String converstaion = "[" + context.getString(R.string.red_package) + "]";
        String message = Constants.REDBAG + Constants.CHUANCODE + mRemark + Constants.CHUANCODE + mCoin + Constants.CHUANCODE + mCount + Constants.CHUANCODE + id;
        try {
            MessageInfo sendMessage=new MessageInfo();
            sendMessage.setRemark(mRemark);
            sendMessage.setCoin(mCoin);
            sendMessage.setCount(mCount+"");
            sendMessage.setRedId(id);
            sendMessage.setMessage(message);
            send(mUser,null,JSON.toJSONString(sendMessage), WsContans.MSG_REDBAG);

            MessageInfo messageInfo = new MessageInfo();
            messageInfo.setUsername(mUser);
            messageInfo.setMessage(message);
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
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
            messageInfo.setSend(UtilTool.getTocoId());
            messageInfo.setConverstaion(converstaion);
            mMgr.addMessage(messageInfo);
            if (mMgr.findConversation(mUser)) {
                mMgr.updateConversation(mUser, 0, converstaion, time);
            } else {
                ConversationInfo info = new ConversationInfo();
                info.setTime(time);
                info.setFriend(mName);
                info.setUser(mUser);
                info.setMessage(converstaion);
                info.setChatType(RoomManage.ROOM_TYPE_SINGLE);
                mMgr.addConversation(info);
            }
            EventBus.getDefault().post(new MessageEvent(context.getString(R.string.send_red_packet_le)));
            EventBus.getDefault().post(new MessageEvent(context.getString(R.string.oneself_send_msg)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
