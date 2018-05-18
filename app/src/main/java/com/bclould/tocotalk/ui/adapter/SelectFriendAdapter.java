package com.bclould.tocotalk.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
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
public class SelectFriendAdapter extends BaseAdapter {

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
    public int getCount() {
        return mUsers.size();
    }

    @Override
    public Object getItem(int i) {
        return mUsers.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int position, View view, ViewGroup viewGroup) {
        Collections.sort(mUsers);
        final ViewHolder viewHolder;
        UserInfo userInfo = mUsers.get(position);
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_friend_child, null);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        //根据position获取首字母作为目录catalog
        String catalog = mUsers.get(position).getFirstLetter();

        //如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
        if (position == getPositionForSection(catalog)) {
            viewHolder.mCatalog.setVisibility(View.VISIBLE);
            viewHolder.mCatalog.setText(userInfo.getFirstLetter().toUpperCase());
        } else {
            viewHolder.mCatalog.setVisibility(View.GONE);
        }
        Bitmap bitmap = null;
        if (!userInfo.getPath().isEmpty()) {
            String path = userInfo.getPath();
            bitmap = BitmapFactory.decodeFile(path);
        } else {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) mContext.getDrawable(R.mipmap.img_nfriend_headshot1);
            bitmap = bitmapDrawable.getBitmap();
        }
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            if (baos != null && bitmap != null)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            mDatas = baos.toByteArray();
            baos.close();
        } catch (Exception e) {
            UtilTool.Log("日志", e.getMessage());
            e.printStackTrace();
        }
        viewHolder.mFriendChildTouxiang.setImageBitmap(bitmap);
        UtilTool.Log("好友", userInfo.getUser());
        if (!StringUtils.isEmpty(userInfo.getRemark())) {
            viewHolder.mFriendChildName.setText(userInfo.getRemark());
        } else if (userInfo.getUser().contains("@")) {
            viewHolder.mFriendChildName.setText(userInfo.getUser().substring(0, userInfo.getUser().indexOf("@")));
        }
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemListener.onItemClick(viewHolder.mFriendChildName.getText().toString()
                        ,mUsers.get(position).getUser());
            }
        });
        return view;
    }

    public void addOnItemListener(OnItemListener onItemListener){
        this.onItemListener=onItemListener;
    }

    public interface OnItemListener{
        void onItemClick(String name,String user);
    }

    /**
     * 获取catalog首次出现位置
     */
    public int getPositionForSection(String catalog) {
        for (int i = 0; i < getCount(); i++) {
            String sortStr = mUsers.get(i).getFirstLetter();
            if (catalog.equalsIgnoreCase(sortStr)) {
                return i;
            }
        }
        return -1;
    }

    class ViewHolder {
        @Bind(R.id.catalog)
        TextView mCatalog;
        @Bind(R.id.friend_child_touxiang)
        ImageView mFriendChildTouxiang;
        @Bind(R.id.friend_child_name)
        TextView mFriendChildName;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
