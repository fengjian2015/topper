package com.bclould.tea.model;

/**
 * Created by GIjia on 2018/5/24.
 */

public class RoomMemberInfo {
    private int id;
    private String name;
    private String jid;
    private String image_url;
    private String remark;
    private String my_user;
    private String roomId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getMy_user() {
        return my_user;
    }

    public void setMy_user(String my_user) {
        this.my_user = my_user;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getJid() {
        return jid;
    }

    public void setJid(String jid) {
        this.jid = jid;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
