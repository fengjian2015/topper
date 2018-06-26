package com.bclould.tea.ui.activity;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.bclould.tea.Presenter.GroupPresenter;
import com.bclould.tea.R;
import com.bclould.tea.base.BaseActivity;
import com.bclould.tea.history.DBManager;
import com.bclould.tea.history.DBRoomManage;
import com.bclould.tea.history.DBRoomMember;
import com.bclould.tea.model.RoomMemberInfo;
import com.bclould.tea.ui.adapter.SelectGroupMemberAdapter;
import com.bclould.tea.ui.widget.DeleteCacheDialog;
import com.bclould.tea.utils.MessageEvent;
import com.bclould.tea.utils.UtilTool;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

@RequiresApi(api = Build.VERSION_CODES.N)
public class SelectGroupMemberActivity extends BaseActivity implements SelectGroupMemberAdapter.OnItemListener{

    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    private String roomId;
    private DBRoomMember mDBRoomMember;
    private ArrayList<RoomMemberInfo> mList=new ArrayList<>();
    private SelectGroupMemberAdapter mAdapter;
    private DBManager mDBManager;
    private DBRoomManage mDBRoomManage;
    private int type=0;//1轉讓群主，2踢人
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_group_member);
        ButterKnife.bind(this);
        mDBRoomMember=new DBRoomMember(this);
        mDBManager=new DBManager(this);
        mDBRoomManage=new DBRoomManage(this);
        initIntent();
        init();
    }

    private void init() {
        mList.addAll(mDBRoomMember.queryAllRequest(roomId));
        for(RoomMemberInfo roomMemberInfo:mList){
            if(roomMemberInfo.getJid().equals(UtilTool.getTocoId())){
                mList.remove(roomMemberInfo);
                break;
            }
        }
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new SelectGroupMemberAdapter(this,mList,mDBManager,mDBRoomMember);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.addOnItemListener(this);
    }

    private void initIntent() {
        roomId=getIntent().getStringExtra("roomId");
        type=getIntent().getIntExtra("type",0);
    }


    @OnClick({R.id.bark})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;
        }
    }

    @Override
    public void onItemClick(RoomMemberInfo roomMemberInfo,String memberName) {
        transferGroup(roomMemberInfo,memberName);
    }

    private void transferGroup(final RoomMemberInfo roomMemberInfo, String memberName) {
        final DeleteCacheDialog deleteCacheDialog = new DeleteCacheDialog(R.layout.dialog_delete_cache, this, R.style.dialog);
        deleteCacheDialog.show();
        deleteCacheDialog.setTitle(getString(R.string.assign_to) + " " + memberName + " " + getString(R.string.what));
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
                    new GroupPresenter(SelectGroupMemberActivity.this).transferGroup(Integer.parseInt(roomId), roomMemberInfo.getJid(), new GroupPresenter.CallBack() {
                        @Override
                        public void send() {
                            mDBRoomManage.updateOwner(roomId,roomMemberInfo.getJid());
                            MessageEvent messageEvent= new MessageEvent(getString(R.string.refresh_group_room));
                            messageEvent.setId(roomId);
                            EventBus.getDefault().post(messageEvent);
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
}
