package com.bclould.tea.ui.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.bclould.tea.Presenter.RedRecordPresenter;
import com.bclould.tea.R;
import com.bclould.tea.base.BaseActivity;
import com.bclould.tea.history.DBManager;
import com.bclould.tea.history.DBRoomMember;
import com.bclould.tea.model.GrabRedInfo;
import com.bclould.tea.ui.adapter.RedPacketRVAdapter;
import com.bclould.tea.ui.widget.ChangeTextSpaceView;
import com.bclould.tea.utils.UtilTool;
import java.util.List;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by GA on 2017/12/29.
 */

public class RedPacketActivity extends BaseActivity {

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
    @Bind(R.id.tv_property)
    TextView mTvProperty;
    @Bind(R.id.tv_content)
    TextView mTvContent;
    @Bind(R.id.view_line)
    TextView mViewLine;
    private String mCoin;
    private String mCount;
    private String mRemark;
    private String mUser;
    private String mName;
    //    private Bitmap mBitmap;
    private DBManager mMgr;
    private DBRoomMember mDBRoomMember;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(getResources().getColor(R.color.redpacket5));
        }
        setContentView(R.layout.activity_red_packet);
        mMgr = new DBManager(this);
        mDBRoomMember = new DBRoomMember(this);
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
//            byte[] bytes = bundle.getByteArray("image");
            mUser = bundle.getString("user");
            mName = bundle.getString("name");
            GrabRedInfo grabRedInfo = (GrabRedInfo) bundle.getSerializable("grabRedInfo");
//            int who = intent.getIntExtra("who", 0);
            mTvContent.setText(grabRedInfo.getData().getDesc());
            mCount = grabRedInfo.getData().getTotal_money();
            mCoin = grabRedInfo.getData().getCoin_name();
            mRemark = grabRedInfo.getData().getIntro();
            List<GrabRedInfo.DataBean.LogBean> mLogBeanList = grabRedInfo.getData().getLog();
//            if (who == 0) {
//                if (mLogBeanList.size() == 0) {
//                    mTvHint.setText(getString(R.string.wait_get_red));
//                } else {
//                    mTvHint.setText(getString(R.string.ta_already_received));
//                }
//            } else if (who == 1) {
//                mTvHint.setText(getString(R.string.red_packet_hint));
//            }
            initRecylerView(mLogBeanList, grabRedInfo.getData());
//            mBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            mTvCoin.setText(mCoin);
            if (UtilTool.parseDouble(grabRedInfo.getData().getMoney()) <= 0) {
                mTvProperty.setVisibility(View.GONE);
                mTvCount.setVisibility(View.GONE);
                mTvCoin.setVisibility(View.GONE);
            } else {
                mTvProperty.setText(grabRedInfo.getData().getAssets_notice());
                mTvCount.setText(grabRedInfo.getData().getMoney());
                mTvCount.setSpacing(2);
                mTvProperty.setVisibility(View.VISIBLE);
                mTvCount.setVisibility(View.VISIBLE);
                mTvCoin.setVisibility(View.VISIBLE);
            }
            mTvRemark.setText(mRemark);
            mTvName.setText(mName + getString(R.string.red_envelope));
//            mIvTouxiang.setImageBitmap(mBitmap);
            UtilTool.getImage(this, mIvTouxiang, mDBRoomMember, mMgr, mUser);
//            if(grabRedInfo.getStatus()==2){
//                mTvHint.setText(getString(R.string.red_envelope_been_robbed));
//            }
//            mTvHint.setVisibility(View.VISIBLE);
        } else {
//            mTvHint.setVisibility(View.GONE);
            String id = intent.getStringExtra("id");
            RedRecordPresenter redRecordPresenter = new RedRecordPresenter(this);
            redRecordPresenter.signRpLog(Integer.parseInt(id), new RedRecordPresenter.CallBack2() {
                @Override
                public void send(GrabRedInfo grabRedInfo) {
                    GrabRedInfo.DataBean data = grabRedInfo.getData();
                    if(data==null)return;
                    List<GrabRedInfo.DataBean.LogBean> logBeanList = data.getLog();
                    mCoin = data.getCoin_name();
                    mTvCoin.setText(data.getCoin_name());
                    mTvContent.setText(grabRedInfo.getData().getDesc());
                    if (UtilTool.parseDouble(grabRedInfo.getData().getMoney()) <= 0) {
                        mTvProperty.setVisibility(View.GONE);
                        mTvCount.setVisibility(View.GONE);
                        mTvCoin.setVisibility(View.GONE);
                    } else {
                        mTvCount.setText(grabRedInfo.getData().getMoney());
                        mTvCount.setSpacing(2);
                        mTvProperty.setText(grabRedInfo.getData().getAssets_notice());
                        mTvProperty.setVisibility(View.VISIBLE);
                    }

                    mTvRemark.setText(data.getIntro());
                    mTvName.setText(data.getSend_rp_user_name());
                    initRecylerView(logBeanList, data);
                    UtilTool.setCircleImg(RedPacketActivity.this, data.getAvatar(), mIvTouxiang);
                    //                    mIvTouxiang.setImageBitmap(UtilTool.getImage(mMgr, jid, RedPacketActivity.this));

                }
            });
        }
    }

    private void initRecylerView(List<GrabRedInfo.DataBean.LogBean> mLogBeanList, GrabRedInfo.DataBean data) {
        boolean isShowBestLuck = false;
        if (data.getRp_number() == mLogBeanList.size()) {
            isShowBestLuck = true;
        }
        if(mLogBeanList==null||mLogBeanList.size()==0){
            mViewLine.setVisibility(View.GONE);
        }
        RedPacketRVAdapter redPacketRVAdapter = new RedPacketRVAdapter(this, mLogBeanList, mMgr, mCoin, isShowBestLuck);
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
