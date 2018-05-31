package com.bclould.tocotalk.model;

/**
 * Created by GIjia on 2018/5/10.
 */

public class IndividualInfo {

    private DataBean data;
    private int status;
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public static class DataBean {
        private String name;
        private String avatar;
        private String country;
        private int no_see_me;
        private int no_see_him;
        private String remark;

        public int getNo_see_me() {
            return no_see_me;
        }

        public void setNo_see_me(int no_see_me) {
            this.no_see_me = no_see_me;
        }

        public int getNo_see_him() {
            return no_see_him;
        }

        public void setNo_see_him(int no_see_him) {
            this.no_see_him = no_see_him;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }
    }

}
