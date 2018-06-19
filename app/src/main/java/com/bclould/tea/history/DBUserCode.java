package com.bclould.tea.history;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.support.annotation.RequiresApi;
import com.bclould.tea.model.UserCodeInfo;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by GIjia on 2018/6/15.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class DBUserCode {
    private static Object lock = new Object();
    private final Context mContext;
    private DBHelper helper;
    public SQLiteDatabase db;

    public DBUserCode(Context context) {
        mContext = context;
        helper = new DBHelper(context);
        //因为getWritableDatabase内部调用了mContext.openOrCreateDatabase(mName, 0, mFactory);
        //所以要确保context已初始化,我们可以把实例化DBManager的步骤放在Activity的onCreate里
    }

    public void addUserCode(UserCodeInfo userCodeInfo) {
        synchronized (lock) {
            if(findUserCode(userCodeInfo.getEmail()))return;
            db = helper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("email", userCodeInfo.getEmail());
            values.put("password", userCodeInfo.getPassword());
            db.insert("UserCodeDB", null, values);
        }
    }

    public boolean findUserCode(String email) {
        db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from UserCodeDB where email=?",
                new String[]{email});
        boolean result = cursor.moveToNext();
        cursor.close();
        return result;
    }

    public void deleteUser(String email) {// 删除数据
        synchronized (lock) {
            db = helper.getWritableDatabase();
            db.delete("UserCodeDB", "email=?", new String[] { email });
        }
    }

    public List<UserCodeInfo> selectAllUserCode() {
        synchronized (lock) {
            db = helper.getWritableDatabase();
            List<UserCodeInfo> userCodeList = new ArrayList<UserCodeInfo>();
            Cursor cursor = db.rawQuery("select * from UserCodeDB", null);
            while (cursor.moveToNext()) {
                UserCodeInfo userCodeInfo=new UserCodeInfo();
                userCodeInfo.setEmail(cursor.getString(cursor.getColumnIndex("email")));
                userCodeInfo.setPassword(cursor.getString(cursor.getColumnIndex("password")));
                userCodeList.add(userCodeInfo);
            }
            cursor.close();
            return userCodeList;
        }
    }

    public List<String> selectAllEmily() {
        synchronized (lock) {
            db = helper.getWritableDatabase();
            List<String> userCodeList = new ArrayList<>();
            Cursor cursor = db.rawQuery("select * from UserCodeDB", null);
            while (cursor.moveToNext()) {
                userCodeList.add(cursor.getString(cursor.getColumnIndex("email")));
            }
            cursor.close();
            return userCodeList;
        }
    }

    public UserCodeInfo queryLastUser() {
        db = helper.getWritableDatabase();
        UserCodeInfo userInfo = new UserCodeInfo();
        String sql = "select * from UserCodeDB order by id desc limit 1";
        Cursor c = db.rawQuery(sql, null);
        while (c.moveToNext()) {
            userInfo.setEmail(c.getString(c.getColumnIndex("email")));
            userInfo.setPassword(c.getString(c.getColumnIndex("password")));
        }
        c.close();
        return userInfo;
    }
}
