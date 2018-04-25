package com.bclould.tocotalk.model;

/**
 * Created by GA on 2018/4/25.
 */

public class BetInfo {

    /**
     * status : 1
     * message : 投注成功!
     * data : {"bet_number":"yt:ue:s8:71","created_at":"2018-04-25 17:18:45","bonus_number":0,"winning_type":"1","status":1}
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
         * bet_number : yt:ue:s8:71
         * created_at : 2018-04-25 17:18:45
         * bonus_number : 0
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
