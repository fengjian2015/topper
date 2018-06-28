package com.bclould.tea.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bclould.tea.R;
import com.bclould.tea.history.DBManager;
import com.bclould.tea.history.DBRoomManage;
import com.bclould.tea.model.RoomManageInfo;
import com.bclould.tea.ui.activity.ConversationActivity;
import com.bclould.tea.utils.MessageEvent;
import com.bclould.tea.utils.UtilTool;
import com.bclould.tea.xmpp.RoomManage;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by GA on 2018/1/5.
 */
@RequiresApi(api = Build.VERSION_CODES.N)
public class GroupListRVAdapter extends RecyclerView.Adapter {

    private final Context mContext;
    private final List<RoomManageInfo> mJoinChatRoom;
    private DBManager mDBManager;
    private ArrayList<Boolean> roomList;
    private DBRoomManage dbRoomManage;

    public GroupListRVAdapter(Context context, ArrayList<RoomManageInfo> joinChatRoom, DBManager mDBManager, ArrayList<Boolean> roomList, DBRoomManage dbRoomManage) {
        mContext = context;
        this.mDBManager=mDBManager;
        mJoinChatRoom = joinChatRoom;
        this.roomList=roomList;
        this.dbRoomManage=dbRoomManage;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_group_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.setData(mJoinChatRoom.get(position));
    }

    @Override
    public int getItemCount() {
        if (mJoinChatRoom != null) {
            return mJoinChatRoom.size();
        }
        return 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.group_touxiang)
        ImageView mGroupTouxiang;
        @Bind(R.id.group_name)
        TextView mGroupName;

        RoomManageInfo mRoomManageInfo;
        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent();
                    intent.setClass(mContext, ConversationActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("name",mRoomManageInfo.getRoomName());
                    bundle.putString("user", mRoomManageInfo.getRoomId());
                    bundle.putString("chatType", RoomManage.ROOM_TYPE_MULTI);
                    intent.putExtras(bundle);
                    mContext.startActivity(intent);
                    mDBManager.updateConversationNumber(mRoomManageInfo.getRoomId(), 0);
                    EventBus.getDefault().post(new MessageEvent(mContext.getString(R.string.dispose_unread_msg)));
                    ((Activity)mContext).finish();
                }
            });
        }

        public void setData(RoomManageInfo groupInfo) {
            mRoomManageInfo=groupInfo;
            mGroupName.setText(groupInfo.getRoomName());
            UtilTool.getGroupImage(dbRoomManage,mRoomManageInfo.getRoomId(),mContext,mGroupTouxiang);
        }
    }
}
