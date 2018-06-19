package com.bclould.tea.model;

import java.util.List;

/**
 * Created by GA on 2018/3/4.
 */

public class InOutInfo {

    /**
     * status : 1
     * data : [{"user_id":41,"user_name":"liaolinan2","number":"1","number_u":"0.95","created_at":"2018-04-23 17:58:31","txid":"154185234234"},{"user_id":41,"user_name":"liaolinan2","number":"1","number_u":"0.95","created_at":"2018-04-23 17:55:14","txid":"我问问问问为"},"..."]
     */

    private int status;
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

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
         * user_id : 41
         * user_name : liaolinan2
         * number : 1
         * number_u : 0.95
         * created_at : 2018-04-23 17:58:31
         * txid : 154185234234
         */

        private int user_id;
        private String user_name;
        private String number;
        private String number_u;
        private String created_at;
        private String txid;

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

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }

        public String getTxid() {
            return txid;
        }

        public void setTxid(String txid) {
            this.txid = txid;
        }
    }
}
