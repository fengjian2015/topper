package com.bclould.tea.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by GA on 2018/1/19.
 */

public class DealListInfo implements Serializable {


    /**
     * status : 1
     * massage : ok
     * data : [{"id":2,"user_id":34,"type":2,"coin_name":"btc","country":"china","currency":"cny","premium":"20.00000000","price":"80000.00000000","number":"1.80000000","deadline":20,"pay_type":"支付宝","min_amount":2000,"max_amount":100000,"remark":"标记","status":2,"created_at":1516243378,"count_trans_number":1,"username":"xihongwei"},{"id":2,"user_id":34,"type":2,"coin_name":"btc","country":"china","currency":"cny","premium":"20.00000000","price":"80000.00000000","number":"1.80000000","deadline":20,"pay_type":"支付宝","min_amount":2000,"max_amount":100000,"remark":"标记","status":2,"created_at":1516243378,"count_trans_number":1,"username":"xihongwei"}]
     */

    private int status;
    private String massage;
    private List<DataBean> data;

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

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean implements Serializable {
        /**
         * id : 2
         * user_id : 34
         * type : 2
         * coin_name : btc
         * country : china
         * currency : cny
         * premium : 20.00000000
         * price : 80000.00000000
         * number : 1.80000000
         * deadline : 20
         * pay_type : 支付宝
         * min_amount : 2000
         * max_amount : 100000
         * remark : 标记
         * status : 2
         * created_at : 1516243378
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
        private String remark;
        private String min_amount;
        private String max_amount;
        private int status;
        private String created_at;
        private int count_trans_number;
        private String username;
        private int self_trans;
        private String avatar;

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public int getSelf_trans() {
            return self_trans;
        }

        public void setSelf_trans(int self_trans) {
            this.self_trans = self_trans;
        }

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

        public String getMin_amount() {
            return min_amount;
        }

        public void setMin_amount(String min_amount) {
            this.min_amount = min_amount;
        }

        public String getMax_amount() {
            return max_amount;
        }

        public void setMax_amount(String max_amount) {
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
