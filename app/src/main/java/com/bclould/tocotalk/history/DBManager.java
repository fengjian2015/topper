package com.bclould.tocotalk.history;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.Toast;

import com.bclould.tocotalk.model.AddRequestInfo;
import com.bclould.tocotalk.model.AuatarListInfo;
import com.bclould.tocotalk.model.ConversationInfo;
import com.bclould.tocotalk.model.MessageInfo;
import com.bclould.tocotalk.model.RemarkListInfo;
import com.bclould.tocotalk.model.UserInfo;
import com.bclould.tocotalk.utils.Constants;
import com.bclould.tocotalk.utils.StringUtils;
import com.bclould.tocotalk.utils.UtilTool;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.N)
public class DBManager {
    private final Context mContext;
    private DBHelper helper;
    public SQLiteDatabase db;
    private String mUser = "";
    private int mOffset = 1;

    public DBManager(Context context) {
        mContext = context;
        helper = new DBHelper(context);
        //因为getWritableDatabase内部调用了mContext.openOrCreateDatabase(mName, 0, mFactory);
        //所以要确保context已初始化,我们可以把实例化DBManager的步骤放在Activity的onCreate里
    }


    public int addMessage(MessageInfo messageInfo) {
        db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("count", messageInfo.getCount());
        values.put("my_user", UtilTool.getJid());
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
        values.put("lat",messageInfo.getLat());
        values.put("lng",messageInfo.getLng());
        values.put("address",messageInfo.getAddress());
        values.put("title",messageInfo.getTitle());
        int id = (int) db.insert("MessageRecord", null, values);
        UtilTool.Log("日志", "添加成功" + messageInfo.toString());
        return id;
    }

    public long queryMessageCount(String user) {
        db = helper.getReadableDatabase();
        String sql = "select count(*) from MessageRecord where user=? and my_user=?";
        Cursor cursor = db.rawQuery(sql, new String[]{user, UtilTool.getJid()});
        cursor.moveToFirst();
        long count = cursor.getLong(0);
        cursor.close();
        return count;
    }


    //查詢紅包和轉賬記錄
    public List<MessageInfo> queryTypeMessage(String user, int fromtype, int sentype, int sendred, int fromred, int limit) {
        db = helper.getReadableDatabase();
        ArrayList<MessageInfo> messageInfos = new ArrayList<MessageInfo>();
        String sql = "select * from MessageRecord where user=? and my_user=? and (msgType=? or msgType=? or msgType=? or msgType=?) ORDER BY id desc limit ?";
        Cursor c = db.rawQuery(sql, new String[]{user, UtilTool.getJid(), fromtype + "", sentype + "", sendred + "", fromred + "", limit * 10 + ""});
        if (c != null) {
            while (c.moveToNext()) {
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
                messageInfos.add(messageInfo);
            }
            c.close();
        }
        return messageInfos;
    }

    //模糊查找文本消息
    public List<MessageInfo> queryTextMessage(String user, String content, int limit) {
        db = helper.getReadableDatabase();
        ArrayList<MessageInfo> messageInfos = new ArrayList<MessageInfo>();
        String sql = "select * from MessageRecord where user=? and my_user=? and message like ? ORDER BY id desc limit ?";
        Cursor c = db.rawQuery(sql, new String[]{user, UtilTool.getJid(), "%" + content + "%", limit * 10 + ""});
        if (c != null) {
            while (c.moveToNext()) {
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
                messageInfos.add(messageInfo);
            }
            c.close();
        }
        return messageInfos;
    }

    //通過消息類型查找聊天記錄
    public List<MessageInfo> queryTypeMessage(String user, int fromtype, int sentype, int limit) {
        db = helper.getReadableDatabase();
        ArrayList<MessageInfo> messageInfos = new ArrayList<MessageInfo>();
        String sql = "select * from MessageRecord where user=? and my_user=? and (msgType=? or msgType=?) ORDER BY id desc limit ?";
        Cursor c = db.rawQuery(sql, new String[]{user, UtilTool.getJid(), fromtype + "", sentype + "", limit * 20 + ""});
        if (c != null) {
            while (c.moveToNext()) {
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
                messageInfos.add(messageInfo);
            }
            c.close();
        }
        return messageInfos;
    }

