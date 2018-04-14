package com.bclould.tocotalk.model;

import java.util.List;

/**
 * Created by GA on 2018/3/27.
 */

public class TransferListInfo {

    /**
     * status : 1
     * data : [{"user_id":34,"log_id":12,"type":"转账","coin_name":"TPC","type_desc":"支出转账","number":"-2","created_at":"2018-03-23 11:40:01"},{"user_id":35,"log_id":12,"type":"转账","coin_name":"TPC","type_desc":"收到转账","number":"2","created_at":"2018-03-23 11:40:01"}]
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
         * log_id : 12
         * type : 转账
         * coin_name : TPC
         * type_desc : 支出转账
         * number : -2
         * created_at : 2018-03-23 11:40:01
         */

        private int user_id;
        private int log_id;
        private String type;
        private String coin_name;
        private String type_desc;
        private String number;
        private String created_at;
        private int type_number;

        public int getType_number() {
            return type_number;
        }

        public void setType_number(int type_number) {
            this.type_number = type_number;
        }

        public int getUser_id() {
            return user_id;
        }

        public void setUser_id(int user_id) {
            this.user_id = user_id;
        }

        public int getLog_id() {
            return log_id;
        }

        public void setLog_id(int log_id) {
            this.log_id = log_id;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getCoin_name() {
            return coin_name;
        }

        public void setCoin_name(String coin_name) {
            this.coin_name = coin_name;
        }

        public String getType_desc() {
            return type_desc;
        }

        public void setType_desc(String type_desc) {
            this.type_desc = type_desc;
        }

        public String getNumber() {
            return number;
        }

        public void setNumber(String number) {
            this.number = number;
        }

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }
    }
}
