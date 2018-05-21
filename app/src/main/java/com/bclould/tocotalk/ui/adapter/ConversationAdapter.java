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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bclould.tocotalk.R;
import com.bclould.tocotalk.history.DBManager;
import com.bclould.tocotalk.model.ConversationInfo;
import com.bclould.tocotalk.ui.activity.ConversationActivity;
import com.bclould.tocotalk.ui.fragment.ConversationFragment;
import com.bclould.tocotalk.ui.widget.DeleteCacheDialog;
import com.bclould.tocotalk.utils.MessageEvent;
import com.bclould.tocotalk.utils.StringUtils;
import com.bclould.tocotalk.utils.UtilTool;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by GA on 2017/12/20.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class ConversationAdapter extends RecyclerView.Adapter {

    private final Context mContext;
    private final List<ConversationInfo> mConversationList;
    private final DBManager mMgr;

    public ConversationAdapter(ConversationFragment conversationFragment, Context context, List<ConversationInfo> ConversationList, DBManager mgr) {
        mContext = context;
        mConversationList = ConversationList;
        mMgr = mgr;
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
        private byte[] mDatas;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            mRlItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent();
                    intent.setClass(mContext, ConversationActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("name", mConversationInfo.getFriend());
                    bundle.putString("user", mConversationInfo.getUser());
                    intent.putExtras(bundle);
                    mContext.startActivity(intent);
                    mNumber.setVisibility(View.GONE);
                    mMgr.updateConversation(mConversationInfo.getUser(), 0, mConversationInfo.getMessage(), mConversationInfo.getTime());
                    EventBus.getDefault().post(new MessageEvent(mContext.getString(R.string.dispose_unread_msg)));
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
            if ("true".equals(conversationInfo.getIstop())) {
                mRlItem.setBackgroundColor(mContext.getResources().getColor(R.color.gray2));
            } else {
                mRlItem.setBackgroundColor(mContext.getResources().getColor(R.color.white));
            }
            mConversationInfo = conversationInfo;
            UtilTool.getImage(mMgr, conversationInfo.getUser(), mContext, mTab1ItemImg);
           /* Bitmap bitmap = UtilTool.getImage(mMgr, conversationInfo.getUser(), mContext);
            mTab1ItemImg.setImageBitmap(bitmap);*/
            String remark = mMgr.queryRemark(conversationInfo.getUser());
            if (!StringUtils.isEmpty(remark)) {
                mTab1ItemName.setText(remark);
            } else {
                mTab1ItemName.setText(conversationInfo.getFriend());
            }
            mTab1ItemText.setText(conversationInfo.getMessage());
            mTime.setText(conversationInfo.getTime());
            if (conversationInfo.getNumber() != 0) {
                mNumber.setVisibility(View.VISIBLE);
                mNumber.setText(conversationInfo.getNumber() + "");
            }
        }
    }

    private void showDeleteDialog(final String user) {
        final DeleteCacheDialog deleteCacheDialog = new DeleteCacheDialog(R.layout.dialog_delete_cache, mContext, R.style.dialog);
        deleteCacheDialog.show();
        deleteCacheDialog.setTitle(mContext.getString(R.string.confirm_delete));
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
                    mMgr.deleteConversation(user);
                    EventBus.getDefault().post(new MessageEvent(mContext.getString(R.string.dispose_unread_msg)));
                    deleteCacheDialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

}