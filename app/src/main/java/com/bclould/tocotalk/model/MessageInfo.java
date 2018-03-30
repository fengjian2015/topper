package com.bclould.tocotalk.model;

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
    private String voice;
    private int voiceStatus;
    private String voiceTime;
    private int sendStatus;
    private int msgType;
    private int imageType;

    public int getImageType() {
        return imageType;
    }

    public void setImageType(int imageType) {
        this.imageType = imageType;
    }

    public int getMsgType() {
        return msgType;
    }

    public void setMsgType(int msgType) {
        this.msgType = msgType;
    }

    public int getSendStatus() {
        return sendStatus;
    }

    public void setSendStatus(int sendStatus) {
        this.sendStatus = sendStatus;
    }

    public String getVoice() {
        return voice;
    }

    public void setVoice(String voice) {
        this.voice = voice;
    }

    public int getVoiceStatus() {
        return voiceStatus;
    }

    public void setVoiceStatus(int voiceStatus) {
        this.voiceStatus = voiceStatus;
    }

    public String getVoiceTime() {
        return voiceTime;
    }

    public void setVoiceTime(String voiceTime) {
        this.voiceTime = voiceTime;
    }

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
                ", voice='" + voice + '\'' +
                ", voiceStatus=" + voiceStatus +
                ", voiceTime='" + voiceTime + '\'' +
                ", sendStatus=" + sendStatus +
                '}';
    }
}
