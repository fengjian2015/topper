package com.bclould.tea.model;

import java.util.List;

/**
 * Created by GA on 2018/4/4.
 */

public class ExchangeOrderInfo {

    /**
     * status : 1
     * data : [{"id":42,"number":"1","price":"1.245","created_at":"2018-06-29 17:19:24","type":2,"status":1,"status_name":"等待處理"}]
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
         * id : 42
         * number : 1
         * price : 1.245
         * created_at : 2018-06-29 17:19:24
         * type : 2
         * status : 1
         * status_name : 等待處理
         */

        private int id;
        private String number;
        private String price;
        private String created_at;
        private int type;
        private int status;
        private String status_name;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getNumber() {
            return number;
        }

        public void setNumber(String number) {
            this.number = number;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
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

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getStatus_name() {
            return status_name;
        }

        public void setStatus_name(String status_name) {
            this.status_name = status_name;
        }
    }
}
