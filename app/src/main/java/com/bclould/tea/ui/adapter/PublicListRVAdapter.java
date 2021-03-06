package com.bclould.tea.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bclould.tea.R;
import com.bclould.tea.model.PublicInfo;
import com.bclould.tea.utils.UtilTool;
import java.util.List;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by GA on 2017/9/29.
 */

public class PublicListRVAdapter extends RecyclerView.Adapter {

    private final Context mContext;
    private final List<PublicInfo.DataBean> mUsers;
    private OnclickListener mOnclickListener;
    public PublicListRVAdapter(Context context, List<PublicInfo.DataBean> users) {
        mContext = context;
        mUsers = users;
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
        PublicInfo.DataBean mUserInfo;
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

        public void setData(PublicInfo.DataBean userInfo, int position) {
            this.position=position;
            mUserInfo = userInfo;
            //根据position获取首字母作为目录catalog
            String catalog = userInfo.getFirstLetter();

            //如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
            if (position == getPositionForSection(catalog)) {
                mCatalog.setVisibility(View.VISIBLE);
                mCatalog.setText(userInfo.getFirstLetter().toUpperCase());
            } else {
                mCatalog.setVisibility(View.GONE);
            }
            UtilTool.setCircleImg(mContext, userInfo.getLogo(), mFriendChildTouxiang);
            mFriendChildName.setText(userInfo.getName());
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
