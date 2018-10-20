package com.bclould.tea.utils;

import com.bclould.tea.model.NodeInfo;

/**
 * Created by GA on 2017/11/22.
 */

public class MessageEvent {


    public MessageEvent(String msg) {
        mMsg = msg;
    }

    private String mMsg;
    private String coinName;
    private String state;
    private String reviewCount;
    private String id;
    private String likeCount;
    private boolean type;
    private String filtrate;
    private int number;
    private String name;
    private String filepath;
    private String roomId;
    private String url;
    private NodeInfo mNodeInfo;

    public NodeInfo getNodeInfo() {
        return mNodeInfo;
    }

    public void setNodeInfo(NodeInfo nodeInfo) {
        mNodeInfo = nodeInfo;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getFiltrate() {
        return filtrate;
    }

    public void setFiltrate(String filtrate) {
        this.filtrate = filtrate;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public boolean isType() {
        return type;
    }

    public void setType(boolean type) {
        this.type = type;
    }

    public String getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(String likeCount) {
        this.likeCount = likeCount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(String reviewCount) {
        this.reviewCount = reviewCount;
    }

    public String getMsg() {
        return mMsg;
    }

    public void setMsg(String msg) {
        mMsg = msg;
    }

    public String getCoinName() {
        return coinName;
    }

    public void setCoinName(String coinName) {
        this.coinName = coinName;
    }
}
