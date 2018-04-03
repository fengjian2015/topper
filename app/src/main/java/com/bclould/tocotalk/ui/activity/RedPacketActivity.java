package com.bclould.tocotalk.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bclould.tocotalk.Presenter.RedRecordPresenter;
import com.bclould.tocotalk.R;
import com.bclould.tocotalk.history.DBManager;
import com.bclould.tocotalk.model.GrabRedInfo;
import com.bclould.tocotalk.model.UserInfo;
import com.bclould.tocotalk.ui.adapter.RedPacketRVAdapter;
import com.bclould.tocotalk.ui.widget.ChangeTextSpaceView;
import com.bclould.tocotalk.utils.Constants;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by GA on 2017/12/29.
 */

public class RedPacketActivity extends AppCompatActivity {

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
    @Bind(R.id.ll_details)
    LinearLayout mLlDetails;
    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    private String mCoin;
    private String mCount;
    private String mRemark;
    private String mUser;
    private Bitmap mBitmap;
    private DBManager mMgr;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(getColor(R.color.redpacket2));
        setContentView(R.layout.activity_red_packet);
        mMgr = new DBManager(this);
        ButterKnife.bind(this);
        initData();
    }

    private void initData() {
        Intent intent = getIntent();
        boolean from = intent.getBooleanExtra("from", false);
        boolean type = intent.getBooleanExtra("type", false);
        if (type) {
            mTvRedpacketRecord.setVisibility(View.VISIBLE);
        } else {
            mTvRedpacketRecord.setVisibility(View.GONE);
        }
        if (from) {
            Bundle bundle = intent.getExtras();
            byte[] bytes = bundle.getByteArray("image");
            mUser = bundle.getString("user");
            GrabRedInfo grabRedInfo = (GrabRedInfo) bundle.getSerializable("grabRedInfo");
            mCount = grabRedInfo.getData().getTotal_money();
            mCoin = grabRedInfo.getData().getCoin_name();
            mRemark = grabRedInfo.getData().getIntro();
            List<GrabRedInfo.DataBean.LogBean> mLogBeanList = grabRedInfo.getData().getLog();
            initRecylerView(mLogBeanList);
            mBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            mTvCoin.setText(mCoin);
            mTvCount.setText(mCount);
            mTvCount.setSpacing(2);
            mTvRemark.setText(mRemark);
            mTvName.setText(mUser.substring(0, mUser.indexOf("@")));
            mIvTouxiang.setImageBitmap(mBitmap);
        } else {
            String id = intent.getStringExtra("id");
            RedRecordPresenter redRecordPresenter = new RedRecordPresenter(this);
            redRecordPresenter.signRpLog(Integer.parseInt(id), new RedRecordPresenter.CallBack2() {
                @Override
                public void send(GrabRedInfo.DataBean data) {
                    List<GrabRedInfo.DataBean.LogBean> logBeanList = data.getLog();
                    initRecylerView(logBeanList);
                    List<UserInfo> userInfos = mMgr.queryUser(data.getSend_rp_user_name() + "@" + Constants.DOMAINNAME);
                    mTvCoin.setText(data.getCoin_name());
                    mTvCount.setText(data.getTotal_money());
                    mTvCount.setSpacing(2);
                    mTvRemark.setText(data.getIntro());
                    mTvName.setText(data.getSend_rp_user_name());
                    mIvTouxiang.setImageBitmap(BitmapFactory.decodeFile(userInfos.get(0).getPath()));
                }
            });
        }
    }

    private void initRecylerView(List<GrabRedInfo.DataBean.LogBean> mLogBeanList) {
        RedPacketRVAdapter redPacketRVAdapter = new RedPacketRVAdapter(this, mLogBeanList, mMgr);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(redPacketRVAdapter);
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
