package com.bclould.tea.ui.adapter;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bclould.tea.R;
import com.bclould.tea.history.DBManager;
import com.bclould.tea.history.DBRoomMember;
import com.bclould.tea.model.RoomMemberInfo;
import com.bclould.tea.model.UserInfo;
import com.bclould.tea.utils.StringUtils;
import com.bclould.tea.utils.UtilTool;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by GA on 2017/9/29.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class SelectGroupMemberAdapter extends RecyclerView.Adapter{

    private final Context mContext;
    private final DBManager mMgr;
    private OnItemListener onItemListener;
    private ArrayList<RoomMemberInfo> list;
    private DBRoomMember mDBRoomMember;

    public SelectGroupMemberAdapter(Context context, ArrayList<RoomMemberInfo> list, DBManager DBManager, DBRoomMember mDBRoomMember) {
        mContext = context;
        this.list=list;
        mMgr = DBManager;
        this.mDBRoomMember=mDBRoomMember;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_friend_child, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.setData(list.get(position));
    }

    @Override
    public int getItemCount() {
        if (list.size() != 0) {
            return list.size();
        }
        return 0;
    }

    public void addOnItemListener(OnItemListener onItemListener){
        this.onItemListener=onItemListener;
    }

    public interface OnItemListener{
        void onItemClick(RoomMemberInfo roomMemberInfo,String memberName);
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.catalog)
        TextView mCatalog;
        @Bind(R.id.friend_child_touxiang)
        ImageView mFriendChildTouxiang;
        @Bind(R.id.friend_child_name)
        TextView mFriendChildName;

        RoomMemberInfo roomMemberInfo;
        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemListener.onItemClick(roomMemberInfo,mFriendChildName.getText().toString());
                }
            });
        }

        public void setData(RoomMemberInfo roomMemberInfo) {
            this.roomMemberInfo=roomMemberInfo;
            mCatalog.setVisibility(View.GONE);
            String userId=roomMemberInfo.getJid();
            String mName=mMgr.queryRemark(userId);
            if (!StringUtils.isEmpty(mName)) {
                mFriendChildName.setText(mName);
            } else {
                mFriendChildName.setText(roomMemberInfo.getName());
            }
            String url=mDBRoomMember.findMemberUrl(roomMemberInfo.getRoomId(),userId);
            if(StringUtils.isEmpty(url)&&mMgr.findUser(userId)){
                UserInfo info = mMgr.queryUser(userId);
                if (!info.getPath().isEmpty()) {
                    url=info.getPath();
                }
            }
            UtilTool.getImage(mContext, mFriendChildTouxiang,url);
        }
    }
}
