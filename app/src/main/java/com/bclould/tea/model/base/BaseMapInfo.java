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

    public HashMap getData() {
        return data;
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
