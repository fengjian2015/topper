package com.bclould.tea.model;

import java.util.List;

/**
 * Created by GA on 2018/5/8.
 */

public class NewsListInfo {

    /**
     * status : 1
     * top : [{"id":5,"title":"2","created_at":"2017-03-17 11:48:05","index_pic":"http://pic.58pic.com/58pic/14/64/56/25h58PIC3eG_1024.jpg"},{"id":6,"title":"测试","created_at":"2017-03-15 15:00:12","index_pic":"http://pic.58pic.com/58pic/14/64/56/25h58PIC3eG_1024.jpg"},{"id":7,"title":"123123","created_at":"2017-03-24 10:59:52","index_pic":"http://pic.58pic.com/58pic/14/64/56/25h58PIC3eG_1024.jpg"}]
     * lists : [{"id":7,"title":"123123","created_at":"2017-03-24 10:59:52","index_pic":"http://pic.58pic.com/58pic/14/64/56/25h58PIC3eG_1024.jpg","is_important":0},{"id":5,"title":"2","created_at":"2017-03-17 11:48:05","index_pic":"http://pic.58pic.com/58pic/14/64/56/25h58PIC3eG_1024.jpg","is_important":0},{"id":6,"title":"测试","created_at":"2017-03-15 15:00:12","index_pic":"http://pic.58pic.com/58pic/14/64/56/25h58PIC3eG_1024.jpg","is_important":0}]
     */

    private int status;
    private List<TopBean> top;
    private List<ListsBean> lists;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<TopBean> getTop() {
        return top;
    }

    public void setTop(List<TopBean> top) {
        this.top = top;
    }

    public List<ListsBean> getLists() {
        return lists;
    }

    public void setLists(List<ListsBean> lists) {
        this.lists = lists;
    }

    public static class TopBean {
        /**
         * id : 5
         * title : 2
         * created_at : 2017-03-17 11:48:05
         * index_pic : http://pic.58pic.com/58pic/14/64/56/25h58PIC3eG_1024.jpg
         */

        private int id;
        private String title;
        private String created_at;
        private String index_pic;

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

        @Override
        public String toString() {
            return "'" + "top{" + "'" +
                    "id=" + id +
                    ", title='" + title + '\'' +
                    ", created_at='" + created_at + '\'' +
                    ", index_pic='" + index_pic + '\'' +
                    '}';
        }
    }

    public static class ListsBean {
        /**
         * id : 7
         * title : 123123
         * created_at : 2017-03-24 10:59:52
         * index_pic : http://pic.58pic.com/58pic/14/64/56/25h58PIC3eG_1024.jpg
         * is_important : 0
         */

        private int id;
        private String title;
        private String created_at;
        private String index_pic;
        private int is_important;
        private int is_ad;

        public int getIs_ad() {
            return is_ad;
        }

        public void setIs_ad(int is_ad) {
            this.is_ad = is_ad;
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

        public int getIs_important() {
            return is_important;
        }

        public void setIs_important(int is_important) {
            this.is_important = is_important;
        }

        @Override
        public String toString() {
            return "list{" +
                    "id=" + id +
                    ", title='" + title + '\'' +
                    ", created_at='" + created_at + '\'' +
                    ", index_pic='" + index_pic + '\'' +
                    ", is_important=" + is_important +
                    ", is_ad=" + is_ad +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "{" +
                "status=" + status +
                ", top=" + top +
                ", lists=" + lists +
                '}';
    }
}
