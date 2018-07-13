package com.bclould.tea.model;

/**
 * Created by GA on 2018/2/27.
 */

public class QrCardInfo {
    private String message;
    private String name;
    private String roomId;
    private String roomName;
    private String from_name;
    private String roomPath;

    public String getRoomPath() {
        return roomPath;
    }

    public void setRoomPath(String roomPath) {
        this.roomPath = roomPath;
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

    public String getFrom_name() {
        return from_name;
    }

    public void setFrom_name(String from_name) {
        this.from_name = from_name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
