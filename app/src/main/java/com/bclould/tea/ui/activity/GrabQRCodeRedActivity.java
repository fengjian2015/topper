package com.bclould.tea.ui.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.bclould.tea.Presenter.RedPacketPresenter;
import com.bclould.tea.R;
import com.bclould.tea.base.BaseActivity;
import com.bclould.tea.history.DBManager;
import com.bclould.tea.model.GrabRedInfo;
import com.bclould.tea.ui.widget.ChangeTextSpaceView;
import com.bclould.tea.utils.UtilTool;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by GA on 2018/1/23.
 */

public class GrabQRCodeRedActivity extends BaseActivity {

    @Bind(R.id.bark)
    ImageView mBark;
    @Bind(R.id.tv_redpacket_record)
    TextView mTvRedpacketRecord;
    @Bind(R.id.title)
    RelativeLayout mTitle;
    @Bind(R.id.iv_touxiang)
    ImageView mIvTouxiang;
    @Bind(R.id.tv_name)
    TextView mTvName;
    @Bind(R.id.tv_remark)
    TextView mTvRemark;
    @Bind(R.id.tv_count)
    ChangeTextSpaceView mTvCount;
    @Bind(R.id.tv_coin)
    TextView mTvCoin;
    private String mId;
    private DBManager mMgr;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.redpacket4));
        }
        setContentView(R.layout.activity_grab_qr_red);
        ButterKnife.bind(this);
        mMgr = new DBManager(this);
        Intent intent = getIntent();
        mId = intent.getStringExtra("id");
        boolean type = intent.getBooleanExtra("type", false);
        if (type) {
            mTvRedpacketRecord.setVisibility(View.VISIBLE);
        } else {
            mTvRedpacketRecord.setVisibility(View.GONE);
        }
        initData();
    }
    private void initData() {
        RedPacketPresenter redPacketPresenter = new RedPacketPresenter(this);
        redPacketPresenter.grabQrRed(Integer.parseInt(mId), new RedPacketPresenter.CallBack3() {
            @Override
            public void send(GrabRedInfo.DataBean dataBean) {
                if (dataBean != null) {
                    UtilTool.Log("日志", dataBean.getSend_rp_user_name());
                    mTvName.setText(dataBean.getSend_rp_user_name());
                    mTvRemark.setText(dataBean.getIntro());
                    if (dataBean.getLog().size() != 0)
                        mTvCount.setText(dataBean.getLog().get(0).getMoney());
                    mTvCoin.setText(dataBean.getCoin_name());
                    UtilTool.setCircleImg(GrabQRCodeRedActivity.this,dataBean.getAvatar(), mIvTouxiang);
                    //                    mIvTouxiang.setImageBitmap(UtilTool.getImage(mMgr, user, GrabQRCodeRedActivity.this));
                }
            }
        });
    }

    @OnClick({R.id.bark, R.id.tv_redpacket_record})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;
            case R.id.tv_redpacket_record:
                startActivity(new Intent(this, RedPacketRecordActivity.class));
                break;
        }
    }
}
