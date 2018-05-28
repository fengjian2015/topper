package com.bclould.tocotalk.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bclould.tocotalk.R;
import com.bclould.tocotalk.history.DBManager;
import com.bclould.tocotalk.model.OrderListInfo;
import com.bclould.tocotalk.ui.activity.OrderCloseActivity;
import com.bclould.tocotalk.ui.activity.OrderDetailsActivity;
import com.bclould.tocotalk.utils.Constants;
import com.bclould.tocotalk.utils.UtilTool;
import com.bumptech.glide.Glide;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by GA on 2017/9/20.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class OrderRVAdapter extends RecyclerView.Adapter {
    private final Context mContext;
    private final DBManager mMgr;
    private final int mType;
    private List<OrderListInfo.DataBean> mDataBeanList;

    public OrderRVAdapter(Context context, List<OrderListInfo.DataBean> dataBeanList, DBManager mgr, int type) {
        mContext = context;
        mDataBeanList = dataBeanList;
        mMgr = mgr;
        mType = type;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        RecyclerView.ViewHolder viewHolder = null;
        if (mType == 2) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_order, parent, false);
            viewHolder = new ViewHolder(view);
        } else {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_order2, parent, false);
            viewHolder = new ViewHolder2(view);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (mType == 2) {
            ViewHolder viewHolder = (ViewHolder) holder;
            viewHolder.setData(mDataBeanList.get(position));
        } else {
            ViewHolder2 viewHolder2 = (ViewHolder2) holder;
            viewHolder2.setData(mDataBeanList.get(position));
        }
    }

    @Override
    public int getItemCount() {
        if (mDataBeanList.size() != 0)
            return mDataBeanList.size();
        return 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.iv_touxiang)
        ImageView mIvTouxiang;
        @Bind(R.id.tv_name)
        TextView mTvName;
        @Bind(R.id.tv_coin_type)
        TextView mTvCoinType;
        @Bind(R.id.tv_money)
        TextView mTvMoney;
        @Bind(R.id.tv_time)
        TextView mTvTime;
        @Bind(R.id.tv_type)
        TextView mTvType;
        @Bind(R.id.rl_itme)
        RelativeLayout mRlItme;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void setData(final OrderListInfo.DataBean dataBean) {
            mTvMoney.setText(mContext.getString(R.string.deal_money) + dataBean.getTrans_amount());
            mTvTime.setText(dataBean.getCreated_at());
            mTvType.setText(dataBean.getStatus_name());
            mTvCoinType.setText(dataBean.getType_name() + dataBean.getCoin_name());
            if (dataBean.getStatus() == 4) {
                mTvType.setTextColor(mContext.getResources().getColor(R.color.color_orange));
            } else {
                mTvType.setTextColor(mContext.getResources().getColor(R.color.black));
            }
                /*String jid = dataBean.getUser_name() + "@" + Constants.DOMAINNAME;
                UtilTool.getImage(mMgr, jid, mContext, mIvTouxiang);*/
            if (dataBean.getAvatar().isEmpty()) {
                UtilTool.setCircleImg(mContext, R.mipmap.img_nfriend_headshot1, mIvTouxiang);
            } else {
                UtilTool.setCircleImg(mContext, dataBean.getAvatar(), mIvTouxiang);
            }
            if (dataBean.getType() == 1) {
                mTvName.setText(dataBean.getUser_name());
                mTvCoinType.setBackground(mContext.getDrawable(R.drawable.bg_buysell_shape));
                mTvCoinType.setTextColor(mContext.getResources().getColor(R.color.blue2));
            } else {
                mTvName.setText(dataBean.getUser_name());
                mTvCoinType.setBackground(mContext.getDrawable(R.drawable.bg_buysell_shape2));
                mTvCoinType.setTextColor(mContext.getResources().getColor(R.color.green2));
            }

            mRlItme.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (dataBean.getStatus() == 0) {
                        Intent intent = new Intent(mContext, OrderCloseActivity.class);
                        intent.putExtra("status", dataBean.getStatus());
                        intent.putExtra("id", dataBean.getId() + "");
                        mContext.startActivity(intent);
                    } else if (dataBean.getStatus() == 3) {
                        Intent intent = new Intent(mContext, OrderCloseActivity.class);
                        intent.putExtra("status", dataBean.getStatus());
                        intent.putExtra("id", dataBean.getId() + "");
                        mContext.startActivity(intent);
                    } else if (dataBean.getStatus() == 4) {
                        Intent intent = new Intent(mContext, OrderCloseActivity.class);
                        intent.putExtra("status", dataBean.getStatus());
                        intent.putExtra("id", dataBean.getId() + "");
                        mContext.startActivity(intent);
                    } else {
                        Intent intent = new Intent(mContext, OrderDetailsActivity.class);
                        intent.putExtra("type", mContext.getString(R.string.order));
                        intent.putExtra("id", dataBean.getId() + "");
                        mContext.startActivity(intent);
                    }
                }
            });

        }
    }

    class ViewHolder2 extends RecyclerView.ViewHolder {
        @Bind(R.id.iv_touxiang)
        ImageView mIvTouxiang;
        @Bind(R.id.tv_name)
        TextView mTvName;
        @Bind(R.id.tv_coin_type)
        TextView mTvCoinType;
        @Bind(R.id.tv_money)
        TextView mTvMoney;
        @Bind(R.id.tv_time)
        TextView mTvTime;
        @Bind(R.id.rl_itme)
        RelativeLayout mRlItme;

        ViewHolder2(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void setData(final OrderListInfo.DataBean dataBean) {

            mTvMoney.setText(mContext.getString(R.string.deal_count) + " " + dataBean.getNumber() + " " + dataBean.getCoin_name());
            mTvTime.setText(dataBean.getCreated_at());
            mTvCoinType.setText(dataBean.getCoin_name() + dataBean.getType_name());
            if (dataBean.getType() == 1) {
                if (dataBean.getAvatar().isEmpty()) {
                    mIvTouxiang.setImageBitmap(UtilTool.setDefaultimage(mContext));
                } else {
                    Glide.with(mContext).load(dataBean.getAvatar()).into(mIvTouxiang);
                }
                mTvName.setText(dataBean.getUser_name());
                mTvCoinType.setBackground(mContext.getDrawable(R.drawable.bg_buysell_shape));
                mTvCoinType.setTextColor(mContext.getResources().getColor(R.color.blue2));
            } else {
                try {
                    String jid = dataBean.getUser_name() + "@" + Constants.DOMAINNAME;

                    UtilTool.getImage(mMgr, jid, mContext, mIvTouxiang);
                    //                    mIvTouxiang.setImageBitmap(UtilTool.getImage(mMgr, jid, mContext));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mTvName.setText(dataBean.getUser_name());
                mTvCoinType.setBackground(mContext.getDrawable(R.drawable.bg_buysell_shape2));
                mTvCoinType.setTextColor(mContext.getResources().getColor(R.color.green2));
            }

            mRlItme.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (dataBean.getStatus() == 0) {
                        Intent intent = new Intent(mContext, OrderCloseActivity.class);
                        intent.putExtra("status", dataBean.getStatus());
                        intent.putExtra("id", dataBean.getId() + "");
                        mContext.startActivity(intent);
                    } else if (dataBean.getStatus() == 3) {
                        Intent intent = new Intent(mContext, OrderCloseActivity.class);
                        intent.putExtra("status", dataBean.getStatus());
                        intent.putExtra("id", dataBean.getId() + "");
                        mContext.startActivity(intent);
                    } else if (dataBean.getStatus() == 4) {
                        Intent intent = new Intent(mContext, OrderCloseActivity.class);
                        intent.putExtra("status", dataBean.getStatus());
                        intent.putExtra("id", dataBean.getId() + "");
                        mContext.startActivity(intent);
                    } else {
                        Intent intent = new Intent(mContext, OrderDetailsActivity.class);
                        intent.putExtra("type", mContext.getString(R.string.order));
                        intent.putExtra("id", dataBean.getId() + "");
                        mContext.startActivity(intent);
                    }
                }
            });

        }
    }
}
