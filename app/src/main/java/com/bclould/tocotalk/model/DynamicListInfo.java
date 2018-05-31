package com.bclould.tocotalk.model;

import java.util.List;

/**
 * Created by GA on 2018/3/23.
 */

public class DynamicListInfo {

    /**
     * status : 1
     * data : [{"id":4,"user_name":"liaolinan2","content":"嘿嘿","key_type":1,"key":"liaolinan2201803221956424811521719797894.png","key_compress":"liaolinan2201803221956424811521719797894.png","position":"","review_count":0,"like_count":0,"created_at":"2018-03-22 19:56:49","key_compress_urls":[],"key_urls":[],"is_like":1,"reviewList":[{"id":25,"user_name":"2017aaa","content":"什么鬼","created_at":"2018-03-28 11:51:54","like_count":1}]}]
     */

    private int status;
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

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
         * id : 4
         * user_name : liaolinan2
         * content : 嘿嘿
         * key_type : 1
         * key : liaolinan2201803221956424811521719797894.png
         * key_compress : liaolinan2201803221956424811521719797894.png
         * position :
         * review_count : 0
         * like_count : 0
         * created_at : 2018-03-22 19:56:49
         * key_compress_urls : []
         * key_urls : []
         * is_like : 1
         * reviewList : [{"id":25,"user_name":"2017aaa","content":"什么鬼","created_at":"2018-03-28 11:51:54","like_count":1}]
         */

        private int id;
        private String user_name;
        private String content;
        private int key_type;
        private String key;
        private String key_compress;
        private String position;
        private int review_count;
        private int like_count;
        private String created_at;
        private String avatar;
        private int is_self;
        private int rewardCount;

        public int getRewardCount() {
            return rewardCount;
        }

        public void setRewardCount(int rewardCount) {
            this.rewardCount = rewardCount;
        }

        public int getIs_self() {
            return is_self;
        }

        public void setIs_self(int is_self) {
            this.is_self = is_self;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        private int is_like;
        private List<?> key_compress_urls;
        private List<?> key_urls;
        private List<ReviewListBean> reviewList;

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

        public int getKey_type() {
            return key_type;
        }

        public void setKey_type(int key_type) {
            this.key_type = key_type;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getKey_compress() {
            return key_compress;
        }

        public void setKey_compress(String key_compress) {
            this.key_compress = key_compress;
        }

        public String getPosition() {
            return position;
        }

        public void setPosition(String position) {
            this.position = position;
        }

        public int getReview_count() {
            return review_count;
        }

        public void setReview_count(int review_count) {
            this.review_count = review_count;
        }

        public int getLike_count() {
            return like_count;
        }

        public void setLike_count(int like_count) {
            this.like_count = like_count;
        }

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }

        public int getIs_like() {
            return is_like;
        }

        public void setIs_like(int is_like) {
            this.is_like = is_like;
        }

        public List<?> getKey_compress_urls() {
            return key_compress_urls;
        }

        public void setKey_compress_urls(List<?> key_compress_urls) {
            this.key_compress_urls = key_compress_urls;
        }

        public List<?> getKey_urls() {
            return key_urls;
        }

        public void setKey_urls(List<?> key_urls) {
            this.key_urls = key_urls;
        }

        public List<ReviewListBean> getReviewList() {
            return reviewList;
        }

        public void setReviewList(List<ReviewListBean> reviewList) {
            this.reviewList = reviewList;
        }

        public static class ReviewListBean {
            /**
             * id : 25
             * user_name : 2017aaa
             * content : 什么鬼
             * created_at : 2018-03-28 11:51:54
             * like_count : 1
             */

            private int id;
            private String user_name;
            private String content;
            private String created_at;
            private int like_count;

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
