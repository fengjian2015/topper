package com.bclould.tea.model;

import java.util.List;

/**
 * Created by GIjia on 2018/9/26.
 */

public class NodeInfo {
    /**
     * status : 1
     * data : {"total_income":"256,700.00","level":"共识节点","freed":"+12","income":"+21","dividend":"+300","total_performance":"100.00","node_income":"100.00","super_dividend":"100.00","main_super_dividend":"27,100.00","lists":[{"title":"超级节点","money":"2,566.34","created_at":"2018.01.01 20:00","number":"309.235"},{"title":"主节点","money":"2,566.34","created_at":"2018.01.01 20:00","number":"309.235"},{"title":"超级节点","money":"2,566.34","created_at":"2018.01.01 20:00","number":"309.235"},{"title":"主节点","money":"2,566.34","created_at":"2018.01.01 20:00","number":"309.235"}]}
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
         * total_income : 256,700.00
         * level : 共识节点
         * freed : +12
         * income : +21
         * dividend : +300
         * total_performance : 100.00
         * node_income : 100.00
         * super_dividend : 100.00
         * main_super_dividend : 27,100.00
         * lists : [{"title":"超级节点","money":"2,566.34","created_at":"2018.01.01 20:00","number":"309.235"},{"title":"主节点","money":"2,566.34","created_at":"2018.01.01 20:00","number":"309.235"},{"title":"超级节点","money":"2,566.34","created_at":"2018.01.01 20:00","number":"309.235"},{"title":"主节点","money":"2,566.34","created_at":"2018.01.01 20:00","number":"309.235"}]
         */

        private String total_income;
        private String level;
        private String freed;
        private String income;
        private String dividend;
        private String total_performance;
        private String node_income;
        private String super_dividend;
        private String main_super_dividend;
        private List<ListsBean> lists;

        public String getTotal_income() {
            return total_income;
        }

        public void setTotal_income(String total_income) {
            this.total_income = total_income;
        }

        public String getLevel() {
            return level;
        }

        public void setLevel(String level) {
            this.level = level;
        }

        public String getFreed() {
            return freed;
        }

        public void setFreed(String freed) {
            this.freed = freed;
        }

        public String getIncome() {
            return income;
        }

        public void setIncome(String income) {
            this.income = income;
        }

        public String getDividend() {
            return dividend;
        }

        public void setDividend(String dividend) {
            this.dividend = dividend;
        }

        public String getTotal_performance() {
            return total_performance;
        }

        public void setTotal_performance(String total_performance) {
            this.total_performance = total_performance;
        }

        public String getNode_income() {
            return node_income;
        }

        public void setNode_income(String node_income) {
            this.node_income = node_income;
        }

        public String getSuper_dividend() {
            return super_dividend;
        }

        public void setSuper_dividend(String super_dividend) {
            this.super_dividend = super_dividend;
        }

        public String getMain_super_dividend() {
            return main_super_dividend;
        }

        public void setMain_super_dividend(String main_super_dividend) {
            this.main_super_dividend = main_super_dividend;
        }

        public List<ListsBean> getLists() {
            return lists;
        }

        public void setLists(List<ListsBean> lists) {
            this.lists = lists;
        }

        public static class ListsBean {
            /**
             * title : 超级节点
             * money : 2,566.34
             * created_at : 2018.01.01 20:00
             * number : 309.235
             */

            private String title;
            private String money;
            private String created_at;
            private String number;

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getMoney() {
                return money;
            }

            public void setMoney(String money) {
                this.money = money;
            }

            public String getCreated_at() {
                return created_at;
            }

            public void setCreated_at(String created_at) {
                this.created_at = created_at;
            }

            public String getNumber() {
                return number;
            }

            public void setNumber(String number) {
                this.number = number;
            }
        }
    }
}
