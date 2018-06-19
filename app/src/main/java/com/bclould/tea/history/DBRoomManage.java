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

    public DBRoomManage(Context context) {
        mContext = context;
        helper = new DBHelper(context);
        //因为getWritableDatabase内部调用了mContext.openOrCreateDatabase(mName, 0, mFactory);
        //所以要确保context已初始化,我们可以把实例化DBManager的步骤放在Activity的onCreate里
    }

    public synchronized int addRoom(RoomManageInfo roomManageInfo) {
        db = helper.getWritableDatabase();
        if(findRoom(roomManageInfo.getRoomId())){
            updateRoom(roomManageInfo.getRoomId(),roomManageInfo.getRoomName());
            return 0;
        }else {
            ContentValues values = new ContentValues();
            values.put("roomImage", roomManageInfo.getRoomImage());
            values.put("my_user", UtilTool.getTocoId());
            values.put("roomId", roomManageInfo.getRoomId());
            values.put("roomName", roomManageInfo.getRoomName());
            values.put("roomNumber", roomManageInfo.getRoomNumber());
            int id = (int) db.insert("RoomManage", null, values);
            UtilTool.Log("fengjian", "添加房间到数据库成功" + roomManageInfo.toString());
            return id;
        }
    }

    public void updateRoom(String roomId, String roomName) {
        db = helper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("roomName", roomName);
        db.update("RoomManage", cv, "roomId=? and my_user=?", new String[]{roomId, UtilTool.getTocoId()});
    }

    public boolean findRoom(String roomJid){
        db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from RoomManage where roomId=? and my_user=?",
                new String[]{roomJid, UtilTool.getTocoId()});
        boolean result = cursor.moveToNext();
        UtilTool.Log("fengjian----","房间id："+roomJid+"   是否创建过："+result);
        cursor.close();
        return result;
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
        db.execSQL("DELETE FROM RoomManage");
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
            addRequestInfos.add(addRequestInfo);
        }
        c.close();
        return addRequestInfos;
    }
}