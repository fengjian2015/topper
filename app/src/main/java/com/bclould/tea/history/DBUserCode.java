package com.bclould.tea.history;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.bclould.tea.model.UserCodeInfo;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by GIjia on 2018/6/15.
 */

public class DBUserCode {
    private static Object lock = new Object();
    private final Context mContext;


    public DBUserCode(Context context) {
        mContext = context;
        DatabaseManager.initializeInstance(new DBHelper(context));
        //因为getWritableDatabase内部调用了mContext.openOrCreateDatabase(mName, 0, mFactory);
        //所以要确保context已初始化,我们可以把实例化DBManager的步骤放在Activity的onCreate里
    }

    public void addUserCode(UserCodeInfo userCodeInfo) {
        synchronized (lock) {
            SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(true);
            if (findUserCode(userCodeInfo.getEmail())) return;
            ContentValues values = new ContentValues();
            values.put("email", userCodeInfo.getEmail());
            values.put("password", userCodeInfo.getPassword());
            db.insert("UserCodeDB", null, values);
            DatabaseManager.getInstance().closeWritableDatabase();
        }
    }

    public boolean findUserCode(String email) {
        synchronized (lock) {
            SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(false);
            Cursor cursor = db.rawQuery("select * from UserCodeDB where email=?",
                    new String[]{email});
            boolean result = cursor.moveToNext();
            cursor.close();
            DatabaseManager.getInstance().closeWritableDatabase();
            return result;
        }
    }

    public void deleteUser(String email) {// 删除数据
        synchronized (lock) {
            SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(true);
            db.delete("UserCodeDB", "email=?", new String[]{email});
            DatabaseManager.getInstance().closeWritableDatabase();
        }
    }

    public List<UserCodeInfo> selectAllUserCode() {
        synchronized (lock) {
            SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(true);
            List<UserCodeInfo> userCodeList = new ArrayList<UserCodeInfo>();
            Cursor cursor = db.rawQuery("select * from UserCodeDB", null);
            while (cursor.moveToNext()) {
                UserCodeInfo userCodeInfo = new UserCodeInfo();
                userCodeInfo.setEmail(cursor.getString(cursor.getColumnIndex("email")));
                userCodeInfo.setPassword(cursor.getString(cursor.getColumnIndex("password")));
                userCodeList.add(userCodeInfo);
            }
            cursor.close();
            DatabaseManager.getInstance().closeWritableDatabase();
            return userCodeList;
        }
    }

    public List<String> selectAllEmily() {
        synchronized (lock) {
            SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(true);
            List<String> userCodeList = new ArrayList<>();
            Cursor cursor = db.rawQuery("select * from UserCodeDB", null);
            while (cursor.moveToNext()) {
                userCodeList.add(cursor.getString(cursor.getColumnIndex("email")));
            }
            cursor.close();
            DatabaseManager.getInstance().closeWritableDatabase();
            return userCodeList;
        }
    }

    public UserCodeInfo queryLastUser() {
        synchronized (lock) {
            SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(true);
            UserCodeInfo userInfo = new UserCodeInfo();
            String sql = "select * from UserCodeDB order by id desc limit 1";
            Cursor c = db.rawQuery(sql, null);
            while (c.moveToNext()) {
                userInfo.setEmail(c.getString(c.getColumnIndex("email")));
                userInfo.setPassword(c.getString(c.getColumnIndex("password")));
            }
            c.close();
            DatabaseManager.getInstance().closeWritableDatabase();
            return userInfo;

        }
    }
}