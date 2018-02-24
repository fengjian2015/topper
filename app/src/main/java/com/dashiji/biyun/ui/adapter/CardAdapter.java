package com.dashiji.biyun.ui.adapter;

/**
 * Created by GA on 2017/10/16.
 */

import android.support.v7.widget.CardView;

public interface CardAdapter {

    int MAX_ELEVATION_FACTOR = 8;

    float getBaseElevation();

    CardView getCardViewAt(int position);

    int getCount();
}
