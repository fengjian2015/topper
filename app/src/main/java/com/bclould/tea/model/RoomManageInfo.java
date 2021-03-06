package com.bclould.tea.model;

/**
 * Created by GIjia on 2018/5/24.
 */

public class RoomManageInfo {
    private String roomImage;
    private String roomId;
    private String roomName;
    private int roomNumber;
    private String my_user;
    private String owner;
    private String description;
    private int isRefresh;
    private int allowModify;
    private int isReview;

    public int getIsReview() {
        return isReview;
    }

    public void setIsReview(int isReview) {
        this.isReview = isReview;
    }

    public int getAllowModify() {
        return allowModify;
    }

    public void setAllowModify(int allowModify) {
        this.allowModify = allowModify;
    }

    public int getIsRefresh() {
        return isRefresh;
    }

    public void setIsRefresh(int isRefresh) {
        this.isRefresh = isRefresh;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getMy_user() {
        return my_user;
    }

    public void setMy_user(String my_user) {
        this.my_user = my_user;
    }

    public String getRoomImage() {
        return roomImage;
    }

    public void setRoomImage(String roomImage) {
        this.roomImage = roomImage;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(int roomNumber) {
        this.roomNumber = roomNumber;
    }
}
