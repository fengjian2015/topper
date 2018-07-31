package com.bclould.tea.model;

/**
 * Created by GA on 2017/10/16.
 */

public class CardInfo {

    private int mIcon ;

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

    public CardInfo(int icon, String title) {
        mIcon = icon;
        mTitle = title;
    }



}
