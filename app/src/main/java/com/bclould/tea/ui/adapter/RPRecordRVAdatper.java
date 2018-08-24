package com.bclould.tea.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bclould.tea.R;
import com.bclould.tea.model.RpRecordInfo;
import com.bclould.tea.ui.activity.GrabQRCodeRedActivity;
import com.bclould.tea.ui.activity.QRCodeRedActivity;
import com.bclould.tea.ui.activity.RedPacketActivity;
import com.bclould.tea.utils.Constants;
import com.bclould.tea.utils.UtilTool;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by GA on 2018/8/24.
 */

public class RPRecordRVAdatper extends RecyclerView.Adapter {

    private final Context mContext;
    private final List<RpRecordInfo.DataBean.LogBean> mDataList;
    private boolean mType;

    public RPRecordRVAdatper(Context context, List<RpRecordInfo.DataBean.LogBean> dataList) {
        mContext = context;
        mDataList = dataList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_rp_record, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.setData(mDataList.get(position));
    }

    @Override
    public int getItemCount() {
        if (mDataList.size() != 0) {
            return mDataList.size();
        }
        return 0;
    }

    public void setType(String type) {
        if (type.equals("get")) {
            mType = true;
        } else {
            mType = false;
        }
    }

    @SuppressLint("NewApi")
    class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.tv_name)
        TextView mTvName;
        @Bind(R.id.tv_type)
        TextView mTvType;
        @Bind(R.id.tv_time)
        TextView mTvTime;
        @Bind(R.id.tv_money)
        TextView mTvMoney;
        private RpRecordInfo.DataBean.LogBean mLogBean;

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
                            intent.putExtra("code", UtilTool.base64PetToJson(mContext, Constants.REDPACKAGE, "redID", mLogBean.getId() + "", mContext.getString(R.string.red_package)));
                            intent.putExtra("type", false);
                            mContext.startActivity(intent);
                        }
                    }
                }
            });
        }

        public void setData(RpRecordInfo.DataBean.LogBean logBean) {
            mLogBean = logBean;
            mTvTime.setText(logBean.getCreated_at());
            mTvMoney.setText(logBean.getMoney() + " " + logBean.getCoin_name());
            if (mType) {
                mTvName.setText(logBean.getName());
                mTvType.setVisibility(View.VISIBLE);
                if (logBean.getRp_type() == 1) {
                    if (logBean.getType() == 1) {
                        mTvType.setText(mContext.getString(R.string.individual));
                    } else {
                        mTvType.setText(mContext.getString(R.string.group1));
                    }
                } else {
                    mTvType.setText(mContext.getString(R.string.sao));
                }
            } else {
                mTvType.setVisibility(View.GONE);
                if (logBean.getRp_type() == 1) {
                    if (logBean.getType() == 1 || logBean.getType() == 2) {
                        mTvName.setText(mContext.getString(R.string.putong_red));
                    } else {
                        mTvName.setText(mContext.getString(R.string.lucky_red_packets));
                    }
                } else {
                    mTvName.setText(mContext.getString(R.string.qr_code_red_package));
                }
            }
        }
    }
}
