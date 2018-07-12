package com.bclould.tea.ui.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;


/**
 * Created by Pooholah on 2016/5/25.
 */
@SuppressLint("AppCompatCustomView")
public class TextViewDoubleClickable extends TextView {
    /**
     * 记录点击的时间
     */
    private long mClick_time = 0;
    private DoubleClickListener mDoubleClickListener;

    public TextViewDoubleClickable(Context context) {
        this(context, null);
    }

    public TextViewDoubleClickable(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TextViewDoubleClickable(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    private void init() {
        mClick_time = System.currentTimeMillis();
    }

    public void setOnDoubleClickListener(
            final DoubleClickListener mDoubleClickListener) {
        this.mDoubleClickListener = mDoubleClickListener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            long newClickTime = System.currentTimeMillis();
            if (newClickTime - mClick_time < 1000) {//调用双击事件
                if(mDoubleClickListener != null) {
                    mDoubleClickListener.onDoubleClick(this);
                }
            }
            mClick_time = newClickTime;
        }
        return super.onTouchEvent(event);
    }

    public interface DoubleClickListener {
        void onDoubleClick(View v);
    }
}
