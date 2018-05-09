package com.bclould.tocotalk.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bclould.tocotalk.R;
import com.bclould.tocotalk.base.BaseActivity;
import com.bclould.tocotalk.base.MyApp;
import com.bclould.tocotalk.crypto.otr.OtrChatListenerManager;
import com.bclould.tocotalk.history.DBManager;
import com.bclould.tocotalk.model.MessageInfo;
import com.bclould.tocotalk.utils.Constants;
import com.bclould.tocotalk.utils.MessageEvent;
import com.bclould.tocotalk.utils.UtilTool;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

@RequiresApi(api = Build.VERSION_CODES.N)
public class ConversationDetailsActivity extends BaseActivity {
    @Bind(R.id.bark)
    ImageView mBark;
    @Bind(R.id.rl_details)
    RelativeLayout rlDetails;
    @Bind(R.id.image_head)
    ImageView imageHead;
    @Bind(R.id.tv_name)
    TextView tvName;
    @Bind(R.id.tv_otr_state)
    TextView tvOtrState;
    @Bind(R.id.tv_otr)
    TextView tvOtr;
    @Bind(R.id.on_off_otr)
    ImageView onOffOtr;
    @Bind(R.id.tv_my_otr)
    TextView tvMyOtr;
    @Bind(R.id.rl_looking_chat)
    RelativeLayout rlLookingChat;
    @Bind(R.id.on_off_top)
    ImageView onOffTop;
    @Bind(R.id.on_off_message_free)
    ImageView onOffMessageFree;
    @Bind(R.id.rl_empty_talk)
    RelativeLayout rlEmptyTalk;

    private DBManager mMgr;
    private String mName;
    private String mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation_details);
        ButterKnife.bind(this);
        MyApp.getInstance().addActivity(this);
        mMgr = new DBManager(this);//初始化数据库管理类
        EventBus.getDefault().register(this);//初始化EventBus
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
        imageHead.setImageBitmap(mUserImage);
        tvName.setText(mName);
        if(OtrChatListenerManager.getInstance().isVerified(OtrChatListenerManager.getInstance().sessionID(Constants.MYUSER,mUser))){
            tvOtrState.setText(getString(R.string.otr_authenticated));
        }else{
            tvOtrState.setText(getString(R.string.otr_unauthorized));
        }
        tvOtr.setText(OtrChatListenerManager.getInstance().getRemotePublicKey(OtrChatListenerManager.getInstance().sessionID(Constants.MYUSER,mUser)));
        tvMyOtr.setText(OtrChatListenerManager.getInstance().getLocalPublicKey(OtrChatListenerManager.getInstance().sessionID(Constants.MYUSER,mUser)));
        changeOTRState();
    }

    private void changeOTRState(){
        if("true".equals(OtrChatListenerManager.getInstance().getOTRState(mUser))){
            onOffOtr.setSelected(true);
        }else{
            onOffOtr.setSelected(false);
        }
    }

    @OnClick({R.id.rl_details, R.id.bark, R.id.on_off_otr, R.id.rl_looking_chat, R.id.on_off_message_free, R.id.on_off_top, R.id.rl_empty_talk})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_details:
                goIndividualDetails();
                break;
            case R.id.bark:
                finish();
                break;
            case R.id.on_off_otr:
                resultOTR();
                break;
            case R.id.rl_looking_chat:
                // TODO: 2018/5/9 查找聊天內容
                break;
            case R.id.on_off_message_free:
                // TODO: 2018/5/9 消息免打擾
                break;
            case R.id.on_off_top:
                // TODO: 2018/5/9 置頂
                break;
            case R.id.rl_empty_talk:
                // TODO: 2018/5/9 清空消息記錄
                break;
        }
    }

    private void goIndividualDetails() {
        Intent intent=new Intent(this,IndividualDetailsActivity.class);
        intent.putExtra("user",mUser);
        intent.putExtra("name",mName);
        startActivity(intent);
    }

    private void resultOTR(){
        try {
            if("true".equals(OtrChatListenerManager.getInstance().getOTRState(JidCreate.entityBareFrom(mUser).toString()))){
                OtrChatListenerManager.getInstance().addOTRState(JidCreate.entityBareFrom(mUser).toString(),"false");
                OtrChatListenerManager.getInstance().endMessage(OtrChatListenerManager.getInstance().sessionID(Constants.MYUSER,JidCreate.entityBareFrom(mUser).toString()));
            }else{
                OtrChatListenerManager.getInstance().createOtrChatManager(OtrChatListenerManager.getInstance().sessionID(Constants.MYUSER,JidCreate.entityBareFrom(mUser).toString()),this);
                OtrChatListenerManager.getInstance().startMessage(OtrChatListenerManager.getInstance().sessionID(Constants.MYUSER,JidCreate.entityBareFrom(mUser).toString()),this);
                OtrChatListenerManager.getInstance().startSession(OtrChatListenerManager.getInstance().sessionID(Constants.MYUSER,JidCreate.entityBareFrom(mUser).toString()));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    //接受通知
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        String msg = event.getMsg();
        if (msg.equals(getString(R.string.otr_isopen))){
            changeOTRState();
        }else if(msg.equals(getString(R.string.delete_friend))){
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
