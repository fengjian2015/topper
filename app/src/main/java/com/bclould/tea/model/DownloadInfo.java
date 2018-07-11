package com.bclould.tea.model;

/**
 * Created by GA on 2018/7/11.
 */

public class DownloadInfo {
    private long current_size;
    private long total_size;

    public long getCurrent_size() {
        return current_size;
    }

    public void setCurrent_size(long current_size) {
        this.current_size = current_size;
    }

    public long getTotal_size() {
        return total_size;
    }

    public void setTotal_size(long total_size) {
        this.total_size = total_size;
    }
}
