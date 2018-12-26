package com.bclould.tea.model;

import java.util.List;

/**
 * Created by GIjia on 2018/10/12.
 */

public class FinancialCoinInfo {
    /**
     * status : 1
     * message : success
     * data : [{"id":60,"name":"FTC","logo":"https://topper-bucket.oss-cn-shenzhen.aliyuncs.com/c6b161f36d13f75f443dbb8f7fb6444cf0c8d08a.jpg","coin_over":0.4,"rate":"+9.00%"}]
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
         * id : 60
         * name : FTC
         * logo : https://topper-bucket.oss-cn-shenzhen.aliyuncs.com/c6b161f36d13f75f443dbb8f7fb6444cf0c8d08a.jpg
         * coin_over : 0.4
         * rate : +9.00%
         */

        private int id;
        private String name;
        private String logo;
        private double coin_over;
        private String rate;
        private double rate_value;

        public double getRate_value() {
            return rate_value;
        }

        public void setRate_value(double rate_value) {
            this.rate_value = rate_value;
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

        public double getCoin_over() {
            return coin_over;
        }

        public void setCoin_over(double coin_over) {
            this.coin_over = coin_over;
        }

        public String getRate() {
            return rate;
        }

        public void setRate(String rate) {
            this.rate = rate;
        }
    }
}
