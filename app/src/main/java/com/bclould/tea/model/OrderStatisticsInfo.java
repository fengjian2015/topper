package com.bclould.tea.model;

/**
 * Created by GA on 2018/5/14.
 */

public class OrderStatisticsInfo {

    /**
     * status : 1
     * data : {"sale_sum":"413.2","buy_sum":"35.01","total_trans":16,"all":83,"processing":0,"cancel":120,"complete":28,"abnormal":9}
     */

    private int status;
    private DataBean data;

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
         * sale_sum : 413.2
         * buy_sum : 35.01
         * total_trans : 16
         * all : 83
         * processing : 0
         * cancel : 120
         * complete : 28
         * abnormal : 9
         */

        private String sale_sum;
        private String buy_sum;
        private int total_trans;
        private int all;
        private int processing;
        private int cancel;
        private int complete;
        private int abnormal;

        public String getSale_sum() {
            return sale_sum;
        }

        public void setSale_sum(String sale_sum) {
            this.sale_sum = sale_sum;
        }

        public String getBuy_sum() {
            return buy_sum;
        }

        public void setBuy_sum(String buy_sum) {
            this.buy_sum = buy_sum;
        }

        public int getTotal_trans() {
            return total_trans;
        }

        public void setTotal_trans(int total_trans) {
            this.total_trans = total_trans;
        }

        public int getAll() {
            return all;
        }

        public void setAll(int all) {
            this.all = all;
        }

        public int getProcessing() {
            return processing;
        }

        public void setProcessing(int processing) {
            this.processing = processing;
        }

        public int getCancel() {
            return cancel;
        }

        public void setCancel(int cancel) {
            this.cancel = cancel;
        }

        public int getComplete() {
            return complete;
        }

        public void setComplete(int complete) {
            this.complete = complete;
        }

        public int getAbnormal() {
            return abnormal;
        }

        public void setAbnormal(int abnormal) {
            this.abnormal = abnormal;
        }
    }
}
