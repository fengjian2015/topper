package com.bclould.tea.model.base;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by fengjian on 2019/1/7.
 */

public class BaseListInfo {
    private int status;
    private String message;
    private List<Map> data;

    public List<Map> getData() {
        return data;
    }

    public void setData(List<Map> data) {
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
