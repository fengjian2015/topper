package com.bclould.tea.network.https;

/**
 * Created by GIjia on 2018/7/29.
 */

public interface GlideProgressListener {
    public void start(String url);
    public void progress(int progress,String url);
    public void end(String url);
}
