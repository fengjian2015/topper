package com.bclould.tea.model;

/**
 * Created by GA on 2018/1/16.
 */

public class LoginInfo {


    /**
     * status : 1
     * message : eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOjI5LCJpc3MiOiJodHRwczovL3d3dy5iY2xvdWxkLmNvbTo4MTEyL2FwaS9zaWduSW4iLCJpYXQiOjE1MTYwODM2NjYsImV4cCI6MTUxNjA4NzI2NiwibmJmIjoxNTE2MDgzNjY2LCJqdGkiOiJNUGhucUJRVnBIdVhpMVlNIn0.6gVG8x4RoYejsptTHeeo6-SjC1eZRrTS7uNW7TlZUc0
     * data : {"user_id":29}
     */

    private int status;
    private int type;
    private String message;
    private DataBean data;

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
         * user_id : 29
         */

        private int user_id;
        private String name;
        private String country;
        private String currency;
        private String xmpp;
        private String toco_id;

        public String getToco_id() {
            return toco_id;
        }

        public void setToco_id(String toco_id) {
            this.toco_id = toco_id;
        }

        public String getXmpp() {
            return xmpp;
        }

        public void setXmpp(String xmpp) {
            this.xmpp = xmpp;
        }

        public String getCurrency() {
            return currency;
        }

        public void setCurrency(String currency) {
            this.currency = currency;
        }

        private int validate_type;

        public int getValidate_type() {
            return validate_type;
        }

        public void setValidate_type(int validate_type) {
            this.validate_type = validate_type;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getUser_id() {
            return user_id;
        }

        public void setUser_id(int user_id) {
            this.user_id = user_id;
        }
    }
}
