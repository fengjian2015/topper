package com.bclould.tea.model;

import android.support.annotation.NonNull;

import com.bclould.tea.utils.Cn2Spell;
import com.bclould.tea.utils.StringUtils;

/**
 * Created by GA on 2017/12/26.
 */

public class UserInfo implements Comparable<UserInfo> {
    private String user;
    private String path;
    private int status;
    private String remark;
    private String pinyin;
    private String firstLetter;
    private String userName;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPinyin() {
        return pinyin;
    }

    public String getFirstLetter() {
        return firstLetter;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
        pinyin = Cn2Spell.getPinYin(remark); // 根据姓名获取拼音
        firstLetter="";
        if(!StringUtils.isEmpty(pinyin)) {
            firstLetter = pinyin.substring(0, 1).toUpperCase(); // 获取拼音首字母并转成大写
        }
        if (!firstLetter.matches("[A-Z]")) { // 如果不在A-Z中则默认为“#”
            firstLetter = "#";
        }
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public int compareTo(@NonNull UserInfo userInfo) {
        if (firstLetter.equals("#") && !userInfo.getFirstLetter().equals("#")) {
            return 1;
        } else if (!firstLetter.equals("#") && userInfo.getFirstLetter().equals("#")) {
            return -1;
        } else {
            return pinyin.compareToIgnoreCase(userInfo.getPinyin());
        }
    }
}
