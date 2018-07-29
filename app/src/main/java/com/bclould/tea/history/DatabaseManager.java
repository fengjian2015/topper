package com.bclould.tea.history;

import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Message;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by yst on 2017/6/1.
 */

public class DatabaseManager {
    private static DatabaseManager instance;
    private static DBHelper mDatabaseHelper;

    private AtomicInteger mWritableOpenCounter = new AtomicInteger();
    private SQLiteDatabase mWritableDatabase;

    public static  void initializeInstance(DBHelper helper) {
        synchronized (DatabaseManager.class) {
            if (instance == null) {
                instance = new DatabaseManager();
                mDatabaseHelper = helper;
            }
        }
    }

    public static  DatabaseManager getInstance() {
        synchronized (DatabaseManager.class) {
            if (instance == null) {
                throw new IllegalStateException(DatabaseManager.class.getSimpleName() + " is not initialized, call initializeInstance(..) method first.");
            }
            return instance;
        }
    }

    public SQLiteDatabase openWritableDatabase(boolean isWritableOrReadable) {
        synchronized (DatabaseManager.class) {
            mWritableOpenCounter.incrementAndGet();
            try {
                if(isWritableOrReadable) {
                    mWritableDatabase = mDatabaseHelper.getWritableDatabase();
                }else{
                    mWritableDatabase = mDatabaseHelper.getReadableDatabase();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            return mWritableDatabase;
        }
    }



    public void closeWritableDatabase() {
        synchronized (DatabaseManager.class) {
            try {
                if (mWritableOpenCounter.decrementAndGet() == 0) {
                    handler.removeMessages(1);
                    handler.sendEmptyMessageDelayed(1,60*1000);
                }else {
                    handler.removeMessages(1);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    if(mWritableDatabase!=null)
                    mWritableDatabase.close();
                    break;
            }
        }
    };

}
