package com.bclould.tea.ui.adapter;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bclould.tea.Presenter.GrabRedPresenter;
import com.bclould.tea.R;
import com.bclould.tea.history.DBManager;
import com.bclould.tea.history.DBRoomMember;
import com.bclould.tea.model.GrabRedInfo;
import com.bclould.tea.model.MessageInfo;
import com.bclould.tea.model.SerMap;
import com.bclould.tea.ui.activity.ChatLookLocationActivity;
import com.bclould.tea.ui.activity.GrabQRCodeRedActivity;
import com.bclould.tea.ui.activity.GuessDetailsActivity;
import com.bclould.tea.ui.activity.ImageViewActivity;
import com.bclould.tea.ui.activity.IndividualDetailsActivity;
import com.bclould.tea.ui.activity.NewsDetailsActivity;
import com.bclould.tea.ui.activity.OrderCloseActivity;
import com.bclould.tea.ui.activity.OrderDetailsActivity;
import com.bclould.tea.ui.activity.PayDetailsActivity;
import com.bclould.tea.ui.activity.RealNameC1Activity;
import com.bclould.tea.ui.activity.RedPacketActivity;
import com.bclould.tea.ui.activity.SelectFriendActivity;
import com.bclould.tea.ui.activity.TransferDetailsActivity;
import com.bclould.tea.ui.activity.VideoActivity;
import com.bclould.tea.ui.widget.CurrencyDialog;
import com.bclould.tea.ui.widget.DeleteCacheDialog;
import com.bclould.tea.ui.widget.MenuListPopWindow;
import com.bclould.tea.utils.AnimatorTool;
import com.bclould.tea.utils.ChatTimeUtil;
import com.bclould.tea.utils.Constants;
import com.bclould.tea.utils.CustomLinkMovementMethod;
import com.bclould.tea.utils.HyperLinkUtil;
import com.bclould.tea.utils.MessageEvent;
import com.bclould.tea.utils.StringUtils;
import com.bclould.tea.utils.ToastShow;
import com.bclould.tea.utils.UtilTool;
import com.bclould.tea.xmpp.RoomManage;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.github.rockerhieu.emojicon.EmojiconTextView;

import static com.bclould.tea.utils.UtilTool.Log;

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
    public static final int FROM_TRANSFER_MSG = 12;//接受转账消息类型
    public static final int TO_TRANSFER_MSG = 13;//发送转账消息类型
    public static final int FROM_LOCATION_MSG = 20;//接受定位
    public static final int TO_LOCATION_MSG = 21;//發送定位
    public static final int FROM_CARD_MSG = 22;//接受名片
    public static final int TO_CARD_MSG = 23;//發送名片
    public static final int FROM_LINK_MSG = 24;//接受新聞分享
    public static final int TO_LINK_MSG = 25;//發送新聞分享
    public static final int FROM_GUESS_MSG = 26;//接受竞猜分享
    public static final int TO_GUESS_MSG = 27;//發送竞猜分享
    public static final int RED_GET_MSG = 28;//紅包被領取

    public static final int ADMINISTRATOR_OTC_ORDER_MSG = 14;//管理員otc訂單消息
    public static final int ADMINISTRATOR_RED_PACKET_EXPIRED_MSG = 15;//管理員紅包過期消息
    public static final int ADMINISTRATOR_AUTH_STATUS_MSG = 16;//管理員實名認證消息
    public static final int ADMINISTRATOR_RECEIPT_PAY_MSG = 17;//管理員收付款消息
    public static final int ADMINISTRATOR_TRANSFER_MSG = 18;//管理員轉賬消息
    public static final int ADMINISTRATOR_IN_OUT_COIN_MSG = 19;//管理員提幣消息
    public static final int ADMINISTRATOR_IN_COIN_MSG = 29;//管理員充幣消息

    private final Context mContext;
    private final List<MessageInfo> mMessageList;
