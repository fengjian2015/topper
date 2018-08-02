package com.bclould.tea.model;

/**
 * Created by GA on 2017/10/16.
 */

public class CardInfo {

    private int mIcon;
    private int mBg;

    public int getIcon() {
        return mIcon;
    }

    public void setIcon(int icon) {
        mIcon = icon;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    private String mTitle;

    public int getBg() {
        return mBg;
    }

    public void setBg(int bg) {
        mBg = bg;
    }

    public CardInfo(int icon, String title, int bg) {
        mIcon = icon;
        mTitle = title;
        mBg = bg;
    }
}
