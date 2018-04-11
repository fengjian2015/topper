package com.bclould.tocotalk.model;

/**
 * Created by GA on 2017/10/31.
 */

import java.util.List;

public class MyAssetsInfo {


    /**
     * status : 1
     * data : [{"id":27,"name":"USDT","logo":"https://topperex.s3-ap-northeast-1.amazonaws.com/exchange/gLauR2QEsSnZwKf5h39y4LA0B7Chz4zr8s4Pslqr.png","display":"USDT","can_in":2,"can_out":1,"can_trans":2,"status":2,"wallet":"","over":"7213.98","lock":"0","total":"7213.98","currency":"CNY","number":45336.26},{"id":29,"name":"TPX","logo":"https://topperex.s3.ap-northeast-1.amazonaws.com/exchange/JPYAkJfoA61BNkKuuKRu1FGn74rDCxgaKSZVKfgF.png","display":"TPX","can_in":2,"can_out":1,"can_trans":2,"status":2,"wallet":"","over":"99","lock":"0","total":"99","currency":"CNY","number":622.16},"..."]
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
         * can_in : 2
         * can_out : 1
         * can_trans : 2
         * status : 2
         * wallet :
         * over : 7213.98
         * lock : 0
         * total : 7213.98
         * currency : CNY
         * number : 45336.26
         */

        private int id;
        private String name;
        private String logo;
        private String display;
        private int can_in;
        private int can_out;
        private int can_trans;
        private int status;
        private String wallet;
        private String over;
        private String lock;
        private String total;
        private String currency;
        private double number;

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

        public int getCan_in() {
            return can_in;
        }

        public void setCan_in(int can_in) {
            this.can_in = can_in;
        }

        public int getCan_out() {
            return can_out;
        }

        public void setCan_out(int can_out) {
            this.can_out = can_out;
        }

        public int getCan_trans() {
            return can_trans;
        }

        public void setCan_trans(int can_trans) {
            this.can_trans = can_trans;
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

        public String getCurrency() {
            return currency;
        }

        public void setCurrency(String currency) {
            this.currency = currency;
        }

        public double getNumber() {
            return number;
        }

        public void setNumber(double number) {
            this.number = number;
        }
    }
}
