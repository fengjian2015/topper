package com.bclould.tea.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bclould.tea.base.MyApp;
import com.bclould.tea.R;
import com.bclould.tea.base.BaseActivity;
import com.bclould.tea.model.CardBean;
import com.bclould.tea.ui.adapter.TiXianPVAdapter;
import com.bclould.tea.ui.widget.WithdrawCashDialog;
import com.bclould.tea.utils.ShadowTransformer;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by GA on 2017/9/25.
 */

public class TiXianActivity extends BaseActivity {


    @Bind(R.id.bark)
    ImageView mBark;
    @Bind(R.id.add)
    TextView mAdd;
    @Bind(R.id.editText)
    EditText mEditText;
    @Bind(R.id.usable_money)
    TextView mUsableMoney;
    @Bind(R.id.next_step)
    Button mNextStep;
    @Bind(R.id.viewPager)
    ViewPager mViewPager;
    @Bind(R.id.bank_name)
    TextView mBankName;
    private ShadowTransformer mCardShadowTransformer;
    private List<CardBean> mData;
    private TiXianPVAdapter mTiXianPVAdapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ti_xian);
        ButterKnife.bind(this);
        initViewPage();
        MyApp.getInstance().addActivity(this);
    }

    //初始化ViewPage
    private void initViewPage() {

        mTiXianPVAdapter = new TiXianPVAdapter(this);
        mTiXianPVAdapter.addCardItem(new CardBean("中国银行", "* * * * 0 7 9 6"));
        mTiXianPVAdapter.addCardItem(new CardBean("建设银行", "* * * * 0 0 2 3"));
        mTiXianPVAdapter.addCardItem(new CardBean("工商银行", "* * * * 4 8 9 5"));
        mTiXianPVAdapter.addCardItem(new CardBean("农业银行", "* * * * 9 9 9 9"));

        mCardShadowTransformer = new ShadowTransformer(mViewPager, mTiXianPVAdapter);
        mCardShadowTransformer.enableScaling(true);

        mViewPager.setAdapter(mTiXianPVAdapter);
        mViewPager.setPageTransformer(false, mCardShadowTransformer);
        mViewPager.setOffscreenPageLimit(3);

        //监听ViewPage切换
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                mBankName.setText(mData.get(position).getBackName());

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


    }

    //点击事件的处理
    @OnClick({R.id.bark, R.id.add, R.id.next_step})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:

                finish();

                break;
            case R.id.add:

                startActivity(new Intent(this, BankCardBindingActivity.class));

                break;
            case R.id.next_step:

                showDialog();

                break;
        }
    }

    //显示Dialog
    private void showDialog() {

        String money = mEditText.getText().toString().trim();

        if (!money.isEmpty()) {

            WithdrawCashDialog withdrawCashDialog = new WithdrawCashDialog(this);

            withdrawCashDialog.show();

            dialogClick(withdrawCashDialog);

        } else {

            Toast.makeText(this, "金额不能为空", Toast.LENGTH_SHORT).show();

        }
    }

    //Dialog的点击事件处理
    private void dialogClick(final WithdrawCashDialog dialog) {

        Button cancel = (Button) dialog.findViewById(R.id.cache_dialog_cancel);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(TiXianActivity.this, "点击了取消", Toast.LENGTH_SHORT).show();
                dialog.dismiss();

            }
        });

        Button confirm = (Button) dialog.findViewById(R.id.cache_dialog_confirm);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(TiXianActivity.this, "点击了确定", Toast.LENGTH_SHORT).show();
                dialog.dismiss();

            }
        });

    }

    public void setData(List<CardBean> data) {

        mData = data;

    }
}
