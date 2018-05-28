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
    private static final int DATABASE_VERSION = 9;

    public DBHelper(Context context) {
        //CursorFactory设置为null,使用默认值
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table ConversationRecord(id integer primary key autoincrement, my_user varchar, number integer, message varchar, time varchar, user varchar, friend varchar, istop varchar,chatType varchar)");
        db.execSQL("create table MessageRecord(id integer primary key autoincrement, my_user varchar, user varchar, message varchar, time varchar, type integer, coin varchar, count varchar, remark varchar" +
                ", state integer, redId integer, voice varchar, voiceStatus integer, voiceTime varchar, sendStatus integer, msgType integer" +
                ", imageType integer,send varchar,lat float,lng float,address varchar,title varchar,headUrl varchar,cardUser varchar)");
        db.execSQL("create table AddRequest(id integer primary key autoincrement, my_user varchar, user varchar, type integer)");
        db.execSQL("create table UserImage(id integer primary key autoincrement, my_user varchar, user varchar, status integer, path varchar, remark varchar)");
        db.execSQL("create table RoomManage(id integer primary key autoincrement, roomImage varchar, roomId varchar, roomName varchar, roomNumber integer,my_user varchar)");
        db.execSQL("create table RoomMember(id integer primary key autoincrement, name varchar, jid varchar, image_url varchar, remark varchar,my_user varchar)");
    }

    //如果DATABASE_VERSION值被改为2,系统发现现有数据库版本不同,即会调用onUpgrade
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        switch (oldVersion){
            case 8:
                //2018-5-28增加發送名片
                db.execSQL("ALTER TABLE MessageRecord ADD headUrl TEXT");
                db.execSQL("ALTER TABLE MessageRecord ADD cardUser TEXT");
                //创建房间表和成员表
                String roomManage = "create table if not exists RoomManage"  + "(roomImage text,roomId text,roomName text,roomNumber integer,my_user text)";
                db.execSQL( roomManage );
                String roomMember = "create table if not exists RoomMember"  + "(name text,jid text,image_url text,remark text,my_user text)";
                db.execSQL( roomMember );
                break;
        }

            //20180523增加房间类型，用于新增群聊判断
//            db.execSQL("ALTER TABLE ConversationRecord ADD chatType TEXT");
//            db.execSQL("ALTER TABLE MessageRecord ADD send TEXT");//2018-5-15增加是誰發送的消息
            //2018-5-22增加定位字段
//            db.execSQL("ALTER TABLE MessageRecord ADD lat REAL");
//            db.execSQL("ALTER TABLE MessageRecord ADD lng REAL");
//            db.execSQL("ALTER TABLE MessageRecord ADD address TEXT");
//            db.execSQL("ALTER TABLE MessageRecord ADD title TEXT");
//        db.execSQL("ALTER TABLE ConversationRecord ADD istop TEXT");
//        db.execSQL("ALTER TABLE UserImage ADD remark TEXT");
    }
}
