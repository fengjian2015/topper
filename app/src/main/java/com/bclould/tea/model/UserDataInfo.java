package com.bclould.tea.model;

/**
 * Created by GA on 2018/6/22.
 */

public class UserDataInfo {


    /**
     * status : 1
     * data : {"merchant_name":"习红卫","avatar":"http://toco--bucket.oss-cn-shenzhen.aliyuncs.com/MERCHANT4Avatar.png?time=1529669655","coin_name":"TPC"}
     */

    private int status;
    private String message;
    private DataBean data;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * merchant_name : 习红卫
         * avatar : http://toco--bucket.oss-cn-shenzhen.aliyuncs.com/MERCHANT4Avatar.png?time=1529669655
         * coin_name : TPC
         */

        private String merchant_name;
        private String avatar;
        private String coin_name;
        private String remark;

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }

        public String getMerchant_name() {
            return merchant_name;
        }

        public void setMerchant_name(String merchant_name) {
            this.merchant_name = merchant_name;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public String getCoin_name() {
            return coin_name;
        }

        public void setCoin_name(String coin_name) {
            this.coin_name = coin_name;
        }
    }
}
