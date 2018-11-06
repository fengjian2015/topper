package com.bclould.tea.model;

import java.util.List;

/**
 * Created by GIjia on 2018/11/5.
 */

public class FinanciaProductInfo {
    /**
     * status : 1
     * data : [{"text":"全部","id":0},{"text":"活期","id":1},{"text":"1个月","id":2},{"text":"3个月","id":3},{"text":"6个月","id":4},{"text":"12个月","id":5}]
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
         * text : 全部
         * id : 0
         */

        private String text;
        private int id;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }
}
