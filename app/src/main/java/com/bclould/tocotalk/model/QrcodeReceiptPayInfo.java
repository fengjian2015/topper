package com.bclould.tocotalk.model;

/**
 * Created by GA on 2018/4/18.
 */

public class QrcodeReceiptPayInfo {
    private int id;
    private String name;
    private String number;
    private String coin_name;
    private String created_at;
    private int type;
    private int type_number;

    public int getType_number() {
        return type_number;
    }

    public void setType_number(int type_number) {
        this.type_number = type_number;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getCoin_name() {
        return coin_name;
    }

    public void setCoin_name(String coin_name) {
        this.coin_name = coin_name;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }
}
