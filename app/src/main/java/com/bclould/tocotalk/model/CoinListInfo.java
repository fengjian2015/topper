package com.bclould.tocotalk.model;

import java.util.List;

/**
 * Created by GA on 2018/4/4.
 */

public class CoinListInfo {

    /**
     * status : 1
     * message : success
     * data : [{"id":1,"name":"CNY","logo":"https://hyhc.s3-ap-southeast-1.amazonaws.com/d875ea0c438bc5d4ed792b0957fa1c6e99e9c8e0.png/SRxeFft6sQ05vXCC5gir24o8B6gDzuwck0R9mXg2.png","coin_over":"100"},{"id":27,"name":"USDT","logo":"https://topperex.s3-ap-northeast-1.amazonaws.com/exchange/gLauR2QEsSnZwKf5h39y4LA0B7Chz4zr8s4Pslqr.png","coin_over":"100"},"..."]
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
         * id : 1
         * name : CNY
         * logo : https://hyhc.s3-ap-southeast-1.amazonaws.com/d875ea0c438bc5d4ed792b0957fa1c6e99e9c8e0.png/SRxeFft6sQ05vXCC5gir24o8B6gDzuwck0R9mXg2.png
         * coin_over : 100
         */

        private int id;
        private String name;
        private String logo;
        private String coin_over;

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
    }
}
