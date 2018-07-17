package com.bclould.tea.model;

import java.util.List;

/**
 * Created by GIjia on 2018/7/17.
 */

public class UnclaimedRedInfo {
    /**
     * status : 1
     * message : success
     * data : [{"id":264,"coin_name":"TPC","intro":"%E6%81%AD%E5%96%9C%E7%99%BC%E8%B2%A1%EF%BC%8C%E5%A4%A7%E5%90%89%E5%A4%A7%E5%88%A9","created_at":1530102792}]
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
         * id : 264
         * coin_name : TPC
         * intro : %E6%81%AD%E5%96%9C%E7%99%BC%E8%B2%A1%EF%BC%8C%E5%A4%A7%E5%90%89%E5%A4%A7%E5%88%A9
         * created_at : 1530102792
         */

        private int id;
        private String coin_name;
        private String intro;
        private String created_at;
        private String toco_id;

        public String getToco_id() {
            return toco_id;
        }

        public void setToco_id(String toco_id) {
            this.toco_id = toco_id;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getCoin_name() {
            return coin_name;
        }

        public void setCoin_name(String coin_name) {
            this.coin_name = coin_name;
        }

        public String getIntro() {
            return intro;
        }

        public void setIntro(String intro) {
            this.intro = intro;
        }

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }
    }
}
