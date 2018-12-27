package com.bclould.tea.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.GridView;

import com.bclould.tea.R;
import com.bclould.tea.base.BaseActivity;
import com.bclould.tea.base.MyApp;
import com.bclould.tea.history.DBManager;
import com.bclould.tea.history.DBRoomManage;
import com.bclould.tea.history.DBRoomMember;
import com.bclould.tea.model.RoomMemberInfo;
import com.bclould.tea.ui.adapter.GroupDetailsMemberAdapter;
import com.bclould.tea.utils.AppLanguageUtils;
import com.bclould.tea.utils.EventBusUtil;
import com.bclould.tea.utils.MessageEvent;
import com.bclould.tea.utils.MySharedPreferences;
import com.bclould.tea.utils.UtilTool;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

@RequiresApi(api = Build.VERSION_CODES.N)
public class GroupMemberActivity extends BaseActivity {
    @Bind(R.id.partner_detial_gridview)
    GridView mPartnerDetialGridview;

    private String roomId;
    private DBRoomMember mDBRoomMember;
    private DBRoomManage mDBRoomManage;
    private DBManager mMgr;
    private List<RoomMemberInfo> mList = new ArrayList<>();
    private GroupDetailsMemberAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_member);
        EventBus.getDefault().register(this);//初始化EventBus
        ButterKnife.bind(this);
        setTitle(getString(R.string.group_member_manage));
        mDBRoomMember = new DBRoomMember(this);
        mMgr=new DBManager(this);
        mDBRoomManage=new DBRoomManage(this);
        initIntent();
        setGroupMember(true);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(AppLanguageUtils.attachBaseContext(newBase, MySharedPreferences.getInstance().getString(newBase.getString(R.string.language_pref_key))));
    }

    private void initIntent() {
        Intent intent = getIntent();
        roomId = intent.getStringExtra("roomId");
    }

    private void setGroupMember(boolean isFirst) {
        new GetMember(isFirst).run();

    }

    class GetMember implements Runnable {
        private boolean isFirst;
        public GetMember(boolean isFirst){
            this.isFirst=isFirst;
        }
        @Override
        public void run() {
            mList.clear();
            //是群主添加兩個
            if (isOwner()) {
                mList.add(new RoomMemberInfo());
            }
            mList.add(new RoomMemberInfo());
            mList.addAll(mDBRoomMember.queryAllRequest(roomId));
            GroupMemberActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (isFirst) {
                        mAdapter = new GroupDetailsMemberAdapter(GroupMemberActivity.this, mList, roomId, mMgr, mDBRoomManage,mDBRoomMember);
                        mPartnerDetialGridview.setAdapter(mAdapter);
//            new GroupPresenter(this).selectGroupMember(Integer.parseInt(roomId), mDBRoomMember, true, mDBRoomManage, mMgr, new GroupPresenter.CallBack() {
//                @Override
//                public void send() {
//                    EventBus.getDefault().post(new MessageEvent(EventBusUtil.refresh_group_members));
//                }
//            });
                    } else {
                        mAdapter.notifyDataSetChanged();
                    }
                }
            });
        }
    }

    //接受通知
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        String msg = event.getMsg();
        if (msg.equals(EventBusUtil.quit_group)) {
            if (roomId.equals(event.getId())) {
                finish();
            }
        } else if (msg.equals(EventBusUtil.refresh_group_members)) {
            setGroupMember(false);
        }else if (msg.equals(EventBusUtil.refresh_group_room)) {
            if (roomId.equals(event.getId())) {
                setGroupMember(false);
            }
        } else if (msg.equals(EventBusUtil.kick_out_success)) {
            if (roomId.equals(event.getId())) {
                finish();
            }
        }
    }

    private boolean isOwner() {
        if (UtilTool.getTocoId().equals(mDBRoomManage.findRoomOwner(roomId))) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @OnClick({R.id.bark})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;
        }
    }
}
