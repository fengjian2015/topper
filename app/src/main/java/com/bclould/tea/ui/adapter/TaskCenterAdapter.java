package com.bclould.tea.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bclould.tea.R;
import com.bclould.tea.model.base.BaseInfoConstants;
import com.bclould.tea.utils.UtilTool;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by fengjian on 2019/1/2.
 */

public class TaskCenterAdapter extends RecyclerView.Adapter {
    private final Context mContext;
    private final List<Map> mData;
    private OnItemListener onItemListener;

    public TaskCenterAdapter(Context context, List<Map> data) {
        mContext = context;
        mData = data;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_task_center, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.setData(mData.get(position),position);
    }

    @Override
    public int getItemCount() {
        if (mData.size() != 0) {
            return mData.size();
        }
        return 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.iv_image)
        ImageView mIvImage;
        @Bind(R.id.tv_title)
        TextView mTvTitle;
        @Bind(R.id.tv_content)
        TextView mTvContent;
        @Bind(R.id.tv_earn)
        TextView mTvEarn;
        @Bind(R.id.tv_earned)
        TextView mTvEarned;
        @Bind(R.id.iv_earned)
        ImageView mIvEarned;

        int position;
        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void setData(Map dataBean, final int position) {
            this.position=position;
            mTvTitle.setText(dataBean.get(BaseInfoConstants.TITLE)+"");
            mTvContent.setText(dataBean.get(BaseInfoConstants.DESC)+"");
            UtilTool.setCircleImg(mContext,dataBean.get(BaseInfoConstants.ICON)+"",mIvImage);
            if((boolean)dataBean.get(BaseInfoConstants.IS_COMPLETE)){
                mTvEarned.setVisibility(View.VISIBLE);
                mIvEarned.setVisibility(View.VISIBLE);
                mTvEarn.setVisibility(View.GONE);
                mTvEarned.setText(mContext.getResources().getString(R.string.earned)+dataBean.get(BaseInfoConstants.NUMBER)+dataBean.get(BaseInfoConstants.COIN_NAME));
            }else {
                mTvEarned.setVisibility(View.GONE);
                mIvEarned.setVisibility(View.GONE);
                mTvEarn.setVisibility(View.VISIBLE);
                if("login".equals(dataBean.get(BaseInfoConstants.CODE)+"")){
                    mTvEarn.setText(mContext.getResources().getString(R.string.receive_award));
                }else{
                    mTvEarn.setText(mContext.getResources().getString(R.string.earn)+dataBean.get(BaseInfoConstants.COIN_NAME));
                }
                mTvEarn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(onItemListener!=null){
                            onItemListener.onItemClick(position);
                        }
                    }
                });
            }
        }
    }

    public void addOnItemListener(OnItemListener onItemListener){
        this.onItemListener=onItemListener;
    }

    public interface OnItemListener{
        void onItemClick(int position);
    }

}
