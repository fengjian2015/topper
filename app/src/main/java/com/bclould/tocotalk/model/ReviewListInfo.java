package com.bclould.tocotalk.model;

import java.util.List;

/**
 * Created by GA on 2018/3/26.
 */

public class ReviewListInfo {

    /**
     * status : 1
     * data : {"info":{"id":34,"user_id":41,"user_name":"廖矮子","content":"段友","key":"liaolinan2201804022117187791522675033673.png","created_at":"2018-04-02 21:17:27","like_count":3,"is_like":0,"avatar":"https://bclould.s3.ap-northeast-2.amazonaws.com/TOCO41Avatar.png?time=1527492529"},"list":[{"id":74,"user_name":"liaolinan2","content":"愤怒","key_type":0,"created_at":"2018-05-21 14:10:14","like_count":0,"is_like":0,"url":"","reply_lists":[{"user_name":"xihongwei","content":"不要愤怒","created_at":"2018-05-29 15:43:51"}]},{"id":73,"user_name":"liaolinan2","content":"龙","key_type":0,"created_at":"2018-05-21 14:10:11","like_count":0,"is_like":0,"url":"","reply_lists":[]},"..."]}
     */

    private int status;
    private String message;
    private DataBean data;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

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
         * info : {"id":34,"user_id":41,"user_name":"廖矮子","content":"段友","key":"liaolinan2201804022117187791522675033673.png","created_at":"2018-04-02 21:17:27","like_count":3,"is_like":0,"avatar":"https://bclould.s3.ap-northeast-2.amazonaws.com/TOCO41Avatar.png?time=1527492529"}
         * list : [{"id":74,"user_name":"liaolinan2","content":"愤怒","key_type":0,"created_at":"2018-05-21 14:10:14","like_count":0,"is_like":0,"url":"","reply_lists":[{"user_name":"xihongwei","content":"不要愤怒","created_at":"2018-05-29 15:43:51"}]},{"id":73,"user_name":"liaolinan2","content":"龙","key_type":0,"created_at":"2018-05-21 14:10:11","like_count":0,"is_like":0,"url":"","reply_lists":[]},"..."]
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
             * id : 34
             * user_id : 41
             * user_name : 廖矮子
             * content : 段友
             * key : liaolinan2201804022117187791522675033673.png
             * created_at : 2018-04-02 21:17:27
             * like_count : 3
             * is_like : 0
             * avatar : https://bclould.s3.ap-northeast-2.amazonaws.com/TOCO41Avatar.png?time=1527492529
             */

            private int id;
            private int user_id;
            private String user_name;
            private String content;
            private String key;
            private String created_at;
            private int like_count;
            private int is_like;
            private String avatar;

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

            public int getIs_like() {
                return is_like;
            }

            public void setIs_like(int is_like) {
                this.is_like = is_like;
            }

            public String getAvatar() {
                return avatar;
            }

            public void setAvatar(String avatar) {
                this.avatar = avatar;
            }
        }

        public static class ListBean {
            /**
             * id : 74
             * user_name : liaolinan2
             * content : 愤怒
             * key_type : 0
             * created_at : 2018-05-21 14:10:14
             * like_count : 0
             * is_like : 0
             * url :
             * reply_lists : [{"user_name":"xihongwei","content":"不要愤怒","created_at":"2018-05-29 15:43:51"}]
             */

            private int id;
            private String user_name;
            private String content;
            private int key_type;
            private String created_at;
            private int like_count;
            private int is_like;
            private String url;
            private List<ReplyListsBean> reply_lists;

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

            public int getIs_like() {
                return is_like;
            }

            public void setIs_like(int is_like) {
                this.is_like = is_like;
            }

            public String getUrl() {
                return url;
            }

            public void setUrl(String url) {
                this.url = url;
            }

            public List<ReplyListsBean> getReply_lists() {
                return reply_lists;
            }

            public void setReply_lists(List<ReplyListsBean> reply_lists) {
                this.reply_lists = reply_lists;
            }

            public static class ReplyListsBean {
                /**
                 * user_name : xihongwei
                 * content : 不要愤怒
                 * created_at : 2018-05-29 15:43:51
                 */

                private String user_name;
                private String content;
                private String created_at;

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
            }
        }
    }
}
