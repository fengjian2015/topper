package com.bclould.tea.xmpp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.bclould.tea.R;
import com.bclould.tea.history.DBConversationBurnManage;
import com.bclould.tea.history.DBManager;
import com.bclould.tea.model.ConversationInfo;
import com.bclould.tea.model.MessageInfo;
import com.bclould.tea.network.OSSupload;
import com.bclould.tea.topperchat.MessageManage;
import com.bclould.tea.topperchat.WsConnection;
import com.bclould.tea.topperchat.WsContans;
import com.bclould.tea.utils.Constants;
import com.bclould.tea.utils.EventBusUtil;
import com.bclould.tea.utils.MessageEvent;
import com.bclould.tea.utils.StringUtils;
import com.bclould.tea.utils.UtilTool;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.greenrobot.eventbus.EventBus;
import org.msgpack.jackson.dataformat.MessagePackFactory;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.bclould.tea.topperchat.WsContans.MSG_SINGLER;
import static com.bclould.tea.ui.adapter.ChatAdapter.TO_CARD_MSG;
import static com.bclould.tea.ui.adapter.ChatAdapter.TO_FILE_MSG;
import static com.bclould.tea.ui.adapter.ChatAdapter.TO_GUESS_MSG;
import static com.bclould.tea.ui.adapter.ChatAdapter.TO_HTML_MSG;
import static com.bclould.tea.ui.adapter.ChatAdapter.TO_IMG_MSG;
import static com.bclould.tea.ui.adapter.ChatAdapter.TO_INVITE_MSG;
import static com.bclould.tea.ui.adapter.ChatAdapter.TO_LINK_MSG;
import static com.bclould.tea.ui.adapter.ChatAdapter.TO_LOCATION_MSG;
import static com.bclould.tea.ui.adapter.ChatAdapter.TO_RED_MSG;
import static com.bclould.tea.ui.adapter.ChatAdapter.TO_TEXT_MSG;
import static com.bclould.tea.ui.adapter.ChatAdapter.TO_TRANSFER_MSG;
import static com.bclould.tea.ui.adapter.ChatAdapter.TO_VIDEO_MSG;
import static com.bclould.tea.ui.adapter.ChatAdapter.TO_VOICE_MSG;
import static com.bclould.tea.ui.adapter.ChatAdapter.TO_WITHDRAW_MSG;

/**
 * Created by GIjia on 2018/5/17.
 */

public class SingleManage implements Room {
    private DBManager mMgr;
    private DBConversationBurnManage mDBConversationBurnManage;
    private String mUser;//對方id
    private Context context;
    private String mName;
    private String url;
    private static ExecutorService mSingleThreadExecutor = null;
    private int isBurnReading=0;

    public SingleManage(DBManager mMgr, String mUser, Context context, String name) {
        this.mMgr = mMgr;
        mDBConversationBurnManage=new DBConversationBurnManage(context);
        this.mUser = mUser;
        this.context = context;
        this.mName = name;
        mSingleThreadExecutor = Executors.newSingleThreadExecutor();// 每次只执行一个线程任务的线程池
        if (StringUtils.isEmpty(mName)) {
            this.mName = mMgr.findUserName(mUser);
        }
    }

    @Override
    public void setIsBurnReading(int isBurnReading){
        this.isBurnReading=isBurnReading;
    }

    public void setUrl(String url){
        this.url=url;
    }

    private synchronized boolean send(String to, byte[] attachment, String body, int msgType, String msgId, long time) throws Exception {
        mMgr.addMessageMsgId(msgId,null);
        MessageManage.getInstance().sendSingLe(to,attachment,body,msgType,msgId,time,mUser,mMgr,context,isBurnReading);
        return true;
    }

    private synchronized boolean sendWithdraw(String to, byte[] attachment, MessageInfo messageInfo, int msgType, String msgId, long time) throws Exception {
        String body=JSON.toJSONString(messageInfo);
        mMgr.addMessageMsgId(msgId,messageInfo.getRoomId());
        MessageManage.getInstance().sendSingLe(to,attachment,body,msgType,msgId,time,mUser,mMgr,context,isBurnReading);
        return true;
    }

    private boolean sendOTR(String to, byte[] attachment, String body, int msgType) throws Exception {
        String msgId = UtilTool.createMsgId(to);
        ObjectMapper objectMapper = new ObjectMapper(new MessagePackFactory());
        Map<Object, Object> messageMap = new HashMap<>();
        messageMap.put("body", body);
        messageMap.put("attachment", attachment);

        Map<Object, Object> contentMap = new HashMap<>();
        contentMap.put("to", to);
        contentMap.put("from", UtilTool.getTocoId());
        contentMap.put("crypt", true);
        contentMap.put("message", objectMapper.writeValueAsBytes(messageMap));
        contentMap.put("type", msgType);
        contentMap.put("id", UtilTool.createMsgId(to));
        Map<Object, Object> sendMap = new HashMap<>();
        sendMap.put("type", MSG_SINGLER);
        sendMap.put("content", objectMapper.writeValueAsBytes(contentMap));
        mMgr.addMessageMsgId(msgId,null);
        WsConnection.getInstance().sendMessage(objectMapper.writeValueAsBytes(sendMap));
        return true;
    }

    @Override
    public void addMessageManageListener(MessageManageListener messageManageListener) {
        MessageListenerManage.get().addMessageManageListener(messageManageListener);
    }

    @Override
    public void removerMessageManageListener(MessageManageListener messageManageListener) {
        MessageListenerManage.get().removerMessageManageListener(messageManageListener);
    }

    private void refreshAddData(MessageInfo messageInfo) {
        MessageListenerManage.get().refreshAddData(messageInfo);
    }

