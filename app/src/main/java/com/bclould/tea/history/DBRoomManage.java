package com.bclould.tea.history;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.bclould.tea.model.RoomManageInfo;
import com.bclould.tea.utils.UtilTool;

import java.util.ArrayList;

/**
 * Created by GIjia on 2018/5/24.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class DBRoomManage {
    private final Context mContext;
    private DBHelper helper;
    public SQLiteDatabase db;
    private static Object lock = new Object();

    public DBRoomManage(Context context) {
        mContext = context;
        helper = new DBHelper(context);
        //因为getWritableDatabase内部调用了mContext.openOrCreateDatabase(mName, 0, mFactory);
        //所以要确保context已初始化,我们可以把实例化DBManager的步骤放在Activity的onCreate里
    }

    public synchronized int addRoom(RoomManageInfo roomManageInfo) {
            db = helper.getWritableDatabase();
            if (findRoom(roomManageInfo.getRoomId())) {
                updateRoom(roomManageInfo);
                return 0;
            } else {
                ContentValues values = new ContentValues();
                values.put("roomImage", roomManageInfo.getRoomImage());
                values.put("my_user", UtilTool.getTocoId());
                values.put("roomId", roomManageInfo.getRoomId());
                values.put("roomName", roomManageInfo.getRoomName());
                values.put("roomNumber", roomManageInfo.getRoomNumber());
                values.put("owner", roomManageInfo.getOwner());
                int id = (int) db.insert("RoomManage", null, values);
                UtilTool.Log("數據庫","插入房間");
                return id;
            }
    }

    private void updateRoom(RoomManageInfo roomManageInfo) {
            db = helper.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put("roomImage", roomManageInfo.getRoomImage());
            cv.put("roomName", roomManageInfo.getRoomName());
            cv.put("roomNumber", roomManageInfo.getRoomNumber());
            cv.put("owner", roomManageInfo.getOwner());
            db.update("RoomManage", cv, "roomId=? and my_user=?", new String[]{roomManageInfo.getRoomId(), UtilTool.getTocoId()});
    }

    public void updateRoom(String roomId, String roomName) {
            db = helper.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put("roomName", roomName);
            db.update("RoomManage", cv, "roomId=? and my_user=?", new String[]{roomId, UtilTool.getTocoId()});
    }

    public void updateOwner(String roomId, String owner) {
            db = helper.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put("owner", owner);
            db.update("RoomManage", cv, "roomId=? and my_user=?", new String[]{roomId, UtilTool.getTocoId()});
    }

    public void updateUrl(String roomId, String roomImage) {
            db = helper.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put("roomImage", roomImage);
            db.update("RoomManage", cv, "roomId=? and my_user=?", new String[]{roomId, UtilTool.getTocoId()});
    }

    public boolean findRoom(String roomJid){
            db = helper.getReadableDatabase();
            Cursor cursor = db.rawQuery("select * from RoomManage where roomId=? and my_user=?",
                    new String[]{roomJid, UtilTool.getTocoId()});
            boolean result = cursor.moveToNext();
            cursor.close();
            return result;
    }

    public String findRoomOwner(String roomJid){
            db = helper.getReadableDatabase();
            String owner = null;
            Cursor cursor = db.rawQuery("select owner from RoomManage where roomId=? and my_user=?",
                    new String[]{roomJid, UtilTool.getTocoId()});
            while (cursor.moveToNext()) {
                owner = cursor.getString(cursor.getColumnIndex("owner"));
            }
            cursor.close();
            return owner;
    }


    public int findRoomNumber(String roomJid){
            db = helper.getReadableDatabase();
            int roomNumber = 200;
            Cursor cursor = db.rawQuery("select roomNumber from RoomManage where roomId=? and my_user=?",
                    new String[]{roomJid, UtilTool.getTocoId()});
            while (cursor.moveToNext()) {
                roomNumber = cursor.getInt(cursor.getColumnIndex("roomNumber"));
            }
            cursor.close();
            return roomNumber;
    }

    public String findRoomUrl(String roomJid){
            db = helper.getReadableDatabase();
            String roomImage = null;
            Cursor cursor = db.rawQuery("select roomImage from RoomManage where roomId=? and my_user=?",
                    new String[]{roomJid, UtilTool.getTocoId()});
            while (cursor.moveToNext()) {
                roomImage = cursor.getString(cursor.getColumnIndex("roomImage"));
            }
            cursor.close();
            return roomImage;
    }


    public String findRoomName(String roomJid) {
            db = helper.getReadableDatabase();
            String roomName = null;
            Cursor cursor = db.rawQuery("select roomName from RoomManage where roomId=? and my_user=?",
                    new String[]{roomJid, UtilTool.getTocoId()});
            while (cursor.moveToNext()) {
                roomName = cursor.getString(cursor.getColumnIndex("roomName"));
            }
            cursor.close();
            return roomName;
    }

    public void deleteAllRoom(){
            db = helper.getWritableDatabase();
            db.execSQL("DELETE FROM RoomManage");
    }

    public void deleteRoom(String roomId) {
            db = helper.getWritableDatabase();
            db.delete("RoomManage", "roomId=? and my_user=?", new String[]{roomId, UtilTool.getTocoId()});
    }

    public ArrayList<RoomManageInfo> queryAllRequest() {
            db = helper.getReadableDatabase();
            ArrayList<RoomManageInfo> addRequestInfos = new ArrayList<>();
            String sql = "select * from RoomManage where my_user=?";
            Cursor c = db.rawQuery(sql, new String[]{UtilTool.getTocoId()});
            while (c.moveToNext()) {
                RoomManageInfo addRequestInfo = new RoomManageInfo();
                addRequestInfo.setRoomName(c.getString(c.getColumnIndex("roomName")));
                addRequestInfo.setRoomId(c.getString(c.getColumnIndex("roomId")));
                addRequestInfo.setRoomImage(c.getString(c.getColumnIndex("roomImage")));
                addRequestInfo.setRoomNumber(c.getInt(c.getColumnIndex("roomNumber")));
                addRequestInfo.setMy_user(c.getString(c.getColumnIndex("my_user")));
                addRequestInfo.setOwner(c.getString(c.getColumnIndex("owner")));
                addRequestInfos.add(addRequestInfo);
            }
            c.close();
            return addRequestInfos;
    }
}
