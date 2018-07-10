package com.bclould.tea.model;

import java.util.List;

/**
 * Created by GIjia on 2018/6/21.
 */

public class GroupMemberInfo {

    /**
     * status : 1
     * message : success
     * data : {"id":15,"user_id":34,"name":"习红卫的群聊","description":"","created_at":"1528703138","logo":"","users":[{"toco_id":"1c60397ae8842","name":"xihongwei","avatar":"http://toco.oss-cn-shenzhen.aliyuncs.com/TOCO34Avatar.png?time=1528958917"},{"toco_id":"45a8ae6359794","name":"liaolinan2","avatar":"http://toco--bucket.oss-cn-shenzhen.aliyuncs.com/TOCO41Avatar.png?time=1529658459"},{"toco_id":"93e8531adfa77","name":"xiaomin12","avatar":"https://bclould.s3.ap-northeast-2.amazonaws.com/TOCO213Avatar.png?time=1528774298"}]}
     */

    private int status;
    private String message;
    private DataBean data;

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

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * id : 15
         * user_id : 34
         * name : 习红卫的群聊
         * description :
         * created_at : 1528703138
         * logo :
         * users : [{"toco_id":"1c60397ae8842","name":"xihongwei","avatar":"http://toco.oss-cn-shenzhen.aliyuncs.com/TOCO34Avatar.png?time=1528958917"},{"toco_id":"45a8ae6359794","name":"liaolinan2","avatar":"http://toco--bucket.oss-cn-shenzhen.aliyuncs.com/TOCO41Avatar.png?time=1529658459"},{"toco_id":"93e8531adfa77","name":"xiaomin12","avatar":"https://bclould.s3.ap-northeast-2.amazonaws.com/TOCO213Avatar.png?time=1528774298"}]
         */

        private int id;
        private int user_id;
        private String name;
        private String toco_id;
        private String description;
        private String created_at;
        private String logo;
        private int max_people;
        private int is_allow_modify_data;
        private List<UsersBean> users;

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

        public String getLogo() {
            return logo;
        }

        public void setLogo(String logo) {
            this.logo = logo;
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
             * name : xihongwei
             * avatar : http://toco.oss-cn-shenzhen.aliyuncs.com/TOCO34Avatar.png?time=1528958917
             */

            private String toco_id;
            private String name;
            private String avatar;

            public String getToco_id() {
                return toco_id;
            }

            public void setToco_id(String toco_id) {
                this.toco_id = toco_id;
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
}
