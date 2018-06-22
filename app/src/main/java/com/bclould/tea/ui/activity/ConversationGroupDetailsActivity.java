package com.bclould.tea.ui.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bclould.tea.Presenter.GroupPresenter;
import com.bclould.tea.Presenter.PersonalDetailsPresenter;
import com.bclould.tea.R;
import com.bclould.tea.base.BaseActivity;
import com.bclould.tea.base.MyApp;
import com.bclould.tea.crypto.otr.OtrChatListenerManager;
import com.bclould.tea.history.DBManager;
import com.bclould.tea.history.DBRoomManage;
import com.bclould.tea.history.DBRoomMember;
import com.bclould.tea.model.ConversationInfo;
import com.bclould.tea.model.MessageInfo;
import com.bclould.tea.model.RoomMemberInfo;
import com.bclould.tea.ui.adapter.GroupDetailsMemberAdapter;
import com.bclould.tea.ui.widget.DeleteCacheDialog;
import com.bclould.tea.ui.widget.MyGridView;
import com.bclould.tea.utils.MessageEvent;
import com.bclould.tea.utils.MySharedPreferences;
import com.bclould.tea.utils.StringUtils;
import com.bclould.tea.utils.UtilTool;
import com.bclould.tea.xmpp.RoomManage;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.bclould.tea.Presenter.GroupPresenter.*;
import static com.bclould.tea.utils.MySharedPreferences.SETTING;

@RequiresApi(api = Build.VERSION_CODES.N)
public class ConversationGroupDetailsActivity extends BaseActivity {

    @Bind(R.id.bark)
    ImageView mBark;
    @Bind(R.id.tv_member_number)
    TextView mTvMemberNumber;
    @Bind(R.id.partner_detial_gridview)
    MyGridView mPartnerDetialGridview;
    @Bind(R.id.tvgroupr_name)
    TextView mTvgrouprName;
    @Bind(R.id.on_off_top)
    ImageView mOnOffTop;
    @Bind(R.id.on_off_message_free)
    ImageView mOnOffMessageFree;
    @Bind(R.id.tv_member_name)
    TextView mTvMemberName;

