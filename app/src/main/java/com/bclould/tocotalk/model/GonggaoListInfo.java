package com.bclould.tocotalk.model;

import java.util.List;

/**
 * Created by GA on 2018/5/10.
 */

public class GonggaoListInfo {

    /**
     * status : 1
     * data : [{"id":7,"title":"123123","content":"<p><img style=\"width: 528px;\" src=\"https://ofybqrgr4.qnssl.com/b88df6664944ee832552b0d68919a81f2eeab0b0.jpg\" data-filename=\"b88df6664944ee832552b0d68919a81f2eeab0b0.jpg\"><\/p><p><br><\/p>","created_at":"2017-03-24 10:59:52","index_pic":"","is_important":0},{"id":5,"title":"2","content":"<p>33<\/p>","created_at":"2017-03-17 11:48:05","index_pic":"","is_important":0},"..."]
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
         * id : 7
         * title : 123123
         * content : <p><img style="width: 528px;" src="https://ofybqrgr4.qnssl.com/b88df6664944ee832552b0d68919a81f2eeab0b0.jpg" data-filename="b88df6664944ee832552b0d68919a81f2eeab0b0.jpg"></p><p><br></p>
         * created_at : 2017-03-24 10:59:52
         * index_pic :
         * is_important : 0
         */

        private int id;
        private String title;
        private String content;
        private String created_at;
        private String index_pic;
        private int is_top;
        private int is_new;
        private int status;

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public int getIs_top() {
            return is_top;
        }

        public void setIs_top(int is_top) {
            this.is_top = is_top;
        }

        public int getIs_new() {
            return is_new;
        }

        public void setIs_new(int is_new) {
            this.is_new = is_new;
        }

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

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }

        public String getIndex_pic() {
            return index_pic;
        }

        public void setIndex_pic(String index_pic) {
            this.index_pic = index_pic;
        }

    }
}
