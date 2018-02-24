package com.dashiji.biyun.model;

/**
 * Created by GA on 2017/12/19.
 */

public class ConversationInfo {
    private int number;
    private String message;
    private String time;
    private String user;
    private String friend;

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getFriend() {
        return friend;
    }

    public void setFriend(String friend) {
        this.friend = friend;
    }

    @Override
    public String toString() {
        return "ConversationInfo{" +
                "number='" + number + '\'' +
                ", message='" + message + '\'' +
                ", time='" + time + '\'' +
                ", user='" + user + '\'' +
                ", friend='" + friend + '\'' +
                '}';
    }
}
