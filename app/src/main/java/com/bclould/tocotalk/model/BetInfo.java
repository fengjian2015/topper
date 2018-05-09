package com.bclould.tocotalk.model;

import java.util.List;

/**
 * Created by GA on 2018/4/25.
 */

public class BetInfo {

    /**
     * status : 1
     * message : 投注成功!
     * data : {"prize_pool_number":"3.26371294","coin_number":2,"list":[{"bet_number":"22:30:44:5Q","created_at":"2018-05-09 15:08:51","bonus_number":"","winning_type":"1","status":1}]}
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
         * prize_pool_number : 3.26371294
         * coin_number : 2
         * list : [{"bet_number":"22:30:44:5Q","created_at":"2018-05-09 15:08:51","bonus_number":"","winning_type":"1","status":1}]
         */

        private String prize_pool_number;
        private int coin_number;
        private List<ListBean> list;

        public String getPrize_pool_number() {
            return prize_pool_number;
        }

        public void setPrize_pool_number(String prize_pool_number) {
            this.prize_pool_number = prize_pool_number;
        }

        public int getCoin_number() {
            return coin_number;
        }

        public void setCoin_number(int coin_number) {
            this.coin_number = coin_number;
        }

        public List<ListBean> getList() {
            return list;
        }

        public void setList(List<ListBean> list) {
            this.list = list;
        }

        public static class ListBean {
            /**
             * bet_number : 22:30:44:5Q
             * created_at : 2018-05-09 15:08:51
             * bonus_number :
             * winning_type : 1
             * status : 1
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
