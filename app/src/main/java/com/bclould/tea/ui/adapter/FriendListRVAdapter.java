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
import com.bclould.tea.ui.activity.SelectFriendActivity;
import com.bclould.tea.ui.widget.MenuListPopWindow;
import com.bclould.tea.utils.MessageEvent;
import com.bclould.tea.utils.StringUtils;
import com.bclould.tea.utils.UtilTool;

import org.greenrobot.eventbus.EventBus;

import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.bclould.tea.ui.adapter.ChatAdapter.TO_CARD_MSG;

/**
 * Created by GA on 2017/9/29.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class FriendListRVAdapter extends RecyclerView.Adapter {

    private final Context mContext;
    private final List<UserInfo> mUsers;
    private final DBManager mMgr;
    private RelativeLayout mRlTitle;
    public FriendListRVAdapter(Context context, List<UserInfo> users, DBManager mgr, RelativeLayout mRlTitle) {
        mContext = context;
        mUsers = users;
        mMgr = mgr;
        this.mRlTitle=mRlTitle;
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

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);

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
            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    showRemarkDialog( mName,mFriendChildName.getText().toString(),mUser);
                    return false;
                }
            });
        }

        public void setData(UserInfo userInfo, int position) {
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

    private void showRemarkDialog(final String name, final String remark, final String user) {
        List<String> list = Arrays.asList(new String[]{mContext.getString(R.string.updata_remark),mContext.getString(R.string.send_card)});
        final MenuListPopWindow menu = new MenuListPopWindow(mContext, list);
        menu.setListOnClick(new MenuListPopWindow.ListOnClick() {
            @Override
            public void onclickitem(int position) {
                Intent intent;
                switch (position){
                    case 0:
                        menu.dismiss();
                        break;
                    case 1:
                        menu.dismiss();
                        intent=new Intent(mContext, RemarkActivity.class);
                        intent.putExtra("name",name);
                        intent.putExtra("remark",remark);
                        intent.putExtra("user",user);
                        mContext.startActivity(intent);
                        break;
                    case 2:
                        menu.dismiss();
                        UserInfo info = mMgr.queryUser(user);
                        intent = new Intent(mContext, SelectFriendActivity.class);
                        intent.putExtra("type", 2);
                        MessageInfo messageInfo=new MessageInfo();
                        messageInfo.setHeadUrl(info.getPath());
                        messageInfo.setMessage(name);
                        messageInfo.setCardUser(user);
                        intent.putExtra("msgType",TO_CARD_MSG);
                        intent.putExtra("messageInfo", messageInfo);
                        mContext.startActivity(intent);
                        break;
                }
            }
        });
        menu.setColor(Color.BLACK);
        menu.showAtLocation();
    }
}
