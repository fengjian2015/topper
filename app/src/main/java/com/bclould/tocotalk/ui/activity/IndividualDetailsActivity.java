package com.bclould.tocotalk.ui.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bclould.tocotalk.Presenter.IndividualDetailsPresenter;
import com.bclould.tocotalk.Presenter.PersonalDetailsPresenter;
import com.bclould.tocotalk.R;
import com.bclould.tocotalk.base.BaseActivity;
import com.bclould.tocotalk.base.MyApp;
import com.bclould.tocotalk.history.DBManager;
import com.bclould.tocotalk.model.IndividualInfo;
import com.bclould.tocotalk.model.MessageInfo;
import com.bclould.tocotalk.ui.widget.DeleteCacheDialog;
import com.bclould.tocotalk.utils.MessageEvent;
import com.bclould.tocotalk.utils.ToastShow;
import com.bclould.tocotalk.utils.UtilTool;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.bclould.tocotalk.ui.adapter.ChatAdapter.TO_CARD_MSG;

@RequiresApi(api = Build.VERSION_CODES.N)
public class IndividualDetailsActivity extends BaseActivity {
    @Bind(R.id.bark)
    ImageView mBark;
    @Bind(R.id.iv_head)
    ImageView ivHead;
    @Bind(R.id.tv_name)
    TextView tvName;
    @Bind(R.id.image_qr)
    ImageView imageQr;
    @Bind(R.id.rl_dynamic)
    RelativeLayout rlDynamic;
    @Bind(R.id.image1)
    ImageView image1;
    @Bind(R.id.image2)
    ImageView image2;
    @Bind(R.id.image3)
    ImageView image3;
    @Bind(R.id.image4)
    ImageView image4;
    @Bind(R.id.tv_region)
    TextView tvRegion;
    @Bind(R.id.tv_remark)
    TextView tvRemark;
    @Bind(R.id.btn_brak)
    Button btnBrak;
    @Bind(R.id.rl_qr)
    RelativeLayout rlQr;
    @Bind(R.id.rl_remark)
    RelativeLayout rlRemark;
    @Bind(R.id.rl_card)
    RelativeLayout rlCard;
    @Bind(R.id.tv1)
    TextView mTv1;
    @Bind(R.id.tv6)
    TextView mTv6;
    @Bind(R.id.on_off_dy)
    ImageView mOnOffDy;
    @Bind(R.id.tv2)
    TextView mTv2;
    @Bind(R.id.rl_cache)
    RelativeLayout mRlCache;
    @Bind(R.id.tv3)
    TextView mTv3;
    @Bind(R.id.tv4)
    TextView mTv4;
    @Bind(R.id.tv5)
    TextView mTv5;
    @Bind(R.id.tv7)
    TextView mTv7;
    @Bind(R.id.on_off_dy2)
    ImageView mOnOffDy2;
    @Bind(R.id.cv_no_look_ta_dy)
    CardView mCvNoLookTaDy;
    @Bind(R.id.cv_no_look_ta_dy2)
    CardView mCvNoLookTaDy2;

    private DBManager mMgr;
    private String mUser;
    private String mName;
    private String roomId;
    private IndividualInfo.DataBean individualInfo;
    private int REMARK = 100;
    private int type = 0;//1表示查看好友，2表示添加好友

