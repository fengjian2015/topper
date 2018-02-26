package com.dashiji.biyun.model;

import java.util.List;

/**
 * Created by GA on 2018/2/26.
 */

public class CoinInfo {

    /**
     * status : 1
     * data : [{"name":"LTC","coin_over":"0"},{"name":"DOGE","coin_over":"0"},{"name":"ZEC","coin_over":"0"},{"name":"LSK","coin_over":"0"},{"name":"MAID","coin_over":"0"},{"name":"SHC","coin_over":"0"},{"name":"BTC","coin_over":"0"},{"name":"ANS","coin_over":"0"},{"name":"TPC","coin_over":"0.059"}]
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
         * name : LTC
         * coin_over : 0
         */

        private String name;
        private String coin_over;

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
