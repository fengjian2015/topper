package com.bclould.tea.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bclould.tea.Presenter.IndividualDetailsPresenter;
import com.bclould.tea.Presenter.PersonalDetailsPresenter;
import com.bclould.tea.R;
import com.bclould.tea.base.BaseActivity;
import com.bclould.tea.base.MyApp;
import com.bclould.tea.history.DBManager;
import com.bclould.tea.model.IndividualInfo;
import com.bclould.tea.model.MessageInfo;
import com.bclould.tea.ui.widget.DeleteCacheDialog;
import com.bclould.tea.utils.ActivityUtil;
import com.bclould.tea.utils.AppLanguageUtils;
import com.bclould.tea.utils.EventBusUtil;
import com.bclould.tea.utils.MessageEvent;
import com.bclould.tea.utils.MySharedPreferences;
import com.bclould.tea.utils.ToastShow;
import com.bclould.tea.utils.UtilTool;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.bclould.tea.topperchat.WsContans.TOCO_SERVICE;
import static com.bclould.tea.ui.adapter.ChatAdapter.TO_CARD_MSG;

@RequiresApi(api = Build.VERSION_CODES.N)
public class IndividualDetailsActivity extends BaseActivity {

    @Bind(R.id.bark)
    ImageView mBark;
    @Bind(R.id.iv_head)
    ImageView mIvHead;
    @Bind(R.id.tv_name)
    TextView mTvName;
    @Bind(R.id.image_qr)
    ImageView mImageQr;
    @Bind(R.id.rl_qr)
    RelativeLayout mRlQr;
    @Bind(R.id.tv1)
    TextView mTv1;
    @Bind(R.id.image1)
    ImageView mImage1;
    @Bind(R.id.image2)
    ImageView mImage2;
    @Bind(R.id.image3)
    ImageView mImage3;
    @Bind(R.id.image4)
    ImageView mImage4;
    @Bind(R.id.rl_dynamic)
    RelativeLayout mRlDynamic;
    @Bind(R.id.tv6)
    TextView mTv6;
    @Bind(R.id.on_off_dy)
    ImageView mOnOffDy;
    @Bind(R.id.rl_no_look_ta_dy)
    RelativeLayout mRlNoLookTaDy;
    @Bind(R.id.tv7)
    TextView mTv7;
    @Bind(R.id.on_off_dy2)
    ImageView mOnOffDy2;
    @Bind(R.id.rl_no_look_ta_dy2)
    RelativeLayout mRlNoLookTaDy2;
    @Bind(R.id.tv2)
    TextView mTv2;
    @Bind(R.id.tv_region)
    TextView mTvRegion;
    @Bind(R.id.rl_cache)
    RelativeLayout mRlCache;
    @Bind(R.id.tv3)
    TextView mTv3;
    @Bind(R.id.tv_remark)
    TextView mTvRemark;
    @Bind(R.id.rl_remark)
    RelativeLayout mRlRemark;
    @Bind(R.id.tv4)
    TextView mTv4;
    @Bind(R.id.tv5)
    TextView mTv5;
    @Bind(R.id.rl_card)
    RelativeLayout mRlCard;
    @Bind(R.id.btn_send)
    Button mBtnSend;
    @Bind(R.id.btn_brak)
    Button mBtnBrak;
    @Bind(R.id.ll_data)
    LinearLayout mLlData;
    @Bind(R.id.iv2)
    ImageView mIv2;
    @Bind(R.id.ll_error)
    LinearLayout mLlError;
    private DBManager mMgr;
    private String mUser;
    private String mName;
    private String roomId;
    private IndividualInfo.DataBean individualInfo;
    private int REMARK = 100;
    private int type = 0;//1表示查看好友，2表示添加好友 3表示自己

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
        if (type == 1) {
            mImageQr.setVisibility(View.VISIBLE);
            mRlRemark.setVisibility(View.VISIBLE);
            mBtnBrak.setText(getString(R.string.delete));
            mBtnSend.setVisibility(View.VISIBLE);
            mRlCard.setVisibility(View.VISIBLE);
            mRlNoLookTaDy.setVisibility(View.VISIBLE);
            mRlNoLookTaDy2.setVisibility(View.VISIBLE);
        } else if (type == 2) {
            mImageQr.setVisibility(View.INVISIBLE);
            mRlRemark.setVisibility(View.GONE);
            mBtnBrak.setText(getString(R.string.add_friend));
            mBtnSend.setVisibility(View.GONE);
            mRlCard.setVisibility(View.GONE);
            mRlNoLookTaDy.setVisibility(View.GONE);
            mRlNoLookTaDy2.setVisibility(View.GONE);
        } else if (type == 3) {
            mBtnBrak.setVisibility(View.GONE);
            mRlCard.setVisibility(View.GONE);
            mRlNoLookTaDy.setVisibility(View.GONE);
            mRlNoLookTaDy2.setVisibility(View.GONE);
            mRlRemark.setVisibility(View.GONE);
            mBtnSend.setVisibility(View.GONE);
        }
        if (TOCO_SERVICE.equals(mUser)) {
            mBtnBrak.setVisibility(View.GONE);
        }
        initData();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(AppLanguageUtils.attachBaseContext(newBase, MySharedPreferences.getInstance().getString(newBase.getString(R.string.language_pref_key))));
    }

    private void initData() {
        mPresenter = new IndividualDetailsPresenter(this);
        mPresenter.getIndividual(mUser, true, new IndividualDetailsPresenter.CallBack() {
            @Override
            public void send(IndividualInfo.DataBean data) {
                if (!IndividualDetailsActivity.this.isDestroyed()) {
                    mLlData.setVisibility(View.VISIBLE);
                    mLlError.setVisibility(View.GONE);
                    if (data == null) return;
                    saveData(data);
                    individualInfo = data;
                    mName = individualInfo.getName();
                    mTvName.setText(individualInfo.getName());
                    mTvRemark.setText(individualInfo.getRemark());
                    mTvRegion.setText(individualInfo.getCountry());
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
                    Glide.with(IndividualDetailsActivity.this).load(individualInfo.getAvatar()).apply(new RequestOptions().placeholder(R.mipmap.img_nfriend_headshot1)).into(mIvHead);
                    if (!individualInfo.getAvatar().isEmpty()) {
                        UtilTool.setCircleImg(IndividualDetailsActivity.this, individualInfo.getAvatar(), mIvHead);
                    } else {
                        UtilTool.setCircleImg(IndividualDetailsActivity.this, R.mipmap.img_nfriend_headshot1, mIvHead);
                    }
                }
            }

            @Override
            public void error() {
                if (ActivityUtil.isActivityOnTop(IndividualDetailsActivity.this)) {
                    mLlData.setVisibility(View.GONE);
                    mLlError.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void saveData(IndividualInfo.DataBean data) {
        mMgr.addStrangerUserInfo(data.getToco_id(), data.getAvatar(), data.getName());
        mMgr.updateConversationName(data.getToco_id(), data.getName());
    }

    @OnClick({R.id.rl_no_look_ta_dy, R.id.rl_no_look_ta_dy2, R.id.rl_dynamic, R.id.bark, R.id.btn_brak, R.id.rl_qr, R.id.rl_remark, R.id.rl_card, R.id.iv_head, R.id.ll_error, R.id.btn_send})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_dynamic:
                if (isNoSeeHim) {
                    ToastShow.showToast2(this, getString(R.string.no_look_ta_dy_hint));
                } else {
                    Intent intent = new Intent(this, PersonageDynamicActivity.class);
                    intent.putExtra("name", mName);
                    intent.putExtra("user", mUser);
                    startActivity(intent);
                }
                break;
            case R.id.bark:
                finish();
                break;
            case R.id.ll_error:
                initData();
                break;
            case R.id.rl_no_look_ta_dy:
                noLookTaDy(2);
                break;
            case R.id.rl_no_look_ta_dy2:
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
            case R.id.iv_head:
                lookLargerImage();
                break;
            case R.id.btn_send:
                goConversation();
                break;
        }
    }

    private void goConversation() {
        MyApp.getInstance().exit(ConversationActivity.class.getName());
        Intent intent = new Intent(this, ConversationActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        Bundle bundle = new Bundle();
        bundle.putString("name", mName);
        bundle.putString("user", mUser);
        intent.putExtras(bundle);
        mMgr.updateNumber(mUser, 0);
        EventBus.getDefault().post(new MessageEvent(EventBusUtil.dispose_unread_msg));
        startActivity(intent);
        finish();
    }

    private void lookLargerImage() {
        Intent intent = new Intent(this, LargerImageActivity.class);
        intent.putExtra("url", individualInfo.getAvatar());
        startActivity(intent);
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
        Intent intent = new Intent(this, SelectConversationActivity.class);
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
                new PersonalDetailsPresenter(this).addFriend(mUser, mName);
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
                            mMgr.deleteMessage(roomId,0);
                            mMgr.deleteUser(mUser);
                            EventBus.getDefault().post(new MessageEvent(EventBusUtil.delete_friend));
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
        if (msg.equals(EventBusUtil.new_friend)) {
            if (ActivityUtil.isActivityOnTop(IndividualDetailsActivity.this)) {
                init();
            }
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
            mTvRemark.setText(remark);
            EventBus.getDefault().post(new MessageEvent(getString(R.string.change_friend_remark)));
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
