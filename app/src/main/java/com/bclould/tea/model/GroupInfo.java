package com.bclould.tea.model;

import java.util.List;

/**
 * Created by GA on 2018/1/5.
 */

public class GroupInfo {

    /**
     * status : 1
     * message : success
     * data : [{"id":14,"user_id":208,"name":"R级","description":"","created_at":"1528703132","users":[{"toco_id":"1c60397ae8842","avatar":"https://bclould.s3.ap-northeast-2.amazonaws.com/TOCO34Avatar.png"},{"toco_id":"0ecf6f29f0cac","avatar":""}]},{"id":15,"user_id":34,"name":"习红卫的群聊","description":"","created_at":"1528703138","users":[{"toco_id":"1c60397ae8842","avatar":"https://bclould.s3.ap-northeast-2.amazonaws.com/TOCO34Avatar.png"}]}]
     */

    private int status;
    private String message;
    private List<DataBean> data;
    private int is_allow_modify_data;

    public int getIs_allow_modify_data() {
        return is_allow_modify_data;
    }

    public void setIs_allow_modify_data(int is_allow_modify_data) {
        this.is_allow_modify_data = is_allow_modify_data;
    }

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
         * id : 14
         * user_id : 208
         * name : R级
         * description :
         * created_at : 1528703132
         * users : [{"toco_id":"1c60397ae8842","avatar":"https://bclould.s3.ap-northeast-2.amazonaws.com/TOCO34Avatar.png"},{"toco_id":"0ecf6f29f0cac","avatar":""}]
         */

        private int id;
        private int user_id;
        private String name;
        private String description;
        private String created_at;
        private String toco_id;
        private String logo;
        private int max_people;
        private int is_allow_modify_data;
        private int is_review;
        private List<UsersBean> users;

        public int getIs_review() {
            return is_review;
        }

        public void setIs_review(int is_review) {
            this.is_review = is_review;
        }

        public int getIs_allow_modify_data() {
            return is_allow_modify_data;
        }

        public void setIs_allow_modify_data(int is_allow_modify_data) {
            this.is_allow_modify_data = is_allow_modify_data;
        }

        public int getMax_people() {
            return max_people;
        }

        public void setMax_people(int max_people) {
            this.max_people = max_people;
        }

        public String getLogo() {
            return logo;
        }

        public void setLogo(String logo) {
            this.logo = logo;
        }

        public String getToco_id() {
            return toco_id;
        }

        public void setToco_id(String toco_id) {
            this.toco_id = toco_id;
        }

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

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }

        public List<UsersBean> getUsers() {
            return users;
        }

        public void setUsers(List<UsersBean> users) {
            this.users = users;
        }

        public static class UsersBean {
            /**
             * toco_id : 1c60397ae8842
             * avatar : https://bclould.s3.ap-northeast-2.amazonaws.com/TOCO34Avatar.png
             */

            private String toco_id;
            private String avatar;
            private String name;

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getToco_id() {
                return toco_id;
            }

            public void setToco_id(String toco_id) {
                this.toco_id = toco_id;
            }

            public String getAvatar() {
                return avatar;
            }

            public void setAvatar(String avatar) {
                this.avatar = avatar;
            }
        }
    }
}
