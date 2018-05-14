package com.bclould.tocotalk.utils;

import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by GA on 2018/5/10.
 */

public class SpaceItemDecoration2 extends RecyclerView.ItemDecoration {
    int mSpace;

    public SpaceItemDecoration2(int space) {
        mSpace = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        LinearLayoutManager layoutManager = (LinearLayoutManager) parent.getLayoutManager();
        outRect.top = mSpace;
    }
}
