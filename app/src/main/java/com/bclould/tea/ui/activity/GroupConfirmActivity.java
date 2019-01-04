package com.bclould.tea.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.bclould.tea.Presenter.GroupPresenter;
import com.bclould.tea.R;
import com.bclould.tea.base.BaseActivity;
import com.bclould.tea.history.DBManager;
import com.bclould.tea.history.DBRoomManage;
import com.bclould.tea.model.ConversationInfo;
import com.bclould.tea.model.RoomManageInfo;
import com.bclould.tea.utils.EventBusUtil;
import com.bclould.tea.utils.MessageEvent;
import com.bclould.tea.utils.ToastShow;
import com.bclould.tea.utils.UtilTool;
import com.bclould.tea.xmpp.RoomManage;
import org.greenrobot.eventbus.EventBus;
import java.text.SimpleDateFormat;
import java.util.Date;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class GroupConfirmActivity extends BaseActivity {

    @Bind(R.id.iv_touxiang)
    ImageView mIvTouxiang;
    @Bind(R.id.name)
    TextView mName;
    @Bind(R.id.tv_add)
    TextView mTvAdd;
    @Bind(R.id.btn_add)
    Button mBtnAdd;

    private String roomName;
    private String roomId;
    private String roomPath;
    private DBRoomManage mDBRoomManage;
    private DBManager mDBManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_confirm);
        ButterKnife.bind(this);
        setTitle("");
        mDBRoomManage = new DBRoomManage(this);
        mDBManager=new DBManager(this);
        getintent();
        init();
    }


    private void init() {
        mName.setText(roomName);
        UtilTool.getGroupImage(roomPath, this, mIvTouxiang);
        if(mDBRoomManage.findRoom(roomId)){
            mBtnAdd.setVisibility(View.GONE);
            mTvAdd.setVisibility(View.VISIBLE);
        }else{
            mTvAdd.setVisibility(View.GONE);
            mBtnAdd.setVisibility(View.VISIBLE);
        }
    }

    private void getintent() {
        roomName = getIntent().getStringExtra("roomName");
        roomId = getIntent().getStringExtra("roomId");
        roomPath = getIntent().getStringExtra("roomPath");
    }

    @OnClick({R.id.bark, R.id.btn_add})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;
            case R.id.btn_add:
                addRoom();
                break;
        }
    }

    private void addRoom() {
        new GroupPresenter(this).inviteGroup(roomId, UtilTool.getTocoId(), new GroupPresenter.CallBack() {
            @Override
            public void send() {
                ToastShow.showToast2(GroupConfirmActivity.this, getString(R.string.add_success));
                saveRoom(roomId);
                createConversation(roomId,roomName);
                EventBus.getDefault().post(new MessageEvent(EventBusUtil.oneself_send_msg));
                GroupConfirmActivity.this.finish();
            }
        });
    }

    private void saveRoom(String group_id){
        RoomManageInfo roomManageInfo = new RoomManageInfo();
        roomManageInfo.setRoomName(roomName);
        roomManageInfo.setRoomId(group_id);
        mDBRoomManage.addRoom(roomManageInfo);
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
        mDBManager.addConversation(info);
    }
}
