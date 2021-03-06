package com.bclould.tea.model;

import java.util.List;

/**
 * Created by GA on 2018/4/23.
 */

public class GuessListInfo {

    /**
     * status : 1
     * message : ok
     * data : [{"id":6,"title":"咯哦哦","period_qty":1,"status":1,"created_at":"2018-04-26 15:31:57","coin_id":28,"single_coin":"2","limit_number":"10","user_name":"liaolinan2","coin_name":"TPC","lottery_time":"2018-04-26 15:31:57","win_number":"----","prize_pool_number":"10","lottery_status":1,"join_created_at":"2018-04-25 17:10:17","coin_count":"8"},{"id":3,"title":"我的竞猜你看着办","period_qty":1,"status":2,"created_at":"2018-04-22 20:22:01","coin_id":28,"single_coin":"2","limit_number":"20","user_name":"xihongwei","coin_name":"TPC","lottery_time":"2018-04-22 20:22:01","win_number":"----","prize_pool_number":"0","lottery_status":1,"join_created_at":"2018-04-23 15:31:18","coin_count":"2"}]
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
         * id : 6
         * title : 咯哦哦
         * period_qty : 1
         * status : 1
         * created_at : 2018-04-26 15:31:57
         * coin_id : 28
         * single_coin : 2
         * limit_number : 10
         * user_name : liaolinan2
         * coin_name : TPC
         * lottery_time : 2018-04-26 15:31:57
         * win_number : ----
         * prize_pool_number : 10
         * lottery_status : 1
         * join_created_at : 2018-04-25 17:10:17
         * coin_count : 8
         */

        private int id;
        private String title;
        private int period_qty;
        private int status;
        private String created_at;
        private int coin_id;
        private String single_coin;
        private String limit_number;
        private String user_name;
        private String coin_name;
        private String lottery_time;
        private String win_number;
        private String prize_pool_number;
        private int lottery_status;
        private String join_created_at;
        private String coin_count;
        private String password;

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

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

        public int getPeriod_qty() {
            return period_qty;
        }

        public void setPeriod_qty(int period_qty) {
            this.period_qty = period_qty;
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

        public int getCoin_id() {
            return coin_id;
        }

        public void setCoin_id(int coin_id) {
            this.coin_id = coin_id;
        }

        public String getSingle_coin() {
            return single_coin;
        }

        public void setSingle_coin(String single_coin) {
            this.single_coin = single_coin;
        }

        public String getLimit_number() {
            return limit_number;
        }

        public void setLimit_number(String limit_number) {
            this.limit_number = limit_number;
        }

        public String getUser_name() {
            return user_name;
        }

        public void setUser_name(String user_name) {
            this.user_name = user_name;
        }

        public String getCoin_name() {
            return coin_name;
        }

        public void setCoin_name(String coin_name) {
            this.coin_name = coin_name;
        }

        public String getLottery_time() {
            return lottery_time;
        }

        public void setLottery_time(String lottery_time) {
            this.lottery_time = lottery_time;
        }

        public String getWin_number() {
            return win_number;
        }

        public void setWin_number(String win_number) {
            this.win_number = win_number;
        }

        public String getPrize_pool_number() {
            return prize_pool_number;
        }

        public void setPrize_pool_number(String prize_pool_number) {
            this.prize_pool_number = prize_pool_number;
        }

        public int getLottery_status() {
            return lottery_status;
        }

        public void setLottery_status(int lottery_status) {
            this.lottery_status = lottery_status;
        }

        public String getJoin_created_at() {
            return join_created_at;
        }

        public void setJoin_created_at(String join_created_at) {
            this.join_created_at = join_created_at;
        }

        public String getCoin_count() {
            return coin_count;
        }

        public void setCoin_count(String coin_count) {
            this.coin_count = coin_count;
        }
    }
}
