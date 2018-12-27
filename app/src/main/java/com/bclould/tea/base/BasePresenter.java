package com.bclould.tea.base;

import android.app.Activity;
import android.content.Context;

/**
 * Created by kullo.me on 2017/9/14.
 */
public interface BasePresenter {
    void bindView(BaseView view);

    <T extends Context> void start (T context);

    void release();
}
