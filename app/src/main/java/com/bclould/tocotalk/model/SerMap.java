package com.bclould.tocotalk.model;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by GA on 2018/3/23.
 */

public class SerMap  implements Serializable {
    public HashMap<String,String> map;
    public  SerMap(){

    }

    public HashMap<String, String> getMap() {
        return map;
    }

    public void setMap(HashMap<String, String> map) {
        this.map = map;

    }
}