    public List<MessageInfo> queryMessage(String user) {
        long count = queryMessageCount(user);
        db = helper.getReadableDatabase();
        ArrayList<MessageInfo> messageInfos = new ArrayList<MessageInfo>();
        String sql = "select * from MessageRecord where user=? and my_user=? limit ?,?";
        Cursor c = db.rawQuery(sql, new String[]{user, UtilTool.getJid(), count - 10 + "", 10 + ""});
        if (c != null) {
            while (c.moveToNext()) {
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
                messageInfos.add(messageInfo);
            }
            c.close();
        }
        return messageInfos;
    }

    /**
     * 用於刷新
     *
     * @param user
     * @param id
     * @return
     */
    public List<MessageInfo> queryRefreshMessage(String user, int id) {
        db = helper.getReadableDatabase();
        ArrayList<MessageInfo> messageInfos = new ArrayList<MessageInfo>();
        String sql = "select * from MessageRecord where user=? and my_user=? and id < ? ORDER BY id desc limit ?";
        Cursor c = db.rawQuery(sql, new String[]{user, UtilTool.getJid(), id + "", 10 + ""});
        if (c != null) {
            while (c.moveToNext()) {
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
                messageInfos.add(messageInfo);
            }
            c.close();
        }
        if (messageInfos.size() == 0) {
            Toast.makeText(mContext, "没有更多记录了", Toast.LENGTH_SHORT).show();
        }
        Collections.reverse(messageInfos);
        return messageInfos;
    }

    /**
     * 用於加載
     *
     * @param user
     * @param id
     * @return
     */
    public List<MessageInfo> queryLoadMessage(String user, int id, boolean isFist) {

        ArrayList<MessageInfo> messageInfos = new ArrayList<MessageInfo>();
        if(id<=0){
            Toast.makeText(mContext, "没有更多记录了", Toast.LENGTH_SHORT).show();
            return messageInfos;
        }
        String sql;
        db = helper.getReadableDatabase();
        if (isFist) {
            sql = "select * from MessageRecord where user=? and my_user=? and id >= ? limit ?";
        } else {
            sql = "select * from MessageRecord where user=? and my_user=? and id > ? limit ?";
        }
        Cursor c = db.rawQuery(sql, new String[]{user, UtilTool.getJid(), id + "", 10 + ""});
        if (c != null) {
            while (c.moveToNext()) {
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
                messageInfos.add(messageInfo);
            }
            c.close();
        }
        if (messageInfos.size() == 0) {
            Toast.makeText(mContext, "没有更多记录了", Toast.LENGTH_SHORT).show();
        }
        return messageInfos;
    }

