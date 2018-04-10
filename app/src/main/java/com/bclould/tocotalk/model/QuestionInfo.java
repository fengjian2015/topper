package com.bclould.tocotalk.model;

import java.util.List;

/**
 * Created by GA on 2018/4/9.
 */

public class QuestionInfo {

    /**
     * status : 1
     * data : [{"id":2,"title":"如何注册TOCOTALK？"},{"id":3,"title":"验证邮箱收不到确认邮件怎么办？"},"..."]
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
         * id : 2
         * title : 如何注册TOCOTALK？
         */

        private int id;
        private String title;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }
}
