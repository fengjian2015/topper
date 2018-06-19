package com.bclould.tea.model;

import java.util.List;

/**
 * Created by GA on 2018/2/26.
 */

public class CoinInfo {


    /**
     * status : 1
     * data : [{"id":2,"logo":"https://www.bclould.com:8112/images/coin/coin_ltc.png","name":"LTC"},{"id":3,"logo":"https://www.bclould.com:8112/images/coin/coin_doge.png","name":"DOGE"},"..."]
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
         * id : 2
         * logo : https://www.bclould.com:8112/images/coin/coin_ltc.png
         * name : LTC
         */

        private int id;
        private String logo;
        private String name;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

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
    }
}
