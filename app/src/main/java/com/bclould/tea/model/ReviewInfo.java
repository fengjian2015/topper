package com.bclould.tea.model;

import java.util.List;

/**
 * Created by GIjia on 2018/7/12.
 */

public class ReviewInfo {

    /**
     * status : 1
     * message : success
     * data : [{"id":6,"group_id":84,"user_id":209,"to_user_id":34,"label":null,"created_at":1531387279,"status":2,"to_toco_id":"1c60397ae8842","toco_id":"256dfdfb783b3","to_avatar":"http://toco--bucket.oss-cn-shenzhen.aliyuncs.com/TOCO34Avatar.png?time=1530544794","to_name":"xihongwei","name":"aaaaaaaa","avatar":"https://toco--bucket.oss-cn-shenzhen.aliyuncs.com/TOCO209Avatar.png?time=1530849394"}]
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
         * id : 6
         * group_id : 84
         * user_id : 209
         * to_user_id : 34
         * label : null
         * created_at : 1531387279
         * status : 2
         * to_toco_id : 1c60397ae8842
         * toco_id : 256dfdfb783b3
         * to_avatar : http://toco--bucket.oss-cn-shenzhen.aliyuncs.com/TOCO34Avatar.png?time=1530544794
         * to_name : xihongwei
         * name : aaaaaaaa
         * avatar : https://toco--bucket.oss-cn-shenzhen.aliyuncs.com/TOCO209Avatar.png?time=1530849394
         */

        private int id;
        private int group_id;
        private int user_id;
        private int to_user_id;
        private Object label;
        private int created_at;
        private int status;
        private String to_toco_id;
        private String toco_id;
        private String to_avatar;
        private String to_name;
        private String name;
        private String avatar;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getGroup_id() {
            return group_id;
        }

        public void setGroup_id(int group_id) {
            this.group_id = group_id;
        }

        public int getUser_id() {
            return user_id;
        }

        public void setUser_id(int user_id) {
            this.user_id = user_id;
        }

        public int getTo_user_id() {
            return to_user_id;
        }

        public void setTo_user_id(int to_user_id) {
            this.to_user_id = to_user_id;
        }

        public Object getLabel() {
            return label;
        }

        public void setLabel(Object label) {
            this.label = label;
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

        public String getTo_toco_id() {
            return to_toco_id;
        }

        public void setTo_toco_id(String to_toco_id) {
            this.to_toco_id = to_toco_id;
        }

        public String getToco_id() {
            return toco_id;
        }

        public void setToco_id(String toco_id) {
            this.toco_id = toco_id;
        }

        public String getTo_avatar() {
            return to_avatar;
        }

        public void setTo_avatar(String to_avatar) {
            this.to_avatar = to_avatar;
        }

        public String getTo_name() {
            return to_name;
        }

        public void setTo_name(String to_name) {
            this.to_name = to_name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }
    }
}
