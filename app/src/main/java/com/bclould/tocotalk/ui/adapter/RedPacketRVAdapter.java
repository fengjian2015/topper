package com.bclould.tocotalk.ui.adapter;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bclould.tocotalk.R;
import com.bclould.tocotalk.history.DBManager;
import com.bclould.tocotalk.model.GrabRedInfo;
import com.bclould.tocotalk.utils.Constants;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by GA on 2018/1/17.
 */

public class RedPacketRVAdapter extends RecyclerView.Adapter {

    private final List<GrabRedInfo.DataBean.LogBean> mLogBeanList;
    private final Context mContext;
    private final DBManager mMgr;

    public RedPacketRVAdapter(Context context, List<GrabRedInfo.DataBean.LogBean> logBeanList, DBManager mgr) {
        mLogBeanList = logBeanList;
        mContext = context;
        mMgr = mgr;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_red_packet, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.setData(mLogBeanList.get(position));
    }

    @Override
    public int getItemCount() {
        if (mLogBeanList != null) {
            return mLogBeanList.size();
        }
        return 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.iv_touxiang)
        ImageView mIvTouxiang;
        @Bind(R.id.tv_name)
        TextView mTvName;
        @Bind(R.id.tv_time)
        TextView mTvTime;
        @Bind(R.id.tv_money)
        TextView mTvMoney;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void setData(GrabRedInfo.DataBean.LogBean logBean) {
            mTvName.setText(logBean.getName());
            mTvTime.setText(logBean.getTime());
            mTvMoney.setText(logBean.getMoney());
            try {
                mIvTouxiang.setImageBitmap(BitmapFactory.decodeFile(mMgr.queryUser(logBean.getName() + "@" + Constants.DOMAINNAME).get(0).getPath()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
