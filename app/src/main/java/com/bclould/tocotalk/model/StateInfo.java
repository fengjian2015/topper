package com.bclould.tocotalk.model;

import java.util.List;

/**
 * Created by GA on 2018/4/3.
 */

public class StateInfo {

    /**
     * status : 1
     * data : [{"id":13,"nicename":"Australia","name_zh":"澳大利亚"},{"id":44,"nicename":"China","name_zh":"中国"},"..."]
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
         * id : 13
         * nicename : Australia
         * name_zh : 澳大利亚
         */

        private int id;
        private String nicename;
        private String name_zh;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getNicename() {
            return nicename;
        }

        public void setNicename(String nicename) {
            this.nicename = nicename;
        }

        public String getName_zh() {
            return name_zh;
        }

        public void setName_zh(String name_zh) {
            this.name_zh = name_zh;
        }
    }
}
