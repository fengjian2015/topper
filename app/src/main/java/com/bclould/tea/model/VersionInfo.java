package com.bclould.tea.model;

/**
 * Created by GA on 2018/3/14.
 */

public class VersionInfo {


    /**
     * status : 1
     * data : {"version":"0.36","content":"新增更新日誌，新增長按好友列表修改備註，新增上傳頭像圓形剪切，頭像調整成圓形，解決個別用戶無法刪除好友問題","name":"TopperChat","download_url":"https://toco--bucket.oss-cn-shenzhen.aliyuncs.com/topperchat.apk"}
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
         * version : 0.36
         * content : 新增更新日誌，新增長按好友列表修改備註，新增上傳頭像圓形剪切，頭像調整成圓形，解決個別用戶無法刪除好友問題
         * name : TopperChat
         * download_url : https://toco--bucket.oss-cn-shenzhen.aliyuncs.com/topperchat.apk
         */

        private String version;
        private String content;
        private String name;
        private String download_url;

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDownload_url() {
            return download_url;
        }

        public void setDownload_url(String download_url) {
            this.download_url = download_url;
        }
    }
}
