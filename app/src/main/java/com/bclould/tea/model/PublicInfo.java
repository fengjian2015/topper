package com.bclould.tea.model;

import android.support.annotation.NonNull;

import com.bclould.tea.utils.Cn2Spell;
import com.bclould.tea.utils.StringUtils;

import java.util.List;

/**
 * Created by GIjia on 2018/7/27.
 */

public class PublicInfo{

    /**
     * status : 1
     * message : success
     * data : [{"id":3,"name":"xihongwei","logo":"","desc":"","menu":""},{"id":4,"name":"123123","logo":"","desc":"","menu":{"button":[{"type":"click","name":"菜单名称","key":"3123132","subMenu":false,"tabSubMenu":false,"overLength":true,"hasSubmenu":false,"sub_button":[{"type":"click","name":"子菜单名称","key":"12312312"},{"type":"click","name":"子菜单名称","key":"12312312"},{"type":"click","name":"子菜单名称","key":"12312312"}]},{"type":"click","name":"菜单名称","key":"3123132","subMenu":false,"tabSubMenu":false,"overLength":true,"hasSubmenu":false,"sub_button":[{"type":"click","name":"子菜单名称","key":"12312312"},{"type":"click","name":"子菜单名称","key":"12312312"},{"type":"click","name":"子菜单名称","key":"12312312"}]},{"type":"view","name":"菜单名称","key":"3123132","subMenu":false,"tabSubMenu":true,"overLength":true,"hasSubmenu":true,"url":"www.baidu.com"}]}}]
     */

    private int status;
    private String message;
    private List<DataBean> data;

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

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean implements Comparable<PublicInfo.DataBean>{
        /**
         * id : 3
         * name : xihongwei
         * logo :
         * desc :
         * menu :
         */

        private int id;
        private String name;
        private String logo;
        private String desc;
        private String menu;
        private String firstLetter;
        private String pinyin;

        public String getFirstLetter() {
            return firstLetter;
        }

        public void setFirstLetter(String firstLetter) {
            this.firstLetter = firstLetter;
        }

        public String getPinyin() {
            return pinyin;
        }

        public void setPinyin(String pinyin) {
            this.pinyin = pinyin;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        @Override
        public int compareTo(@NonNull DataBean dataBean) {
            if (firstLetter.equals("#") && !dataBean.getFirstLetter().equals("#")) {
                return 1;
            } else if (!firstLetter.equals("#") && dataBean.getFirstLetter().equals("#")) {
                return -1;
            } else {
                return pinyin.compareToIgnoreCase(dataBean.getPinyin());
            }
        }

        public void setName(String name) {
            this.name = name;
            pinyin = Cn2Spell.getPinYin(name); // 根据姓名获取拼音
            firstLetter="";
            if(!StringUtils.isEmpty(pinyin)) {
                firstLetter = pinyin.substring(0, 1).toUpperCase(); // 获取拼音首字母并转成大写
            }
            if (!firstLetter.matches("[A-Z]")) { // 如果不在A-Z中则默认为“#”
                firstLetter = "#";
            }
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

        public String getMenu() {
            return menu;
        }

        public void setMenu(String menu) {
            this.menu = menu;
        }
    }
}