//    private final Bitmap mFromBitmap;

    private final DBManager mMgr;
    private final GrabRedPresenter mGrabRedPresenter;
    //    private final Bitmap mToBitmap;
    private final MediaPlayer mMediaPlayer;
    ArrayList<String> mImageList = new ArrayList<>();
    private CurrencyDialog mCurrencyDialog;
    private String mFileName;
    private AnimationDrawable mAnim;
    private final String mRoomId;
    private String mRoomType;
    private String mName;
    private RelativeLayout mrlTitle;
    private DBRoomMember mDBRoomMember;

    public ChatAdapter(Context context, List<MessageInfo> messageList, String roomId, DBManager mgr, MediaPlayer mediaPlayer, String name, String roomType, RelativeLayout rlTitle, DBRoomMember mDBRoomMember) {
        mContext = context;
        mMessageList = messageList;
        mRoomId = roomId;
        mRoomType = roomType;
        mMgr = mgr;
        mMediaPlayer = mediaPlayer;
        mGrabRedPresenter = new GrabRedPresenter(mContext);
        //繼續保留這兩個字段是用於之前版本有的消息沒有send字段
        mName = name;
        mrlTitle = rlTitle;
        this.mDBRoomMember = mDBRoomMember;
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
        } else if (viewType == FROM_CARD_MSG) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_from_chat_card, parent, false);
            holder = new FromCardHolder(view);
        } else if (viewType == TO_CARD_MSG) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_to_chat_card, parent, false);
            holder = new ToCardHolder(view);
        } else if (viewType == FROM_LINK_MSG) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_from_chat_link, parent, false);
            holder = new FromLinkHolder(view);
        } else if (viewType == TO_LINK_MSG) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_to_chat_link, parent, false);
            holder = new ToLinkHolder(view);
        } else if (viewType == FROM_GUESS_MSG) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_from_chat_guess, parent, false);
            holder = new FromGuessHolder(view);
        } else if (viewType == TO_GUESS_MSG) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_to_chat_guess, parent, false);
            holder = new ToGuessHolder(view);
        } else if (viewType == FROM_LOCATION_MSG) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_from_chat_location, parent, false);
            holder = new FromLocationHolder(view);
        } else if (viewType == TO_LOCATION_MSG) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_to_chat_location, parent, false);
            holder = new ToLocationHolder(view);
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
        } else if (viewType == RED_GET_MSG) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_chat_red_get, parent, false);
            holder = new ReadGetHolder(view);
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
        } else if (viewType == ADMINISTRATOR_IN_COIN_MSG) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_administrator_chat_in_coin, parent, false);
            holder = new InCoinInformHolder(view);
        } else {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_chat_text, parent, false);
            holder = new TextChatHolder(view);
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
            case TO_LOCATION_MSG:
                ToLocationHolder toLocationHolder = (ToLocationHolder) holder;
                toLocationHolder.setData(mMessageList.get(position));
                break;
            case FROM_LOCATION_MSG:
                FromLocationHolder fromLocationHolder = (FromLocationHolder) holder;
                fromLocationHolder.setData(mMessageList.get(position));
                break;
            case TO_TRANSFER_MSG:
                ToTransferHolder toTransferHolder = (ToTransferHolder) holder;
                toTransferHolder.setData(mMessageList.get(position));
                break;
            case FROM_TRANSFER_MSG:
                FromTransferHolder fromTransferHolder = (FromTransferHolder) holder;
                fromTransferHolder.setData(mMessageList.get(position));
                break;
            case TO_CARD_MSG:
                ToCardHolder toCardHolder = (ToCardHolder) holder;
                toCardHolder.setData(mMessageList.get(position));
                break;
            case FROM_CARD_MSG:
                FromCardHolder fromCardHolder = (FromCardHolder) holder;
                fromCardHolder.setData(mMessageList.get(position));
                break;
            case TO_LINK_MSG:
                ToLinkHolder toLinkHolder = (ToLinkHolder) holder;
                toLinkHolder.setData(mMessageList.get(position));
                break;
            case FROM_LINK_MSG:
                FromLinkHolder fromLinkHolder = (FromLinkHolder) holder;
                fromLinkHolder.setData(mMessageList.get(position));
                break;
            case TO_GUESS_MSG:
                ToGuessHolder toGuessHolder = (ToGuessHolder) holder;
                toGuessHolder.setData(mMessageList.get(position));
                break;
            case FROM_GUESS_MSG:
                FromGuessHolder fromGuessHolder = (FromGuessHolder) holder;
                fromGuessHolder.setData(mMessageList.get(position));
                break;
            case RED_GET_MSG:
                ReadGetHolder readGetHolder = (ReadGetHolder) holder;
                readGetHolder.setData(mMessageList.get(position));
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
            case ADMINISTRATOR_IN_COIN_MSG:
                InCoinInformHolder inCoinInformHolder = (InCoinInformHolder) holder;
                inCoinInformHolder.setData(mMessageList.get(position));
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

    private void showCopyDialog(final int msgtype, final MessageInfo messageInfo, final boolean isCopy, boolean isTransmit) {
        List<String> list = new ArrayList<>();
        list.add(mContext.getString(R.string.delete));
        if (isCopy)
            list.add(mContext.getString(R.string.copy));
        if (isTransmit)
            list.add(mContext.getString(R.string.transmit));

        final MenuListPopWindow menu = new MenuListPopWindow(mContext, list);
        menu.setListOnClick(new MenuListPopWindow.ListOnClick() {
            @Override
            public void onclickitem(int position) {
                switch (position) {
                    case 0:
                        menu.dismiss();
                        break;
                    case 1:
                        menu.dismiss();
                        MessageInfo messageInfoNext = mMgr.deleteSingleMessage(mRoomId, messageInfo.getId() + "");
                        String conversation = mMgr.findLastMessageConversation(mRoomId);
                        if (!StringUtils.isEmpty(conversation)) {
                            mMgr.updateConversationMessage(mRoomId, conversation);
                        }
                        if (messageInfoNext != null) {
                            mMessageList.get(mMessageList.indexOf(messageInfo) + 1).setShowChatTime(messageInfoNext.getShowChatTime());
                        }
                        mMessageList.remove(messageInfo);
                        notifyDataSetChanged();
                        EventBus.getDefault().post(new MessageEvent(mContext.getString(R.string.dispose_unread_msg)));
                        break;
                    case 2:
                        menu.dismiss();
                        if (isCopy) {
                            //获取剪贴板管理器：
                            ClipboardManager cm = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                            // 创建普通字符型ClipData
                            ClipData mClipData = ClipData.newPlainText("Label", messageInfo.getMessage());
                            // 将ClipData内容放到系统剪贴板里。
                            cm.setPrimaryClip(mClipData);
                            ToastShow.showToast2((Activity) mContext, mContext.getString(R.string.copy_succeed));
                        } else {
                            Intent intent = new Intent(mContext, SelectFriendActivity.class);
                            intent.putExtra("type", 1);
                            intent.putExtra("msgType", msgtype);
                            intent.putExtra("messageInfo", messageInfo);
                            mContext.startActivity(intent);
                        }
                        break;
                    case 3:
                        menu.dismiss();
                        Intent intent = new Intent(mContext, SelectFriendActivity.class);
                        intent.putExtra("type", 1);
                        intent.putExtra("msgType", msgtype);
                        intent.putExtra("messageInfo", messageInfo);
                        mContext.startActivity(intent);
                        break;
                }
            }
        });
        menu.setColor(Color.BLACK);
        menu.showAtLocation();
    }

    private void setMsgState(int sendStatus, ImageView mIvWarning, ImageView mIvLoad) {
        if (sendStatus == 1) {
            mIvWarning.setVisibility(View.GONE);
            mIvLoad.setVisibility(View.GONE);
        } else if (sendStatus == 2) {
            mIvWarning.setVisibility(View.VISIBLE);
            mIvLoad.setVisibility(View.GONE);
        } else {
            mIvLoad.setVisibility(View.VISIBLE);
            mIvWarning.setVisibility(View.GONE);
        }
        AnimationDrawable animationDrawable = (AnimationDrawable) mIvLoad.getBackground();
        animationDrawable.start();
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

    class ToTextHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.iv_touxiang)
        ImageView mIvTouxiang;
        @Bind(R.id.iv_warning)
        ImageView mIvWarning;
        @Bind(R.id.iv_load)
        ImageView mIvLoad;
        @Bind(R.id.tv_messamge)
        EmojiconTextView mTvMessamge;
        @Bind(R.id.chat_createtime)
        View tvCreateTime;

        ToTextHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void setData(final MessageInfo messageInfo) {
//            mIvTouxiang.setImageBitmap(mToBitmap);
            setCreatetime(tvCreateTime, messageInfo.getShowChatTime());
            UtilTool.getImage(mMgr, UtilTool.getTocoId(), mContext, mIvTouxiang);
            goIndividualDetails(mIvTouxiang, UtilTool.getTocoId(), UtilTool.getUser(), messageInfo);
            HyperLinkUtil hyperLinkUtil = new HyperLinkUtil();
            mTvMessamge.setText(hyperLinkUtil.getHyperClickableSpan(mContext, new SpannableStringBuilder(messageInfo.getMessage()), false, messageInfo.getId(), mMgr, new HyperLinkUtil.OnChangeLinkListener() {
                @Override
                public void changeLink(String message, String oldMessage) {
                    if (!messageInfo.getMessage().equals(oldMessage)) return;
                    messageInfo.setMessage(message);
                    ((Activity) mContext).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            notifyDataSetChanged();
                        }
                    });
                }
            }));
            mTvMessamge.setMovementMethod(new CustomLinkMovementMethod());
            setMsgState(messageInfo.getSendStatus(), mIvWarning, mIvLoad);
            mIvWarning.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mMessageList.remove(messageInfo);
                    RoomManage.getInstance().getRoom(mRoomId).anewSendText(messageInfo.getMessage(), messageInfo.getId());
                }
            });
            mTvMessamge.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    showCopyDialog(messageInfo.getMsgType(), messageInfo, true, true);
                    return false;
                }
            });
        }
    }

    class FromTextHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.iv_touxiang)
        ImageView mIvTouxiang;
        @Bind(R.id.tv_messamge)
        EmojiconTextView mTvMessamge;
        @Bind(R.id.chat_createtime)
        View tvCreateTime;

        FromTextHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void setData(final MessageInfo messageInfo) {
//            mIvTouxiang.setImageBitmap(mFromBitmap);
            setCreatetime(tvCreateTime, messageInfo.getShowChatTime());
            if (RoomManage.ROOM_TYPE_MULTI.equals(mRoomType)) {
                UtilTool.getImage(mContext, mIvTouxiang, mDBRoomMember.findMemberUrl(mRoomId, messageInfo.getSend()));
            } else {
                UtilTool.getImage(mMgr, messageInfo.getSend(), mContext, mIvTouxiang);
            }
            goIndividualDetails(mIvTouxiang, mRoomId, mName, messageInfo);
            HyperLinkUtil hyperLinkUtil = new HyperLinkUtil();
            mTvMessamge.setText(hyperLinkUtil.getHyperClickableSpan(mContext, new SpannableStringBuilder(messageInfo.getMessage()), true, messageInfo.getId(), mMgr, new HyperLinkUtil.OnChangeLinkListener() {
                @Override
                public void changeLink(String message, String oldMessage) {
                    if (!messageInfo.getMessage().equals(oldMessage)) return;
                    messageInfo.setMessage(message);
                    ((Activity) mContext).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            notifyDataSetChanged();
                        }
                    });
                }
            }));
            mTvMessamge.setMovementMethod(new CustomLinkMovementMethod());
            mTvMessamge.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    showCopyDialog(messageInfo.getMsgType(), messageInfo, true, true);
                    return false;
                }
            });
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
        @Bind(R.id.chat_createtime)
        View tvCreateTime;

        ToRedHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void setData(final MessageInfo messageInfo) {
//            mIvTouxiang.setImageBitmap(mToBitmap);
            setCreatetime(tvCreateTime, messageInfo.getShowChatTime());
            UtilTool.getImage(mMgr, UtilTool.getTocoId(), mContext, mIvTouxiang);
            final String mName = mMgr.findUserName(UtilTool.getTocoId());
            goIndividualDetails(mIvTouxiang, UtilTool.getTocoId(), UtilTool.getUser(), messageInfo);
            mTvCoinRedpacket.setText(messageInfo.getCoin() + mContext.getString(R.string.red_package));
            mTvRemark.setText(messageInfo.getRemark());
            mCvRedpacket.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mGrabRedPresenter.grabRedPacket(messageInfo.getRedId(), new GrabRedPresenter.CallBack() {
                        @Override
                        public void send(GrabRedInfo info) {
                            if (info.getStatus() == 4) {
                                Toast.makeText(mContext, info.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                            mMgr.updateMessageState(messageInfo.getId() + "", 1);
                            messageInfo.setStatus(1);
                            notifyDataSetChanged();
//                            skip(info, mToBitmap, UtilTool.getTocoId(), 0);
                            skip(info, UtilTool.getTocoId(), 0, mName);
                        }
                    });
                }
            });
            mCvRedpacket.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    showCopyDialog(messageInfo.getMsgType(), messageInfo, false, false);
                    return false;
                }
            });
            if (messageInfo.getStatus() == 1) {
                mCvRedpacket.setCardBackgroundColor(mContext.getResources().getColor(R.color.transfer));
            } else {
                mCvRedpacket.setCardBackgroundColor(mContext.getResources().getColor(R.color.redpacket2));
            }
        }
    }

    private void skip(GrabRedInfo baseInfo, String user, int who, String name) {
        Intent intent = new Intent(mContext, RedPacketActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("user", user);
        bundle.putString("name", name);
        bundle.putSerializable("grabRedInfo", baseInfo);
        /*ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] bytes = baos.toByteArray();
        bundle.putByteArray("image", bytes);*/
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
        @Bind(R.id.chat_createtime)
        View tvCreateTime;

        FromRedHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void setData(final MessageInfo messageInfo) {
            String mUser = messageInfo.getSend();
            if (StringUtils.isEmpty(mUser))
                mUser = mRoomId;
//            mIvTouxiang.setImageBitmap(mFromBitmap);
            setCreatetime(tvCreateTime, messageInfo.getShowChatTime());
            final String mName = mMgr.findUserName(mUser);
            if (RoomManage.ROOM_TYPE_MULTI.equals(mRoomType)) {
                UtilTool.getImage(mContext, mIvTouxiang, mDBRoomMember.findMemberUrl(mRoomId, messageInfo.getSend()));
            } else {
                UtilTool.getImage(mMgr, mUser, mContext, mIvTouxiang);
            }
            goIndividualDetails(mIvTouxiang, mUser, mName, messageInfo);
            mTvCoinRedpacket.setText(messageInfo.getCoin() + mContext.getString(R.string.red_package));
            mTvRemark.setText(messageInfo.getRemark());
            if (messageInfo.getStatus() == 1) {
                mCvRedpacket.setCardBackgroundColor(mContext.getResources().getColor(R.color.transfer));
                mTvExamine.setText(mContext.getString(R.string.took_red_packet));
                final String finalMUser = mUser;
                mCvRedpacket.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mGrabRedPresenter.grabRedPacket(messageInfo.getRedId(), new GrabRedPresenter.CallBack() {
                            @Override
                            public void send(GrabRedInfo info) {
                                mMgr.updateMessageState(messageInfo.getId() + "", 1);
                                messageInfo.setStatus(1);
                                notifyDataSetChanged();
                                if (info.getStatus() == 4) {
                                    Toast.makeText(mContext, info.getMessage(), Toast.LENGTH_SHORT).show();
                                } else {
//                                    skip(info, mFromBitmap, mUser, 1);
                                    skip(info, finalMUser, 1, mName);
                                }
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
            mCvRedpacket.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    showCopyDialog(messageInfo.getMsgType(), messageInfo, false, false);
                    return false;
                }
            });
        }
    }

    //显示币种弹框
    private void showDialog(final MessageInfo messageInfo) {
        //暫無群聊，所以沒有考慮群聊情況
        String mUser = messageInfo.getSend();
        if (StringUtils.isEmpty(mUser))
            mUser = mRoomId;
        final String mName = mMgr.findUserName(mUser);
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
        name.setText(mName);
        tvRemark.setText(messageInfo.getRemark());
//        touxiang.setImageBitmap(mFromBitmap);
        UtilTool.getImage(mMgr, mUser, mContext, touxiang);
        bark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCurrencyDialog.dismiss();
            }
        });
        final String finalMUser = mUser;
        open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mGrabRedPresenter.grabRedPacket(messageInfo.getRedId(), new GrabRedPresenter.CallBack() {
                    @Override
                    public void send(GrabRedInfo info) {
                        mMgr.updateMessageState(messageInfo.getId() + "", 1);
                        messageInfo.setStatus(1);
                        notifyDataSetChanged();
                        if (info.getStatus() == 4) {
                            Toast.makeText(mContext, info.getMessage(), Toast.LENGTH_SHORT).show();
                        } else {
//                            skip(info, mFromBitmap, mUser, 1);
                            skip(info, finalMUser, 1, mName);
                        }
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
        @Bind(R.id.iv_load)
        ImageView mIvLoad;
        @Bind(R.id.chat_createtime)
        View tvCreateTime;

        ToVoiceHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void setData(final MessageInfo messageInfo) {
//            mIvTouxiang.setImageBitmap(mToBitmap);
            setCreatetime(tvCreateTime, messageInfo.getShowChatTime());
            UtilTool.getImage(mMgr, UtilTool.getTocoId(), mContext, mIvTouxiang);
            goIndividualDetails(mIvTouxiang, UtilTool.getTocoId(), UtilTool.getUser(), messageInfo);
            mTvVoiceTime.setText(messageInfo.getVoiceTime() + "''");
            int wide = Integer.parseInt(messageInfo.getVoiceTime()) * 2;
            String blank = " ";
            for (int i = 0; i < wide; i++) {
                blank += " ";
            }
            mIvVoice.setText(blank);
            setMsgState(messageInfo.getSendStatus(), mIvWarning, mIvLoad);
            mIvWarning.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mMessageList.remove(messageInfo);
                    RoomManage.getInstance().getRoom(mRoomId).anewSendVoice(messageInfo);
                }
            });
            final AnimationDrawable anim = (AnimationDrawable) mIvAnim.getBackground();
            mRlVoice.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    playVoice(mMediaPlayer, messageInfo.getVoice(), anim);
                }
            });
            mRlVoice.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    showCopyDialog(messageInfo.getMsgType(), messageInfo, false, false);
                    return false;
                }
            });
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
        @Bind(R.id.chat_createtime)
        View tvCreateTime;

        FromVoiceHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void setData(final MessageInfo messageInfo) {
//            mIvTouxiang.setImageBitmap(mFromBitmap);
            setCreatetime(tvCreateTime, messageInfo.getShowChatTime());
            if (RoomManage.ROOM_TYPE_MULTI.equals(mRoomType)) {
                UtilTool.getImage(mContext, mIvTouxiang, mDBRoomMember.findMemberUrl(mRoomId, messageInfo.getSend()));
            } else {
                UtilTool.getImage(mMgr, messageInfo.getSend(), mContext, mIvTouxiang);
            }

            goIndividualDetails(mIvTouxiang, mRoomId, mName, messageInfo);
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
            mRlVoice.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    showCopyDialog(messageInfo.getMsgType(), messageInfo, false, false);
                    return false;
                }
            });
        }
    }

    RequestOptions requestOptions = new RequestOptions()
            .placeholder(R.mipmap.image_placeholder)
            .error(R.mipmap.image_placeholder)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .centerCrop();

    class ToImgHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.iv_touxiang)
        ImageView mIvTouxiang;
        @Bind(R.id.iv_img)
        ImageView mIvImg;
        @Bind(R.id.iv_warning)
        ImageView mIvWarning;
        @Bind(R.id.iv_load)
        ImageView mIvLoad;
        @Bind(R.id.chat_createtime)
        View tvCreateTime;

        ToImgHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void setData(final MessageInfo messageInfo) {
//            mIvTouxiang.setImageBitmap(mToBitmap);
            setCreatetime(tvCreateTime, messageInfo.getShowChatTime());
            UtilTool.getImage(mMgr, UtilTool.getTocoId(), mContext, mIvTouxiang);
            goIndividualDetails(mIvTouxiang, UtilTool.getTocoId(), UtilTool.getUser(), messageInfo);
            Glide.with(mContext).load(new File(messageInfo.getVoice())).listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    mIvImg.setImageDrawable(resource);//必須加上，不然動圖會出現不播放情況
                    return false;
                }
            }).apply(requestOptions).into(mIvImg);
            setMsgState(messageInfo.getSendStatus(), mIvWarning, mIvLoad);
            mIvWarning.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mMessageList.remove(messageInfo);
                    RoomManage.getInstance().getRoom(mRoomId).anewSendUpload(messageInfo);
                    notifyDataSetChanged();
                }
            });
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
            mIvImg.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    showCopyDialog(messageInfo.getMsgType(), messageInfo, false, true);
                    return false;
                }
            });
        }
    }

    class FromImgHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.iv_touxiang)
        ImageView mIvTouxiang;
        @Bind(R.id.iv_img)
        ImageView mIvImg;
        @Bind(R.id.chat_createtime)
        View tvCreateTime;

        FromImgHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void setData(final MessageInfo messageInfo) {
//            mIvTouxiang.setImageBitmap(mFromBitmap);
            setCreatetime(tvCreateTime, messageInfo.getShowChatTime());
            if (RoomManage.ROOM_TYPE_MULTI.equals(mRoomType)) {
                UtilTool.getImage(mContext, mIvTouxiang, mDBRoomMember.findMemberUrl(mRoomId, messageInfo.getSend()));
            } else {
                UtilTool.getImage(mMgr, messageInfo.getSend(), mContext, mIvTouxiang);
            }
            goIndividualDetails(mIvTouxiang, mRoomId, mName, messageInfo);
            Glide.with(mContext).load(new File(messageInfo.getVoice())).listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    mIvImg.setImageDrawable(resource);
                    return false;
                }
            }).apply(requestOptions).into(mIvImg);
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
            mIvImg.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    showCopyDialog(messageInfo.getMsgType(), messageInfo, false, true);
                    return false;
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
        @Bind(R.id.chat_createtime)
        View tvCreateTime;

        ToVideoHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void setData(final MessageInfo messageInfo) {
//            mIvTouxiang.setImageBitmap(mToBitmap);
            setCreatetime(tvCreateTime, messageInfo.getShowChatTime());
            UtilTool.getImage(mMgr, UtilTool.getTocoId(), mContext, mIvTouxiang);
            goIndividualDetails(mIvTouxiang, UtilTool.getTocoId(), UtilTool.getUser(), messageInfo);
            mIvVideo.setImageBitmap(BitmapFactory.decodeFile(messageInfo.getVoice()));
            setMsgState(messageInfo.getSendStatus(), mIvWarning, mIvLoad);
            mIvWarning.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mMessageList.remove(messageInfo);
                    RoomManage.getInstance().getRoom(mRoomId).anewSendUpload(messageInfo);
                    notifyDataSetChanged();
                }
            });
            mRlVideo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, VideoActivity.class);
                    intent.putExtra("url", messageInfo.getMessage());
                    mContext.startActivity(intent);
                }
            });
            mRlVideo.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    showCopyDialog(messageInfo.getMsgType(), messageInfo, false, true);
                    return false;
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
        @Bind(R.id.chat_createtime)
        View tvCreateTime;

        FromVideoHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void setData(final MessageInfo messageInfo) {
//            mIvTouxiang.setImageBitmap(mFromBitmap);
            setCreatetime(tvCreateTime, messageInfo.getShowChatTime());
            if (RoomManage.ROOM_TYPE_MULTI.equals(mRoomType)) {
                UtilTool.getImage(mContext, mIvTouxiang, mDBRoomMember.findMemberUrl(mRoomId, messageInfo.getSend()));
            } else {
                UtilTool.getImage(mMgr, messageInfo.getSend(), mContext, mIvTouxiang);
            }
            goIndividualDetails(mIvTouxiang, mRoomId, mName, messageInfo);
            mIvVideo.setImageBitmap(BitmapFactory.decodeFile(messageInfo.getVoice()));
            mRlVideo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, VideoActivity.class);
                    intent.putExtra("url", messageInfo.getMessage());
                    mContext.startActivity(intent);
                }
            });
            mRlVideo.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    showCopyDialog(messageInfo.getMsgType(), messageInfo, false, true);
                    return false;
                }
            });
        }
    }

    class ToLocationHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.iv_touxiang)
        ImageView mIvTouxiang;
        @Bind(R.id.tv_title)
        TextView tvTitle;
        @Bind(R.id.tv_address)
        TextView tvAddress;
        @Bind(R.id.iv_location)
        ImageView ivLocation;
        @Bind(R.id.rl_location)
        RelativeLayout rlLocation;
        @Bind(R.id.iv_warning)
        ImageView mIvWarning;
        @Bind(R.id.iv_load)
        ImageView mIvLoad;
        @Bind(R.id.chat_createtime)
        View tvCreateTime;

        ToLocationHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void setData(final MessageInfo messageInfo) {
            setCreatetime(tvCreateTime, messageInfo.getShowChatTime());
            UtilTool.getImage(mMgr, UtilTool.getTocoId(), mContext, mIvTouxiang);
            goIndividualDetails(mIvTouxiang, UtilTool.getTocoId(), UtilTool.getUser(), messageInfo);
            setMsgState(messageInfo.getSendStatus(), mIvWarning, mIvLoad);
            tvTitle.setText(messageInfo.getTitle());
            tvAddress.setText(messageInfo.getAddress());
            ivLocation.setImageBitmap(BitmapFactory.decodeFile(messageInfo.getVoice()));
            mIvWarning.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mMessageList.remove(messageInfo);
                    RoomManage.getInstance().getRoom(mRoomId).anewSendLocation(messageInfo);
                }
            });
            rlLocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, ChatLookLocationActivity.class);
                    intent.putExtra("lng", messageInfo.getLng());
                    intent.putExtra("lat", messageInfo.getLat());
                    intent.putExtra("title", messageInfo.getTitle());
                    intent.putExtra("address", messageInfo.getAddress());
                    mContext.startActivity(intent);
                }
            });