    private String avatar;
    private IndividualDetailsPresenter mPresenter;
//    private String NO_SEE_HIM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual_details);
        ButterKnife.bind(this);
        MyApp.getInstance().addActivity(this);
        mMgr = new DBManager(this);//初始化数据库管理类
        initIntent();
        init();
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
    }

    private void initIntent() {
        Intent intent = getIntent();
        mUser = intent.getStringExtra("user");
        mName = intent.getStringExtra("name");
        roomId = intent.getStringExtra("roomId");
    }

    boolean isNoSeeHim = false;
    boolean isNoSeeMe = false;

    private void init() {
        if (mMgr.findUser(mUser)) {
            if (mUser.equals(UtilTool.getTocoId())) {
                type = 3;
            } else {
                type = 1;
            }
        } else {
            type = 2;
        }
        if(type ==1){
            imageQr.setVisibility(View.VISIBLE);
            rlRemark.setVisibility(View.VISIBLE);
            btnBrak.setText(getString(R.string.delete));
            rlCard.setVisibility(View.VISIBLE);
            mCvNoLookTaDy.setVisibility(View.VISIBLE);
            mCvNoLookTaDy2.setVisibility(View.VISIBLE);
        }else if (type == 2) {
            imageQr.setVisibility(View.INVISIBLE);
            rlRemark.setVisibility(View.GONE);
            btnBrak.setText(getString(R.string.add_friend));
            rlCard.setVisibility(View.GONE);
            mCvNoLookTaDy.setVisibility(View.GONE);
            mCvNoLookTaDy2.setVisibility(View.GONE);
        } else if (type == 3) {
            btnBrak.setVisibility(View.GONE);
            rlCard.setVisibility(View.GONE);
            mCvNoLookTaDy.setVisibility(View.GONE);
            mCvNoLookTaDy2.setVisibility(View.GONE);
            rlRemark.setVisibility(View.GONE);
        }
        mPresenter = new IndividualDetailsPresenter(this);
        mPresenter.getIndividual(mUser,true, new IndividualDetailsPresenter.CallBack() {
            @Override
            public void send(IndividualInfo.DataBean data) {
                if (!IndividualDetailsActivity.this.isDestroyed()) {
                    if (data == null) return;
                    individualInfo = data;
                    mName=individualInfo.getName();
                    tvName.setText(individualInfo.getName());
                    tvRemark.setText(individualInfo.getRemark());
                    tvRegion.setText(individualInfo.getCountry());
                    avatar = individualInfo.getAvatar();
                    if (data.getNo_see_him() == 1) {
                        isNoSeeHim = true;
                    } else if (data.getNo_see_him() == 2) {
                        isNoSeeHim = false;
                    }
                    if (data.getNo_see_me() == 1) {
                        isNoSeeMe = true;
                    } else if (data.getNo_see_me() == 2) {
                        isNoSeeMe = false;
                    }
                    mOnOffDy.setSelected(isNoSeeHim);
                    mOnOffDy2.setSelected(isNoSeeMe);
                    Glide.with(IndividualDetailsActivity.this).load(individualInfo.getAvatar()).apply(new RequestOptions().placeholder(R.mipmap.img_nfriend_headshot1)).into(ivHead);
                    if (!individualInfo.getAvatar().isEmpty()) {
                        UtilTool.setCircleImg(IndividualDetailsActivity.this, individualInfo.getAvatar(), ivHead);
                    } else {
                        UtilTool.setCircleImg(IndividualDetailsActivity.this, R.mipmap.img_nfriend_headshot1, ivHead);
                    }
                }
            }
        });
    }

    @OnClick({R.id.cv_no_look_ta_dy, R.id.cv_no_look_ta_dy2, R.id.rl_dynamic, R.id.bark, R.id.btn_brak, R.id.rl_qr, R.id.rl_remark, R.id.rl_card})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_dynamic:
                if (isNoSeeHim) {
                    ToastShow.showToast2(this, getString(R.string.no_look_ta_dy_hint));
                } else {
                    Intent intent = new Intent(this, PersonageDynamicActivity.class);
                    intent.putExtra("name", mName);
                    intent.putExtra("user",mUser);
                    startActivity(intent);
                }
                break;
            case R.id.bark:
                finish();
                break;
            case R.id.cv_no_look_ta_dy:
                noLookTaDy(2);
                break;
            case R.id.cv_no_look_ta_dy2:
                noLookTaDy(1);
                break;
            case R.id.btn_brak:
                if (type == 1) {
                    showDeleteDialog();
                } else {
                    addFriend();
                }
                break;
            case R.id.rl_qr:
                goQR();
                break;
            case R.id.rl_remark:
                goChangeRemark();
                break;
            case R.id.rl_card:
                sendCard();
                break;
        }
    }

    //不看Ta的動態
    private void noLookTaDy(final int type) {
        mPresenter.noLookTaDy(mUser, type, new IndividualDetailsPresenter.CallBack2() {
            @Override
            public void send() {
                if (type == 2) {
                    isNoSeeHim = !isNoSeeHim;
                    mOnOffDy.setSelected(isNoSeeHim);
                    EventBus.getDefault().post(new MessageEvent(getString(R.string.shield_dy)));
                } else {
                    isNoSeeMe = !isNoSeeMe;
                    mOnOffDy2.setSelected(isNoSeeMe);
                }
            }
        });
    }

    private void sendCard() {
        if (individualInfo == null) {
            init();
            return;
        }
        Intent intent = new Intent(this, SelectFriendActivity.class);
        intent.putExtra("type", 2);
        MessageInfo messageInfo = new MessageInfo();
        messageInfo.setHeadUrl(avatar);
        messageInfo.setMessage(mName);
        messageInfo.setCardUser(mUser);
        intent.putExtra("msgType", TO_CARD_MSG);
        intent.putExtra("messageInfo", messageInfo);
        this.startActivity(intent);
    }

    private void goChangeRemark() {
        if (individualInfo == null) {
            init();
            return;
        }
        Intent intent = new Intent(this, RemarkActivity.class);
        intent.putExtra("name", mName);
        intent.putExtra("remark", individualInfo.getRemark());
        intent.putExtra("user", mUser);
        startActivityForResult(intent, REMARK);
    }

    private void goQR() {
        if (type == 2) return;
        Intent intent = new Intent(this, QRCodeActivity.class);
        intent.putExtra("user", mUser);
        startActivity(intent);
    }

    private void addFriend() {
        try {
            if (!mMgr.findUser(mUser)) {
                new PersonalDetailsPresenter(this).addFriend(mUser,mName);
            } else {
                Toast.makeText(this, getString(R.string.have_friend), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, getString(R.string.send_request_error), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void showDeleteDialog() {
        if (individualInfo == null) {
            init();
            return;
        }
        final DeleteCacheDialog deleteCacheDialog = new DeleteCacheDialog(R.layout.dialog_delete_cache, this, R.style.dialog);
        deleteCacheDialog.show();
        deleteCacheDialog.setTitle(getString(R.string.confirm_delete) + " " + mName + " " + getString(R.string.what));
        Button cancel = (Button) deleteCacheDialog.findViewById(R.id.btn_cancel);
        Button confirm = (Button) deleteCacheDialog.findViewById(R.id.btn_confirm);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteCacheDialog.dismiss();
            }
        });
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    new PersonalDetailsPresenter(IndividualDetailsActivity.this).deleteFriend(mUser, new PersonalDetailsPresenter.CallBack() {
                        @Override
                        public void send() {
                            mMgr.deleteConversation(roomId);
                            mMgr.deleteMessage(roomId);
                            mMgr.deleteUser(mUser);
                            EventBus.getDefault().post(new MessageEvent(getString(R.string.delete_friend)));
                            finish();
                        }
                    });
                    deleteCacheDialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        String msg = event.getMsg();
        if (msg.equals(getString(R.string.new_friend))) {
            init();
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REMARK && data != null) {
            String remark = data.getStringExtra("remark");
            tvRemark.setText(remark);
            EventBus.getDefault().post(new MessageEvent(getString(R.string.change_friend_remark)));
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
