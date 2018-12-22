package com.bclould.tea.model;

/**
 * Created by GIjia on 2018/12/21.
 */

public class ExternalUserInfo {

    /**
     * status : 1
     * data : {"name":"fengjian11","openid":"b2a8ff31c4589","avatar":"https://topper-bucket.oss-cn-shenzhen.aliyuncs.com/TOCO602Avatar.png?time=1531403085","email":"545988524123456@qq.com"}
     */

    private int status;
    private String message;
    private DataBean data;
    private String error;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
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
         * name : fengjian11
         * openid : b2a8ff31c4589
         * avatar : https://topper-bucket.oss-cn-shenzhen.aliyuncs.com/TOCO602Avatar.png?time=1531403085
         * email : 545988524123456@qq.com
         */

        private String name;
        private String openid;
        private String avatar;
        private String email;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getOpenid() {
            return openid;
        }

        public void setOpenid(String openid) {
            this.openid = openid;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }
}
