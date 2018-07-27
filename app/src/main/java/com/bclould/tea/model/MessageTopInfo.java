package com.bclould.tea.model;

import java.util.List;

/**
 * Created by GIjia on 2018/7/27.
 */

public class MessageTopInfo {
    /**
     * status : 1
     * message : success
     * data : [{"for_id":2}]
     */

    private int status;
    private String message;
    private List<DataBean> data;

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

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * for_id : 2
         */

        private String for_id;

        public String getFor_id() {
            return for_id;
        }

        public void setFor_id(String for_id) {
            this.for_id = for_id;
        }
    }
}
