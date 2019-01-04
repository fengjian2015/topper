package com.bclould.tea.history;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.bclould.tea.model.ConversationInfo;
import com.bclould.tea.utils.StringUtils;
import com.bclould.tea.utils.UtilTool;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by GIjia on 2018/8/6.
 */

public class DBConversationBurnManage {

    private Object lock=new Object();
    private Context mContext;
    public DBConversationBurnManage(Context context) {
        mContext = context;
        DatabaseManager.initializeInstance(new DBHelper(context));
        //因为getWritableDatabase内部调用了mContext.openOrCreateDatabase(mName, 0, mFactory);
        //所以要确保context已初始化,我们可以把实例化DBManager的步骤放在Activity的onCreate里
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
            values.put("friend", conversationInfo.getFriend());
            values.put("chatType", conversationInfo.getChatType());
            values.put("createTime", conversationInfo.getCreateTime());
            db.insert("ConversationBurnDB", null, values);
            DatabaseManager.getInstance().closeWritableDatabase();
        }
    }

    public boolean findConversation(String user) {
        synchronized (lock) {
            SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(false);
            Cursor cursor = db.rawQuery("select * from ConversationBurnDB where user=? and my_user=?",
                    new String[]{user, UtilTool.getTocoId()});
            boolean result = cursor.moveToNext();
            cursor.close();
            DatabaseManager.getInstance().closeWritableDatabase();
            return result;
        }
    }


    public int queryNumber(String user) {
        synchronized (lock) {
            SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(true);
            String sql = "select * from ConversationBurnDB where my_user=? and user=?";
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
            db.update("ConversationBurnDB", cv, "user=? and my_user=?", new String[]{user, UtilTool.getTocoId()});
            DatabaseManager.getInstance().closeWritableDatabase();
        }
    }

    public void updateConversationMessage(String user, String chat) {
        synchronized (lock) {
            SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(true);
            ContentValues cv = new ContentValues();
            cv.put("message", chat);
            db.update("ConversationBurnDB", cv, "user=? and my_user=?", new String[]{user, UtilTool.getTocoId()});
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
            values.put("friend", conversationInfo.getFriend());
            values.put("chatType", conversationInfo.getChatType());
            values.put("createTime", conversationInfo.getCreateTime());
            db.update("ConversationBurnDB", values, "user=? and my_user=?", new String[]{conversationInfo.getUser(), UtilTool.getTocoId()});
            DatabaseManager.getInstance().closeWritableDatabase();
        }
    }

    public void updateConversation(String user, int number, String chat,long createTime) {
        synchronized (lock) {
            SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(true);
            ContentValues cv = new ContentValues();
            cv.put("number", number);
            cv.put("message", chat);
            cv.put("createTime", createTime);
            db.update("ConversationBurnDB", cv, "user=? and my_user=?", new String[]{user, UtilTool.getTocoId()});
            DatabaseManager.getInstance().closeWritableDatabase();
        }
    }

    public void updateConversation(String name,String user, int number, String chat,long createTime) {
        synchronized (lock) {
            SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(true);
            ContentValues cv = new ContentValues();
            cv.put("number", number);
            cv.put("message", chat);
            cv.put("createTime", createTime);
            cv.put("friend",name);
            db.update("ConversationBurnDB", cv, "user=? and my_user=?", new String[]{user, UtilTool.getTocoId()});
            DatabaseManager.getInstance().closeWritableDatabase();
        }
    }

    public void updateConversationCreateTime(String user,long createTime){
        synchronized (lock) {
            SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(true);
            ContentValues values = new ContentValues();
            values.put("createTime", createTime);
            db.update("ConversationBurnDB", values, "user=?", new String[]{user});
            DatabaseManager.getInstance().closeWritableDatabase();
        }
    }

    public List<ConversationInfo> queryConversation() {
        synchronized (lock) {
            SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(true);
            List<ConversationInfo> conversationList = new ArrayList<>();
            String sql = "select * from ConversationBurnDB where my_user=?";
            Cursor c = db.rawQuery(sql, new String[]{UtilTool.getTocoId()});
            while (c.moveToNext()) {
                ConversationInfo conversationInfo = new ConversationInfo();
                conversationInfo.setNumber(c.getInt(c.getColumnIndex("number")));
                conversationInfo.setMessage(c.getString(c.getColumnIndex("message")));
                conversationInfo.setUser(c.getString(c.getColumnIndex("user")));
                conversationInfo.setFriend(c.getString(c.getColumnIndex("friend")));
                conversationInfo.setChatType(c.getString(c.getColumnIndex("chatType")));
                conversationInfo.setCreateTime(c.getLong(c.getColumnIndex("createTime")));
                conversationList.add(conversationInfo);
            }
            c.close();
            DatabaseManager.getInstance().closeWritableDatabase();
            return conversationList;
        }
    }

    public void deleteConversation(String user) {
        synchronized (lock) {
            SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(true);
            db.delete("ConversationBurnDB", "user=? and my_user=?", new String[]{user, UtilTool.getTocoId()});
            DatabaseManager.getInstance().closeWritableDatabase();
        }
    }
}
