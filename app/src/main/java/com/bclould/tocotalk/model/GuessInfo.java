package com.bclould.tocotalk.model;

import java.util.List;

/**
 * Created by GA on 2018/4/25.
 */

public class GuessInfo {

    /**
     * status : 1
     * message : ok
     * data : {"id":2,"title":"","period_qty":2,"status":1,"created_at":"2018-04-20 15:16:05","coin_id":28,"single_coin":"2","limit_number":"20","user_name":"xihongwei","countdown":0,"coin_name":"TPC","current_people_number":1,"prize_pool_number":"30","betList":[{"bet_number":"yt:ue:s8:71","created_at":"2018-04-20 15:23:09","bonus_number":"4.80000000","winning_type":"1","status":3}]}
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

    public static class DataBean {
        /**
         * id : 2
         * title :
         * period_qty : 2
         * status : 1
         * created_at : 2018-04-20 15:16:05
         * coin_id : 28
         * single_coin : 2
         * limit_number : 20
         * user_name : xihongwei
         * countdown : 0
         * coin_name : TPC
         * current_people_number : 1
         * prize_pool_number : 30
         * betList : [{"bet_number":"yt:ue:s8:71","created_at":"2018-04-20 15:23:09","bonus_number":"4.80000000","winning_type":"1","status":3}]
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
        private int countdown;
        private String coin_name;
        private int current_people_number;
        private String prize_pool_number;
        private List<BetListBean> betList;

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

        public int getCountdown() {
            return countdown;
        }

        public void setCountdown(int countdown) {
            this.countdown = countdown;
        }

        public String getCoin_name() {
            return coin_name;
        }

        public void setCoin_name(String coin_name) {
            this.coin_name = coin_name;
        }

        public int getCurrent_people_number() {
            return current_people_number;
        }

        public void setCurrent_people_number(int current_people_number) {
            this.current_people_number = current_people_number;
        }

        public String getPrize_pool_number() {
            return prize_pool_number;
        }

        public void setPrize_pool_number(String prize_pool_number) {
            this.prize_pool_number = prize_pool_number;
        }

        public List<BetListBean> getBetList() {
            return betList;
        }

        public void setBetList(List<BetListBean> betList) {
            this.betList = betList;
        }

        public static class BetListBean {
            /**
             * bet_number : yt:ue:s8:71
             * created_at : 2018-04-20 15:23:09
             * bonus_number : 4.80000000
             * winning_type : 1
             * status : 3
             */

            private String bet_number;
            private String created_at;
            private String bonus_number;
            private String winning_type;
            private int status;

            public String getBet_number() {
                return bet_number;
            }

            public void setBet_number(String bet_number) {
                this.bet_number = bet_number;
            }

            public String getCreated_at() {
                return created_at;
            }

            public void setCreated_at(String created_at) {
                this.created_at = created_at;
            }

            public String getBonus_number() {
                return bonus_number;
            }

            public void setBonus_number(String bonus_number) {
                this.bonus_number = bonus_number;
            }

            public String getWinning_type() {
                return winning_type;
            }

            public void setWinning_type(String winning_type) {
                this.winning_type = winning_type;
            }

            public int getStatus() {
                return status;
            }

            public void setStatus(int status) {
                this.status = status;
            }
        }
    }
}
