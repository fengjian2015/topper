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
import android.widget.ImageView;
import android.widget.TextView;

import com.bclould.tocotalk.R;
import com.bclould.tocotalk.history.DBManager;
import com.bclould.tocotalk.model.UserInfo;
import com.bclould.tocotalk.ui.activity.ConversationActivity;
import com.bclould.tocotalk.utils.Constants;
import com.bclould.tocotalk.utils.MessageEvent;
import com.bclould.tocotalk.utils.UtilTool;

import org.greenrobot.eventbus.EventBus;

import java.io.ByteArrayOutputStream;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by GA on 2017/9/29.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class FriendListVPAdapter extends RecyclerView.Adapter {

    private final Context mContext;
    private final List<UserInfo> mUsers;
    private final DBManager mMgr;

    public FriendListVPAdapter(Context context, List<UserInfo> users, DBManager mgr) {
        mContext = context;
        /*UtilTool.sortList(users, "status", "ASC");
        UtilTool.sortList(users, "user", "ASC");*/
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
        viewHolder.setData(mUsers.get(position));
    }

    @Override
    public int getItemCount() {
        if (mUsers.size() != 0) {
            return mUsers.size();
        }
        return 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.friend_child_touxiang)
        ImageView mFriendChildTouxiang;
        @Bind(R.id.friend_child_name)
        TextView mFriendChildName;
        UserInfo mUserInfo;
        private byte[] mDatas;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, ConversationActivity.class);
                    Bundle bundle = new Bundle();
                    String user = mFriendChildName.getText() + "@" + Constants.DOMAINNAME;
                    bundle.putString("name", mFriendChildName.getText().toString());
                    bundle.putString("user", user);
                    if (mDatas != null)
                        bundle.putByteArray("image", mDatas);
                    intent.putExtras(bundle);
                    mMgr.updateNumber(user, 0);
                    EventBus.getDefault().post(new MessageEvent(mContext.getString(R.string.dispose_unread_msg)));
                    mContext.startActivity(intent);
                }
            });
        }

        public void setData(UserInfo userInfo) {
            mUserInfo = userInfo;
            String user = userInfo.getUser();
            Bitmap bitmap = null;
            if (!userInfo.getPath().isEmpty()) {
                String path = userInfo.getPath();
                bitmap = BitmapFactory.decodeFile(path);
            } else {
                BitmapDrawable bitmapDrawable = (BitmapDrawable) mContext.getDrawable(R.mipmap.img_nfriend_headshot1);
                bitmap = bitmapDrawable.getBitmap();
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try {
                if (baos != null && bitmap != null)
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                mDatas = baos.toByteArray();
                baos.close();
            } catch (Exception e) {
                UtilTool.Log("日志", e.getMessage());
                e.printStackTrace();
            }
            mFriendChildTouxiang.setImageBitmap(bitmap);
            UtilTool.Log("好友", user);
            if (user.contains("@"))
                mFriendChildName.setText(user.substring(0, user.indexOf("@")));
            /*if (userInfo.getStatus() == 1) {
                mFriendChildType.setText("在线");
                mFriendChildType.setTextColor(mContext.getColor(R.color.green));
            } else {
                mFriendChildType.setText("离线");
                mFriendChildType.setTextColor(mContext.getColor(R.color.red));
            }*/
        }
    }

    /*String user = mGroupBeanList.get(groupPosition).getEntries().get(childPosition).getUser();
            Bitmap bitmap = null;
            if (!mMgr.findUser(user)) {
                byte[] bytes = UtilTool.getUserImage(MyApp.getInstance().createConnection(), user);
                bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                UtilTool.saveImages(bitmap, user, mContext, mMgr);
            } else {
                List<UserInfo> userInfos = mMgr.queryUser(user);
                UserInfo userInfo = userInfos.get(0);
                bitmap = BitmapFactory.decodeFile(userInfo.getPath());
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            final byte[] datas = baos.toByteArray();
            try {
                baos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mFriendChildTouxiang.setImageBitmap(bitmap);
            mFriendChildName.setText(user.substring(0, user.indexOf("@")));

            for (String key : mFromMap.keySet()) {
                if (mFriendChildName.getText().equals(key)) {
                    if (mFromMap.get(key)) {
                        mFriendChildType.setText("在线");
                        mFriendChildType.setTextColor(mContext.getColor(R.color.green));
                    } else {
                        mFriendChildType.setText("离线");
                        mFriendChildType.setTextColor(mContext.getColor(R.color.red));
                    }
                }

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(mContext, ConversationActivity.class);
                        intent.putExtra("name", mFriendChildName.getText());
                        intent.putExtra("user", mFriendChildName.getText() + "@xmpp.bclould.com");
                        intent.putExtra("image", datas);
                        mContext.startActivity(intent);
                    }
                });

            }*/
}
