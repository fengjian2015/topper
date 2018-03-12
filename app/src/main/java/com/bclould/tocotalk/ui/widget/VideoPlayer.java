package com.bclould.tocotalk.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.VideoView;

/**
 * Crated by gl152 on 2017/9/25.
 */

public class VideoPlayer extends VideoView {

    private onStatueListener onStatueListener;

    public VideoPlayer(Context context) {
        super(context);
    }

    public VideoPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public VideoPlayer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void start() {
        super.start();
        if (onStatueListener != null) {
            onStatueListener.onStart();
        }
    }

    @Override
    public void pause() {
        super.pause();
        if (onStatueListener != null) {
            onStatueListener.onPause();
        }
    }

    public void setOnStatueListener(onStatueListener onStatueListener) {
        this.onStatueListener = onStatueListener;
    }

    //videoview没有专门的监听去获取video开始和停止状态，自定义个接口实现
    public interface onStatueListener {
        void onStart();

        void onPause();
    }


}
