package com.bclould.tea.model;

/**
 * Created by GIjia on 2018/9/25.
 */

public class BindingInfo {
    /**
     * status : 1
     * data : {"email":"","total_asset":"","released":"","fund":""}
     */

    private int status;
    private DataBean data;
    private String message;

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
         * email :
         * total_asset :
         * released :
         * fund :
         */

        private String email;
        private String total_asset;
        private String released;
        private String fund;

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getTotal_asset() {
            return total_asset;
        }

        public void setTotal_asset(String total_asset) {
            this.total_asset = total_asset;
        }

        public String getReleased() {
            return released;
        }

        public void setReleased(String released) {
            this.released = released;
        }

        public String getFund() {
            return fund;
        }

        public void setFund(String fund) {
            this.fund = fund;
        }
    }
}
