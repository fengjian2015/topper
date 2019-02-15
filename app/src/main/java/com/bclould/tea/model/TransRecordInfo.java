package com.bclould.tea.model;

/**
 * Created by GA on 2018/4/14.
 */

public class TransRecordInfo {

    /**
     * status : 1
     * data : {"type_name":"收款","name":"chuyang","number":"12","coin_name":"TPC","created_at":"2018-04-02 21:26:17"}
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
         * type_name : 收款
         * name : chuyang
         * number : 12
         * coin_name : TPC
         * created_at : 2018-04-02 21:26:17
         */

        private String type_name;
        private String name;
        private String number;
        private String coin_name;
        private String created_at;
        private String txid;
        private String order_sn;

        public String getOrder_sn() {
            return order_sn;
        }

        public void setOrder_sn(String order_sn) {
            this.order_sn = order_sn;
        }

        public String getTxid() {
            return txid;
        }

        public void setTxid(String txid) {
            this.txid = txid;
        }

        public String getType_name() {
            return type_name;
        }

        public void setType_name(String type_name) {
            this.type_name = type_name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getNumber() {
            return number;
        }

        public void setNumber(String number) {
            this.number = number;
        }

        public String getCoin_name() {
            return coin_name;
        }

        public void setCoin_name(String coin_name) {
            this.coin_name = coin_name;
        }

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }
    }
}
