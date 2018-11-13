package com.bclould.tea.model;

import java.util.List;

/**
 * Created by GIjia on 2018/11/10.
 */

public class FGCInfo {
    /**
     * status : 1
     * data : {"rate":45.3,"usd_num":100.0244,"fgc_num":100,"record":[{"user_id":23,"coin_number":1.5,"fgc_number":250.5,"rate":45.5,"created_at":"2018-11-10"}]}
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
         * rate : 45.3
         * usd_num : 100.0244
         * fgc_num : 100
         * record : [{"user_id":23,"coin_number":1.5,"fgc_number":250.5,"rate":45.5,"created_at":"2018-11-10"}]
         */

        private double rate;
        private double usd_num;
        private double fgc_num;
        private List<RecordBean> record;

        public double getFgc_num() {
            return fgc_num;
        }

        public void setFgc_num(double fgc_num) {
            this.fgc_num = fgc_num;
        }

        public double getRate() {
            return rate;
        }

        public void setRate(double rate) {
            this.rate = rate;
        }

        public double getUsd_num() {
            return usd_num;
        }

        public void setUsd_num(double usd_num) {
            this.usd_num = usd_num;
        }



        public List<RecordBean> getRecord() {
            return record;
        }

        public void setRecord(List<RecordBean> record) {
            this.record = record;
        }

        public static class RecordBean {
            /**
             * user_id : 23
             * coin_number : 1.5
             * fgc_number : 250.5
             * rate : 45.5
             * created_at : 2018-11-10
             */

            private int user_id;
            private double coin_number;
            private double fgc_number;
            private double rate;
            private String created_at;

            public int getUser_id() {
                return user_id;
            }

            public void setUser_id(int user_id) {
                this.user_id = user_id;
            }

            public double getCoin_number() {
                return coin_number;
            }

            public void setCoin_number(double coin_number) {
                this.coin_number = coin_number;
            }

            public double getFgc_number() {
                return fgc_number;
            }

            public void setFgc_number(double fgc_number) {
                this.fgc_number = fgc_number;
            }

            public double getRate() {
                return rate;
            }

            public void setRate(double rate) {
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
