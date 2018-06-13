package com.bclould.tocotalk.model;

/**
 * Created by GIjia on 2018/6/11.
 */

public class GroupCreateInfo {

    /**
     * status : 1
     * message : 创建群组成功
     * data : {"group_id":15}
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
         * group_id : 15
         */

        private int group_id;

        public int getGroup_id() {
            return group_id;
        }

        public void setGroup_id(int group_id) {
            this.group_id = group_id;
        }
    }
}
