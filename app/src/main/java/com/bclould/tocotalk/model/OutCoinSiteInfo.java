package com.bclould.tocotalk.model;

import java.util.List;

/**
 * Created by GA on 2017/11/8.
 */

public class OutCoinSiteInfo {


    /**
     * status : 1
     * message : [{"id":16,"label":"zhandsan","address":"sdfsfjklsa554f14sfafsdfsa"}]
     */

    private int status;
    private List<MessageBean> message;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<MessageBean> getMessage() {
        return message;
    }

    public void setMessage(List<MessageBean> message) {
        this.message = message;
    }

    public static class MessageBean {
        /**
         * id : 16
         * label : zhandsan
         * address : sdfsfjklsa554f14sfafsdfsa
         */

        private int id;
        private String label;
        private String address;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }
    }
}
