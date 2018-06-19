package com.bclould.tea.ui.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.bclould.tea.R;

/**
 * Created by GA on 2017/9/22.
 */

public class WithdrawCashDialog extends Dialog {

    public WithdrawCashDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.dialog_withdraw_cash);
    }
}
