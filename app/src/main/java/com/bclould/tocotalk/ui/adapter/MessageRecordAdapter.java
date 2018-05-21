package com.bclould.tocotalk.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bclould.tocotalk.R;
import com.bclould.tocotalk.history.DBManager;
import com.bclould.tocotalk.model.MessageInfo;
import com.bclould.tocotalk.ui.activity.ConversationActivity;
import com.bclould.tocotalk.ui.activity.ConversationRecordFindActivity;
import com.bclould.tocotalk.utils.StringUtils;
import com.bclould.tocotalk.utils.UtilTool;

import java.io.Serializable;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.bclould.tocotalk.ui.activity.ConversationRecordFindActivity.DATE_MSG;
import static com.bclould.tocotalk.ui.activity.ConversationRecordFindActivity.IMAGE_MSG;
import static com.bclould.tocotalk.ui.activity.ConversationRecordFindActivity.TEXT_MSG;
import static com.bclould.tocotalk.ui.activity.ConversationRecordFindActivity.TEXT_SELECT;
import static com.bclould.tocotalk.ui.activity.ConversationRecordFindActivity.TRADE_MSG;
import static com.bclould.tocotalk.ui.activity.ConversationRecordFindActivity.VIDEO_MSG;
import static com.bclould.tocotalk.ui.adapter.ChatAdapter.FROM_RED_MSG;
import static com.bclould.tocotalk.ui.adapter.ChatAdapter.FROM_TRANSFER_MSG;
import static com.bclould.tocotalk.ui.adapter.ChatAdapter.TO_RED_MSG;
import static com.bclould.tocotalk.ui.adapter.ChatAdapter.TO_TRANSFER_MSG;

