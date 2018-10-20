package com.bclould.tea.model;

import java.util.List;

/**
 * Created by GIjia on 2018/9/29.
 */

public class HistoryInfo {
    /**
     * status : 1
     * data : [{"type":"共識節點","amount":"100","created_at":"2018-09-29 14:17:21"}]
     */

    private int status;
    private List<DataBean> data;
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

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * type : 共識節點
         * amount : 100
         * created_at : 2018-09-29 14:17:21
         */

        private String type;
        private String amount;
        private String created_at;
        private String title;
        private String number;

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

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getAmount() {
            return amount;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }
    }
}
