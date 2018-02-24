package com.dashiji.biyun.ui.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.dashiji.biyun.R;

/**
 * Created by GA on 2017/11/2.
 */

public class PushingDialog extends Dialog {

    public PushingDialog(@NonNull Context context, int theme) {
        super(context, theme);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_pushing);
    }
}
