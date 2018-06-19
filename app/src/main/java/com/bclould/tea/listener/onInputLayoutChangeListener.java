package com.bclould.tea.listener;

import android.graphics.Rect;
import android.view.View;
import android.view.ViewTreeObserver;

/**
 * Created by GA on 2018/4/27.
 */

public abstract class onInputLayoutChangeListener implements ViewTreeObserver.OnGlobalLayoutListener {

    private View rootView;
    private int curRectHeight = -1;

    public onInputLayoutChangeListener(View rootView) {
        this.rootView = rootView;
    }

    @Override
    public void onGlobalLayout() {
        Rect rect = new Rect();
        rootView.getWindowVisibleDisplayFrame(rect);
        int displayHight = rect.bottom - rect.top;
        int height = rootView.getHeight();
//        if (curRectHeight != displayHight) {
//            curRectHeight = displayHight;
//            onLayoutChange(displayHight, height);
//        }
        onLayoutChange(displayHight, height);
    }

    public abstract void onLayoutChange(int intputTop, int windowHeight);
}