package com.bclould.tocotalk.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bclould.tocotalk.base.MyApp;
import com.bclould.tocotalk.R;
import com.bclould.tocotalk.base.BaseActivity;
import com.bclould.tocotalk.ui.adapter.BankCardRVAdapter;
import com.bclould.tocotalk.utils.FullyLinearLayoutManager;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by GA on 2017/9/26.
 */

public class BankCardActivity extends BaseActivity {


    @Bind(R.id.bark)
    ImageView mBark;
    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @Bind(R.id.add_bank_card)
    Button mAddBankCard;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_card);
        ButterKnife.bind(this);
        initRecyclerView();
        MyApp.getInstance().addActivity(this);
    }

    //初始化条目
    private void initRecyclerView() {

        mRecyclerView.setLayoutManager(new FullyLinearLayoutManager(this));

        mRecyclerView.setAdapter(new BankCardRVAdapter(this));

        mRecyclerView.setNestedScrollingEnabled(false);

    }

    //点击事件的处理
    @OnClick({R.id.bark, R.id.add_bank_card})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:

                finish();

                break;
            case R.id.add_bank_card:

                startActivity(new Intent(this, BankCardBindingActivity.class));

                break;
        }
    }
}
