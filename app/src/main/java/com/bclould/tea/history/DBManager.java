package com.bclould.tea.history;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.Toast;

import com.bclould.tea.model.AddRequestInfo;
import com.bclould.tea.model.AuatarListInfo;
import com.bclould.tea.model.ConversationInfo;
import com.bclould.tea.model.GroupInfo;
import com.bclould.tea.model.MessageInfo;
import com.bclould.tea.model.RemarkListInfo;
import com.bclould.tea.model.UserInfo;
import com.bclould.tea.utils.StringUtils;
import com.bclould.tea.utils.UtilTool;
import com.bclould.tea.xmpp.RoomManage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DBManager {
    private final Context mContext;
    private static Object lock = new Object();
    public DBManager(Context context) {
        mContext = context;
        DatabaseManager.initializeInstance(new DBHelper(context));
        //因为getWritableDatabase内部调用了mContext.openOrCreateDatabase(mName, 0, mFactory);
        //所以要确保context已初始化,我们可以把实例化DBManager的步骤放在Activity的onCreate里
    }


    public int addMessage(MessageInfo messageInfo) {
        synchronized (lock) {
            SQLiteDatabase sdb = DatabaseManager.getInstance().openWritableDatabase(true);
            String currentShowTime = getCurrentShowTime(messageInfo);
            messageInfo.setShowChatTime(currentShowTime);
            ContentValues values = new ContentValues();
            values.put("count", messageInfo.getCount());
            values.put("my_user", UtilTool.getTocoId());
            values.put("type", messageInfo.getType());
            values.put("remark", messageInfo.getRemark());
            values.put("message", messageInfo.getMessage());
            values.put("user", messageInfo.getUsername());
            values.put("time", messageInfo.getTime());
            values.put("coin", messageInfo.getCoin());
            values.put("state", messageInfo.getStatus());
            values.put("redId", messageInfo.getRedId());
            values.put("voice", messageInfo.getVoice());
            values.put("voiceStatus", messageInfo.getVoiceStatus());
            values.put("voiceTime", messageInfo.getVoiceTime());
            values.put("sendStatus", messageInfo.getSendStatus());
            values.put("msgType", messageInfo.getMsgType());
            values.put("imageType", messageInfo.getImageType());
            values.put("send", messageInfo.getSend());
            values.put("lat", messageInfo.getLat());
            values.put("lng", messageInfo.getLng());
            values.put("address", messageInfo.getAddress());
            values.put("title", messageInfo.getTitle());
            values.put("cardUser", messageInfo.getCardUser());
            values.put("headUrl", messageInfo.getHeadUrl());
            values.put("linkUrl", messageInfo.getLinkUrl());
            values.put("content", messageInfo.getContent());
            values.put("converstaion", messageInfo.getConverstaion());
            values.put("guessPw", messageInfo.getGuessPw());
            values.put("initiator", messageInfo.getInitiator());
            values.put("betId", messageInfo.getBetId());
            values.put("periodQty", messageInfo.getPeriodQty());
            values.put("filekey", messageInfo.getKey());
            values.put("createTime", messageInfo.getCreateTime());
            values.put("msgId", messageInfo.getMsgId());
            values.put("showChatTime", messageInfo.getShowChatTime());
            values.put("roomName",messageInfo.getRoomName());
            values.put("roomId",messageInfo.getRoomId());
            values.put("isRead",messageInfo.getIsRead());
            values.put("isBurnReading",messageInfo.getIsBurnReading());
            int id = (int) sdb.insert("MessageRecord", null, values);
            UtilTool.Log("日志", "添加成功" + messageInfo.toString());
            DatabaseManager.getInstance().closeWritableDatabase();
            return id;

        }
    }

    private synchronized String getCurrentShowTime(MessageInfo message) {
        synchronized (lock) {
            SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(false);
            long currTime = message.getCreateTime();
            try {
                Cursor c = db.rawQuery(
                        "SELECT createTime from MessageRecord where user = ? and my_user=? and isBurnReading=?  and (showChatTime is NOT NULL and showChatTime != '') order by createTime desc limit 1",
                        new String[]{message.getUsername(), UtilTool.getTocoId(),message.getIsBurnReading()+""});
                if (c.moveToNext()) {
                    long lastTime = c.getLong(c.getColumnIndex("createTime"));
                    if (UtilTool.compareTime(lastTime, currTime)) {
                        return currTime + "";
                    } else {
                        return "";
                    }
                }
                c.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            DatabaseManager.getInstance().closeWritableDatabase();
            return currTime + "";
        }
    }

    public long queryMessageCount(String user,int isBurnReading) {
        synchronized (lock) {
            SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(false);
            String sql = "select count(*) from MessageRecord where user=? and my_user=? and isBurnReading=?";
            Cursor cursor = db.rawQuery(sql, new String[]{user, UtilTool.getTocoId(),isBurnReading+""});
            cursor.moveToFirst();
            long count = cursor.getLong(0);
            cursor.close();
            DatabaseManager.getInstance().closeWritableDatabase();
            return count;
        }
    }


    //查詢紅包和轉賬記錄
    public List<MessageInfo> queryTypeMessage(String user, int fromtype, int sentype, int sendred, int fromred, int limit) {
        synchronized (lock) {
            SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(false);
            ArrayList<MessageInfo> messageInfos = new ArrayList<MessageInfo>();
            String sql = "select * from MessageRecord where user=? and my_user=? and isBurnReading=0 and (msgType=? or msgType=? or msgType=? or msgType=?) ORDER BY createTime desc limit ?";
            Cursor c = db.rawQuery(sql, new String[]{user, UtilTool.getTocoId(), fromtype + "", sentype + "", sendred + "", fromred + "", limit * 10 + ""});
            if (c != null) {
                while (c.moveToNext()) {
                    messageInfos.add(addMessage(c));
                }
                c.close();
            }
            DatabaseManager.getInstance().closeWritableDatabase();
            return messageInfos;
        }
    }

    private MessageInfo addMessage(Cursor c){
        synchronized (lock) {
            //MessageRecord
            MessageInfo messageInfo = new MessageInfo();
            messageInfo.setUsername(c.getString(c.getColumnIndex("user")));
            messageInfo.setMessage(c.getString(c.getColumnIndex("message")));
            messageInfo.setTime(c.getString(c.getColumnIndex("time")));
            messageInfo.setType(c.getInt(c.getColumnIndex("type")));
            messageInfo.setCount(c.getString(c.getColumnIndex("count")));
            messageInfo.setCoin(c.getString(c.getColumnIndex("coin")));
            messageInfo.setRemark(c.getString(c.getColumnIndex("remark")));
            messageInfo.setStatus(c.getInt(c.getColumnIndex("state")));
            messageInfo.setId(c.getInt(c.getColumnIndex("id")));
            messageInfo.setRedId(c.getInt(c.getColumnIndex("redId")));
            messageInfo.setVoiceStatus(c.getInt(c.getColumnIndex("voiceStatus")));
            messageInfo.setVoice(c.getString(c.getColumnIndex("voice")));
            messageInfo.setVoiceTime(c.getString(c.getColumnIndex("voiceTime")));
            messageInfo.setSendStatus(c.getInt(c.getColumnIndex("sendStatus")));
            messageInfo.setMsgType(c.getInt(c.getColumnIndex("msgType")));
            messageInfo.setImageType(c.getInt(c.getColumnIndex("imageType")));
            messageInfo.setSend(c.getString(c.getColumnIndex("send")));
            messageInfo.setLng(c.getFloat(c.getColumnIndex("lng")));
            messageInfo.setLat(c.getFloat(c.getColumnIndex("lat")));
            messageInfo.setAddress(c.getString(c.getColumnIndex("address")));
            messageInfo.setTitle(c.getString(c.getColumnIndex("title")));
            messageInfo.setHeadUrl(c.getString(c.getColumnIndex("headUrl")));
            messageInfo.setCardUser(c.getString(c.getColumnIndex("cardUser")));
            messageInfo.setLinkUrl(c.getString(c.getColumnIndex("linkUrl")));
            messageInfo.setContent(c.getString(c.getColumnIndex("content")));
            messageInfo.setConverstaion(c.getString(c.getColumnIndex("converstaion")));
            messageInfo.setGuessPw(c.getString(c.getColumnIndex("guessPw")));
            messageInfo.setInitiator(c.getString(c.getColumnIndex("initiator")));
            messageInfo.setBetId(c.getString(c.getColumnIndex("betId")));
            messageInfo.setPeriodQty(c.getString(c.getColumnIndex("periodQty")));
            messageInfo.setKey(c.getString(c.getColumnIndex("filekey")));
            messageInfo.setCreateTime(c.getLong(c.getColumnIndex("createTime")));
            messageInfo.setMsgId(c.getString(c.getColumnIndex("msgId")));
            messageInfo.setShowChatTime(c.getString(c.getColumnIndex("showChatTime")));
            messageInfo.setRoomId(c.getString(c.getColumnIndex("roomId")));
            messageInfo.setRoomName(c.getString(c.getColumnIndex("roomName")));
            messageInfo.setIsRead(c.getInt(c.getColumnIndex("isRead")));
            messageInfo.setIsBurnReading(c.getInt(c.getColumnIndex("isBurnReading")));
            return messageInfo;
        }
    }

    //模糊查找文本消息
    public List<MessageInfo> queryTextMessage(String user, String content, int limit) {
        synchronized (lock) {
            SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(false);
            ArrayList<MessageInfo> messageInfos = new ArrayList<MessageInfo>();
            String sql = "select * from MessageRecord where user=? and my_user=? and isBurnReading=0 and message like ? ORDER BY createTime desc limit ?";
            Cursor c = db.rawQuery(sql, new String[]{user, UtilTool.getTocoId(), "%" + content + "%", limit * 10 + ""});
            if (c != null) {
                while (c.moveToNext()) {
                    messageInfos.add(addMessage(c));
                }
                c.close();
            }
            DatabaseManager.getInstance().closeWritableDatabase();
            return messageInfos;
        }
    }

    //通過消息類型查找聊天記錄
    public List<MessageInfo> queryTypeMessage(String user, int fromtype, int sentype, int limit) {
        synchronized (lock) {
            SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(false);
            ArrayList<MessageInfo> messageInfos = new ArrayList<MessageInfo>();
            String sql = "select * from MessageRecord where user=? and my_user=? and isBurnReading=0 and (msgType=? or msgType=?) ORDER BY createTime desc limit ?";
            Cursor c = db.rawQuery(sql, new String[]{user, UtilTool.getTocoId(), fromtype + "", sentype + "", limit * 20 + ""});
            if (c != null) {
                while (c.moveToNext()) {
                    messageInfos.add(addMessage(c));
                }
                c.close();
            }
            DatabaseManager.getInstance().closeWritableDatabase();
            return messageInfos;
        }
    }

    public List<MessageInfo> queryMessage(String user,int isBurnReading) {
        synchronized (lock) {
            SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(false);
            long count = queryMessageCount(user,isBurnReading);
            ArrayList<MessageInfo> messageInfos = new ArrayList<MessageInfo>();
            String sql = "select * from MessageRecord where user=? and my_user=? and isBurnReading=? ORDER BY createTime asc limit ?,?";
            Cursor c = db.rawQuery(sql, new String[]{user, UtilTool.getTocoId(),isBurnReading+"", count - 10 + "", 10 + ""});
            if (c != null) {
                while (c.moveToNext()) {
                    messageInfos.add(addMessage(c));
                }
                c.close();
            }
            DatabaseManager.getInstance().closeWritableDatabase();
            return messageInfos;
        }
    }

    public MessageInfo queryMessageMsg(String msgId) {
        synchronized (lock) {
            SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(false);
            MessageInfo messageInfo = new MessageInfo();
            String sql = "select * from MessageRecord where msgId=? and my_user=?";
            Cursor c = db.rawQuery(sql, new String[]{msgId, UtilTool.getTocoId()});
            if (c != null) {
                if (c.moveToNext()) {
                    messageInfo = addMessage(c);
                }
                c.close();
            }
            DatabaseManager.getInstance().closeWritableDatabase();
            return messageInfo;
        }
    }

    /**
     * 用於刷新
     *
     * @param user
     * @param id
     * @return
     */
    public List<MessageInfo> queryRefreshMessage(String user, long createTime,int isBurnReading) {
        synchronized (lock) {
            SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(false);
            ArrayList<MessageInfo> messageInfos = new ArrayList<MessageInfo>();
            String sql = "select * from MessageRecord where user=? and my_user=? and isBurnReading=? and createTime < ? ORDER BY createTime desc limit ?";
            Cursor c = db.rawQuery(sql, new String[]{user, UtilTool.getTocoId(),isBurnReading+"", createTime + "", 10 + ""});
            if (c != null) {
                while (c.moveToNext()) {
                    messageInfos.add(addMessage(c));
                }
                c.close();
            }
            if (messageInfos.size() == 0) {
                Toast.makeText(mContext, "没有更多记录了", Toast.LENGTH_SHORT).show();
            }
            Collections.reverse(messageInfos);
            DatabaseManager.getInstance().closeWritableDatabase();
            return messageInfos;
        }
    }

    /**
     * 用於加載
     *
     * @param user
     * @param id
     * @return
     */
    public List<MessageInfo> queryLoadMessage(String user, long createTime, boolean isFist,int isBurnReading) {
        synchronized (lock) {
            SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(false);
            ArrayList<MessageInfo> messageInfos = new ArrayList<MessageInfo>();
            String sql;
            if (isFist) {
                sql = "select * from MessageRecord where user=? and my_user=? and isBurnReading=? and createTime >= ? limit ?";
            } else {
                sql = "select * from MessageRecord where user=? and my_user=? and isBurnReading=? and createTime > ? limit ?";
            }
            Cursor c = db.rawQuery(sql, new String[]{user, UtilTool.getTocoId(), isBurnReading+"",createTime + "", 10 + ""});
            if (c != null) {
                while (c.moveToNext()) {
                    messageInfos.add(addMessage(c));
                }
                c.close();
            }
            if (messageInfos.size() == 0) {
                Toast.makeText(mContext, "没有更多记录了", Toast.LENGTH_SHORT).show();
            }
            DatabaseManager.getInstance().closeWritableDatabase();
            return messageInfos;
        }
    }

    public void deleteMessage(String user,int isBurnReading) {
        synchronized (lock) {
            SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(true);
            db.delete("MessageRecord", "user=? and my_user=? and isBurnReading=?", new String[]{user, UtilTool.getTocoId(),isBurnReading+""});
            DatabaseManager.getInstance().closeWritableDatabase();
        }
    }


    public MessageInfo deleteSingleMessage(String mUser,String id,int isBurnReading){
        synchronized (lock) {
            SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(true);
            MessageInfo messageInfo = null;
            Cursor cursor = db.rawQuery("select * from MessageRecord where user=? and id=? and my_user=?", new String[]{mUser, id, UtilTool.getTocoId()});
            if (cursor.moveToLast()) {
                String showChatTime = cursor.getString(cursor.getColumnIndex("showChatTime"));
                long createTime = cursor.getLong(cursor.getColumnIndex("createTime"));
                String msgId = cursor.getString(cursor.getColumnIndex("msgId"));
                cursor.close();
                if (!StringUtils.isEmpty(showChatTime)) {
                    Cursor cursor1 = db.rawQuery("select * from MessageRecord where createTime > ? and my_user=? and isBurnReading=? ORDER BY createTime asc", new String[]{createTime + "", UtilTool.getTocoId(),isBurnReading+""});
                    if (cursor1.moveToFirst()) {
                        String msgId1 = cursor1.getString(cursor1.getColumnIndex("msgId"));
                        long showTime = cursor1.getLong(cursor1.getColumnIndex("createTime"));
                        messageInfo = addMessage(cursor1);
                        messageInfo.setShowChatTime(showTime + "");
                        updateShowTimeMessage(msgId1, showTime + "");
                    }
                    cursor1.close();
                }
            }
            db.delete("MessageRecord", "user=? and my_user=? and id=?", new String[]{mUser, UtilTool.getTocoId(), id});
            DatabaseManager.getInstance().closeWritableDatabase();
            return messageInfo;
        }
    }

    public int deleteSingleMessageMsgId(String msgId,int isBurnReading){
        synchronized (lock) {
            SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(true);
            MessageInfo messageInfo = null;
            Cursor cursor = db.rawQuery("select * from MessageRecord where msgId=? and my_user=?", new String[]{ msgId, UtilTool.getTocoId()});
            if (cursor.moveToLast()) {
                String showChatTime = cursor.getString(cursor.getColumnIndex("showChatTime"));
                long createTime = cursor.getLong(cursor.getColumnIndex("createTime"));
                cursor.close();
                if (!StringUtils.isEmpty(showChatTime)) {
                    Cursor cursor1 = db.rawQuery("select * from MessageRecord where createTime > ? and my_user=? and isBurnReading=? ORDER BY createTime asc", new String[]{createTime + "", UtilTool.getTocoId(),isBurnReading+""});
                    if (cursor1.moveToFirst()) {
                        String msgId1 = cursor1.getString(cursor1.getColumnIndex("msgId"));
                        long showTime = cursor1.getLong(cursor1.getColumnIndex("createTime"));
                        messageInfo = addMessage(cursor1);
                        messageInfo.setShowChatTime(showTime + "");
                        updateShowTimeMessage(msgId1, showTime + "");
                    }
                    cursor1.close();
                }
            }
            int numm= db.delete("MessageRecord", "my_user=? and msgId=?", new String[]{ UtilTool.getTocoId(), msgId});
            DatabaseManager.getInstance().closeWritableDatabase();
            return numm;
        }
    }

    public void updateShowTimeMessage(String id,String showChatTime) {
        synchronized (lock) {
            SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(true);
            ContentValues values = new ContentValues();
            values.put("showChatTime", showChatTime);
            db.update("MessageRecord", values, "msgId=?", new String[]{id + ""});
            DatabaseManager.getInstance().closeWritableDatabase();
        }
    }

    public void updateVoice(String id,String voice) {
        synchronized (lock) {
            SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(true);
            ContentValues values = new ContentValues();
            values.put("voice", voice);
            db.update("MessageRecord", values, "msgId=?", new String[]{id + ""});
            DatabaseManager.getInstance().closeWritableDatabase();
        }
    }

    public String findLastMessageConversation(String roomid,int isBurnReading) {
        synchronized (lock) {
            SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(false);
            String conversation = null;
            Cursor cursor = db.rawQuery("select converstaion from MessageRecord where user=? and my_user=? and isBurnReading=? ORDER BY createTime asc",
                    new String[]{roomid, UtilTool.getTocoId(),isBurnReading+""});

            if (cursor.moveToLast()) {
                conversation = cursor.getString(cursor.getColumnIndex("converstaion"));
            }
            cursor.close();
            DatabaseManager.getInstance().closeWritableDatabase();
            return conversation;
        }
    }

    public String findLastMessageConversationTime(String roomid,int isBurnReading) {
        synchronized (lock) {
            SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(false);
            String time = "";
            Cursor cursor = db.rawQuery("select time from MessageRecord where user=? and my_user=? and isBurnReading=? ORDER BY createTime asc",
                    new String[]{roomid, UtilTool.getTocoId(),isBurnReading+""});

            if (cursor.moveToLast()) {
                time = cursor.getString(cursor.getColumnIndex("time"));
            }
            cursor.close();
            DatabaseManager.getInstance().closeWritableDatabase();
            return time;
        }
    }

    public long findLastMessageConversationCreateTime(String roomid,int isBurnReading) {
        synchronized (lock) {
            SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(false);
            long time = 0;
            Cursor cursor = db.rawQuery("select createTime from MessageRecord where user=? and my_user=? and isBurnReading=? ORDER BY createTime asc",
                    new String[]{roomid, UtilTool.getTocoId(),isBurnReading+""});

            if (cursor.moveToLast()) {
                time = cursor.getLong(cursor.getColumnIndex("createTime"));
            }
            cursor.close();
            DatabaseManager.getInstance().closeWritableDatabase();
            return time;
        }
    }

    public void updateMessageState(String id, int state) {
        synchronized (lock) {
            SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(true);
            ContentValues values = new ContentValues();
            values.put("state", state);
            db.update("MessageRecord", values, "id=?", new String[]{id});
            DatabaseManager.getInstance().closeWritableDatabase();
        }
    }

    public void updateMessageRedState(String redId, int state) {
        synchronized (lock) {
            SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(true);
            ContentValues values = new ContentValues();
            values.put("state", state);
            db.update("MessageRecord", values, "redId=?", new String[]{redId});
            DatabaseManager.getInstance().closeWritableDatabase();
        }
    }

    public void updateImageType(String id, int imageType) {
        synchronized (lock) {
            SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(true);
            ContentValues values = new ContentValues();
            values.put("imageType", imageType);
            db.update("MessageRecord", values, "id=?", new String[]{id});
            DatabaseManager.getInstance().closeWritableDatabase();
        }
    }

    public void updateImageVoice(String id, String voice) {
        synchronized (lock) {
            SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(true);
            ContentValues values = new ContentValues();
            values.put("voice", voice);
            db.update("MessageRecord", values, "id=?", new String[]{id});
            DatabaseManager.getInstance().closeWritableDatabase();
        }
    }

    public int addRequest(String user, int type,String userName) {
        synchronized (lock) {
            SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(true);
            ContentValues values = new ContentValues();
            values.put("my_user", UtilTool.getTocoId());
            values.put("user", user);
            values.put("type", type);
            values.put("userName", userName);
            int id = (int) db.insert("AddRequest", null, values);
            Log.e("wgy", "添加请求数据库成功");
            DatabaseManager.getInstance().closeWritableDatabase();
            return id;
        }
    }

    public boolean findRequest(String user) {
        synchronized (lock) {
            SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(false);
            Cursor cursor = db.rawQuery("select * from AddRequest where user=? and my_user=?",
                    new String[]{user, UtilTool.getTocoId()});
            boolean result = cursor.moveToNext();
            cursor.close();
            DatabaseManager.getInstance().closeWritableDatabase();
            return result;
        }
    }

    public AddRequestInfo queryRequest(String user) {
        synchronized (lock) {
            SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(true);
            String sql = "select * from AddRequest where my_user=? and user=?";
            Cursor c = db.rawQuery(sql, new String[]{UtilTool.getTocoId(), user});
            AddRequestInfo addRequestInfo = new AddRequestInfo();
            while (c.moveToNext()) {
                addRequestInfo.setUser(c.getString(c.getColumnIndex("user")));
                addRequestInfo.setId(c.getInt(c.getColumnIndex("id")));
                addRequestInfo.setType(c.getInt(c.getColumnIndex("type")));
                addRequestInfo.setUserName(c.getString(c.getColumnIndex("userName")));
            }
            c.close();
            DatabaseManager.getInstance().closeWritableDatabase();
            return addRequestInfo;
        }
    }


    public void updateRequest(int id, int type) {
        synchronized (lock) {
            SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(true);
            ContentValues values = new ContentValues();
            values.put("type", type);
            db.update("AddRequest", values, "id=? and my_user=?", new String[]{id + "", UtilTool.getTocoId()});
            DatabaseManager.getInstance().closeWritableDatabase();
        }
    }
    public void updateRequest(String toco_id, int type) {
        synchronized (lock) {
            SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(true);
            ContentValues values = new ContentValues();
            values.put("type", type);
            db.update("AddRequest", values, "user=? and my_user=?", new String[]{toco_id, UtilTool.getTocoId()});
            DatabaseManager.getInstance().closeWritableDatabase();
        }
    }

    public ArrayList<AddRequestInfo> queryAllRequest() {
        synchronized (lock) {
            SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(false);
            ArrayList<AddRequestInfo> addRequestInfos = new ArrayList<>();
            String sql = "select * from AddRequest where my_user=?";
            Cursor c = db.rawQuery(sql, new String[]{UtilTool.getTocoId()});
            while (c.moveToNext()) {
                AddRequestInfo addRequestInfo = new AddRequestInfo();
                addRequestInfo.setUser(c.getString(c.getColumnIndex("user")));
                addRequestInfo.setId(c.getInt(c.getColumnIndex("id")));
                addRequestInfo.setType(c.getInt(c.getColumnIndex("type")));
                addRequestInfo.setUserName(c.getString(c.getColumnIndex("userName")));
                addRequestInfos.add(addRequestInfo);
            }
            c.close();
            DatabaseManager.getInstance().closeWritableDatabase();
            return addRequestInfos;
        }
    }
    public void deleteRequest() {
        synchronized (lock) {
            try {
                SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(true);
                db.delete("AddRequest", "my_user=? ", new String[]{UtilTool.getTocoId()});
            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                DatabaseManager.getInstance().closeWritableDatabase();
            }
        }
    }


    public synchronized void addConversation(ConversationInfo conversationInfo) {
        synchronized (lock) {
            SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(true);
            if (findConversation(conversationInfo.getUser())) {
                updateConversation(conversationInfo);
                return;
            }
            ContentValues values = new ContentValues();
            values.put("number", conversationInfo.getNumber());
            values.put("my_user", UtilTool.getTocoId());
            values.put("message", conversationInfo.getMessage());
            values.put("user", conversationInfo.getUser());
            values.put("time", conversationInfo.getTime());
            values.put("friend", conversationInfo.getFriend());
            values.put("istop", conversationInfo.getIstop());
            values.put("chatType", conversationInfo.getChatType());
            values.put("createTime", conversationInfo.getCreateTime());
            values.put("draft",conversationInfo.getDraft());
            values.put("atme",conversationInfo.getAtme());
            db.insert("ConversationRecord", null, values);
            DatabaseManager.getInstance().closeWritableDatabase();
        }
    }

    public boolean findConversation(String user) {
        synchronized (lock) {
            SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(false);
            Cursor cursor = db.rawQuery("select * from ConversationRecord where user=? and my_user=?",
                    new String[]{user, UtilTool.getTocoId()});
            boolean result = cursor.moveToNext();
            cursor.close();
            DatabaseManager.getInstance().closeWritableDatabase();
            return result;
        }
    }

    public void updateConversationCreateTime(String user,long createTime){
        synchronized (lock) {
            SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(true);
            ContentValues values = new ContentValues();
            values.put("createTime", createTime);
            db.update("ConversationRecord", values, "user=?", new String[]{user});
            DatabaseManager.getInstance().closeWritableDatabase();
        }
    }

    public int queryConversationNumber() {
        synchronized (lock) {
            SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(true);
            String sql = "select number from ConversationRecord where my_user=?";
            Cursor c = db.rawQuery(sql, new String[]{UtilTool.getTocoId()});
            int number = 0;
            while (c.moveToNext()) {
                number += (c.getInt(c.getColumnIndex("number")));
            }
            c.close();
            DatabaseManager.getInstance().closeWritableDatabase();
            return number;
        }
    }

    public void updateConversationName(String user,String name) {
        synchronized (lock) {
            if (!findConversation(user)) return;
            SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(true);
            ContentValues values = new ContentValues();
            values.put("friend", name);
            db.update("ConversationRecord", values, "user=? and my_user=?", new String[]{user, UtilTool.getTocoId()});
            DatabaseManager.getInstance().closeWritableDatabase();
        }
    }

    public void updateConversation(ConversationInfo conversationInfo) {
        synchronized (lock) {
            SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(true);
            ContentValues values = new ContentValues();
            values.put("number", conversationInfo.getNumber());
            values.put("my_user", UtilTool.getTocoId());
            values.put("message", conversationInfo.getMessage());
            values.put("time", conversationInfo.getTime());
            values.put("friend", conversationInfo.getFriend());
            values.put("istop", conversationInfo.getIstop());
            values.put("chatType", conversationInfo.getChatType());
            values.put("createTime", conversationInfo.getCreateTime());
            db.update("ConversationRecord", values, "user=? and my_user=?", new String[]{conversationInfo.getUser(), UtilTool.getTocoId()});
            DatabaseManager.getInstance().closeWritableDatabase();
        }
    }

    public void updateConversation(String user, int number, String chat, String time,long createTime) {
        synchronized (lock) {
            SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(true);
            ContentValues cv = new ContentValues();
            cv.put("number", number);
            cv.put("time", time);
            cv.put("message", chat);
            cv.put("createTime", createTime);
            db.update("ConversationRecord", cv, "user=? and my_user=?", new String[]{user, UtilTool.getTocoId()});
            DatabaseManager.getInstance().closeWritableDatabase();
        }
    }

    public void updateConversation(String name,String user, int number, String chat, String time,long createTime,String atme) {
        synchronized (lock) {
            SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(true);
            ContentValues cv = new ContentValues();
            cv.put("number", number);
            cv.put("time", time);
            cv.put("message", chat);
            cv.put("createTime", createTime);
            cv.put("friend",name);
            if(!StringUtils.isEmpty(atme))
            cv.put("atme",atme);
            db.update("ConversationRecord", cv, "user=? and my_user=?", new String[]{user, UtilTool.getTocoId()});
            DatabaseManager.getInstance().closeWritableDatabase();
        }
    }

    public void updateConversationNumber(String user, int number) {
        synchronized (lock) {
            SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(true);
            ContentValues cv = new ContentValues();
            cv.put("number", number);
            db.update("ConversationRecord", cv, "user=? and my_user=?", new String[]{user, UtilTool.getTocoId()});
            DatabaseManager.getInstance().closeWritableDatabase();
        }
    }

    public void updateConversationFriend(String user, String friend) {
        synchronized (lock) {
            SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(true);
            ContentValues cv = new ContentValues();
            cv.put("friend", friend);
            db.update("ConversationRecord", cv, "user=? and my_user=?", new String[]{user, UtilTool.getTocoId()});
            DatabaseManager.getInstance().closeWritableDatabase();
        }
    }

    public void updateConversationMessage(String user, String chat) {
        synchronized (lock) {
            SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(true);
            ContentValues cv = new ContentValues();
            cv.put("message", chat);
            db.update("ConversationRecord", cv, "user=? and my_user=?", new String[]{user, UtilTool.getTocoId()});
            DatabaseManager.getInstance().closeWritableDatabase();
        }
    }

    public void updateConversationIstop(String user, String istop) {
        synchronized (lock) {
            SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(true);
            ContentValues cv = new ContentValues();
            cv.put("istop", istop);
            db.update("ConversationRecord", cv, "user=? and my_user=?", new String[]{user, UtilTool.getTocoId()});
            DatabaseManager.getInstance().closeWritableDatabase();
        }
    }

    public String findConversationDraft(String user) {
        synchronized (lock) {
            SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(false);
            String draft = null;
            Cursor cursor = db.rawQuery("select draft from ConversationRecord where user=? and my_user=?",
                    new String[]{user, UtilTool.getTocoId()});
            while (cursor.moveToNext()) {
                draft = cursor.getString(cursor.getColumnIndex("draft"));
            }
            cursor.close();
            DatabaseManager.getInstance().closeWritableDatabase();
            return draft;
        }
    }

    public String findConversationAtme(String user) {
        synchronized (lock) {
            SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(false);
            String atme = null;
            Cursor cursor = db.rawQuery("select atme from ConversationRecord where user=? and my_user=?",
                    new String[]{user, UtilTool.getTocoId()});
            while (cursor.moveToNext()) {
                atme = cursor.getString(cursor.getColumnIndex("atme"));
            }
            cursor.close();
            DatabaseManager.getInstance().closeWritableDatabase();
            return atme;
        }
    }

    public String findConversationIstop(String user) {
        synchronized (lock) {
            SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(false);
            String istop = null;
            Cursor cursor = db.rawQuery("select istop from ConversationRecord where user=? and my_user=?",
                    new String[]{user, UtilTool.getTocoId()});
            while (cursor.moveToNext()) {
                istop = cursor.getString(cursor.getColumnIndex("istop"));
            }
            cursor.close();
            DatabaseManager.getInstance().closeWritableDatabase();
            return istop;
        }
    }

    public String findConversationName(String user) {
        synchronized (lock) {
            SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(false);
            String friend = null;
            Cursor cursor = db.rawQuery("select friend from ConversationRecord where user=? and my_user=?",
                    new String[]{user, UtilTool.getTocoId()});
            while (cursor.moveToNext()) {
                friend = cursor.getString(cursor.getColumnIndex("friend"));
            }
            cursor.close();
            DatabaseManager.getInstance().closeWritableDatabase();
            return friend;
        }
    }

    public List<ConversationInfo> queryConversation() {
        synchronized (lock) {
            SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(true);
            List<ConversationInfo> conversationList = new ArrayList<>();
            String sql = "select * from ConversationRecord where my_user=?";
            Cursor c = db.rawQuery(sql, new String[]{UtilTool.getTocoId()});
            while (c.moveToNext()) {
                ConversationInfo conversationInfo = new ConversationInfo();
                conversationInfo.setNumber(c.getInt(c.getColumnIndex("number")));
                conversationInfo.setMessage(c.getString(c.getColumnIndex("message")));
                conversationInfo.setTime(c.getString(c.getColumnIndex("time")));
                conversationInfo.setUser(c.getString(c.getColumnIndex("user")));
                conversationInfo.setFriend(c.getString(c.getColumnIndex("friend")));
                conversationInfo.setIstop(c.getString(c.getColumnIndex("istop")));
                conversationInfo.setChatType(c.getString(c.getColumnIndex("chatType")));
                conversationInfo.setCreateTime(c.getLong(c.getColumnIndex("createTime")));
                conversationInfo.setDraft(c.getString(c.getColumnIndex("draft")));
                conversationList.add(conversationInfo);
            }
            c.close();
            DatabaseManager.getInstance().closeWritableDatabase();
            return conversationList;
        }
    }

    public List<ConversationInfo> queryConversationGroup() {
        synchronized (lock) {
            SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(true);
            List<ConversationInfo> conversationList = new ArrayList<>();
            String sql = "select * from ConversationRecord where my_user=? and chatType=?";
            Cursor c = db.rawQuery(sql, new String[]{UtilTool.getTocoId(), RoomManage.ROOM_TYPE_MULTI});
            while (c.moveToNext()) {
                ConversationInfo conversationInfo = new ConversationInfo();
                conversationInfo.setNumber(c.getInt(c.getColumnIndex("number")));
                conversationInfo.setMessage(c.getString(c.getColumnIndex("message")));
                conversationInfo.setTime(c.getString(c.getColumnIndex("time")));
                conversationInfo.setUser(c.getString(c.getColumnIndex("user")));
                conversationInfo.setFriend(c.getString(c.getColumnIndex("friend")));
                conversationInfo.setIstop(c.getString(c.getColumnIndex("istop")));
                conversationInfo.setChatType(c.getString(c.getColumnIndex("chatType")));
                conversationInfo.setCreateTime(c.getLong(c.getColumnIndex("createTime")));
                conversationList.add(conversationInfo);
            }
            c.close();
            DatabaseManager.getInstance().closeWritableDatabase();
            return conversationList;
        }
    }

    public int queryNumber(String user) {
        synchronized (lock) {
            SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(true);
            String sql = "select * from ConversationRecord where my_user=? and user=?";
            Cursor c = db.rawQuery(sql, new String[]{UtilTool.getTocoId(), user});
            int number = 0;
            while (c.moveToNext()) {
                number = c.getInt(c.getColumnIndex("number"));
            }
            c.close();
            DatabaseManager.getInstance().closeWritableDatabase();
            return number;
        }
    }

    public void updateNumber(String user, int number) {
        synchronized (lock) {
            SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(true);
            ContentValues cv = new ContentValues();
            cv.put("number", number);
            db.update("ConversationRecord", cv, "user=? and my_user=?", new String[]{user, UtilTool.getTocoId()});
            DatabaseManager.getInstance().closeWritableDatabase();
        }
    }

    public void updateAtme(String user, String atme) {
        synchronized (lock) {
            SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(true);
            ContentValues cv = new ContentValues();
            cv.put("atme", atme);
            db.update("ConversationRecord", cv, "user=? and my_user=?", new String[]{user, UtilTool.getTocoId()});
            DatabaseManager.getInstance().closeWritableDatabase();
        }
    }


    public void updataConversationDraft(String user, String draft) {
        synchronized (lock) {
            SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(true);
            ContentValues cv = new ContentValues();
            cv.put("draft", draft);
            db.update("ConversationRecord", cv, "user=? and my_user=?", new String[]{user, UtilTool.getTocoId()});
            DatabaseManager.getInstance().closeWritableDatabase();
        }
    }

    public void deleteConversation(String user) {
        synchronized (lock) {
            SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(true);
            db.delete("ConversationRecord", "user=? and my_user=?", new String[]{user, UtilTool.getTocoId()});
            DatabaseManager.getInstance().closeWritableDatabase();
        }
    }

    public void deleteConversation(List<GroupInfo.DataBean> dataBeans) {
        synchronized (lock) {
            SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(true);
            List<ConversationInfo> conversationInfos = queryConversation();
            if (conversationInfos != null && conversationInfos.size() > 0) {
                for (ConversationInfo conversationInfo : conversationInfos) {
                    if (RoomManage.ROOM_TYPE_MULTI.equals(conversationInfo.getChatType())) {
                        A:
                        for (int i = 0; i < dataBeans.size(); i++) {
                            if (conversationInfo.getUser().equals(dataBeans.get(i).getId() + "")) {
                                break A;
                            }
                            if (i == dataBeans.size() - 1) {
                                deleteConversation(conversationInfo.getUser());
                            }
                        }
                    }
                }
            }
            DatabaseManager.getInstance().closeWritableDatabase();
        }

    }

    public void addUser(String user, String path) {
        synchronized (lock) {
            SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(true);
            ContentValues values = new ContentValues();
            values.put("my_user", UtilTool.getTocoId());
            values.put("user", user);
            values.put("path", path);
            values.put("remark", "");
            values.put("userName", "");
            values.put("status", 0);
            db.insert("UserImage", null, values);
            DatabaseManager.getInstance().closeWritableDatabase();
        }
    }
    public void addUser(UserInfo userInfo) {
        synchronized (lock) {
            SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(true);
            ContentValues values = new ContentValues();
            values.put("my_user", UtilTool.getTocoId());
            values.put("user", userInfo.getUser());
            values.put("path", userInfo.getPath());
            values.put("remark", userInfo.getRemark());
            values.put("userName", userInfo.getUserName());
            values.put("status", userInfo.getStatus());
            db.insert("UserImage", null, values);
            DatabaseManager.getInstance().closeWritableDatabase();
        }
    }

    public void addUserList(List<AuatarListInfo.DataBean> data) {
        synchronized (lock) {
            SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(true);
            for (AuatarListInfo.DataBean info : data) {
//            UtilTool.Log("好友", info.getToco_id());
                if (!findUser(info.getToco_id())) {
                    ContentValues values = new ContentValues();
                    values.put("my_user", UtilTool.getTocoId());
                    values.put("user", info.getToco_id());
                    values.put("userName", info.getName());
                    values.put("path", info.getAvatar());
                    values.put("remark", info.getRemark() + "");
                    values.put("status", 0);
                    db.insert("UserImage", null, values);
                } else {
                    ContentValues values = new ContentValues();
                    if (!queryUser(info.getToco_id()).getPath().equals(info.getAvatar())) {
                        values.put("path", info.getAvatar());
                        db.update("UserImage", values, "user=? and my_user=?", new String[]{info.getToco_id(), UtilTool.getTocoId()});
                    }
                    if (!queryUser(info.getToco_id()).getRemark().equals(info.getRemark() + "")) {
                        values.put("remark", info.getRemark());
                        db.update("UserImage", values, "user=? and my_user=?", new String[]{info.getToco_id(), UtilTool.getTocoId()});
                    }
                }
            }
            DatabaseManager.getInstance().closeWritableDatabase();
        }
    }

    public UserInfo queryUser(String user) {
        synchronized (lock) {
            SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(false);
            UserInfo userInfo = new UserInfo();
            String sql = "select * from UserImage where user=? and my_user=?";
            Cursor c = db.rawQuery(sql, new String[]{user, UtilTool.getTocoId()});
            while (c.moveToNext()) {
                userInfo.setUser(c.getString(c.getColumnIndex("user")));
                userInfo.setPath(c.getString(c.getColumnIndex("path")));
                userInfo.setRemark(c.getString(c.getColumnIndex("remark")));
                userInfo.setUserName(c.getString(c.getColumnIndex("userName")));
            }
            c.close();
            DatabaseManager.getInstance().closeWritableDatabase();
            return userInfo;
        }
    }

    public List<UserInfo> queryAllUser() {
        synchronized (lock) {
            SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(false);
            List<UserInfo> userInfos = new ArrayList<>();
            try {
                String sql = "select * from UserImage where my_user=?";
                Cursor c = db.rawQuery(sql, new String[]{UtilTool.getTocoId()});
                while (c.moveToNext()) {
                    UserInfo userInfo = new UserInfo();
                    userInfo.setUser(c.getString(c.getColumnIndex("user")));
                    userInfo.setPath(c.getString(c.getColumnIndex("path")));
                    userInfo.setStatus(c.getInt(c.getColumnIndex("status")));
                    userInfo.setRemark(c.getString(c.getColumnIndex("remark")));
                    userInfo.setUserName(c.getString(c.getColumnIndex("userName")));
                    userInfos.add(userInfo);
                }
                c.close();
            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                DatabaseManager.getInstance().closeWritableDatabase();
            }
            return userInfos;
        }
    }

    public List<String> queryAllUserName() {
        synchronized (lock) {
            SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(false);
            List<String> userInfos = new ArrayList<>();
            try {
                String sql = "select user from UserImage where my_user=?";
                Cursor c = db.rawQuery(sql, new String[]{UtilTool.getTocoId()});
                while (c.moveToNext()) {
                    String user = c.getString(c.getColumnIndex("user"));
                    userInfos.add(user);
                }
                c.close();
            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                DatabaseManager.getInstance().closeWritableDatabase();
            }
            return userInfos;
        }
    }

    public synchronized String queryRemark(String user) {
        synchronized (lock) {
            SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(false);
            String remark = "";
            try {
                String sql = "select remark from UserImage where user=? and my_user=?";
                Cursor c = db.rawQuery(sql, new String[]{user, UtilTool.getTocoId()});
                while (c.moveToNext()) {
                    remark = c.getString(c.getColumnIndex("remark"));
                }
                c.close();
            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                DatabaseManager.getInstance().closeWritableDatabase();
            }
            return remark;
        }
    }

    public void updateRemark(List<RemarkListInfo.DataBean> listdata) {
        synchronized (lock) {
            SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(true);
            for (RemarkListInfo.DataBean dataBean : listdata) {
                ContentValues cv = new ContentValues();
                cv.put("remark", dataBean.getRemark());
                db.update("UserImage", cv, "user=? and my_user=?", new String[]{dataBean.getName(), UtilTool.getTocoId()});
            }
            DatabaseManager.getInstance().closeWritableDatabase();
        }
    }

    public void updateRemark(String remark, String user) {
        synchronized (lock) {
            SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(true);
            ContentValues cv = new ContentValues();
            cv.put("remark", remark);
            db.update("UserImage", cv, "user=? and my_user=?", new String[]{user, UtilTool.getTocoId()});
            DatabaseManager.getInstance().closeWritableDatabase();
        }
    }

    public boolean findUser(String user) {
        synchronized (lock) {
            SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(false);
            Cursor cursor = null;
            try {
                cursor = db.rawQuery("select * from UserImage where user=? and my_user=?", new String[]{user, UtilTool.getTocoId()});
                boolean result = cursor.moveToNext();
                return result;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                DatabaseManager.getInstance().closeWritableDatabase();
                if (cursor != null)
                    cursor.close();
            }
            return false;
        }
    }

    public String findUserPath(String user) {
        synchronized (lock) {
            SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(false);
            String path = null;
            Cursor cursor = db.rawQuery("select path from UserImage where user=? and my_user=?",
                    new String[]{user, UtilTool.getTocoId()});
            while (cursor.moveToNext()) {
                path = cursor.getString(cursor.getColumnIndex("path"));
            }
            cursor.close();
            DatabaseManager.getInstance().closeWritableDatabase();
            return path;
        }
    }

    public String findUserName(String user) {
        synchronized (lock) {
            SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(false);
            String roomName = null;
            Cursor cursor = db.rawQuery("select userName from UserImage where user=? and my_user=?",
                    new String[]{user, UtilTool.getTocoId()});
            while (cursor.moveToNext()) {
                roomName = cursor.getString(cursor.getColumnIndex("userName"));
            }
            cursor.close();
            DatabaseManager.getInstance().closeWritableDatabase();
            return roomName;
        }
    }

    public void deleteUser(String user) {
        synchronized (lock) {
            SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(true);
            int type = db.delete("UserImage", "user=? and my_user=?", new String[]{user, UtilTool.getTocoId()});
            UtilTool.Log("fsdafa", type + "");
            UtilTool.Log("fsdafa", "删除" + user);
            DatabaseManager.getInstance().closeWritableDatabase();
        }
    }

    public void updateUser(String user, int status) {
        synchronized (lock) {
            SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(true);
            ContentValues cv = new ContentValues();
            cv.put("status", status);
            db.update("UserImage", cv, "user=? and my_user=?", new String[]{user, UtilTool.getTocoId()});
            DatabaseManager.getInstance().closeWritableDatabase();
        }
    }

    public void updatePath(String user, String path) {
        synchronized (lock) {
            SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(true);
            ContentValues cv = new ContentValues();
            cv.put("path", path);
            db.update("UserImage", cv, "user=? and my_user=?", new String[]{user, UtilTool.getTocoId()});
            DatabaseManager.getInstance().closeWritableDatabase();
        }
    }

    public void deleteAllFriend(){
        synchronized (lock) {
            SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(true);
            //不刪除自己的頭像
            UserInfo userInfo = queryUser(UtilTool.getTocoId());
            db.execSQL("DELETE FROM UserImage");
            if (StringUtils.isEmpty(userInfo.getUser())) {
                userInfo.setPath("");
                userInfo.setRemark(UtilTool.getUser());
                userInfo.setStatus(0);
                userInfo.setUser(UtilTool.getTocoId());
                userInfo.setUserName(UtilTool.getTocoId());
            }
            addUser(userInfo);
            DatabaseManager.getInstance().closeWritableDatabase();
        }

    }

    public void updateMessageHint(int id, int status) {
        synchronized (lock) {
            SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(true);
            ContentValues values = new ContentValues();
            values.put("sendStatus", status);
            db.update("MessageRecord", values, "id=?", new String[]{id + ""});
            DatabaseManager.getInstance().closeWritableDatabase();
        }
    }



    public void updateMessageStatus(int id) {
        synchronized (lock) {
            SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(true);
            ContentValues values = new ContentValues();
            values.put("voiceStatus", 1);
            db.update("MessageRecord", values, "id=?", new String[]{id + ""});
            DatabaseManager.getInstance().closeWritableDatabase();
        }
    }

    public void updateMessageStatus(String msgId,int status) {
        synchronized (lock) {
            SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(true);
            ContentValues values = new ContentValues();
            values.put("sendStatus", status);
            db.update("MessageRecord", values, "msgId=?", new String[]{msgId});
            DatabaseManager.getInstance().closeWritableDatabase();
        }
    }

    public void updateMessageContentUrlTitle(String msgId,String content,String headUrl,String title) {
        synchronized (lock) {
            SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(true);
            ContentValues values = new ContentValues();
            values.put("content", content);
            values.put("headUrl", headUrl);
            values.put("title",title);
            db.update("MessageRecord", values, "msgId=?", new String[]{msgId});
            DatabaseManager.getInstance().closeWritableDatabase();
        }
    }

    public void updateMessageCreateTime(String msgId,long createTime){
        synchronized (lock) {
            SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(true);
            ContentValues values = new ContentValues();
            values.put("createTime", createTime);
            db.update("MessageRecord", values, "msgId=?", new String[]{msgId});
            DatabaseManager.getInstance().closeWritableDatabase();
        }
    }

    public void updateMessage(int id,String message) {
        synchronized (lock) {
            SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(true);
            ContentValues values = new ContentValues();
            values.put("message", message);
            db.update("MessageRecord", values, "id=?", new String[]{id + ""});
            DatabaseManager.getInstance().closeWritableDatabase();
        }
    }

    public void updateMessage(String url, int id) {
        synchronized (lock) {
            SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(true);
            ContentValues values = new ContentValues();
            values.put("message", url);
            db.update("MessageRecord", values, "id=?", new String[]{id + ""});
            DatabaseManager.getInstance().closeWritableDatabase();
        }
    }

    public void addMessageMsgId(String msgId,String  roomId) {
        synchronized (lock) {
            SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(true);
            ContentValues values = new ContentValues();
            values.put("msgId", msgId);
            values.put("msgTime",System.currentTimeMillis());
            values.put("roomId",roomId);
            db.insert("MessageState", null, values);
            DatabaseManager.getInstance().closeWritableDatabase();
        }
    }

    public int deleteSingleMsgId(String msgId) {
        synchronized (lock) {
            SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(true);
            int type = -1;
            try {
                type = db.delete("MessageState", "msgId=? ", new String[]{msgId});
            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                DatabaseManager.getInstance().closeWritableDatabase();
            }
            return type;
        }
    }

    public void deleteAllMsgId() {
        synchronized (lock) {
            SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(true);
            try {
                db.execSQL("DELETE FROM MessageState");
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                DatabaseManager.getInstance().closeWritableDatabase();
            }
        }
    }

    public List<MessageInfo> queryAllMsgId() {
        synchronized (lock) {
            SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(false);
            List<MessageInfo> userInfos = new ArrayList<>();
            try {
                Cursor c = db.rawQuery("select * from MessageState", null);
                while (c.moveToNext()) {
                    MessageInfo messageInfo=new MessageInfo();
                    String msgId = c.getString(c.getColumnIndex("msgId"));
                    String roomId = c.getString(c.getColumnIndex("roomId"));
                    messageInfo.setMsgId(msgId);
                    messageInfo.setRoomId(roomId);
                    userInfos.add(messageInfo);
                }
                c.close();
            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                DatabaseManager.getInstance().closeWritableDatabase();
            }
            return userInfos;
        }
    }

    public String findIsWithdraw(String msgId) {
        synchronized (lock) {
            SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(false);
            String roomId = null;
            Cursor cursor = db.rawQuery("select roomId from MessageState where msgId=?",
                    new String[]{msgId});
            while (cursor.moveToNext()) {
                roomId = cursor.getString(cursor.getColumnIndex("roomId"));
            }
            cursor.close();
            DatabaseManager.getInstance().closeWritableDatabase();
            return roomId;
        }
    }

    public List<String> queryAllOvertimeMsgId(long time) {
        synchronized (lock) {
            SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(false);
            List<String> userInfos = new ArrayList<>();
            try {
                Cursor c = db.rawQuery("select * from MessageState where msgTime<?",  new String[]{time+""});
                while (c.moveToNext()) {
                    String msgId = c.getString(c.getColumnIndex("msgId"));
                    userInfos.add(msgId);
                }
                c.close();
            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                DatabaseManager.getInstance().closeWritableDatabase();
            }
            return userInfos;
        }
    }

    //-----------用戶信息
    public int addStrangerUserInfo(String user,String path,String userName) {
        synchronized (lock) {
            SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(true);
            if (findStrangerUserInfo(user)) {
                updateStrangerUserInfo(user, path, userName);
            }
            ContentValues values = new ContentValues();
            values.put("user", user);
            values.put("path", path);
            values.put("userName", userName);
            int id = (int) db.insert("UserInfo", null, values);
            DatabaseManager.getInstance().closeWritableDatabase();
            return id;
        }
    }

    public String findStrangerPath(String user) {
        synchronized (lock) {
            SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(false);
            String path = null;
            Cursor cursor = db.rawQuery("select path from UserInfo where user=?",
                    new String[]{user});
            while (cursor.moveToNext()) {
                path = cursor.getString(cursor.getColumnIndex("path"));
            }
            cursor.close();
            DatabaseManager.getInstance().closeWritableDatabase();
            return path;
        }
    }

    public boolean findStrangerUserInfo(String user) {
        synchronized (lock) {
            SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(false);
            Cursor cursor = db.rawQuery("select * from UserInfo where user=? ",
                    new String[]{user});
            boolean result = cursor.moveToNext();
            cursor.close();
            DatabaseManager.getInstance().closeWritableDatabase();
            return result;
        }
    }

    public String findStrangerName(String user) {
        synchronized (lock) {
            SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(false);
            String userName = null;
            Cursor cursor = db.rawQuery("select userName from UserInfo where user=?",
                    new String[]{user});
            while (cursor.moveToNext()) {
                userName = cursor.getString(cursor.getColumnIndex("userName"));
            }
            cursor.close();
            DatabaseManager.getInstance().closeWritableDatabase();
            return userName;
        }
    }

    public void updateStrangerUserInfo(String user,String path,String userName) {
        synchronized (lock) {
            SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(true);
            ContentValues values = new ContentValues();
            values.put("path", path);
            values.put("userName", userName);
            db.update("UserInfo", values, "user=?", new String[]{user});
            DatabaseManager.getInstance().closeWritableDatabase();
        }
    }
}
