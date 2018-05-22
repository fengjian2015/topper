package com.bclould.tocotalk.ui.adapter;

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

import com.bclould.tocotalk.R;
import com.bclould.tocotalk.history.DBManager;
import com.bclould.tocotalk.model.UserInfo;
import com.bclould.tocotalk.ui.activity.ConversationActivity;
import com.bclould.tocotalk.utils.Constants;
import com.bclould.tocotalk.utils.MessageEvent;
import com.bclould.tocotalk.utils.StringUtils;
import com.bclould.tocotalk.utils.UtilTool;

import org.greenrobot.eventbus.EventBus;

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

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, ConversationActivity.class);
                    Bundle bundle = new Bundle();
                    String name = mUser.substring(0, mUser.indexOf("@"));
                    bundle.putString("name", name);
                    String user = mFriendChildName.getText() + "@" + Constants.DOMAINNAME;
                    bundle.putString("name", mUser.substring(0, mUser.indexOf("@")));
                    bundle.putString("user", mUser);
                    intent.putExtras(bundle);
                    mMgr.updateNumber(mUser, 0);
                    EventBus.getDefault().post(new MessageEvent(mContext.getString(R.string.dispose_unread_msg)));
                    mContext.startActivity(intent);
                }
            });
        }

        public void setData(UserInfo userInfo, int position) {
            mUserInfo = userInfo;
            mUser = userInfo.getUser();
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
            } else if (mUser.contains("@"))
                mFriendChildName.setText(mUser.substring(0, mUser.indexOf("@")));
        }
    }
}
