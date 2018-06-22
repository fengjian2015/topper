package com.bclould.tea.history;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.bclould.tea.model.GroupMemberInfo;
import com.bclould.tea.model.RoomManageInfo;
import com.bclould.tea.model.RoomMemberInfo;
import com.bclould.tea.model.UserInfo;
import com.bclould.tea.utils.UtilTool;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by GIjia on 2018/5/24.
 */
@RequiresApi(api = Build.VERSION_CODES.N)
public class DBRoomMember {
    private final Context mContext;
    private DBHelper helper;
    public SQLiteDatabase db;

    public DBRoomMember(Context context) {
        mContext = context;
        helper = new DBHelper(context);
        //因为getWritableDatabase内部调用了mContext.openOrCreateDatabase(mName, 0, mFactory);
        //所以要确保context已初始化,我们可以把实例化DBManager的步骤放在Activity的onCreate里
    }

    public synchronized int addRoomMember(RoomMemberInfo roomMemberInfo) {
        db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", roomMemberInfo.getName());
        values.put("my_user", UtilTool.getTocoId());
        values.put("jid", roomMemberInfo.getJid());
        values.put("image_url", roomMemberInfo.getImage_url());
        values.put("remark", roomMemberInfo.getRemark());
        values.put("roomId",roomMemberInfo.getRoomId());
        int id = (int) db.insert("RoomMember", null, values);
        UtilTool.Log("fengjian", "添加成員到数据库成功" + roomMemberInfo.toString());
        return id;
    }

    public synchronized void addRoomMember(List<GroupMemberInfo.DataBean> groupMemberInfoList,String roomId) {
        db = helper.getWritableDatabase();
        for(GroupMemberInfo.DataBean dataBean:groupMemberInfoList){
            ContentValues values = new ContentValues();
            values.put("name", "");
            values.put("my_user", UtilTool.getTocoId());
            values.put("jid", dataBean.getToco_id());
            values.put("image_url", dataBean.getAvatar());
            values.put("remark", "");
            values.put("roomId",roomId);
            db.insert("RoomMember", null, values);
            UtilTool.Log("fengjian", "添加成員到数据库成功" + dataBean.toString());
        }
    }

    public synchronized void addRoomMemberUserInfo(List<UserInfo> infoList, String roomId) {
        db = helper.getWritableDatabase();
        for(UserInfo userInfo:infoList){
            ContentValues values = new ContentValues();
            values.put("name", "");
            values.put("my_user", UtilTool.getTocoId());
            values.put("jid", userInfo.getUser());
            values.put("image_url", userInfo.getPath());
            values.put("remark", "");
            values.put("roomId",roomId);
            db.insert("RoomMember", null, values);
            UtilTool.Log("fengjian", "添加成員到数据库成功" + userInfo.toString());
        }
    }

    public void deleteAllRoomMember(){
        db = helper.getWritableDatabase();
        db.execSQL("DELETE FROM RoomMember");
    }

    public ArrayList<RoomMemberInfo> queryAllRequest(String roomId) {
        db = helper.getReadableDatabase();
        ArrayList<RoomMemberInfo> addRequestInfos = new ArrayList<>();
        String sql = "select * from RoomMember where my_user=? and roomId = ?";
        Cursor c = db.rawQuery(sql, new String[]{UtilTool.getTocoId(),roomId});
        while (c.moveToNext()) {
            RoomMemberInfo roomMemberInfo=new RoomMemberInfo();
            roomMemberInfo.setName(c.getString(c.getColumnIndex("name")));
            roomMemberInfo.setJid(c.getString(c.getColumnIndex("jid")));
            roomMemberInfo.setImage_url(c.getString(c.getColumnIndex("image_url")));
            roomMemberInfo.setRemark(c.getString(c.getColumnIndex("remark")));
            roomMemberInfo.setMy_user(c.getString(c.getColumnIndex("my_user")));
            roomMemberInfo.setRoomId(c.getString(c.getColumnIndex("roomId")));
            roomMemberInfo.setId(c.getInt(c.getColumnIndex("id")));
            addRequestInfos.add(roomMemberInfo);
        }
        c.close();
        return addRequestInfos;
    }

    public String findMemberUrl(String roomId,String toco_id) {
        db = helper.getReadableDatabase();
        String image_url = null;
        Cursor cursor = db.rawQuery("select image_url from RoomMember where roomId=? and jid=? and my_user=?",
                new String[]{roomId,toco_id, UtilTool.getTocoId()});
        while (cursor.moveToNext()) {
            image_url = cursor.getString(cursor.getColumnIndex("image_url"));
        }
        cursor.close();
        return image_url;
    }

    public boolean findMember(String roomId,String toco_id) {
        Cursor cursor=null;
        try {
            db = helper.getReadableDatabase();
            String image_url = null;
            cursor = db.rawQuery("select * from RoomMember where roomId=? and jid=? and my_user=?",
                    new String[]{roomId,toco_id, UtilTool.getTocoId()});
            boolean result = cursor.moveToNext();
            return result;
        }catch (Exception e){
            e.printStackTrace();
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return false;
    }

    public void deleteRoom(String roomId) {
        db = helper.getWritableDatabase();
        db.delete("RoomMember", "roomId=? and my_user=?", new String[]{roomId, UtilTool.getTocoId()});
    }
}
