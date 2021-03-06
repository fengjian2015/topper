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
import com.bclould.tea.history.DBManager;
import com.bclould.tea.model.BaseInfo;
import com.bclould.tea.ui.activity.IndividualDetailsActivity;
import com.bclould.tea.utils.UtilTool;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by GA on 2017/12/12.
 */

public class AddFriendAdapter extends RecyclerView.Adapter {

    private final Context mContext;
    private final List<BaseInfo.DataBean> mDataList;
    private final DBManager mMgr;

    public AddFriendAdapter(Context context, List<BaseInfo.DataBean> dataList, DBManager mgr) {
        mContext = context;
        mDataList = dataList;
        mMgr = mgr;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_search, parent, false);
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

    class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.iv_touxiang)
        ImageView mIvTouxiang;
        @Bind(R.id.tv_name)
        TextView mTvName;
        private String mName;
        private String tocoId;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int type;
                    if (mMgr.findUser(tocoId)) {
                        if(tocoId.equals(UtilTool.getTocoId())){
                            type = 2;
                        }else {
                            type = 1;
                        }
                    } else {
                        type = 2;
                    }
                    Intent intent = new Intent(mContext, IndividualDetailsActivity.class);
                    intent.putExtra("roomId", tocoId);
                    intent.putExtra("name", mName);
                    intent.putExtra("user", tocoId);
                    mContext.startActivity(intent);
                }
            });
        }

        public void setData(BaseInfo.DataBean dataBean) {
            mName = dataBean.getName();
            tocoId=dataBean.getToco_id();
            if (dataBean.getUrl().isEmpty()) {
                Glide.with(mContext).load(UtilTool.setDefaultimage(mContext)).apply(RequestOptions.bitmapTransform(new CircleCrop())).into(mIvTouxiang);
//                mIvTouxiang.setImageBitmap(UtilTool.setDefaultimage(mContext));
            } else {
                Glide.with(mContext).load(dataBean.getUrl()).apply(RequestOptions.bitmapTransform(new CircleCrop())).into(mIvTouxiang);
            }
            mTvName.setText(dataBean.getName());
        }
    }
}
