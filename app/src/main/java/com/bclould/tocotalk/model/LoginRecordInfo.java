package com.bclould.tocotalk.model;

import java.util.List;

/**
 * Created by GA on 2018/4/2.
 */

public class LoginRecordInfo {

    /**
     * status : 1
     * data : [{"ip":"58.61.137.1","created_at":"2018-03-23 19:39:34"},{"ip":"58.61.136.199","created_at":"2018-03-22 14:08:14"},"..."]
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
         * ip : 58.61.137.1
         * created_at : 2018-03-23 19:39:34
         */

        private String ip;
        private String created_at;

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
    }
}
