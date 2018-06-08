package com.bclould.tocotalk.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bclould.tocotalk.R;
import com.bclould.tocotalk.history.DBManager;
import com.bclould.tocotalk.model.UserInfo;
import com.bclould.tocotalk.ui.activity.ConversationActivity;
import com.bclould.tocotalk.utils.Constants;
import com.bclould.tocotalk.utils.MessageEvent;
import com.bclould.tocotalk.utils.StringUtils;
import com.bclould.tocotalk.utils.UtilTool;

import org.greenrobot.eventbus.EventBus;

import java.io.ByteArrayOutputStream;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by GA on 2017/9/29.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class SelectFriendAdapter extends RecyclerView.Adapter{

    private final Context mContext;
    private final List<UserInfo> mUsers;
    private final DBManager mMgr;
    private byte[] mDatas;
    private OnItemListener onItemListener;

    public SelectFriendAdapter(Context context, List<UserInfo> users, DBManager mgr) {
        mContext = context;
        mUsers = users;
        mMgr = mgr;
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

    public void addOnItemListener(OnItemListener onItemListener){
        this.onItemListener=onItemListener;
    }

    public interface OnItemListener{
        void onItemClick(String remark,String name,String user);
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
        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemListener.onItemClick(mFriendChildName.getText().toString(),mName
                            ,mUser);
                }
            });
        }

        public void setData(UserInfo userInfo, final int position) {
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

            UtilTool.Log("好友", userInfo.getUser());
            if (!StringUtils.isEmpty(userInfo.getRemark())) {
                mFriendChildName.setText(remark);
            } else {
                mFriendChildName.setText(mName);
            }

        }
    }
}
