package com.bclould.tocotalk.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bclould.tocotalk.Presenter.GrabRedPresenter;
import com.bclould.tocotalk.R;
import com.bclould.tocotalk.crypto.otr.OtrChatListenerManager;
import com.bclould.tocotalk.history.DBManager;
import com.bclould.tocotalk.model.GrabRedInfo;
import com.bclould.tocotalk.model.MessageInfo;
import com.bclould.tocotalk.model.SerMap;
import com.bclould.tocotalk.model.VoiceInfo;
import com.bclould.tocotalk.ui.activity.GrabQRCodeRedActivity;
import com.bclould.tocotalk.ui.activity.ImageViewActivity;
import com.bclould.tocotalk.ui.activity.IndividualDetailsActivity;
import com.bclould.tocotalk.ui.activity.OrderCloseActivity;
import com.bclould.tocotalk.ui.activity.OrderDetailsActivity;
import com.bclould.tocotalk.ui.activity.PayDetailsActivity;
import com.bclould.tocotalk.ui.activity.RealNameC1Activity;
import com.bclould.tocotalk.ui.activity.RedPacketActivity;
import com.bclould.tocotalk.ui.activity.TransferDetailsActivity;
import com.bclould.tocotalk.ui.activity.VideoActivity;
import com.bclould.tocotalk.ui.widget.CurrencyDialog;
import com.bclould.tocotalk.utils.Constants;
import com.bclould.tocotalk.utils.MessageEvent;
import com.bclould.tocotalk.utils.UtilTool;
import com.bclould.tocotalk.xmpp.XmppConnection;

import org.greenrobot.eventbus.EventBus;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.util.stringencoder.Base64;
import org.jxmpp.jid.impl.JidCreate;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.github.rockerhieu.emojicon.EmojiconTextView;

import static com.bclould.tocotalk.utils.UtilTool.Log;