    public List<MessageInfo> pagingQueryMessage(String user) {
        long count = queryMessageCount(user);
        db = helper.getReadableDatabase();
        ArrayList<MessageInfo> messageInfos = new ArrayList<MessageInfo>();
        String sql = "select * from MessageRecord where user=? and my_user=? limit ?,?";
        Cursor c = null;
        if (count > 10) {
            if (mUser.equals(user)) {
                Log.e("wgy", "再次查询" + mOffset);
                if (count - mOffset * 10 < 0) {
                    if (count - mOffset * 10 > -10) {
                        c = db.rawQuery(sql, new String[]{user, UtilTool.getJid(), 0 + "", count - (mOffset - 1) * 10 + ""});
                        mOffset++;
                    } else {
                        Toast.makeText(mContext, "没有更多记录了", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    c = db.rawQuery(sql, new String[]{user, UtilTool.getJid(), count - mOffset * 10 + "", 10 + ""});
                    mOffset++;
                }
            } else {
                mOffset = 2;
                if (count - mOffset * 10 < 0) {
                    c = db.rawQuery(sql, new String[]{user, UtilTool.getJid(), 0 + "", count - (mOffset - 1) * 10 + ""});
                    mOffset++;
                } else {
                    Log.e("wgy", "一开始查询" + mOffset);
                    c = db.rawQuery(sql, new String[]{user, UtilTool.getJid(), count - 10 * mOffset + "", 10 + ""});
                    mOffset++;
                }
            }
        } else {
            Toast.makeText(mContext, "没有更多记录了", Toast.LENGTH_SHORT).show();
        }
        if (c != null) {
            while (c.moveToNext()) {
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
                messageInfos.add(messageInfo);
            }

            c.close();
        }
        mUser = user;
        return messageInfos;
    }

    public void deleteMessage(String user) {
        db = helper.getWritableDatabase();
        db.delete("MessageRecord", "user=? and my_user=?", new String[]{user, UtilTool.getJid()});
    }

    public void deleteSingleMessage(String mUser,String id){
        db = helper.getWritableDatabase();
        db.delete("MessageRecord", "user=? and my_user=? and id=?", new String[]{mUser, UtilTool.getJid(),id});
    }

    public void updateMessageState(String id, int state) {
        db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("state", state);
        db.update("MessageRecord", values, "id=?", new String[]{id});
    }

    public void updateImageType(String id, int imageType) {
        db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("imageType", imageType);
        db.update("MessageRecord", values, "id=?", new String[]{id});
    }

    public int addRequest(String user, int type) {
        db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("my_user", UtilTool.getJid());
        values.put("user", user);
        values.put("type", type);
        int id = (int) db.insert("AddRequest", null, values);
        Log.e("wgy", "添加请求数据库成功");
        return id;
    }

    public boolean findRequest(String user) {
        db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from AddRequest where user=? and my_user=?",
                new String[]{user, UtilTool.getJid()});
        boolean result = cursor.moveToNext();
        cursor.close();
        return result;
    }

    public AddRequestInfo queryRequest(String user) {
        db = helper.getWritableDatabase();
        String sql = "select * from AddRequest where my_user=? and user=?";
        Cursor c = db.rawQuery(sql, new String[]{UtilTool.getJid(), user});
        AddRequestInfo addRequestInfo = new AddRequestInfo();
        while (c.moveToNext()) {
            addRequestInfo.setUser(c.getString(c.getColumnIndex("user")));
            addRequestInfo.setId(c.getInt(c.getColumnIndex("id")));
            addRequestInfo.setType(c.getInt(c.getColumnIndex("type")));
        }
        c.close();
        return addRequestInfo;
    }

    public void updateRequest(int id, int type) {
        db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("type", type);
        db.update("AddRequest", values, "id=? and my_user=?", new String[]{id + "", UtilTool.getJid()});
    }

    public ArrayList<AddRequestInfo> queryAllRequest() {
        db = helper.getReadableDatabase();
        ArrayList<AddRequestInfo> addRequestInfos = new ArrayList<>();
        String sql = "select * from AddRequest where my_user=?";
        Cursor c = db.rawQuery(sql, new String[]{UtilTool.getJid()});
        while (c.moveToNext()) {
            AddRequestInfo addRequestInfo = new AddRequestInfo();
            addRequestInfo.setUser(c.getString(c.getColumnIndex("user")));
            addRequestInfo.setId(c.getInt(c.getColumnIndex("id")));
            addRequestInfo.setType(c.getInt(c.getColumnIndex("type")));
            addRequestInfos.add(addRequestInfo);
        }
        c.close();
        return addRequestInfos;
    }


    public void addConversation(ConversationInfo conversationInfo) {
        if (findConversation(conversationInfo.getUser())) {
            return;
        }
        db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("number", conversationInfo.getNumber());
        values.put("my_user", UtilTool.getJid());
        values.put("message", conversationInfo.getMessage());
        values.put("user", conversationInfo.getUser());
        values.put("time", conversationInfo.getTime());
        values.put("friend", conversationInfo.getFriend());
        values.put("istop", conversationInfo.getIstop());
        db.insert("ConversationRecord", null, values);
    }

    public boolean findConversation(String user) {
        db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from ConversationRecord where user=? and my_user=?",
                new String[]{user, UtilTool.getJid()});
        boolean result = cursor.moveToNext();
        cursor.close();
        return result;
    }

