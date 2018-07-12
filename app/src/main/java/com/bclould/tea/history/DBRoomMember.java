package com.bclould.tea.history;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.bclould.tea.model.GroupInfo;
import com.bclould.tea.model.GroupMemberInfo;
import com.bclould.tea.model.RoomManageInfo;
import com.bclould.tea.model.RoomMemberInfo;
import com.bclould.tea.model.UserInfo;
import com.bclould.tea.utils.UtilTool;
import com.umeng.commonsdk.debug.E;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by GIjia on 2018/5/24.
 */
@RequiresApi(api = Build.VERSION_CODES.N)
public class DBRoomMember {
    private final Context mContext;
    private static Object lock = new Object();

    public DBRoomMember(Context context) {
        mContext = context;
        DatabaseManager.initializeInstance(new DBHelper(context));
        //因为getWritableDatabase内部调用了mContext.openOrCreateDatabase(mName, 0, mFactory);
        //所以要确保context已初始化,我们可以把实例化DBManager的步骤放在Activity的onCreate里
    }

    public synchronized void addRoomMember(RoomMemberInfo roomMemberInfo) {
        synchronized (lock) {
            if(findMember(roomMemberInfo.getRoomId(),roomMemberInfo.getJid())){
                updateRoom(roomMemberInfo);
            }else {
                SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(true);
                ContentValues values = new ContentValues();
                values.put("name", roomMemberInfo.getName());
                values.put("my_user", UtilTool.getTocoId());
                values.put("jid", roomMemberInfo.getJid());
                values.put("image_url", roomMemberInfo.getImage_url());
                values.put("remark", roomMemberInfo.getRemark());
                values.put("roomId", roomMemberInfo.getRoomId());
                values.put("isRefresh",roomMemberInfo.getIsRefresh());
                int id = (int) db.insert("RoomMember", null, values);
                DatabaseManager.getInstance().closeWritableDatabase();
            }
        }
    }

    public synchronized void addRoomMember(List<GroupMemberInfo.DataBean.UsersBean> groupMemberInfoList,String roomId) {
        synchronized (lock) {
            SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(true);
            for (GroupMemberInfo.DataBean.UsersBean dataBean : groupMemberInfoList) {
                if(findMember(roomId,dataBean.getToco_id())){
                    updateRoom(dataBean,roomId);
                }else {
                    ContentValues values = new ContentValues();
                    values.put("name", dataBean.getName());
                    values.put("my_user", UtilTool.getTocoId());
                    values.put("jid", dataBean.getToco_id());
                    values.put("image_url", dataBean.getAvatar());
                    values.put("roomId", roomId);
                    values.put("isRefresh",1);
                    db.insert("RoomMember", null, values);
                }
            }
            DatabaseManager.getInstance().closeWritableDatabase();
        }
    }



    public synchronized void addRoomMemberUserInfo(List<UserInfo> infoList, String roomId) {
        synchronized (lock) {
            SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(true);
            for (UserInfo userInfo : infoList) {
                if(findMember(roomId,userInfo.getUser())){
                    updateRoom(userInfo,roomId);
                }else {
                    ContentValues values = new ContentValues();
                    values.put("name", userInfo.getUserName());
                    values.put("my_user", UtilTool.getTocoId());
                    values.put("jid", userInfo.getUser());
                    values.put("image_url", userInfo.getPath());
                    values.put("remark", "");
                    values.put("roomId", roomId);
                    db.insert("RoomMember", null, values);
                }
            }
            DatabaseManager.getInstance().closeWritableDatabase();
        }
    }

    public void deleteAllRoomMember(){
        synchronized (lock) {
            SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(true);
            db.execSQL("DELETE FROM RoomMember");
            DatabaseManager.getInstance().closeWritableDatabase();
        }
    }

