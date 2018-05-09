package com.bclould.tocotalk.ui.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.TextView;

import com.bclould.tocotalk.R;

/**
 * Created by GA on 2017/9/22.
 */

public class DeleteCacheDialog extends Dialog {

    private final int mDialog_layout;

    public DeleteCacheDialog(int dialog_layout, @NonNull Context context, int theme) {
        super(context, theme);
        mDialog_layout = dialog_layout;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(mDialog_layout);
    }

    public void setTitle(String s) {
        TextView title = (TextView) findViewById(R.id.tv_title);
        title.setText(s);
    }


}
