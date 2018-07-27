package com.bclould.tea.ui.activity;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bclould.tea.Presenter.PublicPresenter;
import com.bclould.tea.R;
import com.bclould.tea.base.BaseActivity;
import com.bclould.tea.base.MyApp;
import com.bclould.tea.model.PublicDetailsInfo;
import com.bclould.tea.utils.UtilTool;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

@RequiresApi(api = Build.VERSION_CODES.N)
public class PublicDetailsActivity extends BaseActivity {
    @Bind(R.id.iv_else)
    ImageView mIvElse;
    @Bind(R.id.iv_head)
    ImageView mIvHead;
    @Bind(R.id.tv_name)
    TextView mTvName;
    @Bind(R.id.tv_desc)
    TextView mTvDesc;
    @Bind(R.id.tv_state)
    TextView mTvState;
    @Bind(R.id.ll_error)
    LinearLayout mLlError;
    @Bind(R.id.ll_data)
    LinearLayout mLlData;

    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_public_details);
        ButterKnife.bind(this);
        MyApp.getInstance().addActivity(this);
        initGetintent();
        init();
    }

    private void setView(PublicDetailsInfo publicDetailsInfo){
        UtilTool.setCircleImg(this,publicDetailsInfo.getData().getLogo(),mIvHead);
        mTvName.setText(publicDetailsInfo.getData().getName());
        mTvDesc.setText(publicDetailsInfo.getData().getDesc());
    }

    private void init() {
        new PublicPresenter(this).publicDeltails(UtilTool.parseInt(id), new PublicPresenter.CallBack1() {
            @Override
            public void send(PublicDetailsInfo publicDetailsInfo) {
                mLlError.setVisibility(View.GONE);
                mLlData.setVisibility(View.VISIBLE);
                setView(publicDetailsInfo);
            }

            @Override
            public void error() {
                mLlError.setVisibility(View.VISIBLE);
                mLlData.setVisibility(View.GONE);
            }
        });
    }

    @OnClick({R.id.bark, R.id.iv_else, R.id.tv_state,R.id.ll_error})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;
            case R.id.iv_else:
                //更多取關
                break;
            case R.id.tv_state:
                //關注或者發消息
                publicAdd();
                break;
            case R.id.ll_error:
                init();
                break;
        }
    }

    private void publicAdd(){
        new PublicPresenter(this).publicAdd(UtilTool.parseInt(id), new PublicPresenter.CallBack1() {
            @Override
            public void send(PublicDetailsInfo publicDetailsInfo) {

            }

            @Override
            public void error() {

            }
        });
    }

    private void initGetintent() {
        id = getIntent().getStringExtra("id");
    }
}