    public ArrayList<RoomMemberInfo> queryAllRequest(String roomId) {
        synchronized (lock) {
            SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(false);
            ArrayList<RoomMemberInfo> addRequestInfos = new ArrayList<>();
            try {
                String sql = "select * from RoomMember where my_user=? and roomId = ?";
                Cursor c = db.rawQuery(sql, new String[]{UtilTool.getTocoId(), roomId});
                while (c.moveToNext()) {
                    RoomMemberInfo roomMemberInfo = new RoomMemberInfo();
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
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                DatabaseManager.getInstance().closeWritableDatabase();
            }
            return addRequestInfos;
        }
    }

    public ArrayList<RoomMemberInfo> queryRequest(String roomId) {
        synchronized (lock) {
            SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(false);
            ArrayList<RoomMemberInfo> addRequestInfos = new ArrayList<>();
            String sql = "select * from RoomMember where my_user=? and roomId = ?";
            Cursor c = db.rawQuery(sql, new String[]{UtilTool.getTocoId(), roomId});
            while (c.moveToNext()) {
                RoomMemberInfo roomMemberInfo = new RoomMemberInfo();
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
            DatabaseManager.getInstance().closeWritableDatabase();
            return addRequestInfos;
        }
    }

    public String findMemberName(String roomId,String toco_id) {
        synchronized (lock) {
            SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(false);
            String name = null;
            Cursor cursor = db.rawQuery("select name from RoomMember where roomId=? and jid=? and my_user=?",
                    new String[]{roomId, toco_id, UtilTool.getTocoId()});
            while (cursor.moveToNext()) {
                name = cursor.getString(cursor.getColumnIndex("name"));
            }
            cursor.close();
            DatabaseManager.getInstance().closeWritableDatabase();
            return name;
        }
    }

    public String findMemberUrl(String roomId,String toco_id) {
        synchronized (lock) {
            SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(false);
            String image_url = null;
            Cursor cursor = db.rawQuery("select image_url from RoomMember where roomId=? and jid=? and my_user=?",
                    new String[]{roomId, toco_id, UtilTool.getTocoId()});
            while (cursor.moveToNext()) {
                image_url = cursor.getString(cursor.getColumnIndex("image_url"));
            }
            cursor.close();
            DatabaseManager.getInstance().closeWritableDatabase();
            return image_url;
        }
    }


    public String findMemberUrl(String toco_id) {
        synchronized (lock) {
            SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(false);
            String image_url = null;
            Cursor cursor = db.rawQuery("select image_url from RoomMember where jid=? and my_user=?",
                    new String[]{toco_id, UtilTool.getTocoId()});
            while (cursor.moveToNext()) {
                image_url = cursor.getString(cursor.getColumnIndex("image_url"));
            }
            cursor.close();
            DatabaseManager.getInstance().closeWritableDatabase();
            return image_url;
        }
    }

    public boolean findMember(String roomId,String toco_id) {
        synchronized (lock) {
            SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(false);
            Cursor cursor = null;
            try {
                cursor = db.rawQuery("select * from RoomMember where roomId=? and jid=? and my_user=?",
                        new String[]{roomId, toco_id, UtilTool.getTocoId()});
                boolean result = cursor.moveToNext();
                return result;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                DatabaseManager.getInstance().closeWritableDatabase();
                if (cursor != null)
                    cursor.close();
            }
            return false;
        }
    }


    public void updateIsRefresh1(List<GroupMemberInfo.DataBean.UsersBean> groupMemberInfoList,String roomId) {
        synchronized (lock) {
            SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(true);
            for(GroupMemberInfo.DataBean.UsersBean dataBean:groupMemberInfoList){
                ContentValues cv = new ContentValues();
                cv.put("isRefresh", 0);
                db.update("RoomMember", cv, "roomId=? and jid=? and my_user=?", new String[]{roomId,dataBean.getToco_id(), UtilTool.getTocoId()});
            }
            DatabaseManager.getInstance().closeWritableDatabase();
        }
    }

    public void updateIsRefresh(List<GroupInfo.DataBean.UsersBean> dataBeans,String roomId) {
        synchronized (lock) {
            SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(true);
            for(GroupInfo.DataBean.UsersBean dataBean:dataBeans){
                ContentValues cv = new ContentValues();
                cv.put("isRefresh", 0);
                db.update("RoomMember", cv, "roomId=? and jid=? and my_user=?", new String[]{roomId,dataBean.getToco_id(), UtilTool.getTocoId()});
            }
            DatabaseManager.getInstance().closeWritableDatabase();
        }
    }

    public void updateRoom(RoomMemberInfo roomMemberInfo) {
        synchronized (lock) {
            SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(true);
            ContentValues values = new ContentValues();
            values.put("name", roomMemberInfo.getName());
            values.put("my_user", UtilTool.getTocoId());
            values.put("jid", roomMemberInfo.getJid());
            values.put("image_url", roomMemberInfo.getImage_url());
            values.put("remark", roomMemberInfo.getRemark());
            values.put("isRefresh",roomMemberInfo.getIsRefresh());
            db.update("RoomMember", values, "roomId=? and my_user=? and jid=?", new String[]{roomMemberInfo.getRoomId(), UtilTool.getTocoId(), roomMemberInfo.getJid()});
            DatabaseManager.getInstance().closeWritableDatabase();
        }
    }

    private void updateRoom(GroupMemberInfo.DataBean.UsersBean dataBean,String roomid) {
        synchronized (lock) {
            SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(true);
            ContentValues values = new ContentValues();
            values.put("name", dataBean.getName());
            values.put("my_user", UtilTool.getTocoId());
            values.put("jid", dataBean.getToco_id());
            values.put("image_url", dataBean.getAvatar());
            values.put("isRefresh",1);
            db.update("RoomMember", values, "roomId=? and my_user=? and jid=?", new String[]{roomid, UtilTool.getTocoId(),dataBean.getToco_id()});
            DatabaseManager.getInstance().closeWritableDatabase();
        }
    }

    private void updateRoom(UserInfo userInfo,String roomid) {
        synchronized (lock) {
            SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(true);
            ContentValues values = new ContentValues();
            values.put("name", userInfo.getUserName());
            values.put("my_user", UtilTool.getTocoId());
            values.put("jid", userInfo.getUser());
            values.put("image_url", userInfo.getPath());
            values.put("remark", "");
            db.update("RoomMember", values, "roomId=? and my_user=? and jid=?", new String[]{roomid, UtilTool.getTocoId(),userInfo.getUser()});
            DatabaseManager.getInstance().closeWritableDatabase();
        }
    }


    public void updateRoom(String roomId,String jid, String name) {
        synchronized (lock) {
            SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(true);
            ContentValues cv = new ContentValues();
            cv.put("name", name);
            db.update("RoomMember", cv, "roomId=? and jid=? and my_user=?", new String[]{roomId, jid, UtilTool.getTocoId()});
            DatabaseManager.getInstance().closeWritableDatabase();
        }
    }


    public ArrayList<RoomMemberInfo> queryAllOldRequest(String roomId) {
        synchronized (lock) {
            SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(false);
            ArrayList<RoomMemberInfo> addRequestInfos = new ArrayList<>();
            try {
                String sql = "select * from RoomMember where my_user=? and roomId = ? and isRefresh=0";
                Cursor c = db.rawQuery(sql, new String[]{UtilTool.getTocoId(), roomId});
                while (c.moveToNext()) {
                    RoomMemberInfo roomMemberInfo = new RoomMemberInfo();
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
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                DatabaseManager.getInstance().closeWritableDatabase();
            }
            return addRequestInfos;
        }
    }

    public void deleteOldRoomMember(ArrayList<RoomMemberInfo> roomManageInfos,String roomId) {
        synchronized (lock) {
            SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(true);
            for(RoomMemberInfo roomManageInfo:roomManageInfos){
                db.delete("RoomMember", "roomId=? and my_user=? and jid=?", new String[]{roomId, UtilTool.getTocoId(),roomManageInfo.getJid()});
            }
            DatabaseManager.getInstance().closeWritableDatabase();
        }
    }

    public void deleteRoom(String roomId) {
        synchronized (lock) {
            SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(true);
            db.delete("RoomMember", "roomId=? and my_user=?", new String[]{roomId, UtilTool.getTocoId()});
            DatabaseManager.getInstance().closeWritableDatabase();
        }
    }

    public void deleteRoomMember(String roomId,String tocoId) {
        synchronized (lock) {
            SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(true);
            db.delete("RoomMember", "roomId=? and my_user=? and jid=?", new String[]{roomId, UtilTool.getTocoId(), tocoId});
            DatabaseManager.getInstance().closeWritableDatabase();
        }
    }
}
