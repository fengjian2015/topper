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
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bclould.tea.Presenter.FileDownloadPresenter;
import com.bclould.tea.Presenter.GrabRedPresenter;
import com.bclould.tea.R;
import com.bclould.tea.base.MyApp;
import com.bclould.tea.history.DBManager;
import com.bclould.tea.history.DBRoomMember;
import com.bclould.tea.model.DownloadInfo;
import com.bclould.tea.model.GrabRedInfo;
import com.bclould.tea.model.MessageInfo;
import com.bclould.tea.model.SerMap;
import com.bclould.tea.ui.activity.AddCollectActivity;
import com.bclould.tea.ui.activity.ChatLookLocationActivity;
import com.bclould.tea.ui.activity.ConversationActivity;
import com.bclould.tea.ui.activity.FileOpenActivity;
import com.bclould.tea.ui.activity.GroupConfirmActivity;
import com.bclould.tea.ui.activity.GuessDetailsActivity;
import com.bclould.tea.ui.activity.HTMLActivity;
import com.bclould.tea.ui.activity.ImageViewActivity;
import com.bclould.tea.ui.activity.IndividualDetailsActivity;
import com.bclould.tea.ui.activity.NewsDetailsActivity;
import com.bclould.tea.ui.activity.RedPacketActivity;
import com.bclould.tea.ui.activity.SelectConversationActivity;
import com.bclould.tea.ui.activity.TransferDetailsActivity;
import com.bclould.tea.ui.activity.VideoActivity;
import com.bclould.tea.ui.widget.CurrencyDialog;
import com.bclould.tea.ui.widget.DeleteCacheDialog;
import com.bclould.tea.ui.widget.LodingCircleView;
import com.bclould.tea.ui.widget.MenuListPopWindow;
import com.bclould.tea.ui.widget.MyYAnimation;
import com.bclould.tea.ui.widget.RedDialog;
import com.bclould.tea.utils.ActivityUtil;
import com.bclould.tea.utils.AnimatorTool;
import com.bclould.tea.utils.ChatTimeUtil;
import com.bclould.tea.utils.Constants;
import com.bclould.tea.utils.CustomLinkMovementMethod;
import com.bclould.tea.utils.EventBusUtil;
import com.bclould.tea.utils.HyperLinkUtil;
import com.bclould.tea.utils.MessageEvent;
import com.bclould.tea.utils.MySharedPreferences;
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
import com.danikula.videocache.HttpProxyCacheServer;

import org.greenrobot.eventbus.EventBus;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.github.rockerhieu.emojicon.EmojiconTextView;

import static com.bclould.tea.ui.activity.SystemSetActivity.AUTOMATICALLY_DOWNLOA;
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
    public static final int FROM_INVITE_MSG = 29;//接受群邀请链接
    public static final int TO_INVITE_MSG = 30;//发送群邀请链接
    public static final int FROM_WITHDRAW_MSG = 31;//發送回撤消息
    public static final int TO_WITHDRAW_MSG = 32;//接受回撤消息
    public static final int FROM_HTML_MSG = 33;//發送純網頁信息
    public static final int TO_HTML_MSG = 34;//接受純網頁信息

    private final Context mContext;
    private final List<MessageInfo> mMessageList;
