package com.bclould.tocotalk.model;

import java.util.List;

/**
 * Created by GA on 2018/3/26.
 */

public class ReviewListInfo {

    /**
     * status : 1
     * data : {"info":{"user_name":"xihongwei","content":"我的第二条动态，欢迎点赞！","key":"","created_at":"1520500099","like_count":0},"list":[{"id":1,"user_name":"xihongwei","content":"不点会怎样 ~","created_at":"1520500538","like_count":0}]}
     */

    private int status;
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    private DataBean data;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * info : {"user_name":"xihongwei","content":"我的第二条动态，欢迎点赞！","key":"","created_at":"1520500099","like_count":0}
         * list : [{"id":1,"user_name":"xihongwei","content":"不点会怎样 ~","created_at":"1520500538","like_count":0}]
         */

        private InfoBean info;
        private List<ListBean> list;

        public InfoBean getInfo() {
            return info;
        }

        public void setInfo(InfoBean info) {
            this.info = info;
        }

        public List<ListBean> getList() {
            return list;
        }

        public void setList(List<ListBean> list) {
            this.list = list;
        }

        public static class InfoBean {
            /**
             * user_name : xihongwei
             * content : 我的第二条动态，欢迎点赞！
             * key :
             * created_at : 1520500099
             * like_count : 0
             */

            private String user_name;
            private String content;
            private String key;
            private String created_at;
            private String avatar;

            public String getAvatar() {
                return avatar;
            }

            public void setAvatar(String avatar) {
                this.avatar = avatar;
            }

            private int like_count;
            private int is_like;

            public int getIs_like() {
                return is_like;
            }

            public void setIs_like(int is_like) {
                this.is_like = is_like;
            }

            public String getUser_name() {
                return user_name;
            }

            public void setUser_name(String user_name) {
                this.user_name = user_name;
            }

            public String getContent() {
                return content;
            }

            public void setContent(String content) {
                this.content = content;
            }

            public String getKey() {
                return key;
            }

            public void setKey(String key) {
                this.key = key;
            }

            public String getCreated_at() {
                return created_at;
            }

            public void setCreated_at(String created_at) {
                this.created_at = created_at;
            }

            public int getLike_count() {
                return like_count;
            }

            public void setLike_count(int like_count) {
                this.like_count = like_count;
            }
        }

        public static class ListBean {
            /**
             * id : 1
             * user_name : xihongwei
             * content : 不点会怎样 ~
             * created_at : 1520500538
             * like_count : 0
             */

            private int id;
            private String user_name;
            private String content;
            private String created_at;
            private int like_count;
            private int is_like;

            public int getIs_like() {
                return is_like;
            }

            public void setIs_like(int is_like) {
                this.is_like = is_like;
            }

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getUser_name() {
                return user_name;
            }

            public void setUser_name(String user_name) {
                this.user_name = user_name;
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

            public int getLike_count() {
                return like_count;
            }

            public void setLike_count(int like_count) {
                this.like_count = like_count;
            }
        }
    }
}
