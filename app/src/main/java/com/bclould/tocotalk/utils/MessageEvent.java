package com.bclould.tocotalk.utils;

/**
 * Created by GA on 2017/11/22.
 */

public class MessageEvent {


    public MessageEvent(String msg) {
        mMsg = msg;
    }

    private String mMsg;
    private String coinName;

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
