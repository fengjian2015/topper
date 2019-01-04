package com.bclould.tea.history;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.bclould.tea.model.GroupInfo;
import com.bclould.tea.model.RoomManageInfo;
import com.bclould.tea.utils.UtilTool;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by GIjia on 2018/5/24.
 */

public class DBRoomManage {
    private final Context mContext;
    private static Object lock = new Object();

    public DBRoomManage(Context context) {
        mContext = context;
        DatabaseManager.initializeInstance(new DBHelper(context));
        //因为getWritableDatabase内部调用了mContext.openOrCreateDatabase(mName, 0, mFactory);
        //所以要确保context已初始化,我们可以把实例化DBManager的步骤放在Activity的onCreate里
    }

    public synchronized int addRoom(RoomManageInfo roomManageInfo) {
        synchronized (lock) {
            SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(true);
            if (findRoom(roomManageInfo.getRoomId())) {
                updateRoom(roomManageInfo);
                DatabaseManager.getInstance().closeWritableDatabase();
                return 0;
            } else {
                ContentValues values = new ContentValues();
                values.put("roomImage", roomManageInfo.getRoomImage());
                values.put("my_user", UtilTool.getTocoId());
                values.put("roomId", roomManageInfo.getRoomId());
                values.put("roomName", roomManageInfo.getRoomName());
                values.put("roomNumber", roomManageInfo.getRoomNumber());
                values.put("owner", roomManageInfo.getOwner());
                values.put("description", roomManageInfo.getDescription());
                values.put("isRefresh",roomManageInfo.getIsRefresh());
                values.put("allowModify",roomManageInfo.getAllowModify());
                values.put("isReview",roomManageInfo.getIsReview());
                int id = (int) db.insert("RoomManage", null, values);
                UtilTool.Log("數據庫", "插入房間");
                DatabaseManager.getInstance().closeWritableDatabase();
                return id;
            }
        }
    }

    private void updateRoom(RoomManageInfo roomManageInfo) {
        synchronized (lock) {
            SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(true);
            ContentValues cv = new ContentValues();
            cv.put("roomImage", roomManageInfo.getRoomImage());
            cv.put("roomName", roomManageInfo.getRoomName());
            cv.put("roomNumber", roomManageInfo.getRoomNumber());
            cv.put("owner", roomManageInfo.getOwner());
            cv.put("description",roomManageInfo.getDescription());
            cv.put("isRefresh",roomManageInfo.getIsRefresh());
            cv.put("allowModify",roomManageInfo.getAllowModify());
            cv.put("isReview",roomManageInfo.getIsReview());
            db.update("RoomManage", cv, "roomId=? and my_user=?", new String[]{roomManageInfo.getRoomId(), UtilTool.getTocoId()});
            DatabaseManager.getInstance().closeWritableDatabase();
        }
    }


    public void updateIsReview(String roomId, int isReview) {
        synchronized (lock) {
            SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(true);
            ContentValues cv = new ContentValues();
            cv.put("isReview", isReview);
            db.update("RoomManage", cv, "roomId=? and my_user=?", new String[]{roomId, UtilTool.getTocoId()});
            DatabaseManager.getInstance().closeWritableDatabase();
        }
    }

    public void updateRoom(String roomId, String roomName) {
        synchronized (lock) {
            SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(true);
            ContentValues cv = new ContentValues();
            cv.put("roomName", roomName);
            db.update("RoomManage", cv, "roomId=? and my_user=?", new String[]{roomId, UtilTool.getTocoId()});
            DatabaseManager.getInstance().closeWritableDatabase();
        }
    }


    public void updateIsRefresh(List<GroupInfo.DataBean> dataBeans) {
        synchronized (lock) {
            SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(true);
            for(GroupInfo.DataBean dataBean:dataBeans){
                ContentValues cv = new ContentValues();
                cv.put("isRefresh", 0);
                db.update("RoomManage", cv, "roomId=? and my_user=?", new String[]{dataBean.getId()+"", UtilTool.getTocoId()});
            }
            DatabaseManager.getInstance().closeWritableDatabase();
        }
    }

