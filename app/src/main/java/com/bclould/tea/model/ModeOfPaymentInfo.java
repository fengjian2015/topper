package com.bclould.tea.model;

/**
 * Created by GA on 2018/4/8.
 */

public class ModeOfPaymentInfo {

    /**
     * status : 1
     * data : {"bank":true,"alipay":false,"wechat":false}
     */

    private int status;
    private DataBean data;

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
         * bank : true
         * alipay : false
         * wechat : false
         */

        private boolean bank;
        private boolean alipay;
        private boolean wechat;

        public boolean isBank() {
            return bank;
        }

        public void setBank(boolean bank) {
            this.bank = bank;
        }

        public boolean isAlipay() {
            return alipay;
        }

        public void setAlipay(boolean alipay) {
            this.alipay = alipay;
        }

        public boolean isWechat() {
            return wechat;
        }

        public void setWechat(boolean wechat) {
            this.wechat = wechat;
        }
    }
}
