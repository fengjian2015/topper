package com.bclould.tea.ui.activity;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;

import com.bclould.tea.R;
import com.bclould.tea.base.BaseActivity;

/**
 * Created by GA on 2018/4/8.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class PawnActivity extends BaseActivity{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_financing);
    }
}