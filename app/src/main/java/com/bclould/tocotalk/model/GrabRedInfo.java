package com.bclould.tocotalk.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by GA on 2018/1/15.
 */

public class GrabRedInfo implements Serializable {

    /**
     * status : 0
     * message : 自己的红包
     * data : {"send_rp_user_name":"xihongwei","total_money":"5","intro":"发红包啦","coin_name":"BTC","rp_number":3,"log":[{"name":"xihongwei","money":"2.34","id":4,"time":1516008671},{"name":"xihongwei","money":"2.16","id":5,"time":1516008727},{"name":"xihongwei","money":"0.5","id":6,"time":1516008736}]}
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

    public class DataBean implements Serializable{
        /**
         * send_rp_user_name : xihongwei
         * total_money : 5
         * intro : 发红包啦
         * coin_name : BTC
         * rp_number : 3
         * log : [{"name":"xihongwei","money":"2.34","id":4,"time":1516008671},{"name":"xihongwei","money":"2.16","id":5,"time":1516008727},{"name":"xihongwei","money":"0.5","id":6,"time":1516008736}]
         */

        private String send_rp_user_name;
        private String total_money;
        private String intro;
        private String coin_name;
        private int rp_number;
        private List<LogBean> log;

        public String getSend_rp_user_name() {
            return send_rp_user_name;
        }

        public void setSend_rp_user_name(String send_rp_user_name) {
            this.send_rp_user_name = send_rp_user_name;
        }

        public String getTotal_money() {
            return total_money;
        }

        public void setTotal_money(String total_money) {
            this.total_money = total_money;
        }

        public String getIntro() {
            return intro;
        }

        public void setIntro(String intro) {
            this.intro = intro;
        }

        public String getCoin_name() {
            return coin_name;
        }

        public void setCoin_name(String coin_name) {
            this.coin_name = coin_name;
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

        public class LogBean implements Serializable{
            /**
             * name : xihongwei
             * money : 2.34
             * id : 4
             * time : 1516008671
             */

            private String name;
            private String money;
            private int id;
            private int is_good;

            public int getIs_good() {
                return is_good;
            }

            public void setIs_good(int is_good) {
                this.is_good = is_good;
            }

            private String time;

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getMoney() {
                return money;
            }

            public void setMoney(String money) {
                this.money = money;
            }

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getTime() {
                return time;
            }

            public void setTime(String time) {
                this.time = time;
            }
        }
    }
}
