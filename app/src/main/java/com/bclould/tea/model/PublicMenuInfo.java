package com.bclould.tea.model;

import java.util.List;

/**
 * Created by GIjia on 2018/7/27.
 */

public class PublicMenuInfo {
    private List<ButtonBean> button;

    public List<ButtonBean> getButton() {
        return button;
    }

    public void setButton(List<ButtonBean> button) {
        this.button = button;
    }

    public static class ButtonBean {
        /**
         * type : click
         * name : 菜单名称
         * key : 3123132
         * subMenu : false
         * tabSubMenu : false
         * overLength : true
         * hasSubmenu : false
         * sub_button : [{"type":"click","name":"子菜单名称","key":"12312312"},{"type":"click","name":"子菜单名称","key":"12312312"},{"type":"click","name":"子菜单名称","key":"12312312"}]
         * url : www.baidu.com
         */

        private String type;
        private String name;
        private String key;
        private boolean subMenu;
        private boolean tabSubMenu;
        private boolean overLength;
        private boolean hasSubmenu;
        private String url;
        private List<SubButtonBean> sub_button;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public boolean isSubMenu() {
            return subMenu;
        }

        public void setSubMenu(boolean subMenu) {
            this.subMenu = subMenu;
        }

        public boolean isTabSubMenu() {
            return tabSubMenu;
        }

        public void setTabSubMenu(boolean tabSubMenu) {
            this.tabSubMenu = tabSubMenu;
        }

        public boolean isOverLength() {
            return overLength;
        }

        public void setOverLength(boolean overLength) {
            this.overLength = overLength;
        }

        public boolean isHasSubmenu() {
            return hasSubmenu;
        }

        public void setHasSubmenu(boolean hasSubmenu) {
            this.hasSubmenu = hasSubmenu;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public List<SubButtonBean> getSub_button() {
            return sub_button;
        }

        public void setSub_button(List<SubButtonBean> sub_button) {
            this.sub_button = sub_button;
        }

        public static class SubButtonBean {
            /**
             * type : click
             * name : 子菜单名称
             * key : 12312312
             */

            private String type;
            private String name;
            private String key;

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getKey() {
                return key;
            }

            public void setKey(String key) {
                this.key = key;
            }
        }
    }
}
