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

public class ChatCopyDialog extends Dialog {

    private final int mDialog_layout;

    public ChatCopyDialog(int dialog_layout, @NonNull Context context, int theme) {
        super(context, theme);
        mDialog_layout = dialog_layout;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(mDialog_layout);
    }

    public void isCopy(boolean isCopy) {
        Button title = (Button) findViewById(R.id.btn_copy);
        TextView tv_line=(TextView)findViewById(R.id.tv_line);
        if(isCopy){
            title.setVisibility(View.VISIBLE);
            tv_line.setVisibility(View.VISIBLE);
        }
    }

    public void  isShowTransmit(boolean isShowTransmit){
        Button transmit = (Button) findViewById(R.id.btn_transmit);
        TextView tv_line1=(TextView)findViewById(R.id.tv_line1);
        if(isShowTransmit){
            transmit.setVisibility(View.VISIBLE);
            tv_line1.setVisibility(View.VISIBLE);
        }
    }
}
