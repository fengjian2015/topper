package com.bclould.tea.model;

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
    private int type;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

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
        private String currency;
        private String title;
        private String desc;
        private String bet_coin;
        private String name;
        private String merchant_name;
        private String toco_id;
        private String coordinate;
        private int fingerprint;
        private int gesture;
        private String country;
        private String response;

        public String getResponse() {
            return response;
        }

        public void setResponse(String response) {
            this.response = response;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public String getCoordinate() {
            return coordinate;
        }

        public void setCoordinate(String coordinate) {
            this.coordinate = coordinate;
        }

        public int getFingerprint() {
            return fingerprint;
        }

        public void setFingerprint(int fingerprint) {
            this.fingerprint = fingerprint;
        }

        public int getGesture() {
            return gesture;
        }

        public void setGesture(int gesture) {
            this.gesture = gesture;
        }

        public String getToco_id() {
            return toco_id;
        }

        public void setToco_id(String toco_id) {
            this.toco_id = toco_id;
        }

        public String getMerchant_name() {
            return merchant_name;
        }

        public void setMerchant_name(String merchant_name) {
            this.merchant_name = merchant_name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAd_cost() {
            return ad_cost;
        }

        public void setAd_cost(String ad_cost) {
            this.ad_cost = ad_cost;
        }

        private String ad_cost;

        public String getMark() {
            return mark;
        }

        public void setMark(String mark) {
            this.mark = mark;
        }

        private int type;
        private String xmpp;

        public String getXmpp() {
            return xmpp;
        }

        public void setXmpp(String xmpp) {
            this.xmpp = xmpp;
        }

        public String getBet_coin() {
            return bet_coin;
        }

        public void setBet_coin(String bet_coin) {
            this.bet_coin = bet_coin;
        }


        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        private String rate;

        public String getCurrency() {
            return currency;
        }

        public void setCurrency(String currency) {
            this.currency = currency;
        }

        public String getRate() {
            return rate;
        }

        public void setRate(String rate) {
            this.rate = rate;
        }

        private String usd;

        public String getUsd() {
            return usd;
        }

        public void setUsd(String usd) {
            this.usd = usd;
        }

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
