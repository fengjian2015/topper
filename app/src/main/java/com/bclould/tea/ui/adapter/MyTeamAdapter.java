package com.bclould.tea.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bclould.tea.R;
import com.bclould.tea.model.MyTeamInfo;
import com.bclould.tea.utils.StringUtils;
import com.bclould.tea.utils.UtilTool;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by GA on 2018/1/5.
 */
public class MyTeamAdapter extends RecyclerView.Adapter {
    private int oldClick = -1;
    private final Context mContext;
    private final List<MyTeamInfo.DataBean.UserListBean> mHashMapList;
    private OnItemClick mOnItemClick;

    public MyTeamAdapter(Context context, List<MyTeamInfo.DataBean.UserListBean> mHashMapList) {
        mContext = context;
        this.mHashMapList = mHashMapList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_team_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.setData(mHashMapList.get(position), position);
    }

    @Override
    public int getItemCount() {
        if (mHashMapList != null) {
            return mHashMapList.size();
        }
        return 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.iv_head)
        ImageView mIvHead;
        @Bind(R.id.tv_name)
        TextView mTvName;
        @Bind(R.id.tv_money)
        TextView mTvMoney;
        @Bind(R.id.tv_time)
        TextView mTvTime;
        @Bind(R.id.tv_up)
        TextView mTvUp;
        @Bind(R.id.tv_dowm)
        TextView mTvDowm;
        @Bind(R.id.ll_button)
        LinearLayout mLlButton;
        @Bind(R.id.tv1)
        TextView mTv1;
        @Bind(R.id.iv1)
        ImageView mIv1;
        @Bind(R.id.tv_type_desc)
        TextView mTvTypeDesc;

        MyTeamInfo.DataBean.UserListBean mRoomManageInfo;
        int position;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (oldClick == position) {
                        oldClick = -1;
                    } else {
                        oldClick = position;
                    }
                    notifyDataSetChanged();
                }
            });
        }

        public void setData(final MyTeamInfo.DataBean.UserListBean groupInfo, int position) {
            this.position = position;
            mRoomManageInfo = groupInfo;
            UtilTool.setCircleImg(mContext,groupInfo.getAvatar(),mIvHead);
            mTvName.setText(groupInfo.getUser_name());
            mTvMoney.setText(groupInfo.getPerformance());
            mTvTime.setText(groupInfo.getCreated_at());
            if(StringUtils.isEmpty(groupInfo.getType_desc())){
                mTvTypeDesc.setVisibility(View.GONE);
            }else{
                mTvTypeDesc.setVisibility(View.VISIBLE);
            }
            mTvTypeDesc.setText(groupInfo.getType_desc());

            if(groupInfo.getType()==1){
                //共识节点
                mTvTypeDesc.setBackground(mContext.getResources().getDrawable(R.drawable.bg_blue_shape4));
            }else if(groupInfo.getType()==2){
                //超级节点
                mTvTypeDesc.setBackground(mContext.getResources().getDrawable(R.drawable.bg_red_shape2));
            }else if(groupInfo.getType()==3){
                //主节点
                mTvTypeDesc.setBackground(mContext.getResources().getDrawable(R.drawable.bg_blue_shape5));
            }
            if(groupInfo.getNext_id()==0){
                mTvDowm.setSelected(false);
                mTvDowm.setEnabled(false);
            }else{
                mTvDowm.setSelected(true);
                mTvDowm.setEnabled(true);
            }

            if(groupInfo.getPrev_id()==0){
                mTvUp.setSelected(false);
                mTvUp.setEnabled(false);
            }else{
                mTvUp.setSelected(true);
                mTvUp.setEnabled(true);
            }

            mTvDowm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mOnItemClick!=null){
                        mOnItemClick.next(groupInfo.getNext_id());
                    }
                }
            });

            mTvUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mOnItemClick!=null){
                        mOnItemClick.next(groupInfo.getPrev_id());
                    }
                }
            });

            if (position == oldClick) {
                mLlButton.setVisibility(View.VISIBLE);
                mTv1.setVisibility(View.VISIBLE);
                mIv1.setImageResource(R.mipmap.iocn_more_down);
            } else {
                mTv1.setVisibility(View.GONE);
                mLlButton.setVisibility(View.GONE);
                mIv1.setImageResource(R.mipmap.iocn_more_down1);
            }
        }
    }

    public void setOnItemClick(OnItemClick onItemClick){
        this.mOnItemClick=onItemClick;
    }

    public interface OnItemClick{
        void next(int id);
        void prev(int id);
    }
}
