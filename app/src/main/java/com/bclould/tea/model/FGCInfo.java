package com.bclould.tea.model;

import java.util.List;

/**
 * Created by GIjia on 2018/11/10.
 */

public class FGCInfo {

    /**
     * status : 1
     * data : {"fgc_num":"39.98030089","usd_num":1000,"rate":0.34,"x":["11-05","11-06","11-07","11-08","11-09","11-12","11-13"],"y":["275.950","276.450","276.700","280.550","279.750","278.800","276.600"],"record":[{"id":4,"user_id":603,"coin_number":"2","fgc_number":"0.05015045","rate":"39.88","created_at":"2018-11-10 16:09:44"},{"id":3,"user_id":603,"coin_number":"1","fgc_number":"0.02507522","rate":"39.88","created_at":"2018-11-10 16:09:36"},{"id":2,"user_id":603,"coin_number":"1","fgc_number":"0.02507522","rate":"39.88","created_at":"2018-11-10 16:09:04"},{"id":1,"user_id":603,"coin_number":"1","fgc_number":"39.88","rate":"39.88","created_at":"2018-11-10 15:58:48"}]}
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
         * fgc_num : 39.98030089
         * usd_num : 1000
         * rate : 0.34
         * x : ["11-05","11-06","11-07","11-08","11-09","11-12","11-13"]
         * y : ["275.950","276.450","276.700","280.550","279.750","278.800","276.600"]
         * record : [{"id":4,"user_id":603,"coin_number":"2","fgc_number":"0.05015045","rate":"39.88","created_at":"2018-11-10 16:09:44"},{"id":3,"user_id":603,"coin_number":"1","fgc_number":"0.02507522","rate":"39.88","created_at":"2018-11-10 16:09:36"},{"id":2,"user_id":603,"coin_number":"1","fgc_number":"0.02507522","rate":"39.88","created_at":"2018-11-10 16:09:04"},{"id":1,"user_id":603,"coin_number":"1","fgc_number":"39.88","rate":"39.88","created_at":"2018-11-10 15:58:48"}]
         */

        private String fgc_num;
        private int usd_num;
        private double rate;
        private List<String> x;
        private List<Float> y;
        private List<RecordBean> record;

        public List<Float> getY() {
            return y;
        }

        public void setY(List<Float> y) {
            this.y = y;
        }

        public String getFgc_num() {
            return fgc_num;
        }

        public void setFgc_num(String fgc_num) {
            this.fgc_num = fgc_num;
        }

        public int getUsd_num() {
            return usd_num;
        }

        public void setUsd_num(int usd_num) {
            this.usd_num = usd_num;
        }

        public double getRate() {
            return rate;
        }

        public void setRate(double rate) {
            this.rate = rate;
        }

        public List<String> getX() {
            return x;
        }

        public void setX(List<String> x) {
            this.x = x;
        }


        public List<RecordBean> getRecord() {
            return record;
        }

        public void setRecord(List<RecordBean> record) {
            this.record = record;
        }

        public static class RecordBean {
            /**
             * id : 4
             * user_id : 603
             * coin_number : 2
             * fgc_number : 0.05015045
             * rate : 39.88
             * created_at : 2018-11-10 16:09:44
             */

            private int id;
            private int user_id;
            private String coin_number;
            private String fgc_number;
            private String rate;
            private String created_at;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public int getUser_id() {
                return user_id;
            }

            public void setUser_id(int user_id) {
                this.user_id = user_id;
            }

            public String getCoin_number() {
                return coin_number;
            }

            public void setCoin_number(String coin_number) {
                this.coin_number = coin_number;
            }

            public String getFgc_number() {
                return fgc_number;
            }

            public void setFgc_number(String fgc_number) {
                this.fgc_number = fgc_number;
            }

            public String getRate() {
                return rate;
            }

            public void setRate(String rate) {
                this.rate = rate;
            }

            public String getCreated_at() {
                return created_at;
            }

            public void setCreated_at(String created_at) {
                this.created_at = created_at;
            }
        }
    }
}
