package com.bclould.tocotalk.model;

import java.io.Serializable;

/**
 * Created by GA on 2017/12/14.
 */

public class MessageInfo implements Serializable {

    private String message;
    private String count;
    private String coin;
    private String username;
    private String time;
    private String remark;
    private int type;
    private int status;
    private int id;
    private int redId;
    private String voice;
    private int voiceStatus;
    private String voiceTime;
    private int sendStatus;
    private int msgType;
    private int imageType;
    private String send;//2018-05-15增加字段，用於判斷是誰發送消息
    //2018年5月22日增加定位相關信息
    private float lat;
    private float lng;
    private String address;
    private String title;

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
                ", status=" + status +
                ", id=" + id +
                ", redId=" + redId +
                ", voice='" + voice + '\'' +
                ", voiceStatus=" + voiceStatus +
                ", voiceTime='" + voiceTime + '\'' +
                ", sendStatus=" + sendStatus +
                ", msgType=" + msgType +
                ", imageType=" + imageType +
                ", send='" + send + '\'' +
                ", lat=" + lat +
                ", lng=" + lng +
                ", address='" + address + '\'' +
                ", title='" + title + '\'' +
                '}';
    }

    public float getLat() {
        return lat;
    }

    public void setLat(float lat) {
        this.lat = lat;
    }

    public float getLng() {
        return lng;
    }

    public void setLng(float lng) {
        this.lng = lng;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSend() {
        return send;
    }

    public void setSend(String send) {
        this.send = send;
    }

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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
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

}