//    private final Bitmap mFromBitmap;

    private final DBManager mMgr;
    private final GrabRedPresenter mGrabRedPresenter;
    //    private final Bitmap mToBitmap;
    private final MediaPlayer mMediaPlayer;
    private int voicePosition;
    ArrayList<String> mImageList = new ArrayList<>();
    private String mFileName;
    private AnimationDrawable mAnim;
    private final String mRoomId;
    private String mRoomType;
    private String mName;
    private RelativeLayout mrlTitle;
    private DBRoomMember mDBRoomMember;
    private RedDialog mRedDialog;

    public ChatAdapter(Context context, List<MessageInfo> messageList, String roomId, DBManager mgr, MediaPlayer mediaPlayer, String name, String roomType, RelativeLayout rlTitle, DBRoomMember mDBRoomMember
    ) {
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
        } else if (viewType == TO_TRANSFER_MSG) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_to_chat_transfer, parent, false);
            holder = new ToTransferHolder(view);
        } else if (viewType == FROM_TRANSFER_MSG) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_from_chat_transfer, parent, false);
            holder = new FromTransferHolder(view);
        } else if (viewType == RED_GET_MSG) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_chat_red_get, parent, false);
            holder = new ReadGetHolder(view);
        } else if (viewType == TO_FILE_MSG) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_to_chat_file, parent, false);
            holder = new ToFileHolder(view);
        } else if (viewType == FROM_FILE_MSG) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_from_chat_file, parent, false);
            holder = new FromFileHolder(view);
        } else if (viewType == TO_INVITE_MSG) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_to_chat_invite, parent, false);
            holder = new ToInviteHolder(view);
        } else if (viewType == FROM_INVITE_MSG) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_from_chat_invite, parent, false);
            holder = new FromInviteHolder(view);
        } else if (viewType == TO_WITHDRAW_MSG) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_chat_text, parent, false);
            holder = new WithdrawChatHolder(view);
        } else if (viewType == FROM_WITHDRAW_MSG) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_chat_text, parent, false);
            holder = new WithdrawChatHolder(view);
        } else if (viewType == TO_HTML_MSG) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_to_chat_html, parent, false);
            holder = new ToHTMLHolder(view);
        } else if (viewType == FROM_HTML_MSG) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_from_chat_html, parent, false);
            holder = new FromHTMLHolder(view);
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
                fromVoiceHolder.setData(mMessageList.get(position), position);
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
            case TO_FILE_MSG:
                ToFileHolder toFileHolder = (ToFileHolder) holder;
                toFileHolder.setData(mMessageList.get(position));
                break;
            case FROM_FILE_MSG:
                FromFileHolder fromFileHolder = (FromFileHolder) holder;
                fromFileHolder.setData(mMessageList.get(position));
                break;
            case TO_INVITE_MSG:
                ToInviteHolder toInviteHolder = (ToInviteHolder) holder;
                toInviteHolder.setData(mMessageList.get(position));
                break;
            case FROM_INVITE_MSG:
                FromInviteHolder fromInviteHolder = (FromInviteHolder) holder;
                fromInviteHolder.setData(mMessageList.get(position));
                break;
            case TO_WITHDRAW_MSG:
            case FROM_WITHDRAW_MSG:
                WithdrawChatHolder withdrawChatHolder = (WithdrawChatHolder) holder;
                withdrawChatHolder.setData(mMessageList.get(position));
                break;
            case TO_HTML_MSG:
                ToHTMLHolder toHTMLHolder = (ToHTMLHolder) holder;
                toHTMLHolder.setData(mMessageList.get(position),position);
                break;
            case FROM_HTML_MSG:
                FromHTMLHolder fromHTMLHolder = (FromHTMLHolder) holder;
                fromHTMLHolder.setData(mMessageList.get(position),position);
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

    private void showDeleteDialog(final MessageInfo messageInfo) {
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
                    MessageInfo messageInfoNext = mMgr.deleteSingleMessage(mRoomId, messageInfo.getId() + "",0);
                    String conversation = mMgr.findLastMessageConversation(mRoomId,0);
                    if (!StringUtils.isEmpty(conversation)) {
                        mMgr.updateConversationMessage(mRoomId, conversation);
                    }
                    if (messageInfoNext != null) {
                        mMessageList.get(mMessageList.indexOf(messageInfo) + 1).setShowChatTime(messageInfoNext.getShowChatTime());
                    }
                    mMessageList.remove(messageInfo);
                    notifyDataSetChanged();
                    EventBus.getDefault().post(new MessageEvent(EventBusUtil.dispose_unread_msg));
                    deleteCacheDialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void showCopyDialog(final int msgtype, final MessageInfo messageInfo, final boolean isCopy, boolean isTransmit, boolean isWithdraw,boolean isCollect) {
        final List<String> list = new ArrayList<>();
        list.add(mContext.getString(R.string.delete));
        if (isCopy)
            list.add(mContext.getString(R.string.copy));
        if (isTransmit)
            list.add(mContext.getString(R.string.transmit));
        if(isWithdraw&&messageInfo.getSendStatus()==1&&
                (messageInfo.getCreateTime()+(3*60*1000)>System.currentTimeMillis())){
            list.add(mContext.getString(R.string.withdrew));
        }
        if(isCollect){
            list.add(mContext.getString(R.string.collect));
        }
        final MenuListPopWindow menu = new MenuListPopWindow(mContext, list);
        menu.setListOnClick(new MenuListPopWindow.ListOnClick() {
            @Override
            public void onclickitem(int position) {
                position = position - 1;
                if (position < 0) {
                    menu.dismiss();
                } else if (mContext.getString(R.string.delete).equals(list.get(position))) {
                    menu.dismiss();
                    showDeleteDialog(messageInfo);
                } else if (mContext.getString(R.string.copy).equals(list.get(position))) {
                    menu.dismiss();
                    //获取剪贴板管理器：
                    ClipboardManager cm = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                    // 创建普通字符型ClipData
                    ClipData mClipData = ClipData.newPlainText("Label", messageInfo.getMessage());
                    // 将ClipData内容放到系统剪贴板里。
                    cm.setPrimaryClip(mClipData);
                    ToastShow.showToast2((Activity) mContext, mContext.getString(R.string.copy_succeed));
                } else if (mContext.getString(R.string.transmit).equals(list.get(position))) {
                    menu.dismiss();
                    Intent intent = new Intent(mContext, SelectConversationActivity.class);
                    intent.putExtra("type", 1);
                    intent.putExtra("msgType", msgtype);
                    intent.putExtra("messageInfo", messageInfo);
                    mContext.startActivity(intent);
                } else if (mContext.getString(R.string.withdrew).equals(list.get(position))) {
                    menu.dismiss();
                    MessageInfo messageInfo1 = new MessageInfo();
                    messageInfo1.setMessage(mContext.getString(R.string.you_withdrew_a_message));
                    messageInfo1.setBetId(messageInfo.getMsgId());
                    messageInfo1.setInitiator(UtilTool.getUser());
                    if (RoomManage.ROOM_TYPE_MULTI.equals(mRoomType)) {
                        messageInfo1.setRoomId(mRoomId);
                    } else {
                        messageInfo1.setRoomId(UtilTool.getTocoId());
                    }
                    RoomManage.getInstance().getRoom(mRoomId).sendWithdraw(messageInfo1);
                }else if(mContext.getString(R.string.collect).equals(list.get(position))){
                    menu.dismiss();
                    Intent intent = new Intent(mContext, AddCollectActivity.class);
                    intent.putExtra("url", new HyperLinkUtil().getHtmlUrl(messageInfo.getMessage()));
                    mContext.startActivity(intent);
                }else {
                    menu.dismiss();
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

    private void setNameAndUrl(ImageView mIvTouxiang, MessageInfo messageInfo, TextView tvName) {
        UtilTool.getImage(mContext, mIvTouxiang, mDBRoomMember, mMgr, messageInfo.getSend());

        if (RoomManage.ROOM_TYPE_MULTI.equals(mRoomType)) {
            String name = mMgr.queryRemark(messageInfo.getSend());
            if (StringUtils.isEmpty(name)) {
                name = mDBRoomMember.findMemberName(mRoomId, messageInfo.getSend());
            }
            if (StringUtils.isEmpty(name)) {
                name = messageInfo.getSend();
            }
            tvName.setText(name);
            tvName.setVisibility(View.VISIBLE);
        } else {
            tvName.setVisibility(View.GONE);
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
            setCreatetime(tvCreateTime, messageInfo.getShowChatTime());
            UtilTool.getImage(mMgr, UtilTool.getTocoId(), mContext, mIvTouxiang);
            goIndividualDetails(mIvTouxiang, UtilTool.getTocoId(), UtilTool.getUser(), messageInfo);
            final HyperLinkUtil hyperLinkUtil = new HyperLinkUtil();
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
                    notifyDataSetChanged();
                }
            });
            mTvMessamge.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if(!StringUtils.isEmpty(hyperLinkUtil.getHtmlUrl(messageInfo.getMessage()))){
                        showCopyDialog(messageInfo.getMsgType(), messageInfo, true, true, true,true);
                    }else{
                        showCopyDialog(messageInfo.getMsgType(), messageInfo, true, true, true,false);
                    }
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
        @Bind(R.id.tv_name)
        TextView tvName;

        FromTextHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void setData(final MessageInfo messageInfo) {
//            mIvTouxiang.setImageBitmap(mFromBitmap);
            setNameAndUrl(mIvTouxiang, messageInfo, tvName);
            setCreatetime(tvCreateTime, messageInfo.getShowChatTime());
            goIndividualDetails(mIvTouxiang, mRoomId, mName, messageInfo);
            final HyperLinkUtil hyperLinkUtil = new HyperLinkUtil();
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
                    if(!StringUtils.isEmpty(hyperLinkUtil.getHtmlUrl(messageInfo.getMessage()))){
                        showCopyDialog(messageInfo.getMsgType(), messageInfo, true, true, false,true);
                    }else{
                        showCopyDialog(messageInfo.getMsgType(), messageInfo, true, true, false,false);
                    }
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
                    if (RoomManage.ROOM_TYPE_MULTI.equals(mRoomType)) {
                        if (messageInfo.getStatus() == 1) {
                            mGrabRedPresenter.grabRedPacket(true, messageInfo.getRedId(), new GrabRedPresenter.CallBack() {
                                @Override
                                public void send(GrabRedInfo info) {
                                    mMgr.updateMessageState(messageInfo.getId() + "", 1);
                                    messageInfo.setStatus(1);
                                    notifyDataSetChanged();
                                    if (info.getStatus() == 4) {
                                        Toast.makeText(mContext, info.getMessage(), Toast.LENGTH_SHORT).show();
                                    }else if(info.getStatus()==2){
                                        showDialog(messageInfo,true,false,info);
                                    }else if(info.getStatus()==5){
                                        showDialog(messageInfo,false,true,info);
                                    }else{
                                        skip(info, UtilTool.getTocoId(), 1, info.getData().getSend_rp_user_name());
                                    }
                                }

                                @Override
                                public void error() {

                                }
                            });
                        } else {
                            mCvRedpacket.setCardBackgroundColor(mContext.getResources().getColor(R.color.redpacket2));
                            mTvExamine.setText(mContext.getString(R.string.look_red_packet));
                            mCvRedpacket.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    showDialog(messageInfo,false,false,null);
                                }
                            });
                        }
                    } else {
                        mGrabRedPresenter.grabRedPacket(true, messageInfo.getRedId(), new GrabRedPresenter.CallBack() {
                            @Override
                            public void send(GrabRedInfo info) {
                                mMgr.updateMessageState(messageInfo.getId() + "", 1);
                                messageInfo.setStatus(1);
                                notifyDataSetChanged();
                                if (info.getStatus() == 4) {
                                    Toast.makeText(mContext, info.getMessage(), Toast.LENGTH_SHORT).show();
                                } else if(info.getStatus()==2){
                                    showDialog(messageInfo,true,false,info);
                                }else if(info.getStatus()==5){
                                    showDialog(messageInfo,false,true,info);
                                }else{
                                    skip(info, UtilTool.getTocoId(), 0, info.getData().getSend_rp_user_name());
                                }
                            }

                            @Override
                            public void error() {

                            }
                        });
                    }
                }
            });
            mCvRedpacket.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    showCopyDialog(messageInfo.getMsgType(), messageInfo, false, false, false,false);
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
        @Bind(R.id.tv_name)
        TextView tvName;

        FromRedHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void setData(final MessageInfo messageInfo) {
            String mUser = messageInfo.getSend();
            setCreatetime(tvCreateTime, messageInfo.getShowChatTime());
            final String mName = mMgr.findUserName(mUser);
            setNameAndUrl(mIvTouxiang, messageInfo, tvName);
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
                        mGrabRedPresenter.grabRedPacket(true, messageInfo.getRedId(), new GrabRedPresenter.CallBack() {
                            @Override
                            public void send(GrabRedInfo info) {
                                mMgr.updateMessageState(messageInfo.getId() + "", 1);
                                messageInfo.setStatus(1);
                                notifyDataSetChanged();
                                if (info.getStatus() == 4) {
                                    Toast.makeText(mContext, info.getMessage(), Toast.LENGTH_SHORT).show();
                                } else if(info.getStatus()==2){
                                    showDialog(messageInfo,true,false,info);
                                }else if(info.getStatus()==5){
                                    showDialog(messageInfo,false,true,info);
                                }else{
                                    skip(info, finalMUser, 1, info.getData().getSend_rp_user_name());
                                }
                            }
                            @Override
                            public void error() {

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
                        showDialog(messageInfo,false,false,null);
                    }
                });
            }
            mCvRedpacket.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    showCopyDialog(messageInfo.getMsgType(), messageInfo, false, false, false,false);
                    return false;
                }
            });
        }
    }

    //显示币种弹框
    private void showDialog(final MessageInfo messageInfo, boolean isGrabThe, boolean isOverdue, final GrabRedInfo grabRedInfo) {
        //暫無群聊，所以沒有考慮群聊情況
        String mUser = messageInfo.getSend();
        if (StringUtils.isEmpty(mUser))
            mUser = mRoomId;
        String mName = mMgr.queryRemark(mUser);
        if (StringUtils.isEmpty(mName)) {
            mName = mMgr.findUserName(mUser);
        }
        if (StringUtils.isEmpty(mName)) {
            mName = mDBRoomMember.findMemberName(mRoomId, mUser);
        }
        if (StringUtils.isEmpty(mName)) {
            mName = mUser;
        }
        if(!ActivityUtil.isActivityOnTop(mContext))return;

        if(mRedDialog==null){
            mRedDialog=new RedDialog(mContext);
        }
        mRedDialog.show();
        mRedDialog.setCoin(isGrabThe,isOverdue,messageInfo,mName,mDBRoomMember,mMgr);
        final String finalMName1 = mName;
        final String finalMUser1 = mUser;
        mRedDialog.setOnClickListener(new RedDialog.OnClickListener() {
            @Override
            public void onOpen() {
                mGrabRedPresenter.grabRedPacket(false, messageInfo.getRedId(), new GrabRedPresenter.CallBack() {
                    @Override
                    public void send(GrabRedInfo info) {
                        mRedDialog.stopAnimation();
                        mMgr.updateMessageState(messageInfo.getId() + "", 1);
                        messageInfo.setStatus(1);
                        notifyDataSetChanged();
                        if (info.getStatus() == 4) {
                            mRedDialog.dismiss();
                            Toast.makeText(mContext, info.getMessage(), Toast.LENGTH_SHORT).show();
                        }  else if(info.getStatus()==2){
                            showDialog(messageInfo,true,false,info);
                        }else if(info.getStatus()==5){
                            showDialog(messageInfo,false,true,info);
                        }else {
                            mRedDialog.dismiss();
//                            skip(info, mFromBitmap, mUser, 1);
                            skip(info, finalMUser1, 1, finalMName1);
                        }

                    }

                    @Override
                    public void error() {
                        mRedDialog.dismiss();
                    }
                });
            }

            @Override
            public void onDetail() {
                if (RoomManage.ROOM_TYPE_SINGLE.equals(mRoomType)&&messageInfo.getMsgType()==TO_RED_MSG) {
                    skip(grabRedInfo, finalMUser1, 0, finalMName1);
                }else{
                    skip(grabRedInfo, finalMUser1, 1, finalMName1);
                }
            }
            @Override
            public void onBreak() {
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
                    notifyDataSetChanged();
                }
            });
            final AnimationDrawable anim = (AnimationDrawable) mIvAnim.getBackground();
            mRlVoice.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    playVoice(mMediaPlayer, messageInfo.getVoice(), anim, mMessageList.size() - 1);
                }
            });

            mRlVoice.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    showCopyDialog(messageInfo.getMsgType(), messageInfo, false, false, true,false);
                    return false;
                }
            });
        }
    }

    public void stopVoicePlay(){
        if(mAnim!=null){
            mAnim.selectDrawable(0);
            mAnim.stop();
        }
        mMediaPlayer.stop();
        mMediaPlayer.reset();
    }

    public void playVoice(MediaPlayer mediaPlayer, String fileName, final AnimationDrawable anim, final int position) {
        try {
            ((ConversationActivity) mContext).isPlayVoi1ce(true);
            if (mAnim != null) {
                mAnim.selectDrawable(0);
                mAnim.stop();
            }
            mFileName = fileName;
            mAnim = anim;
            voicePosition = position;
            //对mediaPlayer进行实例化
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    ((ConversationActivity) mContext).isPlayVoi1ce(false);
                    mediaPlayer.reset();
                    anim.selectDrawable(0);
                    anim.stop();
                    mMessageList.get(position).setVoiceStatus(1);
                    notifyItemChanged(position);
                    for (int i = position; i < mMessageList.size(); i++) {
                        if (mMessageList.get(i).getMsgType() == FROM_VOICE_MSG && mMessageList.get(i).getVoiceStatus() != 1) {
                            View view = ((ConversationActivity) mContext).getItemView(i);
                            if (view != null) {
                                ImageView mIvAnim = view.findViewById(R.id.iv_anim);
                                mAnim = (AnimationDrawable) mIvAnim.getBackground();
                            }
                            playVoice(mMediaPlayer, mMessageList.get(i).getVoice(), mAnim, i);
                            mMgr.updateMessageStatus(mMessageList.get(i).getId());
                            break;
                        }
                    }
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
        } catch (IOException e) {
            Log("日志", "没有找到这个文件");
            e.printStackTrace();
        }
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            try {
                ((ConversationActivity) mContext).isPlayVoi1ce(true);
                mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        ((ConversationActivity) mContext).isPlayVoi1ce(false);
                        mediaPlayer.reset();
                        mAnim.selectDrawable(0);
                        mAnim.stop();
                        mMessageList.get(voicePosition).setVoiceStatus(1);
                        notifyItemChanged(voicePosition);
                        for (int i = voicePosition; i < mMessageList.size(); i++) {
                            if (mMessageList.get(i).getMsgType() == FROM_VOICE_MSG && mMessageList.get(i).getVoiceStatus() != 1) {
                                View view = ((ConversationActivity) mContext).getItemView(i);
                                if (view != null) {
                                    ImageView mIvAnim = view.findViewById(R.id.iv_anim);
                                    mAnim = (AnimationDrawable) mIvAnim.getBackground();
                                }
                                playVoice(mMediaPlayer, mMessageList.get(i).getVoice(), mAnim, i);
                                mMgr.updateMessageStatus(mMessageList.get(i).getId());
                                break;
                            }
                        }
                    }
                });
                mAnim.stop();
                mMediaPlayer.stop();
                mMediaPlayer.reset();
                mMediaPlayer.setDataSource(mFileName);     //设置资源目录
                mMediaPlayer.prepare();//缓冲
                mMediaPlayer.start();//开始或恢复播放
                mAnim.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    public void refreshPlayVoice(boolean isSpeakerOn) {
        try {
            mHandler.removeMessages(1);
            if (mAnim != null && mAnim.isRunning()) {
                if (isSpeakerOn) {
                    mMediaPlayer.setAudioStreamType(android.media.AudioManager.MODE_NORMAL);
                    mHandler.sendEmptyMessage(1);
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        mMediaPlayer.setAudioStreamType(android.media.AudioManager.MODE_IN_COMMUNICATION);
                    } else {
                        mMediaPlayer.setAudioStreamType(android.media.AudioManager.MODE_IN_CALL);
                    }
                    mHandler.sendEmptyMessageDelayed(1, 1000);
                }
            }
        } catch (Exception e) {
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
        @Bind(R.id.tv_name)
        TextView tvName;

        FromVoiceHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void setData(final MessageInfo messageInfo, final int position) {
            setCreatetime(tvCreateTime, messageInfo.getShowChatTime());
            setNameAndUrl(mIvTouxiang, messageInfo, tvName);
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
                    playVoice(mMediaPlayer, messageInfo.getVoice(), anim, position);
                    mMgr.updateMessageStatus(messageInfo.getId());
                    mIvStatus.setVisibility(View.GONE);
                    messageInfo.setVoiceStatus(1);
                    mMessageList.get(position).setVoiceStatus(1);
                }
            });
            mRlVoice.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    showCopyDialog(messageInfo.getMsgType(), messageInfo, false, false, false,false);
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
            setCreatetime(tvCreateTime, messageInfo.getShowChatTime());
            UtilTool.getImage(mMgr, UtilTool.getTocoId(), mContext, mIvTouxiang);
            goIndividualDetails(mIvTouxiang, UtilTool.getTocoId(), UtilTool.getUser(), messageInfo);
            String postfixs=UtilTool.getPostfix3(messageInfo.getKey());
            if (".gif".equals(postfixs) || ".GIF".equals(postfixs)) {
                setTypeImage(null,messageInfo.getMessage(),mIvImg);
            }else {
                if(messageInfo.getVoice().startsWith("http")){
                    setTypeImage(null,messageInfo.getVoice(),mIvImg);
                }else{
                    setTypeImage(new File(messageInfo.getVoice()),"",mIvImg);
                }
            }
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
                    showCopyDialog(messageInfo.getMsgType(), messageInfo, false, true, true,false);
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
        @Bind(R.id.tv_name)
        TextView tvName;

        FromImgHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void setData(final MessageInfo messageInfo) {
            setNameAndUrl(mIvTouxiang, messageInfo, tvName);
            setCreatetime(tvCreateTime, messageInfo.getShowChatTime());
            goIndividualDetails(mIvTouxiang, mRoomId, mName, messageInfo);
            String postfixs=UtilTool.getPostfix3(messageInfo.getKey());
            if (".gif".equals(postfixs) || ".GIF".equals(postfixs)) {
                setTypeImage(null,messageInfo.getMessage(),mIvImg);
            }else {
                if(messageInfo.getVoice().startsWith("http")){
                    setTypeImage(null,messageInfo.getVoice(),mIvImg);
                }else{
                    setTypeImage(new File(messageInfo.getVoice()),"",mIvImg);
                }
            }
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
                    showCopyDialog(messageInfo.getMsgType(), messageInfo, false, true, false,false);
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
        CardView mRlVideo;
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

            if(messageInfo.getVoice().startsWith("http")) {
                Glide.with(mContext).load(messageInfo.getVoice()).listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        mIvVideo.setImageDrawable(resource);
                        return false;
                    }
                }).apply(requestOptions).into(mIvVideo);
            }else {
                mIvVideo.setImageBitmap(BitmapFactory.decodeFile(messageInfo.getVoice()));
            }
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
                    intent.putExtra("compressUrl",messageInfo.getVoice());
                    mContext.startActivity(intent);
                }
            });
            mRlVideo.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    showCopyDialog(messageInfo.getMsgType(), messageInfo, false, true, true,false);
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
        CardView mRlVideo;
        @Bind(R.id.iv_video_play)
        ImageView mIvVideoPlay;
        @Bind(R.id.chat_createtime)
        View tvCreateTime;
        @Bind(R.id.tv_name)
        TextView tvName;
        @Bind(R.id.loading_pr)
        LodingCircleView loadingPr;

        FromVideoHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void setData(final MessageInfo messageInfo) {
            setNameAndUrl(mIvTouxiang, messageInfo, tvName);
            setCreatetime(tvCreateTime, messageInfo.getShowChatTime());
            goIndividualDetails(mIvTouxiang, mRoomId, mName, messageInfo);
            setLoading(messageInfo,loadingPr,mIvVideoPlay);
            if(messageInfo.getVoice().startsWith("http")) {
                Glide.with(mContext).load(messageInfo.getVoice()).listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        mIvVideo.setImageDrawable(resource);
                        return false;
                    }
                }).apply(requestOptions).into(mIvVideo);
            }else {
                mIvVideo.setImageBitmap(BitmapFactory.decodeFile(messageInfo.getVoice()));
            }
            mRlVideo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, VideoActivity.class);
                    intent.putExtra("url", messageInfo.getMessage());
                    intent.putExtra("compressUrl",messageInfo.getVoice());
                    mContext.startActivity(intent);
                }
            });
            mRlVideo.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    showCopyDialog(messageInfo.getMsgType(), messageInfo, false, true, false,false);
                    return false;
                }
            });

        }
    }
    private void setLoading( MessageInfo messageInfo, final LodingCircleView loadingPr, ImageView mIvVideoPlay) {
        if(messageInfo.getStatus()!=1&&MySharedPreferences.getInstance().getBoolean(AUTOMATICALLY_DOWNLOA)&&UtilTool.isWifi(mContext)){
            try {
                startLoading(messageInfo,loadingPr,mIvVideoPlay);
            }catch (Exception e){
                e.printStackTrace();
            }
        }else{
            mIvVideoPlay.setVisibility(View.VISIBLE);
            loadingPr.setVisibility(View.GONE);
        }
    }
    private void startLoading(final MessageInfo messageInfo, final LodingCircleView loadingPr, ImageView ivVideoPlay) throws URISyntaxException {
        final String key=messageInfo.getKey();
        final File mFile = new File(Constants.VIDEO + key);
        if (mFile.exists()) {
            if (MySharedPreferences.getInstance().getLong(key) == mFile.length() || (MySharedPreferences.getInstance().getLong(key) == 0 && mFile.length() != 0)) {
                UtilTool.Log("下載"+key,"文件存在   不需要下載");
                ivVideoPlay.setVisibility(View.VISIBLE);
                loadingPr.setVisibility(View.GONE);
                return;
            } else {
                UtilTool.Log("下載"+key,"文件存在   開始下載");
                double currentSize = Double.parseDouble(mFile.length() + "");
                double totleSize = Double.parseDouble(MySharedPreferences.getInstance().getLong(key) + "");
                int progress = (int) (currentSize / totleSize * 100);
                loadingPr.setProgerss(progress,true);
                ivVideoPlay.setVisibility(View.GONE);
                FileDownloadPresenter.getInstance(mContext).dowbloadFile(Constants.BUCKET_NAME2, key, mFile);
            }
        } else {
            UtilTool.Log("下載"+key,"文件不存在   開始下載");
            loadingPr.setVisibility(View.VISIBLE);
            ivVideoPlay.setVisibility(View.GONE);
            FileDownloadPresenter.getInstance(mContext).dowbloadFile(Constants.BUCKET_NAME2, key, mFile);
        }
        FileDownloadPresenter.getInstance(mContext).setOnDownloadCallbackListener(new FileDownloadPresenter.downloadCallback() {
            @Override
            public void onSuccess(File file, String keys) {
                if (!key.equals(keys)) return;
                FileDownloadPresenter.getInstance(mContext).removeDownloadCallbackListener(this);
                UtilTool.Log("下載"+key,"下載成功");
                MessageEvent messageEvent=new MessageEvent(mContext.getString(R.string.automatic_download_complete));
                messageEvent.setUrl(messageInfo.getMessage());
                messageEvent.setFilepath(mFile.getAbsolutePath());
                EventBus.getDefault().post(messageEvent);
            }

            @Override
            public void onFailure(String keys) {
                if (!key.equals(keys)) return;
                FileDownloadPresenter.getInstance(mContext).removeDownloadCallbackListener(this);
                UtilTool.Log("下載"+key,"下載失敗");
            }

            @Override
            public void onSuccsetProgressListeneress(final long currentSize, final long totalSize, final String keys) {
                if (!key.equals(keys)) return;
                if (!mFile.exists()) {
                    MySharedPreferences.getInstance().setLong(key, totalSize);
                } else {
                    if (mFile.length() == 0) {
                        MySharedPreferences.getInstance().setLong(key, totalSize);
                    }
                }
                ((Activity)mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        int progress = (int) (Double.parseDouble(currentSize+ MySharedPreferences.getInstance().getLong(key) + "") / Double.parseDouble(totalSize + MySharedPreferences.getInstance().getLong(key) + "") * 100);
                        loadingPr.setProgerss(progress,true);
                    }
                });
            }
        });
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
                    notifyDataSetChanged();
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
                    showCopyDialog(messageInfo.getMsgType(), messageInfo, false, false, true,false);
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
        @Bind(R.id.tv_name)
        TextView tvName;

        FromLocationHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void setData(final MessageInfo messageInfo) {
            setCreatetime(tvCreateTime, messageInfo.getShowChatTime());
            setNameAndUrl(mIvTouxiang, messageInfo, tvName);
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
                    showCopyDialog(messageInfo.getMsgType(), messageInfo, false, false, false,false);
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
                    notifyDataSetChanged();
                }
            });
            tvUsername.setText(messageInfo.getMessage());
            Glide.with(mContext).load(messageInfo.getHeadUrl()).apply(RequestOptions.bitmapTransform(new CircleCrop()).placeholder(R.mipmap.img_nfriend_headshot1)).into(ivHead);
            rlCard.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    showCopyDialog(messageInfo.getMsgType(), messageInfo, false, false, true,false);
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
            setCreatetime(tvCreateTime, messageInfo.getShowChatTime());
            setNameAndUrl(mIvTouxiang, messageInfo, tvName);
            goIndividualDetails(mIvTouxiang, mRoomId, mName, messageInfo);
            tvUsername.setText(messageInfo.getMessage());
            Glide.with(mContext).load(messageInfo.getHeadUrl()).apply(RequestOptions.bitmapTransform(new CircleCrop()).placeholder(R.mipmap.img_nfriend_headshot1)).into(ivHead);
            rlCard.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    showCopyDialog(messageInfo.getMsgType(), messageInfo, false, false, false,false);
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
                    notifyDataSetChanged();
                }
            });
            Glide.with(mContext).load(messageInfo.getHeadUrl()).apply(requestOptions).into(ivHead);
            tvTitle.setText(messageInfo.getTitle());
            tvContent.setText(messageInfo.getContent());
            rlLink.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    showCopyDialog(messageInfo.getMsgType(), messageInfo, false, true, true,false);
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
            setNameAndUrl(mIvTouxiang, messageInfo, tvName);
            goIndividualDetails(mIvTouxiang, mRoomId, mName, messageInfo);
            tvTitle.setText(messageInfo.getTitle());
            tvContent.setText(messageInfo.getContent());
            Glide.with(mContext).load(messageInfo.getHeadUrl()).apply(requestOptions).into(ivHead);
            rlLink.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    showCopyDialog(messageInfo.getMsgType(), messageInfo, false, true, false,false);
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
                    notifyDataSetChanged();
                }
            });
            rlGuess.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    showCopyDialog(messageInfo.getMsgType(), messageInfo, false, false, true,false);
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
            setNameAndUrl(mIvTouxiang, messageInfo, tvName);
            goIndividualDetails(mIvTouxiang, mRoomId, mName, messageInfo);
            tvTitle.setText(messageInfo.getTitle());
            tvCoin.setText(messageInfo.getCoin() + mContext.getString(R.string.guess));
            tvWho.setText(mContext.getString(R.string.fa_qi_ren) + ":" + messageInfo.getInitiator());
            rlGuess.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    showCopyDialog(messageInfo.getMsgType(), messageInfo, false, false, false,false);
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
                    showCopyDialog(messageInfo.getMsgType(), messageInfo, false, false, false,false);
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
        @Bind(R.id.tv_name)
        TextView tvName;

        FromTransferHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void setData(final MessageInfo messageInfo) {
            setNameAndUrl(mIvTouxiang, messageInfo, tvName);
            setCreatetime(tvCreateTime, messageInfo.getShowChatTime());
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
                    showCopyDialog(messageInfo.getMsgType(), messageInfo, false, false, false,false);
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

    class ToFileHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.iv_touxiang)
        ImageView mIvTouxiang;
        @Bind(R.id.cv_redpacket)
        CardView mCvRedpacket;
        @Bind(R.id.chat_createtime)
        View tvCreateTime;
        @Bind(R.id.iv_type)
        ImageView ivType;
        @Bind(R.id.tv_file_name)
        TextView tvFileName;
        @Bind(R.id.tv_file_size)
        TextView tvFileSize;
        @Bind(R.id.iv_warning)
        ImageView mIvWarning;
        @Bind(R.id.iv_load)
        ImageView mIvLoad;

        ToFileHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void setData(final MessageInfo messageInfo) {
            setCreatetime(tvCreateTime, messageInfo.getShowChatTime());
            goIndividualDetails(mIvTouxiang, UtilTool.getTocoId(), UtilTool.getUser(), messageInfo);
            UtilTool.getImage(mMgr, UtilTool.getTocoId(), mContext, mIvTouxiang);
            setMsgState(messageInfo.getSendStatus(), mIvWarning, mIvLoad);
            tvFileName.setText(messageInfo.getTitle());
            tvFileSize.setText(messageInfo.getCount());
            ivType.setImageResource(UtilTool.getFileImageRe(messageInfo.getContent()));
            mIvWarning.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mMessageList.remove(messageInfo);
                    RoomManage.getInstance().getRoom(mRoomId).anewUploadFile(messageInfo);
                    notifyDataSetChanged();
                }
            });

            mCvRedpacket.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, FileOpenActivity.class);
                    intent.putExtra("messageInfo", messageInfo);
                    mContext.startActivity(intent);
                }
            });
            mCvRedpacket.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    showCopyDialog(messageInfo.getMsgType(), messageInfo, false, true, true,false);
                    return false;
                }
            });
        }
    }

    class FromFileHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.iv_touxiang)
        ImageView mIvTouxiang;
        @Bind(R.id.cv_redpacket)
        CardView mCvRedpacket;
        @Bind(R.id.chat_createtime)
        View tvCreateTime;
        @Bind(R.id.tv_name)
        TextView tvName;
        @Bind(R.id.iv_type)
        ImageView ivType;
        @Bind(R.id.tv_file_name)
        TextView tvFileName;
        @Bind(R.id.tv_file_size)
        TextView tvFileSize;

        FromFileHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void setData(final MessageInfo messageInfo) {
            setNameAndUrl(mIvTouxiang, messageInfo, tvName);
            setCreatetime(tvCreateTime, messageInfo.getShowChatTime());
            goIndividualDetails(mIvTouxiang, mRoomId, mName, messageInfo);
            tvFileName.setText(messageInfo.getTitle());
            tvFileSize.setText(messageInfo.getCount());
            ivType.setImageResource(UtilTool.getFileImageRe(messageInfo.getContent()));
            mCvRedpacket.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, FileOpenActivity.class);
                    intent.putExtra("messageInfo", messageInfo);
                    mContext.startActivity(intent);
                }
            });
            mCvRedpacket.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    showCopyDialog(messageInfo.getMsgType(), messageInfo, false, true, false,false);
                    return false;
                }
            });
        }
    }

    class ToInviteHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.iv_touxiang)
        ImageView mIvTouxiang;
        @Bind(R.id.cv_redpacket)
        CardView mCvRedpacket;
        @Bind(R.id.chat_createtime)
        View tvCreateTime;
        @Bind(R.id.iv_type)
        ImageView ivType;
        @Bind(R.id.tv_file_size)
        TextView tvFileSize;
        @Bind(R.id.iv_warning)
        ImageView mIvWarning;
        @Bind(R.id.iv_load)
        ImageView mIvLoad;

        ToInviteHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void setData(final MessageInfo messageInfo) {
            setCreatetime(tvCreateTime, messageInfo.getShowChatTime());
            goIndividualDetails(mIvTouxiang, UtilTool.getTocoId(), UtilTool.getUser(), messageInfo);
            UtilTool.getImage(mMgr, UtilTool.getTocoId(), mContext, mIvTouxiang);
            setMsgState(messageInfo.getSendStatus(), mIvWarning, mIvLoad);
            tvFileSize.setText(messageInfo.getInitiator() + mContext.getString(R.string.intive_group) + messageInfo.getRoomName() + mContext.getString(R.string.click_look));
            UtilTool.getGroupImage(messageInfo.getHeadUrl(), (Activity) mContext, ivType);
            mIvWarning.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mMessageList.remove(messageInfo);
                    RoomManage.getInstance().getRoom(mRoomId).anewUploadFile(messageInfo);
                    notifyDataSetChanged();
                }
            });

            mCvRedpacket.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, GroupConfirmActivity.class);
                    intent.putExtra("roomName", messageInfo.getRoomName());
                    intent.putExtra("roomId", messageInfo.getRoomId());
                    intent.putExtra("roomPath", messageInfo.getHeadUrl());
                    mContext.startActivity(intent);
                }
            });
            mCvRedpacket.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    showCopyDialog(messageInfo.getMsgType(), messageInfo, false, false, true,false);
                    return false;
                }
            });
        }
    }

    class FromInviteHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.iv_touxiang)
        ImageView mIvTouxiang;
        @Bind(R.id.cv_redpacket)
        CardView mCvRedpacket;
        @Bind(R.id.chat_createtime)
        View tvCreateTime;
        @Bind(R.id.tv_name)
        TextView tvName;
        @Bind(R.id.iv_type)
        ImageView ivType;
        @Bind(R.id.tv_file_name)
        TextView tvFileName;
        @Bind(R.id.tv_file_size)
        TextView tvFileSize;

        FromInviteHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void setData(final MessageInfo messageInfo) {
            setNameAndUrl(mIvTouxiang, messageInfo, tvName);
            setCreatetime(tvCreateTime, messageInfo.getShowChatTime());
            goIndividualDetails(mIvTouxiang, mRoomId, mName, messageInfo);
            tvFileSize.setText(messageInfo.getInitiator() + mContext.getString(R.string.intive_group) + messageInfo.getRoomName() + mContext.getString(R.string.click_look));
            UtilTool.getGroupImage(messageInfo.getHeadUrl(), (Activity) mContext, ivType);
            mCvRedpacket.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, GroupConfirmActivity.class);
                    intent.putExtra("roomName", messageInfo.getRoomName());
                    intent.putExtra("roomId", messageInfo.getRoomId());
                    intent.putExtra("roomPath", messageInfo.getHeadUrl());
                    mContext.startActivity(intent);
                }
            });
            mCvRedpacket.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    showCopyDialog(messageInfo.getMsgType(), messageInfo, false, false, false,false);
                    return false;
                }
            });
        }
    }


    class WithdrawChatHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.chat_createtime)
        View tvCreateTime;
        @Bind(R.id.tv_content)
        TextView tvContent;
        @Bind(R.id.rl_title)
        RelativeLayout rlTitle;

        public WithdrawChatHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void setData(MessageInfo messageInfo) {
            setCreatetime(tvCreateTime, messageInfo.getShowChatTime());
            tvContent.setText(messageInfo.getMessage());
            if (messageInfo.getMsgType() == TO_WITHDRAW_MSG && messageInfo.getSendStatus() != 1) {
                rlTitle.setVisibility(View.GONE);
            } else {
                rlTitle.setVisibility(View.VISIBLE);
            }
        }
    }

    class ToHTMLHolder extends RecyclerView.ViewHolder {
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
        @Bind(R.id.iv_loading)
        ImageView mIvLoading;

        ToHTMLHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void setData(final MessageInfo messageInfo, int position) {
            getHtmlContent(messageInfo,position);
            setCreatetime(tvCreateTime, messageInfo.getShowChatTime());
            UtilTool.getImage(mMgr, UtilTool.getTocoId(), mContext, mIvTouxiang);
            goIndividualDetails(mIvTouxiang, UtilTool.getTocoId(), UtilTool.getUser(), messageInfo);
            setMsgState(messageInfo.getSendStatus(), mIvWarning, mIvLoad);
            mIvWarning.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mMessageList.remove(messageInfo);
                    RoomManage.getInstance().getRoom(mRoomId).anewSendHtml(messageInfo);
                    notifyDataSetChanged();
                }
            });
            Glide.with(mContext).load(messageInfo.getHeadUrl()).apply(requestOptions).into(ivHead);
            if(StringUtils.isEmpty(messageInfo.getTitle())){
                mIvLoading.setVisibility(View.VISIBLE);
                AnimationDrawable animationDrawable = (AnimationDrawable) mIvLoading.getBackground();
                animationDrawable.start();
            }else{
                mIvLoading.setVisibility(View.GONE);
                AnimationDrawable animationDrawable = (AnimationDrawable) mIvLoading.getBackground();
                animationDrawable.stop();
            }
            tvTitle.setText(messageInfo.getTitle());
            tvContent.setText(messageInfo.getContent());
            rlLink.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    showCopyDialog(messageInfo.getMsgType(), messageInfo, false, true, true,true);
                    return false;
                }
            });
            rlLink.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, HTMLActivity.class);
                    intent.putExtra("html5Url", messageInfo.getMessage());
                    mContext.startActivity(intent);
                }
            });

        }
    }
    class FromHTMLHolder extends RecyclerView.ViewHolder {
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
        @Bind(R.id.iv_loading)
        ImageView mIvLoading;

        FromHTMLHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void setData(final MessageInfo messageInfo, int position) {
            getHtmlContent(messageInfo,position);
            setCreatetime(tvCreateTime, messageInfo.getShowChatTime());
            setNameAndUrl(mIvTouxiang, messageInfo, tvName);
            goIndividualDetails(mIvTouxiang, mRoomId, mName, messageInfo);
            if(StringUtils.isEmpty(messageInfo.getTitle())){
                mIvLoading.setVisibility(View.VISIBLE);
                AnimationDrawable animationDrawable = (AnimationDrawable) mIvLoading.getBackground();
                animationDrawable.start();
            }else{
                mIvLoading.setVisibility(View.GONE);
                AnimationDrawable animationDrawable = (AnimationDrawable) mIvLoading.getBackground();
                animationDrawable.stop();
            }
            tvTitle.setText(messageInfo.getTitle());
            tvContent.setText(messageInfo.getContent());
            Glide.with(mContext).load(messageInfo.getHeadUrl()).apply(requestOptions).into(ivHead);
            rlLink.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    showCopyDialog(messageInfo.getMsgType(), messageInfo, false, true, false,true);
                    return false;
                }
            });
            rlLink.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, HTMLActivity.class);
                    intent.putExtra("html5Url", messageInfo.getMessage());
                    mContext.startActivity(intent);
                }
            });
        }
    }


    class TextChatHolder extends RecyclerView.ViewHolder {

        public TextChatHolder(View itemView) {
            super(itemView);
        }

        public void setData(MessageInfo messageInfo) {

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

    private void getHtmlContent(final MessageInfo messageInfo,final int position){
        if(!StringUtils.isEmpty(messageInfo.getContent())&&!StringUtils.isEmpty(messageInfo.getTitle())){
            return;
        }
        new Thread(){
            @Override
            public void run() {
                try {
                    String html5Url=messageInfo.getMessage();
                    if (!UtilTool.checkLinkedExe(html5Url)) {
                        html5Url = "http://" + html5Url;
                    }
                    Document doc = Jsoup.connect(html5Url).get();
                    if(StringUtils.isEmpty(messageInfo.getTitle())||StringUtils.isEmpty(messageInfo.getContent())) {
                        messageInfo.setTitle(doc.title());
                        messageInfo.setContent(mContext.getString(R.string.my_look)+doc.title()+mContext.getString(R.string.share_with_you));
                    }
                    Elements link = doc.body().getElementsByTag("div").select("img");
                    for (int i = 0; i < link.size(); i++) {
                        Element element = link.get(i);
                        String url=element.select("img").attr("src");
                        if(!StringUtils.isEmpty(url)&&StringUtils.isEmpty(messageInfo.getHeadUrl())){
                            messageInfo.setHeadUrl(url);
                        }
                        if(!StringUtils.isEmpty(messageInfo.getHeadUrl())){
                            ((Activity)mContext).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mMgr.updateMessageContentUrlTitle(messageInfo.getMsgId(),messageInfo.getContent(),messageInfo.getHeadUrl(),messageInfo.getTitle());
                                    notifyItemChanged(position);
                                }
                            });
                            return;
                        }
                        UtilTool.Log("fengjian","url:"+url);
                    }
                    ((Activity)mContext).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mMgr.updateMessageContentUrlTitle(messageInfo.getMsgId(),messageInfo.getContent(),messageInfo.getHeadUrl(),messageInfo.getTitle());
                            notifyItemChanged(position);
                        }
                    });
                    return;
                }catch (SocketTimeoutException e){
                    e.printStackTrace();
                }catch (Exception e){
                    if(StringUtils.isEmpty(messageInfo.getTitle())||StringUtils.isEmpty(messageInfo.getContent())) {
                        messageInfo.setTitle(mContext.getString(R.string.cannot_open));
                        messageInfo.setContent(mContext.getString(R.string.my_look)+mContext.getString(R.string.cannot_open)+mContext.getString(R.string.share_with_you));
                        messageInfo.setHeadUrl("");
                        ((Activity)mContext).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mMgr.updateMessageContentUrlTitle(messageInfo.getMsgId(),messageInfo.getContent(),messageInfo.getHeadUrl(),messageInfo.getTitle());
                                notifyItemChanged(position);
                            }
                        });
                    }
                }
            }
        }.start();
    }

    private void setTypeImage(File file, String url, final ImageView mIvImg){
        if(file==null) {
            Glide.with(mContext).load(url).listener(new RequestListener<Drawable>() {
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
        }else{
            Glide.with(mContext).load(file).listener(new RequestListener<Drawable>() {
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
        }
    }
}
