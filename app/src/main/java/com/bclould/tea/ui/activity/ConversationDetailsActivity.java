package com.bclould.tea.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bclould.tea.Presenter.GroupPresenter;
import com.bclould.tea.R;
import com.bclould.tea.base.BaseActivity;
import com.bclould.tea.base.MyApp;
import com.bclould.tea.crypto.otr.OtrChatListenerManager;
import com.bclould.tea.history.DBManager;
import com.bclould.tea.model.ConversationInfo;
import com.bclould.tea.ui.widget.DeleteCacheDialog;
import com.bclould.tea.ui.widget.MenuListPopWindow;
import com.bclould.tea.utils.Constants;
import com.bclould.tea.utils.AppLanguageUtils;
import com.bclould.tea.utils.EventBusUtil;
import com.bclould.tea.utils.MessageEvent;
import com.bclould.tea.utils.MySharedPreferences;
import com.bclould.tea.utils.StringUtils;
import com.bclould.tea.utils.UtilTool;
import com.bclould.tea.xmpp.RoomManage;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.compress.Luban;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.entity.LocalMedia;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.bclould.tea.ui.activity.SerchImageActivity.TYPE_GROUP;
import static com.bclould.tea.utils.MySharedPreferences.SETTING;
import static com.luck.picture.lib.config.PictureMimeType.ofImage;

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
    private String roomId;
    private List<LocalMedia> selectList = new ArrayList<>();

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

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(AppLanguageUtils.attachBaseContext(newBase, MySharedPreferences.getInstance().getString(newBase.getString(R.string.language_pref_key))));
    }

    private void initIntent() {
        Intent intent = getIntent();
        mName = intent.getStringExtra("name");
        mUser = intent.getStringExtra("user");
        roomId= intent.getStringExtra("roomId");
    }

    private void init() {
        UtilTool.getImage(mMgr, mUser, this, imageHead);
        /*Bitmap mUserImage = UtilTool.getImage(mMgr, mUser, this);
        imageHead.setImageBitmap(mUserImage);*/
        setName();
        changeTop();
        changeFree();
        changeOTRState();
    }

    private void setName(){
        String remark=mMgr.queryRemark(mUser);
        if(!com.bclould.tea.utils.StringUtils.isEmpty(remark)){
            tvName.setText(remark);
        }else {
            tvName.setText(mName);
        }
    }

    private void changeTop() {
        String istop=mMgr.findConversationIstop(mUser);
        if("true".equals(istop)){
            onOffTop.setSelected(true);
        }else {
            onOffTop.setSelected(false);
        }
    }

    private void changeFree(){
        boolean free= MySharedPreferences.getInstance().getBoolean(SETTING+mUser+UtilTool.getTocoId());
        onOffMessageFree.setSelected(free);
    }

    private void changeOTRState(){
        if(OtrChatListenerManager.getInstance().isVerified(OtrChatListenerManager.getInstance().sessionID(UtilTool.getTocoId(),mUser),this)){
            tvOtrState.setText(getString(R.string.otr_authenticated));
        }else{
            tvOtrState.setText(getString(R.string.otr_unauthorized));
        }
        tvOtr.setText(OtrChatListenerManager.getInstance().getRemotePublicKey(OtrChatListenerManager.getInstance().sessionID(UtilTool.getTocoId(),mUser),this));
        tvMyOtr.setText(OtrChatListenerManager.getInstance().getLocalPublicKey(OtrChatListenerManager.getInstance().sessionID(UtilTool.getTocoId(),mUser),this));
        if("true".equals(OtrChatListenerManager.getInstance().getOTRState(mUser))){
            onOffOtr.setSelected(true);
        }else{
            onOffOtr.setSelected(false);
        }
    }

    @OnClick({R.id.rl_details, R.id.bark, R.id.on_off_otr, R.id.rl_looking_chat, R.id.on_off_message_free, R.id.on_off_top, R.id.rl_empty_talk,R.id.image_add})
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
                goRecord();
                break;
            case R.id.on_off_message_free:
                messageFree();
                break;
            case R.id.on_off_top:
                messageTop();
                break;
            case R.id.rl_empty_talk:
                clearMessage();
                break;
            case R.id.image_add:
                goCreateGroup();
                break;
        }
    }

    private void goCreateGroup() {
        Intent intent = new Intent(this, CreateGroupRoomActivity.class);
        intent.putExtra("roomName", "");
        intent.putExtra("type",0);
        intent.putExtra("tocoId",mUser);
        startActivity(intent);
    }

    private void goRecord() {
        Intent intent=new Intent(this,ConversationRecordFindActivity.class);
        intent.putExtra("roomId",roomId);
        intent.putExtra("user",mUser);
        intent.putExtra("name",mName);
        startActivity(intent);
    }

    private void messageFree(){
        boolean free= MySharedPreferences.getInstance().getBoolean(SETTING+mUser+UtilTool.getTocoId());
        if(free){
            onOffMessageFree.setSelected(false);
            MySharedPreferences.getInstance().setBoolean(SETTING+mUser+UtilTool.getTocoId(),false);
        }else{
            onOffMessageFree.setSelected(true);
            MySharedPreferences.getInstance().setBoolean(SETTING+mUser+UtilTool.getTocoId(),true);
        }
    }

    private void messageTop() {
        final int status;
        final String istop = mMgr.findConversationIstop(roomId);
        if (StringUtils.isEmpty(istop)) {
            status=1;
        } else if ("true".equals(istop)) {
            status=0;
        } else {
            status=1;
        }
        new GroupPresenter(this).setTopMessage(roomId, status,true, new GroupPresenter.CallBack() {
            @Override
            public void send() {
                if(status==1){
                    if(!mMgr.findConversation(roomId)){
                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Date curDate = new Date(System.currentTimeMillis());
                        String time = formatter.format(curDate);
                        ConversationInfo info = new ConversationInfo();
                        info.setTime(time);
                        info.setFriend(mName);
                        info.setUser(mUser);
                        info.setIstop("true");
                        info.setMessage("");
                        info.setCreateTime(UtilTool.createChatCreatTime());
                        info.setChatType(RoomManage.ROOM_TYPE_SINGLE);
                        mMgr.addConversation(info);
                    }else{
                        mMgr.updateConversationIstop(roomId, "true");
                    }
                    onOffTop.setSelected(true);
                }else{
                    onOffTop.setSelected(false);
                    mMgr.updateConversationIstop(roomId, "false");
                }
                EventBus.getDefault().post(new MessageEvent(getString(R.string.message_top_change)));
            }
        });
    }

    private void clearMessage() {
        final DeleteCacheDialog deleteCacheDialog = new DeleteCacheDialog(R.layout.dialog_delete_cache, this,R.style.dialog);
        deleteCacheDialog.show();
        deleteCacheDialog.setTitle(getString(R.string.make_sure_clear_transcript));
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
                    mMgr.deleteMessage(mUser,0);
                    mMgr.updateConversationMessage(mUser,"");
                    Toast.makeText(ConversationDetailsActivity.this, getString(R.string.empty_success), Toast.LENGTH_SHORT).show();
                    EventBus.getDefault().post(new MessageEvent(EventBusUtil.msg_database_update));
                    deleteCacheDialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void goIndividualDetails() {
        Intent intent=new Intent(this,IndividualDetailsActivity.class);
        intent.putExtra("user",mUser);
        intent.putExtra("name",mName);
        intent.putExtra("roomId",roomId);
        startActivity(intent);
    }

    private void resultOTR(){
        try {
             OtrChatListenerManager.getInstance().changeState(mUser.toString(),this);
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
        }else if(msg.equals(EventBusUtil.delete_friend)){
            finish();
        }else if(msg.equals(getString(R.string.change_friend_remark))){
            setName();
        }
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
