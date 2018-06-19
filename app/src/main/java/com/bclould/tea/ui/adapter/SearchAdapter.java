package com.bclould.tea.ui.adapter;

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
import com.bclould.tea.model.UserInfo;
import com.bclould.tea.ui.activity.ConversationActivity;
import com.bclould.tea.utils.MessageEvent;
import com.bclould.tea.utils.UtilTool;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by GA on 2018/1/27.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class SearchAdapter extends RecyclerView.Adapter {

    private final List<UserInfo> mUserInfos;
    private final Context mContext;
    private final DBManager mMgr;

    public SearchAdapter(Context context, List<UserInfo> userInfos, DBManager mgr) {
        mUserInfos = userInfos;
        mContext = context;
        mMgr = mgr;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_search, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.setData(mUserInfos.get(position));
    }

    @Override
    public int getItemCount() {
        if (mUserInfos.size() != 0) {
            return mUserInfos.size();
        }
        return 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.iv_touxiang)
        ImageView mIvTouxiang;
        @Bind(R.id.tv_name)
        TextView mTvName;
        private String mName;
        private String mUser;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, ConversationActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("name", mName);
                    bundle.putString("user", mUser);
                    intent.putExtras(bundle);
                    mMgr.updateNumber(mUser, 0);
                    EventBus.getDefault().post(new MessageEvent(mContext.getString(R.string.dispose_unread_msg)));
                    mContext.startActivity(intent);
                }
            });
        }

        public void setData(UserInfo userInfo) {
            mName = userInfo.getUserName();
            mUser = userInfo.getUser() ;
            UtilTool.getImage(mMgr, userInfo.getUser(), mContext, mIvTouxiang);
            if (!userInfo.getRemark().isEmpty()) {
                mTvName.setText(userInfo.getRemark());
            } else {
                mTvName.setText(userInfo.getUser());
            }
        }
    }
}