//
            rlLocation.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    showCopyDialog(messageInfo.getMsgType(), messageInfo, false, false);
                    return false;
                }
            });
        }
    }

    class FromLocationHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.iv_touxiang)
        ImageView mIvTouxiang;
        @Bind(R.id.tv_title)
        TextView tvTitle;
        @Bind(R.id.tv_address)
        TextView tvAddress;
        @Bind(R.id.iv_location)
        ImageView ivLocation;
        @Bind(R.id.rl_location)
        RelativeLayout rlLocation;
        @Bind(R.id.chat_createtime)
        View tvCreateTime;

        FromLocationHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void setData(final MessageInfo messageInfo) {
            setCreatetime(tvCreateTime, messageInfo.getShowChatTime());
            if (RoomManage.ROOM_TYPE_MULTI.equals(mRoomType)) {
                UtilTool.getImage(mContext, mIvTouxiang, mDBRoomMember.findMemberUrl(mRoomId, messageInfo.getSend()));
            } else {
                UtilTool.getImage(mMgr, messageInfo.getSend(), mContext, mIvTouxiang);
            }
            goIndividualDetails(mIvTouxiang, mRoomId, mName, messageInfo);
            tvTitle.setText(messageInfo.getTitle());
            tvAddress.setText(messageInfo.getAddress());
            ivLocation.setImageBitmap(BitmapFactory.decodeFile(messageInfo.getVoice()));
            rlLocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, ChatLookLocationActivity.class);
                    intent.putExtra("lng", messageInfo.getLng());
                    intent.putExtra("lat", messageInfo.getLat());
                    intent.putExtra("title", messageInfo.getTitle());
                    intent.putExtra("address", messageInfo.getAddress());
                    mContext.startActivity(intent);
                }
            });
            rlLocation.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    showCopyDialog(messageInfo.getMsgType(), messageInfo, false, false);
                    return false;
                }
            });
        }
    }

    class ToCardHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.iv_touxiang)
        ImageView mIvTouxiang;
        @Bind(R.id.iv_head)
        ImageView ivHead;
        @Bind(R.id.tv_username)
        TextView tvUsername;
        @Bind(R.id.rl_card)
        RelativeLayout rlCard;
        @Bind(R.id.iv_warning)
        ImageView mIvWarning;
        @Bind(R.id.iv_load)
        ImageView mIvLoad;
        @Bind(R.id.chat_createtime)
        View tvCreateTime;

        ToCardHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void setData(final MessageInfo messageInfo) {
            setCreatetime(tvCreateTime, messageInfo.getShowChatTime());
            UtilTool.getImage(mMgr, UtilTool.getTocoId(), mContext, mIvTouxiang);
            goIndividualDetails(mIvTouxiang, UtilTool.getTocoId(), UtilTool.getUser(), messageInfo);
            setMsgState(messageInfo.getSendStatus(), mIvWarning, mIvLoad);
            mIvWarning.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mMessageList.remove(messageInfo);
                    RoomManage.getInstance().getRoom(mRoomId).anewSendCard(messageInfo);
                }
            });
            tvUsername.setText(messageInfo.getMessage());
            Glide.with(mContext).load(messageInfo.getHeadUrl()).apply(RequestOptions.bitmapTransform(new CircleCrop()).placeholder(R.mipmap.img_nfriend_headshot1)).into(ivHead);
            rlCard.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    showCopyDialog(messageInfo.getMsgType(), messageInfo, false, false);
                    return false;
                }
            });
            rlCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, IndividualDetailsActivity.class);
                    intent.putExtra("user", messageInfo.getCardUser());
                    intent.putExtra("name", messageInfo.getCardUser().split("@")[0]);
                    intent.putExtra("roomId", mRoomId);
                    mContext.startActivity(intent);
                }
            });
        }
    }

    class FromCardHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.iv_touxiang)
        ImageView mIvTouxiang;
        @Bind(R.id.tv_name)
        TextView tvName;
        @Bind(R.id.iv_head)
        ImageView ivHead;
        @Bind(R.id.tv_username)
        TextView tvUsername;
        @Bind(R.id.rl_card)
        RelativeLayout rlCard;
        @Bind(R.id.chat_createtime)
        View tvCreateTime;

        FromCardHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void setData(final MessageInfo messageInfo) {
            // TODO: 2018/5/28 所有的from需要增加一個名字
            setCreatetime(tvCreateTime, messageInfo.getShowChatTime());
            if (RoomManage.ROOM_TYPE_MULTI.equals(mRoomType)) {
                UtilTool.getImage(mContext, mIvTouxiang, mDBRoomMember.findMemberUrl(mRoomId, messageInfo.getSend()));
            } else {
                UtilTool.getImage(mMgr, messageInfo.getSend(), mContext, mIvTouxiang);
            }
            goIndividualDetails(mIvTouxiang, mRoomId, mName, messageInfo);
            tvUsername.setText(messageInfo.getMessage());
            Glide.with(mContext).load(messageInfo.getHeadUrl()).apply(RequestOptions.bitmapTransform(new CircleCrop()).placeholder(R.mipmap.img_nfriend_headshot1)).into(ivHead);
            rlCard.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    showCopyDialog(messageInfo.getMsgType(), messageInfo, false, false);
                    return false;
                }
            });
            rlCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, IndividualDetailsActivity.class);
                    intent.putExtra("user", messageInfo.getCardUser());
                    intent.putExtra("name", messageInfo.getCardUser().split("@")[0]);
                    intent.putExtra("roomId", mRoomId);
                    mContext.startActivity(intent);
                }
            });
        }
    }


    class ToLinkHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.iv_touxiang)
        ImageView mIvTouxiang;
        @Bind(R.id.iv_head)
        ImageView ivHead;
        @Bind(R.id.tv_title)
        TextView tvTitle;
        @Bind(R.id.tv_content)
        TextView tvContent;
        @Bind(R.id.rl_link)
        RelativeLayout rlLink;
        @Bind(R.id.iv_warning)
        ImageView mIvWarning;
        @Bind(R.id.iv_load)
        ImageView mIvLoad;
        @Bind(R.id.chat_createtime)
        View tvCreateTime;

        ToLinkHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void setData(final MessageInfo messageInfo) {
            setCreatetime(tvCreateTime, messageInfo.getShowChatTime());
            UtilTool.getImage(mMgr, UtilTool.getTocoId(), mContext, mIvTouxiang);
            goIndividualDetails(mIvTouxiang, UtilTool.getTocoId(), UtilTool.getUser(), messageInfo);
            setMsgState(messageInfo.getSendStatus(), mIvWarning, mIvLoad);
            mIvWarning.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mMessageList.remove(messageInfo);
                    RoomManage.getInstance().getRoom(mRoomId).anewSendShareLink(messageInfo);
                }
            });
            Glide.with(mContext).load(messageInfo.getHeadUrl()).apply(requestOptions).into(ivHead);
            tvTitle.setText(messageInfo.getTitle());
            tvContent.setText(messageInfo.getContent());
            rlLink.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    showCopyDialog(messageInfo.getMsgType(), messageInfo, false, false);
                    return false;
                }
            });
            rlLink.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int id = 0;
                    try {
                        String idtext = messageInfo.getLinkUrl().substring((Constants.BASE_URL.length() + Constants.NEWS_WEB_URL.length()), messageInfo.getLinkUrl().lastIndexOf("/"));
                        id = Integer.parseInt(idtext);
                    } catch (Exception e) {
                        e.printStackTrace();
                        UtilTool.Log("fengjian---", "轉換id失敗");
                    }

                    UtilTool.Log("fengjian---", "跳轉id：" + id + "\n" + "url：" + messageInfo.getLinkUrl());
                    Intent intent = new Intent(mContext, NewsDetailsActivity.class);
                    intent.putExtra("id", id);
                    intent.putExtra("type", Constants.NEWS_MAIN_TYPE);
                    mContext.startActivity(intent);
                }
            });
        }
    }

    class FromLinkHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.iv_touxiang)
        ImageView mIvTouxiang;
        @Bind(R.id.tv_name)
        TextView tvName;
        @Bind(R.id.iv_head)
        ImageView ivHead;
        @Bind(R.id.tv_title)
        TextView tvTitle;
        @Bind(R.id.tv_content)
        TextView tvContent;
        @Bind(R.id.rl_link)
        RelativeLayout rlLink;
        @Bind(R.id.chat_createtime)
        View tvCreateTime;

        FromLinkHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void setData(final MessageInfo messageInfo) {
            // TODO: 2018/5/28 所有的from需要增加一個名字
            setCreatetime(tvCreateTime, messageInfo.getShowChatTime());
            if (RoomManage.ROOM_TYPE_MULTI.equals(mRoomType)) {
                UtilTool.getImage(mContext, mIvTouxiang, mDBRoomMember.findMemberUrl(mRoomId, messageInfo.getSend()));
            } else {
                UtilTool.getImage(mMgr, messageInfo.getSend(), mContext, mIvTouxiang);
            }
            goIndividualDetails(mIvTouxiang, mRoomId, mName, messageInfo);
            tvTitle.setText(messageInfo.getTitle());
            tvContent.setText(messageInfo.getContent());
            Glide.with(mContext).load(messageInfo.getHeadUrl()).apply(requestOptions).into(ivHead);
            rlLink.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    showCopyDialog(messageInfo.getMsgType(), messageInfo, false, false);
                    return false;
                }
            });
            rlLink.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int id = 0;
                    try {
                        String idtext = messageInfo.getLinkUrl().substring((Constants.BASE_URL.length() + Constants.NEWS_WEB_URL.length()), messageInfo.getLinkUrl().lastIndexOf("/"));
                        id = Integer.parseInt(idtext);
                    } catch (Exception e) {
                        e.printStackTrace();
                        UtilTool.Log("fengjian---", "轉換id失敗");
                    }

                    UtilTool.Log("fengjian---", "跳轉id：" + id + "\n" + "url：" + messageInfo.getLinkUrl());
                    Intent intent = new Intent(mContext, NewsDetailsActivity.class);
                    intent.putExtra("id", id);
                    intent.putExtra("type", Constants.NEWS_MAIN_TYPE);
                    mContext.startActivity(intent);
                }
            });
        }
    }

    class ToGuessHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.iv_touxiang)
        ImageView mIvTouxiang;
        @Bind(R.id.tv_coin)
        TextView tvCoin;
        @Bind(R.id.tv_who)
        TextView tvWho;
        @Bind(R.id.tv_title)
        TextView tvTitle;
        @Bind(R.id.rl_guess)
        RelativeLayout rlGuess;
        @Bind(R.id.iv_warning)
        ImageView mIvWarning;
        @Bind(R.id.iv_load)
        ImageView mIvLoad;
        @Bind(R.id.chat_createtime)
        View tvCreateTime;

        ToGuessHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void setData(final MessageInfo messageInfo) {
            setCreatetime(tvCreateTime, messageInfo.getShowChatTime());
            UtilTool.getImage(mMgr, UtilTool.getTocoId(), mContext, mIvTouxiang);
            goIndividualDetails(mIvTouxiang, UtilTool.getTocoId(), UtilTool.getUser(), messageInfo);
            setMsgState(messageInfo.getSendStatus(), mIvWarning, mIvLoad);
            tvTitle.setText(messageInfo.getTitle());
            tvCoin.setText(messageInfo.getCoin() + mContext.getString(R.string.guess));
            tvWho.setText(mContext.getString(R.string.fa_qi_ren) + ":" + messageInfo.getInitiator());
            tvCoin.setText(messageInfo.getCoin() + mContext.getString(R.string.guess));
            tvWho.setText(mContext.getString(R.string.fa_qi_ren) + ":" + messageInfo.getInitiator());

            mIvWarning.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mMessageList.remove(messageInfo);
                    RoomManage.getInstance().getRoom(mRoomId).anewSendShareGuess(messageInfo);
                }
            });
            rlGuess.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    showCopyDialog(messageInfo.getMsgType(), messageInfo, false, false);
                    return false;
                }
            });
            rlGuess.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (messageInfo.getGuessPw() != null) {
                        if (messageInfo.getGuessPw().isEmpty()) {
                            Intent intent = new Intent(mContext, GuessDetailsActivity.class);
                            intent.putExtra("bet_id", Integer.parseInt(messageInfo.getBetId()));
                            intent.putExtra("period_qty", Integer.parseInt(messageInfo.getPeriodQty()));
                            mContext.startActivity(intent);
                        } else {
                            showPWDialog(messageInfo);
                        }
                    } else {
                        Intent intent = new Intent(mContext, GuessDetailsActivity.class);
                        intent.putExtra("bet_id", Integer.parseInt(messageInfo.getBetId()));
                        intent.putExtra("period_qty", Integer.parseInt(messageInfo.getPeriodQty()));
                        mContext.startActivity(intent);
                    }
                }
            });
        }
    }

    class FromGuessHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.iv_touxiang)
        ImageView mIvTouxiang;
        @Bind(R.id.tv_name)
        TextView tvName;
        @Bind(R.id.tv_coin)
        TextView tvCoin;
        @Bind(R.id.tv_who)
        TextView tvWho;
        @Bind(R.id.tv_title)
        TextView tvTitle;
        @Bind(R.id.rl_guess)
        RelativeLayout rlGuess;
        @Bind(R.id.chat_createtime)
        View tvCreateTime;

        FromGuessHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void setData(final MessageInfo messageInfo) {
            // TODO: 2018/5/28 所有的from需要增加一個名字
            setCreatetime(tvCreateTime, messageInfo.getShowChatTime());
            if (RoomManage.ROOM_TYPE_MULTI.equals(mRoomType)) {
                UtilTool.getImage(mContext, mIvTouxiang, mDBRoomMember.findMemberUrl(mRoomId, messageInfo.getSend()));
            } else {
                UtilTool.getImage(mMgr, messageInfo.getSend(), mContext, mIvTouxiang);
            }
            goIndividualDetails(mIvTouxiang, mRoomId, mName, messageInfo);
            tvTitle.setText(messageInfo.getTitle());
            tvCoin.setText(messageInfo.getCoin() + mContext.getString(R.string.guess));
            tvWho.setText(mContext.getString(R.string.fa_qi_ren) + ":" + messageInfo.getInitiator());
            rlGuess.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    showCopyDialog(messageInfo.getMsgType(), messageInfo, false, false);
                    return false;
                }
            });
            rlGuess.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (messageInfo.getGuessPw() != null) {
                        if (messageInfo.getGuessPw().isEmpty()) {
                            Intent intent = new Intent(mContext, GuessDetailsActivity.class);
                            intent.putExtra("bet_id", Integer.parseInt(messageInfo.getBetId()));
                            intent.putExtra("period_qty", Integer.parseInt(messageInfo.getPeriodQty()));
                            mContext.startActivity(intent);
                        } else {
                            showPWDialog(messageInfo);
                        }
                    } else {
                        Intent intent = new Intent(mContext, GuessDetailsActivity.class);
                        intent.putExtra("bet_id", Integer.parseInt(messageInfo.getBetId()));
                        intent.putExtra("period_qty", Integer.parseInt(messageInfo.getPeriodQty()));
                        mContext.startActivity(intent);
                    }
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
        @Bind(R.id.chat_createtime)
        View tvCreateTime;

        ToTransferHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void setData(final MessageInfo messageInfo) {
            setCreatetime(tvCreateTime, messageInfo.getShowChatTime());
            goIndividualDetails(mIvTouxiang, UtilTool.getTocoId(), UtilTool.getUser(), messageInfo);
            UtilTool.getImage(mMgr, UtilTool.getTocoId(), mContext, mIvTouxiang);
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
            mCvRedpacket.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    showCopyDialog(messageInfo.getMsgType(), messageInfo, false, false);
                    return false;
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
        @Bind(R.id.chat_createtime)
        View tvCreateTime;

        FromTransferHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void setData(final MessageInfo messageInfo) {
//            mIvTouxiang.setImageBitmap(mFromBitmap);
            setCreatetime(tvCreateTime, messageInfo.getShowChatTime());
            if (RoomManage.ROOM_TYPE_MULTI.equals(mRoomType)) {
                UtilTool.getImage(mContext, mIvTouxiang, mDBRoomMember.findMemberUrl(mRoomId, messageInfo.getSend()));
            } else {
                UtilTool.getImage(mMgr, messageInfo.getSend(), mContext, mIvTouxiang);
            }
            goIndividualDetails(mIvTouxiang, mRoomId, mName, messageInfo);
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
            mCvRedpacket.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    showCopyDialog(messageInfo.getMsgType(), messageInfo, false, false);
                    return false;
                }
            });
        }
    }

    class ReadGetHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.tv_content)
        TextView tvContent;
        @Bind(R.id.tv_go)
        TextView tvGo;
        @Bind(R.id.chat_createtime)
        View tvCreateTime;

        ReadGetHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void setData(final MessageInfo messageInfo) {
            setCreatetime(tvCreateTime, messageInfo.getShowChatTime());
            tvContent.setText(messageInfo.getMessage());
            tvGo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, RedPacketActivity.class);
                    intent.putExtra("id", messageInfo.getRedId() + "");
                    intent.putExtra("from", false);
                    intent.putExtra("type", false);
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
                    mTvStatus.setText(mContext.getString(R.string.relative_pay_dengdai_fb));
                } else {
                    mTvStatus.setText(mContext.getString(R.string.pay_succeed_dengdai_fb));
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

        InCoinInformHolder(View view) {
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
                    intent.putExtra("log_id", messageInfo.getBetId());
                    intent.putExtra("type_number", messageInfo.getType() + "");
                    mContext.startActivity(intent);
                }
            });
        }
    }


    private void goIndividualDetails(View view, String user, String name, final MessageInfo messageInfo) {
        //兼容之前沒有send字段問題
        if (messageInfo.getSend() != null) {
            user = messageInfo.getSend();
            name = mMgr.findUserName(user);
            if (StringUtils.isEmpty(name)) {

            }
        }
        final String finalName = name;
        final String finalUser = user;


        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, IndividualDetailsActivity.class);
                intent.putExtra("user", finalUser);
                intent.putExtra("name", finalName);
                intent.putExtra("roomId", mRoomId);
                mContext.startActivity(intent);
            }
        });
    }

    class TextChatHolder extends RecyclerView.ViewHolder {

        public TextChatHolder(View itemView) {
            super(itemView);
        }

        public void setData(MessageInfo messageInfo) {

        }
    }

    private void showPWDialog(final MessageInfo messageInfo) {
        final DeleteCacheDialog deleteCacheDialog = new DeleteCacheDialog(R.layout.dialog_command, mContext, R.style.dialog);
        deleteCacheDialog.show();
        final EditText etGuessPw = (EditText) deleteCacheDialog.findViewById(R.id.et_guess_password);
        Button btnConfirm = (Button) deleteCacheDialog.findViewById(R.id.btn_confirm);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String pw = etGuessPw.getText().toString();
                if (pw.isEmpty()) {
                    AnimatorTool.getInstance().editTextAnimator(etGuessPw);
                    Toast.makeText(mContext, mContext.getString(R.string.toast_guess_pw), Toast.LENGTH_SHORT).show();
                } else {
                    if (messageInfo.getGuessPw().equals(pw)) {
                        Intent intent = new Intent(mContext, GuessDetailsActivity.class);
                        intent.putExtra("bet_id", Integer.parseInt(messageInfo.getBetId()));
                        intent.putExtra("period_qty", Integer.parseInt(messageInfo.getPeriodQty()));
                        intent.putExtra("guess_pw", messageInfo.getGuessPw());
                        mContext.startActivity(intent);
                        deleteCacheDialog.dismiss();
                    } else {
                        AnimatorTool.getInstance().editTextAnimator(etGuessPw);
                        Toast.makeText(mContext, mContext.getString(R.string.toast_guess_pw_error), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}
