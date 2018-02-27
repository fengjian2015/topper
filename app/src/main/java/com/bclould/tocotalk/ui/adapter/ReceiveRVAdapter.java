package com.bclould.tocotalk.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bclould.tocotalk.R;
import com.bclould.tocotalk.model.RedRecordInfo;
import com.bclould.tocotalk.ui.activity.GrabQRCodeRedActivity;
import com.bclould.tocotalk.ui.activity.QRCodeRedActivity;
import com.bclould.tocotalk.ui.activity.RedPacketActivity;
import com.bclould.tocotalk.utils.Constants;
import com.bclould.tocotalk.utils.UtilTool;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by GA on 2018/1/3.
 */

public class ReceiveRVAdapter extends RecyclerView.Adapter {

    private final Context mContext;
    private final List<RedRecordInfo.DataBean.LogBean> mLogBeanList;
    public final boolean mType;

    public ReceiveRVAdapter(Context context, List<RedRecordInfo.DataBean.LogBean> logBeanList, boolean type) {
        mContext = context;
        mLogBeanList = logBeanList;
        mType = type;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.item_receive, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.setData(mLogBeanList.get(position));
    }

    @Override
    public int getItemCount() {
        if (mLogBeanList != null)
            return mLogBeanList.size();
        return 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.tv_name)
        TextView mTvName;
        @Bind(R.id.tv_type)
        TextView mTvType;
        @Bind(R.id.tv_time)
        TextView mTvTime;
        @Bind(R.id.tv_money)
        TextView mTvMoney;
        private RedRecordInfo.DataBean.LogBean mLogBean;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mType) {
                        if (mLogBean.getRp_type() == 1) {
                            Intent intent = new Intent(mContext, RedPacketActivity.class);
                            intent.putExtra("id", mLogBean.getId() + "");
                            intent.putExtra("from", false);
                            intent.putExtra("type", false);
                            mContext.startActivity(intent);
                        } else {
                            Intent intent = new Intent(mContext, GrabQRCodeRedActivity.class);
                            intent.putExtra("id", mLogBean.getId() + "");
                            intent.putExtra("type", false);
                            mContext.startActivity(intent);
                        }
                    } else {
                        if (mLogBean.getRp_type() == 1) {
                            Intent intent = new Intent(mContext, RedPacketActivity.class);
                            intent.putExtra("id", mLogBean.getId() + "");
                            intent.putExtra("from", false);
                            intent.putExtra("type", false);
                            mContext.startActivity(intent);
                        } else {
                            Intent intent = new Intent(mContext, QRCodeRedActivity.class);
                            intent.putExtra("code", UtilTool.base64PetToJson(Constants.REDPACKAGE, "redID", mLogBean.getId() + "", "红包"));
                            intent.putExtra("type", false);
                            mContext.startActivity(intent);
                        }
                    }
                }
            });
        }

        public void setData(RedRecordInfo.DataBean.LogBean logBean) {
            mLogBean = logBean;
            mTvTime.setText(logBean.getCreated_at());
            mTvMoney.setText(logBean.getTotal_money() + " " + logBean.getCoin_name());
            if (mType) {
                mTvName.setText(logBean.getName());
                mTvType.setVisibility(View.VISIBLE);
                if (logBean.getRp_type() == 1) {
                    mTvType.setText("个");
                } else {
                    mTvType.setText("扫");
                }
            } else {
                mTvType.setVisibility(View.GONE);
                if (logBean.getRp_type() == 1) {
                    mTvName.setText("普通红包");
                } else {
                    mTvName.setText("二维码红包");
                }
            }
        }
    }
}
