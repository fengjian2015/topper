package com.bclould.tea.model;

import java.util.List;

/**
 * Created by GIjia on 2018/7/3.
 */

public class NewFriendInfo {

    /**
     * status : 1
     * message : success
     * data : [{"id":1,"user_id":35,"friend_id":212,"friend_label":"xixixi","created_at":1530610555,"status":2,"toco_id":"03ab2b70247f5","friend_toco_id":"","avatar":"","name":"xixixi","friend_name":"13823559227","friend_avatar":"http://toco--bucket.oss-cn-shenzhen.aliyuncs.com/TOCO212Avatar.png?time=1530532012"},{"id":2,"user_id":212,"friend_id":35,"friend_label":"颜魏","created_at":1530611088,"status":1,"toco_id":"750cafebbf780","friend_toco_id":"","avatar":"http://toco--bucket.oss-cn-shenzhen.aliyuncs.com/TOCO212Avatar.png?time=1530532012","name":"13823559227","friend_name":"xixixi","friend_avatar":""}]
     */

    private int status;
    private String message;
    private List<DataBean> data;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * id : 1
         * user_id : 35
         * friend_id : 212
         * friend_label : xixixi
         * created_at : 1530610555
         * status : 2
         * toco_id : 03ab2b70247f5
         * friend_toco_id :
         * avatar :
         * name : xixixi
         * friend_name : 13823559227
         * friend_avatar : http://toco--bucket.oss-cn-shenzhen.aliyuncs.com/TOCO212Avatar.png?time=1530532012
         */

        private int id;
        private int user_id;
        private int friend_id;
        private String friend_label;
        private int created_at;
        private int status;
        private String toco_id;
        private String friend_toco_id;
        private String avatar;
        private String name;
        private String friend_name;
        private String friend_avatar;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getUser_id() {
            return user_id;
        }

        public void setUser_id(int user_id) {
            this.user_id = user_id;
        }

        public int getFriend_id() {
            return friend_id;
        }

        public void setFriend_id(int friend_id) {
            this.friend_id = friend_id;
        }

        public String getFriend_label() {
            return friend_label;
        }

        public void setFriend_label(String friend_label) {
            this.friend_label = friend_label;
        }

        public int getCreated_at() {
            return created_at;
        }

        public void setCreated_at(int created_at) {
            this.created_at = created_at;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getToco_id() {
            return toco_id;
        }

        public void setToco_id(String toco_id) {
            this.toco_id = toco_id;
        }

        public String getFriend_toco_id() {
            return friend_toco_id;
        }

        public void setFriend_toco_id(String friend_toco_id) {
            this.friend_toco_id = friend_toco_id;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getFriend_name() {
            return friend_name;
        }

        public void setFriend_name(String friend_name) {
            this.friend_name = friend_name;
        }

        public String getFriend_avatar() {
            return friend_avatar;
        }

        public void setFriend_avatar(String friend_avatar) {
            this.friend_avatar = friend_avatar;
        }
    }
}
