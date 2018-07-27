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
        private MenuBean menu;

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

        public MenuBean getMenu() {
            return menu;
        }

        public void setMenu(MenuBean menu) {
            this.menu = menu;
        }

        public static class MenuBean {
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
    }
}
