package com.bclould.tea.model;

import java.util.List;

/**
 * Created by GA on 2018/3/27.
 */

public class TransferListInfo {

    /**
     * status : 1
     * data : [{"id":19632,"user_id":41,"log_id":0,"type":"竞猜","coin_name":"DOGE","number":"2.204","created_at":"2018-11-09 10:11:18","data_arr":{"bet_id":21,"period_qty":8},"type_number":19,"type_desc":"竞猜抽成"}]
     */

    private int status;
    private List<DataBean> data;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * id : 19632
         * user_id : 41
         * log_id : 0
         * type : 竞猜
         * coin_name : DOGE
         * number : 2.204
         * created_at : 2018-11-09 10:11:18
         * data_arr : {"bet_id":21,"period_qty":8}
         * type_number : 19
         * type_desc : 竞猜抽成
         */

        private int id;
        private int user_id;
        private int log_id;
        private String type;
        private String coin_name;
        private String number;
        private String created_at;
        private DataArrBean data_arr;
        private int type_number;
        private String type_desc;

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

        public int getLog_id() {
            return log_id;
        }

        public void setLog_id(int log_id) {
            this.log_id = log_id;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getCoin_name() {
            return coin_name;
        }

        public void setCoin_name(String coin_name) {
            this.coin_name = coin_name;
        }

        public String getNumber() {
            return number;
        }

        public void setNumber(String number) {
            this.number = number;
        }

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }

        public DataArrBean getData_arr() {
            return data_arr;
        }

        public void setData_arr(DataArrBean data_arr) {
            this.data_arr = data_arr;
        }

        public int getType_number() {
            return type_number;
        }

        public void setType_number(int type_number) {
            this.type_number = type_number;
        }

        public String getType_desc() {
            return type_desc;
        }

        public void setType_desc(String type_desc) {
            this.type_desc = type_desc;
        }

        public static class DataArrBean {
            /**
             * bet_id : 21
             * period_qty : 8
             */

            private int bet_id;
            private int period_qty;

            public int getBet_id() {
                return bet_id;
            }

            public void setBet_id(int bet_id) {
                this.bet_id = bet_id;
            }

            public int getPeriod_qty() {
                return period_qty;
            }

            public void setPeriod_qty(int period_qty) {
                this.period_qty = period_qty;
            }
        }
    }
}
