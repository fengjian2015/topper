package com.bclould.tocotalk.model;

/**
 * Created by GA on 2018/3/8.
 */

public class UrlInfo {

    /**
     * status : 1
     * data : {"url":"https://s3.ap-northeast-2.amazonaws.com/bclould/usdt.png?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=AKIAICDF67DP3Z3DCEMQ%2F20180308%2Fap-northeast-2%2Fs3%2Faws4_request&X-Amz-Date=20180308T021352Z&X-Amz-SignedHeaders=Host&X-Amz-Expires=86400&X-Amz-Signature=b81c0ab1ece1449b6678e6efc47faf7dc1e5edc8439d528c4391a5470853f9ef","expired":1520561632}
     */

    private int status;
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
         * url : https://s3.ap-northeast-2.amazonaws.com/bclould/usdt.png?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=AKIAICDF67DP3Z3DCEMQ%2F20180308%2Fap-northeast-2%2Fs3%2Faws4_request&X-Amz-Date=20180308T021352Z&X-Amz-SignedHeaders=Host&X-Amz-Expires=86400&X-Amz-Signature=b81c0ab1ece1449b6678e6efc47faf7dc1e5edc8439d528c4391a5470853f9ef
         * expired : 1520561632
         */

        private String url;
        private String expired;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getExpired() {
            return expired;
        }

        public void setExpired(String expired) {
            this.expired = expired;
        }
    }
}
