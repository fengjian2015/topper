package com.bclould.tocotalk.model;

import java.util.List;

/**
 * Created by GA on 2018/4/25.
 */

public class GuessInfo {

    /**
     * status : 1
     * message : ok
     * data : {"id":2,"period_qty":23,"status":3,"created_at":"2018-05-07 16:45:57","coin_id":28,"password":"","user_name":"liaolinan2","current_people_number":5,"title":"ppppp","single_coin":"0.1","limit_number":"4.69394607","countdown":0,"coin_name":"TPC","win_number":"98_69_E4_A0","win_number_index":"239142,327999,85984,200727","win_number_hash":"34AEA8EABFC2F5C5D67A44E4BE2966EF955DDFCB463016AC75CD1047CA6D8AB9,E638373ED9DD4CC0D53436AA81095E86DED3F9470F4443601D4A9A27F7FA7269,E522DE35F5067D89DE43FBDF949D9278EA7F3B72E6B6DF4B08344FF303AB18E4,F165C60997EF36A1240DFD5BA2C12953BAA217A7114BCBCE14C3A683CDD32FA0","win_number_hash_url":"https://block.minerpro.club/block/34AEA8EABFC2F5C5D67A44E4BE2966EF955DDFCB463016AC75CD1047CA6D8AB9,https://block.minerpro.club/block/E638373ED9DD4CC0D53436AA81095E86DED3F9470F4443601D4A9A27F7FA7269,https://block.minerpro.club/block/E522DE35F5067D89DE43FBDF949D9278EA7F3B72E6B6DF4B08344FF303AB18E4,https://block.minerpro.club/block/F165C60997EF36A1240DFD5BA2C12953BAA217A7114BCBCE14C3A683CDD32FA0","prize_pool_number":"3.75515686","betList":[{"bet_number":"59:22:22:V0","created_at":"2018-05-07 19:45:11","bonus_number":"0","winning_type":"","status":2},{"bet_number":"71:53:85:1Y","created_at":"2018-05-07 19:45:11","bonus_number":"0","winning_type":"","status":2},{"bet_number":"44:86:63:6Z","created_at":"2018-05-07 19:45:11","bonus_number":"0","winning_type":"","status":2},{"bet_number":"44:81:54:QB","created_at":"2018-05-07 19:45:11","bonus_number":"0","winning_type":"","status":2},{"bet_number":"59:52:95:A4","created_at":"2018-05-07 19:45:11","bonus_number":"0","winning_type":"","status":2}],"over_count_num":0}
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
         * period_qty : 23
         * status : 3
         * created_at : 2018-05-07 16:45:57
         * coin_id : 28
         * password :
         * user_name : liaolinan2
         * current_people_number : 5
         * title : ppppp
         * single_coin : 0.1
         * limit_number : 4.69394607
         * countdown : 0
         * coin_name : TPC
         * win_number : 98_69_E4_A0
         * win_number_index : 239142,327999,85984,200727
         * win_number_hash : 34AEA8EABFC2F5C5D67A44E4BE2966EF955DDFCB463016AC75CD1047CA6D8AB9,E638373ED9DD4CC0D53436AA81095E86DED3F9470F4443601D4A9A27F7FA7269,E522DE35F5067D89DE43FBDF949D9278EA7F3B72E6B6DF4B08344FF303AB18E4,F165C60997EF36A1240DFD5BA2C12953BAA217A7114BCBCE14C3A683CDD32FA0
         * win_number_hash_url : https://block.minerpro.club/block/34AEA8EABFC2F5C5D67A44E4BE2966EF955DDFCB463016AC75CD1047CA6D8AB9,https://block.minerpro.club/block/E638373ED9DD4CC0D53436AA81095E86DED3F9470F4443601D4A9A27F7FA7269,https://block.minerpro.club/block/E522DE35F5067D89DE43FBDF949D9278EA7F3B72E6B6DF4B08344FF303AB18E4,https://block.minerpro.club/block/F165C60997EF36A1240DFD5BA2C12953BAA217A7114BCBCE14C3A683CDD32FA0
         * prize_pool_number : 3.75515686
         * betList : [{"bet_number":"59:22:22:V0","created_at":"2018-05-07 19:45:11","bonus_number":"0","winning_type":"","status":2},{"bet_number":"71:53:85:1Y","created_at":"2018-05-07 19:45:11","bonus_number":"0","winning_type":"","status":2},{"bet_number":"44:86:63:6Z","created_at":"2018-05-07 19:45:11","bonus_number":"0","winning_type":"","status":2},{"bet_number":"44:81:54:QB","created_at":"2018-05-07 19:45:11","bonus_number":"0","winning_type":"","status":2},{"bet_number":"59:52:95:A4","created_at":"2018-05-07 19:45:11","bonus_number":"0","winning_type":"","status":2}]
         * over_count_num : 0
         */

        private int id;
        private int period_qty;
        private int status;
        private String created_at;
        private int coin_id;
        private String password;
        private String user_name;
        private int current_people_number;
        private String title;
        private String single_coin;
        private String limit_number;
        private int countdown;
        private String coin_name;
        private String win_number;
        private String win_number_index;
        private String win_number_hash;
        private String win_number_hash_url;
        private String prize_pool_number;
        private int over_count_num;
        private int limit_people_number;

        public int getLimit_people_number() {
            return limit_people_number;
        }

        public void setLimit_people_number(int limit_people_number) {
            this.limit_people_number = limit_people_number;
        }

        private List<BetListBean> betList;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
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

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getUser_name() {
            return user_name;
        }

        public void setUser_name(String user_name) {
            this.user_name = user_name;
        }

        public int getCurrent_people_number() {
            return current_people_number;
        }

        public void setCurrent_people_number(int current_people_number) {
            this.current_people_number = current_people_number;
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

        public String getLimit_number() {
            return limit_number;
        }

        public void setLimit_number(String limit_number) {
            this.limit_number = limit_number;
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

        public String getWin_number() {
            return win_number;
        }

        public void setWin_number(String win_number) {
            this.win_number = win_number;
        }

        public String getWin_number_index() {
            return win_number_index;
        }

        public void setWin_number_index(String win_number_index) {
            this.win_number_index = win_number_index;
        }

        public String getWin_number_hash() {
            return win_number_hash;
        }

        public void setWin_number_hash(String win_number_hash) {
            this.win_number_hash = win_number_hash;
        }

        public String getWin_number_hash_url() {
            return win_number_hash_url;
        }

        public void setWin_number_hash_url(String win_number_hash_url) {
            this.win_number_hash_url = win_number_hash_url;
        }

        public String getPrize_pool_number() {
            return prize_pool_number;
        }

        public void setPrize_pool_number(String prize_pool_number) {
            this.prize_pool_number = prize_pool_number;
        }

        public int getOver_count_num() {
            return over_count_num;
        }

        public void setOver_count_num(int over_count_num) {
            this.over_count_num = over_count_num;
        }

        public List<BetListBean> getBetList() {
            return betList;
        }

        public void setBetList(List<BetListBean> betList) {
            this.betList = betList;
        }

        public static class BetListBean {
            /**
             * bet_number : 59:22:22:V0
             * created_at : 2018-05-07 19:45:11
             * bonus_number : 0
             * winning_type :
             * status : 2
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
