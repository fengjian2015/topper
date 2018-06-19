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
