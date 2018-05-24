package com.bclould.tocotalk.history;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by GIjia on 2018/5/24.
 */

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
}
