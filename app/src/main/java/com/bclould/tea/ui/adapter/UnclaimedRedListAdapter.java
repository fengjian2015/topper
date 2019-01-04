package com.bclould.tea.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.bclould.tea.Presenter.GrabRedPresenter;
import com.bclould.tea.R;
import com.bclould.tea.history.DBManager;
import com.bclould.tea.history.DBRoomMember;
import com.bclould.tea.model.GrabRedInfo;
import com.bclould.tea.model.UnclaimedRedInfo;
import com.bclould.tea.ui.activity.RedPacketActivity;
import com.bclould.tea.ui.widget.CurrencyDialog;
import com.bclould.tea.ui.widget.MyYAnimation;
import com.bclould.tea.utils.StringUtils;
import com.bclould.tea.utils.UtilTool;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by GA on 2017/10/12.
 */

public class UnclaimedRedListAdapter extends RecyclerView.Adapter {

    private  Context mContext;
    private  List<UnclaimedRedInfo.DataBean> reviewInfos;
    private String roomId;
    private DBRoomMember mDBRoomMember;
    private DBManager mMgr;
    private CurrencyDialog mCurrencyDialog;

    public UnclaimedRedListAdapter(Context reviewListActivity, ArrayList<UnclaimedRedInfo.DataBean> arrayList, DBManager mDBManager, DBRoomMember mDBRoomMember, String roomId) {
        this.mContext=reviewListActivity;
        this.reviewInfos=arrayList;
        this.roomId=roomId;
        this.mMgr=mDBManager;
        this.mDBRoomMember=mDBRoomMember;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.item_unclaimed_red, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        if (reviewInfos.size() != 0 )
            viewHolder.setData(reviewInfos.get(position));
    }

    @Override
    public int getItemCount() {
        if (reviewInfos.size() != 0) {
            return reviewInfos.size();
        }
        return 0;
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.iv_touxiang)
        ImageView ivTouxiang;
        @Bind(R.id.tv_remark)
        TextView tvRemark;
        @Bind(R.id.tv_name)
        TextView tvName;
        @Bind(R.id.tv_time)
        TextView tvTime;

        private String mUser;
        private String mName;
        private UnclaimedRedInfo.DataBean mAddRequestInfo;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showDialog(mAddRequestInfo,mUser,mName);
                }
            });
        }


        public void setData(UnclaimedRedInfo.DataBean dataBean) {
            mAddRequestInfo=dataBean;
            mUser=mAddRequestInfo.getToco_id();
            mName=mMgr.queryRemark(mUser);
            if(StringUtils.isEmpty(mName)){
                mName=mMgr.findUserName(mUser);
            }
            if(StringUtils.isEmpty(mName)){
                mName=mDBRoomMember.findMemberName(roomId,mUser);
            }
            if(StringUtils.isEmpty(mName)){
                mName=mUser;
            }
            UtilTool.getImage(mContext, ivTouxiang,mDBRoomMember,mMgr,mUser);
            tvName.setText(mName);
            tvRemark.setText(URLDecoder.decode(dataBean.getIntro()));
            tvTime.setText(dataBean.getCreated_at()+"");
        }


    }

    //显示币种弹框
    private void showDialog(final UnclaimedRedInfo.DataBean mAddRequestInfo, String mUser, String mName) {
        //暫無群聊，所以沒有考慮群聊情況

        mCurrencyDialog = new CurrencyDialog(R.layout.dialog_redpacket, mContext, R.style.dialog);
        Window window = mCurrencyDialog.getWindow();
        window.setWindowAnimations(R.style.CustomDialog);
        mCurrencyDialog.show();
        mCurrencyDialog.setCanceledOnTouchOutside(false);
        ImageView touxiang = (ImageView) mCurrencyDialog.findViewById(R.id.iv_touxiang);
        TextView tvDetail=mCurrencyDialog.findViewById(R.id.tv_detail);
        TextView from = (TextView) mCurrencyDialog.findViewById(R.id.tv_from);
        TextView name = (TextView) mCurrencyDialog.findViewById(R.id.tv_name);
        TextView tvRemark = (TextView) mCurrencyDialog.findViewById(R.id.tv_remark);
        ImageView bark = (ImageView) mCurrencyDialog.findViewById(R.id.iv_bark);
        final Button open = (Button) mCurrencyDialog.findViewById(R.id.btn_open);
        final MyYAnimation myYAnimation=new MyYAnimation();
        myYAnimation.setRepeatCount(Animation.INFINITE);
        from.setText(mContext.getString(R.string.red_package_hint) + mAddRequestInfo.getCoin_name() + mContext.getString(R.string.red_package));
        name.setText(mName);
        tvRemark.setText(URLDecoder.decode(mAddRequestInfo.getIntro()));
//        touxiang.setImageBitmap(mFromBitmap);
        UtilTool.getImage(mContext,touxiang,mDBRoomMember,mMgr,mUser);
        bark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCurrencyDialog.dismiss();
            }
        });
        final String finalMUser = mUser;
        final String finalMName = mName;
        open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                open.startAnimation(myYAnimation);
                new GrabRedPresenter(mContext).grabRedPacket(false,mAddRequestInfo.getId(), new GrabRedPresenter.CallBack() {
                    @Override
                    public void send(GrabRedInfo info) {
                        myYAnimation.cancel();
                        if (info.getStatus() == 4) {
                            Toast.makeText(mContext, info.getMessage(), Toast.LENGTH_SHORT).show();
                        } else {
//                            skip(info, mFromBitmap, mUser, 1);
                            skip(info, finalMUser, 1, finalMName);
                        }
                        mCurrencyDialog.dismiss();
                    }

                    @Override
                    public void error() {
                        mCurrencyDialog.dismiss();
                    }
                });

            }
        });
    }

    private void skip(GrabRedInfo baseInfo, String user, int who, String name) {
        Intent intent = new Intent(mContext, RedPacketActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("user", user);
        bundle.putString("name", name);
        bundle.putSerializable("grabRedInfo", baseInfo);
        intent.putExtras(bundle);
        intent.putExtra("from", true);
        intent.putExtra("type", true);
        intent.putExtra("who", who);
        mContext.startActivity(intent);
    }
}