/**
 * Created by GA on 2018/3/9.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class ChatAdapter extends RecyclerView.Adapter {

    public static final int FROM_TEXT_MSG = 0;//接收文本消息类型
    public static final int TO_TEXT_MSG = 1;//发送文本消息类型
    public static final int FROM_IMG_MSG = 2;//接收图片消息类型
    public static final int TO_IMG_MSG = 3;//发送图片消息类型
    public static final int FROM_VOICE_MSG = 4;//接收语音消息类型
    public static final int TO_VOICE_MSG = 5;//发送语音消息类型
    public static final int FROM_VIDEO_MSG = 6;//接受视频消息类型
    public static final int TO_VIDEO_MSG = 7;//发送视频消息类型
    public static final int FROM_RED_MSG = 8;//接受红包消息类型
    public static final int TO_RED_MSG = 9;//发送红包消息类型
    public static final int FROM_FILE_MSG = 10;//接受文件消息类型
    public static final int TO_FILE_MSG = 11;//发送文件消息类型
    public static final int FROM_TRANSFER_MSG = 12;//接受红包消息类型
    public static final int TO_TRANSFER_MSG = 13;//发送红包消息类型
    public static final int ADMINISTRATOR_OTC_ORDER_MSG = 14;//管理員otc訂單消息
    public static final int ADMINISTRATOR_RED_PACKET_EXPIRED_MSG = 15;//管理員紅包過期消息
    public static final int ADMINISTRATOR_AUTH_STATUS_MSG = 16;//管理員實名認證消息
    public static final int ADMINISTRATOR_RECEIPT_PAY_MSG = 17;//管理員收付款消息
    public static final int ADMINISTRATOR_TRANSFER_MSG = 18;//管理員轉賬消息
    public static final int ADMINISTRATOR_IN_OUT_COIN_MSG = 19;//管理員充提幣消息
    private final Context mContext;
    private final List<MessageInfo> mMessageList;
    private final Bitmap mFromBitmap;
    private final String mUser;
    private final DBManager mMgr;
    private final GrabRedPresenter mGrabRedPresenter;
    private final Bitmap mToBitmap;
    private final MediaPlayer mMediaPlayer;
    ArrayList<String> mImageList = new ArrayList<>();
    private CurrencyDialog mCurrencyDialog;
    private String mFileName;
    private AnimationDrawable mAnim;
    private String mName;
    private String mToName;

    public ChatAdapter(Context context, List<MessageInfo> messageList, Bitmap fromBitmap, String user, DBManager mgr, MediaPlayer mediaPlayer, String name) {
        mContext = context;
        mMessageList = messageList;
        mFromBitmap = fromBitmap;
        mUser = user;
        mName = name;
        mMgr = mgr;
        mMediaPlayer = mediaPlayer;
        mGrabRedPresenter = new GrabRedPresenter(mContext);
        mToBitmap = UtilTool.getImage(mMgr, UtilTool.getJid(), mContext);
        mToName = UtilTool.getJid().substring(0, UtilTool.getJid().lastIndexOf("@"));
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        RecyclerView.ViewHolder holder = null;
        if (viewType == TO_TEXT_MSG) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_to_chat_text, parent, false);
            holder = new ToTextHolder(view);
        } else if (viewType == FROM_TEXT_MSG) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_from_chat_text, parent, false);
            holder = new FromTextHolder(view);
        } else if (viewType == TO_RED_MSG) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_to_chat_red, parent, false);
            holder = new ToRedHolder(view);
        } else if (viewType == FROM_RED_MSG) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_from_chat_red, parent, false);
            holder = new FromRedHolder(view);
        } else if (viewType == TO_VOICE_MSG) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_to_chat_voice, parent, false);
            holder = new ToVoiceHolder(view);
        } else if (viewType == FROM_VOICE_MSG) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_from_chat_voice, parent, false);
            holder = new FromVoiceHolder(view);
        } else if (viewType == TO_IMG_MSG) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_to_chat_img, parent, false);
            holder = new ToImgHolder(view);
        } else if (viewType == FROM_IMG_MSG) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_from_chat_img, parent, false);
            holder = new FromImgHolder(view);
        } else if (viewType == TO_VIDEO_MSG) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_to_chat_video, parent, false);
            holder = new ToVideoHolder(view);
        } else if (viewType == FROM_VIDEO_MSG) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_from_chat_video, parent, false);
            holder = new FromVideoHolder(view);
        } /*else if (viewType == TO_FILE_MSG) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_to_chat_file, parent, false);
            holder = new FromVideoHolder(view);
        } else if (viewType == FROM_FILE_MSG) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_from_chat_file, parent, false);
            holder = new FromVideoHolder(view);
        } */ else if (viewType == TO_TRANSFER_MSG) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_to_chat_transfer, parent, false);
            holder = new ToTransferHolder(view);
        } else if (viewType == FROM_TRANSFER_MSG) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_from_chat_transfer, parent, false);
            holder = new FromTransferHolder(view);
        } else if (viewType == ADMINISTRATOR_OTC_ORDER_MSG) {
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
        }
        return holder;
    }

    HashMap<String, String> mImageMap = new HashMap<>();
    ArrayList<Integer> mIntegerList = new ArrayList<>();

    public void upDateImage() {
        mImageList.clear();
        mImageMap.clear();
        mIntegerList.clear();
        for (final MessageInfo info : mMessageList) {
            if (info.getMsgType() == TO_IMG_MSG) {
                mImageList.add(info.getMessage());
                mImageMap.put(info.getMessage(), "");
                mIntegerList.add(info.getId());
            } else if (info.getMsgType() == FROM_IMG_MSG) {
                if (info.getImageType() != 0) {
                    mImageList.add(info.getMessage());
                    mImageMap.put(info.getMessage(), "");
                    mIntegerList.add(info.getId());
                } else {
                    mImageList.add(info.getVoice());
                    mImageMap.put(info.getVoice(), info.getMessage());
                    mIntegerList.add(info.getId());
                }
            }
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        UtilTool.Log("肉质", "刷新走了onBindViewHolder");
        upDateImage();
        int itemViewType = getItemViewType(position);
        switch (itemViewType) {
            case TO_TEXT_MSG:
                ToTextHolder toTextHolder = (ToTextHolder) holder;
                toTextHolder.setData(mMessageList.get(position));
                break;
            case FROM_TEXT_MSG:
                FromTextHolder fromTextHolder = (FromTextHolder) holder;
                fromTextHolder.setData(mMessageList.get(position));
                break;
            case TO_RED_MSG:
                ToRedHolder toRedHolder = (ToRedHolder) holder;
                toRedHolder.setData(mMessageList.get(position));
                break;
            case FROM_RED_MSG:
                FromRedHolder fromRedHolder = (FromRedHolder) holder;
                fromRedHolder.setData(mMessageList.get(position));
                break;
            case TO_VOICE_MSG:
                ToVoiceHolder toVoiceHolder = (ToVoiceHolder) holder;
                toVoiceHolder.setData(mMessageList.get(position));
                break;
            case FROM_VOICE_MSG:
                FromVoiceHolder fromVoiceHolder = (FromVoiceHolder) holder;
                fromVoiceHolder.setData(mMessageList.get(position));
                break;
            case TO_IMG_MSG:
                ToImgHolder toImgHolder = (ToImgHolder) holder;
                toImgHolder.setData(mMessageList.get(position));
                break;
            case FROM_IMG_MSG:
                FromImgHolder fromImgHolder = (FromImgHolder) holder;
                fromImgHolder.setData(mMessageList.get(position));
                break;
            case TO_VIDEO_MSG:
                ToVideoHolder toVideoHolder = (ToVideoHolder) holder;
                toVideoHolder.setData(mMessageList.get(position));
                break;
            case FROM_VIDEO_MSG:
                FromVideoHolder fromVideoHolder = (FromVideoHolder) holder;
                fromVideoHolder.setData(mMessageList.get(position));
                break;
            case TO_TRANSFER_MSG:
                ToTransferHolder toTransferHolder = (ToTransferHolder) holder;
                toTransferHolder.setData(mMessageList.get(position));
                break;
            case FROM_TRANSFER_MSG:
                FromTransferHolder fromTransferHolder = (FromTransferHolder) holder;
                fromTransferHolder.setData(mMessageList.get(position));
                break;
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
        }
    }

    @Override
    public int getItemCount() {
        if (mMessageList.size() != 0) {
            return mMessageList.size();
        }
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        return mMessageList.get(position).getMsgType();
    }

    class ToTextHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.iv_touxiang)
        ImageView mIvTouxiang;
        @Bind(R.id.iv_warning)
        ImageView mIvWarning;
        @Bind(R.id.tv_messamge)
        EmojiconTextView mTvMessamge;

        ToTextHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void setData(final MessageInfo messageInfo) {
            mIvTouxiang.setImageBitmap(mToBitmap);
            goIndividualDetails(mIvTouxiang, Constants.MYUSER, mToName);
            mTvMessamge.setText(messageInfo.getMessage());
            if (messageInfo.getSendStatus() == 1) {
                mIvWarning.setVisibility(View.VISIBLE);
            } else {
                mIvWarning.setVisibility(View.GONE);
            }
            mIvWarning.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    anewSendText(mUser, messageInfo.getMessage(), mIvWarning, messageInfo.getId(), messageInfo);
                }
            });
        }
    }

    private void anewSendText(String user, String message, ImageView ivWarning, int id, MessageInfo messageInfo) {
        try {
            ChatManager manager = ChatManager.getInstanceFor(XmppConnection.getInstance().getConnection());
            Chat chat = manager.createChat(JidCreate.entityBareFrom(user), null);
            chat.sendMessage(OtrChatListenerManager.getInstance().sentMessagesChange(message,
                    OtrChatListenerManager.getInstance().sessionID(Constants.MYUSER, String.valueOf(JidCreate.entityBareFrom(mUser)))));
            ivWarning.setVisibility(View.GONE);
            messageInfo.setSendStatus(0);
            mMgr.updateMessageHint(id, 0);
        } catch (Exception e) {
            e.printStackTrace();
            ivWarning.setVisibility(View.VISIBLE);
            Toast.makeText(mContext, mContext.getString(R.string.send_error), Toast.LENGTH_SHORT).show();
        }
    }

    class FromTextHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.iv_touxiang)
        ImageView mIvTouxiang;
        @Bind(R.id.tv_messamge)
        EmojiconTextView mTvMessamge;

        FromTextHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void setData(MessageInfo messageInfo) {
            mIvTouxiang.setImageBitmap(mFromBitmap);
            goIndividualDetails(mIvTouxiang, mUser, mName);
            mTvMessamge.setText(messageInfo.getMessage());
        }
    }

    class ToRedHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.iv_touxiang)
        ImageView mIvTouxiang;
        @Bind(R.id.tv_remark)
        TextView mTvRemark;
        @Bind(R.id.tv_examine)
        TextView mTvExamine;
        @Bind(R.id.iv_redPacket)
        ImageView mIvRedPacket;
        @Bind(R.id.tv_coin_redpacket)
        TextView mTvCoinRedpacket;
        @Bind(R.id.cv_redpacket)
        CardView mCvRedpacket;

        ToRedHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void setData(final MessageInfo messageInfo) {
            mIvTouxiang.setImageBitmap(mToBitmap);
            goIndividualDetails(mIvTouxiang, Constants.MYUSER, mToName);
            mTvCoinRedpacket.setText(messageInfo.getCoin() + mContext.getString(R.string.red_package));
            mTvRemark.setText(messageInfo.getRemark());
            mCvRedpacket.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mGrabRedPresenter.grabRedPacket(messageInfo.getRedId(), new GrabRedPresenter.CallBack() {
                        @Override
                        public void send(GrabRedInfo info) {
                            mMgr.updateMessageState(messageInfo.getId() + "", 1);
                            messageInfo.setStatus(1);
                            notifyDataSetChanged();
                            skip(info, mToBitmap, UtilTool.getJid(), 0);
                        }
                    });
                }
            });
            if (messageInfo.getStatus() == 1) {
                mCvRedpacket.setCardBackgroundColor(mContext.getResources().getColor(R.color.transfer));
            } else {
                mCvRedpacket.setCardBackgroundColor(mContext.getResources().getColor(R.color.redpacket2));
            }
        }
    }

    private void skip(GrabRedInfo baseInfo, Bitmap bitmap, String user, int who) {
        Intent intent = new Intent(mContext, RedPacketActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("user", user);
        bundle.putSerializable("grabRedInfo", baseInfo);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] bytes = baos.toByteArray();
        bundle.putByteArray("image", bytes);
        intent.putExtras(bundle);
        intent.putExtra("from", true);
        intent.putExtra("type", true);
        intent.putExtra("who", who);
        mContext.startActivity(intent);
    }

    class FromRedHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.iv_touxiang)
        ImageView mIvTouxiang;
        @Bind(R.id.tv_remark)
        TextView mTvRemark;
        @Bind(R.id.tv_examine)
        TextView mTvExamine;
        @Bind(R.id.iv_redPacket)
        ImageView mIvRedPacket;
        @Bind(R.id.tv_coin_redpacket)
        TextView mTvCoinRedpacket;
        @Bind(R.id.cv_redpacket)
        CardView mCvRedpacket;

        FromRedHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void setData(final MessageInfo messageInfo) {
            mIvTouxiang.setImageBitmap(mFromBitmap);
            goIndividualDetails(mIvTouxiang, mUser, mName);
            mTvCoinRedpacket.setText(messageInfo.getCoin() + mContext.getString(R.string.red_package));
            mTvRemark.setText(messageInfo.getRemark());
            if (messageInfo.getStatus() == 1) {
                mCvRedpacket.setCardBackgroundColor(mContext.getResources().getColor(R.color.transfer));
                mTvExamine.setText(mContext.getString(R.string.took_red_packet));
                mCvRedpacket.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mGrabRedPresenter.grabRedPacket(messageInfo.getRedId(), new GrabRedPresenter.CallBack() {
                            @Override
                            public void send(GrabRedInfo info) {
                                mMgr.updateMessageState(messageInfo.getId() + "", 1);
                                messageInfo.setStatus(1);
                                notifyDataSetChanged();
                                skip(info, mFromBitmap, mUser, 1);
                            }
                        });
                    }
                });
            } else {
                mCvRedpacket.setCardBackgroundColor(mContext.getResources().getColor(R.color.redpacket2));
                mTvExamine.setText(mContext.getString(R.string.look_red_packet));
                mCvRedpacket.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showDialog(messageInfo);
                    }
                });
            }
        }
    }

    //显示币种弹框
    private void showDialog(final MessageInfo messageInfo) {
        mCurrencyDialog = new CurrencyDialog(R.layout.dialog_redpacket, mContext, R.style.dialog);
        Window window = mCurrencyDialog.getWindow();
        window.setWindowAnimations(R.style.CustomDialog);
        mCurrencyDialog.show();
        mCurrencyDialog.setCanceledOnTouchOutside(false);
        ImageView touxiang = (ImageView) mCurrencyDialog.findViewById(R.id.iv_touxiang);
        TextView from = (TextView) mCurrencyDialog.findViewById(R.id.tv_from);
        TextView name = (TextView) mCurrencyDialog.findViewById(R.id.tv_name);
        TextView tvRemark = (TextView) mCurrencyDialog.findViewById(R.id.tv_remark);
        ImageView bark = (ImageView) mCurrencyDialog.findViewById(R.id.iv_bark);
        Button open = (Button) mCurrencyDialog.findViewById(R.id.btn_open);
        from.setText(mContext.getString(R.string.red_package_hint) + messageInfo.getCoin() + mContext.getString(R.string.red_package));
        name.setText(mUser.substring(0, mUser.indexOf("@")));
        tvRemark.setText(messageInfo.getRemark());
        touxiang.setImageBitmap(mFromBitmap);
        bark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCurrencyDialog.dismiss();
            }
        });
        open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mGrabRedPresenter.grabRedPacket(messageInfo.getRedId(), new GrabRedPresenter.CallBack() {
                    @Override
                    public void send(GrabRedInfo info) {
                        mMgr.updateMessageState(messageInfo.getId() + "", 1);
                        messageInfo.setStatus(1);
                        notifyDataSetChanged();
                        skip(info, mFromBitmap, mUser, 1);
                    }
                });
                mCurrencyDialog.dismiss();
            }
        });
    }

    class ToVoiceHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.iv_touxiang)
        ImageView mIvTouxiang;
        @Bind(R.id.tv_voice_time)
        TextView mTvVoiceTime;
        @Bind(R.id.iv_warning)
        ImageView mIvWarning;
        @Bind(R.id.iv_voice)
        TextView mIvVoice;
        @Bind(R.id.iv_anim)
        ImageView mIvAnim;
        @Bind(R.id.rl_voice)
        RelativeLayout mRlVoice;

        ToVoiceHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void setData(final MessageInfo messageInfo) {
            mIvTouxiang.setImageBitmap(mToBitmap);
            goIndividualDetails(mIvTouxiang, Constants.MYUSER, mToName);
            mTvVoiceTime.setText(messageInfo.getVoiceTime() + "''");
            int wide = Integer.parseInt(messageInfo.getVoiceTime()) * 2;
            String blank = " ";
            for (int i = 0; i < wide; i++) {
                blank += " ";
            }
            mIvVoice.setText(blank);
            if (messageInfo.getSendStatus() == 1) {
                mIvWarning.setVisibility(View.VISIBLE);
            } else {
                mIvWarning.setVisibility(View.GONE);
            }
            mIvWarning.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    anewSendVoice(messageInfo, mIvWarning);
                }
            });
            final AnimationDrawable anim = (AnimationDrawable) mIvAnim.getBackground();
            mRlVoice.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    playVoice(mMediaPlayer, messageInfo.getVoice(), anim);
                }
            });
        }
    }

    private void anewSendVoice(MessageInfo messageInfo, ImageView ivWarning) {
        try {
            ChatManager manager = ChatManager.getInstanceFor(XmppConnection.getInstance().getConnection());
            Chat chat = manager.createChat(JidCreate.entityBareFrom(mUser), null);
            Message message = new Message();
            byte[] bytes = UtilTool.readStream(messageInfo.getVoice());
            String base64 = Base64.encodeToString(bytes);
            VoiceInfo voiceInfo = new VoiceInfo();
            voiceInfo.setElementText(base64);
            int duration = UtilTool.getFileDuration(messageInfo.getVoice(), mContext);
            message.setBody(OtrChatListenerManager.getInstance().sentMessagesChange("[audio]:" + duration + mContext.getString(R.string.second),
                    OtrChatListenerManager.getInstance().sessionID(Constants.MYUSER, String.valueOf(JidCreate.entityBareFrom(mUser)))));
            message.addExtension(voiceInfo);
            chat.sendMessage(message);
            ivWarning.setVisibility(View.GONE);
            messageInfo.setSendStatus(0);
            mMgr.updateMessageHint(messageInfo.getId(), 0);
            EventBus.getDefault().post(new MessageEvent(mContext.getString(R.string.oneself_send_msg)));
        } catch (Exception e) {
            e.printStackTrace();
            ivWarning.setVisibility(View.VISIBLE);
            Toast.makeText(mContext, mContext.getString(R.string.send_error), Toast.LENGTH_SHORT).show();
        }
    }

    public void playVoice(MediaPlayer mediaPlayer, String fileName, final AnimationDrawable anim) {
        try {
            //对mediaPlayer进行实例化
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    mediaPlayer.reset();
                    anim.selectDrawable(0);
                    anim.stop();
                }
            });
            if (mediaPlayer.isPlaying()) {
                if (mFileName.equals(fileName)) {
                    mediaPlayer.stop();
                    mediaPlayer.reset();
                    anim.selectDrawable(0);
                    anim.stop();
                } else {
                    mediaPlayer.stop();
                    mediaPlayer.reset();
                    mAnim.selectDrawable(0);
                    mAnim.stop();
                    mediaPlayer.setDataSource(fileName);     //设置资源目录
                    mediaPlayer.prepare();//缓冲
                    mediaPlayer.start();
                    anim.start();
                }
            } else {
                mediaPlayer.reset();
                mediaPlayer.setDataSource(fileName);     //设置资源目录
                mediaPlayer.prepare();//缓冲
                mediaPlayer.start();//开始或恢复播放
                anim.start();
            }
            mFileName = fileName;
            mAnim = anim;
        } catch (IOException e) {
            Log("日志", "没有找到这个文件");
            e.printStackTrace();
        }
    }

    class FromVoiceHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.iv_touxiang)
        ImageView mIvTouxiang;
        @Bind(R.id.tv_voice_time)
        TextView mTvVoiceTime;
        @Bind(R.id.iv_status)
        ImageView mIvStatus;
        @Bind(R.id.iv_voice)
        TextView mIvVoice;
        @Bind(R.id.iv_anim)
        ImageView mIvAnim;
        @Bind(R.id.rl_voice)
        RelativeLayout mRlVoice;

        FromVoiceHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void setData(final MessageInfo messageInfo) {
            mIvTouxiang.setImageBitmap(mFromBitmap);
            goIndividualDetails(mIvTouxiang, mUser, mName);
            mTvVoiceTime.setText(messageInfo.getVoiceTime() + "''");
            int wide = Integer.parseInt(messageInfo.getVoiceTime()) * 2;
            String blank = " ";
            for (int i = 0; i < wide; i++) {
                blank += " ";
            }
            if (messageInfo.getVoiceStatus() == 1) {
                mIvStatus.setVisibility(View.GONE);
            } else {
                mIvStatus.setVisibility(View.VISIBLE);
            }
            mIvVoice.setText(blank);
            final AnimationDrawable anim = (AnimationDrawable) mIvAnim.getBackground();
            mRlVoice.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    playVoice(mMediaPlayer, messageInfo.getVoice(), anim);
                    mMgr.updateMessageStatus(messageInfo.getId());
                    mIvStatus.setVisibility(View.GONE);
                    messageInfo.setVoiceStatus(1);
                }
            });
        }
    }

    class ToImgHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.iv_touxiang)
        ImageView mIvTouxiang;
        @Bind(R.id.iv_img)
        ImageView mIvImg;
        @Bind(R.id.iv_warning)
        ImageView mIvWarning;
        @Bind(R.id.iv_load)
        ImageView mIvLoad;

        ToImgHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void setData(final MessageInfo messageInfo) {
            mIvTouxiang.setImageBitmap(mToBitmap);
            goIndividualDetails(mIvTouxiang, Constants.MYUSER, mToName);
            Bitmap bitmap = BitmapFactory.decodeFile(messageInfo.getVoice());
            mIvImg.setImageBitmap(bitmap);
            if (messageInfo.getSendStatus() == 1) {
                mIvWarning.setVisibility(View.GONE);
                mIvLoad.setVisibility(View.GONE);
            } else if (messageInfo.getSendStatus() == 2) {
                mIvWarning.setVisibility(View.VISIBLE);
                mIvLoad.setVisibility(View.GONE);
            } else {
                mIvLoad.setVisibility(View.VISIBLE);
                mIvWarning.setVisibility(View.GONE);
            }
            mIvWarning.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    anewSendImg();
                }
            });
            AnimationDrawable animationDrawable = (AnimationDrawable) mIvLoad.getBackground();
            animationDrawable.start();
            mIvImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, ImageViewActivity.class);
                    intent.putStringArrayListExtra("images", mImageList);
                    intent.putIntegerArrayListExtra("msgId", mIntegerList);
                    SerMap serMap = new SerMap();
                    serMap.setMap(mImageMap);
                    intent.putExtra("imgMap", serMap);
                    int position = 0;
                    for (int i = 0; i < mImageList.size(); i++) {
                        if (messageInfo.getMessage().equals(mImageList.get(i))) {
                            position = i;
                        }
                    }
                    intent.putExtra("clickedIndex", position);
                    mContext.startActivity(intent);
                }
            });
        }
    }

    class FromImgHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.iv_touxiang)
        ImageView mIvTouxiang;
        @Bind(R.id.iv_img)
        ImageView mIvImg;

        FromImgHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void setData(final MessageInfo messageInfo) {
            mIvTouxiang.setImageBitmap(mFromBitmap);
            goIndividualDetails(mIvTouxiang, mUser, mName);
            Bitmap bitmap = BitmapFactory.decodeFile(messageInfo.getVoice());
            mIvImg.setImageBitmap(bitmap);
            mIvImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, ImageViewActivity.class);
                    intent.putStringArrayListExtra("images", mImageList);
                    intent.putIntegerArrayListExtra("msgId", mIntegerList);
                    SerMap serMap = new SerMap();
                    serMap.setMap(mImageMap);
                    intent.putExtra("imgMap", serMap);
                    int position = 0;
                    for (int i = 0; i < mImageList.size(); i++) {
                        if (messageInfo.getImageType() == 0) {
                            if (messageInfo.getVoice().equals(mImageList.get(i))) {
                                position = i;
                            }
                        } else {
                            if (messageInfo.getMessage().equals(mImageList.get(i))) {
                                position = i;
                            }
                        }
                    }
                    intent.putExtra("clickedIndex", position);
                    mContext.startActivity(intent);
                }
            });
        }
    }

    class ToVideoHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.iv_touxiang)
        ImageView mIvTouxiang;
        @Bind(R.id.iv_video)
        ImageView mIvVideo;
        @Bind(R.id.iv_video_play)
        ImageView mIvVideoPlay;
        @Bind(R.id.rl_video)
        RelativeLayout mRlVideo;
        @Bind(R.id.iv_warning)
        ImageView mIvWarning;
        @Bind(R.id.iv_load)
        ImageView mIvLoad;

        ToVideoHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void setData(final MessageInfo messageInfo) {
            mIvTouxiang.setImageBitmap(mToBitmap);
            goIndividualDetails(mIvTouxiang, Constants.MYUSER, mToName);
            mIvVideo.setImageBitmap(BitmapFactory.decodeFile(messageInfo.getVoice()));
            if (messageInfo.getSendStatus() == 0) {
                mIvLoad.setVisibility(View.VISIBLE);
                mIvWarning.setVisibility(View.GONE);
            } else if (messageInfo.getSendStatus() == 2) {
                mIvWarning.setVisibility(View.VISIBLE);
                mIvLoad.setVisibility(View.GONE);
            } else {
                mIvWarning.setVisibility(View.GONE);
                mIvLoad.setVisibility(View.GONE);
            }
            AnimationDrawable animationDrawable = (AnimationDrawable) mIvLoad.getBackground();
            animationDrawable.start();
            mRlVideo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, VideoActivity.class);
                    intent.putExtra("url", messageInfo.getMessage());
                    mContext.startActivity(intent);
                }
            });
        }
    }

    class FromVideoHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.iv_touxiang)
        ImageView mIvTouxiang;
        @Bind(R.id.iv_video)
        ImageView mIvVideo;
        @Bind(R.id.rl_video)
        RelativeLayout mRlVideo;
        @Bind(R.id.iv_video_play)
        ImageView mIvVideoPlay;

        FromVideoHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void setData(final MessageInfo messageInfo) {
            mIvTouxiang.setImageBitmap(mFromBitmap);
            goIndividualDetails(mIvTouxiang, mUser, mName);
            mIvVideo.setImageBitmap(BitmapFactory.decodeFile(messageInfo.getVoice()));
            mRlVideo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, VideoActivity.class);
                    intent.putExtra("url", messageInfo.getMessage());
                    mContext.startActivity(intent);
                }
            });
        }
    }

    class ToTransferHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.iv_touxiang)
        ImageView mIvTouxiang;
        @Bind(R.id.tv_remark)
        TextView mTvRemark;
        @Bind(R.id.tv_coin_count)
        TextView mTvCoinCount;
        @Bind(R.id.iv_transfer)
        ImageView mIvTransfer;
        @Bind(R.id.cv_redpacket)
        CardView mCvRedpacket;

        ToTransferHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void setData(final MessageInfo messageInfo) {
            mIvTouxiang.setImageBitmap(mToBitmap);
            goIndividualDetails(mIvTouxiang, Constants.MYUSER, mToName);
            mTvRemark.setText(messageInfo.getRemark());
            mTvCoinCount.setText(messageInfo.getCount() + messageInfo.getCoin());
            if (messageInfo.getStatus() == 0) {
                mCvRedpacket.setCardBackgroundColor(mContext.getResources().getColor(R.color.redpacket));
                mTvRemark.setText(messageInfo.getRemark());
            } else {
                mCvRedpacket.setCardBackgroundColor(mContext.getResources().getColor(R.color.redpacket3));
            }
            mCvRedpacket.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mMgr.updateMessageState(messageInfo.getId() + "", 1);
                    messageInfo.setStatus(1);
                    notifyDataSetChanged();
                    Intent intent = new Intent(mContext, TransferDetailsActivity.class);
                    intent.putExtra("count", messageInfo.getCount());
                    intent.putExtra("coin", messageInfo.getCoin());
                    intent.putExtra("time", messageInfo.getTime());
                    intent.putExtra("type", 1);
                    mContext.startActivity(intent);
                }
            });
        }
    }

    class FromTransferHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.iv_touxiang)
        ImageView mIvTouxiang;
        @Bind(R.id.tv_remark)
        TextView mTvRemark;
        @Bind(R.id.tv_coin_count)
        TextView mTvCoinCount;
        @Bind(R.id.iv_transfer)
        ImageView mIvTransfer;
        @Bind(R.id.cv_redpacket)
        CardView mCvRedpacket;

        FromTransferHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void setData(final MessageInfo messageInfo) {
            mIvTouxiang.setImageBitmap(mFromBitmap);
            goIndividualDetails(mIvTouxiang, mUser, mName);
            mTvRemark.setText(messageInfo.getRemark());
            mTvCoinCount.setText(messageInfo.getCount() + messageInfo.getCoin());
            if (messageInfo.getStatus() == 0) {
                mCvRedpacket.setCardBackgroundColor(mContext.getResources().getColor(R.color.redpacket));
                mTvRemark.setText(messageInfo.getRemark());
            } else {
                mCvRedpacket.setCardBackgroundColor(mContext.getResources().getColor(R.color.redpacket3));
                mTvRemark.setText(mContext.getString(R.string.transfer_took));
            }
            mCvRedpacket.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mMgr.updateMessageState(messageInfo.getId() + "", 1);
                    messageInfo.setStatus(1);
                    notifyDataSetChanged();
                    Intent intent = new Intent(mContext, TransferDetailsActivity.class);
                    intent.putExtra("count", messageInfo.getCount());
                    intent.putExtra("coin", messageInfo.getCoin());
                    intent.putExtra("time", messageInfo.getTime());
                    intent.putExtra("type", 0);
                    mContext.startActivity(intent);
                }
            });
        }
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

        OtcOrderStatusHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void setData(final MessageInfo messageInfo) {
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

        RedExpiredHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void setData(final MessageInfo messageInfo) {
            mTvCoin.setText(messageInfo.getCoin());
            mTvCount.setText(messageInfo.getCount());
            mTvTime.setText(messageInfo.getTime());
            if (messageInfo.getStatus() == 1) {
                mTvRedType.setText(mContext.getString(R.string.ordinary_red_packet));
            } else if (messageInfo.getStatus() == 2) {
                mTvRedType.setText(mContext.getString(R.string.qr_code_red_packet));
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

        AuthStatusHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void setData(MessageInfo messageInfo) {
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

        ReceiptPayHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void setData(final MessageInfo messageInfo) {
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

        TransferInformHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void setData(final MessageInfo messageInfo) {
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

        InoutCoinInformHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void setData(final MessageInfo messageInfo) {
            mTvCoin.setText(messageInfo.getCoin());
            mTvCount.setText(messageInfo.getCount());
            mTvTime.setText(messageInfo.getTime());
            mLlRedExpriedMsg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, PayDetailsActivity.class);
                    intent.putExtra("id", messageInfo.getRedId() + "");
                    intent.putExtra("type_number", messageInfo.getType() + "");
                    mContext.startActivity(intent);
                }
            });
        }
    }

    private void goIndividualDetails(View view, final String user, final String name) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, IndividualDetailsActivity.class);
                intent.putExtra("user", user);
                intent.putExtra("name", name);
                mContext.startActivity(intent);
            }
        });
    }
}
