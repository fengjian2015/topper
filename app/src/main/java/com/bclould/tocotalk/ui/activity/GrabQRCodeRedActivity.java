package com.bclould.tocotalk.ui.activity;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bclould.tocotalk.Presenter.RedPacketPresenter;
import com.bclould.tocotalk.R;
import com.bclould.tocotalk.history.DBManager;
import com.bclould.tocotalk.model.GrabRedInfo;
import com.bclould.tocotalk.model.UserInfo;
import com.bclould.tocotalk.ui.widget.ChangeTextSpaceView;
import com.bclould.tocotalk.utils.Constants;
import com.bclould.tocotalk.utils.UtilTool;
import com.bclould.tocotalk.xmpp.XmppConnection;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by GA on 2018/1/23.
 */

public class GrabQRCodeRedActivity extends AppCompatActivity {

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
        getWindow().setStatusBarColor(getColor(R.color.redpacket3));
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
                UtilTool.Log("日志", dataBean.getSend_rp_user_name());
                mTvName.setText(dataBean.getSend_rp_user_name());
                mTvRemark.setText(dataBean.getIntro());
                mTvCount.setText(dataBean.getLog().get(0).getMoney());
                mTvCoin.setText(dataBean.getCoin_name());
                String user = dataBean.getSend_rp_user_name() + "@" + Constants.DOMAINNAME;
                if (mMgr.findUser(user)) {
                    List<UserInfo> info = mMgr.queryUser(user);
                    mIvTouxiang.setImageBitmap(BitmapFactory.decodeFile(info.get(0).getPath()));
                } else {
                    Drawable image = XmppConnection.getInstance().getUserImage(user);
                    mIvTouxiang.setImageDrawable(image);
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
