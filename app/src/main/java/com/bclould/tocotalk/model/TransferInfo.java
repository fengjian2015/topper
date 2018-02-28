package com.bclould.tocotalk.model;

import java.util.List;

/**
 * Created by GA on 2018/2/28.
 */

public class TransferInfo {

    /**
     * status : 1
     * data : [{"send_name":"xixixi","receive_name":"xihongwei","number":"0.1","coin_name":"btc","created_at":"2018-02-01 15:03:52","desc":"转出","to_id":35},{"send_name":"xihongwei","receive_name":"xixixi","number":"0.1","coin_name":"btc","created_at":"2018-02-01 15:07:54","desc":"转入","to_id":35}]
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
         * send_name : xixixi
         * receive_name : xihongwei
         * number : 0.1
         * coin_name : btc
         * created_at : 2018-02-01 15:03:52
         * desc : 转出
         * to_id : 35
         */

        private String send_name;
        private String receive_name;
        private String number;
        private String coin_name;
        private String created_at;
        private String desc;
        private int to_id;

        public String getSend_name() {
            return send_name;
        }

        public void setSend_name(String send_name) {
            this.send_name = send_name;
        }

        public String getReceive_name() {
            return receive_name;
        }

        public void setReceive_name(String receive_name) {
            this.receive_name = receive_name;
        }

        public String getNumber() {
            return number;
        }

        public void setNumber(String number) {
            this.number = number;
        }

        public String getCoin_name() {
            return coin_name;
        }

        public void setCoin_name(String coin_name) {
            this.coin_name = coin_name;
        }

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public int getTo_id() {
            return to_id;
        }

        public void setTo_id(int to_id) {
            this.to_id = to_id;
        }
    }
}
