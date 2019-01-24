package com.bclould.tea.history;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.bclould.tea.model.PublicDetailsInfo;
import com.bclould.tea.model.PublicInfo;
import com.bclould.tea.utils.UtilTool;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by GIjia on 2018/5/24.
 */

public class DBPublicManage {
    private final Context mContext;
    private static Object lock = new Object();

    public DBPublicManage(Context context) {
        mContext = context;
        DatabaseManager.initializeInstance(new DBHelper(context));
        //因为getWritableDatabase内部调用了mContext.openOrCreateDatabase(mName, 0, mFactory);
        //所以要确保context已初始化,我们可以把实例化DBManager的步骤放在Activity的onCreate里
    }

    public synchronized int addPublic(PublicDetailsInfo.DataBean dataBean) {
        synchronized (lock) {
            SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(true);
            ContentValues values = new ContentValues();
            values.put("my_user", UtilTool.getTocoId());
            values.put("publicId", dataBean.getId()+"");
            values.put("name", dataBean.getName());
            values.put("logo",dataBean.getLogo());
            values.put("publicDesc",dataBean.getDesc());
            values.put("menu",dataBean.getMenu());
            int id = (int) db.insert("PublicDB", null, values);
            UtilTool.Log("數據庫", "插入公眾號");
            DatabaseManager.getInstance().closeWritableDatabase();
            return id;

        }
    }

    public synchronized int addPublic(PublicInfo.DataBean dataBean) {
        synchronized (lock) {
            SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(true);
            if (findPublic(dataBean.getId()+"")) {
                updatePublic(dataBean);
                DatabaseManager.getInstance().closeWritableDatabase();
                return 0;
            } else {
                ContentValues values = new ContentValues();
                values.put("my_user", UtilTool.getTocoId());
                values.put("publicId", dataBean.getId()+"");
                values.put("name", dataBean.getName());
                values.put("logo",dataBean.getLogo());
                values.put("publicDesc",dataBean.getDesc());
                values.put("isRefresh",1);
                values.put("menu",dataBean.getMenu());
                int id = (int) db.insert("PublicDB", null, values);
                UtilTool.Log("數據庫", "插入公眾號");
                DatabaseManager.getInstance().closeWritableDatabase();
                return id;
            }
        }
    }


    private void updatePublic(PublicInfo.DataBean dataBean) {
        synchronized (lock) {
            SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(true);
            ContentValues cv = new ContentValues();
            cv.put("name", dataBean.getName());
            cv.put("logo",dataBean.getLogo());
            cv.put("publicDesc",dataBean.getDesc());
            cv.put("isRefresh",1);
            cv.put("menu",dataBean.getMenu());
            db.update("PublicDB", cv, "publicId=? and my_user=?", new String[]{dataBean.getId()+"", UtilTool.getTocoId()});
            DatabaseManager.getInstance().closeWritableDatabase();
        }
    }

    public boolean findPublic(String publicId){
        synchronized (lock) {
            SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(false);
            Cursor cursor = db.rawQuery("select * from PublicDB where publicId=? and my_user=?",
                    new String[]{publicId, UtilTool.getTocoId()});
            boolean result = cursor.moveToNext();
            cursor.close();
            DatabaseManager.getInstance().closeWritableDatabase();
            return result;
        }
    }

    public String findPublicLogo(String publicId) {
        synchronized (lock) {
            SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(false);
            String logo = null;
            Cursor cursor = db.rawQuery("select logo from PublicDB where publicId=? and my_user=?",
                    new String[]{publicId, UtilTool.getTocoId()});
            while (cursor.moveToNext()) {
                logo = cursor.getString(cursor.getColumnIndex("logo"));
            }
            cursor.close();
            DatabaseManager.getInstance().closeWritableDatabase();
            return logo;
        }
    }

    public String findPublicName(String publicId) {
        synchronized (lock) {
            SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(false);
            String name = null;
            Cursor cursor = db.rawQuery("select name from PublicDB where publicId=? and my_user=?",
                    new String[]{publicId, UtilTool.getTocoId()});
            while (cursor.moveToNext()) {
                name = cursor.getString(cursor.getColumnIndex("name"));
            }
            cursor.close();
            DatabaseManager.getInstance().closeWritableDatabase();
            return name;
        }
    }

