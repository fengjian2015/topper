package com.bclould.tocotalk.model;

/**
 * Created by GA on 2018/4/19.
 */

public class AuatarListInfo {

    /**
     * status : 1
     * message : success
     * data : {"name":"liaolinan2","avatar":"https://bclould.s3.ap-northeast-2.amazonaws.com/TOCO41Avatar.png"}
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
         * name : liaolinan2
         * avatar : https://bclould.s3.ap-northeast-2.amazonaws.com/TOCO41Avatar.png
         */

        private String name;
        private String avatar;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }
    }
}
