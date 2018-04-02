package com.bclould.tocotalk.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bclould.tocotalk.R;
import com.bclould.tocotalk.history.DBManager;
import com.bclould.tocotalk.model.OrderListInfo;
import com.bclould.tocotalk.ui.activity.OrderDetailsActivity;
import com.bclould.tocotalk.utils.Constants;

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
    private List<OrderListInfo.DataBean> mDataBeanList;

    public OrderRVAdapter(Context context, List<OrderListInfo.DataBean> dataBeanList, DBManager mgr) {
        mContext = context;
        mDataBeanList = dataBeanList;
        mMgr = mgr;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_order, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.setData(mDataBeanList.get(position));
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
        @Bind(R.id.tv_order_number)
        TextView mTvOrderNumber;
        @Bind(R.id.tv_type)
        TextView mTvType;
        @Bind(R.id.rl_itme)
        RelativeLayout mRlItme;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void setData(final OrderListInfo.DataBean dataBean) {

            mTvMoney.setText("交易金额" + dataBean.getTrans_amount());
            mTvOrderNumber.setText("订单号:" + dataBean.getOrder_no());
            mTvType.setText(dataBean.getStatus_name());
            mTvCoinType.setText(dataBean.getCoin_name() + dataBean.getType_name());
            if (dataBean.getType() == 1) {
                try {
                    String jid = dataBean.getTo_user_name() + "@" + Constants.DOMAINNAME;
                    Bitmap bitmap = BitmapFactory.decodeFile(mMgr.queryUser(jid).get(0).getPath());
                    mIvTouxiang.setImageBitmap(bitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mTvName.setText(dataBean.getTo_user_name());
                mTvCoinType.setBackground(mContext.getDrawable(R.drawable.bg_buysell_shape));
                mTvCoinType.setTextColor(mContext.getColor(R.color.blue2));
            } else {
                try {
                    String jid = dataBean.getUser_name() + "@" + Constants.DOMAINNAME;
                    Bitmap bitmap = BitmapFactory.decodeFile(mMgr.queryUser(jid).get(0).getPath());
                    mIvTouxiang.setImageBitmap(bitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mTvName.setText(dataBean.getTo_user_name());
                mTvCoinType.setBackground(mContext.getDrawable(R.drawable.bg_buysell_shape2));
                mTvCoinType.setTextColor(mContext.getColor(R.color.green2));
            }

            mRlItme.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (dataBean.getStatus() == 0 || dataBean.getStatus() == 3) {
                        Toast.makeText(mContext, "交易已关闭", Toast.LENGTH_SHORT).show();
                    } else {
                        Intent intent = new Intent(mContext, OrderDetailsActivity.class);
                        intent.putExtra("type", "订单");
                        intent.putExtra("id", dataBean.getId() + "");
                        mContext.startActivity(intent);
                    }
                }
            });

        }
    }
}