    public String findPublicMenu(String publicId) {
        synchronized (lock) {
            SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(false);
            String menu = null;
            Cursor cursor = db.rawQuery("select menu from PublicDB where publicId=? and my_user=?",
                    new String[]{publicId, UtilTool.getTocoId()});
            while (cursor.moveToNext()) {
                menu = cursor.getString(cursor.getColumnIndex("menu"));
            }
            cursor.close();
            DatabaseManager.getInstance().closeWritableDatabase();
            return menu;
        }
    }


    public void deleteOldPublic() {
        synchronized (lock) {
            SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(true);
            ArrayList<PublicInfo.DataBean> roomManageInfos=queryAllOldRoom();
            for(PublicInfo.DataBean  dataBean:roomManageInfos){
                db.delete("PublicDB", "publicId=? and my_user=?", new String[]{dataBean.getId()+"", UtilTool.getTocoId()});
            }
            DatabaseManager.getInstance().closeWritableDatabase();
        }
    }

    public void deletePublic(String id) {
        synchronized (lock) {
            SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(true);
            db.delete("PublicDB", "publicId=? and my_user=?", new String[]{id, UtilTool.getTocoId()});
            DatabaseManager.getInstance().closeWritableDatabase();
        }
    }

    public ArrayList<PublicInfo.DataBean> queryAllOldRoom() {
        synchronized (lock) {
            SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(false);
            ArrayList<PublicInfo.DataBean> dataBeans = new ArrayList<>();
            String sql = "select * from PublicDB where my_user=? and isRefresh=0";
            Cursor c = db.rawQuery(sql, new String[]{UtilTool.getTocoId()});
            while (c.moveToNext()) {
                PublicInfo.DataBean addRequestInfo = new PublicInfo.DataBean();
                addRequestInfo.setName(c.getString(c.getColumnIndex("name")));
                addRequestInfo.setDesc(c.getString(c.getColumnIndex("publicDesc")));
                addRequestInfo.setId(c.getInt(c.getColumnIndex("publicId")));
                addRequestInfo.setLogo(c.getString(c.getColumnIndex("logo")));
                addRequestInfo.setMenu(c.getString(c.getColumnIndex("menu")));
                dataBeans.add(addRequestInfo);
            }
            c.close();
            DatabaseManager.getInstance().closeWritableDatabase();
            return dataBeans;
        }
    }


    public ArrayList<PublicInfo.DataBean> queryAllRequest() {
        synchronized (lock) {
            SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(false);
            ArrayList<PublicInfo.DataBean> dataBeans = new ArrayList<>();
            String sql = "select * from PublicDB where my_user=?";
            Cursor c = db.rawQuery(sql, new String[]{UtilTool.getTocoId()});
            while (c.moveToNext()) {
                PublicInfo.DataBean addRequestInfo = new PublicInfo.DataBean();
                addRequestInfo.setName(c.getString(c.getColumnIndex("name")));
                addRequestInfo.setDesc(c.getString(c.getColumnIndex("publicDesc")));
                addRequestInfo.setId(c.getInt(c.getColumnIndex("publicId")));
                addRequestInfo.setLogo(c.getString(c.getColumnIndex("logo")));
                addRequestInfo.setMenu(c.getString(c.getColumnIndex("menu")));
                dataBeans.add(addRequestInfo);
            }
            c.close();
            DatabaseManager.getInstance().closeWritableDatabase();
            return dataBeans;
        }
    }

    public void updateIsRefresh(List<PublicInfo.DataBean> dataBeans) {
        synchronized (lock) {
            SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(true);
            for(PublicInfo.DataBean dataBean:dataBeans){
                ContentValues cv = new ContentValues();
                cv.put("isRefresh", 0);
                db.update("PublicDB", cv, "publicId=? and my_user=?", new String[]{dataBean.getId()+"", UtilTool.getTocoId()});
            }
            DatabaseManager.getInstance().closeWritableDatabase();
        }
    }

}
