package com.bclould.tea.ui.adapter;


import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bclould.tea.R;
import com.bclould.tea.model.MessageInfo;
import com.bclould.tea.ui.activity.GrabQRCodeRedActivity;
import com.bclould.tea.ui.activity.OrderCloseActivity;
import com.bclould.tea.ui.activity.OrderDetailsActivity;
import com.bclould.tea.ui.activity.PayDetailsActivity;
import com.bclould.tea.ui.activity.RealNameC1Activity;
import com.bclould.tea.ui.activity.RedPacketActivity;
import com.bclould.tea.utils.ChatTimeUtil;
import com.bclould.tea.utils.StringUtils;
import com.bclould.tea.utils.UtilTool;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by GA on 2018/3/9.
 */

public class ChatServerAdapter extends RecyclerView.Adapter {
    public static final int ADMINISTRATOR_OTC_ORDER_MSG = 14;//管理員otc訂單消息
    public static final int ADMINISTRATOR_RED_PACKET_EXPIRED_MSG = 15;//管理員紅包過期消息
    public static final int ADMINISTRATOR_AUTH_STATUS_MSG = 16;//管理員實名認證消息
    public static final int ADMINISTRATOR_RECEIPT_PAY_MSG = 17;//管理員收付款消息
    public static final int ADMINISTRATOR_TRANSFER_MSG = 18;//管理員轉賬消息
    public static final int ADMINISTRATOR_IN_OUT_COIN_MSG = 19;//管理員提幣消息
    public static final int ADMINISTRATOR_IN_COIN_MSG = 29;//管理員充幣消息
    public static final int ADMINISTRATOR_EXCEPTIONAL_MSG = 30;//打賞
    public static final int ADMINISTRATOR_SERVICE_TEXT_MSG = 31;//文本消息

    private final Context mContext;
    private final List<MessageInfo> mMessageList;