    public void updateOwner(String roomId, String owner) {
        synchronized (lock) {
            SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(true);
            ContentValues cv = new ContentValues();
            cv.put("owner", owner);
            db.update("RoomManage", cv, "roomId=? and my_user=?", new String[]{roomId, UtilTool.getTocoId()});
            DatabaseManager.getInstance().closeWritableDatabase();
        }
    }

    public void updateUrl(String roomId, String roomImage) {
        synchronized (lock) {
            SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(true);
            ContentValues cv = new ContentValues();
            cv.put("roomImage", roomImage);
            db.update("RoomManage", cv, "roomId=? and my_user=?", new String[]{roomId, UtilTool.getTocoId()});
            DatabaseManager.getInstance().closeWritableDatabase();
        }
    }

    public void updateDescription(String roomId, String description) {
        synchronized (lock) {
            SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(true);
            ContentValues cv = new ContentValues();
            cv.put("description", description);
            db.update("RoomManage", cv, "roomId=? and my_user=?", new String[]{roomId, UtilTool.getTocoId()});
            DatabaseManager.getInstance().closeWritableDatabase();
        }
    }

    public void updateAllowModify(String roomId, int allowModify) {
        synchronized (lock) {
            SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(true);
            ContentValues cv = new ContentValues();
            cv.put("allowModify", allowModify);
            db.update("RoomManage", cv, "roomId=? and my_user=?", new String[]{roomId, UtilTool.getTocoId()});
            DatabaseManager.getInstance().closeWritableDatabase();
        }
    }

    public boolean findRoom(String roomJid){
        synchronized (lock) {
            SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(false);
            Cursor cursor = db.rawQuery("select * from RoomManage where roomId=? and my_user=?",
                    new String[]{roomJid, UtilTool.getTocoId()});
            boolean result = cursor.moveToNext();
            cursor.close();
            DatabaseManager.getInstance().closeWritableDatabase();
            return result;
        }
    }


    public String findRoomOwner(String roomJid){
        synchronized (lock) {
            SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(false);
            String owner = null;
            Cursor cursor = db.rawQuery("select owner from RoomManage where roomId=? and my_user=?",
                    new String[]{roomJid, UtilTool.getTocoId()});
            while (cursor.moveToNext()) {
                owner = cursor.getString(cursor.getColumnIndex("owner"));
            }
            cursor.close();
            DatabaseManager.getInstance().closeWritableDatabase();
            return owner;
        }
    }

    public String findRoomDescription(String roomJid){
        synchronized (lock) {
            SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(false);
            String description = null;
            Cursor cursor = db.rawQuery("select description from RoomManage where roomId=? and my_user=?",
                    new String[]{roomJid, UtilTool.getTocoId()});
            while (cursor.moveToNext()) {
                description = cursor.getString(cursor.getColumnIndex("description"));
            }
            cursor.close();
            DatabaseManager.getInstance().closeWritableDatabase();
            return description;
        }
    }

    public int findRoomisReview(String roomJid){
        synchronized (lock) {
            SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(false);
            int isReview = 0;
            Cursor cursor = db.rawQuery("select isReview from RoomManage where roomId=? and my_user=?",
                    new String[]{roomJid, UtilTool.getTocoId()});
            while (cursor.moveToNext()) {
                isReview = cursor.getInt(cursor.getColumnIndex("isReview"));
            }
            cursor.close();
            DatabaseManager.getInstance().closeWritableDatabase();
            return isReview;
        }
    }

    public int findRoomAllowModify(String roomJid){
        synchronized (lock) {
            SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(false);
            int allowModify = 0;
            Cursor cursor = db.rawQuery("select allowModify from RoomManage where roomId=? and my_user=?",
                    new String[]{roomJid, UtilTool.getTocoId()});
            while (cursor.moveToNext()) {
                allowModify = cursor.getInt(cursor.getColumnIndex("allowModify"));
            }
            cursor.close();
            DatabaseManager.getInstance().closeWritableDatabase();
            return allowModify;
        }
    }


