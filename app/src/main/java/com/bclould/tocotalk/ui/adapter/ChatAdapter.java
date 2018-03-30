package com.bclould.tocotalk.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bclould.tocotalk.Presenter.GrabRedPresenter;
import com.bclould.tocotalk.R;
import com.bclould.tocotalk.history.DBManager;
import com.bclould.tocotalk.model.GrabRedInfo;
import com.bclould.tocotalk.model.MessageInfo;
import com.bclould.tocotalk.model.SerMap;
import com.bclould.tocotalk.model.UserInfo;
import com.bclould.tocotalk.model.VoiceInfo;
import com.bclould.tocotalk.ui.activity.ImageViewActivity;
import com.bclould.tocotalk.ui.activity.RedPacketActivity;
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

    public ChatAdapter(Context context, List<MessageInfo> messageList, Bitmap fromBitmap, String user, DBManager mgr, MediaPlayer mediaPlayer) {
        mContext = context;
        mMessageList = messageList;
        mFromBitmap = fromBitmap;
        mUser = user;
        mMgr = mgr;
        mMediaPlayer = mediaPlayer;
        mGrabRedPresenter = new GrabRedPresenter(mContext);
        List<UserInfo> userInfos = mMgr.queryUser(Constants.MYUSER);
        if (userInfos.size() != 0) {
            mToBitmap = BitmapFactory.decodeFile(userInfos.get(0).getPath());
        } else {
            Drawable drawable = mContext.getResources().getDrawable(R.mipmap.img_nfriend_headshot1);
            BitmapDrawable bd = (BitmapDrawable) drawable;
            mToBitmap = bd.getBitmap();
        }
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
        } else if (viewType == TO_FILE_MSG) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_to_chat_file, parent, false);
            holder = new FromVideoHolder(view);
        } else if (viewType == FROM_FILE_MSG) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_from_chat_file, parent, false);
            holder = new FromVideoHolder(view);
        }
        return holder;
    }

    HashMap<String, String> mImageMap = new HashMap<>();

    public void upDateImage() {
        mImageList.clear();
        mImageMap.clear();
        for (final MessageInfo info : mMessageList) {
            if (info.getMsgType() == TO_IMG_MSG) {
                mImageList.add(info.getMessage());
                mImageMap.put(info.getMessage(), "");
            } else if (info.getMsgType() == FROM_IMG_MSG) {
                if (info.getImageType() != 0) {
                    mImageList.add(info.getMessage());
                    mImageMap.put(info.getMessage(), "");
                } else {
                    mImageList.add(info.getVoice());
                    mImageMap.put(info.getVoice(), info.getMessage());
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
            chat.sendMessage(message);
            ivWarning.setVisibility(View.GONE);
            messageInfo.setSendStatus(0);
            mMgr.updateMessageHint(id, 0);
        } catch (Exception e) {
            e.printStackTrace();
            ivWarning.setVisibility(View.VISIBLE);
            Toast.makeText(mContext, "发送失败", Toast.LENGTH_SHORT).show();
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
            mTvCoinRedpacket.setText(messageInfo.getCoin() + "红包");
            mTvRemark.setText(messageInfo.getRemark());
            mCvRedpacket.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mGrabRedPresenter.grabRedPacket(messageInfo.getRedId(), new GrabRedPresenter.CallBack() {
                        @Override
                        public void send(GrabRedInfo info) {
                            mMgr.updateMessageState(messageInfo.getId() + "", 1);
                            messageInfo.setState(1);
                            notifyDataSetChanged();
                            skip(info, mToBitmap, UtilTool.getMyUser());
                        }
                    });
                }
            });
            if (messageInfo.getState() == 1) {
                mCvRedpacket.setCardBackgroundColor(mContext.getColor(R.color.redpacket));
                mTvExamine.setText("已領取紅包");
            } else {
                mCvRedpacket.setCardBackgroundColor(mContext.getColor(R.color.redpacket2));
                mTvExamine.setText("查看红包");
            }
        }
    }

    private void skip(GrabRedInfo baseInfo, Bitmap bitmap, String user) {
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
            mTvCoinRedpacket.setText(messageInfo.getCoin() + "红包");
            mTvRemark.setText(messageInfo.getRemark());
            if (messageInfo.getState() == 1) {
                mCvRedpacket.setCardBackgroundColor(mContext.getColor(R.color.redpacket));
                mTvExamine.setText("已領取紅包");
                mCvRedpacket.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mGrabRedPresenter.grabRedPacket(messageInfo.getRedId(), new GrabRedPresenter.CallBack() {
                            @Override
                            public void send(GrabRedInfo info) {
                                mMgr.updateMessageState(messageInfo.getId() + "", 1);
                                messageInfo.setState(1);
                                notifyDataSetChanged();
                                skip(info, mFromBitmap, mUser);
                            }
                        });
                    }
                });
            } else {
                mCvRedpacket.setCardBackgroundColor(mContext.getColor(R.color.redpacket2));
                mTvExamine.setText("查看红包");
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
        from.setText("給你發了一個" + messageInfo.getCoin() + "紅包");
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
                        messageInfo.setState(1);
                        notifyDataSetChanged();
                        skip(info, mFromBitmap, mUser);
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
            mRlVoice.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    playVoice(mMediaPlayer, messageInfo.getVoice());
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
            message.setBody("[audio]:" + duration + "秒");
            message.addExtension(voiceInfo);
            chat.sendMessage(message);
            ivWarning.setVisibility(View.GONE);
            messageInfo.setSendStatus(0);
            mMgr.updateMessageHint(messageInfo.getId(), 0);
            EventBus.getDefault().post(new MessageEvent("自己发了消息"));
        } catch (Exception e) {
            e.printStackTrace();
            ivWarning.setVisibility(View.VISIBLE);
            Toast.makeText(mContext, "发送失败", Toast.LENGTH_SHORT).show();
        }
    }

    public void playVoice(MediaPlayer mediaPlayer, String fileName) {
        try {
            //对mediaPlayer进行实例化
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    mediaPlayer.reset();
                }
            });
            if (mediaPlayer.isPlaying()) {
                if (mFileName.equals(fileName)) {
                    mediaPlayer.stop();
                    mediaPlayer.reset();
                } else {
                    mediaPlayer.stop();
                    mediaPlayer.reset();
                    mediaPlayer.setDataSource(fileName);     //设置资源目录
                    mediaPlayer.prepare();//缓冲
                    mediaPlayer.start();
                }
            } else {
                mediaPlayer.reset();
                mediaPlayer.setDataSource(fileName);     //设置资源目录
                mediaPlayer.prepare();//缓冲
                mediaPlayer.start();//开始或恢复播放
            }
            mFileName = fileName;
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
            mRlVoice.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    playVoice(mMediaPlayer, messageInfo.getVoice());
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
            Bitmap bitmap = BitmapFactory.decodeFile(messageInfo.getVoice());
            mIvImg.setImageBitmap(bitmap);
            mIvImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, ImageViewActivity.class);
                    intent.putStringArrayListExtra("images", mImageList);
                    SerMap serMap = new SerMap();
                    serMap.setMap(mImageMap);
                    intent.putExtra("imgMap", serMap);
                    int position = 0;
                    for (int i = 0; i < mImageList.size(); i++) {
                        if (messageInfo.getImageType() == 0) {
                            if (messageInfo.getVoice().equals(mImageList.get(i))) {
                                position = i;
                            }
                        }else {
                            if (messageInfo.getMessage().equals(mImageList.get(i))) {
                                position = i;
                            }
                        }
                    }
                    intent.putExtra("clickedIndex", position);
                    intent.putExtra("id", messageInfo.getId());
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
}
