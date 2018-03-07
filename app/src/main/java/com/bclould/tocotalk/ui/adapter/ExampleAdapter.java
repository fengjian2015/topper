package com.bclould.tocotalk.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bclould.tocotalk.Presenter.GrabRedPresenter;
import com.bclould.tocotalk.R;
import com.bclould.tocotalk.history.DBManager;
import com.bclould.tocotalk.model.GrabRedInfo;
import com.bclould.tocotalk.model.MessageInfo;
import com.bclould.tocotalk.model.UserInfo;
import com.bclould.tocotalk.ui.activity.ImageViewActivity;
import com.bclould.tocotalk.ui.activity.RedPacketActivity;
import com.bclould.tocotalk.ui.widget.BubbleImageView;
import com.bclould.tocotalk.ui.widget.CurrencyDialog;
import com.bclould.tocotalk.utils.Constants;
import com.bclould.tocotalk.utils.UtilTool;
import com.bclould.tocotalk.xmpp.XmppConnection;

import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.util.stringencoder.Base64;
import org.jxmpp.jid.impl.JidCreate;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.github.rockerhieu.emojicon.EmojiconTextView;

import static com.bclould.tocotalk.ui.fragment.ConversationFragment.IMGMESSAGE;
import static com.bclould.tocotalk.utils.UtilTool.Log;


/**
 * 作者：wgyscsf on 2017/1/2 18:46
 * 邮箱：wgyscsf@163.com
 * 博客：http://blog.csdn.net/wgyscsf
 */
@RequiresApi(api = Build.VERSION_CODES.N)
public class ExampleAdapter extends BaseAdapter {

    private final Context mContext;
    private final List<MessageInfo> mMessageList;
    private final Bitmap mUserImage;
    private final String mUser;
    private final DBManager mMgr;
    private final GrabRedPresenter mGrabRedPresenter;
    private final MediaPlayer mMediaPlayer;

    private Bitmap mBitmap;
    private CurrencyDialog mCurrencyDialog;
    private String mFileName;
    ArrayList<String> mImageList = new ArrayList<>();

    public ExampleAdapter(Context context, List<MessageInfo> messageList, Bitmap userImage, String user, DBManager mgr, MediaPlayer mediaPlayer) {
        mContext = context;
        mMessageList = messageList;
        mUserImage = userImage;
        mUser = user;
        mMgr = mgr;
        mGrabRedPresenter = new GrabRedPresenter(this, mContext);
        mMediaPlayer = mediaPlayer;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        mImageList.clear();
        for (MessageInfo info : mMessageList) {
            if (info.getMsgType() == IMGMESSAGE) {
                if (info.getType() == 0) {
                    mImageList.add(info.getMessage());
                } else {
                    String message = info.getMessage();
                    String url = message.substring(message.indexOf(":") + 1, message.length());
                    mImageList.add(url);
                }
            }
        }
    }

