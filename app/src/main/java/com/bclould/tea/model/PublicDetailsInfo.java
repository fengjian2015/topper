package com.bclould.tea.model;

import java.util.List;

/**
 * Created by GIjia on 2018/7/27.
 */

public class PublicDetailsInfo {
    /**
     * status : 1
     * message : success
     * data : {"id":4,"name":"123123","logo":"","desc":"","merchant_id":3,"status":1,"created_at":1532055139,"menu":{"button":[{"type":"click","name":"菜单名称","key":"3123132","subMenu":false,"tabSubMenu":false,"overLength":true,"hasSubmenu":false,"sub_button":[{"type":"click","name":"子菜单名称","key":"12312312"},{"type":"click","name":"子菜单名称","key":"12312312"},{"type":"click","name":"子菜单名称","key":"12312312"}]},{"type":"click","name":"菜单名称","key":"3123132","subMenu":false,"tabSubMenu":false,"overLength":true,"hasSubmenu":false,"sub_button":[{"type":"click","name":"子菜单名称","key":"12312312"},{"type":"click","name":"子菜单名称","key":"12312312"},{"type":"click","name":"子菜单名称","key":"12312312"}]},{"type":"view","name":"菜单名称","key":"3123132","subMenu":false,"tabSubMenu":true,"overLength":true,"hasSubmenu":true,"url":"www.baidu.com"}]}}
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
        /**
         * id : 4
         * name : 123123
         * logo :
         * desc :
         * merchant_id : 3
         * status : 1
         * created_at : 1532055139
         * menu : {"button":[{"type":"click","name":"菜单名称","key":"3123132","subMenu":false,"tabSubMenu":false,"overLength":true,"hasSubmenu":false,"sub_button":[{"type":"click","name":"子菜单名称","key":"12312312"},{"type":"click","name":"子菜单名称","key":"12312312"},{"type":"click","name":"子菜单名称","key":"12312312"}]},{"type":"click","name":"菜单名称","key":"3123132","subMenu":false,"tabSubMenu":false,"overLength":true,"hasSubmenu":false,"sub_button":[{"type":"click","name":"子菜单名称","key":"12312312"},{"type":"click","name":"子菜单名称","key":"12312312"},{"type":"click","name":"子菜单名称","key":"12312312"}]},{"type":"view","name":"菜单名称","key":"3123132","subMenu":false,"tabSubMenu":true,"overLength":true,"hasSubmenu":true,"url":"www.baidu.com"}]}
         */

        private int id;
        private String name;
        private String logo;
        private String desc;
        private int merchant_id;
        private int status;
        private int created_at;
        private String menu;

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

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public int getMerchant_id() {
            return merchant_id;
        }

        public void setMerchant_id(int merchant_id) {
            this.merchant_id = merchant_id;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public int getCreated_at() {
            return created_at;
        }

        public void setCreated_at(int created_at) {
            this.created_at = created_at;
        }

        public String getMenu() {
            return menu;
        }

        public void setMenu(String menu) {
            this.menu = menu;
        }
    }
}
