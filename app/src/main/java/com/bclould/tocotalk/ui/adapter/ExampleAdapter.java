package com.bclould.tocotalk.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bclould.tocotalk.Presenter.GrabRedPresenter;
import com.bclould.tocotalk.R;
import com.bclould.tocotalk.history.DBManager;
import com.bclould.tocotalk.model.GrabRedInfo;
import com.bclould.tocotalk.model.MessageInfo;
import com.bclould.tocotalk.model.UserInfo;
import com.bclould.tocotalk.ui.activity.RedPacketActivity;
import com.bclould.tocotalk.ui.widget.CurrencyDialog;
import com.bclould.tocotalk.utils.Constants;
import com.bclould.tocotalk.utils.UtilTool;

import java.io.ByteArrayOutputStream;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.github.rockerhieu.emojicon.EmojiconTextView;

/**
 * 作者：wgyscsf on 2017/1/2 18:46
 * 邮箱：wgyscsf@163.com
 * 博客：http://blog.csdn.net/wgyscsf
 */
public class ExampleAdapter extends BaseAdapter {

    private final Context mContext;
    private final List<MessageInfo> mMessageList;
    private final Bitmap mUserImage;
    private final String mUser;
    private final DBManager mMgr;
    private final GrabRedPresenter mGrabRedPresenter;
    private Bitmap mBitmap;
    private CurrencyDialog mCurrencyDialog;

    public ExampleAdapter(Context context, List<MessageInfo> messageList, Bitmap userImage, String user, DBManager mgr) {
        mContext = context;
        mMessageList = messageList;
        mUserImage = userImage;
        mUser = user;
        mMgr = mgr;
        mGrabRedPresenter = new GrabRedPresenter(this, mContext);
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
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_example_activity, viewGroup, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final ViewHolder viewHolder2 = viewHolder;
        UtilTool.Log("日志", mMessageList.get(position).getRedId() + " ");
        if (mMessageList.get(position).getType() == 0) {
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
            viewHolder.mRlMessage2.setVisibility(View.VISIBLE);
            viewHolder.mRlMessage.setVisibility(View.GONE);
            if (mMessageList.get(position).getRemark() == null) {
                viewHolder.mTvMessamge2.setVisibility(View.VISIBLE);
                viewHolder.mCvRedpacket2.setVisibility(View.GONE);
                String message = mMessageList.get(position).getMessage();
                viewHolder.mTvMessamge2.setText(message);
            } else {
                final String coin = mMessageList.get(position).getCoin();
                final String remark = mMessageList.get(position).getRemark();
                final String count = mMessageList.get(position).getCount();
                final String id = mMessageList.get(position).getId() + "";
                final int redId = mMessageList.get(position).getRedId();
                final int type = mMessageList.get(position).getType();
                viewHolder.mTvMessamge2.setVisibility(View.GONE);
                viewHolder.mCvRedpacket2.setVisibility(View.VISIBLE);
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
            }
        } else {
            viewHolder.mIeaHeadImg.setImageBitmap(mUserImage);
            viewHolder.mRlMessage2.setVisibility(View.GONE);
            viewHolder.mRlMessage.setVisibility(View.VISIBLE);
            if (mMessageList.get(position).getRemark() == null) {
                viewHolder.mTvMessamge.setVisibility(View.VISIBLE);
                viewHolder.mCvRedpacket.setVisibility(View.GONE);
                String message = mMessageList.get(position).getMessage();
                viewHolder.mTvMessamge.setText(message);
            } else {
                final String coin = mMessageList.get(position).getCoin();
                final String remark = mMessageList.get(position).getRemark();
                final String count = mMessageList.get(position).getCount();
                final String id = mMessageList.get(position).getId() + "";
                final int redId = mMessageList.get(position).getRedId();
                final int type = mMessageList.get(position).getType();
                viewHolder.mTvMessamge.setVisibility(View.GONE);
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
            }
        }
        return convertView;
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
        mMgr.updateMessage(id, 1);
        mMessageList.get(position).setState(1);
        notifyDataSetChanged();
    }


    class ViewHolder {
        @Bind(R.id.iea_headImg)
        ImageView mIeaHeadImg;
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
        @Bind(R.id.tv_messamge2)
        EmojiconTextView mTvMessamge2;
        @Bind(R.id.iea_headImg2)
        ImageView mIeaHeadImg2;
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