    private GroupDetailsMemberAdapter mAdapter;
    private List<RoomMemberInfo> mList=new ArrayList<>();
    private String roomId;
    private String roomName;
    private DBRoomMember mDBRoomMember;
    private DBRoomManage mDBRoomManage;
    private DBManager mMgr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation_group_details);
        ButterKnife.bind(this);
        MyApp.getInstance().addActivity(this);
        EventBus.getDefault().register(this);//初始化EventBus
        initIntent();
        init();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void initIntent() {
        Intent intent = getIntent();
        roomId = intent.getStringExtra("roomId");
    }

    private void init() {
        mDBRoomMember = new DBRoomMember(this);
        mMgr = new DBManager(this);
        mDBRoomManage = new DBRoomManage(this);
        roomName = mDBRoomManage.findRoomName(roomId);
        changeTop();
        changeFree();
        setGroupName();
        setGroupMember(true);
    }

    private void setGroupMember(boolean isFirst) {
        mList.clear();
        mList.addAll(mDBRoomMember.queryAllRequest(roomId));
        mTvMemberNumber.setText(mList.size()+"人");
        mList.add(new RoomMemberInfo());
        if(isFirst){
            mAdapter=new GroupDetailsMemberAdapter(this,mList,roomId);
            mPartnerDetialGridview.setAdapter(mAdapter);
            new GroupPresenter(this).selectGroupMember(Integer.parseInt(roomId), mDBRoomMember, true, new GroupPresenter.CallBack() {
                @Override
                public void send() {
                    EventBus.getDefault().post(new MessageEvent(getString(R.string.refresh_group_members)));
                }
            });
        }else{
            mAdapter.notifyDataSetChanged();
        }

    }

    private void setGroupName() {
        mTvgrouprName.setText(mDBRoomManage.findRoomName(roomId));
    }

    private void changeTop() {
        String istop = mMgr.findConversationIstop(roomId);
        if ("true".equals(istop)) {
            mOnOffTop.setSelected(true);
        } else {
            mOnOffTop.setSelected(false);
        }
    }

    private void changeFree() {
        boolean free = MySharedPreferences.getInstance().getBoolean(SETTING + roomId + UtilTool.getTocoId());
        mOnOffMessageFree.setSelected(free);
    }

    @OnClick({R.id.bark, R.id.on_off_message_free, R.id.on_off_top, R.id.rl_empty_talk,R.id.btn_brak,R.id.rl_group_qr})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
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
            case R.id.btn_brak:
                deleteGroup();
                break;
            case R.id.rl_group_qr:
                // TODO: 2018/6/20 跳轉二維碼 
                break;
        }
    }

    //接受通知
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        String msg = event.getMsg();
        if (msg.equals(getString(R.string.quit_group))) {
            if(roomId.equals(event.getId())){
                finish();
            }
        }else if(msg.equals(getString(R.string.refresh_group_members))){
            setGroupMember(false);
        }
    }

    private void deleteGroup(){
        final DeleteCacheDialog deleteCacheDialog = new DeleteCacheDialog(R.layout.dialog_delete_cache, this, R.style.dialog);
        deleteCacheDialog.show();
        deleteCacheDialog.setTitle(getString(R.string.determine_exit) + " " + roomName + " " + getString(R.string.what));
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
                    new GroupPresenter(ConversationGroupDetailsActivity.this).deleteGroup(Integer.parseInt(roomId), UtilTool.getTocoId(), new CallBack() {
                        @Override
                        public void send() {
                            mDBRoomManage.deleteRoom(roomId);
                            mDBRoomMember.deleteRoom(roomId);
                            mMgr.deleteConversation(roomId);
                            mMgr.deleteMessage(roomId);
                            MessageEvent messageEvent=new MessageEvent(getString(R.string.quit_group));
                            messageEvent.setId(roomId);
                            EventBus.getDefault().post(messageEvent);
                        }
                    });
                    deleteCacheDialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void messageFree() {
        boolean free = MySharedPreferences.getInstance().getBoolean(SETTING + roomId + UtilTool.getTocoId());
        if (free) {
            mOnOffMessageFree.setSelected(false);
            MySharedPreferences.getInstance().setBoolean(SETTING + roomId + UtilTool.getTocoId(), false);
        } else {
            mOnOffMessageFree.setSelected(true);
            MySharedPreferences.getInstance().setBoolean(SETTING + roomId + UtilTool.getTocoId(), true);
        }
    }

    private void messageTop() {
        String istop = mMgr.findConversationIstop(roomId);
        if (StringUtils.isEmpty(istop)) {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date curDate = new Date(System.currentTimeMillis());
            String time = formatter.format(curDate);
            ConversationInfo info = new ConversationInfo();
            info.setTime(time);
            info.setFriend(roomName);
            info.setUser(roomId);
            info.setMessage("");
            info.setChatType(RoomManage.ROOM_TYPE_MULTI);
            mMgr.addConversation(info);
            mOnOffTop.setSelected(true);
            mMgr.updateConversationIstop(roomId, "true");
        } else if ("true".equals(istop)) {
            mOnOffTop.setSelected(false);
            mMgr.updateConversationIstop(roomId, "false");
        } else {
            mOnOffTop.setSelected(true);
            mMgr.updateConversationIstop(roomId, "true");
        }
        EventBus.getDefault().post(new MessageEvent(getString(R.string.message_top_change)));
    }

    private void clearMessage() {
        final DeleteCacheDialog deleteCacheDialog = new DeleteCacheDialog(R.layout.dialog_delete_cache, this, R.style.dialog);
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
                    mMgr.deleteMessage(roomId);
                    mMgr.updateConversationMessage(roomId, "");
                    Toast.makeText(ConversationGroupDetailsActivity.this, getString(R.string.empty_success), Toast.LENGTH_SHORT).show();
                    EventBus.getDefault().post(new MessageEvent(getString(R.string.msg_database_update)));
                    deleteCacheDialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }
}
