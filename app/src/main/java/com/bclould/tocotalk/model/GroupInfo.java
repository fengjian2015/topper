package com.bclould.tocotalk.model;

import java.util.List;

/**
 * Created by GA on 2018/1/5.
 */

public class GroupInfo {

    /**
     * status : 1
     * message : success
     * data : [{"user_id":34,"room_name":"xihongwei1","logo":"","room_max_people_number":20,"name":"","users":[{"jid":"","remark":"xihongwei","avatar":"https://bclould.s3.ap-northeast-2.amazonaws.com/TOCO34Avatar.png"},{"jid":"","remark":"廖矮子","avatar":"https://bclould.s3.ap-northeast-2.amazonaws.com/TOCO41Avatar.png?time=1527492529"}]},{"user_id":210,"room_name":"feng1231527650366936@conference.xmpp.coingbank.com","logo":"","room_max_people_number":200,"name":"","users":[{"jid":"","remark":"feng123sss","avatar":""},{"jid":"","remark":"feng123sss","avatar":""},{"jid":"","remark":"xihongwei","avatar":"https://bclould.s3.ap-northeast-2.amazonaws.com/TOCO34Avatar.png"},{"jid":"","remark":"hhjbjmmmws","avatar":"https://bclould.s3.ap-northeast-2.amazonaws.com/TOCO208Avatar.png?time=1527161040"}]},{"user_id":210,"room_name":"feng1231527650515747@conference.xmpp.coingbank.com","logo":"","room_max_people_number":200,"name":"","users":[{"jid":"","remark":"xihongwei","avatar":"https://bclould.s3.ap-northeast-2.amazonaws.com/TOCO34Avatar.png"},{"jid":"","remark":"hhjbjmmmws","avatar":"https://bclould.s3.ap-northeast-2.amazonaws.com/TOCO208Avatar.png?time=1527161040"}]},{"user_id":210,"room_name":"feng1231527652162353@conference.xmpp.coingbank.com","logo":"","room_max_people_number":200,"name":"大大大","users":[{"jid":"xihongwei@xmpp.coingbank.com","remark":"xihongwei","avatar":"https://bclould.s3.ap-northeast-2.amazonaws.com/TOCO34Avatar.png"},{"jid":"fengjian@xmpp.coingbank.com","remark":"hhjbjmmmws","avatar":"https://bclould.s3.ap-northeast-2.amazonaws.com/TOCO208Avatar.png?time=1527161040"}]}]
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
         * user_id : 34
         * room_name : xihongwei1
         * logo :
         * room_max_people_number : 20
         * name :
         * users : [{"jid":"","remark":"xihongwei","avatar":"https://bclould.s3.ap-northeast-2.amazonaws.com/TOCO34Avatar.png"},{"jid":"","remark":"廖矮子","avatar":"https://bclould.s3.ap-northeast-2.amazonaws.com/TOCO41Avatar.png?time=1527492529"}]
         */

        private int user_id;
        private String room_name;
        private String logo;
        private int room_max_people_number;
        private String name;
        private List<UsersBean> users;

        public int getUser_id() {
            return user_id;
        }

        public void setUser_id(int user_id) {
            this.user_id = user_id;
        }

        public String getRoom_name() {
            return room_name;
        }

        public void setRoom_name(String room_name) {
            this.room_name = room_name;
        }

        public String getLogo() {
            return logo;
        }

        public void setLogo(String logo) {
            this.logo = logo;
        }

        public int getRoom_max_people_number() {
            return room_max_people_number;
        }

        public void setRoom_max_people_number(int room_max_people_number) {
            this.room_max_people_number = room_max_people_number;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<UsersBean> getUsers() {
            return users;
        }

        public void setUsers(List<UsersBean> users) {
            this.users = users;
        }

        public static class UsersBean {
            /**
             * jid :
             * remark : xihongwei
             * avatar : https://bclould.s3.ap-northeast-2.amazonaws.com/TOCO34Avatar.png
             */

            private String jid;
            private String remark;
            private String avatar;

            public String getJid() {
                return jid;
            }

            public void setJid(String jid) {
                this.jid = jid;
            }

            public String getRemark() {
                return remark;
            }

            public void setRemark(String remark) {
                this.remark = remark;
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
