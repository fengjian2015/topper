package com.bclould.tocotalk.model;

/**
 * Created by GA on 2018/6/22.
 */

public class UserDataInfo {

    /**
     * status : 1
     * data : {"avatar":"http://toco--bucket.oss-cn-shenzhen.aliyuncs.com/TOCO41Avatar.png?time=1529575170","remark":"廖矮子"}
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
         * avatar : http://toco--bucket.oss-cn-shenzhen.aliyuncs.com/TOCO41Avatar.png?time=1529575170
         * remark : 廖矮子
         */

        private String avatar;
        private String remark;

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }
    }
}
