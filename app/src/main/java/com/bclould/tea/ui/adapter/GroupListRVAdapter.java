package com.bclould.tea.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bclould.tea.R;
import com.bclould.tea.model.RoomManageInfo;
import com.bclould.tea.ui.activity.GroupChatActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by GA on 2018/1/5.
 */

public class GroupListRVAdapter extends RecyclerView.Adapter {

    private final Context mContext;
    private final List<RoomManageInfo> mJoinChatRoom;

    public GroupListRVAdapter(Context context, ArrayList<RoomManageInfo> joinChatRoom) {
        mContext = context;
        mJoinChatRoom = joinChatRoom;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_group_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.setData(mJoinChatRoom.get(position).getRoomName());
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

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, GroupChatActivity.class);
                    intent.putExtra("groupname", mGroupName.getText());
                    mContext.startActivity(intent);
                }
            });
        }

        public void setData(String groupInfo) {
            mGroupName.setText(groupInfo);
        }
    }
}
