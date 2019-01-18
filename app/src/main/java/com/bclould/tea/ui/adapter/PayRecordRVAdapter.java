package com.bclould.tea.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bclould.tea.R;
import com.bclould.tea.model.TransferListInfo;
import com.bclould.tea.ui.activity.GuessDetailsActivity;
import com.bclould.tea.ui.activity.PayDetailsActivity;
import com.bclould.tea.ui.activity.my.taskrecord.TaskRecordActivity;
import com.bclould.tea.ui.activity.wallet.exchangefrc.ExchangeFRCActivity;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by GA on 2018/3/20.
 */

public class PayRecordRVAdapter extends RecyclerView.Adapter {

    private final Context mContext;
    private final List<TransferListInfo.DataBean> mDataBeanList;

    public PayRecordRVAdapter(Context context, List<TransferListInfo.DataBean> dataBeanList) {
        mContext = context;
        mDataBeanList = dataBeanList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_pay_record, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.setData(mDataBeanList.get(position));
    }

    @Override
    public int getItemCount() {
        if (mDataBeanList.size() != 0) {
            return mDataBeanList.size();
        }
        return 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.iv_photo)
        ImageView mIvPhoto;
        @Bind(R.id.tv_name)
        TextView mTvName;
        @Bind(R.id.tv_type)
        TextView mTvType;
        @Bind(R.id.tv_money)
        TextView mTvMoney;
        @Bind(R.id.tv_time)
        TextView mTvTime;
        private TransferListInfo.DataBean mDataBean;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mDataBean.getType_number() == 16 || mDataBean.getType_number() == 17 || mDataBean.getType_number() == 18 || mDataBean.getType_number() == 19) {
                        Intent intent = new Intent(mContext, GuessDetailsActivity.class);
                        intent.putExtra("bet_id", mDataBean.getData_arr().getBet_id());
                        intent.putExtra("period_qty", mDataBean.getData_arr().getPeriod_qty());
                        mContext.startActivity(intent);
                    } else if(mDataBean.getType_number() == 31){
                        mContext.startActivity(new Intent(mContext, TaskRecordActivity.class));
                    }else if(mDataBean.getType_number() == 33){
                        mContext.startActivity(new Intent(mContext, ExchangeFRCActivity.class));
                    }else {
                        Intent intent = new Intent(mContext, PayDetailsActivity.class);
                        intent.putExtra("log_id", mDataBean.getLog_id() + "");
                        intent.putExtra("id", mDataBean.getId() + "");
                        intent.putExtra("type_number", mDataBean.getType_number() + "");
                        mContext.startActivity(intent);
                    }
                }
            });
        }

        public void setData(TransferListInfo.DataBean dataBean) {
            if (dataBean.getType_number() == 1 || dataBean.getType_number() == 2) {
                mIvPhoto.setImageResource(R.mipmap.icon_record_transfer);
            } else if (dataBean.getType_number() == 3 || dataBean.getType_number() == 4) {
                mIvPhoto.setImageResource(R.mipmap.icon_record_redp);
            } else if (dataBean.getType_number() == 5 || dataBean.getType_number() == 6) {
                mIvPhoto.setImageResource(R.mipmap.icon_record_money);
            } else if (dataBean.getType_number() == 7) {
                mIvPhoto.setImageResource(R.mipmap.icon_record_recharge);
            } else if (dataBean.getType_number() == 8) {
                mIvPhoto.setImageResource(R.mipmap.icon_record_send);
            } else if (dataBean.getType_number() == 12 || dataBean.getType_number() == 13) {
                mIvPhoto.setImageResource(R.mipmap.icon_record_give);
            } else if (dataBean.getType_number() == 14 || dataBean.getType_number() == 15) {
                mIvPhoto.setImageResource(R.mipmap.icon_record_ex);
            } else if (dataBean.getType_number() == 16 || dataBean.getType_number() == 17 || dataBean.getType_number() == 18 || dataBean.getType_number() == 19) {
                mIvPhoto.setImageResource(R.mipmap.icon_wealth_block);
            }else{
                mIvPhoto.setImageResource(R.mipmap.icon_wealth_block);
            }
            mDataBean = dataBean;
            mTvName.setText(dataBean.getType());
            mTvType.setText(dataBean.getType_desc());
            mTvTime.setText(dataBean.getCreated_at());
            if (dataBean.getNumber().contains("-")) {
                mTvMoney.setText(dataBean.getNumber() + " " + dataBean.getCoin_name());
                mTvMoney.setTextColor(mContext.getResources().getColor(R.color.red));
            } else {
                if(dataBean.getNumber().contains("+")){
                    mTvMoney.setText(dataBean.getNumber() + " " + dataBean.getCoin_name());
                }else {
                    mTvMoney.setText("+" + dataBean.getNumber() + " " + dataBean.getCoin_name());
                }
                mTvMoney.setTextColor(mContext.getResources().getColor(R.color.blue2));
            }
        }
    }
}
