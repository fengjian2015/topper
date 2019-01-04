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
import com.bclould.tea.model.PublicInfo;
import com.bclould.tea.ui.activity.PublicDetailsActivity;
import com.bclould.tea.utils.UtilTool;
import java.util.List;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by GA on 2017/12/12.
 */

public class SearchPublicAdapter extends RecyclerView.Adapter {

    private final Context mContext;
    private final List<PublicInfo.DataBean> mDataList;


    public SearchPublicAdapter(Context context, List<PublicInfo.DataBean> list) {
        mContext = context;
        mDataList = list;
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
        private String id;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(mContext, PublicDetailsActivity.class);
                    intent.putExtra("id",id);
                    mContext.startActivity(intent);
                }
            });
        }

        public void setData(PublicInfo.DataBean dataBean) {
            id=dataBean.getId()+"";
            UtilTool.setCircleImg(mContext,dataBean.getLogo(),mIvTouxiang);
            mTvName.setText(dataBean.getName());
        }

    }
}
