package com.bclould.tea.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.bclould.tea.R;
import com.bclould.tea.history.DBConversationBurnManage;
import com.bclould.tea.history.DBManager;
import com.bclould.tea.model.ConversationInfo;
import com.bclould.tea.ui.activity.ConversationBurnActivity;
import com.bclould.tea.ui.widget.DeleteCacheDialog;
import com.bclould.tea.utils.ChatTimeUtil;
import com.bclould.tea.utils.EventBusUtil;
import com.bclould.tea.utils.MessageEvent;
import com.bclould.tea.utils.StringUtils;
import org.greenrobot.eventbus.EventBus;
import java.util.List;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by GA on 2017/12/20.
 */

public class ConversationBurnListAdapter extends RecyclerView.Adapter {

    private final Context mContext;
    private final List<ConversationInfo> mConversationList;
    private DBConversationBurnManage mDBConversationBurnManage;
    private DBManager mDBManager;

    public ConversationBurnListAdapter(Context context, List<ConversationInfo> ConversationList,DBConversationBurnManage mDBConversationBurnManage,DBManager mDBManager) {
        mContext = context;
        mConversationList = ConversationList;
        this.mDBConversationBurnManage=mDBConversationBurnManage;
        this.mDBManager=mDBManager;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.fragment_conversation_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.setData(mConversationList.get(position));
    }

    @Override
    public int getItemCount() {
        if (mConversationList != null) {
            return mConversationList.size();
        }
        return 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.tab1_item_img)
        ImageView mTab1ItemImg;
        @Bind(R.id.tab1_item_name)
        TextView mTab1ItemName;
        @Bind(R.id.tab1_item_text)
        TextView mTab1ItemText;
        @Bind(R.id.time)
        TextView mTime;
        @Bind(R.id.number)
        TextView mNumber;
        @Bind(R.id.rl_item)
        RelativeLayout mRlItem;
        ConversationInfo mConversationInfo;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            mRlItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent();
                    intent.setClass(mContext, ConversationBurnActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("name", mConversationInfo.getFriend());
                    bundle.putString("user", mConversationInfo.getUser());
                    bundle.putString("chatType", mConversationInfo.getChatType());
                    intent.putExtras(bundle);
                    mContext.startActivity(intent);
                    mNumber.setVisibility(View.GONE);
                    mConversationInfo.setNumber(0);
                    mDBConversationBurnManage.updateConversation(mConversationInfo);
                    EventBus.getDefault().post(new MessageEvent(EventBusUtil.dispose_unread_msg));
                }
            });
            mRlItem.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    showDeleteDialog(mConversationInfo.getUser());
                    return false;
                }
            });
        }


        public void setData(ConversationInfo conversationInfo) {
            mConversationInfo = conversationInfo;
            mTab1ItemImg.setImageResource(R.mipmap.img_nfriend_headshot1);
            String remark = mDBManager.queryRemark(conversationInfo.getUser());
            if (!StringUtils.isEmpty(remark)) {
                mTab1ItemName.setText(remark);
            } else {
                mTab1ItemName.setText(conversationInfo.getFriend());
            }
            mTab1ItemText.setText(R.string.message);
            mTime.setText(ChatTimeUtil.getConversation(conversationInfo.getCreateTime()));
            if (conversationInfo.getNumber() != 0) {
                mNumber.setVisibility(View.VISIBLE);
                if(conversationInfo.getNumber()>=100){
                    mNumber.setText("99+");
                }else {
                    mNumber.setText(conversationInfo.getNumber() + "");
                }
            }else{
                mNumber.setVisibility(View.GONE);
            }
        }
    }

    private void showDeleteDialog(final String user) {
        final DeleteCacheDialog deleteCacheDialog = new DeleteCacheDialog(R.layout.dialog_delete_cache, mContext, R.style.dialog);
        deleteCacheDialog.show();
        deleteCacheDialog.setTitle(mContext.getString(R.string.confirm_delete_and_message));
        Button cancel = (Button) deleteCacheDialog.findViewById(R.id.btn_cancel);
        Button confirm = (Button) deleteCacheDialog.findViewById(R.id.btn_confirm);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteCacheDialog.dismiss();
            }
        });
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    mDBConversationBurnManage.deleteConversation(user);
                    mDBManager.deleteMessage(user,1);
                    EventBus.getDefault().post(new MessageEvent(EventBusUtil.dispose_unread_msg));
                    deleteCacheDialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

}