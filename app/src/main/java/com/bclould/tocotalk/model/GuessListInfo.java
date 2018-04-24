package com.bclould.tocotalk.model;

import java.util.List;

/**
 * Created by GA on 2018/4/23.
 */

public class GuessListInfo {

    /**
     * status : 1
     * message : ok
     * data : [{"id":3,"title":"我的竞猜你看着办","period_qty":1,"status":1,"created_at":"2018-04-22 20:22:01","coin_id":28,"single_coin":"2","limit_number":"20","user_name":"xihongwei","coin_name":"TPC","lottery_time":"2018-04-22 20:22:01","win_number":"0","prize_pool_number":"0","lottery_status":1},{"id":2,"title":"","period_qty":2,"status":1,"created_at":"2018-04-20 15:16:05","coin_id":28,"single_coin":"2","limit_number":"20","user_name":"xihongwei","coin_name":"TPC","lottery_time":"2018-04-20 15:16:05","win_number":"","prize_pool_number":"0","lottery_status":0},"..."]
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
         * period_qty : 1
         * status : 1
         * created_at : 2018-04-22 20:22:01
         * coin_id : 28
         * single_coin : 2
         * limit_number : 20
         * user_name : xihongwei
         * coin_name : TPC
         * lottery_time : 2018-04-22 20:22:01
         * win_number : 0
         * prize_pool_number : 0
         * lottery_status : 1
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
    }
}
