package com.bclould.tea.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bclould.tea.base.MyApp;
import com.bclould.tea.R;
import com.bclould.tea.base.BaseActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by GA on 2017/9/27.
 */

public class BankCardBindingActivity4 extends BaseActivity {

    @Bind(R.id.bark)
    ImageView mBark;
    @Bind(R.id.complete)
    Button mComplete;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_card_binding4);
        ButterKnife.bind(this);
        MyApp.getInstance().addActivity(this);
    }

    @OnClick({R.id.bark, R.id.complete})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:

                finish();

                break;
            case R.id.complete:

                finish();

                break;
        }
    }
}
