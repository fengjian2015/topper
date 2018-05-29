package com.bclould.tocotalk.model;

/**
 * Created by GA on 2018/3/26.
 */

public class LikeInfo {


    /**
     * status : 1
     * data : {"likeCounts":2,"status":1}
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
         * likeCounts : 2
         * status : 1
         */

        private int likeCounts;
        private int status;

        public int getLikeCounts() {
            return likeCounts;
        }

        public void setLikeCounts(int likeCounts) {
            this.likeCounts = likeCounts;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }
    }
}
