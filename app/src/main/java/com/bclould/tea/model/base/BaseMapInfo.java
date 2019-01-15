package com.bclould.tea.model.base;

import java.util.HashMap;
import java.util.List;

/**
 * Created by fengjian on 2019/1/7.
 */

public class BaseMapInfo {
    private int status;
    private String message;
    private HashMap data;
    private int type;
    public HashMap getData() {
        return data;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setData(HashMap data) {
        this.data = data;
    }

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
}
