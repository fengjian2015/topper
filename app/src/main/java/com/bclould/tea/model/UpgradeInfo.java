package com.bclould.tea.model;

/**
 * Created by GIjia on 2018/9/26.
 */

public class UpgradeInfo {
    /**
     * status : 1
     * data : {"current_node":"当前未成为共识节点","over_num":"355.54FTC","consensus_node_text":"共识节点，。。。","super_node_text":"超级节点，。。。","main_node_text":"主节点，。。。","type":1,"consensus_node":{"min":1,"max":5,"desc":"支付100FTC","number":100,"coin_name":"FTC"},"super_node":{"min":1,"max":10,"desc":"尚未达到","number":100,"coin_name":"FTC"},"main_node":{"min":1,"max":5,"desc":"尚未达到","number":100,"coin_name":"FTC"}}
     */

    private int status;
    private DataBean data;
    private String message;

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
         * current_node : 当前未成为共识节点
         * over_num : 355.54FTC
         * consensus_node_text : 共识节点，。。。
         * super_node_text : 超级节点，。。。
         * main_node_text : 主节点，。。。
         * type : 1
         * consensus_node : {"min":1,"max":5,"desc":"支付100FTC","number":100,"coin_name":"FTC"}
         * super_node : {"min":1,"max":10,"desc":"尚未达到","number":100,"coin_name":"FTC"}
         * main_node : {"min":1,"max":5,"desc":"尚未达到","number":100,"coin_name":"FTC"}
         */

        private String current_node;
        private String over_num;
        private String consensus_node_text;
        private String super_node_text;
        private String main_node_text;
        private int type;
        private ConsensusNodeBean consensus_node;
        private SuperNodeBean super_node;
        private MainNodeBean main_node;

        public String getCurrent_node() {
            return current_node;
        }

        public void setCurrent_node(String current_node) {
            this.current_node = current_node;
        }

        public String getOver_num() {
            return over_num;
        }

        public void setOver_num(String over_num) {
            this.over_num = over_num;
        }

        public String getConsensus_node_text() {
            return consensus_node_text;
        }

        public void setConsensus_node_text(String consensus_node_text) {
            this.consensus_node_text = consensus_node_text;
        }

        public String getSuper_node_text() {
            return super_node_text;
        }

        public void setSuper_node_text(String super_node_text) {
            this.super_node_text = super_node_text;
        }

        public String getMain_node_text() {
            return main_node_text;
        }

        public void setMain_node_text(String main_node_text) {
            this.main_node_text = main_node_text;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public ConsensusNodeBean getConsensus_node() {
            return consensus_node;
        }

        public void setConsensus_node(ConsensusNodeBean consensus_node) {
            this.consensus_node = consensus_node;
        }

        public SuperNodeBean getSuper_node() {
            return super_node;
        }

        public void setSuper_node(SuperNodeBean super_node) {
            this.super_node = super_node;
        }

        public MainNodeBean getMain_node() {
            return main_node;
        }

        public void setMain_node(MainNodeBean main_node) {
            this.main_node = main_node;
        }

        public static class ConsensusNodeBean {
            /**
             * min : 1
             * max : 5
             * desc : 支付100FTC
             * number : 100
             * coin_name : FTC
             */

            private int min;
            private int max;
            private String desc;
            private int number;
            private String coin_name;

            public int getMin() {
                return min;
            }

            public void setMin(int min) {
                this.min = min;
            }

            public int getMax() {
                return max;
            }

            public void setMax(int max) {
                this.max = max;
            }

            public String getDesc() {
                return desc;
            }

            public void setDesc(String desc) {
                this.desc = desc;
            }

            public int getNumber() {
                return number;
            }

            public void setNumber(int number) {
                this.number = number;
            }

            public String getCoin_name() {
                return coin_name;
            }

            public void setCoin_name(String coin_name) {
                this.coin_name = coin_name;
            }
        }

        public static class SuperNodeBean {
            /**
             * min : 1
             * max : 10
             * desc : 尚未达到
             * number : 100
             * coin_name : FTC
             */

            private int min;
            private int max;
            private String desc;
            private int number;
            private String coin_name;

            public int getMin() {
                return min;
            }

            public void setMin(int min) {
                this.min = min;
            }

            public int getMax() {
                return max;
            }

            public void setMax(int max) {
                this.max = max;
            }

            public String getDesc() {
                return desc;
            }

            public void setDesc(String desc) {
                this.desc = desc;
            }

            public int getNumber() {
                return number;
            }

            public void setNumber(int number) {
                this.number = number;
            }

            public String getCoin_name() {
                return coin_name;
            }

            public void setCoin_name(String coin_name) {
                this.coin_name = coin_name;
            }
        }

        public static class MainNodeBean {
            /**
             * min : 1
             * max : 5
             * desc : 尚未达到
             * number : 100
             * coin_name : FTC
             */

            private int min;
            private int max;
            private String desc;
            private int number;
            private String coin_name;

            public int getMin() {
                return min;
            }

            public void setMin(int min) {
                this.min = min;
            }

            public int getMax() {
                return max;
            }

            public void setMax(int max) {
                this.max = max;
            }

            public String getDesc() {
                return desc;
            }

            public void setDesc(String desc) {
                this.desc = desc;
            }

            public int getNumber() {
                return number;
            }

            public void setNumber(int number) {
                this.number = number;
            }

            public String getCoin_name() {
                return coin_name;
            }

            public void setCoin_name(String coin_name) {
                this.coin_name = coin_name;
            }
        }
    }
}
