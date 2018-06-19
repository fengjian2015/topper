package com.bclould.tea.model;

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
    private int sendStatus; //0發送中 1 成功  2失敗
    private int msgType;
    private int imageType;
    private String send;//2018-05-15增加字段，用於判斷是誰發送消息
    //2018年5月22日增加定位相關信息
    private float lat;
    private float lng;
    private String address;
    private String title;
    //2018-05-28新增名片分享
    private String headUrl;
    private String cardUser;
    //2018-05-29新增鏈接分享和用於會話列表顯示字段
    private String linkUrl;
    private String content;
    private String converstaion;
    //2018-05-29新增分享竞猜字段
    private String guessPw;
    private String initiator;//发起人
    private String betId;
    private String periodQty;
    private String key;
    //2018-06-13新增創建消息時間
    private long createTime;
    //  //2018-06-14新增消息唯一標示
    private String msgId;
    //2018-06-15增加用於聊天界面時間顯示
    private String showChatTime;

    public String getShowChatTime() {
        return showChatTime;
    }

    public void setShowChatTime(String showChatTime) {
        this.showChatTime = showChatTime;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getBetId() {
        return betId;
    }

    public void setBetId(String betId) {
        this.betId = betId;
    }

    public String getPeriodQty() {
        return periodQty;
    }

    public void setPeriodQty(String periodQty) {
        this.periodQty = periodQty;
    }

    public String getGuessPw() {
        return guessPw;
    }

    public void setGuessPw(String guessPw) {
        this.guessPw = guessPw;
    }

    public String getInitiator() {
        return initiator;
    }

    public void setInitiator(String initiator) {
        this.initiator = initiator;
    }

    public String getLinkUrl() {
        return linkUrl;
    }

    public void setLinkUrl(String linkUrl) {
        this.linkUrl = linkUrl;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getConverstaion() {
        return converstaion;
    }

    public void setConverstaion(String converstaion) {
        this.converstaion = converstaion;
    }

    public String getHeadUrl() {
        return headUrl;
    }

    public void setHeadUrl(String headUrl) {
        this.headUrl = headUrl;
    }

    public String getCardUser() {
        return cardUser;
    }

    public void setCardUser(String cardUser) {
        this.cardUser = cardUser;
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
