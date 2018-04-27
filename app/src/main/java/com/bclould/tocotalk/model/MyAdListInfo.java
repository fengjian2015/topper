package com.bclould.tocotalk.model;

import java.util.List;

/**
 * Created by GA on 2018/4/27.
 */

public class MyAdListInfo {

    /**
     * status : 1
     * message : ok
     * data : [{"id":62,"user_id":34,"type":2,"coin_name":"TPC","country":"中國 - 大陸","currency":"CNY","premium":"0","price":"8.5","number":"0","deadline":30,"pay_type":"銀行卡","bank_id":0,"min_amount":0,"max_amount":10000,"remark":"买吧","status":0,"created_at":"2018-04-26 18:38:59","otc_free":"0.00000000","mobile":"17666105113","otc_out_free":"0.01000000","otc_in_free":"0.00000000","count_trans_number":1,"username":"xihongwei"},"..."]
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
         * id : 62
         * user_id : 34
         * type : 2
         * coin_name : TPC
         * country : 中國 - 大陸
         * currency : CNY
         * premium : 0
         * price : 8.5
         * number : 0
         * deadline : 30
         * pay_type : 銀行卡
         * bank_id : 0
         * min_amount : 0
         * max_amount : 10000
         * remark : 买吧
         * status : 0
         * created_at : 2018-04-26 18:38:59
         * otc_free : 0.00000000
         * mobile : 17666105113
         * otc_out_free : 0.01000000
         * otc_in_free : 0.00000000
         * count_trans_number : 1
         * username : xihongwei
         */

        private int id;
        private int user_id;
        private int type;
        private String coin_name;
        private String country;
        private String currency;
        private String premium;
        private String price;
        private String number;
        private int deadline;
        private String pay_type;
        private int bank_id;
        private int min_amount;
        private int max_amount;
        private String remark;
        private int status;
        private String created_at;
        private String otc_free;
        private String mobile;
        private String otc_out_free;
        private String otc_in_free;
        private int count_trans_number;
        private String username;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getUser_id() {
            return user_id;
        }

        public void setUser_id(int user_id) {
            this.user_id = user_id;
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

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public String getCurrency() {
            return currency;
        }

        public void setCurrency(String currency) {
            this.currency = currency;
        }

        public String getPremium() {
            return premium;
        }

        public void setPremium(String premium) {
            this.premium = premium;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public String getNumber() {
            return number;
        }

        public void setNumber(String number) {
            this.number = number;
        }

        public int getDeadline() {
            return deadline;
        }

        public void setDeadline(int deadline) {
            this.deadline = deadline;
        }

        public String getPay_type() {
            return pay_type;
        }

        public void setPay_type(String pay_type) {
            this.pay_type = pay_type;
        }

        public int getBank_id() {
            return bank_id;
        }

        public void setBank_id(int bank_id) {
            this.bank_id = bank_id;
        }

        public int getMin_amount() {
            return min_amount;
        }

        public void setMin_amount(int min_amount) {
            this.min_amount = min_amount;
        }

        public int getMax_amount() {
            return max_amount;
        }

        public void setMax_amount(int max_amount) {
            this.max_amount = max_amount;
        }

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }

        public String getOtc_free() {
            return otc_free;
        }

        public void setOtc_free(String otc_free) {
            this.otc_free = otc_free;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public String getOtc_out_free() {
            return otc_out_free;
        }

        public void setOtc_out_free(String otc_out_free) {
            this.otc_out_free = otc_out_free;
        }

        public String getOtc_in_free() {
            return otc_in_free;
        }

        public void setOtc_in_free(String otc_in_free) {
            this.otc_in_free = otc_in_free;
        }

        public int getCount_trans_number() {
            return count_trans_number;
        }

        public void setCount_trans_number(int count_trans_number) {
            this.count_trans_number = count_trans_number;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }
    }
}
