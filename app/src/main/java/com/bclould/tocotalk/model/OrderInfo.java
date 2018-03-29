package com.bclould.tocotalk.model;

import java.io.Serializable;

/**
 * Created by GA on 2018/1/22.
 */

public class OrderInfo implements Serializable {

    /**
     * status : 1
     * massage : 成功
     * data : {"order_no":"2018012656565253","payment_no":301281,"trans_id":10,"type":1,"coin_name":"BTC","currency":"人民幣","price":"80000","trans_amount":"4000","number":"0.05","pay_type":"銀聯, 微信, 支付寶","user_id":34,"user_name":"xihongwei","to_user_id":41,"to_user_name":"liaolinan2","created_at":1516945496,"id":25,"deadline":1800}
     */

    private int status;
    private String massage;
    private DataBean data;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMassage() {
        return massage;
    }

    public void setMassage(String massage) {
        this.massage = massage;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean implements Serializable {
        /**
         * order_no : 2018012656565253
         * payment_no : 301281
         * trans_id : 10
         * type : 1
         * coin_name : BTC
         * currency : 人民幣
         * price : 80000
         * trans_amount : 4000
         * number : 0.05
         * pay_type : 銀聯, 微信, 支付寶
         * user_id : 34
         * user_name : xihongwei
         * to_user_id : 41
         * to_user_name : liaolinan2
         * created_at : 1516945496
         * id : 25
         * deadline : 1800
         */

        private String order_no;
        private int payment_no;
        private int trans_id;
        private int type;
        private String coin_name;
        private String currency;
        private String price;
        private String trans_amount;
        private String number;
        private String pay_type;
        private int user_id;
        private String user_name;
        private int to_user_id;
        private String to_user_name;
        private String created_at;
        private int id;
        private int deadline;

        public String getOrder_no() {
            return order_no;
        }

        public void setOrder_no(String order_no) {
            this.order_no = order_no;
        }

        public int getPayment_no() {
            return payment_no;
        }

        public void setPayment_no(int payment_no) {
            this.payment_no = payment_no;
        }

        public int getTrans_id() {
            return trans_id;
        }

        public void setTrans_id(int trans_id) {
            this.trans_id = trans_id;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getCoin_name() {
            return coin_name;
        }

        public void setCoin_name(String coin_name) {
            this.coin_name = coin_name;
        }

        public String getCurrency() {
            return currency;
        }

        public void setCurrency(String currency) {
            this.currency = currency;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public String getTrans_amount() {
            return trans_amount;
        }

        public void setTrans_amount(String trans_amount) {
            this.trans_amount = trans_amount;
        }

        public String getNumber() {
            return number;
        }

        public void setNumber(String number) {
            this.number = number;
        }

        public String getPay_type() {
            return pay_type;
        }

        public void setPay_type(String pay_type) {
            this.pay_type = pay_type;
        }

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

        public int getTo_user_id() {
            return to_user_id;
        }

        public void setTo_user_id(int to_user_id) {
            this.to_user_id = to_user_id;
        }

        public String getTo_user_name() {
            return to_user_name;
        }

        public void setTo_user_name(String to_user_name) {
            this.to_user_name = to_user_name;
        }

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getDeadline() {
            return deadline;
        }

        public void setDeadline(int deadline) {
            this.deadline = deadline;
        }
    }
}
