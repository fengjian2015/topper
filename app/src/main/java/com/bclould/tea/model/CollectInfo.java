package com.bclould.tea.model;

import java.util.List;

/**
 * Created by GA on 2018/7/13.
 */

public class CollectInfo {

    /**
     * status : 1
     * message : success
     * data : [{"id":1,"user_id":0,"title":"https://www.bitfinex.com/","url":"Bitfinex - 比特币、莱特币和以太坊兑换与保证金交易平台","created_at":null,"status":1},{"id":2,"user_id":0,"title":"https://www.poloniex.com/","url":"Poloniex - Bitcoin/Digital Asset Exchange","created_at":null,"status":1}]
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
         * id : 1
         * user_id : 0
         * title : https://www.bitfinex.com/
         * url : Bitfinex - 比特币、莱特币和以太坊兑换与保证金交易平台
         * created_at : null
         * status : 1
         */

        private int id;
        private int user_id;
        private String title;
        private String url;
        private Object created_at;
        private int status;
        private String icon;

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getUser_id() {
            return user_id;
        }

        public void setUser_id(int user_id) {
            this.user_id = user_id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public Object getCreated_at() {
            return created_at;
        }

        public void setCreated_at(Object created_at) {
            this.created_at = created_at;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }
    }
}
