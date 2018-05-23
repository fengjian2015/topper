package com.bclould.tocotalk.ui.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bclould.tocotalk.R;

/**
 * Created by GA on 2017/9/22.
 */

public class CenterEntryDialog extends Dialog {

    private final int mDialog_layout=R.layout.dialog_center_entry;

    public CenterEntryDialog( @NonNull Context context, int theme) {
        super(context, theme);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(mDialog_layout);
    }

    public void isTop(String istop,Context context){
        Button btn_stick = (Button) findViewById(R.id.btn_stick);
        if("true".equals(istop)){
            btn_stick.setText(context.getString(R.string.cancel_the_top));
        }else{
            btn_stick.setText(context.getString(R.string.stick));
        }
    }

}
