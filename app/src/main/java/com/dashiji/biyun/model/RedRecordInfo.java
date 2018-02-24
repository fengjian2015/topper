package com.dashiji.biyun.model;

import java.util.List;

/**
 * Created by GA on 2018/1/18.
 */

public class RedRecordInfo {


    /**
     * status : 1
     * message : ok
     * data : {"name":"xixixi","total_money":"1.2","rp_number":5,"log":[{"id":150,"coin_name":"BTC","total_money":"0.2","created_at":"2018-01-24 10:35:20","type":1,"rp_type":2,"name":"liaolinan2"}]}
     */

    private int status;
    private String message;
    private DataBean data;

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

    public static class DataBean {
        /**
         * name : xixixi
         * total_money : 1.2
         * rp_number : 5
         * log : [{"id":150,"coin_name":"BTC","total_money":"0.2","created_at":"2018-01-24 10:35:20","type":1,"rp_type":2,"name":"liaolinan2"}]
         */

        private String name;
        private String total_money;
        private int rp_number;
        private String max_money;
        private String most_coin;

        public String getMax_money() {
            return max_money;
        }

        public void setMax_money(String max_money) {
            this.max_money = max_money;
        }

        public String getMost_coin() {
            return most_coin;
        }

        public void setMost_coin(String most_coin) {
            this.most_coin = most_coin;
        }

        private List<LogBean> log;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getTotal_money() {
            return total_money;
        }

        public void setTotal_money(String total_money) {
            this.total_money = total_money;
        }

        public int getRp_number() {
            return rp_number;
        }

        public void setRp_number(int rp_number) {
            this.rp_number = rp_number;
        }

        public List<LogBean> getLog() {
            return log;
        }

        public void setLog(List<LogBean> log) {
            this.log = log;
        }

        public static class LogBean {
            /**
             * id : 150
             * coin_name : BTC
             * total_money : 0.2
             * created_at : 2018-01-24 10:35:20
             * type : 1
             * rp_type : 2
             * name : liaolinan2
             */

            private int id;
            private String coin_name;
            private String total_money;
            private String created_at;
            private int type;
            private int rp_type;
            private String name;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getCoin_name() {
                return coin_name;
            }

            public void setCoin_name(String coin_name) {
                this.coin_name = coin_name;
            }

            public String getTotal_money() {
                return total_money;
            }

            public void setTotal_money(String total_money) {
                this.total_money = total_money;
            }

            public String getCreated_at() {
                return created_at;
            }

            public void setCreated_at(String created_at) {
                this.created_at = created_at;
            }

            public int getType() {
                return type;
            }

            public void setType(int type) {
                this.type = type;
            }

            public int getRp_type() {
                return rp_type;
            }

            public void setRp_type(int rp_type) {
                this.rp_type = rp_type;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }
        }
    }
}
