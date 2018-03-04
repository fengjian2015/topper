package com.bclould.tocotalk.model;

import java.util.List;

/**
 * Created by GA on 2018/3/4.
 */

public class InOutInfo {

    /**
     * status : 1
     * data : [{"user_id":34,"user_name":"xihongwei","number":"100.00000000","number_u":"99.50000000","created_at":1519807152}]
     */

    private int status;
    private List<DataBean> data;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
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
         * user_name : xihongwei
         * number : 100.00000000
         * number_u : 99.50000000
         * created_at : 1519807152
         */

        private int user_id;
        private String user_name;
        private String number;
        private String number_u;
        private int created_at;

        public int getUser_id() {
            return user_id;
        }

        public void setUser_id(int user_id) {
            this.user_id = user_id;
        }

        public String getUser_name() {
            return user_name;
        }

        public void setUser_name(String user_name) {
            this.user_name = user_name;
        }

        public String getNumber() {
            return number;
        }

        public void setNumber(String number) {
            this.number = number;
        }

        public String getNumber_u() {
            return number_u;
        }

        public void setNumber_u(String number_u) {
            this.number_u = number_u;
        }

        public int getCreated_at() {
            return created_at;
        }

        public void setCreated_at(int created_at) {
            this.created_at = created_at;
        }
    }
}
