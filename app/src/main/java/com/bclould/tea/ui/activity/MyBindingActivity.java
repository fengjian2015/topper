package com.bclould.tea.ui.activity;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import com.bclould.tea.Presenter.DistributionPresenter;
import com.bclould.tea.R;
import com.bclould.tea.base.BaseActivity;
import com.bclould.tea.base.MyApp;
import com.bclould.tea.history.DBManager;
import com.bclould.tea.model.BindingInfo;
import com.bclould.tea.utils.UtilTool;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

@RequiresApi(api = Build.VERSION_CODES.N)
public class MyBindingActivity extends BaseActivity {

    @Bind(R.id.touxiang)
    ImageView mTouxiang;
    @Bind(R.id.tv_user)
    TextView mTvUser;
    @Bind(R.id.tv_all_money)
    TextView mTvAllMoney;
    @Bind(R.id.tv_released)
    TextView mTvReleased;
    @Bind(R.id.tv_account_fund)
    TextView mTvAccountFund;

    private DBManager mMgr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyApp.getInstance().addActivity(this);
        setContentView(R.layout.activity_my_binding);
        ButterKnife.bind(this);
        setTitle(getString(R.string.my_binding));
        mMgr=new DBManager(this);
        initHttp();
    }

    private void initHttp(){
        new DistributionPresenter(this).infoFTC(new DistributionPresenter.CallBack1() {
            @Override
            public void send(BindingInfo baseInfo) {
                setView(baseInfo);
            }

            @Override
            public void error() {

            }
        });
    }

    private void setView(BindingInfo baseInfo){
        UtilTool.getImage(mMgr, UtilTool.getTocoId(), this, mTouxiang);
        mTvUser.setText(baseInfo.getData().getEmail());
        mTvAllMoney.setText(baseInfo.getData().getTotal_asset());
        mTvReleased.setText(baseInfo.getData().getReleased());
        mTvAccountFund.setText(baseInfo.getData().getFund());
    }

    @OnClick({R.id.bark})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;

        }
    }
}
