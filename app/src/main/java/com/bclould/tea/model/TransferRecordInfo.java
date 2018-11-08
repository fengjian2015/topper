package com.bclould.tea.model;

import java.util.List;

/**
 * Created by GIjia on 2018/9/29.
 */

public class TransferRecordInfo {
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

        private int type;
        private String created_at;
        private String number;

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

        public String getNumber() {
            return number;
        }

        public void setNumber(String number) {
            this.number = number;
        }
    }
}
