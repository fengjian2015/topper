package com.bclould.tocotalk.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bclould.tocotalk.R;
import com.bclould.tocotalk.base.BaseActivity;
import com.bclould.tocotalk.base.MyApp;
import com.bclould.tocotalk.crypto.otr.OtrChatListenerManager;
import com.bclould.tocotalk.history.DBManager;
import com.bclould.tocotalk.ui.widget.DeleteCacheDialog;
import com.bclould.tocotalk.utils.Constants;
import com.bclould.tocotalk.utils.MessageEvent;
import com.bclould.tocotalk.utils.UtilTool;
import com.bclould.tocotalk.xmpp.XmppConnection;

import org.greenrobot.eventbus.EventBus;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jxmpp.jid.impl.JidCreate;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

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

    private DBManager mMgr;
    private String mName;
    private String mUser;
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
        mName = intent.getStringExtra("name");
        mUser = intent.getStringExtra("user");
    }

    private void init() {
        Bitmap mUserImage = UtilTool.getImage(mMgr, mUser, this);
        ivHead.setImageBitmap(mUserImage);
        tvName.setText(mName);

    }

    @OnClick({R.id.rl_dynamic, R.id.bark, R.id.btn_brak})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_dynamic:
                // TODO: 2018/5/9 跳轉到個人動態
                break;
            case R.id.bark:
                finish();
                break;
            case R.id.btn_brak:
                showDeleteDialog();
                break;
        }
    }

    private void showDeleteDialog() {
        final DeleteCacheDialog deleteCacheDialog = new DeleteCacheDialog(R.layout.dialog_delete_cache, this);
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
                    mMgr.deleteConversation(mUser);
                    mMgr.deleteMessage(mUser);
                    mMgr.deleteUser(mUser);
                    EventBus.getDefault().post(new MessageEvent(getString(R.string.delete_friend)));
                    deleteCacheDialog.dismiss();
                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                    UtilTool.Log("fsdafa", e.getMessage());
                }
            }
        });
    }
}
