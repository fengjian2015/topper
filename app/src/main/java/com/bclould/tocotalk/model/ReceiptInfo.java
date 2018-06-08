package com.bclould.tocotalk.model;

/**
 * Created by GA on 2018/3/28.
 */

public class ReceiptInfo {

    /**
     * status : 1
     * message : ok
     * data : {"coin_name":"TPC","date":"2018-03-27 15:02:03","name":"xihongwei"}
     */

    private int status;
    private int type;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

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
         * coin_name : TPC
         * date : 2018-03-27 15:02:03
         * name : xihongwei
         */

        private String coin_name;
        private String date;
        private String name;
        private String number;
        private String toco_id;

        public String getToco_id() {
            return toco_id;
        }

        public void setToco_id(String toco_id) {
            this.toco_id = toco_id;
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

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