    public int findRoomNumber(String roomJid){
        synchronized (lock) {
            SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(false);
            int roomNumber = 200;
            Cursor cursor = db.rawQuery("select roomNumber from RoomManage where roomId=? and my_user=?",
                    new String[]{roomJid, UtilTool.getTocoId()});
            while (cursor.moveToNext()) {
                roomNumber = cursor.getInt(cursor.getColumnIndex("roomNumber"));
            }
            cursor.close();
            DatabaseManager.getInstance().closeWritableDatabase();
            return roomNumber;
        }
    }

    public String findRoomUrl(String roomJid){
        synchronized (lock) {
            SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(false);
            String roomImage = null;
            Cursor cursor = db.rawQuery("select roomImage from RoomManage where roomId=? and my_user=?",
                    new String[]{roomJid, UtilTool.getTocoId()});
            while (cursor.moveToNext()) {
                roomImage = cursor.getString(cursor.getColumnIndex("roomImage"));
            }
            cursor.close();
            DatabaseManager.getInstance().closeWritableDatabase();
            return roomImage;
        }
    }


    public String findRoomName(String roomJid) {
        synchronized (lock) {
            SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(false);
            String roomName = null;
            Cursor cursor = db.rawQuery("select roomName from RoomManage where roomId=? and my_user=?",
                    new String[]{roomJid, UtilTool.getTocoId()});
            while (cursor.moveToNext()) {
                roomName = cursor.getString(cursor.getColumnIndex("roomName"));
            }
            cursor.close();
            DatabaseManager.getInstance().closeWritableDatabase();
            return roomName;
        }
    }

    public void deleteAllRoom(){
        synchronized (lock) {
            SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(true);
            db.execSQL("DELETE FROM RoomManage");
            DatabaseManager.getInstance().closeWritableDatabase();
        }
    }

    public void deleteRoom(String roomId) {
        synchronized (lock) {
            SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(true);
            db.delete("RoomManage", "roomId=? and my_user=?", new String[]{roomId, UtilTool.getTocoId()});
            DatabaseManager.getInstance().closeWritableDatabase();
        }
    }

    public void deleteOldRoom() {
        synchronized (lock) {
            SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(true);
            ArrayList<RoomManageInfo> roomManageInfos=queryAllOldRoom();
            for(RoomManageInfo roomManageInfo:roomManageInfos){
                db.delete("RoomManage", "roomId=? and my_user=?", new String[]{roomManageInfo.getRoomId(), UtilTool.getTocoId()});
            }
            DatabaseManager.getInstance().closeWritableDatabase();
        }
    }

    public ArrayList<RoomManageInfo> queryAllOldRoom() {
        synchronized (lock) {
            SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(false);
            ArrayList<RoomManageInfo> addRequestInfos = new ArrayList<>();
            String sql = "select * from RoomManage where my_user=? and isRefresh=0";
            Cursor c = db.rawQuery(sql, new String[]{UtilTool.getTocoId()});
            while (c.moveToNext()) {
                RoomManageInfo addRequestInfo = new RoomManageInfo();
                addRequestInfo.setRoomName(c.getString(c.getColumnIndex("roomName")));
                addRequestInfo.setRoomId(c.getString(c.getColumnIndex("roomId")));
                addRequestInfo.setRoomImage(c.getString(c.getColumnIndex("roomImage")));
                addRequestInfo.setRoomNumber(c.getInt(c.getColumnIndex("roomNumber")));
                addRequestInfo.setMy_user(c.getString(c.getColumnIndex("my_user")));
                addRequestInfo.setOwner(c.getString(c.getColumnIndex("owner")));
                addRequestInfo.setDescription(c.getString(c.getColumnIndex("description")));
                addRequestInfo.setAllowModify(c.getInt(c.getColumnIndex("allowModify")));
                addRequestInfos.add(addRequestInfo);
            }
            c.close();
            DatabaseManager.getInstance().closeWritableDatabase();
            return addRequestInfos;
        }
    }


    public ArrayList<RoomManageInfo> queryAllRequest() {
        synchronized (lock) {
            SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(false);
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
                addRequestInfo.setDescription(c.getString(c.getColumnIndex("description")));
                addRequestInfo.setAllowModify(c.getInt(c.getColumnIndex("allowModify")));
                addRequestInfos.add(addRequestInfo);
            }
            c.close();
            DatabaseManager.getInstance().closeWritableDatabase();
            return addRequestInfos;
        }
    }
}
