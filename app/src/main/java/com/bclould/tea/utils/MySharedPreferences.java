package com.bclould.tea.utils;

import android.content.Context;
import android.content.SharedPreferences;


/**
 * Created by GA on 2017/9/28.
 */

public class MySharedPreferences {

    public static final String SETTING = "setting";
    public static MySharedPreferences instance = null;
    private Context mContext;

    public static MySharedPreferences getInstance() {

        if (instance == null) {

            instance = new MySharedPreferences();

        }

        return instance;
    }

    public SharedPreferences getSp(){
        return mContext.getSharedPreferences(SETTING, 0);
    }

    public void init(Context context) {

        mContext = context;

    }

    //存储布尔值sp
    public void setBoolean(String value, boolean key) {

        SharedPreferences sp = mContext.getSharedPreferences(SETTING, 0);

        SharedPreferences.Editor edit = sp.edit();

        edit.putBoolean(value, key);

        edit.commit();

    }

    //取出布尔值sp
    public boolean getBoolean(String value) {

        SharedPreferences sp = mContext.getSharedPreferences(SETTING, 0);

        boolean b = sp.getBoolean(value, false);

        return b;

    }

    //存储字符串sp
    public void setString(String key, String value) {

        SharedPreferences sp = mContext.getSharedPreferences(SETTING, 0);

        SharedPreferences.Editor edit = sp.edit();

        edit.putString(key, value);

        edit.commit();

    }

    //取出字符串sp
    public String getString(String value) {

        SharedPreferences sp = mContext.getSharedPreferences(SETTING, 0);

        String s = sp.getString(value, "");

        return s;

    }

    //存储整数sp
    public void setInteger(String value, int key) {

        SharedPreferences sp = mContext.getSharedPreferences(SETTING, 0);

        SharedPreferences.Editor edit = sp.edit();

        edit.putInt(value, key);

        edit.commit();

    }

    //取出字符串sp
    public int getInteger(String value) {

        SharedPreferences sp = mContext.getSharedPreferences(SETTING, 0);

        int i = sp.getInt(value, 0);

        return i;

    }
}