    private void sendFileResults(String newFile2, boolean isSuccess) {
        MessageListenerManage.get().sendFileResults(newFile2,isSuccess);
    }

    private void sendFile(String msgId, boolean isSuccess) {
        MessageListenerManage.get().sendFile(msgId, isSuccess);

    }

    private void sendError(int id) {
        MessageListenerManage.get().sendError(id);
    }

    @Override
    public boolean anewSendText(String message, int id) {
        try {
            mMgr.deleteSingleMessage(mUser, id + "",isBurnReading);
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
            sendOTR(mUser, null, message, WsContans.MSG_TEXT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private ConversationInfo createConversation(String time, String message, long createTime) {
        ConversationInfo info = new ConversationInfo();
        info.setTime(time);
        info.setFriend(mName);
        info.setUser(mUser);
        info.setMessage(message);
        info.setChatType(RoomManage.ROOM_TYPE_SINGLE);
        info.setCreateTime(createTime);
        return info;
    }

    private void changeConversationInfo(String time, String message, long createTime) {
        if(isBurnReading==0){
            if (mMgr.findConversation(mUser)) {
                mMgr.updateConversation(mUser, 0,  mMgr.findLastMessageConversation(mUser,isBurnReading), mMgr.findLastMessageConversationTime(mUser,isBurnReading), createTime);
            } else {
                mMgr.addConversation(createConversation(time, message, createTime));
            }
        }else{
            if (mDBConversationBurnManage.findConversation(mUser)) {
                mDBConversationBurnManage.updateConversation(mUser, 0,  mMgr.findLastMessageConversation(mUser,isBurnReading),  createTime);
            } else {
                mDBConversationBurnManage.addConversation(createConversation(time, message, createTime));
            }
        }
    }


    @Override
    public void sendATMessage(String message, Map<String, String> atMap) {

    }

    //发送文本消息
    public MessageInfo sendMessage(String message) {
        try {
            MessageInfo sendMessage = new MessageInfo();
            sendMessage.setMessage(message);
            String msgId = UtilTool.createMsgId(mUser);
            long createTime = UtilTool.createChatCreatTime();
            send(mUser, null, JSON.toJSONString(sendMessage), WsContans.MSG_TEXT, msgId, createTime);

            MessageInfo messageInfo = new MessageInfo();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date curDate = new Date(System.currentTimeMillis());
            String time = formatter.format(curDate);
            messageInfo.setMessage(message);
            messageInfo.setConverstaion(message);
            messageInfo.setMsgId(msgId);
            messageInfo.setCreateTime(createTime);
            messageInfo.setTime(time);
            messageInfo.setIsBurnReading(isBurnReading);
            messageInfo.setUsername(mUser);
            messageInfo.setType(0);
            messageInfo.setMsgType(TO_TEXT_MSG);
            messageInfo.setSendStatus(0);
            messageInfo.setSend(UtilTool.getTocoId());
            messageInfo.setId(mMgr.addMessage(messageInfo));
            changeConversationInfo(time, message, createTime);
            EventBus.getDefault().post(new MessageEvent(EventBusUtil.oneself_send_msg));
            refreshAddData(messageInfo);
            return messageInfo;
        } catch (Exception e) {
            e.printStackTrace();
            MessageInfo messageInfo = new MessageInfo();
            messageInfo.setUsername(mUser);
            messageInfo.setMessage(message);
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date curDate = new Date(System.currentTimeMillis());
            String time = formatter.format(curDate);
            messageInfo.setTime(time);
            messageInfo.setType(0);
            messageInfo.setIsBurnReading(isBurnReading);
            messageInfo.setMsgType(TO_TEXT_MSG);
            messageInfo.setSendStatus(2);
            messageInfo.setSend(UtilTool.getTocoId());
            messageInfo.setConverstaion(message);
            messageInfo.setMsgId(UtilTool.createMsgId(mUser));
            messageInfo.setCreateTime(UtilTool.createChatCreatTime());
            messageInfo.setId(mMgr.addMessage(messageInfo));
            changeConversationInfo(time, message, UtilTool.createChatCreatTime());
            EventBus.getDefault().post(new MessageEvent(EventBusUtil.oneself_send_msg));
            refreshAddData(messageInfo);
            if(context==null)return messageInfo;
            Toast.makeText(context, context.getString(R.string.send_error), Toast.LENGTH_SHORT).show();
            return messageInfo;
        }
    }

    @Override
    public void transmitFile(MessageInfo messageInfo){
        MessageInfo messageInfo1 = sendFileMessage(messageInfo.getMessage(), messageInfo.getContent(), messageInfo.getKey(), messageInfo.getTitle(),messageInfo.getCount());
        sendFileAfterMessage(messageInfo1.getKey(),messageInfo.getId(),messageInfo.getMsgId(),messageInfo.getCreateTime(),messageInfo.getTitle(),messageInfo.getCount(),messageInfo.getContent());
    }

    @Override
    public void anewUploadFile(MessageInfo messageInfo){
        mMgr.deleteSingleMessage(mUser, messageInfo.getId() + "",isBurnReading);
        uploadFile(messageInfo.getMessage());
    }

    @Override
    public void uploadFile(final String path){
        final File file = new File(path);//获取文件
        final String title=file.getName();
        long mFolderSize = UtilTool.getFolderSize(file);
        final String size = UtilTool.FormetFileSize(mFolderSize);
        final String postfixs = UtilTool.getPostfix3(file.getName());
        final String key = UtilTool.getUserId() + UtilTool.createtFileName() + ".AN." +postfixs;//命名aws文件名

        final MessageInfo messageInfo = sendFileMessage(path, postfixs, key, title,size);
        OSSClient ossClient = OSSupload.getInstance().visitOSS();
        PutObjectRequest put = new PutObjectRequest(Constants.BUCKET_NAME2, key, file.getPath());
        OSSAsyncTask<PutObjectResult> task = ossClient.asyncPutObject(put, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
            @Override
            public void onSuccess(PutObjectRequest putObjectRequest, PutObjectResult putObjectResult) {
                sendFileAfterMessage(key,messageInfo.getId(),messageInfo.getMsgId(),messageInfo.getCreateTime(),title,size,postfixs);
            }

            @Override
            public void onFailure(PutObjectRequest putObjectRequest, ClientException e, ServiceException e1) {
                UtilTool.Log("aws", "错误");
                mMgr.updateMessageHint(messageInfo.getId(), 2);
                sendError(messageInfo.getId());
            }
        });
    }

    private void sendFileAfterMessage(String key, int mId, String msgId, long createTime,String title,String size,String postfixs) {
        try {
            MessageInfo sendMessage = new MessageInfo();
            sendMessage.setKey(key);
            sendMessage.setCount(size);
            sendMessage.setContent(postfixs);
            sendMessage.setTitle(title);
            int type = WsContans.MSG_FILE;
            send(mUser, null, JSON.toJSONString(sendMessage), type, msgId, createTime);
            sendFile(msgId, true);
            return;
        } catch (Exception e) {
            mMgr.updateMessageHint(mId, 2);
            sendFile(msgId, false);
        }
    }

    //发送文件消息
    public MessageInfo sendFileMessage(String path, String postfix, String key,String title,String size) {
        MessageInfo messageInfo = new MessageInfo();
        try {
            messageInfo.setUsername(mUser);
            messageInfo.setMessage(path);
            messageInfo.setVoice(path);
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date curDate = new Date(System.currentTimeMillis());
            String time = formatter.format(curDate);
            messageInfo.setTime(time);
            messageInfo.setType(0);
            messageInfo.setContent(postfix);
            messageInfo.setTitle(title);
            messageInfo.setIsBurnReading(isBurnReading);
            messageInfo.setCount(size);
            messageInfo.setKey(key);
            messageInfo.setMsgType(TO_FILE_MSG);
            messageInfo.setSend(UtilTool.getTocoId());
            String message = "[" + context.getString(R.string.file) + "]";
            messageInfo.setConverstaion("[" + context.getString(R.string.file) + "]");
            messageInfo.setCreateTime(UtilTool.createChatCreatTime());
            messageInfo.setMsgId(UtilTool.createMsgId(mUser));
            messageInfo.setSendStatus(0);
            messageInfo.setId(mMgr.addMessage(messageInfo));
            changeConversationInfo(time, message, messageInfo.getCreateTime());
            refreshAddData(messageInfo);
            EventBus.getDefault().post(new MessageEvent(EventBusUtil.oneself_send_msg));
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, context.getString(R.string.send_error), Toast.LENGTH_SHORT).show();
            messageInfo.setUsername(mUser);
            messageInfo.setMessage(path);
            messageInfo.setVoice(path);
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date curDate = new Date(System.currentTimeMillis());
            String time = formatter.format(curDate);
            messageInfo.setTime(time);
            messageInfo.setType(0);
            messageInfo.setContent(postfix);
            messageInfo.setTitle(title);
            messageInfo.setCount(size);
            messageInfo.setIsBurnReading(isBurnReading);
            messageInfo.setKey(key);
            messageInfo.setSendStatus(2);
            messageInfo.setMsgType(TO_FILE_MSG);
            messageInfo.setSend(UtilTool.getTocoId());
            String message = "[" + context.getString(R.string.file) + "]";
            messageInfo.setConverstaion("[" + context.getString(R.string.file) + "]");
            messageInfo.setCreateTime(UtilTool.createChatCreatTime());
            messageInfo.setMsgId(UtilTool.createMsgId(mUser));
            messageInfo.setId(mMgr.addMessage(messageInfo));
            changeConversationInfo(time, message, messageInfo.getCreateTime());
            refreshAddData(messageInfo);
            EventBus.getDefault().post(new MessageEvent(EventBusUtil.oneself_send_msg));
        }
        return messageInfo;
    }

    @Override
    public void anewSendUpload(MessageInfo messageInfo) {
        mMgr.deleteSingleMessage(mUser, messageInfo.getId() + "",isBurnReading);
        Upload(messageInfo.getMessage());
    }

    //上传文件到aws
    public void Upload(final String path) {
        mSingleThreadExecutor.execute(new Runnable() {
            @Override
            public void run() {
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
                String postfixs =UtilTool.getPostfix2(file.getName());
                if (!".gif".equals(postfixs) && !".GIF".equals(postfixs)) {
                    UtilTool.comp(bitmap, newFile);//压缩图片
                } else {
                    UtilTool.copyFile(file.getAbsolutePath(), newFile.getAbsolutePath());
                }
                //上传视频到aws
                if (postfix.equals("Video")) {
                    final MessageInfo messageInfo = sendFileMessage(path, postfix, key, newFile.getPath());
                    OSSClient ossClient = OSSupload.getInstance().visitOSS();
                    PutObjectRequest put = new PutObjectRequest(Constants.BUCKET_NAME2, key, file.getPath());
                    OSSAsyncTask<PutObjectResult> task = ossClient.asyncPutObject(put, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
                        @Override
                        public void onSuccess(PutObjectRequest putObjectRequest, PutObjectResult putObjectResult) {
                            sendFileAfterMessage(key, postfix, newFile.getPath(), messageInfo.getId(), messageInfo.getMsgId(), messageInfo.getCreateTime());
                        }

                        @Override
                        public void onFailure(PutObjectRequest putObjectRequest, ClientException e, ServiceException e1) {
                            UtilTool.Log("aws", "错误");
                            mMgr.updateMessageHint(messageInfo.getId(), 2);
                            sendError(messageInfo.getId());
                        }
                    });
                } else {
                    final MessageInfo messageInfo = sendFileMessage(path, postfix, key, newFile.getPath());
                    OSSClient ossClient = OSSupload.getInstance().visitOSS();
                    PutObjectRequest put = new PutObjectRequest(Constants.BUCKET_NAME2, key, file.getPath());
                    OSSAsyncTask<PutObjectResult> task = ossClient.asyncPutObject(put, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
                        @Override
                        public void onSuccess(PutObjectRequest putObjectRequest, PutObjectResult putObjectResult) {
                            sendFileAfterMessage(key, postfix, newFile.getPath(), messageInfo.getId(), messageInfo.getMsgId(), messageInfo.getCreateTime());
                        }

                        @Override
                        public void onFailure(PutObjectRequest putObjectRequest, ClientException e, ServiceException e1) {
                            UtilTool.Log("aws", "错误");
                            mMgr.updateMessageHint(messageInfo.getId(), 2);
                            sendError(messageInfo.getId());
                        }
                    });
                }
            }
        });
    }

    //僅僅是url鏈接
    @Override
    public void transmitVideo(MessageInfo messageInfo) {
        MessageInfo messageInfo1 = sendFileMessage(messageInfo.getMessage(), "Video", messageInfo.getKey(), messageInfo.getVoice());
        sendFileAfterMessage(messageInfo.getKey(), "Video", messageInfo.getVoice(), messageInfo1.getId(), messageInfo1.getMsgId(), messageInfo1.getCreateTime());
    }

    @Override
    public void transmitImage(MessageInfo messageInfo){
        MessageInfo messageInfo1 = sendFileMessage(messageInfo.getMessage(), "Image", messageInfo.getKey(), messageInfo.getVoice());
        sendFileAfterMessage(messageInfo.getKey(), "Image", messageInfo.getVoice(), messageInfo1.getId(),messageInfo1.getMsgId(),messageInfo1.getCreateTime());
    }

    private void sendFileAfterMessage(String key, String postfix, String newFile, int mId, String msgId, long createTime) {
        try {
            String postfixs = key.substring(key.lastIndexOf("."));
            byte[] bytes=new byte[]{};
//            if (".gif".equals(postfixs) || ".GIF".equals(postfixs)) {
//                Bitmap bitmap= BitmapFactory.decodeFile(newFile);
//                ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//                bytes=baos.toByteArray();
//            }else{
//                bytes = UtilTool.readStream(newFile);
//            }
            MessageInfo sendMessage = new MessageInfo();
            sendMessage.setKey(key);
            int type = 0;
            if (postfix.equals("Image")) {
                type = WsContans.MSG_IMAGE;
            } else if (postfix.equals("Video")) {
                type = WsContans.MSG_VIDEO;
            }
            send(mUser, bytes, JSON.toJSONString(sendMessage), type, msgId, createTime);
            sendFileResults(newFile, true);
            return;
        } catch (Exception e) {
            mMgr.updateMessageHint(mId, 2);
            sendFileResults(newFile, false);
        }
    }

    //发送文件消息
    public MessageInfo sendFileMessage(String path, String postfix, String key, String newFile) {
        MessageInfo messageInfo = new MessageInfo();
        try {
            messageInfo.setUsername(mUser);
            messageInfo.setVoice(newFile);
            messageInfo.setMessage(path);
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date curDate = new Date(System.currentTimeMillis());
            String time = formatter.format(curDate);
            messageInfo.setTime(time);
            messageInfo.setIsBurnReading(isBurnReading);
            messageInfo.setType(0);
            messageInfo.setKey(key);
            if (postfix.equals("Image")) {
                messageInfo.setMsgType(TO_IMG_MSG);
            } else if (postfix.equals("Video")) {
                messageInfo.setMsgType(TO_VIDEO_MSG);
            } else {
                messageInfo.setMsgType(TO_FILE_MSG);
            }
            messageInfo.setSend(UtilTool.getTocoId());
            String message;
            if (postfix.equals("Image")) {
                message = "[" + context.getString(R.string.image) + "]";
                messageInfo.setConverstaion("[" + context.getString(R.string.image) + "]");
            } else if (postfix.equals("Video")) {
                message = "[" + context.getString(R.string.video) + "]";
                messageInfo.setConverstaion("[" + context.getString(R.string.video) + "]");
            } else {
                message = "[" + context.getString(R.string.file) + "]";
                messageInfo.setConverstaion("[" + context.getString(R.string.file) + "]");
            }
            messageInfo.setCreateTime(UtilTool.createChatCreatTime());
            messageInfo.setMsgId(UtilTool.createMsgId(mUser));
            messageInfo.setSendStatus(0);
            messageInfo.setId(mMgr.addMessage(messageInfo));
            changeConversationInfo(time, message, messageInfo.getCreateTime());
            refreshAddData(messageInfo);
            EventBus.getDefault().post(new MessageEvent(EventBusUtil.oneself_send_msg));
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
            messageInfo.setIsBurnReading(isBurnReading);
            messageInfo.setType(0);
            messageInfo.setKey(key);
            messageInfo.setSendStatus(2);
            if (postfix.equals("Image")) {
                messageInfo.setMsgType(TO_IMG_MSG);
            } else if (postfix.equals("Video")) {
                messageInfo.setMsgType(TO_VIDEO_MSG);
            } else {
                messageInfo.setMsgType(TO_FILE_MSG);
            }
            messageInfo.setSend(UtilTool.getTocoId());
            String message;
            if (postfix.equals("Image")) {
                message = "[" + context.getString(R.string.image) + "]";
                messageInfo.setConverstaion("[" + context.getString(R.string.image) + "]");
            } else if (postfix.equals("Video")) {
                message = "[" + context.getString(R.string.video) + "]";
                messageInfo.setConverstaion("[" + context.getString(R.string.video) + "]");
            } else {
                message = "[" + context.getString(R.string.file) + "]";
                messageInfo.setConverstaion("[" + context.getString(R.string.file) + "]");
            }
            messageInfo.setCreateTime(UtilTool.createChatCreatTime());
            messageInfo.setMsgId(UtilTool.createMsgId(mUser));
            messageInfo.setId(mMgr.addMessage(messageInfo));
            changeConversationInfo(time, message, messageInfo.getCreateTime());
            refreshAddData(messageInfo);
            EventBus.getDefault().post(new MessageEvent(EventBusUtil.oneself_send_msg));
        }
        return messageInfo;
    }


    @Override
    public void anewSendLocation(MessageInfo messageInfo) {
        mMgr.deleteSingleMessage(mUser, messageInfo.getId() + "",isBurnReading);
        sendLocationMessage(messageInfo.getVoice(), null, messageInfo.getTitle(), messageInfo.getAddress(), messageInfo.getLat(), messageInfo.getLng());
    }

    public void sendLocationMessage(String file, Bitmap bitmap, String title, String address, float lat, float lng) {
        String converstaion = "[" + context.getString(R.string.location) + "]";
        final String postfix = "LOCATION";//获取文件后缀
        final String key = UtilTool.getUserId() + UtilTool.createtFileName() + ".AN.jpg";//命名aws文件名
        //缩略图储存路径
        File newFile;
        if (StringUtils.isEmpty(file)) {
            newFile = new File(Constants.PUBLICDIR + key);
            UtilTool.comp(bitmap, newFile);//压缩图片
        } else {
            newFile = new File(file);
        }
        int mId;
        try {
            MessageInfo messageInfo = new MessageInfo();
            messageInfo.setTitle(title);
            messageInfo.setAddress(address);
            messageInfo.setLat(lat);
            messageInfo.setLng(lng);
            String msgId = UtilTool.createMsgId(mUser);
            long createTime = UtilTool.createChatCreatTime();
            byte[] bytes = UtilTool.readStream(newFile.getAbsolutePath());
            send(mUser, bytes, JSON.toJSONString(messageInfo), WsContans.MSG_LOCATION, msgId, createTime);

            //把消息添加进数据库
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date curDate = new Date(System.currentTimeMillis());
            String time = formatter.format(curDate);
            messageInfo.setSendStatus(0);
            messageInfo.setVoice(newFile.getAbsolutePath());
            messageInfo.setType(0);
            messageInfo.setUsername(mUser);
            messageInfo.setTime(time);
            messageInfo.setMessage(title);
            messageInfo.setIsBurnReading(isBurnReading);
            messageInfo.setMsgType(TO_LOCATION_MSG);
            messageInfo.setSend(UtilTool.getTocoId());
            messageInfo.setConverstaion(converstaion);
            messageInfo.setMsgId(msgId);
            messageInfo.setCreateTime(createTime);
            mId = mMgr.addMessage(messageInfo);
            messageInfo.setId(mId);
            refreshAddData(messageInfo);
            //给聊天列表更新消息
            changeConversationInfo(time, converstaion, createTime);
            //发送消息通知
            EventBus.getDefault().post(new MessageEvent(EventBusUtil.oneself_send_msg));
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
            messageInfo.setIsBurnReading(isBurnReading);
            messageInfo.setAddress(address);
            messageInfo.setLat(lat);
            messageInfo.setLng(lng);
            messageInfo.setMsgType(TO_LOCATION_MSG);
            messageInfo.setSendStatus(2);
            messageInfo.setConverstaion(converstaion);
            messageInfo.setMsgId(UtilTool.createMsgId(mUser));
            messageInfo.setCreateTime(UtilTool.createChatCreatTime());
            mId = mMgr.addMessage(messageInfo);
            messageInfo.setId(mId);
            refreshAddData(messageInfo);
            changeConversationInfo(time, converstaion, UtilTool.createChatCreatTime());
            EventBus.getDefault().post(new MessageEvent(EventBusUtil.oneself_send_msg));

            mMgr.updateMessageHint(mId, 2);
            sendFileResults(newFile.getAbsolutePath(), false);

        }
    }

    @Override
    public void changeName(String name) {

    }

    @Override
    public void anewSendCard(MessageInfo messageInfo) {
        mMgr.deleteSingleMessage(mUser, messageInfo.getId() + "",isBurnReading);
        sendCaed(messageInfo);
    }

    @Override
    public boolean sendCaed(MessageInfo messageInfo) {
        String converstaion = "[" + context.getString(R.string.person_business_card) + "]";
        try {
            long createTime = UtilTool.createChatCreatTime();
            String msgId = UtilTool.createMsgId(mUser);
            send(mUser, null, JSON.toJSONString(messageInfo), WsContans.MSG_CARD, msgId, createTime);
            messageInfo.setUsername(mUser);
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date curDate = new Date(System.currentTimeMillis());
            String time = formatter.format(curDate);
            messageInfo.setTime(time);
            messageInfo.setType(0);
            messageInfo.setMsgType(TO_CARD_MSG);
            messageInfo.setIsBurnReading(isBurnReading);
            messageInfo.setSend(UtilTool.getTocoId());
            messageInfo.setSendStatus(0);
            messageInfo.setConverstaion(converstaion);
            messageInfo.setMsgId(msgId);
            messageInfo.setCreateTime(createTime);
            messageInfo.setId(mMgr.addMessage(messageInfo));
            changeConversationInfo(time, converstaion, createTime);
            EventBus.getDefault().post(new MessageEvent(EventBusUtil.oneself_send_msg));
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
            messageInfo.setIsBurnReading(isBurnReading);
            messageInfo.setSendStatus(2);
            messageInfo.setSend(UtilTool.getTocoId());
            messageInfo.setConverstaion(converstaion);
            messageInfo.setMsgId(UtilTool.createMsgId(mUser));
            messageInfo.setCreateTime(UtilTool.createChatCreatTime());
            messageInfo.setId(mMgr.addMessage(messageInfo));

            changeConversationInfo(time, converstaion, UtilTool.createChatCreatTime());
            EventBus.getDefault().post(new MessageEvent(EventBusUtil.oneself_send_msg));
            refreshAddData(messageInfo);
            return false;
        }
    }


    @Override
    public void anewSendShareLink(MessageInfo messageInfo) {
        mMgr.deleteSingleMessage(mUser, messageInfo.getId() + "",isBurnReading);
        sendShareLink(messageInfo);
    }


    @Override
    public boolean sendShareLink(MessageInfo messageInfo) {
        String converstaion = "[" + context.getString(R.string.share) + "]";
        try {
            String msgId = UtilTool.createMsgId(mUser);
            long createTime = UtilTool.createChatCreatTime();
            send(mUser, null, JSON.toJSONString(messageInfo), WsContans.MSG_SHARE_LINK, msgId, createTime);

            messageInfo.setUsername(mUser);
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date curDate = new Date(System.currentTimeMillis());
            String time = formatter.format(curDate);
            messageInfo.setTime(time);
            messageInfo.setType(0);
            messageInfo.setMsgType(TO_LINK_MSG);
            messageInfo.setIsBurnReading(isBurnReading);
            messageInfo.setSend(UtilTool.getTocoId());
            messageInfo.setSendStatus(0);
            messageInfo.setConverstaion(converstaion);
            messageInfo.setMsgId(msgId);
            messageInfo.setCreateTime(createTime);
            messageInfo.setId(mMgr.addMessage(messageInfo));
            changeConversationInfo(time, converstaion, createTime);
            EventBus.getDefault().post(new MessageEvent(EventBusUtil.oneself_send_msg));
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
            messageInfo.setIsBurnReading(isBurnReading);
            messageInfo.setSendStatus(2);
            messageInfo.setSend(UtilTool.getTocoId());
            messageInfo.setConverstaion(converstaion);
            messageInfo.setMsgId(UtilTool.createMsgId(mUser));
            messageInfo.setCreateTime(UtilTool.createChatCreatTime());
            messageInfo.setId(mMgr.addMessage(messageInfo));
            changeConversationInfo(time, converstaion, UtilTool.createChatCreatTime());
            EventBus.getDefault().post(new MessageEvent(EventBusUtil.oneself_send_msg));
            refreshAddData(messageInfo);
            return false;
        }
    }

    @Override
    public void anewSendShareGuess(MessageInfo messageInfo) {
        mMgr.deleteSingleMessage(mUser, messageInfo.getId() + "",isBurnReading);
        sendShareGuess(messageInfo);
    }


    @Override
    public boolean sendShareGuess(MessageInfo messageInfo) {
        String converstaion = "[" + context.getString(R.string.share_guess) + "]";
        try {
            String msgId = UtilTool.createMsgId(mUser);
            long createTime = UtilTool.createChatCreatTime();
            send(mUser, null, JSON.toJSONString(messageInfo), WsContans.MSG_SHARE_GUESS, msgId, createTime);
            messageInfo.setUsername(mUser);
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date curDate = new Date(System.currentTimeMillis());
            String time = formatter.format(curDate);
            messageInfo.setTime(time);
            messageInfo.setType(0);
            messageInfo.setMsgType(TO_GUESS_MSG);
            messageInfo.setIsBurnReading(isBurnReading);
            messageInfo.setSend(UtilTool.getTocoId());
            messageInfo.setSendStatus(0);
            messageInfo.setConverstaion(converstaion);
            messageInfo.setMsgId(msgId);
            messageInfo.setCreateTime(createTime);
            messageInfo.setId(mMgr.addMessage(messageInfo));
            changeConversationInfo(time, converstaion, createTime);
            EventBus.getDefault().post(new MessageEvent(EventBusUtil.oneself_send_msg));
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
            messageInfo.setIsBurnReading(isBurnReading);
            messageInfo.setSendStatus(2);
            messageInfo.setSend(UtilTool.getTocoId());
            messageInfo.setConverstaion(converstaion);
            messageInfo.setMsgId(UtilTool.createMsgId(mUser));
            messageInfo.setCreateTime(UtilTool.createChatCreatTime());
            messageInfo.setId(mMgr.addMessage(messageInfo));
            changeConversationInfo(time, converstaion, UtilTool.createChatCreatTime());
            EventBus.getDefault().post(new MessageEvent(EventBusUtil.oneself_send_msg));
            refreshAddData(messageInfo);
            return false;
        }
    }


    @Override
    public boolean anewSendVoice(MessageInfo messageInfo) {
        try {
            mMgr.deleteSingleMessage(mUser, messageInfo.getId() + "",isBurnReading);
            sendVoice(UtilTool.getFileDuration(messageInfo.getVoice(), context), messageInfo.getVoice());
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
            String msgId = UtilTool.createMsgId(mUser);
            long createTime = UtilTool.createChatCreatTime();
            MessageInfo messageInfo = new MessageInfo();
            messageInfo.setMessage(context.getString(R.string.voice));
            send(mUser, bytes, JSON.toJSONString(messageInfo), WsContans.MSG_AUDIO, msgId, createTime);

            //把消息添加进数据库
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date curDate = new Date(System.currentTimeMillis());
            String time = formatter.format(curDate);

            messageInfo.setType(0);
            messageInfo.setUsername(mUser);
            messageInfo.setVoice(fileName);
            messageInfo.setTime(time);
            messageInfo.setMsgType(TO_VOICE_MSG);
            messageInfo.setIsBurnReading(isBurnReading);
            messageInfo.setVoiceTime(duration + "");
            messageInfo.setVoiceStatus(1);
            messageInfo.setSendStatus(0);
            messageInfo.setSend(UtilTool.getTocoId());
            messageInfo.setConverstaion(converstaion);
            messageInfo.setMsgId(msgId);
            messageInfo.setCreateTime(createTime);
            messageInfo.setId(mMgr.addMessage(messageInfo));
            refreshAddData(messageInfo);

            //给聊天列表更新消息
            changeConversationInfo(time, converstaion, createTime);
            //发送消息通知
            EventBus.getDefault().post(new MessageEvent(EventBusUtil.oneself_send_msg));
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
            messageInfo.setIsBurnReading(isBurnReading);
            messageInfo.setVoiceTime(duration + "");
            messageInfo.setVoiceStatus(1);
            messageInfo.setSendStatus(2);
            messageInfo.setConverstaion(converstaion);
            messageInfo.setMsgId(UtilTool.createMsgId(mUser));
            messageInfo.setCreateTime(UtilTool.createChatCreatTime());
            messageInfo.setId(mMgr.addMessage(messageInfo));
            refreshAddData(messageInfo);
            changeConversationInfo(time, converstaion, UtilTool.createChatCreatTime());
            EventBus.getDefault().post(new MessageEvent(EventBusUtil.oneself_send_msg));
        }

    }

    public void sendTransfer(String mRemark, String mCoin, String mCount) {
        try {
            String converstaion = "[" + context.getString(R.string.transfer) + "]";
            String message = Constants.TRANSFER + Constants.CHUANCODE + mRemark + Constants.CHUANCODE + mCoin + Constants.CHUANCODE + mCount;
            String msgId = UtilTool.createMsgId(mUser);
            long createTime = UtilTool.createChatCreatTime();
            MessageInfo sendMessage = new MessageInfo();
            sendMessage.setRemark(mRemark);
            sendMessage.setCoin(mCoin);
            sendMessage.setCount(mCount);
            sendMessage.setMessage(message);
            send(mUser, null, JSON.toJSONString(sendMessage), WsContans.MSG_TRANSFER, msgId, createTime);

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
            messageInfo.setIsBurnReading(isBurnReading);
            messageInfo.setCount(mCount);
            messageInfo.setStatus(0);
            messageInfo.setSend(UtilTool.getTocoId());
            messageInfo.setConverstaion(converstaion);
            messageInfo.setMsgId(msgId);
            messageInfo.setCreateTime(createTime);
            mMgr.addMessage(messageInfo);
            changeConversationInfo(time, converstaion, createTime);
            MessageEvent messageEvent = new MessageEvent(context.getString(R.string.transfer));
            messageEvent.setId(msgId);
            EventBus.getDefault().post(messageEvent);
            EventBus.getDefault().post(new MessageEvent(EventBusUtil.oneself_send_msg));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendRed(String mRemark, String mCoin, double mCount, int id) {
        String converstaion = "[" + context.getString(R.string.red_package) + "]";
        String message = Constants.REDBAG + Constants.CHUANCODE + mRemark + Constants.CHUANCODE + mCoin + Constants.CHUANCODE + mCount + Constants.CHUANCODE + id;
        try {
            String msgId = UtilTool.createMsgId(mUser);
            long createTime = UtilTool.createChatCreatTime();
            MessageInfo sendMessage = new MessageInfo();
            sendMessage.setRemark(mRemark);
            sendMessage.setCoin(mCoin);
            sendMessage.setCount(mCount + "");
            sendMessage.setRedId(id);
            sendMessage.setMessage(message);
            send(mUser, null, JSON.toJSONString(sendMessage), WsContans.MSG_REDBAG, msgId, createTime);

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
            messageInfo.setIsBurnReading(isBurnReading);
            messageInfo.setCount(mCount + "");
            messageInfo.setStatus(0);
            messageInfo.setRedId(id);
            messageInfo.setSend(UtilTool.getTocoId());
            messageInfo.setConverstaion(converstaion);
            messageInfo.setMsgId(msgId);
            messageInfo.setCreateTime(createTime);
            mMgr.addMessage(messageInfo);
            changeConversationInfo(time, converstaion, createTime);
            MessageEvent messageEvent = new MessageEvent(EventBusUtil.send_red_packet_le);
            messageEvent.setId(msgId);
            EventBus.getDefault().post(messageEvent);
            EventBus.getDefault().post(new MessageEvent(EventBusUtil.oneself_send_msg));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendInviteGroup(MessageInfo messageInfo) {
        String converstaion = "[" + context.getString(R.string.group_intive) + "]";
        try {
            String msgId=UtilTool.createMsgId(mUser);
            long createTime=UtilTool.createChatCreatTime();
            send(mUser, null, JSON.toJSONString(messageInfo), WsContans.MSG_INTIVE,msgId,createTime);
            messageInfo.setUsername(mUser);
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date curDate = new Date(System.currentTimeMillis());
            String time = formatter.format(curDate);
            messageInfo.setTime(time);
            messageInfo.setType(0);
            messageInfo.setMsgType(TO_INVITE_MSG);
            messageInfo.setSend(UtilTool.getTocoId());
            messageInfo.setSendStatus(0);
            messageInfo.setConverstaion(converstaion);
            messageInfo.setMsgId(msgId);
            messageInfo.setCreateTime(createTime);
            messageInfo.setIsBurnReading(isBurnReading);
            messageInfo.setId(mMgr.addMessage(messageInfo));
            changeConversationInfo(time,converstaion,createTime);
            EventBus.getDefault().post(new MessageEvent(EventBusUtil.oneself_send_msg));
            refreshAddData(messageInfo);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, context.getString(R.string.send_error), Toast.LENGTH_SHORT).show();
            messageInfo.setUsername(mUser);
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date curDate = new Date(System.currentTimeMillis());
            String time = formatter.format(curDate);
            messageInfo.setTime(time);
            messageInfo.setType(2);
            messageInfo.setMsgType(TO_INVITE_MSG);
            messageInfo.setSendStatus(2);
            messageInfo.setIsBurnReading(isBurnReading);
            messageInfo.setSend(UtilTool.getTocoId());
            messageInfo.setConverstaion(converstaion);
            messageInfo.setMsgId(UtilTool.createMsgId(mUser));
            messageInfo.setCreateTime(UtilTool.createChatCreatTime());
            messageInfo.setId(mMgr.addMessage(messageInfo));
            changeConversationInfo(time,converstaion,UtilTool.createChatCreatTime());
            EventBus.getDefault().post(new MessageEvent(EventBusUtil.oneself_send_msg));
            refreshAddData(messageInfo);
        }
    }

    @Override
    public void sendWithdraw(MessageInfo messageInfo) {
        String converstaion =messageInfo.getMessage() ;
        try {
            String msgId=UtilTool.createMsgId(mUser);
            long createTime=UtilTool.createChatCreatTime();
            sendWithdraw(mUser, null, messageInfo, WsContans.MSG_WITHDRAW,msgId,createTime);
            messageInfo.setUsername(mUser);
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date curDate = new Date(System.currentTimeMillis());
            String time = formatter.format(curDate);
            messageInfo.setTime(time);
            messageInfo.setType(0);
            messageInfo.setMsgType(TO_WITHDRAW_MSG);
            messageInfo.setSend(UtilTool.getTocoId());
            messageInfo.setSendStatus(0);
            messageInfo.setIsBurnReading(isBurnReading);
            messageInfo.setConverstaion(converstaion);
            messageInfo.setMsgId(msgId);
            messageInfo.setCreateTime(createTime);
            messageInfo.setId(mMgr.addMessage(messageInfo));
            changeConversationInfo(time,converstaion,createTime);
            EventBus.getDefault().post(new MessageEvent(EventBusUtil.oneself_send_msg));
            refreshAddData(messageInfo);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, context.getString(R.string.send_error), Toast.LENGTH_SHORT).show();
            messageInfo.setUsername(mUser);
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date curDate = new Date(System.currentTimeMillis());
            String time = formatter.format(curDate);
            messageInfo.setTime(time);
            messageInfo.setType(2);
            messageInfo.setIsBurnReading(isBurnReading);
            messageInfo.setMsgType(TO_WITHDRAW_MSG);
            messageInfo.setSendStatus(2);
            messageInfo.setSend(UtilTool.getTocoId());
            messageInfo.setConverstaion(converstaion);
            messageInfo.setMsgId(UtilTool.createMsgId(mUser));
            messageInfo.setCreateTime(UtilTool.createChatCreatTime());
            messageInfo.setId(mMgr.addMessage(messageInfo));
            changeConversationInfo(time,converstaion,UtilTool.createChatCreatTime());
            EventBus.getDefault().post(new MessageEvent(EventBusUtil.oneself_send_msg));
            refreshAddData(messageInfo);
        }
    }

    @Override
    public void sendHtml(MessageInfo messageInfo) {
        String converstaion ="["+context.getString(R.string.url)+"]";
        try {
            String msgId = UtilTool.createMsgId(mUser);
            long createTime = UtilTool.createChatCreatTime();
            send(mUser, null, JSON.toJSONString(messageInfo), WsContans.MSG_HTML, msgId, createTime);
            messageInfo.setUsername(mUser);
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date curDate = new Date(System.currentTimeMillis());
            String time = formatter.format(curDate);
            messageInfo.setTime(time);
            messageInfo.setType(0);
            messageInfo.setMsgType(TO_HTML_MSG);
            messageInfo.setIsBurnReading(isBurnReading);
            messageInfo.setSendStatus(0);
            messageInfo.setSend(UtilTool.getTocoId());
            messageInfo.setConverstaion(converstaion);
            messageInfo.setMsgId(msgId);
            messageInfo.setCreateTime(createTime);
            messageInfo.setId(mMgr.addMessage(messageInfo));
            changeConversationInfo(time, converstaion, createTime);
            EventBus.getDefault().post(new MessageEvent(EventBusUtil.oneself_send_msg));
            refreshAddData(messageInfo);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, context.getString(R.string.send_error), Toast.LENGTH_SHORT).show();
            messageInfo.setUsername(mUser);
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date curDate = new Date(System.currentTimeMillis());
            String time = formatter.format(curDate);
            messageInfo.setTime(time);
            messageInfo.setType(0);
            messageInfo.setMsgType(TO_HTML_MSG);
            messageInfo.setIsBurnReading(isBurnReading);
            messageInfo.setSendStatus(2);
            messageInfo.setSend(UtilTool.getTocoId());
            messageInfo.setConverstaion(converstaion);
            messageInfo.setMsgId(UtilTool.createMsgId(mUser));
            messageInfo.setCreateTime(UtilTool.createChatCreatTime());
            messageInfo.setId(mMgr.addMessage(messageInfo));
            changeConversationInfo(time, converstaion, UtilTool.createChatCreatTime());
            EventBus.getDefault().post(new MessageEvent(EventBusUtil.oneself_send_msg));
            refreshAddData(messageInfo);
        }
    }

    @Override
    public void anewSendHtml(MessageInfo messageInfo) {
        mMgr.deleteSingleMessage(mUser, messageInfo.getId() + "",isBurnReading);
        sendHtml(messageInfo);
    }
}
