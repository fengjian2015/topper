package com.bclould.tocotalk.model;

/**
 * Created by GA on 2018/4/17.
 */

public class RedExpiredInfo {
    private int id;
    private int rp_type;
    private String number;
    private String created_at;
    private String coin_name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRp_type() {
        return rp_type;
    }

    public void setRp_type(int rp_type) {
        this.rp_type = rp_type;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getCoin_name() {
        return coin_name;
    }

    public void setCoin_name(String coin_name) {
        this.coin_name = coin_name;
    }

}
