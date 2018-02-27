package com.bclould.tocotalk.model;

import java.util.List;

/**
 * Created by GA on 2018/2/26.
 */

public class CoinInfo {

    /**
     * status : 1
     * data : [{"logo":"https://www.bclould.com:8112/images/coin/coin_ltc.png","name":"LTC","coin_over":"0"},{"logo":"https://www.bclould.com:8112/images/coin/coin_doge.png","name":"DOGE","coin_over":"0"},{"logo":"https://www.bclould.com:8112/images/coin/coin_zec.png","name":"ZEC","coin_over":"0"},{"logo":"https://www.bclould.com:8112/images/coin/coin_lsk.png","name":"LSK","coin_over":"0"},{"logo":"https://www.bclould.com:8112/images/coin/coin_maid.png","name":"MAID","coin_over":"0"},{"logo":"https://www.bclould.com:8112/images/coin/coin_shc.png","name":"SHC","coin_over":"0"},{"logo":"https://www.bclould.com:8112/images/coin/dbb8c7597187cf99577350caee0d6387f46c6891.png","name":"BTC","coin_over":"0"},{"logo":"https://www.bclould.com:8112/images/coin/coin_ans.png","name":"ANS","coin_over":"0"},{"logo":"https://www.bclould.com:8112/images/coin/kuOFxXaZKWNVZJ3yTS1bNUR4leHd4e8gbpLheqmZ.png","name":"TPC","coin_over":"0.059"}]
     */

    private int status;
    private List<DataBean> data;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * logo : https://www.bclould.com:8112/images/coin/coin_ltc.png
         * name : LTC
         * coin_over : 0
         */

        private String logo;
        private String name;
        private String coin_over;

        public String getLogo() {
            return logo;
        }

        public void setLogo(String logo) {
            this.logo = logo;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCoin_over() {
            return coin_over;
        }

        public void setCoin_over(String coin_over) {
            this.coin_over = coin_over;
        }
    }
}