    public ChatServerAdapter(Context context, List<MessageInfo> messageList) {
        mContext = context;
        mMessageList = messageList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        RecyclerView.ViewHolder holder = null;
        if (viewType == ADMINISTRATOR_OTC_ORDER_MSG) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_administrator_chat_otc_order, parent, false);
            holder = new OtcOrderStatusHolder(view);
        } else if (viewType == ADMINISTRATOR_RED_PACKET_EXPIRED_MSG) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_administrator_chat_red_expired, parent, false);
            holder = new RedExpiredHolder(view);
        } else if (viewType == ADMINISTRATOR_AUTH_STATUS_MSG) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_administrator_chat_auth_status, parent, false);
            holder = new AuthStatusHolder(view);
        } else if (viewType == ADMINISTRATOR_RECEIPT_PAY_MSG) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_administrator_chat_receipt_pay, parent, false);
            holder = new ReceiptPayHolder(view);
        } else if (viewType == ADMINISTRATOR_TRANSFER_MSG) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_administrator_chat_transfer, parent, false);
            holder = new TransferInformHolder(view);
        } else if (viewType == ADMINISTRATOR_IN_OUT_COIN_MSG) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_administrator_chat_inout_coin, parent, false);
            holder = new InoutCoinInformHolder(view);
        } else if (viewType == ADMINISTRATOR_IN_COIN_MSG) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_administrator_chat_in_coin, parent, false);
            holder = new InCoinInformHolder(view);
        } else if (viewType == ADMINISTRATOR_EXCEPTIONAL_MSG) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_administrator_chat_exceptional, parent, false);
            holder = new ExceptionalHolder(view);
        } else if (viewType == ADMINISTRATOR_SERVICE_TEXT_MSG) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_administrator_chat_service_text, parent, false);
            holder = new ServiceTextHolder(view);
        } else {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_chat_text, parent, false);
            holder = new TextChatHolder(view);
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        UtilTool.Log("肉质", "刷新走了onBindViewHolder");
        int itemViewType = getItemViewType(position);
        switch (itemViewType) {
            case ADMINISTRATOR_OTC_ORDER_MSG:
                OtcOrderStatusHolder orderStatusHolder = (OtcOrderStatusHolder) holder;
                orderStatusHolder.setData(mMessageList.get(position));
                break;
            case ADMINISTRATOR_RED_PACKET_EXPIRED_MSG:
                RedExpiredHolder redExpiredHolder = (RedExpiredHolder) holder;
                redExpiredHolder.setData(mMessageList.get(position));
                break;
            case ADMINISTRATOR_AUTH_STATUS_MSG:
                AuthStatusHolder authStatusHolder = (AuthStatusHolder) holder;
                authStatusHolder.setData(mMessageList.get(position));
                break;
            case ADMINISTRATOR_RECEIPT_PAY_MSG:
                ReceiptPayHolder receiptPayHolder = (ReceiptPayHolder) holder;
                receiptPayHolder.setData(mMessageList.get(position));
                break;
            case ADMINISTRATOR_TRANSFER_MSG:
                TransferInformHolder transferInformHolder = (TransferInformHolder) holder;
                transferInformHolder.setData(mMessageList.get(position));
                break;
            case ADMINISTRATOR_IN_OUT_COIN_MSG:
                InoutCoinInformHolder inoutCoinInformHolder = (InoutCoinInformHolder) holder;
                inoutCoinInformHolder.setData(mMessageList.get(position));
                break;
            case ADMINISTRATOR_IN_COIN_MSG:
                InCoinInformHolder inCoinInformHolder = (InCoinInformHolder) holder;
                inCoinInformHolder.setData(mMessageList.get(position));
                break;
            case ADMINISTRATOR_EXCEPTIONAL_MSG:
                ExceptionalHolder exceptionalHolder = (ExceptionalHolder) holder;
                exceptionalHolder.setData(mMessageList.get(position));
                break;
            case ADMINISTRATOR_SERVICE_TEXT_MSG:
                ServiceTextHolder serviceTextHolder = (ServiceTextHolder) holder;
                serviceTextHolder.setData(mMessageList.get(position));
                break;
            default:
                TextChatHolder textChatHolder = (TextChatHolder) holder;
                textChatHolder.setData(mMessageList.get(position));
                break;
        }
    }

    @Override
    public int getItemCount() {
        if (mMessageList.size() != 0) {
            return mMessageList.size();
        }
        return 0;
    }

    private void setCreatetime(View view, String currentShowTime) {
        TextView createtime = (TextView) view.findViewById(R.id.chat_createtime);
        if (StringUtils.isEmpty(currentShowTime)) {
            createtime.setVisibility(View.GONE);
        } else {
            createtime.setText(ChatTimeUtil.createChatShowTime(currentShowTime));
            createtime.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return mMessageList.get(position).getMsgType();
    }

    class OtcOrderStatusHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.tv_type_msg)
        TextView mTvTypeMsg;
        @Bind(R.id.tv_coin)
        TextView mTvCoin;
        @Bind(R.id.tv_number_hint)
        TextView mTvNumberHint;
        @Bind(R.id.tv_order_number)
        TextView mTvOrderNumber;
        @Bind(R.id.tv_status_hint)
        TextView mTvStatusHint;
        @Bind(R.id.tv_status)
        TextView mTvStatus;
        @Bind(R.id.tv_time_hint)
        TextView mTvTimeHint;
        @Bind(R.id.tv_time)
        TextView mTvTime;
        @Bind(R.id.ll_order_msg)
        LinearLayout mLlOrderMsg;
        @Bind(R.id.chat_createtime)
        View tvCreateTime;

        OtcOrderStatusHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void setData(final MessageInfo messageInfo) {
            setCreatetime(tvCreateTime, messageInfo.getShowChatTime());
            mTvCoin.setText(messageInfo.getCoin());
            mTvOrderNumber.setText(messageInfo.getCount());
            mTvTime.setText(messageInfo.getTime());
            if (messageInfo.getStatus() == 0) {
                mTvStatus.setText(mContext.getString(R.string.canceled_canc));
                mTvTypeMsg.setText(mContext.getString(R.string.order_cancel_hint));
            } else if (messageInfo.getStatus() == 4) {
                mTvStatus.setText(mContext.getString(R.string.order_timeout));
                mTvTypeMsg.setText(mContext.getString(R.string.order_timeout_hint));
            } else if (messageInfo.getStatus() == 3) {
                mTvStatus.setText(mContext.getString(R.string.finish));
                mTvTypeMsg.setText(mContext.getString(R.string.order_finish_hint));
            } else if (messageInfo.getStatus() == 2) {
                if (messageInfo.getType() == 1) {
                    mTvStatus.setText(mContext.getString(R.string.pay_succeed_dengdai_fb));
                } else {
                    mTvStatus.setText(mContext.getString(R.string.relative_pay_dengdai_fb));
                }
            } else if (messageInfo.getStatus() == 1) {
                mTvStatus.setText(mContext.getString(R.string.pending));
            }
            mLlOrderMsg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (messageInfo.getStatus() == 1 || messageInfo.getStatus() == 2) {
                        Intent intent = new Intent(mContext, OrderDetailsActivity.class);
                        intent.putExtra("type", mContext.getString(R.string.order));
                        intent.putExtra("id", messageInfo.getRedId() + "");
                        mContext.startActivity(intent);
                    } else {
                        Intent intent = new Intent(mContext, OrderCloseActivity.class);
                        intent.putExtra("status", messageInfo.getStatus());
                        intent.putExtra("id", messageInfo.getRedId() + "");
                        mContext.startActivity(intent);
                    }
                }
            });
        }
    }

    class RedExpiredHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.tv_type_msg)
        TextView mTvTypeMsg;
        @Bind(R.id.tv_coin)
        TextView mTvCoin;
        @Bind(R.id.tv_red_type_hint)
        TextView mTvRedTypeHint;
        @Bind(R.id.tv_red_type)
        TextView mTvRedType;
        @Bind(R.id.tv_status_hint)
        TextView mTvStatusHint;
        @Bind(R.id.tv_count)
        TextView mTvCount;
        @Bind(R.id.tv_time_hint)
        TextView mTvTimeHint;
        @Bind(R.id.tv_time)
        TextView mTvTime;
        @Bind(R.id.ll_red_expried_msg)
        LinearLayout mLlRedExpriedMsg;
        @Bind(R.id.chat_createtime)
        View tvCreateTime;

        RedExpiredHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void setData(final MessageInfo messageInfo) {
            setCreatetime(tvCreateTime, messageInfo.getShowChatTime());
            mTvCoin.setText(messageInfo.getCoin());
            mTvCount.setText(messageInfo.getCount());
            mTvTime.setText(messageInfo.getTime());
            if (messageInfo.getStatus() == 1) {
                mTvRedType.setText(mContext.getString(R.string.ordinary_red_packet));
            } else if (messageInfo.getStatus() == 2) {
                mTvRedType.setText(mContext.getString(R.string.qr_code_red_package));
            }
            mLlRedExpriedMsg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (messageInfo.getStatus() == 1) {
                        Intent intent = new Intent(mContext, RedPacketActivity.class);
                        intent.putExtra("type", true);
                        intent.putExtra("from", false);
                        intent.putExtra("id", messageInfo.getRedId() + "");
                        mContext.startActivity(intent);
                    } else {
                        Intent intent = new Intent(mContext, GrabQRCodeRedActivity.class);
                        intent.putExtra("id", messageInfo.getRedId() + "");
                        mContext.startActivity(intent);
                    }
                }
            });
        }
    }

    class AuthStatusHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.tv_status_hint)
        TextView mTvStatusHint;
        @Bind(R.id.tv_status)
        TextView mTvStatus;
        @Bind(R.id.tv_time_hint)
        TextView mTvTimeHint;
        @Bind(R.id.tv_time)
        TextView mTvTime;
        @Bind(R.id.ll_auth_status_msg)
        LinearLayout mLlAuthStatusMsg;
        @Bind(R.id.chat_createtime)
        View tvCreateTime;

        AuthStatusHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void setData(MessageInfo messageInfo) {
            setCreatetime(tvCreateTime, messageInfo.getShowChatTime());
            mTvTime.setText(messageInfo.getTime());
            if (messageInfo.getStatus() == 3) {
                mTvStatus.setText(mContext.getString(R.string.auth_succeed));
            } else if (messageInfo.getStatus() == 4) {
                mTvStatus.setText(mContext.getString(R.string.auth_error_hint));
            }
            mLlAuthStatusMsg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mContext.startActivity(new Intent(mContext, RealNameC1Activity.class));
                }
            });
        }
    }

    class ReceiptPayHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.tv_type_msg)
        TextView mTvTypeMsg;
        @Bind(R.id.tv_coin)
        TextView mTvCoin;
        @Bind(R.id.tv_who_hint)
        TextView mTvWhoHint;
        @Bind(R.id.tv_who)
        TextView mTvWho;
        @Bind(R.id.tv_status_hint)
        TextView mTvStatusHint;
        @Bind(R.id.tv_count)
        TextView mTvCount;
        @Bind(R.id.tv_time_hint)
        TextView mTvTimeHint;
        @Bind(R.id.tv_time)
        TextView mTvTime;
        @Bind(R.id.ll_red_expried_msg)
        LinearLayout mLlRedExpriedMsg;
        @Bind(R.id.chat_createtime)
        View tvCreateTime;

        ReceiptPayHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void setData(final MessageInfo messageInfo) {
            setCreatetime(tvCreateTime, messageInfo.getShowChatTime());
            mTvCoin.setText(messageInfo.getCoin());
            mTvCount.setText(messageInfo.getCount());
            mTvWho.setText(messageInfo.getRemark());
            mTvTime.setText(messageInfo.getTime());
            if (messageInfo.getStatus() == 1) {
                mTvWhoHint.setText(mContext.getString(R.string.payer));
                mTvTypeMsg.setText(mContext.getString(R.string.receipt_inform));
                mTvStatusHint.setText(mContext.getString(R.string.receipt_count));
            } else {
                mTvWhoHint.setText(mContext.getString(R.string.payee));
                mTvTypeMsg.setText(mContext.getString(R.string.pay_inform));
                mTvStatusHint.setText(mContext.getString(R.string.pay_count));
            }
            mLlRedExpriedMsg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, PayDetailsActivity.class);
                    intent.putExtra("id", messageInfo.getRedId() + "");
                    intent.putExtra("log_id", messageInfo.getBetId());
                    intent.putExtra("type_number", messageInfo.getType() + "");
                    mContext.startActivity(intent);
                }
            });
        }
    }

    class TransferInformHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.tv_type_msg)
        TextView mTvTypeMsg;
        @Bind(R.id.tv_coin)
        TextView mTvCoin;
        @Bind(R.id.tv_who_hint)
        TextView mTvWhoHint;
        @Bind(R.id.tv_who)
        TextView mTvWho;
        @Bind(R.id.tv_status_hint)
        TextView mTvStatusHint;
        @Bind(R.id.tv_count)
        TextView mTvCount;
        @Bind(R.id.tv_time_hint)
        TextView mTvTimeHint;
        @Bind(R.id.tv_time)
        TextView mTvTime;
        @Bind(R.id.ll_red_expried_msg)
        LinearLayout mLlRedExpriedMsg;
        @Bind(R.id.chat_createtime)
        View tvCreateTime;

        TransferInformHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void setData(final MessageInfo messageInfo) {
            setCreatetime(tvCreateTime, messageInfo.getShowChatTime());
            mTvCoin.setText(messageInfo.getCoin());
            mTvCount.setText(messageInfo.getCount());
            mTvWho.setText(messageInfo.getRemark());
            mTvTime.setText(messageInfo.getTime());
            if (messageInfo.getStatus() == 1) {
                mTvWhoHint.setText(mContext.getString(R.string.payer));
                mTvTypeMsg.setText(mContext.getString(R.string.in_account_inform));
                mTvStatusHint.setText(mContext.getString(R.string.in_account_count));
            } else {
                mTvWhoHint.setText(mContext.getString(R.string.payee));
                mTvTypeMsg.setText(mContext.getString(R.string.transfer_inform));
                mTvStatusHint.setText(mContext.getString(R.string.transfer_count));
            }
            mLlRedExpriedMsg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, PayDetailsActivity.class);
                    intent.putExtra("id", messageInfo.getRedId() + "");
                    intent.putExtra("log_id", messageInfo.getBetId());
                    intent.putExtra("type_number", messageInfo.getType() + "");
                    mContext.startActivity(intent);
                }
            });
        }
    }

    class InoutCoinInformHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.tv_type_msg)
        TextView mTvTypeMsg;
        @Bind(R.id.tv_coin)
        TextView mTvCoin;
        @Bind(R.id.tv_status_hint)
        TextView mTvStatusHint;
        @Bind(R.id.tv_count)
        TextView mTvCount;
        @Bind(R.id.tv_time_hint)
        TextView mTvTimeHint;
        @Bind(R.id.tv_time)
        TextView mTvTime;
        @Bind(R.id.ll_red_expried_msg)
        LinearLayout mLlRedExpriedMsg;
        @Bind(R.id.chat_createtime)
        View tvCreateTime;

        InoutCoinInformHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void setData(final MessageInfo messageInfo) {
            setCreatetime(tvCreateTime, messageInfo.getShowChatTime());
            mTvCoin.setText(messageInfo.getCoin());
            mTvCount.setText(messageInfo.getCount());
            mTvTime.setText(messageInfo.getTime());
            mLlRedExpriedMsg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, PayDetailsActivity.class);
                    intent.putExtra("id", messageInfo.getRedId() + "");
                    intent.putExtra("log_id", messageInfo.getBetId());
                    intent.putExtra("type_number", messageInfo.getType() + "");
                    mContext.startActivity(intent);
                }
            });
        }
    }

    class InCoinInformHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.tv_type_msg)
        TextView mTvTypeMsg;
        @Bind(R.id.tv_coin)
        TextView mTvCoin;
        @Bind(R.id.tv_status_hint)
        TextView mTvStatusHint;
        @Bind(R.id.tv_count)
        TextView mTvCount;
        @Bind(R.id.tv_time_hint)
        TextView mTvTimeHint;
        @Bind(R.id.tv_time)
        TextView mTvTime;
        @Bind(R.id.ll_red_expried_msg)
        LinearLayout mLlRedExpriedMsg;
        @Bind(R.id.chat_createtime)
        View tvCreateTime;

        InCoinInformHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void setData(final MessageInfo messageInfo) {
            setCreatetime(tvCreateTime, messageInfo.getShowChatTime());
            mTvCoin.setText(messageInfo.getCoin());
            mTvCount.setText(messageInfo.getCount());
            mTvTime.setText(messageInfo.getTime());
            mLlRedExpriedMsg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, PayDetailsActivity.class);
                    intent.putExtra("id", messageInfo.getRedId() + "");
                    intent.putExtra("log_id", messageInfo.getBetId());
                    intent.putExtra("type_number", messageInfo.getType() + "");
                    mContext.startActivity(intent);
                }
            });
        }
    }

    //打賞
    class ExceptionalHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.tv_type_msg)
        TextView mTvTypeMsg;
        @Bind(R.id.tv_coin)
        TextView mTvCoin;
        @Bind(R.id.tv_count)
        TextView mTvCount;
        @Bind(R.id.tv_time)
        TextView mTvTime;
        @Bind(R.id.ll_red_expried_msg)
        LinearLayout mLlRedExpriedMsg;
        @Bind(R.id.chat_createtime)
        View tvCreateTime;

        ExceptionalHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void setData(final MessageInfo messageInfo) {
            setCreatetime(tvCreateTime, messageInfo.getShowChatTime());
            mTvCoin.setText(messageInfo.getCoin());
            mTvCount.setText(messageInfo.getCount());
            mTvTime.setText(messageInfo.getTime());
            if (messageInfo.getType() == 13) {
                mTvTypeMsg.setText(mContext.getString(R.string.exceptional_spending));
            } else {
                mTvTypeMsg.setText(mContext.getString(R.string.exceptional_income));
            }
            mLlRedExpriedMsg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, PayDetailsActivity.class);
                    intent.putExtra("id", messageInfo.getRedId() + "");
                    intent.putExtra("log_id", messageInfo.getBetId());
                    intent.putExtra("type_number", messageInfo.getType() + "");
                    mContext.startActivity(intent);
                }
            });
        }
    }

    //系统文本消息
    class ServiceTextHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.tv_title)
        TextView mTvTitle;
        @Bind(R.id.tv_content)
        TextView mTvContent;
        @Bind(R.id.ll_red_expried_msg)
        LinearLayout mLlRedExpriedMsg;
        @Bind(R.id.chat_createtime)
        View tvCreateTime;

        ServiceTextHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void setData(final MessageInfo messageInfo) {
            setCreatetime(tvCreateTime, messageInfo.getShowChatTime());
            mTvTitle.setText(messageInfo.getTitle());
            mTvContent.setText(messageInfo.getContent());

            mLlRedExpriedMsg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
        }
    }

    class TextChatHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.chat_createtime)
        View tvCreateTime;

        public TextChatHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void setData(MessageInfo messageInfo) {
            setCreatetime(tvCreateTime, messageInfo.getShowChatTime());

        }
    }
}
