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
import com.bclould.tocotalk.model.ConversationInfo;
import com.bclould.tocotalk.model.MessageInfo;
import com.bclould.tocotalk.model.UserInfo;
import com.bclould.tocotalk.utils.UtilTool;

import java.util.ArrayList;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.N)
public class DBManager {
    private final Context mContext;
    private DBHelper helper;
    private SQLiteDatabase db;
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
        values.put("my_user", UtilTool.getMyUser());
        values.put("type", messageInfo.getType());
        values.put("remark", messageInfo.getRemark());
        values.put("message", messageInfo.getMessage());
        values.put("user", messageInfo.getUsername());
        values.put("time", messageInfo.getTime());
        values.put("coin", messageInfo.getCoin());
        values.put("state", messageInfo.getState());
        values.put("redId", messageInfo.getRedId());
        values.put("voice", messageInfo.getVoice());
        values.put("voiceStatus", messageInfo.getVoiceStatus());
        values.put("voiceTime", messageInfo.getVoiceTime());
        values.put("sendStatus", messageInfo.getSendStatus());
        values.put("msgType", messageInfo.getMsgType());
        int id = (int) db.insert("MessageRecord", null, values);
        UtilTool.Log("日志", "添加成功" + messageInfo.toString());
        db.close();
        return id;
    }

    public long queryMessageCount(String user) {
        db = helper.getReadableDatabase();
        String sql = "select count(*) from MessageRecord where user=? and my_user=?";
        Cursor cursor = db.rawQuery(sql, new String[]{user, UtilTool.getMyUser()});
        cursor.moveToFirst();
        long count = cursor.getLong(0);
        cursor.close();
        db.close();
        return count;
    }

    public List<MessageInfo> queryMessage(String user) {
        long count = queryMessageCount(user);
        db = helper.getReadableDatabase();
        ArrayList<MessageInfo> messageInfos = new ArrayList<MessageInfo>();
        String sql = "select * from MessageRecord where user=? and my_user=? limit ?,?";
        Cursor c = db.rawQuery(sql, new String[]{user, UtilTool.getMyUser(), count - 10 + "", 10 + ""});
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
                messageInfo.setState(c.getInt(c.getColumnIndex("state")));
                messageInfo.setId(c.getInt(c.getColumnIndex("id")));
                messageInfo.setRedId(c.getInt(c.getColumnIndex("redId")));
                messageInfo.setVoiceStatus(c.getInt(c.getColumnIndex("voiceStatus")));
                messageInfo.setVoice(c.getString(c.getColumnIndex("voice")));
                messageInfo.setVoiceTime(c.getString(c.getColumnIndex("voiceTime")));
                messageInfo.setSendStatus(c.getInt(c.getColumnIndex("sendStatus")));
                messageInfo.setMsgType(c.getInt(c.getColumnIndex("msgType")));
                messageInfos.add(messageInfo);
            }
            c.close();
        }
        db.close();
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
                        c = db.rawQuery(sql, new String[]{user, UtilTool.getMyUser(), 0 + "", count - (mOffset - 1) * 10 + ""});
                        mOffset++;
                    } else {
                        Toast.makeText(mContext, "没有更多记录了", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    c = db.rawQuery(sql, new String[]{user, UtilTool.getMyUser(), count - mOffset * 10 + "", 10 + ""});
                    mOffset++;
                }
            } else {
                mOffset = 2;
                if (count - mOffset * 10 < 0) {
                    c = db.rawQuery(sql, new String[]{user, UtilTool.getMyUser(), 0 + "", count - (mOffset - 1) * 10 + ""});
                    mOffset++;
                } else {
                    Log.e("wgy", "一开始查询" + mOffset);
                    c = db.rawQuery(sql, new String[]{user, UtilTool.getMyUser(), count - 10 * mOffset + "", 10 + ""});
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
                messageInfo.setState(c.getInt(c.getColumnIndex("state")));
                messageInfo.setId(c.getInt(c.getColumnIndex("id")));
                messageInfo.setRedId(c.getInt(c.getColumnIndex("redId")));
                messageInfo.setVoiceStatus(c.getInt(c.getColumnIndex("voiceStatus")));
                messageInfo.setVoice(c.getString(c.getColumnIndex("voice")));
                messageInfo.setVoiceTime(c.getString(c.getColumnIndex("voiceTime")));
                messageInfo.setSendStatus(c.getInt(c.getColumnIndex("sendStatus")));
                messageInfo.setMsgType(c.getInt(c.getColumnIndex("msgType")));
                messageInfos.add(messageInfo);
            }

            c.close();
        }
        db.close();
        mUser = user;
        return messageInfos;
    }

    public void deleteMessage(String user) {
        db = helper.getWritableDatabase();
        db.delete("MessageRecord", "user=? and my_user=?", new String[]{user, UtilTool.getMyUser()});
        db.close();
    }

    public void updateMessageState(String id, int state) {
        db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("state", state);
        db.update("MessageRecord", values, "id=?", new String[]{id});
        db.close();
    }

    public int addRequest(String user, int type) {
        db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("my_user", UtilTool.getMyUser());
        values.put("user", user);
        values.put("type", type);
        db.insert("AddRequest", null, values);
        Cursor cursor = db.rawQuery("select last_insert_rowid() from AddRequest", null);
        int id = 0;
        if (cursor.moveToFirst())
            id = cursor.getInt(0);
        db.close();
        Log.e("wgy", "添加请求数据库成功");
        return id;
    }

    public boolean findRequest(String user) {
        db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from ConversationRecord where user=? and my_user=?",
                new String[]{user, UtilTool.getMyUser()});
        boolean result = cursor.moveToNext();
        cursor.close();
        db.close();
        return result;
    }

    public void updateRequest(int id, int type) {
        db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("type", type);
        db.update("AddRequest", values, "id=? and my_user=?", new String[]{id + "", UtilTool.getMyUser()});
        db.close();
    }

    public ArrayList<AddRequestInfo> queryRequest() {
        db = helper.getReadableDatabase();
        ArrayList<AddRequestInfo> addRequestInfos = new ArrayList<>();
        String sql = "select * from AddRequest where my_user=?";
        Cursor c = db.rawQuery(sql, new String[]{UtilTool.getMyUser()});
        while (c.moveToNext()) {
            AddRequestInfo addRequestInfo = new AddRequestInfo();
            addRequestInfo.setUser(c.getString(c.getColumnIndex("user")));
            addRequestInfo.setId(c.getInt(c.getColumnIndex("id")));
            addRequestInfo.setType(c.getInt(c.getColumnIndex("type")));
            addRequestInfos.add(addRequestInfo);
        }
        c.close();
        db.close();
        return addRequestInfos;
    }


    public void addConversation(ConversationInfo conversationInfo) {
        db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("number", conversationInfo.getNumber());
        values.put("my_user", UtilTool.getMyUser());
        values.put("message", conversationInfo.getMessage());
        values.put("user", conversationInfo.getUser());
        values.put("time", conversationInfo.getTime());
        values.put("friend", conversationInfo.getFriend());
        db.insert("ConversationRecord", null, values);
        db.close();
    }

    public boolean findConversation(String user) {
        db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from ConversationRecord where user=? and my_user=?",
                new String[]{user, UtilTool.getMyUser()});
        boolean result = cursor.moveToNext();
        cursor.close();
        db.close();
        return result;
    }

    public void updateConversation(String user, int number, String chat, String time) {
        db = helper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("number", number);
        cv.put("time", time);
        cv.put("message", chat);
        db.update("ConversationRecord", cv, "user=? and my_user=?", new String[]{user, UtilTool.getMyUser()});
        db.close();

    }

    public List<ConversationInfo> queryConversation() {
        db = helper.getWritableDatabase();
        List<ConversationInfo> conversationList = new ArrayList<>();
        String sql = "select * from ConversationRecord where my_user=?";
        Cursor c = db.rawQuery(sql, new String[]{UtilTool.getMyUser()});
        while (c.moveToNext()) {
            ConversationInfo conversationInfo = new ConversationInfo();
            conversationInfo.setNumber(c.getInt(c.getColumnIndex("number")));
            conversationInfo.setMessage(c.getString(c.getColumnIndex("message")));
            conversationInfo.setTime(c.getString(c.getColumnIndex("time")));
            conversationInfo.setUser(c.getString(c.getColumnIndex("user")));
            conversationInfo.setFriend(c.getString(c.getColumnIndex("friend")));
            conversationList.add(conversationInfo);
        }
        c.close();
        db.close();
        return conversationList;
    }

    public int queryNumber(String user) {
        db = helper.getWritableDatabase();
        String sql = "select * from ConversationRecord where my_user=? and user=?";
        Cursor c = db.rawQuery(sql, new String[]{UtilTool.getMyUser(), user});
        int number = 0;
        while (c.moveToNext()) {
            number = c.getInt(c.getColumnIndex("number"));
        }
        c.close();
        db.close();
        return number;
    }

    public void updateNumber(String user, int number) {
        db = helper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("number", number);
        db.update("ConversationRecord", cv, "user=? and my_user=?", new String[]{user, UtilTool.getMyUser()});
        db.close();

    }

    public void deleteConversation(String user) {
        db = helper.getWritableDatabase();
        db.delete("ConversationRecord", "user=? and my_user=?", new String[]{user, UtilTool.getMyUser()});
        db.close();
    }

    public void addUser(String user, String path) {
        db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("my_user", UtilTool.getMyUser());
        values.put("user", user);
        values.put("path", path);
        values.put("status", 0);
        db.insert("UserImage", null, values);
        db.close();
    }

    public List<UserInfo> queryUser(String user) {
        db = helper.getWritableDatabase();
        List<UserInfo> userInfos = new ArrayList<>();
        String sql = "select * from UserImage where user=? and my_user=?";
        Cursor c = db.rawQuery(sql, new String[]{user, UtilTool.getMyUser()});
        while (c.moveToNext()) {
            UserInfo userInfo = new UserInfo();
            userInfo.setUser(c.getString(c.getColumnIndex("user")));
            userInfo.setPath(c.getString(c.getColumnIndex("path")));
            userInfos.add(userInfo);
        }
        c.close();
        db.close();
        return userInfos;
    }

    public List<UserInfo> queryAllUser() {
        List<UserInfo> userInfos = new ArrayList<>();
        try {
            db = helper.getWritableDatabase();
            String sql = "select * from UserImage where my_user=?";
            Cursor c = db.rawQuery(sql, new String[]{UtilTool.getMyUser()});
            while (c.moveToNext()) {
                UserInfo userInfo = new UserInfo();
                userInfo.setUser(c.getString(c.getColumnIndex("user")));
                userInfo.setPath(c.getString(c.getColumnIndex("path")));
                userInfo.setStatus(c.getInt(c.getColumnIndex("status")));
                userInfos.add(userInfo);
            }
            c.close();
//        db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return userInfos;
    }

    public boolean findUser(String user) {
        db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from UserImage where user=? and my_user=?",
                new String[]{user, UtilTool.getMyUser()});
        boolean result = cursor.moveToNext();
        cursor.close();
//        db.close();
        return result;
    }

    public void deleteUser(String user) {
        db = helper.getWritableDatabase();
        db.delete("UserImage", "user=? and my_user=?", new String[]{user, UtilTool.getMyUser()});
        db.close();
    }

    public void updateUser(String user, int status) {
        db = helper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("status", status);
        db.update("UserImage", cv, "user=? and my_user=?", new String[]{user, UtilTool.getMyUser()});
//        db.close();
    }

    public void updateMessageHint(int id, int status) {
        db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("sendStatus", 1);
        db.update("MessageRecord", values, "id=?", new String[]{id + ""});
        db.close();
    }

    public void updateMessageStatus(int id) {
        db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("voiceStatus", 1);
        db.update("MessageRecord", values, "id=?", new String[]{id + ""});
        db.close();
    }

    public void updateMessage(String url, int id) {
        db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("message", url);
        db.update("MessageRecord", values, "id=?", new String[]{id + ""});
        db.close();
    }
}
