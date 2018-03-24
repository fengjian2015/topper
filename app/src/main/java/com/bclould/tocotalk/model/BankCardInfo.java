package com.bclould.tocotalk.model;

import java.io.Serializable;

/**
 * Created by GA on 2018/3/21.
 */

public class BankCardInfo {

    /**
     * status : 1
     * message :
     * data : {"bank":"农业银行-金穗通宝卡(银联卡)-借记卡","truename":"习红卫","card_number":"421222199511140072"}
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

    public static class DataBean implements Serializable {
        /**
         * bank : 农业银行-金穗通宝卡(银联卡)-借记卡
         * truename : 习红卫
         * card_number : 421222199511140072
         */

        private String bank;
        private String truename;
        private String card_number;

        public String getBank() {
            return bank;
        }

        public void setBank(String bank) {
            this.bank = bank;
        }

        public String getTruename() {
            return truename;
        }

        public void setTruename(String truename) {
            this.truename = truename;
        }

        public String getCard_number() {
            return card_number;
        }

        public void setCard_number(String card_number) {
            this.card_number = card_number;
        }
    }
}