    public void updateConversation(String user, int number, String chat, String time) {
        db = helper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("number", number);
        cv.put("time", time);
        cv.put("message", chat);
        db.update("ConversationRecord", cv, "user=? and my_user=?", new String[]{user, UtilTool.getJid()});
    }

    public void updateConversationIstop(String user, String istop) {
        db = helper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("istop", istop);
        db.update("ConversationRecord", cv, "user=? and my_user=?", new String[]{user, UtilTool.getJid()});
    }

    public String findConversationIstop(String user) {
        db = helper.getReadableDatabase();
        String istop = null;
        Cursor cursor = db.rawQuery("select istop from ConversationRecord where user=? and my_user=?",
                new String[]{user, UtilTool.getJid()});
        while (cursor.moveToNext()) {
            istop = cursor.getString(cursor.getColumnIndex("istop"));
        }
        cursor.close();
        return istop;
    }

    public List<ConversationInfo> queryConversation() {
        db = helper.getWritableDatabase();
        List<ConversationInfo> conversationList = new ArrayList<>();
        String sql = "select * from ConversationRecord where my_user=?";
        Cursor c = db.rawQuery(sql, new String[]{UtilTool.getJid()});
        while (c.moveToNext()) {
            ConversationInfo conversationInfo = new ConversationInfo();
            conversationInfo.setNumber(c.getInt(c.getColumnIndex("number")));
            conversationInfo.setMessage(c.getString(c.getColumnIndex("message")));
            conversationInfo.setTime(c.getString(c.getColumnIndex("time")));
            conversationInfo.setUser(c.getString(c.getColumnIndex("user")));
            conversationInfo.setFriend(c.getString(c.getColumnIndex("friend")));
            conversationInfo.setIstop(c.getString(c.getColumnIndex("istop")));
            conversationList.add(conversationInfo);
        }
        c.close();
        return conversationList;
    }

    public int queryNumber(String user) {
        db = helper.getWritableDatabase();
        String sql = "select * from ConversationRecord where my_user=? and user=?";
        Cursor c = db.rawQuery(sql, new String[]{UtilTool.getJid(), user});
        int number = 0;
        while (c.moveToNext()) {
            number = c.getInt(c.getColumnIndex("number"));
        }
        c.close();
        return number;
    }

    public void updateNumber(String user, int number) {
        db = helper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("number", number);
        db.update("ConversationRecord", cv, "user=? and my_user=?", new String[]{user, UtilTool.getJid()});
    }

    public void deleteConversation(String user) {
        db = helper.getWritableDatabase();
        db.delete("ConversationRecord", "user=? and my_user=?", new String[]{user, UtilTool.getJid()});
    }

    public void addUser(String user, String path) {
        db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("my_user", UtilTool.getJid());
        values.put("user", user);
        values.put("path", path);
        values.put("remark", "");
        values.put("status", 0);
        db.insert("UserImage", null, values);
    }

    public void addUserList(List<AuatarListInfo.DataBean> data) {
        db = helper.getWritableDatabase();
        for (AuatarListInfo.DataBean info : data) {
            UtilTool.Log("好友", info.getName());
            if (!findUser(info.getName())) {
                ContentValues values = new ContentValues();
                values.put("my_user", UtilTool.getJid());
                values.put("user", info.getName());
                values.put("path", info.getAvatar());
                values.put("remark", info.getRemark() + "");
                values.put("status", 0);
                db.insert("UserImage", null, values);
            } else {
                ContentValues values = new ContentValues();
                if (!queryUser(info.getName()).getPath().equals(info.getAvatar())) {
                    values.put("path", info.getAvatar());
                    db.update("UserImage", values, "user=? and my_user=?", new String[]{info.getName(), UtilTool.getJid()});
                }
                if (!queryUser(info.getName()).getRemark().equals(info.getRemark() + "")) {
                    values.put("remark", info.getRemark());
                    db.update("UserImage", values, "user=? and my_user=?", new String[]{info.getName(), UtilTool.getJid()});

                }
            }
        }
    }

