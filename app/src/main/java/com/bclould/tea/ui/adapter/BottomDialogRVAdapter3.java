package com.bclould.tea.ui.adapter;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bclould.tea.R;
import com.bclould.tea.model.StateInfo;
import com.bclould.tea.ui.activity.OtcActivity;
import com.bclould.tea.ui.activity.RealNameC1Activity;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by GA on 2017/12/28.
 */

public class BottomDialogRVAdapter3 extends RecyclerView.Adapter {

    private final Context mContext;
    private final List<StateInfo.DataBean> mStateList;


    public BottomDialogRVAdapter3(Context context, List<StateInfo.DataBean> stateList) {
        mContext = context;
        mStateList = stateList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_dialog_bottom, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.setData(mStateList.get(position));
    }

    @Override
    public int getItemCount() {
        if (mStateList.size() == 0)
            return 0;
        return mStateList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.tv_coin_logo)
        ImageView mTvCoinLogo;
        @Bind(R.id.tv_name)
        TextView mTvName;
        private StateInfo.DataBean mDataBean;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);

            view.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onClick(View view) {
                    if (mContext instanceof RealNameC1Activity) {
                        RealNameC1Activity activity = (RealNameC1Activity) mContext;
                        activity.hideDialog(mDataBean.getId(), mDataBean.getName_zh());
                    } else if (mContext instanceof OtcActivity) {
                        OtcActivity activity = (OtcActivity) mContext;
                        activity.hideDialog2(mDataBean.getId(), mDataBean.getName_zh());
                    }
                }
            });
        }

        public void setData(StateInfo.DataBean dataBean) {
            mDataBean = dataBean;
            mTvName.setText(dataBean.getName_zh());
        }
    }
}
