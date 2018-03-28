package com.bclould.tocotalk.model;

/**
 * Created by GA on 2018/3/28.
 */

public class QrReceiptInfo {
    private int redID;
    private String coin_id;
    private String coin_name;
    private String number;
    private String mark;

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }

    public int getRedId() {
        return redID;
    }

    public void setRedId(int redId) {
        this.redID = redId;
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
