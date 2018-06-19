package com.bclould.tea.utils;

import android.graphics.Color;
import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.BackgroundColorSpan;
import android.text.style.ClickableSpan;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;


public class CustomLinkMovementMethod extends LinkMovementMethod {
    private ClickableSpan selectedLink = null;
    private long action_down_time = 0;
    private final static int LONG_CLICK_TIME = 500;
    private OnOutsideClickListener listener;

    public CustomLinkMovementMethod(){
        super();
    }

    public CustomLinkMovementMethod(OnOutsideClickListener listener){
        super();
        this.listener = listener;
    }

    @Override
    public boolean onTouchEvent(TextView widget, Spannable buffer,
                                MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        x -= widget.getTotalPaddingLeft();
        y -= widget.getTotalPaddingTop();

        x += widget.getScrollX();
        y += widget.getScrollY();

        Layout layout = widget.getLayout();
        int line = layout.getLineForVertical(y);
        int off = layout.getOffsetForHorizontal(line, x);
        ClickableSpan[] link = buffer.getSpans(off, off, ClickableSpan.class);
        int action = event.getAction();

        if(link == null || link.length == 0){
            if(action == MotionEvent.ACTION_DOWN){
                action_down_time = System.currentTimeMillis();
            }else if(action == MotionEvent.ACTION_UP) {
                //当按住大于500ms时，不认为是点击事件
                if(System.currentTimeMillis() - action_down_time < LONG_CLICK_TIME){
                    if (listener != null) {
                        listener.onOutsideClick(widget);
                    }
                }
                int startLength = buffer.getSpanStart(selectedLink);
                int endLength = buffer.getSpanEnd(selectedLink);
                if(-1 == startLength || -1 == endLength){
                    return super.onTouchEvent(widget, buffer, event);
                }
                buffer.setSpan(new BackgroundColorSpan(Color.TRANSPARENT),
                        startLength , endLength,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                Selection.removeSelection(buffer);

            }
            return super.onTouchEvent(widget, buffer, event);
        }
        switch (action){
            case MotionEvent.ACTION_DOWN:
                buffer.setSpan(new BackgroundColorSpan(0x26000000),
                        buffer.getSpanStart(link[0]), buffer.getSpanEnd(link[0]),
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                Selection.setSelection(buffer,
                        buffer.getSpanStart(link[0]),
                        buffer.getSpanEnd(link[0]));
                int[] locS = new int[2];
                widget.getLocationOnScreen(locS);
                selectedLink = link[0];//记录背景变色的span
                action_down_time = System.currentTimeMillis();
                break;
            case MotionEvent.ACTION_UP:
                //当按住大于500ms时，不认为是点击事件
                if(System.currentTimeMillis() - action_down_time > LONG_CLICK_TIME){
                    buffer.setSpan(new BackgroundColorSpan(Color.TRANSPARENT),
                            buffer.getSpanStart(link[0]), buffer.getSpanEnd(link[0]),
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    Selection.removeSelection(buffer);
                    return true;
                }
//                link[0].onClick(widget);
                buffer.setSpan(new BackgroundColorSpan(Color.TRANSPARENT),
                        buffer.getSpanStart(link[0]), buffer.getSpanEnd(link[0]),
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                Selection.removeSelection(buffer);
                break;
            case MotionEvent.ACTION_MOVE:
                if (link[0] == selectedLink) {//如果移动的时候的x获得的span与原span相等，不做处理
                    return true;
                }
                break;
            default:
                //取消了动作，将背景置为透明
                if (selectedLink != null &&buffer.getSpanStart(selectedLink) >= 0) {
                    buffer.setSpan(new BackgroundColorSpan(Color.TRANSPARENT),
                            buffer.getSpanStart(selectedLink), buffer.getSpanEnd(selectedLink),
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    Selection.removeSelection(buffer);
                }
                break;
        }
        return super.onTouchEvent(widget, buffer, event);
    }

    public interface OnOutsideClickListener{
        public void onOutsideClick(View v);
    }
}
