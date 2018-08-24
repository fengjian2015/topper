package com.bclould.tea.model;

import java.util.List;

/**
 * Created by GA on 2018/8/24.
 */

public class RpRecordInfo {

    /**
     * status : 1
     * message : ok
     * data : {"top":{"name":"xihongwei","rp_number":81,"best_luck":"29.51","coin_number":[{"name":"FEF","number":131.89},{"name":"TPC","number":109.041},{"name":"DOGE","number":6.66666651},{"name":"BTC","number":4.8}]},"log":[{"id":753,"type":3,"created_at":"8-20","coin_name":"TPC","rp_type":1,"name":"xihongwei"}]}
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
         * top : {"name":"xihongwei","rp_number":81,"best_luck":"29.51","coin_number":[{"name":"FEF","number":131.89},{"name":"TPC","number":109.041},{"name":"DOGE","number":6.66666651},{"name":"BTC","number":4.8}]}
         * log : [{"id":753,"type":3,"created_at":"8-20","coin_name":"TPC","rp_type":1,"name":"xihongwei"}]
         */

        private TopBean top;
        private List<LogBean> log;

        public TopBean getTop() {
            return top;
        }

        public void setTop(TopBean top) {
            this.top = top;
        }

        public List<LogBean> getLog() {
            return log;
        }

        public void setLog(List<LogBean> log) {
            this.log = log;
        }

        public static class TopBean {
            /**
             * name : xihongwei
             * rp_number : 81
             * best_luck : 29.51
             * coin_number : [{"name":"FEF","number":131.89},{"name":"TPC","number":109.041},{"name":"DOGE","number":6.66666651},{"name":"BTC","number":4.8}]
             */

            private String name;
            private int rp_number;
            private String best_luck;
            private List<CoinNumberBean> coin_number;

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public int getRp_number() {
                return rp_number;
            }

            public void setRp_number(int rp_number) {
                this.rp_number = rp_number;
            }

            public String getBest_luck() {
                return best_luck;
            }

            public void setBest_luck(String best_luck) {
                this.best_luck = best_luck;
            }

            public List<CoinNumberBean> getCoin_number() {
                return coin_number;
            }

            public void setCoin_number(List<CoinNumberBean> coin_number) {
                this.coin_number = coin_number;
            }

            public static class CoinNumberBean {
                /**
                 * name : FEF
                 * number : 131.89
                 */

                private String name;
                private String number;

                public String getName() {
                    return name;
                }

                public void setName(String name) {
                    this.name = name;
                }

                public String getNumber() {
                    return number;
                }

                public void setNumber(String number) {
                    this.number = number;
                }
            }
        }

        public static class LogBean {
            /**
             * id : 753
             * type : 3
             * created_at : 8-20
             * coin_name : TPC
             * rp_type : 1
             * name : xihongwei
             */

            private int id;
            private int type;
            private String created_at;
            private String money;
            private String coin_name;
            private int rp_type;
            private int status;
            private String name;
            private String desc;

            public String getMoney() {
                return money;
            }

            public void setMoney(String money) {
                this.money = money;
            }

            public int getStatus() {
                return status;
            }

            public void setStatus(int status) {
                this.status = status;
            }

            public String getDesc() {
                return desc;
            }

            public void setDesc(String desc) {
                this.desc = desc;
            }

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public int getType() {
                return type;
            }

            public void setType(int type) {
                this.type = type;
            }

            public String getCreated_at() {
                return created_at;
            }

            public void setCreated_at(String created_at) {
                this.created_at = created_at;
            }

            public String getCoin_name() {
                return coin_name;
            }

            public void setCoin_name(String coin_name) {
                this.coin_name = coin_name;
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
