package com.bclould.tocotalk.model;

/**
 * Created by GA on 2017/10/31.
 */

import java.util.List;

/**
 * ltc : {"id":2,"name":"ltc","logo":"https://www.yuanbao.com/images/coin/coin_ltc.png","display":"莱特币","status":1,"wallet":"获取失败！","over":"99800","lock":"200！","total":"10000"}
 */
public class MyAssetsInfo {
    /**
     * status : 1
     * data : [{"id":2,"name":"LTC","logo":"https://www.bclould.com:8112/images/coin/coin_ltc.png","display":"莱特币","status":1,"wallet":"TSmU4YdFiSdk34biqsKmtntatN2FUAbkmh","over":"0","lock":"0","total":"0"},{"id":3,"name":"DOGE","logo":"https://www.bclould.com:8112/images/coin/coin_doge.png","display":"狗狗币","status":1,"wallet":"TDefUsZjVrCf6Em1rfhNyMMk2RxMa1PNRD","over":"0","lock":"0","total":"0"}]
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
         * name : LTC
         * logo : https://www.bclould.com:8112/images/coin/coin_ltc.png
         * display : 莱特币
         * status : 1
         * wallet : TSmU4YdFiSdk34biqsKmtntatN2FUAbkmh
         * over : 0
         * lock : 0
         * total : 0
         */

        private int id;
        private String name;
        private String logo;
        private String display;
        private int status;
        private String wallet;
        private String over;
        private String lock;
        private String total;

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

        public String getDisplay() {
            return display;
        }

        public void setDisplay(String display) {
            this.display = display;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getWallet() {
            return wallet;
        }

        public void setWallet(String wallet) {
            this.wallet = wallet;
        }

        public String getOver() {
            return over;
        }

        public void setOver(String over) {
            this.over = over;
        }

        public String getLock() {
            return lock;
        }

        public void setLock(String lock) {
            this.lock = lock;
        }

        public String getTotal() {
            return total;
        }

        public void setTotal(String total) {
            this.total = total;
        }
    }
}
