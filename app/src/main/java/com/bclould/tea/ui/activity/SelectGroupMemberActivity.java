package com.bclould.tea.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bclould.tea.Presenter.GroupPresenter;
import com.bclould.tea.R;
import com.bclould.tea.base.BaseActivity;
import com.bclould.tea.history.DBManager;
import com.bclould.tea.history.DBRoomManage;
import com.bclould.tea.history.DBRoomMember;
import com.bclould.tea.model.RoomMemberInfo;
import com.bclould.tea.ui.adapter.SelectGroupMemberAdapter;
import com.bclould.tea.ui.widget.DeleteCacheDialog;
import com.bclould.tea.utils.AppLanguageUtils;
import com.bclould.tea.utils.EventBusUtil;
import com.bclould.tea.utils.MessageEvent;
import com.bclould.tea.utils.MySharedPreferences;
import com.bclould.tea.utils.StringUtils;
import com.bclould.tea.utils.UtilTool;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

@RequiresApi(api = Build.VERSION_CODES.N)
public class SelectGroupMemberActivity extends BaseActivity implements SelectGroupMemberAdapter.OnItemListener {

    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    private String roomId;
    private DBRoomMember mDBRoomMember;
    private ArrayList<RoomMemberInfo> mList = new ArrayList<>();
    private ArrayList<RoomMemberInfo> oldList = new ArrayList<>();
    private SelectGroupMemberAdapter mAdapter;
    private DBManager mDBManager;
    private DBRoomManage mDBRoomManage;
    private int type = 0;//1轉讓群主，2踢人 ,3@群成員

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_group_member);
        ButterKnife.bind(this);
        setTitle(getString(R.string.new_group_manager),getString(R.string.confirm));
        mDBRoomMember = new DBRoomMember(this);
        mDBManager = new DBManager(this);
        mDBRoomManage = new DBRoomManage(this);
        initIntent();
        init();
    }


    private void init() {
        if(type!=1){
            mTvTitleTop.setText(getString(R.string.select_members));
        }
        if (type == 3) {
            mTvAdd.setVisibility(View.GONE);
        }
        mList.addAll(mDBRoomMember.queryAllRequest(roomId));
        for (RoomMemberInfo roomMemberInfo : mList) {
            if (roomMemberInfo.getJid().equals(UtilTool.getTocoId())) {
                mList.remove(roomMemberInfo);
                break;
            }
        }
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new SelectGroupMemberAdapter(this, mList, mDBManager, mDBRoomMember, type, oldList);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.addOnItemListener(this);
    }

    private void initIntent() {
        roomId = getIntent().getStringExtra("roomId");
        type = getIntent().getIntExtra("type", 0);
    }


    @OnClick({R.id.bark, R.id.tv_add})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;
            case R.id.tv_add:
                if (oldList.size() <= 0) {
                    return;
                }
                if (type == 1) {
                    transferGroup(oldList.get(0));
                } else if (type == 2) {
                    kickOut();
                }
                break;
        }
    }

    private void kickOut() {
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < oldList.size(); i++) {
            if (i == oldList.size() - 1) {
                stringBuffer.append(oldList.get(i).getJid());
            } else {
                stringBuffer.append(oldList.get(i).getJid() + ",");
            }
        }
        new GroupPresenter(this).kickOutGroup(Integer.parseInt(roomId), stringBuffer.toString(), new GroupPresenter.CallBack() {
            @Override
            public void send() {
                for (RoomMemberInfo roomMemberInfo : oldList) {
                    mDBRoomMember.deleteRoomMember(roomId, roomMemberInfo.getJid());
                }
                MessageEvent messageEvent = new MessageEvent(EventBusUtil.refresh_group_room);
                messageEvent.setId(roomId);
                EventBus.getDefault().post(messageEvent);
                finish();
            }
        });
    }

    private void transferGroup(final RoomMemberInfo roomMemberInfo) {
        String mName = mDBManager.queryRemark(roomMemberInfo.getJid());
        if (StringUtils.isEmpty(mName)) {
            mName = roomMemberInfo.getName();
        }
        final DeleteCacheDialog deleteCacheDialog = new DeleteCacheDialog(R.layout.dialog_delete_cache, this, R.style.dialog);
        deleteCacheDialog.show();
        deleteCacheDialog.setTitle(getString(R.string.assign_to) + " " + mName + " " + getString(R.string.what));
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
                            mDBRoomManage.updateOwner(roomId, roomMemberInfo.getJid());
                            MessageEvent messageEvent = new MessageEvent(EventBusUtil.refresh_group_room);
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

    @Override
    public void onItemClick(RoomMemberInfo roomMemberInfo, boolean isCheck) {
        if (type == 1) {
            if (isCheck) {
                oldList.clear();
                oldList.add(roomMemberInfo);
            } else {
                oldList.remove(roomMemberInfo);
            }
        } else if (type == 2) {
            if (isCheck) {
                oldList.add(roomMemberInfo);
            } else {
                oldList.remove(roomMemberInfo);
            }
        }
        mAdapter.notifyDataSetChanged();
        if (type == 3) {
            Intent intent = new Intent();
            intent.putExtra("name", roomMemberInfo.getName());
            intent.putExtra("tocoid", roomMemberInfo.getJid());
            setResult(RESULT_OK, intent);
            finish();
        }

    }
}
