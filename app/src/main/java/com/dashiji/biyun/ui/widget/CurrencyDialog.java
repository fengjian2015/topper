package com.dashiji.biyun.ui.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;

/**
 * Created by GA on 2017/9/22.
 */

public class CurrencyDialog extends Dialog {


    private int mDialog_currency;

    public CurrencyDialog(int dialog_currency, @NonNull Context context, int theme) {
        super(context, theme);
        mDialog_currency = dialog_currency;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(mDialog_currency);
    }

}
