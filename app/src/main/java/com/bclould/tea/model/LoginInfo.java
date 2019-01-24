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
        private int fingerprint;
        private int gesture;
        private int is_update;
        private String gesture_number;
        private int country_id;
        private String alipay_uuid;
        private boolean bind_ftc;
        private String email;
        private String mobile;
        private boolean gc_delivery;

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public boolean isGc_delivery() {
            return gc_delivery;
        }

        public void setGc_delivery(boolean gc_delivery) {
            this.gc_delivery = gc_delivery;
        }

        public boolean isBind_ftc() {
            return bind_ftc;
        }

        public void setBind_ftc(boolean bind_ftc) {
            this.bind_ftc = bind_ftc;
        }

        public String getAlipay_uuid() {
            return alipay_uuid;
        }

        public void setAlipay_uuid(String alipay_uuid) {
            this.alipay_uuid = alipay_uuid;
        }

        public int getCountry_id() {
            return country_id;
        }

        public void setCountry_id(int country_id) {
            this.country_id = country_id;
        }

        public String getGesture_number() {
            return gesture_number;
        }

        public void setGesture_number(String gesture_number) {
            this.gesture_number = gesture_number;
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

        public int getIs_update() {
            return is_update;
        }

        public void setIs_update(int is_update) {
            this.is_update = is_update;
        }

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
