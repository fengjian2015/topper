package com.bclould.tocotalk.ui.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bclould.tocotalk.Presenter.IndividualDetailsPresenter;
import com.bclould.tocotalk.R;
import com.bclould.tocotalk.base.BaseActivity;
import com.bclould.tocotalk.base.MyApp;
import com.bclould.tocotalk.history.DBManager;
import com.bclould.tocotalk.model.IndividualInfo;
import com.bclould.tocotalk.model.MessageInfo;
import com.bclould.tocotalk.ui.widget.DeleteCacheDialog;
import com.bclould.tocotalk.utils.Constants;
import com.bclould.tocotalk.utils.MessageEvent;
import com.bclould.tocotalk.utils.UtilTool;
import com.bclould.tocotalk.xmpp.XmppConnection;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.greenrobot.eventbus.EventBus;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jxmpp.jid.impl.JidCreate;

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

    private DBManager mMgr;
    private String mUser;
    private String mName;
    private String roomId;
    private IndividualInfo.DataBean individualInfo;
    private int REMARK = 100;
    private int type = 0;//1表示查看好友，2表示添加好友

    private String avatar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual_details);
        ButterKnife.bind(this);
        MyApp.getInstance().addActivity(this);
        mMgr = new DBManager(this);//初始化数据库管理类
        initIntent();
        init();
    }

    private void initIntent() {
        Intent intent = getIntent();
        mUser = intent.getStringExtra("user");
        mName = intent.getStringExtra("name");
        roomId= intent.getStringExtra("roomId");
        if(mMgr.findUser(mUser)){
            type=1;
        }else{
            type=2;
        }
    }

    private void init() {
        if (type == 2) {
            imageQr.setVisibility(View.INVISIBLE);
            rlRemark.setVisibility(View.GONE);
            btnBrak.setText(getString(R.string.add_friend));
            if (UtilTool.getUser().equals(mName)) {
                btnBrak.setVisibility(View.GONE);
            }
        }
        IndividualDetailsPresenter presenter = new IndividualDetailsPresenter(this);
        presenter.getIndividual(mName, new IndividualDetailsPresenter.CallBack() {
            @Override
            public void send(IndividualInfo.DataBean data) {
                if (data == null) return;
                individualInfo = data;
                tvName.setText(individualInfo.getName());
                tvRemark.setText(individualInfo.getRemark());
                tvRegion.setText(individualInfo.getCountry());
                avatar=individualInfo.getAvatar();
                Glide.with(IndividualDetailsActivity.this).load(individualInfo.getAvatar()).apply(new RequestOptions().placeholder(R.mipmap.img_nfriend_headshot1)).into(ivHead);
                if (!individualInfo.getAvatar().isEmpty()) {
                    UtilTool.setCircleImg(IndividualDetailsActivity.this, individualInfo.getAvatar(), ivHead);
                } else {
                    UtilTool.setCircleImg(IndividualDetailsActivity.this, R.mipmap.img_nfriend_headshot1, ivHead);
                }
            }
        });
    }

    @OnClick({R.id.rl_dynamic, R.id.bark, R.id.btn_brak, R.id.rl_qr, R.id.rl_remark,R.id.iv_else})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_dynamic:
                Intent intent = new Intent(this, PersonageDynamicActivity.class);
                intent.putExtra("name", mName);
                startActivity(intent);
                break;
            case R.id.bark:
                finish();
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
            case R.id.iv_else:
                sendCard();
                break;
        }
    }

    private void sendCard() {
        Intent intent = new Intent(this, SelectFriendActivity.class);
        intent.putExtra("type", 2);
        MessageInfo messageInfo=new MessageInfo();
        messageInfo.setHeadUrl(avatar);
        messageInfo.setMessage(mName);
        messageInfo.setCardUser(mUser);
        intent.putExtra("msgType",TO_CARD_MSG);
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
        intent.putExtra("user",mUser);
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
            String name = "";
            if (!mName.contains(Constants.DOMAINNAME)) {
                name = mName + "@" + Constants.DOMAINNAME;
            }

            if (!mMgr.findUser(name)) {
                Roster.getInstanceFor(XmppConnection.getInstance().getConnection()).createEntry(JidCreate.entityBareFrom(name), null, new String[]{"Friends"});
                Toast.makeText(this, getString(R.string.send_request_succeed), Toast.LENGTH_SHORT).show();
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
                    Roster roster = Roster.getInstanceFor(XmppConnection.getInstance().getConnection());
                    RosterEntry entry = roster.getEntry(JidCreate.entityBareFrom(mUser));
                    roster.removeEntry(entry);
                    Toast.makeText(IndividualDetailsActivity.this, getString(R.string.delete_succeed), Toast.LENGTH_SHORT).show();
                    mMgr.deleteConversation(roomId);
                    mMgr.deleteMessage(roomId);
                    mMgr.deleteUser(mUser);
                    EventBus.getDefault().post(new MessageEvent(getString(R.string.delete_friend)));
                    deleteCacheDialog.dismiss();
                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
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
