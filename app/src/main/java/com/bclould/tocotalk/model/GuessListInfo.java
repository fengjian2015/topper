package com.bclould.tocotalk.model;

import java.util.List;

/**
 * Created by GA on 2018/4/23.
 */

public class GuessListInfo {

    /**
     * status : 1
     * message : ok
     * data : [{"id":3,"title":"我的竞猜你看着办","single_coin":"2","limit_people_number":10,"limit_number":"20","coin_id":28,"deadline":60,"period_qty":1,"user_id":34,"type":1,"status":1,"created_at":"2018-04-22 20:22:01","coin_name":"TPC","countdown":2789},{"id":2,"title":"","single_coin":"2","limit_people_number":10,"limit_number":"20","coin_id":28,"deadline":60,"period_qty":1,"user_id":34,"type":1,"status":1,"created_at":"2018-04-20 15:16:05","coin_name":"TPC","countdown":0},"..."]
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
         * id : 3
         * title : 我的竞猜你看着办
         * single_coin : 2
         * limit_people_number : 10
         * limit_number : 20
         * coin_id : 28
         * deadline : 60
         * period_qty : 1
         * user_id : 34
         * type : 1
         * status : 1
         * created_at : 2018-04-22 20:22:01
         * coin_name : TPC
         * countdown : 2789
         */

        private int id;
        private String title;
        private String single_coin;
        private int limit_people_number;
        private String limit_number;
        private int coin_id;
        private int deadline;
        private int period_qty;
        private int user_id;
        private int type;
        private int status;
        private String created_at;
        private String coin_name;
        private int countdown;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getSingle_coin() {
            return single_coin;
        }

        public void setSingle_coin(String single_coin) {
            this.single_coin = single_coin;
        }

        public int getLimit_people_number() {
            return limit_people_number;
        }

        public void setLimit_people_number(int limit_people_number) {
            this.limit_people_number = limit_people_number;
        }

        public String getLimit_number() {
            return limit_number;
        }

        public void setLimit_number(String limit_number) {
            this.limit_number = limit_number;
        }

        public int getCoin_id() {
            return coin_id;
        }

        public void setCoin_id(int coin_id) {
            this.coin_id = coin_id;
        }

        public int getDeadline() {
            return deadline;
        }

        public void setDeadline(int deadline) {
            this.deadline = deadline;
        }

        public int getPeriod_qty() {
            return period_qty;
        }

        public void setPeriod_qty(int period_qty) {
            this.period_qty = period_qty;
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

        public String getCoin_name() {
            return coin_name;
        }

        public void setCoin_name(String coin_name) {
            this.coin_name = coin_name;
        }

        public int getCountdown() {
            return countdown;
        }

        public void setCountdown(int countdown) {
            this.countdown = countdown;
        }
    }
}
