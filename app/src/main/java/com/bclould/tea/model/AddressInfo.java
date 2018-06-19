package com.bclould.tea.model;

/**
 * Created by GA on 2018/4/12.
 */

public class AddressInfo {

    /**
     * status : 1
     * data : {"address":"TMNWukAtKaoWGzwspyWTpWMzaYT3dqqqax","title":"这是您的TPC充值地址，请将您的TPC转入此地址，务必备注您的注册邮箱账号，填写错误将无法到账成功！","desc":"1.TPC转账需要TPC网络进行确认，每笔转账至少需要预0.05个TPC在原地址，达到3个确认后您的TPC会自动充值至您的账户之中；\\n2.禁止任何非TPC資產充值該地址，否则将无法到賬為TPC資產,丢失將無法找回；\\n3.您在操作TPC钱包向此地址转账时，请务必填写备注信息：您的邮箱账号，否则将无法到账！\\n4.请勿使用本地址做为挖矿地址，否则不能到账。"}
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
         * address : TMNWukAtKaoWGzwspyWTpWMzaYT3dqqqax
         * title : 这是您的TPC充值地址，请将您的TPC转入此地址，务必备注您的注册邮箱账号，填写错误将无法到账成功！
         * desc : 1.TPC转账需要TPC网络进行确认，每笔转账至少需要预0.05个TPC在原地址，达到3个确认后您的TPC会自动充值至您的账户之中；\n2.禁止任何非TPC資產充值該地址，否则将无法到賬為TPC資產,丢失將無法找回；\n3.您在操作TPC钱包向此地址转账时，请务必填写备注信息：您的邮箱账号，否则将无法到账！\n4.请勿使用本地址做为挖矿地址，否则不能到账。
         */

        private String address;
        private String title;
        private String desc;

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }
    }
}
