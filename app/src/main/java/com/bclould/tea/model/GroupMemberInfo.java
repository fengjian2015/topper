package com.bclould.tea.model;

import java.util.List;

/**
 * Created by GIjia on 2018/6/21.
 */

public class GroupMemberInfo {
    /**
     * status : 1
     * message : success
     * data : [{"toco_id":"1c60397ae8842","avatar":"http://toco.oss-cn-shenzhen.aliyuncs.com/TOCO34Avatar.png?time=1528958917"},{"toco_id":"256dfdfb783b3","avatar":"https://bclould.s3.ap-northeast-2.amazonaws.com/TOCO209Avatar.png?time=1528446080"},{"toco_id":"0ecf6f29f0cac","avatar":""}]
     */

    private int status;
    private String message;
    private List<DataBean> data;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * toco_id : 1c60397ae8842
         * avatar : http://toco.oss-cn-shenzhen.aliyuncs.com/TOCO34Avatar.png?time=1528958917
         */

        private String toco_id;
        private String avatar;

        public String getToco_id() {
            return toco_id;
        }

        public void setToco_id(String toco_id) {
            this.toco_id = toco_id;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        @Override
        public String toString() {
            return "DataBean{" +
                    "toco_id='" + toco_id + '\'' +
                    ", avatar='" + avatar + '\'' +
                    '}';
        }
    }
}
