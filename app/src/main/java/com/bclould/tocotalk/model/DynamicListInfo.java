package com.bclould.tocotalk.model;

import java.util.List;

/**
 * Created by GA on 2018/3/23.
 */

public class DynamicListInfo {


    /**
     * status : 1
     * data : [{"id":4,"user_name":"liaolinan2","content":"嘿嘿","key_type":1,"key":"liaolinan2201803221956424811521719797894.png","key_compress":"liaolinan2201803221956424811521719797894.png","position":"","review_count":0,"like_count":0,"created_at":"2018-03-22 19:56:49","key_compress_urls":[],"key_urls":[],"is_like":1},{"id":7,"user_name":"liaolinan2","content":"你好","key_type":1,"key":"liaolinan2201803222019292811521721166177.png,liaolinan2201803222019315271521721166279.png,liaolinan2201803222019329761521721166289.png,liaolinan2201803222019342091521721166321.png","key_compress":"liaolinan220180322201929283compress1521721166177.png,liaolinan220180322201931528compress1521721166279.png,liaolinan220180322201932977compress1521721166289.png,liaolinan220180322201934210compress1521721166321.png","position":"","review_count":0,"like_count":0,"created_at":"2018-03-22 20:19:39","key_compress_urls":["https://s3.ap-northeast-2.amazonaws.com/bclould/liaolinan220180322201929283compress1521721166177.png?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=AKIAICDF67DP3Z3DCEMQ%2F20180323%2Fap-northeast-2%2Fs3%2Faws4_request&X-Amz-Date=20180323T032906Z&X-Amz-SignedHeaders=Host&X-Amz-Expires=604800&X-Amz-Signature=46955b8097f9b5af7123f9d29d2b4cd80962acb0b60ff73623e15b5a37df4b99","https://s3.ap-northeast-2.amazonaws.com/bclould/liaolinan220180322201931528compress1521721166279.png?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=AKIAICDF67DP3Z3DCEMQ%2F20180323%2Fap-northeast-2%2Fs3%2Faws4_request&X-Amz-Date=20180323T032906Z&X-Amz-SignedHeaders=Host&X-Amz-Expires=604800&X-Amz-Signature=5856f0f8c5b47fd0275ff52d11515c9bcb616aec630ea9d45913c1e005c58676","https://s3.ap-northeast-2.amazonaws.com/bclould/liaolinan220180322201932977compress1521721166289.png?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=AKIAICDF67DP3Z3DCEMQ%2F20180323%2Fap-northeast-2%2Fs3%2Faws4_request&X-Amz-Date=20180323T032906Z&X-Amz-SignedHeaders=Host&X-Amz-Expires=604800&X-Amz-Signature=bf4edcbd239c02a6e4c87b317e3da3ba26ef58331c1bc99d5d2a9ecc30889057","https://s3.ap-northeast-2.amazonaws.com/bclould/liaolinan220180322201934210compress1521721166321.png?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=AKIAICDF67DP3Z3DCEMQ%2F20180323%2Fap-northeast-2%2Fs3%2Faws4_request&X-Amz-Date=20180323T032906Z&X-Amz-SignedHeaders=Host&X-Amz-Expires=604800&X-Amz-Signature=f9d06d69bc40ab47598143fc47f9429a9ac84c18adeeba36d57a3e6368a2233a"],"key_urls":["https://s3.ap-northeast-2.amazonaws.com/bclould/liaolinan2201803222019292811521721166177.png?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=AKIAICDF67DP3Z3DCEMQ%2F20180323%2Fap-northeast-2%2Fs3%2Faws4_request&X-Amz-Date=20180323T032906Z&X-Amz-SignedHeaders=Host&X-Amz-Expires=604800&X-Amz-Signature=e11ed1bedf43358d43fc9a1ccfdc58d89167656fcd9897909ea39c20728b3a6e","https://s3.ap-northeast-2.amazonaws.com/bclould/liaolinan2201803222019315271521721166279.png?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=AKIAICDF67DP3Z3DCEMQ%2F20180323%2Fap-northeast-2%2Fs3%2Faws4_request&X-Amz-Date=20180323T032906Z&X-Amz-SignedHeaders=Host&X-Amz-Expires=604800&X-Amz-Signature=e4af776fb0caeb25400b69179fc72e9e18c4ba75874fca6c023537a8a845b1c0","https://s3.ap-northeast-2.amazonaws.com/bclould/liaolinan2201803222019329761521721166289.png?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=AKIAICDF67DP3Z3DCEMQ%2F20180323%2Fap-northeast-2%2Fs3%2Faws4_request&X-Amz-Date=20180323T032906Z&X-Amz-SignedHeaders=Host&X-Amz-Expires=604800&X-Amz-Signature=51799fd35703f4c608b106131d9a946c79b61d502f6458c4718de8c24c9a91e2","https://s3.ap-northeast-2.amazonaws.com/bclould/liaolinan2201803222019342091521721166321.png?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=AKIAICDF67DP3Z3DCEMQ%2F20180323%2Fap-northeast-2%2Fs3%2Faws4_request&X-Amz-Date=20180323T032906Z&X-Amz-SignedHeaders=Host&X-Amz-Expires=604800&X-Amz-Signature=e2cdbbc9ea16c929bb2c6696dd37231d3e709db0610f3b5afc8644dc05a23bd4"]},"..."]
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
        private int is_like;
        private List<?> key_compress_urls;
        private List<?> key_urls;

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
    }
}
