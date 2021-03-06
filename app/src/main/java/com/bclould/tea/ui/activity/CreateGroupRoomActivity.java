package com.bclould.tea.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import com.bclould.tea.Presenter.GroupPresenter;
import com.bclould.tea.R;
import com.bclould.tea.base.BaseActivity;
import com.bclould.tea.base.MyApp;
import com.bclould.tea.history.DBManager;
import com.bclould.tea.history.DBRoomManage;
import com.bclould.tea.history.DBRoomMember;
import com.bclould.tea.model.ConversationInfo;
import com.bclould.tea.model.RoomManageInfo;
import com.bclould.tea.model.RoomMemberInfo;
import com.bclould.tea.model.UserInfo;
import com.bclould.tea.ui.adapter.CreateGroupRVAdapter;
import com.bclould.tea.utils.EventBusUtil;
import com.bclould.tea.utils.MessageEvent;
import com.bclould.tea.utils.StringUtils;
import com.bclould.tea.utils.UtilTool;
import com.bclould.tea.xmpp.RoomManage;
import org.greenrobot.eventbus.EventBus;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.bclould.tea.topperchat.WsContans.TOCO_SERVICE;

/**
 * Created by GA on 2018/1/5.
 */

public class CreateGroupRoomActivity extends BaseActivity {
    @Bind(R.id.bark)
    ImageView mBark;
    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    private List<UserInfo> mUserInfos=new ArrayList<>();
    private List<UserInfo> mUserInfoList = new ArrayList<>();
    DBManager mgr;
    DBRoomManage mDBRoomManage;
    DBRoomMember mDBRoomMember;
    private String roomName;
    private String roomId;
    private Context context;

    private String tocoId;//快速創群傳遞過來的tocoid

    private int type=0;//0表示創建群，1表示邀請人加入
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group_room);
        context=this;
        ButterKnife.bind(this);
        setTitle(getString(R.string.select_friends),getString(R.string.confirm));
        initIntent();
        initData();
        initRecylerView();
    }

    private void initIntent() {
        type=getIntent().getIntExtra("type",0);
        roomName=getIntent().getStringExtra("roomName");
        roomId=getIntent().getStringExtra("roomId");
        tocoId=getIntent().getStringExtra("tocoId");
    }

    private void initData() {
        mgr = new DBManager(this);
        mDBRoomMember=new DBRoomMember(this);
        mDBRoomManage=new DBRoomManage(this);
        List<UserInfo> userInfos = mgr.queryAllUser();
        List<UserInfo> deleteInfos=new ArrayList<>();
        for (UserInfo info : userInfos) {
            if (info.getUser().equals(UtilTool.getTocoId())||(info.getUser().isEmpty()&&info!=null)
                    ||TOCO_SERVICE.equals(info.getUser())) {
                deleteInfos.add(info);
            }
            //首先移除傳遞過來的用戶
            if (!StringUtils.isEmpty(tocoId)&&info.getUser().equals(tocoId)) {
                mUserInfoList.add(info);
            }
        }

        userInfos.removeAll(deleteInfos);
        mUserInfos.addAll(userInfos);
        Collections.sort(mUserInfos);
    }

    private void initRecylerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        CreateGroupRVAdapter createGroupRVAdapter = new CreateGroupRVAdapter(this, mUserInfos,mgr,roomId,mDBRoomMember,mUserInfoList,tocoId);
        mRecyclerView.setAdapter(createGroupRVAdapter);
    }

    @OnClick({R.id.bark, R.id.tv_add})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;
            case R.id.tv_add:
                if(type==0){
                    createGroup();
                }else if(type==1){
                    inviteGroup();
                }
                break;
        }
    }

    private void inviteGroup(){
        StringBuffer stringBuffer=new StringBuffer();
        for (int i = 0; i < mUserInfoList.size(); i++) {  //添加群成员
            stringBuffer.append(mUserInfoList.get(i).getUser());
            if(i!=mUserInfoList.size()-1){
                stringBuffer.append(",");
            }
        }
        new GroupPresenter(context).inviteGroup(roomId, stringBuffer.toString(), new GroupPresenter.CallBack() {
            @Override
            public void send() {
                mDBRoomMember.addRoomMemberUserInfo(mUserInfoList,roomId);
                EventBus.getDefault().post(new MessageEvent(EventBusUtil.refresh_group_members));
                finish();
            }
        });
    }

    private void createGroup(){
        if (mUserInfoList != null)
            try {
                StringBuffer stringBuffer=new StringBuffer();
                stringBuffer.append(UtilTool.getTocoId()+",");
                for (int i = 0; i < mUserInfoList.size(); i++) {  //添加群成员
                    stringBuffer.append(mUserInfoList.get(i).getUser());
                    if(i!=mUserInfoList.size()-1){
                        stringBuffer.append(",");
                    }
                }
                if(StringUtils.isEmpty(roomName)){
                    roomName=UtilTool.getUser()+"、"+mUserInfoList.get(0).getUserName();
                }
                new GroupPresenter(context).createGroup(roomName, stringBuffer.toString(), "", new GroupPresenter.CallBack2() {
                    @Override
                    public void send(String group_id) {
                        createConversation(group_id,roomName);
                        saveRoom(group_id,mUserInfoList);
                        MyApp.getInstance().exit(ConversationActivity.class.getName());
                        Intent intent = new Intent(context, ConversationActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        Bundle bundle = new Bundle();
                        bundle.putString("name", roomName);
                        bundle.putString("user", group_id);
                        bundle.putString("chatType",RoomManage.ROOM_TYPE_MULTI);
                        intent.putExtras(bundle);
                        startActivity(intent);
                        EventBus.getDefault().post(new MessageEvent(EventBusUtil.oneself_send_msg));
                        EventBus.getDefault().post(new MessageEvent(getString(R.string.create_group_chat)));
                        finish();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        else
            Toast.makeText(this, "请选择好友", Toast.LENGTH_SHORT).show();
    }

    private void saveRoom(String group_id, List<UserInfo> userInfoList){
        userInfoList.add(mgr.queryUser(UtilTool.getTocoId()));
        RoomManageInfo roomManageInfo = new RoomManageInfo();
        roomManageInfo.setRoomName(roomName);
        roomManageInfo.setRoomId(group_id);
        mDBRoomManage.addRoom(roomManageInfo);
        for (UserInfo userInfo:userInfoList) {
            RoomMemberInfo roomMemberInfo = new RoomMemberInfo();
            roomMemberInfo.setRoomId(group_id);
            roomMemberInfo.setName(userInfo.getUserName());
            roomMemberInfo.setJid(userInfo.getUser());
            roomMemberInfo.setImage_url(userInfo.getPath());
            mDBRoomMember.addRoomMember(roomMemberInfo);
        }
    }

    private void createConversation(String room,String roomName){
        ConversationInfo info=new ConversationInfo();
        info.setChatType(RoomManage.ROOM_TYPE_MULTI);
        info.setIstop("false");
        info.setFriend(roomName);
        info.setUser(room);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());
        String time = formatter.format(curDate);
        info.setTime(time);
        info.setCreateTime(UtilTool.createChatCreatTime());
        info.setMessage("加入群聊");
        info.setCreateTime(UtilTool.createChatCreatTime());
        mgr.addConversation(info);
    }


    public void setData(UserInfo userInfo, boolean b) {
        if (b)
            mUserInfoList.add(userInfo);
        else
            mUserInfoList.remove(userInfo);
    }
}
