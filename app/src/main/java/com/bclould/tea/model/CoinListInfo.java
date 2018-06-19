package com.bclould.tea.model;

import java.util.List;

/**
 * Created by GA on 2018/4/4.
 */

public class CoinListInfo {

    /**
     * status : 1
     * message : success
     * data : [{"id":2,"name":"BTC","logo":"https://topperex.s3.ap-northeast-1.amazonaws.com/exchange/I4DOR2YWZ6DIkm2rVteK74oB4RglrEJVYri30uVQ.png","coin_over":"98.890388","out_otc":"0","out_exchange":"0"},{"id":28,"name":"TPC","logo":"https://topperex.s3.ap-northeast-1.amazonaws.com/exchange/kuOFxXaZKWNVZJ3yTS1bNUR4leHd4e8gbpLheqmZ.png","coin_over":"44","out_otc":"0","out_exchange":"0"}]
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
         * id : 2
         * name : BTC
         * logo : https://topperex.s3.ap-northeast-1.amazonaws.com/exchange/I4DOR2YWZ6DIkm2rVteK74oB4RglrEJVYri30uVQ.png
         * coin_over : 98.890388
         * out_otc : 0
         * out_exchange : 0
         */

        private int id;
        private String name;
        private String logo;
        private String coin_over;
        private String out_otc;
        private String in_otc;
        private String out_exchange;
        private String single_coin;

        public String getIn_otc() {
            return in_otc;
        }

        public void setIn_otc(String in_otc) {
            this.in_otc = in_otc;
        }

        private String bet_fee;

        public String getBet_fee() {
            return bet_fee;
        }

        public void setBet_fee(String bet_fee) {
            this.bet_fee = bet_fee;
        }

        public String getSingle_coin() {
            return single_coin;
        }

        public void setSingle_coin(String single_coin) {
            this.single_coin = single_coin;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getLogo() {
            return logo;
        }

        public void setLogo(String logo) {
            this.logo = logo;
        }

        public String getCoin_over() {
            return coin_over;
        }

        public void setCoin_over(String coin_over) {
            this.coin_over = coin_over;
        }

        public String getOut_otc() {
            return out_otc;
        }

        public void setOut_otc(String out_otc) {
            this.out_otc = out_otc;
        }

        public String getOut_exchange() {
            return out_exchange;
        }

        public void setOut_exchange(String out_exchange) {
            this.out_exchange = out_exchange;
        }
    }
}
