package com.bclould.tea.history;

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
    private static final int DATABASE_VERSION = 25;

    public DBHelper(Context context) {
        //CursorFactory设置为null,使用默认值
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table ConversationRecord(id integer primary key autoincrement, my_user varchar, number integer, message varchar, time varchar, user varchar, friend varchar, istop varchar,chatType varchar" +
                ",createTime integer,draft varchar)");
        db.execSQL("create table MessageRecord(id integer primary key autoincrement, my_user varchar, user varchar, message varchar, time varchar, type integer, coin varchar, count varchar, remark varchar" +
                ", state integer, redId integer, voice varchar, voiceStatus integer, voiceTime varchar, sendStatus integer, msgType integer" +
                ", imageType integer,send varchar,lat float,lng float,address varchar,title varchar,headUrl varchar,cardUser varchar,linkUrl varchar" +
                ",content varchar,converstaion varchar,guessPw varchar,initiator varchar,betId varchr,periodQty varchar,filekey varchar,createTime integer,msgId varchar" +
                ",showChatTime varchar)");
        db.execSQL("create table AddRequest(id integer primary key autoincrement, my_user varchar, user varchar, type integer,userName varchar)");
        db.execSQL("create table UserImage(id integer primary key autoincrement, my_user varchar, user varchar, status integer, path varchar, remark varchar,userName varchar)");
        db.execSQL("create table RoomManage(id integer primary key autoincrement, roomImage varchar, roomId varchar, roomName varchar, roomNumber integer,my_user varchar,owner varchar,description varchar,isRefresh integer" +
                ",allowModify integer)");
        db.execSQL("create table RoomMember(id integer primary key autoincrement, name varchar, jid varchar, image_url varchar, remark varchar,my_user varchar,roomId varchar,isRefresh integer)");
        db.execSQL("create table MessageState(id integer primary key autoincrement, msgId varchar,msgTime integer)");
        db.execSQL("create table UserCodeDB(id integer primary key autoincrement, email varchar,password varchar)");
        db.execSQL("create table UserInfo(id integer primary key autoincrement, user varchar,path varchar,userName varchar)");//保存陌生人的信息
    }

    /**
     * 不要加break，用於夸版本升級
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String userCodeDB;
        switch (oldVersion){
            case 8:
                //2018-5-28增加發送名片
                db.execSQL("ALTER TABLE MessageRecord ADD headUrl TEXT");
                db.execSQL("ALTER TABLE MessageRecord ADD cardUser TEXT");
                //创建房间表和成员表
                String roomManage = "create table if not exists RoomManage"  + "(id integer primary key autoincrement,roomImage text,roomId text,roomName text,roomNumber integer,my_user text)";
                db.execSQL( roomManage );
                String roomMember = "create table if not exists RoomMember"  + "(id integer primary key autoincrement,name text,jid text,image_url text,remark text,my_user text)";
                db.execSQL( roomMember );
            case 9:
                //2018-05-29增加新聞分享和用於會話列表顯示（刪除最後一條消息后，列表還是不變）
                db.execSQL("ALTER TABLE MessageRecord ADD linkUrl TEXT");
                db.execSQL("ALTER TABLE MessageRecord ADD content TEXT");
                db.execSQL("ALTER TABLE MessageRecord ADD converstaion TEXT");
            case 10:
                //2018-05-30新增分享竞猜字段
                db.execSQL("ALTER TABLE MessageRecord ADD guessPw TEXT");
                db.execSQL("ALTER TABLE MessageRecord ADD initiator TEXT");
                db.execSQL("ALTER TABLE MessageRecord ADD betId TEXT");
                db.execSQL("ALTER TABLE MessageRecord ADD periodQty TEXT");
            case 12:
                //2018-06-06新曾亞馬遜的filekey,夥伴的名稱
                db.execSQL("ALTER TABLE MessageRecord ADD filekey TEXT");
                db.execSQL("ALTER TABLE UserImage ADD userName TEXT");
                db.execSQL("ALTER TABLE AddRequest ADD userName TEXT");
            case 13:
                //2018-06-13新增消息創建時間根據此時間進行排序
                db.execSQL("ALTER TABLE MessageRecord ADD createTime INTEGER");
            case 14:
                //2018-06-14新增消息唯一標示,增加MessageState表，用於保存發送中的消息
                db.execSQL("ALTER TABLE MessageRecord ADD msgId TEXT");
                String messageState = "create table if not exists MessageState"  + "(msgId text)";
                db.execSQL( messageState );
            case 16:
                //2018-06-15增加UserCodeDB用於保存登錄的賬號 ,會話列表新增時間,增加用於顯示聊天時間的
                userCodeDB = "create table if not exists UserCodeDB"  + "(id integer primary key autoincrement,email text,password text)";
                db.execSQL( userCodeDB );
                db.execSQL("ALTER TABLE ConversationRecord ADD createTime INTEGER");
                db.execSQL("ALTER TABLE MessageRecord ADD showChatTime TEXT");
            case 17:
                db.execSQL("ALTER TABLE RoomMember ADD roomId TEXT");
            case 18:
                //2018-06-26新增群主id owner
                db.execSQL("ALTER TABLE RoomManage ADD owner TEXT");
            case 19:
                //2018-06-28新增UserInfo表，用於存儲陌生人信息
                userCodeDB = "create table if not exists UserInfo"  + "(id integer primary key autoincrement,user text,path text,userName text)";
                db.execSQL( userCodeDB );
            case 20:
                db.execSQL("ALTER TABLE RoomManage ADD description TEXT");
            case 21:
                //2018-7-9新增字段isRefresh，用於刪除多餘的房間和成員
                db.execSQL("ALTER TABLE RoomManage ADD isRefresh INTEGER");
                db.execSQL("ALTER TABLE RoomMember ADD isRefresh INTEGER");
            case 22:
                db.execSQL("ALTER TABLE MessageState ADD msgTime INTEGER");
            case 23:
                db.execSQL("ALTER TABLE RoomManage ADD allowModify INTEGER");
            case 24:
                db.execSQL("ALTER TABLE ConversationRecord ADD draft TEXT");
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
