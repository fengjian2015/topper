package com.bclould.tocotalk.model;

import java.util.List;

/**
 * Created by GA on 2018/3/26.
 */

public class ReviewListInfo {

    /**
     * status : 1
     * message : 評論成功!
     * data : {"list":[{"id":159,"user_name":"xihongwei","content":"回复不要愤怒1","created_at":"2018-06-12 16:23:06","url":"","key_type":"0","commentsUser":{"toco_id":"1c60397ae8842","user_name":"xihongwei"},"replyUser":{"toco_id":"1c60397ae8842","user_name":"xihongwei"}}]}
     */

    private int status;
    private String message;
    private DataBean data;

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

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        private List<DynamicListInfo.DataBean.ReviewListBean> list;

        public List<DynamicListInfo.DataBean.ReviewListBean> getList() {
            return list;
        }

        public void setList(List<DynamicListInfo.DataBean.ReviewListBean> list) {
            this.list = list;
        }
    }
}
