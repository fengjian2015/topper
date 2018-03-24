package com.bclould.tocotalk.model;

import java.util.List;

/**
 * Created by GA on 2018/3/21.
 */

public class CardListInfo {

    /**
     * status : 1
     * data : [{"id":8,"bank_name":"","card_number":"848948********"},{"id":9,"bank_name":"","card_number":"848948********"}]
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
         * id : 8
         * bank_name :
         * card_number : 848948********
         */

        private int id;
        private String bank_name;
        private String card_number;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getBank_name() {
            return bank_name;
        }

        public void setBank_name(String bank_name) {
            this.bank_name = bank_name;
        }

        public String getCard_number() {
            return card_number;
        }

        public void setCard_number(String card_number) {
            this.card_number = card_number;
        }
    }
}
