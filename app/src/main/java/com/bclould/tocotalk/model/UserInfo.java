package com.bclould.tocotalk.model;

/**
 * Created by GA on 2017/12/26.
 */

public class UserInfo {
    private String user;
    private String path;
    private int status;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

}
