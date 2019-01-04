package com.bclould.tea.history;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.bclould.tea.utils.UtilTool;

/**
 * Created by GIjia on 2018/8/7.
 */
public class DBBurnManager {

    private Object lock=new Object();
    private Context mContext;

    public DBBurnManager(Context context) {
        mContext = context;
        DatabaseManager.initializeInstance(new DBHelper(context));
    }

    public synchronized void addBurn(String msgId) {
        synchronized (lock) {
            SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(true);
            if (findBurn(msgId)) {
                return;
            }
            ContentValues values = new ContentValues();
            values.put("my_user", UtilTool.getTocoId());
            values.put("msgId", msgId);
            values.put("createTime",System.currentTimeMillis());
            db.insert("BurnDB", null, values);
            DatabaseManager.getInstance().closeWritableDatabase();
        }
    }

    public boolean findBurn(String msgId) {
        synchronized (lock) {
            SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(false);
            Cursor cursor = db.rawQuery("select * from BurnDB where msgId=? and my_user=?",
                    new String[]{msgId, UtilTool.getTocoId()});
            boolean result = cursor.moveToNext();
            cursor.close();
            DatabaseManager.getInstance().closeWritableDatabase();
            return result;
        }
    }


    public long findBurnCreateTime(String msgId){
        synchronized (lock) {
            SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(false);
            long createTime = 0;
            Cursor cursor = db.rawQuery("select createTime from BurnDB where msgId=? and my_user=?",
                    new String[]{msgId, UtilTool.getTocoId()});
            while (cursor.moveToNext()) {
                createTime = cursor.getLong(cursor.getColumnIndex("createTime"));
            }
            cursor.close();
            DatabaseManager.getInstance().closeWritableDatabase();
            return createTime;
        }
    }

    public void deleteBurn(String msgId) {
        synchronized (lock) {
            SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(true);
            db.delete("BurnDB", "msgId=? and my_user=?", new String[]{msgId, UtilTool.getTocoId()});
            DatabaseManager.getInstance().closeWritableDatabase();
        }
    }
}
