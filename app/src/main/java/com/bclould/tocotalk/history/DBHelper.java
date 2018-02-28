package com.bclould.tocotalk.history;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 作者：wgyscsf on 2017/4/12 18:28
 * 邮箱：wgyscsf@163.com
 * 博客：http://blog.csdn.net/wgyscsf
 */
public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "test.db";
    private static final int DATABASE_VERSION = 1;

    public DBHelper(Context context) {
        //CursorFactory设置为null,使用默认值
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table ConversationRecord(id integer primary key autoincrement, my_user varchar, number Integer, message varchar(225), time varchar(225), user varchar(225), friend varchar(225))");
        db.execSQL("create table MessageRecord(id integer primary key autoincrement, my_user varchar, user varchar, message varchar, time varchar, type Integer, coin varchar, count varchar, remark varchar, state Integer, redId Integer, voice varchar, voiceStatus integer, voiceDuration)");
        db.execSQL("create table AddRequest(id integer primary key autoincrement, my_user varchar, user varchar(225), type Integer)");
        db.execSQL("create table UserImage(id integer primary key autoincrement, my_user varchar, user varchar(225), path varchar, status Integer)");
    }

    //如果DATABASE_VERSION值被改为2,系统发现现有数据库版本不同,即会调用onUpgrade
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
