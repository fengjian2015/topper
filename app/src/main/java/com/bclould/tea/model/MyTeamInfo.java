package com.bclould.tea.model;

import java.util.List;

/**
 * Created by GIjia on 2018/9/25.
 */

public class MyTeamInfo {

    /**
     * status : 1
     * data : {"team_member":"12人","push_member":"5人","performance":"2343FTC","user_name":"xihongwei","user_list":[{"avatar":"","user_name":"习红卫下级","created_at":"2018.01.01 20:00","performance":"100FTC","next_id":0,"prev_id":0,"type_desc":"共识","type":1},{"avatar":"","user_name":"习红卫下级","created_at":"2018.01.01 20:00","performance":"100FTC","next_id":0,"prev_id":0,"type_desc":"超级","type":2}]}
     */

    private int status;
    private String message;
    private DataBean data;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * team_member : 12人
         * push_member : 5人
         * performance : 2343FTC
         * user_name : xihongwei
         * user_list : [{"avatar":"","user_name":"习红卫下级","created_at":"2018.01.01 20:00","performance":"100FTC","next_id":0,"prev_id":0,"type_desc":"共识","type":1},{"avatar":"","user_name":"习红卫下级","created_at":"2018.01.01 20:00","performance":"100FTC","next_id":0,"prev_id":0,"type_desc":"超级","type":2}]
         */

        private String team_member;
        private String push_member;
        private String performance;
        private String user_name;
        private String team_push_reward;
        private List<UserListBean> user_list;

        public String getTeam_push_reward() {
            return team_push_reward;
        }

        public void setTeam_push_reward(String team_push_reward) {
            this.team_push_reward = team_push_reward;
        }

        public String getTeam_member() {
            return team_member;
        }

        public void setTeam_member(String team_member) {
            this.team_member = team_member;
        }

        public String getPush_member() {
            return push_member;
        }

        public void setPush_member(String push_member) {
            this.push_member = push_member;
        }

        public String getPerformance() {
            return performance;
        }

        public void setPerformance(String performance) {
            this.performance = performance;
        }

        public String getUser_name() {
            return user_name;
        }

        public void setUser_name(String user_name) {
            this.user_name = user_name;
        }

        public List<UserListBean> getUser_list() {
            return user_list;
        }

        public void setUser_list(List<UserListBean> user_list) {
            this.user_list = user_list;
        }

        public static class UserListBean {
            /**
             * avatar :
             * user_name : 习红卫下级
             * created_at : 2018.01.01 20:00
             * performance : 100FTC
             * next_id : 0
             * prev_id : 0
             * type_desc : 共识
             * type : 1
             */

            private String avatar;
            private String user_name;
            private String created_at;
            private String performance;
            private int next_id;
            private int prev_id;
            private String type_desc;
            private int type;

            public String getAvatar() {
                return avatar;
            }

            public void setAvatar(String avatar) {
                this.avatar = avatar;
            }

            public String getUser_name() {
                return user_name;
            }

            public void setUser_name(String user_name) {
                this.user_name = user_name;
            }

            public String getCreated_at() {
                return created_at;
            }

            public void setCreated_at(String created_at) {
                this.created_at = created_at;
            }

            public String getPerformance() {
                return performance;
            }

            public void setPerformance(String performance) {
                this.performance = performance;
            }

            public int getNext_id() {
                return next_id;
            }

            public void setNext_id(int next_id) {
                this.next_id = next_id;
            }

            public int getPrev_id() {
                return prev_id;
            }

            public void setPrev_id(int prev_id) {
                this.prev_id = prev_id;
            }

            public String getType_desc() {
                return type_desc;
            }

            public void setType_desc(String type_desc) {
                this.type_desc = type_desc;
            }

            public int getType() {
                return type;
            }

            public void setType(int type) {
                this.type = type;
            }
        }
    }
}
