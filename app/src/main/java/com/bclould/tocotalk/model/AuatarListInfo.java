package com.bclould.tocotalk.model;

import java.util.List;

/**
 * Created by GA on 2018/4/19.
 */

public class AuatarListInfo {


    /**
     * status : 1
     * message : success
     * data : [{"name":"2017aaa","avatar":"","remark":"2017aaa"},{"name":"liaolinan2","avatar":"https://bclould.s3.ap-northeast-2.amazonaws.com/TOCO41Avatar.png","remark":"廖矮子"}]
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
         * name : 2017aaa
         * avatar :
         * remark : 2017aaa
         */

        private String name;
        private String avatar;
        private String remark;
        private String toco_id;

        public String getToco_id() {
            return toco_id;
        }

        public void setToco_id(String toco_id) {
            this.toco_id = toco_id;
        }

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

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }
    }
}
