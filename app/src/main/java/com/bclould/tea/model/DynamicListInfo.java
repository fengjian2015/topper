package com.bclould.tea.model;

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
        private String toco_id;
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

        public String getToco_id() {
            return toco_id;
        }

        public void setToco_id(String toco_id) {
            this.toco_id = toco_id;
        }

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
            private UsersBean replyUser; // 回复人信息
            private UsersBean commentsUser;  // 评论人信息
            private String content;
            private String url;
            private String key_type;
            private String created_at;
            private int like_count;


            public String getUrl() {
                return url;
            }

            public void setUrl(String url) {
                this.url = url;
            }

            public String getKey_type() {
                return key_type;
            }

            public void setKey_type(String key_type) {
                this.key_type = key_type;
            }


            public UsersBean getReplyUser() {
                return replyUser;
            }

            public void setReplyUser(UsersBean replyUser) {
                this.replyUser = replyUser;
            }

            public UsersBean getCommentsUser() {
                return commentsUser;
            }

            public void setCommentsUser(UsersBean commentsUser) {
                this.commentsUser = commentsUser;
            }

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
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

            public static class UsersBean {
                private String user_name;
                private String toco_id;

                public String getUser_name() {
                    return user_name;
                }

                public void setUser_name(String user_name) {
                    this.user_name = user_name;
                }

                public String getToco_id() {
                    return toco_id;
                }

                public void setToco_id(String toco_id) {
                    this.toco_id = toco_id;
                }
            }

            @Override
            public String toString() {
                return "reviewlist{" +
                        "id=" + id +
                        ", replyUser=" + replyUser +
                        ", commentsUser=" + commentsUser +
                        ", content='" + content + '\'' +
                        ", url='" + url + '\'' +
                        ", key_type='" + key_type + '\'' +
                        ", created_at='" + created_at + '\'' +
                        ", like_count=" + like_count +
                        '}';
            }
        }

        @Override
        public String toString() {
            return "data{" +
                    "id=" + id +
                    ", user_name='" + user_name + '\'' +
                    ", content='" + content + '\'' +
                    ", key_type=" + key_type +
                    ", key='" + key + '\'' +
                    ", key_compress='" + key_compress + '\'' +
                    ", position='" + position + '\'' +
                    ", review_count=" + review_count +
                    ", like_count=" + like_count +
                    ", created_at='" + created_at + '\'' +
                    ", avatar='" + avatar + '\'' +
                    ", is_self=" + is_self +
                    ", rewardCount=" + rewardCount +
                    ", is_like=" + is_like +
                    ", key_compress_urls=" + key_compress_urls +
                    ", key_urls=" + key_urls +
                    ", reviewList=" + reviewList +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "{" +
                "status=" + status +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}
