package com.dashiji.biyun.model;

/**
 * Created by GA on 2017/12/14.
 */

public class MessageInfo {

    private String message;
    private String count;
    private String coin;
    private String username;
    private String time;
    private String remark;
    private int type;
    private int state;
    private int id;
    private int redId;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getCoin() {
        return coin;
    }

    public void setCoin(String coin) {
        this.coin = coin;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRedId() {
        return redId;
    }

    public void setRedId(int redId) {
        this.redId = redId;
    }

    @Override
    public String toString() {
        return "MessageInfo{" +
                "message='" + message + '\'' +
                ", count='" + count + '\'' +
                ", coin='" + coin + '\'' +
                ", username='" + username + '\'' +
                ", time='" + time + '\'' +
                ", remark='" + remark + '\'' +
                ", type=" + type +
                ", state=" + state +
                ", id=" + id +
                ", redId=" + redId +
                '}';
    }
}
