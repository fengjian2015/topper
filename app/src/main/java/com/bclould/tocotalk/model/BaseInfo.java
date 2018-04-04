package com.bclould.tocotalk.model;

/**
 * Created by GA on 2017/10/30.
 */

public class BaseInfo {


    /**
     * status : 1
     * message : 成功!
     * data : {"id":1}
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

    public class DataBean {
        /**
         * id : 1
         */

        private int id;
        private String address;
        private String url;
        private String coin_name;
        private String coin_id;
        private String number;
        private String mark;
        private String total;
        private String USDT;
        private String CNY;
        private String buy_price;
        private String sale_price;
        private String trend;

        public String getBuy_price() {
            return buy_price;
        }

        public void setBuy_price(String buy_price) {
            this.buy_price = buy_price;
        }

        public String getSale_price() {
            return sale_price;
        }

        public void setSale_price(String sale_price) {
            this.sale_price = sale_price;
        }

        public String getTrend() {
            return trend;
        }

        public void setTrend(String trend) {
            this.trend = trend;
        }

        public String getUSDT() {
            return USDT;
        }

        public void setUSDT(String USDT) {
            this.USDT = USDT;
        }

        public String getCNY() {
            return CNY;
        }

        public void setCNY(String CNY) {
            this.CNY = CNY;
        }

        public String getTotal() {
            return total;
        }

        public void setTotal(String total) {
            this.total = total;
        }

        public String getRemark() {
            return mark;
        }

        public void setRemark(String remark) {
            this.mark = remark;
        }

        public String getCoin_name() {
            return coin_name;
        }

        public void setCoin_name(String coin_name) {
            this.coin_name = coin_name;
        }

        public String getCoin_id() {
            return coin_id;
        }

        public void setCoin_id(String coin_id) {
            this.coin_id = coin_id;
        }

        public String getNumber() {
            return number;
        }

        public void setNumber(String number) {
            this.number = number;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }
}
