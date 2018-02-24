package com.dashiji.biyun.model;

/**
 * Created by GA on 2017/10/31.
 */

/**
 * ltc : {"id":2,"name":"ltc","logo":"https://www.yuanbao.com/images/coin/coin_ltc.png","display":"莱特币","status":1,"wallet":"获取失败！","over":"99800","lock":"200！","total":"10000"}
 */
public class MyAssetsInfo {


    /**
     * ltc : {"id":2,"name":"ltc","logo":"https://www.yuanbao.com/images/coin/coin_ltc.png","display":"莱特币","status":1,"wallet":"获取失败!","over":"0","lock":"0","total":"0"}
     * doge : {"id":3,"name":"doge","logo":"https://www.yuanbao.com/images/coin/coin_doge.png","display":"狗狗币","status":1,"wallet":"获取失败!","over":"0","lock":"0","total":"0"}
     * zec : {"id":4,"name":"zec","logo":"https://www.yuanbao.com/images/coin/coin_zec.png","display":"零币","status":1,"wallet":"获取失败!","over":"0","lock":"0","total":"0"}
     * lsk : {"id":5,"name":"lsk","logo":"https://www.yuanbao.com/images/coin/coin_lsk.png","display":"应用链","status":2,"wallet":"获取失败!","over":"0","lock":"0","total":"0"}
     * maid : {"id":6,"name":"maid","logo":"https://www.yuanbao.com/images/coin/coin_maid.png","display":"随享币","status":1,"wallet":"获取失败!","over":"0","lock":"0","total":"0"}
     * shc : {"id":7,"name":"shc","logo":"https://www.yuanbao.com/images/coin/coin_shc.png","display":"分享币","status":2,"wallet":"获取失败!","over":"","lock":"","total":"0"}
     * btc : {"id":8,"name":"btc","logo":"https://ofybqrgr4.qnssl.com/dbb8c7597187cf99577350caee0d6387f46c6891.png","display":"比特币","status":2,"wallet":"获取失败!","over":"","lock":"","total":"0"}
     * ans : {"id":9,"name":"ans","logo":"https://www.yuanbao.com/images/coin/coin_ans.png","display":"小蚁股","status":2,"wallet":"获取失败!","over":"","lock":"","total":"0"}
     */

    private LtcBean ltc;
    private LtcBean doge;
    private LtcBean zec;
    private LtcBean lsk;
    private LtcBean maid;
    private LtcBean shc;
    private LtcBean btc;
    private LtcBean ans;
    private LtcBean tpc;

    public LtcBean getTpc() {
        return tpc;
    }

    public void setTpc(LtcBean tpc) {
        this.tpc = tpc;
    }

    public LtcBean getLtc() {
        return ltc;
    }

    public void setLtc(LtcBean ltc) {
        this.ltc = ltc;
    }

    public LtcBean getDoge() {
        return doge;
    }

    public void setDoge(LtcBean doge) {
        this.doge = doge;
    }

    public LtcBean getZec() {
        return zec;
    }

    public void setZec(LtcBean zec) {
        this.zec = zec;
    }

    public LtcBean getLsk() {
        return lsk;
    }

    public void setLsk(LtcBean lsk) {
        this.lsk = lsk;
    }

    public LtcBean getMaid() {
        return maid;
    }

    public void setMaid(LtcBean maid) {
        this.maid = maid;
    }

    public LtcBean getShc() {
        return shc;
    }

    public void setShc(LtcBean shc) {
        this.shc = shc;
    }

    public LtcBean getBtc() {
        return btc;
    }

    public void setBtc(LtcBean btc) {
        this.btc = btc;
    }

    public LtcBean getAns() {
        return ans;
    }

    public void setAns(LtcBean ans) {
        this.ans = ans;
    }

    public static class LtcBean {
        /**
         * id : 2
         * name : ltc
         * logo : https://www.yuanbao.com/images/coin/coin_ltc.png
         * display : 莱特币
         * status : 1
         * wallet : 获取失败!
         * over : 0
         * lock : 0
         * total : 0
         */

        private int id;
        private String name;
        private String logo;
        private String display;
        private int status;
        private String wallet;
        private String over;
        private String lock;
        private String total;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getLogo() {
            return logo;
        }

        public void setLogo(String logo) {
            this.logo = logo;
        }

        public String getDisplay() {
            return display;
        }

        public void setDisplay(String display) {
            this.display = display;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getWallet() {
            return wallet;
        }

        public void setWallet(String wallet) {
            this.wallet = wallet;
        }

        public String getOver() {
            return over;
        }

        public void setOver(String over) {
            this.over = over;
        }

        public String getLock() {
            return lock;
        }

        public void setLock(String lock) {
            this.lock = lock;
        }

        public String getTotal() {
            return total;
        }

        public void setTotal(String total) {
            this.total = total;
        }
    }

}
