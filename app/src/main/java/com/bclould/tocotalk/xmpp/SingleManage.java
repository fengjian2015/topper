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
import com.bclould.tocotalk.model.VoiceInfo;
import com.bclould.tocotalk.service.IMService;
import com.bclould.tocotalk.ui.activity.ConversationActivity;
import com.bclould.tocotalk.utils.Constants;
import com.bclould.tocotalk.utils.MessageEvent;
import com.bclould.tocotalk.utils.StringUtils;
import com.bclould.tocotalk.utils.UtilTool;

import org.greenrobot.eventbus.EventBus;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.util.stringencoder.Base64;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.json.JSONObject;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.impl.JidCreate;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
 * Created by GIjia on 2018/5/17.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class SingleManage implements Room {
    private DBManager mMgr;
    private String mUser;
    private Context context;
    private String mName;
    private ArrayList<MessageManageListener> listeners = new ArrayList<>();

    public SingleManage(DBManager mMgr, String mUser, Context context, String mName) {
        this.mMgr = mMgr;
        this.mUser = mUser;
        this.context = context;
        this.mName = mName;
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
            // TODO: 2018/5/31 重發失敗還是顯示成功 
            mMgr.deleteSingleMessage(mUser,id+"");
            sendMessage(message);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
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
            messageInfo.setSend(UtilTool.getJid());
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
            ChatManager manager = ChatManager.getInstanceFor(XmppConnection.getInstance().getConnection());
            Chat chat = manager.createChat(JidCreate.entityBareFrom(mUser), null);
            org.jivesoftware.smack.packet.Message message = new org.jivesoftware.smack.packet.Message();
            VoiceInfo voiceInfo = new VoiceInfo();
            byte[] bytes = UtilTool.readStream(newFile.getAbsolutePath());
            String base64 = Base64.encodeToString(bytes);
            voiceInfo.setElementText(base64);
            message.addExtension(voiceInfo);
            MessageInfo messageInfo = new MessageInfo();
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
    public MultiUserChat createRoom(String roomJid, String roomName, String password, List<UserInfo> users) {
        return null;
    }

    @Override
    public MultiUserChat joinMultiUserChat(String user, String roomJid) {
        return null;
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
            ChatManager manager = ChatManager.getInstanceFor(XmppConnection.getInstance().getConnection());
            Chat chat = manager.createChat(JidCreate.entityBareFrom(mUser), null);
            chat.sendMessage(OtrChatListenerManager.getInstance().sentMessagesChange(Constants.CARD + ":" + JSON.toJSONString(messageInfo),
                    OtrChatListenerManager.getInstance().sessionID(UtilTool.getJid(), String.valueOf(JidCreate.entityBareFrom(mUser)))));
            messageInfo.setUsername(mUser);
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date curDate = new Date(System.currentTimeMillis());
            String time = formatter.format(curDate);
            messageInfo.setTime(time);
            messageInfo.setType(0);
            messageInfo.setMsgType(TO_CARD_MSG);
            messageInfo.setSend(UtilTool.getJid());
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
            messageInfo.setSend(UtilTool.getJid());
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
            ChatManager manager = ChatManager.getInstanceFor(XmppConnection.getInstance().getConnection());
            Chat chat = manager.createChat(JidCreate.entityBareFrom(mUser), null);
            chat.sendMessage(OtrChatListenerManager.getInstance().sentMessagesChange(Constants.SHARE_LINK + ":" + JSON.toJSONString(messageInfo),
                    OtrChatListenerManager.getInstance().sessionID(UtilTool.getJid(), String.valueOf(JidCreate.entityBareFrom(mUser)))));
            messageInfo.setUsername(mUser);
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date curDate = new Date(System.currentTimeMillis());
            String time = formatter.format(curDate);
            messageInfo.setTime(time);
            messageInfo.setType(0);
            messageInfo.setMsgType(TO_LINK_MSG);
            messageInfo.setSend(UtilTool.getJid());
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
            messageInfo.setSend(UtilTool.getJid());
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
            ChatManager manager = ChatManager.getInstanceFor(XmppConnection.getInstance().getConnection());
            Chat chat = manager.createChat(JidCreate.entityBareFrom(mUser), null);
            chat.sendMessage(OtrChatListenerManager.getInstance().sentMessagesChange(Constants.SHARE_GUESS + ":" + JSON.toJSONString(messageInfo),
                    OtrChatListenerManager.getInstance().sessionID(UtilTool.getJid(), String.valueOf(JidCreate.entityBareFrom(mUser)))));
            messageInfo.setUsername(mUser);
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date curDate = new Date(System.currentTimeMillis());
            String time = formatter.format(curDate);
            messageInfo.setTime(time);
            messageInfo.setType(0);
            messageInfo.setMsgType(TO_GUESS_MSG);
            messageInfo.setSend(UtilTool.getJid());
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
            messageInfo.setSend(UtilTool.getJid());
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

    private void sendFileAfterMessage(String key, String postfix, String newFile, int mId) {
        try {
            ChatManager manager = ChatManager.getInstanceFor(XmppConnection.getInstance().getConnection());
            Chat chat = manager.createChat(JidCreate.entityBareFrom(mUser), null);
            org.jivesoftware.smack.packet.Message message = new org.jivesoftware.smack.packet.Message();
            VoiceInfo voiceInfo = new VoiceInfo();
            byte[] bytes = UtilTool.readStream(newFile);
            String base64 = Base64.encodeToString(bytes);
            voiceInfo.setElementText(base64);
            message.addExtension(voiceInfo);
            message.setBody(OtrChatListenerManager.getInstance().sentMessagesChange("[" + postfix + "]:" + key,
                    OtrChatListenerManager.getInstance().sessionID(UtilTool.getJid(), String.valueOf(JidCreate.entityBareFrom(mUser)))));
            chat.sendMessage(message);
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
            messageInfo.setSend(UtilTool.getJid());
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
            messageInfo.setSend(UtilTool.getJid());

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
        String converstaion = "[" + context.getString(R.string.transfer) + "]";
        String message = Constants.TRANSFER + Constants.CHUANCODE + mRemark + Constants.CHUANCODE + mCoin + Constants.CHUANCODE + mCount;
        try {
            ChatManager manager = ChatManager.getInstanceFor(XmppConnection.getInstance().getConnection());
            Chat chat = manager.createChat(JidCreate.entityBareFrom(mUser), null);
            chat.sendMessage(OtrChatListenerManager.getInstance().sentMessagesChange(message,
                    OtrChatListenerManager.getInstance().sessionID(UtilTool.getJid(), String.valueOf(JidCreate.entityBareFrom(mUser)))));
            MessageInfo messageInfo = new MessageInfo();
            messageInfo.setUsername(mUser);
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
            if (mMgr.findConversation(mUser)) {
                mMgr.updateConversation(mUser, 0, converstaion, time);
            } else {
                ConversationInfo info = new ConversationInfo();
                info.setTime(time);
                info.setFriend(mUser.substring(0, mUser.indexOf("@")));
                info.setUser(mUser);
                info.setMessage(converstaion);
                info.setChatType(RoomManage.ROOM_TYPE_SINGLE);
                mMgr.addConversation(info);
            }
            EventBus.getDefault().post(new MessageEvent(context.getString(R.string.transfer)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendRed(String mRemark, String mCoin, double mCount, int id) {
        String converstaion = "[" + context.getString(R.string.red_package) + "]";
        String message = Constants.REDBAG + Constants.CHUANCODE + mRemark + Constants.CHUANCODE + mCoin + Constants.CHUANCODE + mCount + Constants.CHUANCODE + id;
        try {
            ChatManager manager = ChatManager.getInstanceFor(XmppConnection.getInstance().getConnection());
            Chat chat = manager.createChat(JidCreate.entityBareFrom(mUser), null);
            chat.sendMessage(OtrChatListenerManager.getInstance().sentMessagesChange(message,
                    OtrChatListenerManager.getInstance().sessionID(UtilTool.getJid(), String.valueOf(JidCreate.entityBareFrom(mUser)))));
            MessageInfo messageInfo = new MessageInfo();
            messageInfo.setUsername(mUser);
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
            if (mMgr.findConversation(mUser)) {
                mMgr.updateConversation(mUser, 0, converstaion, time);
            } else {
                ConversationInfo info = new ConversationInfo();
                info.setTime(time);
                info.setFriend(mUser.substring(0, mUser.indexOf("@")));
                info.setUser(mUser);
                info.setMessage(converstaion);
                info.setChatType(RoomManage.ROOM_TYPE_SINGLE);
                mMgr.addConversation(info);
            }
            EventBus.getDefault().post(new MessageEvent(context.getString(R.string.send_red_packet_le)));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
