package com.bclould.tea.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bclould.tea.R;
import com.bclould.tea.history.DBManager;
import com.bclould.tea.model.MessageInfo;
import com.bclould.tea.model.UserInfo;
import com.bclould.tea.ui.activity.ConversationActivity;
import com.bclould.tea.ui.activity.RemarkActivity;
import com.bclould.tea.ui.activity.SelectConversationActivity;
import com.bclould.tea.ui.widget.MenuListPopWindow;
import com.bclould.tea.utils.MessageEvent;
import com.bclould.tea.utils.StringUtils;
import com.bclould.tea.utils.UtilTool;

import java.util.List;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by GA on 2017/9/29.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class FriendListRVAdapter extends RecyclerView.Adapter {

    private final Context mContext;
    private final List<UserInfo> mUsers;
    private final DBManager mMgr;
    private OnclickListener mOnclickListener;
    public FriendListRVAdapter(Context context, List<UserInfo> users, DBManager mgr) {
        mContext = context;
        mUsers = users;
        mMgr = mgr;
    }

    /**
     * 获取catalog首次出现位置
     */
    public int getPositionForSection(String catalog) {
        for (int i = 0; i < getItemCount(); i++) {
            String sortStr = mUsers.get(i).getFirstLetter();
            if (catalog.equalsIgnoreCase(sortStr)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_friend_child, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.setData(mUsers.get(position), position);
    }

    @Override
    public int getItemCount() {
        if (mUsers.size() != 0) {
            return mUsers.size();
        }
        return 0;
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.catalog)
        TextView mCatalog;
        @Bind(R.id.friend_child_touxiang)
        ImageView mFriendChildTouxiang;
        @Bind(R.id.friend_child_name)
        TextView mFriendChildName;
        UserInfo mUserInfo;
        private String mUser;
        private String mName;
        private  int position;
        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mOnclickListener!=null){
                        mOnclickListener.onclick(position);
                    }
                }
            });
            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if(mOnclickListener!=null){
                        mOnclickListener.onLongClick(position);
                    }
                    return false;
                }
            });
        }

        public void setData(UserInfo userInfo, int position) {
            this.position=position;
            mUserInfo = userInfo;
            mUser = userInfo.getUser();
            mName= userInfo.getUserName();
            String remark = userInfo.getRemark();
            //根据position获取首字母作为目录catalog
            String catalog = userInfo.getFirstLetter();

            //如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
            if (position == getPositionForSection(catalog)) {
                mCatalog.setVisibility(View.VISIBLE);
                mCatalog.setText(userInfo.getFirstLetter().toUpperCase());
            } else {
                mCatalog.setVisibility(View.GONE);
            }
            UtilTool.getImage(mMgr, userInfo.getUser(), mContext, mFriendChildTouxiang);
//            mFriendChildTouxiang.setImageBitmap(UtilTool.getImage(mMgr, userInfo.getUser(), mContext));
            UtilTool.Log("好友", mUser);
            if (!StringUtils.isEmpty(remark)) {
                mFriendChildName.setText(remark);
            } else
                mFriendChildName.setText(mName);
        }
    }

    public void setOnClickListener(OnclickListener onClickListener){
        this.mOnclickListener=onClickListener;
    }

    public interface OnclickListener{
        void onclick(int position);
        void onLongClick(int position);
    }
}