    @Override
    public int getCount() {
        if (mMessageList != null) {
            return mMessageList.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return mMessageList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup viewGroup) {
        //设置ViewHolder
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_example_activity, viewGroup, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final ViewHolder viewHolder2 = viewHolder;
        final MessageInfo messageInfo = mMessageList.get(position);
        //判断是谁的消息
        if (messageInfo.getType() == 0) {
            //头像设置
            List<UserInfo> userInfos = mMgr.queryUser(Constants.MYUSER);
            if (userInfos.size() != 0) {
                mBitmap = BitmapFactory.decodeFile(userInfos.get(0).getPath());
                viewHolder.mIeaHeadImg2.setImageBitmap(mBitmap);
            } else {
                Drawable drawable = mContext.getResources().getDrawable(R.mipmap.img_nfriend_headshot1);
                BitmapDrawable bd = (BitmapDrawable) drawable;
                mBitmap = bd.getBitmap();
                viewHolder.mIeaHeadImg2.setImageDrawable(drawable);
            }
            //显示哪个消息
            viewHolder.mRlMessage2.setVisibility(View.VISIBLE);
            viewHolder.mRlMessage.setVisibility(View.GONE);

            //消息类型
            switch (messageInfo.getMsgType()) {
                case 0:
                    viewHolder.mLlTextMsg.setVisibility(View.VISIBLE);
                    viewHolder.mCvRedpacket2.setVisibility(View.GONE);
                    viewHolder.mLlVoice2.setVisibility(View.GONE);
                    viewHolder.mBivImg2.setVisibility(View.GONE);
                    viewHolder.mTvMessamge2.setText(messageInfo.getMessage());
                    if (messageInfo.getSendStatus() == 1) {
                        viewHolder.mIvWarning2.setVisibility(View.VISIBLE);
                    } else {
                        viewHolder.mIvWarning2.setVisibility(View.GONE);
                    }
                    if (viewHolder.mIvWarning2.getVisibility() == View.VISIBLE) {
                        viewHolder.mIvWarning2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                anewSendMessage(mUser, messageInfo.getMessage(), viewHolder2.mIvWarning2, messageInfo.getId());
                            }
                        });
                    }
                    break;
                case 1:
                    final String coin = messageInfo.getCoin();
                    final String remark = messageInfo.getRemark();
                    final String count = messageInfo.getCount();
                    final String id = messageInfo.getId() + "";
                    final int redId = messageInfo.getRedId();
                    final int type = messageInfo.getType();
                    viewHolder.mTvMessamge2.setVisibility(View.GONE);
                    viewHolder.mBivImg2.setVisibility(View.GONE);
                    viewHolder.mCvRedpacket2.setVisibility(View.VISIBLE);
                    viewHolder.mLlVoice2.setVisibility(View.GONE);
                    viewHolder.mTvCoinRedpacket2.setText(coin + "红包");
                    viewHolder.mTvRemark2.setText(remark);
                    viewHolder.mCvRedpacket2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mGrabRedPresenter.grabRedPacket(redId, id, position, type);
                        }
                    });
                    if (mMessageList.get(position).getState() == 1) {
                        viewHolder.mCvRedpacket2.setCardBackgroundColor(mContext.getColor(R.color.redpacket));
                        viewHolder.mTvExamine2.setText("已領取紅包");
                    } else {
                        viewHolder.mCvRedpacket2.setCardBackgroundColor(mContext.getColor(R.color.redpacket2));
                        viewHolder.mTvExamine2.setText("查看红包");
                    }
                    break;
                case 2:
                    viewHolder.mTvMessamge2.setVisibility(View.GONE);
                    viewHolder.mLlVoice2.setVisibility(View.VISIBLE);
                    viewHolder.mBivImg2.setVisibility(View.GONE);
                    viewHolder.mCvRedpacket2.setVisibility(View.GONE);
                    viewHolder.mTvVoiceTime2.setText(messageInfo.getVoiceTime() + "''");
                    int wide = Integer.parseInt(messageInfo.getVoiceTime()) * 2;
                    String blank = " ";
                    for (int i = 0; i < wide; i++) {
                        blank += " ";
                    }
                    viewHolder.mIvVoice2.setText(blank);
                    if (messageInfo.getSendStatus() == 1) {
                        viewHolder.mIvVoiceHint2.setVisibility(View.VISIBLE);
                    } else {
                        viewHolder.mIvVoiceHint2.setVisibility(View.GONE);
                    }
                    viewHolder.mRlVoice2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            playVoice(mMediaPlayer, messageInfo.getVoice());
                        }
                    });
                    viewHolder.mIvVoiceHint2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            anewSendVoiceMsg(messageInfo, viewHolder2.mIvVoiceHint2);
                        }
                    });
                    break;
                case 3:
                    viewHolder.mTvMessamge2.setVisibility(View.GONE);
                    viewHolder.mLlVoice2.setVisibility(View.GONE);
                    viewHolder.mBivImg2.setVisibility(View.VISIBLE);
                    viewHolder.mCvRedpacket2.setVisibility(View.GONE);
                    final Bitmap bitmap = BitmapFactory.decodeFile(messageInfo.getVoice());
                    viewHolder.mBivImg2.setLocalImageBitmap(bitmap, R.drawable.img_chat_bg);
                    viewHolder.mBivImg2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(mContext, ImageViewActivity.class);
                            intent.putStringArrayListExtra("images", mImageList);
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
                    break;
            }
        } else {
            //设置头像
            viewHolder.mIeaHeadImg.setImageBitmap(mUserImage);
            //显示哪个消息
            viewHolder.mRlMessage2.setVisibility(View.GONE);
            viewHolder.mRlMessage.setVisibility(View.VISIBLE);
            //消息类型
            switch (messageInfo.getMsgType()) {
                case 0:
                    viewHolder.mTvMessamge.setVisibility(View.VISIBLE);
                    viewHolder.mCvRedpacket.setVisibility(View.GONE);
                    viewHolder.mBivImg.setVisibility(View.GONE);
                    viewHolder.mLlVoice.setVisibility(View.GONE);
                    String message = messageInfo.getMessage();
                    viewHolder.mTvMessamge.setText(message);
                    break;
                case 1:
                    final String coin = messageInfo.getCoin();
                    final String remark = messageInfo.getRemark();
                    final String count = messageInfo.getCount();
                    final String id = messageInfo.getId() + "";
                    final int redId = messageInfo.getRedId();
                    final int type = messageInfo.getType();
                    viewHolder.mTvMessamge.setVisibility(View.GONE);
                    viewHolder.mLlVoice.setVisibility(View.GONE);
                    viewHolder.mBivImg.setVisibility(View.GONE);
                    viewHolder.mCvRedpacket.setVisibility(View.VISIBLE);
                    viewHolder.mTvCoinRedpacket.setText(coin + "红包");
                    viewHolder.mTvRemark.setText(remark);
                    if (mMessageList.get(position).getState() == 1) {
                        viewHolder.mCvRedpacket.setCardBackgroundColor(mContext.getColor(R.color.redpacket));
                        viewHolder.mTvExamine.setText("已領取紅包");
                        viewHolder.mCvRedpacket.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                mGrabRedPresenter.grabRedPacket(redId, id, position, type);
                            }
                        });
                    } else {
                        viewHolder.mCvRedpacket.setCardBackgroundColor(mContext.getColor(R.color.redpacket2));
                        viewHolder.mTvExamine.setText("查看红包");
                        viewHolder.mCvRedpacket.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                showDialog(mUserImage, coin, remark, count, viewHolder2, id, position, redId);
                            }
                        });
                    }
                    break;
                case 2:
                    viewHolder.mTvMessamge.setVisibility(View.GONE);
                    viewHolder.mLlVoice.setVisibility(View.VISIBLE);
                    viewHolder.mCvRedpacket.setVisibility(View.GONE);
                    viewHolder.mBivImg.setVisibility(View.GONE);
                    viewHolder.mTvVoiceTime.setText(messageInfo.getVoiceTime() + "''");
                    int wide = Integer.parseInt(messageInfo.getVoiceTime()) * 2;
                    String blank = " ";
                    for (int i = 0; i < wide; i++) {
                        blank += " ";
                    }
                    viewHolder.mIvVoice.setText(blank);
                    if (messageInfo.getVoiceStatus() == 1) {
                        viewHolder.mIvStatus.setVisibility(View.GONE);
                    } else {
                        viewHolder.mIvStatus.setVisibility(View.VISIBLE);
                    }
                    viewHolder.mRlVoice.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            playVoice(mMediaPlayer, messageInfo.getVoice());
                            mMgr.updateMessageStatus(messageInfo.getId());
                            viewHolder2.mIvStatus.setVisibility(View.GONE);
                        }
                    });
                    break;
                case 3:
                    viewHolder.mTvMessamge.setVisibility(View.GONE);
                    viewHolder.mLlVoice.setVisibility(View.GONE);
                    viewHolder.mCvRedpacket.setVisibility(View.GONE);
                    viewHolder.mBivImg.setVisibility(View.VISIBLE);
                    final Bitmap bitmap = BitmapFactory.decodeFile(messageInfo.getVoice());
                    viewHolder.mBivImg.setLocalImageBitmap(bitmap, R.drawable.img_chat2_bg);
                    viewHolder.mBivImg.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(mContext, ImageViewActivity.class);
                            intent.putStringArrayListExtra("images", mImageList);
                            int position = 0;
                            String msg = messageInfo.getMessage();
                            for (int i = 0; i < mImageList.size(); i++) {
                                String url = msg.substring(msg.indexOf(":") + 1, msg.length());
                                if (url.equals(mImageList.get(i))) {
                                    position = i;
                                }
                            }
                            intent.putExtra("clickedIndex", position);
                            mContext.startActivity(intent);
                        }
                    });
                    break;
            }
        }
        return convertView;
    }

    private void anewSendVoiceMsg(MessageInfo messageInfo, ImageView ivVoiceHint2) {
        try {
            ChatManager manager = ChatManager.getInstanceFor(XmppConnection.getInstance().getConnection());
            Chat chat = manager.createChat(JidCreate.entityBareFrom(mUser), null);
            Message message = new Message();
            byte[] bytes = UtilTool.readStream(messageInfo.getVoice());
            String base64 = Base64.encodeToString(bytes);
            chat.sendMessage(message);
            ivVoiceHint2.setVisibility(View.GONE);
            mMgr.updateMessageStatus(messageInfo.getId());
        } catch (Exception e) {
            e.printStackTrace();
            ivVoiceHint2.setVisibility(View.VISIBLE);
            Toast.makeText(mContext, "发送失败", Toast.LENGTH_SHORT).show();
        }
    }

    private void anewSendMessage(String user, String message, ImageView ivWarning, int id) {
        try {
            ChatManager manager = ChatManager.getInstanceFor(XmppConnection.getInstance().getConnection());
            Chat chat = manager.createChat(JidCreate.entityBareFrom(user), null);
            chat.sendMessage(message);
            ivWarning.setVisibility(View.GONE);
            mMgr.updateMessageHint(id);
        } catch (Exception e) {
            e.printStackTrace();
            ivWarning.setVisibility(View.VISIBLE);
            Toast.makeText(mContext, "发送失败", Toast.LENGTH_SHORT).show();
        }
    }

    //显示币种弹框
    private void showDialog(final Bitmap userImage, final String coin, final String remark, final String count, final ViewHolder viewHolder, final String id, final int position, final int redId) {

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
        from.setText("給你發了一個" + coin + "紅包");
        name.setText(mUser.substring(0, mUser.indexOf("@")));
        tvRemark.setText(remark);
        touxiang.setImageBitmap(userImage);
        bark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCurrencyDialog.dismiss();
            }
        });
        open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mGrabRedPresenter.grabRedPacket(redId, id, position, position);
                mCurrencyDialog.dismiss();
            }
        });
    }

    private void skip(GrabRedInfo baseInfo, Bitmap userImage, int type, String user) {
        Intent intent = new Intent(mContext, RedPacketActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("user", user);
        bundle.putSerializable("grabRedInfo", baseInfo);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        userImage.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] bytes = baos.toByteArray();
        bundle.putByteArray("image", bytes);
        intent.putExtras(bundle);
        intent.putExtra("from", true);
        intent.putExtra("type", true);
        mContext.startActivity(intent);
    }

    public void setData(GrabRedInfo baseInfo, String id, int position, int type) {
        if (mBitmap == null) {
            List<UserInfo> infos = mMgr.queryUser(Constants.MYUSER);
            mBitmap = BitmapFactory.decodeFile(infos.get(0).getPath());
        }

        if (type != 0)
            skip(baseInfo, mUserImage, 1, mUser);
        else
            skip(baseInfo, mBitmap, 1, Constants.MYUSER);
        mMgr.updateMessageState(id, 1);
        mMessageList.get(position).setState(1);
        notifyDataSetChanged();
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

    static class ViewHolder {
        @Bind(R.id.iea_headImg)
        ImageView mIeaHeadImg;
        @Bind(R.id.biv_img)
        BubbleImageView mBivImg;
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
        @Bind(R.id.ll_voice)
        RelativeLayout mLlVoice;
        @Bind(R.id.tv_messamge)
        EmojiconTextView mTvMessamge;
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
        @Bind(R.id.rl_message)
        RelativeLayout mRlMessage;
        @Bind(R.id.iea_headImg2)
        ImageView mIeaHeadImg2;
        @Bind(R.id.biv_img2)
        BubbleImageView mBivImg2;
        @Bind(R.id.iv_voice_hint2)
        ImageView mIvVoiceHint2;
        @Bind(R.id.tv_voice_time2)
        TextView mTvVoiceTime2;
        @Bind(R.id.iv_voice2)
        TextView mIvVoice2;
        @Bind(R.id.iv_anim2)
        ImageView mIvAnim2;
        @Bind(R.id.rl_voice2)
        RelativeLayout mRlVoice2;
        @Bind(R.id.ll_voice2)
        LinearLayout mLlVoice2;
        @Bind(R.id.iv_warning2)
        ImageView mIvWarning2;
        @Bind(R.id.tv_messamge2)
        EmojiconTextView mTvMessamge2;
        @Bind(R.id.ll_text_msg)
        LinearLayout mLlTextMsg;
        @Bind(R.id.tv_remark2)
        TextView mTvRemark2;
        @Bind(R.id.tv_examine2)
        TextView mTvExamine2;
        @Bind(R.id.iv_redPacket2)
        ImageView mIvRedPacket2;
        @Bind(R.id.tv_coin_redpacket2)
        TextView mTvCoinRedpacket2;
        @Bind(R.id.cv_redpacket2)
        CardView mCvRedpacket2;
        @Bind(R.id.rl_message2)
        RelativeLayout mRlMessage2;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
