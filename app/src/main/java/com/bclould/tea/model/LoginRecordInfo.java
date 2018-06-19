package com.bclould.tea.model;

import java.util.List;

/**
 * Created by GA on 2018/4/2.
 */

public class LoginRecordInfo {


    /**
     * status : 1
     * data : [{"ip":"58.61.137.192","created_at":"2018-04-02 17:25:41","location":"China Guangzhou"},{"ip":"58.61.136.230","created_at":"2018-03-30 18:18:56","location":"China Guangzhou"},"..."]
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
         * ip : 58.61.137.192
         * created_at : 2018-04-02 17:25:41
         * location : China Guangzhou
         */

        private String ip;
        private String created_at;
        private String location;

        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }
    }
}
