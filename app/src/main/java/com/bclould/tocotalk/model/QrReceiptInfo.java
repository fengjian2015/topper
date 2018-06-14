package com.bclould.tocotalk.model;

/**
 * Created by GA on 2018/3/28.
 */

public class QrReceiptInfo {
    private int user_id;
    private String coin_id;
    private String coin_name;
    private String number;
    private String mark;
    private String user_name;

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getCoin_id() {
        return coin_id;
    }

    public void setCoin_id(String coin_id) {
        this.coin_id = coin_id;
    }

    public String getCoin_name() {
        return coin_name;
    }

    public void setCoin_name(String coin_name) {
        this.coin_name = coin_name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

}
