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
     * data : [{"id":27,"name":"USDT","logo":"https://topperex.s3-ap-northeast-1.amazonaws.com/exchange/gLauR2QEsSnZwKf5h39y4LA0B7Chz4zr8s4Pslqr.png","display":"USDT","status":2,"wallet":"TXuJQCojvB6ve7XASSgNpxNNXGSPPUzY96","over":"7213.98","lock":"0","total":"7213.98","number":45479.82,"currency":"CNY"},{"id":29,"name":"TPX","logo":"https://topperex.s3.ap-northeast-1.amazonaws.com/exchange/JPYAkJfoA61BNkKuuKRu1FGn74rDCxgaKSZVKfgF.png","display":"TPX","status":2,"wallet":"TWKFV96U9jyqPV2kmMU7GhwHDGizNY59fH","over":"100","lock":"0","total":"100","number":630.43,"currency":"CNY"},"..."]
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
         * id : 27
         * name : USDT
         * logo : https://topperex.s3-ap-northeast-1.amazonaws.com/exchange/gLauR2QEsSnZwKf5h39y4LA0B7Chz4zr8s4Pslqr.png
         * display : USDT
         * status : 2
         * wallet : TXuJQCojvB6ve7XASSgNpxNNXGSPPUzY96
         * over : 7213.98
         * lock : 0
         * total : 7213.98
         * number : 45479.82
         * currency : CNY
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
        private double number;
        private String currency;

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

        public double getNumber() {
            return number;
        }

        public void setNumber(double number) {
            this.number = number;
        }

        public String getCurrency() {
            return currency;
        }

        public void setCurrency(String currency) {
            this.currency = currency;
        }
    }
}
