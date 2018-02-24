package com.dashiji.biyun.utils;


import android.animation.ObjectAnimator;
import android.view.View;

/**
 * Created by GA on 2017/10/25.
 */

public class AnimatorTool {

    private static AnimatorTool instance;

    public static AnimatorTool getInstance() {

        if (instance == null) {

            instance = new AnimatorTool();

        }

        return instance;
    }

    //抖动动画
    public void editTextAnimator(View view) {

        ObjectAnimator translationY = ObjectAnimator.ofFloat(view, "translationY", 0, -10, 10, 0);

        translationY.setDuration(400);

        translationY.start();

    }

}
