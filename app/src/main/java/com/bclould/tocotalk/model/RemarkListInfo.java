package com.bclould.tocotalk.model;

import java.util.List;

/**
 * Created by GA on 2018/4/19.
 */

public class RemarkListInfo {


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
        private String name;
        private String remark;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }
    }

    @Override
    public String toString() {
        return "RemarkListInfo{" +
                "status=" + status +
                ", data=" + data +
                '}';
    }
}
