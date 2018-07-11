package com.bclould.tea.model;

/**
 * Created by GA on 2017/12/19.
 */

public class ConversationInfo {
    private int number;
    private String message;
    private String time;
    private String user;
    private String friend;
    private String istop;
    private String chatType;
    private long createTime;
    private String draft;
    private String atme;

    public String getAtme() {
        return atme;
    }

    public void setAtme(String atme) {
        this.atme = atme;
    }

    public String getDraft() {
        return draft;
    }

    public void setDraft(String draft) {
        this.draft = draft;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public String getChatType() {
        return chatType;
    }

    public void setChatType(String chatType) {
        this.chatType = chatType;
    }

    public String getIstop() {
        return istop;
    }

    public void setIstop(String istop) {
        this.istop = istop;
    }

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
                "number=" + number +
                ", message='" + message + '\'' +
                ", time='" + time + '\'' +
                ", user='" + user + '\'' +
                ", friend='" + friend + '\'' +
                ", istop='" + istop + '\'' +
                ", chatType='" + chatType + '\'' +
                '}';
    }
}
