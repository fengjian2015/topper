package com.bclould.tocotalk.ui.activity;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bclould.tocotalk.R;
import com.bclould.tocotalk.base.BaseActivity;
import com.bclould.tocotalk.history.DBManager;
import com.bclould.tocotalk.model.ConversationInfo;
import com.bclould.tocotalk.model.UserInfo;
import com.bclould.tocotalk.ui.adapter.CreateGroupRVAdapter;
import com.bclould.tocotalk.utils.MessageEvent;
import com.bclould.tocotalk.utils.StringUtils;
import com.bclould.tocotalk.utils.UtilTool;
import com.bclould.tocotalk.xmpp.RoomManage;
import com.bclould.tocotalk.xmpp.XmppConnection;

import org.greenrobot.eventbus.EventBus;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.muc.MultiUserChat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by GA on 2018/1/5.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class CreateGroupRoomActivity extends BaseActivity {
    @Bind(R.id.bark)
    ImageView mBark;
    @Bind(R.id.tv_create)
    TextView mTvCreate;
    @Bind(R.id.et_group_name)
    EditText mEtGroupName;
    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    private List<UserInfo> mUserInfos;
    private List<UserInfo> mUserInfoList = new ArrayList<>();
    DBManager mgr;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group_room);
        ButterKnife.bind(this);
        initData();
        initRecylerView();
    }

    private void initData() {
        mgr = new DBManager(this);
        mUserInfos = mgr.queryAllUser();
    }

    private void initRecylerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        CreateGroupRVAdapter createGroupRVAdapter = new CreateGroupRVAdapter(this, mUserInfos);
        mRecyclerView.setAdapter(createGroupRVAdapter);
    }

    @OnClick({R.id.bark, R.id.tv_create})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;
            case R.id.tv_create:
                XmppConnection xmppConnection = XmppConnection.getInstance();
                if (mUserInfoList != null)
                    try {
                    String roomJid= UtilTool.getUser()+System.currentTimeMillis() + "@conference." + XmppConnection.getInstance().getConnection().getServiceName();
                    String roomName=mEtGroupName.getText().toString();
                    String nickName=UtilTool.getUser();
                        if(StringUtils.isEmpty(roomName)){
                            roomName="群聊";
                        }
                        MultiUserChat multiUserChat=RoomManage.getInstance().addMultiMessageManage(roomJid
                                ,roomName).createRoom(roomName,nickName,mUserInfoList);
                        if(multiUserChat==null){
                            RoomManage.getInstance().removeRoom(roomJid);
                        }else {
                            createConversation(roomJid,roomName);
                            EventBus.getDefault().post(new MessageEvent(getString(R.string.oneself_send_msg)));
                        }
                        finish();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                else
                    Toast.makeText(this, "请选择好友", Toast.LENGTH_SHORT).show();
                break;
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
        info.setMessage("加入群聊");
        mgr.addConversation(info);
    }


    public void setData(UserInfo userInfo, boolean b) {
        if (b)
            mUserInfoList.add(userInfo);
        else
            mUserInfoList.remove(userInfo);
    }


}
