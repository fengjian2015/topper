package com.bclould.tea.model;

/**
 * Created by GA on 2017/11/29.
 */

public class GoogleInfo {
    /**
     * key :
     * img :
     */

    private String key;
    private String img;
    private int is_google_verify;

    public int getIs_google_verify() {
        return is_google_verify;
    }

    public void setIs_google_verify(int is_google_verify) {
        this.is_google_verify = is_google_verify;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }
}
