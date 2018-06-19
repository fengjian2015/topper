package com.bclould.tea.utils;

/**
 * Created by GA on 2017/11/22.
 */

public class MessageEvent {


    public MessageEvent(String msg) {
        mMsg = msg;
    }

    private String mMsg;
    private String coinName;
    private String state;
    private String reviewCount;
    private String id;
    private String likeCount;
    private boolean type;
    private String filtrate;
    private int number;

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getFiltrate() {
        return filtrate;
    }

    public void setFiltrate(String filtrate) {
        this.filtrate = filtrate;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public boolean isType() {
        return type;
    }

    public void setType(boolean type) {
        this.type = type;
    }

    public String getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(String likeCount) {
        this.likeCount = likeCount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(String reviewCount) {
        this.reviewCount = reviewCount;
    }

    public String getMsg() {
        return mMsg;
    }

    public void setMsg(String msg) {
        mMsg = msg;
    }

    public String getCoinName() {
        return coinName;
    }

    public void setCoinName(String coinName) {
        this.coinName = coinName;
    }
}