/**
 * Created by GIjia on 2018/5/15.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class MessageRecordAdapter extends RecyclerView.Adapter {
    private Context context;
    private List<MessageInfo> messageInfoList;
    private int type;
    private DBManager mMdb;

    public MessageRecordAdapter(Context context, List<MessageInfo> messageInfoList, DBManager mMdb) {
        this.context = context;
        this.messageInfoList = messageInfoList;
        this.mMdb = mMdb;
    }

    public void setType(int type) {
        this.type = type;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return type;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (type == DATE_MSG) {

        } else if (type == IMAGE_MSG) {

        } else if (type == VIDEO_MSG) {

        } else if (type == TRADE_MSG) {
            return new RedViewHolder(LayoutInflater.from(context).inflate(R.layout.message_record_red_item, null));
        } else if (type == TEXT_MSG) {
            return new TextViewHolder(LayoutInflater.from(context).inflate(R.layout.message_record_text_item, null));
        }else if(type == TEXT_SELECT){
            return new TextInputViewHolder(LayoutInflater.from(context).inflate(R.layout.message_record_text_item2, null));
        }
        return new TextViewHolder(LayoutInflater.from(context).inflate(R.layout.message_record_text_item, null));
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        int itemViewType = getItemViewType(position);
        if (itemViewType == DATE_MSG) {

        } else if (itemViewType == IMAGE_MSG) {

        } else if (itemViewType == VIDEO_MSG) {

        } else if (itemViewType == TRADE_MSG) {
            setReadData(holder, position);
        } else if (itemViewType == TEXT_MSG) {
            setTextData(holder,position);
        }else if(itemViewType == TEXT_SELECT){
            setTextInputData(holder,position);
        }
    }

    private void setTextInputData(RecyclerView.ViewHolder holder, int position){
        ((TextInputViewHolder) holder).tvContent.setText(messageInfoList.get(position).getMessage());
        ((TextInputViewHolder)holder).rlSerach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ConversationRecordFindActivity)context).searchType(TEXT_MSG);
            }
        });
    }

    private void setTextData(RecyclerView.ViewHolder holder, final int position) {
        String send=messageInfoList.get(position).getSend();
        if(StringUtils.isEmpty(send)){
            send="";
        }
        String remark=mMdb.queryRemark(send);
        if(!StringUtils.isEmpty(remark)){
            ((TextViewHolder) holder).tvName.setText(remark);
        }else {
            ((TextViewHolder) holder).tvName.setText(send.split("@")[0]);
        }
        ((TextViewHolder) holder).tvContent.setText(messageInfoList.get(position).getMessage());
        ((TextViewHolder) holder).tvTime.setText(messageInfoList.get(position).getTime());

        UtilTool.getImage(mMdb, send, context, ((TextViewHolder)holder).ivHead);
        /*Bitmap bitmap=UtilTool.getImage(mMdb, send, context);
        ((TextViewHolder) holder).ivHead.setImageBitmap(bitmap);*/
        ((TextViewHolder) holder).rlCache.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context, ConversationActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                intent.putExtra("MessageInfo", (Serializable) messageInfoList.get(position));
                context.startActivity(intent);
                ((Activity)context).finish();
            }
        });
    }

    private void setReadData(RecyclerView.ViewHolder holder, final int position) {
        int msgtype = messageInfoList.get(position).getMsgType();
        String send=messageInfoList.get(position).getSend();
        String from=messageInfoList.get(position).getUsername();
        if(StringUtils.isEmpty(send)){
            send="";
        }
        if (StringUtils.isEmpty(from)){
            from="";
        }
        String mToName = send.split("@")[0];
        String fromName=from.split("@")[0] ;

        String toRemark=mMdb.queryRemark(send);
        if(!StringUtils.isEmpty(toRemark)){
            mToName=toRemark;
        }
        String fromRemark=mMdb.queryRemark(from);
        if(!StringUtils.isEmpty(fromRemark)){
            fromName=fromRemark;
        }

        if (msgtype == TO_TRANSFER_MSG) {
            ((RedViewHolder) holder).tvName.setText(mToName + context.getString(R.string.a_transfer));
            ((RedViewHolder) holder).ivType.setImageResource(R.mipmap.icon_record_transfer);
            if (messageInfoList.get(position).getStatus() == 1) {
                ((RedViewHolder) holder).tvState.setText(context.getString(R.string.transfer_give) +fromName);
            } else {
                ((RedViewHolder) holder).tvState.setText(messageInfoList.get(position).getRemark());
            }
        } else if (msgtype == TO_RED_MSG) {
            ((RedViewHolder) holder).tvName.setText(mToName + context.getString(R.string.send_a_red_envelope));
            ((RedViewHolder) holder).ivType.setImageResource(R.mipmap.icon_record_redp);
            if (messageInfoList.get(position).getStatus() == 1) {
                ((RedViewHolder) holder).tvState.setText(context.getString(R.string.red_envelopes_collected));
            } else {
                ((RedViewHolder) holder).tvState.setText(context.getString(R.string.look_red_packet));
            }
        } else if (msgtype == FROM_TRANSFER_MSG) {
            ((RedViewHolder) holder).tvName.setText(mToName + context.getString(R.string.a_transfer));
            ((RedViewHolder) holder).ivType.setImageResource(R.mipmap.icon_record_transfer);
            if (messageInfoList.get(position).getStatus() == 1) {
                ((RedViewHolder) holder).tvState.setText(context.getString(R.string.transfer_took));
            } else {
                ((RedViewHolder) holder).tvState.setText(messageInfoList.get(position).getRemark());
            }
        } else if (msgtype == FROM_RED_MSG) {
            ((RedViewHolder) holder).tvName.setText(mToName + context.getString(R.string.send_a_red_envelope));
            ((RedViewHolder) holder).ivType.setImageResource(R.mipmap.icon_record_redp);
            if (messageInfoList.get(position).getStatus() == 1) {
                ((RedViewHolder) holder).tvState.setText(context.getString(R.string.took_red_packet));
            } else {
                ((RedViewHolder) holder).tvState.setText(context.getString(R.string.look_red_packet));
            }
        }

        /*Bitmap bitmap=UtilTool.getImage(mMdb, send, context);
        ((RedViewHolder) holder).ivHead.setImageBitmap(bitmap);*/
        UtilTool.getImage(mMdb, send, context, ((RedViewHolder) holder).ivHead);
        ((RedViewHolder) holder).tvTime.setText(messageInfoList.get(position).getTime());
        ((RedViewHolder) holder).tvBi.setText(messageInfoList.get(position).getCount() + messageInfoList.get(position).getCoin());
        ((RedViewHolder) holder).rlCache.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context, ConversationActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                intent.putExtra("MessageInfo", (Serializable) messageInfoList.get(position));
                context.startActivity(intent);
                ((Activity)context).finish();
            }
        });

    }

    @Override
    public int getItemCount() {
        return messageInfoList.size();
    }

    /**
     * 交易
     */
    public class RedViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.rl_cache)
        RelativeLayout rlCache;
        @Bind(R.id.iv_head)
        ImageView ivHead;
        @Bind(R.id.tv_name)
        TextView tvName;
        @Bind(R.id.tv_time)
        TextView tvTime;
        @Bind(R.id.iv_type)
        ImageView ivType;
        @Bind(R.id.tv_state)
        TextView tvState;
        @Bind(R.id.tv_bi)
        TextView tvBi;

        public RedViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    /**
     * 文本
     */
    public class TextViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.rl_cache)
        RelativeLayout rlCache;
        @Bind(R.id.iv_head)
        ImageView ivHead;
        @Bind(R.id.tv_name)
        TextView tvName;
        @Bind(R.id.tv_content)
        TextView tvContent;
        @Bind(R.id.tv_time)
        TextView tvTime;
        public TextViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    /**
     * 文本輸入
     */
    public class TextInputViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.rl_serach)
        RelativeLayout rlSerach;
        @Bind(R.id.tv_content)
        TextView tvContent;
        public TextInputViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
