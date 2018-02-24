package com.dashiji.biyun.model;

/**
 * Created by GA on 2017/10/16.
 */

public class CardBean {

    private String mBackName;
    private String mBanckNumber;

    public CardBean(String backName, String backNumber) {
        mBackName = backName;
        mBanckNumber = backNumber;
    }

    public String getBackName() {
        return mBackName;
    }

    public void setBackName(String backName) {
        mBackName = backName;
    }

    public String getBanckNumber() {
        return mBanckNumber;
    }

    public void setBanckNumber(String banckNumber) {
        mBanckNumber = banckNumber;
    }


}
