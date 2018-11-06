package com.bclould.tea.model;

import java.util.List;

/**
 * Created by GIjia on 2018/10/12.
 */

public class FinancialInfo {
    /**
     * status : 1
     * data : {"total":"1","over_num":"1","product_lists":[{"id":1,"title":"活期","save_min_number":100,"income_rate":"+9.00%"},{"id":2,"title":"3个月定期","save_min_number":100,"income_rate":"+9.00%"},{"id":3,"title":"6个月定期","save_min_number":100,"income_rate":"+9.00%"},{"id":4,"title":"12个月定期","save_min_number":100,"income_rate":"+9.00%"}],"income_lists":[{"title":"活期","number":"0","income":"0"},{"title":"3个月定期","number":"0","income":"0"},{"title":"6个月定期","number":"0","income":"0"},{"title":"12个月定期","number":"0","income":"0"}]}
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
         * total : 1
         * over_num : 1
         * product_lists : [{"id":1,"title":"活期","save_min_number":100,"income_rate":"+9.00%"},{"id":2,"title":"3个月定期","save_min_number":100,"income_rate":"+9.00%"},{"id":3,"title":"6个月定期","save_min_number":100,"income_rate":"+9.00%"},{"id":4,"title":"12个月定期","save_min_number":100,"income_rate":"+9.00%"}]
         * income_lists : [{"title":"活期","number":"0","income":"0"},{"title":"3个月定期","number":"0","income":"0"},{"title":"6个月定期","number":"0","income":"0"},{"title":"12个月定期","number":"0","income":"0"}]
         */

        private String total;
        private String over_num;
        private String yesterday_income;
        private List<ProductListsBean> product_lists;
        private List<IncomeListsBean> income_lists;

        public String getYesterday_income() {
            return yesterday_income;
        }

        public void setYesterday_income(String yesterday_income) {
            this.yesterday_income = yesterday_income;
        }

        public String getTotal() {
            return total;
        }

        public void setTotal(String total) {
            this.total = total;
        }

        public String getOver_num() {
            return over_num;
        }

        public void setOver_num(String over_num) {
            this.over_num = over_num;
        }

        public List<ProductListsBean> getProduct_lists() {
            return product_lists;
        }

        public void setProduct_lists(List<ProductListsBean> product_lists) {
            this.product_lists = product_lists;
        }

        public List<IncomeListsBean> getIncome_lists() {
            return income_lists;
        }

        public void setIncome_lists(List<IncomeListsBean> income_lists) {
            this.income_lists = income_lists;
        }

        public static class ProductListsBean {
            /**
             * id : 1
             * title : 活期
             * save_min_number : 100
             * income_rate : +9.00%
             */

            private int id;
            private String title;
            private int save_min_number;
            private String income_rate;
            private int lock_day;
            private double rate_value;
            private String income_rate_value;

            public double getRate_value() {
                return rate_value;
            }

            public void setRate_value(double rate_value) {
                this.rate_value = rate_value;
            }

            public String getIncome_rate_value() {
                return income_rate_value;
            }

            public void setIncome_rate_value(String income_rate_value) {
                this.income_rate_value = income_rate_value;
            }

            public int getLock_day() {
                return lock_day;
            }

            public void setLock_day(int lock_day) {
                this.lock_day = lock_day;
            }

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public int getSave_min_number() {
                return save_min_number;
            }

            public void setSave_min_number(int save_min_number) {
                this.save_min_number = save_min_number;
            }

            public String getIncome_rate() {
                return income_rate;
            }

            public void setIncome_rate(String income_rate) {
                this.income_rate = income_rate;
            }
        }

        public static class IncomeListsBean {
            /**
             * title : 活期
             * number : 0
             * income : 0
             */

            private String title;
            private String number;
            private String income;

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getNumber() {
                return number;
            }

            public void setNumber(String number) {
                this.number = number;
            }

            public String getIncome() {
                return income;
            }

            public void setIncome(String income) {
                this.income = income;
            }
        }
    }
}