    public UserInfo queryUser(String user) {
        db = helper.getWritableDatabase();
        UserInfo userInfo = new UserInfo();
        String sql = "select * from UserImage where user=? and my_user=?";
        Cursor c = db.rawQuery(sql, new String[]{user, UtilTool.getJid()});
        while (c.moveToNext()) {
            userInfo.setUser(c.getString(c.getColumnIndex("user")));
            userInfo.setPath(c.getString(c.getColumnIndex("path")));
            userInfo.setRemark(c.getString(c.getColumnIndex("remark")));
        }
        c.close();
        return userInfo;
    }

    public List<UserInfo> queryAllUser() {
        List<UserInfo> userInfos = new ArrayList<>();
        try {
            db = helper.getWritableDatabase();
            String sql = "select * from UserImage where my_user=?";
            Cursor c = db.rawQuery(sql, new String[]{UtilTool.getJid()});
            while (c.moveToNext()) {
                UserInfo userInfo = new UserInfo();
                userInfo.setUser(c.getString(c.getColumnIndex("user")));
                userInfo.setPath(c.getString(c.getColumnIndex("path")));
                userInfo.setStatus(c.getInt(c.getColumnIndex("status")));
                userInfo.setRemark(c.getString(c.getColumnIndex("remark")));
                userInfos.add(userInfo);
            }
            c.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return userInfos;
    }

    public List<String> queryAllUserName() {
        List<String> userInfos = new ArrayList<>();
        try {
            db = helper.getWritableDatabase();
            String sql = "select user from UserImage where my_user=?";
            Cursor c = db.rawQuery(sql, new String[]{UtilTool.getJid()});
            while (c.moveToNext()) {
                String user = c.getString(c.getColumnIndex("user"));
                userInfos.add(user);
            }
            c.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return userInfos;
    }

    public String queryRemark(String user) {
        db = helper.getWritableDatabase();
        String remark = "";
        String sql = "select remark from UserImage where user=? and my_user=?";
        Cursor c = db.rawQuery(sql, new String[]{user, UtilTool.getJid()});
        while (c.moveToNext()) {
            remark = c.getString(c.getColumnIndex("remark"));
        }
        c.close();
        return remark;
    }

    public void updateRemark(List<RemarkListInfo.DataBean> listdata) {
        db = helper.getWritableDatabase();
        for (RemarkListInfo.DataBean dataBean : listdata) {
            ContentValues cv = new ContentValues();
            cv.put("remark", dataBean.getRemark());
            db.update("UserImage", cv, "user=? and my_user=?", new String[]{dataBean.getName(), UtilTool.getJid()});
        }
    }

    public void updateRemark(String remark, String user) {
        db = helper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("remark", remark);
        db.update("UserImage", cv, "user=? and my_user=?", new String[]{user, UtilTool.getJid()});
    }

    public boolean findUser(String user) {
        Cursor cursor = null;
        try {
            db = helper.getReadableDatabase();
            cursor = db.rawQuery("select * from UserImage where user=? and my_user=?", new String[]{user, UtilTool.getJid()});
            boolean result = cursor.moveToNext();
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return false;
    }

    public void deleteUser(String user) {
        db = helper.getWritableDatabase();
        int type = db.delete("UserImage", "user=? and my_user=?", new String[]{user, UtilTool.getJid()});
        UtilTool.Log("fsdafa", type + "");
        UtilTool.Log("fsdafa", "删除" + user);
    }

    public void updateUser(String user, int status) {
        db = helper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("status", status);
        db.update("UserImage", cv, "user=? and my_user=?", new String[]{user, UtilTool.getJid()});
    }

    public void updatePath(String user, String path) {
        db = helper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("path", path);
        db.update("UserImage", cv, "user=? and my_user=?", new String[]{user, UtilTool.getJid()});
    }

    public void updateMessageHint(int id, int status) {
        db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("sendStatus", status);
        db.update("MessageRecord", values, "id=?", new String[]{id + ""});
    }

    public void updateMessageStatus(int id) {
        db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("voiceStatus", 1);
        db.update("MessageRecord", values, "id=?", new String[]{id + ""});
    }

    public void updateMessage(String url, int id) {
        db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("message", url);
        db.update("MessageRecord", values, "id=?", new String[]{id + ""});
    }
}
