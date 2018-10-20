package com.bclould.tea.model;

import java.util.List;

/**
 * Created by GIjia on 2018/9/7.
 */

public class LocationInfo {
    /**
     * status : 0
     * message : query ok
     * count : 10
     * data : [{"id":"13393830880533983327","title":"后海酒吧美食街","address":"北京市西城区后海银锭桥附近(烟袋斜街西口)","category":"购物:商业步行街","type":0,"location":{"lat":39.93892,"lng":116.39328},"adcode":110102,"province":"北京市","city":"北京市","district":"西城区"},{"id":"13197297789974085370","title":"王府井美食广场","address":"北京市东城区王府井大街200号工美大厦地下1层","category":"美食:小吃快餐","type":0,"location":{"lat":39.91091,"lng":116.41156},"adcode":110101,"province":"北京市","city":"北京市","district":"东城区"},{"id":"4412846406955100612","title":"鲜鱼口老字号美食街","address":"北京市东城区鲜鱼口街72号","category":"美食:中餐厅:其它中餐厅","type":0,"location":{"lat":39.89626,"lng":116.40075},"adcode":110101,"province":"北京市","city":"北京市","district":"东城区"},{"id":"15151331489058364135","title":"畅春园·食街","address":"北京市海淀区颐和园路8号(海淀桥北)","category":"购物:商业步行街","type":0,"location":{"lat":39.98863,"lng":116.30478},"adcode":110108,"province":"北京市","city":"北京市","district":"海淀区"},{"id":"8301295427127788926","title":"王府井小吃街","address":"北京市东城区王府井步行街好友世界商场南侧","category":"美食:小吃快餐","type":0,"location":{"lat":39.91127,"lng":116.4112},"adcode":110101,"province":"北京市","city":"北京市","district":"东城区"},{"id":"11947492581632505065","title":"全聚德烤鸭店(帅府园胡同店)","address":"北京市东城区王府井大街帅府园胡同9号","category":"美食:中餐厅:北京菜","type":0,"location":{"lat":39.91222,"lng":116.41192},"adcode":110101,"province":"北京市","city":"北京市","district":"东城区"},{"id":"2151925127924188045","title":"鼎好美食广场","address":"北京市海淀区中关村大街3号鼎好电子商城5楼(近新中关购物中心)","category":"美食:小吃快餐","type":0,"location":{"lat":39.983856201,"lng":116.314361572},"adcode":110108,"province":"北京市","city":"北京市","district":"海淀区"},{"id":"12511480423602913915","title":"可味美食城(三里屯店)","address":"北京市朝阳区工人体育场北路8号院三里屯SOHO6号商场B1层","category":"美食:小吃快餐","type":0,"location":{"lat":39.93167,"lng":116.45363},"adcode":110105,"province":"北京市","city":"北京市","district":"朝阳区"},{"id":"6312087529801047215","title":"后厨时代美食广场(远洋光华国际店)","address":"北京市朝阳区金桐西路12-7号远洋·光华国际D座B1-05","category":"美食:小吃快餐","type":0,"location":{"lat":39.914928342,"lng":116.454525783},"adcode":110105,"province":"北京市","city":"北京市","district":"朝阳区"},{"id":"6331598348281104017","title":"北小河美食街","address":"北京市朝阳区广顺北大街河荫中路2区","category":"美食:中餐厅:其它中餐厅","type":0,"location":{"lat":40.005268097,"lng":116.469863892},"adcode":110105,"province":"北京市","city":"北京市","district":"朝阳区"}]
     * request_id : 6667204920964266960
     */

    private int status;
    private String message;
    private int count;
    private String request_id;
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

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getRequest_id() {
        return request_id;
    }

    public void setRequest_id(String request_id) {
        this.request_id = request_id;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * id : 13393830880533983327
         * title : 后海酒吧美食街
         * address : 北京市西城区后海银锭桥附近(烟袋斜街西口)
         * category : 购物:商业步行街
         * type : 0
         * location : {"lat":39.93892,"lng":116.39328}
         * adcode : 110102
         * province : 北京市
         * city : 北京市
         * district : 西城区
         */

        private String id;
        private String title;
        private String address;
        private String category;
        private int type;
        private LocationBean location;
        private int adcode;
        private String province;
        private String city;
        private String district;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public LocationBean getLocation() {
            return location;
        }

        public void setLocation(LocationBean location) {
            this.location = location;
        }

        public int getAdcode() {
            return adcode;
        }

        public void setAdcode(int adcode) {
            this.adcode = adcode;
        }

        public String getProvince() {
            return province;
        }

        public void setProvince(String province) {
            this.province = province;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getDistrict() {
            return district;
        }

        public void setDistrict(String district) {
            this.district = district;
        }

        public static class LocationBean {
            /**
             * lat : 39.93892
             * lng : 116.39328
             */

            private double lat;
            private double lng;

            public double getLat() {
                return lat;
            }

            public void setLat(double lat) {
                this.lat = lat;
            }

            public double getLng() {
                return lng;
            }

            public void setLng(double lng) {
                this.lng = lng;
            }
        }
    }
}
